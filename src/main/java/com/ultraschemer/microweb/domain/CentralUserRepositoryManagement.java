package com.ultraschemer.microweb.domain;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ultraschemer.microweb.controller.bean.CreateUserData;
import com.ultraschemer.microweb.domain.bean.CentralUserDataObject;
import com.ultraschemer.microweb.domain.bean.Oauth2AuthorizationConsentedObject;
import com.ultraschemer.microweb.domain.bean.UserData;
import com.ultraschemer.microweb.domain.error.*;
import com.ultraschemer.microweb.entity.*;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.utils.Resource;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import okhttp3.*;
import org.hibernate.Session;

import javax.persistence.PersistenceException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CentralUserRepositoryManagement {
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .build();
    private static JsonObject wellKnown = null;
    private static JsonObject masterWellKnown = null;
    private static final Pattern bearerP = Pattern.compile("^Bearer\\s+(.+)$");

    public static JsonObject wellKnown() throws StandardException {
        if(wellKnown == null) {
            String wellKnowAddress = Configuration.read("backend oauth wellknown");
            if(wellKnowAddress == null || wellKnowAddress.equals("")) {
                throw new Oauth2WellKnowConfigurationException("Well Known address configurations not assigned.");
            }
            Request request = new Request.Builder()
                    .url(wellKnowAddress)
                    .build();
            try(Response response = client.newCall(request).execute()) {
                if(response.code() != 200) {
                    throw new Oauth2WellKnownLoadException("No suitable response from the .wellknown access point.");
                }

                wellKnown = new JsonObject(Objects.requireNonNull(response.body()).string());
            } catch(Oauth2WellKnownLoadException ole) {
                throw ole;
            }  catch(Exception e) {
                throw new Oauth2WellKnownLoadException("Unable to load Oauth2 authentication and authorization urls: " +
                        e.getLocalizedMessage(), e);
            }
        }

        return wellKnown;
    }

    private static JsonObject masterWellKnown() throws StandardException {
        if(masterWellKnown == null) {
            String wellKnowAddress = Configuration.read("keycloak master oauth wellknown");
            if(wellKnowAddress == null || wellKnowAddress.equals("")) {
                throw new Oauth2MasterWellKnowConfigurationException("Well Known address configurations not assigned.");
            }
            Request request = new Request.Builder()
                    .url(wellKnowAddress)
                    .build();
            try(Response response = client.newCall(request).execute()) {
                if(response.code() != 200) {
                    throw new Oauth2MasterWellKnownLoadException("No suitable response from the .wellknown access point.");
                }

                masterWellKnown = new JsonObject(Objects.requireNonNull(response.body()).string());
            } catch(Oauth2MasterWellKnownLoadException ole) {
                throw ole;
            } catch(Exception e) {
                throw new Oauth2MasterWellKnownLoadException("Unable to load Oauth2 authentication and authorization urls: " +
                        e.getLocalizedMessage(), e);
            }
        }

        return masterWellKnown;
    }
    /**
     * Finish the authentication, after the first Oauth2 step (the user authentication). The step presented by this
     * business function is the Application Authorization.
     * @param foreignConsent The object receiving the consent information
     *                       from external User Permission Manager (KeyCloak):
     * @return The Json object with the user authorization data, including access token and expiration information.
     * @throws StandardException In the case of any error in this authorization step
     */
    public static JsonObject finishAuthorizationConsent(Oauth2AuthorizationConsentedObject foreignConsent)
            throws StandardException {
        JsonObject wellKnown = wellKnown();

        // The authorization has already been loaded:
        String tokenEndpoint = wellKnown.getString("token_endpoint");

        // Get company data:
        FormBody.Builder builder = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("response_type", "code")
                .add("client_id", foreignConsent.getClientId())
                .add("client_secret", foreignConsent.getClientSecret())
                .add("redirect_uri", foreignConsent.getRedirectUri())
                .add("session_state", foreignConsent.getSessionState())
                .add("code", foreignConsent.getCode());

        if(foreignConsent.getState() != null) {
            builder.add("state", foreignConsent.getState());
        }

        FormBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(tokenEndpoint)
                .post(formBody)
                .build();

        try(Response response = client.newCall(request).execute()) {
            if (response.code() != 200) {
                throw new FinishAuthorizationConsentException("Unable to finish authorization consent and get access token." +
                        response.code() + "\n\nMessage:\n\n" + Objects.requireNonNull(response.body()).string());
            }
            return new JsonObject(Objects.requireNonNull(response.body()).string());
        } catch(Exception e) {
            throw new FinishAuthorizationConsentException("Unable to finish authorization consent and get access token: " +
                    e.getLocalizedMessage(), e);
        }
    }

    public static User evaluateResourcePermission(String method, String path, String authorization)
            throws StandardException {
        String clientApplication = Configuration.read("keycloak client application");
        String[] roles = Configuration.read("keycloak client application available permissions").split(",");

        if(authorization == null) {
            throw new UnauthorizedException("No authorization header.");
        }

        // Extract the bearer
        Matcher m = bearerP.matcher(authorization);

        String bearer;
        if(m.find()) {
            bearer = m.group(1);
        } else {
            throw new UnauthorizedException("Authorization header in wrong format. You must provide a bearer.");
        }

        if(bearer == null) {
            throw new UnauthorizedException("Authorization bearer token in wrong format.");
        }

        // Get the OpenId well known end-points:
        JsonObject wellKnown = CentralUserRepositoryManagement.wellKnown();

        // Require all possible user permissions from the authorization server:
        FormBody.Builder builder = new FormBody.Builder()
                .add("grant_type", "urn:ietf:params:oauth:grant-type:uma-ticket")
                .add("audience", clientApplication);
        Arrays.stream(roles).map(s -> builder.add("permission", s));
        FormBody allPermissionsFormBody = builder.build();
        Request resourceRequest = new Request.Builder()
                .url(wellKnown.getString("token_endpoint"))
                .header("Authorization", authorization)
                .post(allPermissionsFormBody)
                .build();

        // Process the permissions:
        JsonObject permissionData;
        try(Response response = client.newCall(resourceRequest).execute()) {
            if(response.code() != 200) {
                throw new UnauthorizedException("Unable to load permissions for given resource. Return status: " +
                        response.code() + "\n\nMessage:\n\n" + Objects.requireNonNull(response.body()).string());
            }
            permissionData = new JsonObject(Objects.requireNonNull(response.body()).string());
        } catch(UnauthorizedException ue) {
            throw ue;
        } catch(Exception e) {
            throw new UnauthorizedException("Unable to identify permission for given resource: " +
                    e.getLocalizedMessage(), e);
        }

        // Read the token to get application name
        boolean authorized = false;
        DecodedJWT jwt = null;

        try {
            jwt = JWT.decode(permissionData.getString("access_token"));

            @SuppressWarnings("unchecked")
            ArrayList<HashMap<String, Object>> permissions = (ArrayList<HashMap<String, Object>>)
                    jwt.getClaim("authorization").asMap().get("permissions");

            for(HashMap<String, Object> p: permissions) {
                if(Resource.resourceIsEquivalentToPath((String)p.get("rsname"), path, method)) {
                    authorized = true;
                    break;
                }
            }
        } catch (Exception e) {
            throw new ForbiddenException("Unable to locate the authorization to access this resource: " +
                    e.getLocalizedMessage(), e);
        }

        // Update user data BEFORE finalize authorization evaluation:
        CentralUserDataObject u = new CentralUserDataObject();
        u.setCentralControlId(UUID.fromString(jwt.getClaim("sub").asString()));
        u.setGivenName(jwt.getClaim("given_name").asString());
        u.setFamilyName(jwt.getClaim("family_name").asString());
        u.setAlias(u.getGivenName() + " " + u.getFamilyName());
        u.setName(jwt.getClaim("preferred_username").asString());
        u.seteMail(jwt.getClaim("email").asString());
        u.seteMailStatus(jwt.getClaim("email_verified").asBoolean() ? "verified" : "unverified");

        @SuppressWarnings("unchecked")
        List<String> authorizedRoles = (List<String>)
                ((Map<String, Object>) jwt.getClaim("resource_access").asMap().get(clientApplication)).get("roles");
        User returnUser = evaluateUserAndRoles(u, authorizedRoles);

        if(!authorized) {
            throw new ForbiddenException("Resource not present in the list of user permissions.");
        }

        return returnUser;
    }

    public static User evaluateUserAndRoles(CentralUserDataObject user, List<String> userRoles) throws StandardException {
        try (Session session = EntityUtil.openTransactionSession()){
            List<User> possibleUsers = session.createQuery("from User where centralControlId = :id", User.class)
                    .setParameter("id", user.getCentralControlId())
                    .list();

            User updatedUser;
            if(possibleUsers.size() > 0) {
                updatedUser = possibleUsers.get(0);
            } else {
                updatedUser = new User();
            }

            // Save the default alias:
            if(updatedUser.getAlias() == null ||
                    updatedUser.getAlias().equals(updatedUser.getGivenName() + " " + updatedUser.getFamilyName()))
            {
                updatedUser.setAlias(user.getGivenName() + " " + user.getFamilyName());
            }

            // Set the other user data:
            updatedUser.setGivenName(user.getGivenName());
            updatedUser.setFamilyName(user.getFamilyName());
            updatedUser.setName(user.getName());
            updatedUser.setPassword("-"); // Always invalidate password, to disable default user authentication completely
            updatedUser.setStatus("active");
            updatedUser.setCentralControlId(user.getCentralControlId());

            // Person Id is an internal filter of Microweb
            // Persist user:
            session.persist(updatedUser);

            // Now verify the roles:
            List<Role> currentRoles = session.createQuery("from Role", Role.class).list();

            for(String userRole: userRoles) {
                Role updatedRole;
                if(!currentRoles.stream().map(Role::getName).collect(Collectors.toList()).contains(userRole)) {
                    // Register new role, if they aren't already registered in database:
                    updatedRole = new Role();
                    updatedRole.setName(userRole);
                    updatedRole.setStatus("valid");
                    session.persist(updatedRole);
                } else {
                    updatedRole = currentRoles.stream()
                            .filter(role -> role.getName().equals(userRole)).collect(Collectors.toList()).get(0);
                }

                // Now, link the role to the user
                List<User_Role> possibleUserRoles =
                        session.createQuery("from User_Role where userId = :uid and roleId = :rid", User_Role.class)
                                .setParameter("uid", updatedUser.getId())
                                .setParameter("rid", updatedRole.getId())
                                .list();

                if(possibleUserRoles.size() == 0) {
                    // Create the link object - otherwise, it already exists:
                    User_Role user_role = new User_Role();
                    user_role.setUserId(updatedUser.getId());
                    user_role.setRoleId(updatedRole.getId());
                    user_role.setStatus("active");
                    session.persist(user_role);
                }
            }

            // Remove the link between the roles which the user doesn't have access, which are the currentRoles not in
            // the list of userRoles:
            List<UUID> roleUuidListToUnlink = currentRoles.stream().filter(r -> !userRoles.contains(r.getName()))
                    .map(Role::getId).collect(Collectors.toList());

            if(roleUuidListToUnlink.size() > 0) {
                // Load all User_Roles to be unlinked:
                List<User_Role> userRolesToBeUnlinked =
                        session.createQuery("from User_Role where roleId in :rids and userId = :uid", User_Role.class)
                                .setParameterList("rids", roleUuidListToUnlink)
                                .setParameter("uid", updatedUser.getId())
                                .list();

                for (User_Role userRoleToBeUnlinked : userRolesToBeUnlinked) {
                    userRoleToBeUnlinked.setStatus("inactive");
                    session.persist(userRoleToBeUnlinked);
                }
            }

            if(user.geteMail() != null) {
                // Verify the user e-mail:
                List<EmailAddress> possibleEmailAddresses =
                        session.createQuery("from EmailAddress where address = :email", EmailAddress.class)
                                .setParameter("email", user.geteMail())
                                .list();
                EmailAddress emailAddress;
                if (possibleEmailAddresses.size() == 0) {
                    emailAddress = new EmailAddress();
                    emailAddress.setStatus(user.geteMailStatus());
                    emailAddress.setAddress(user.geteMail());
                    session.persist(emailAddress);
                } else {
                    emailAddress = possibleEmailAddresses.get(0);
                    emailAddress.setStatus(user.geteMailStatus());

                    session.persist(emailAddress);
                }

                // Link e-mail to the user:
                List<User_EmailAddress> emailAddressLink =
                        session.createQuery("from User_EmailAddress where userId = :uid and emailAddressId = :eaid", User_EmailAddress.class)
                                .setParameter("eaid", emailAddress.getId())
                                .setParameter("uid", updatedUser.getId())
                                .list();

                if (emailAddressLink.size() == 0) {
                    // Create the link - otherwise, do nothing:
                    User_EmailAddress userEmailAddressLink = new User_EmailAddress();
                    userEmailAddressLink.setUserId(updatedUser.getId());
                    userEmailAddressLink.setEmailAddressId(emailAddress.getId());
                    userEmailAddressLink.setPreferenceOrder(0);
                    userEmailAddressLink.setStatus(user.geteMailStatus());
                    session.persist(userEmailAddressLink);
                }
            }

            session.getTransaction().commit();
            return updatedUser;
        } catch(Exception e) {
            throw new CentralUserDataUpdateException("Unable to update user data from Central User Management System: "
                    + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Register a user in KeyCloak and then save his/her data in database, for future queries.
     * @param creator The user requesting another user creation.
     * @param u The data of the user to be created.
     * @param roles The user roles
     * @throws StandardException Thrown error in the case of any failure or impossibility to create user.
     */
    public static UserData registerUser(User creator, CreateUserData u, List<String> roles) throws StandardException {
        String clientApplication = Configuration.read("keycloak client application");

        // Only the root user can create users with roles "root" and "user-management".
        // Only users with role "user-management" can access this business rule.
        try(Session session = EntityUtil.openTransactionSession()) {
            List<UUID> creatorRoleIds =
                    session.createQuery("select roleId from User_Role where userId = :uid", UUID.class)
                            .setParameter("uid", creator.getId())
                            .list();

            if(creatorRoleIds.size() == 0) {
                throw new UserRegistrationNoSuitableCreatorRolesException(
                        "The creator user has no roles associated to him. Aborting user registration");
            }

            List<String> creatorRoles =  session.createQuery("select name from Role where id in :rids", String.class)
                    .setParameterList("rids", creatorRoleIds)
                    .list();

            if((!creatorRoles.contains("root")) && (roles.contains("user_manager ") || roles.contains("root"))) {
                throw new UserRegistrationForbiddenException(
                        "The user is forbidden to create users with the given roles.");
            }

            if(!(creatorRoles.contains("root") || creatorRoles.contains("user_manager"))) {
                throw new UserRegistrationForbiddenException(
                        "The user is forbidden to create other users.");
            }
        } catch(PersistenceException pe) {
            throw new UserRegistrationDataError("Unable to register new user due system data querying errors: " +
                    pe.getLocalizedMessage(), pe);
        }

        JsonObject credentials = keyCloakLogin();
        String microwebAdminResource = Configuration.read("keycloak admin resource");
        JsonObject userRepresentation = new JsonObject();

        userRepresentation.put("username", u.getName());
        userRepresentation.put("firstName", u.getGivenName());
        userRepresentation.put("lastName", u.getFamilyName());
        userRepresentation.put("enabled", true);
        userRepresentation.put("emailVerified", true);
        userRepresentation.put("requiredActions", new JsonArray());

        // Create the user:
        Request creationUserRequest = new Request.Builder()
                .url(microwebAdminResource + "/users")
                .header("Authorization", "Bearer " + credentials.getString("access_token"))
                .post(FormBody.create(userRepresentation.toString(), MediaType.parse("application/json; charset=utf-8")))
                .build();

        try(Response response = client.newCall(creationUserRequest).execute()) {
            if(response.code() >= 300) {
                throw new UnableToCreateUserException("Unable to create user. Return status: " + response.code() +
                        "\n\nMessage:\n\n" + Objects.requireNonNull(response.body()).string());
            }
        } catch(UnableToCreateUserException ue) {
            throw ue;
        } catch(Exception e) {
            throw new UnableToCreateUserException("Unable to create user: " + e.getLocalizedMessage(), e);
        }

        // Load created user:
        Request loadUserRequest;
        try {
            loadUserRequest = new Request.Builder()
                    .url(microwebAdminResource + "/users?username=" + URLEncoder.encode(u.getName(),
                            StandardCharsets.UTF_8.toString()))
                    .header("Authorization", "Bearer " + credentials.getString("access_token"))
                    .get()
                    .build();
        } catch(Exception e) {
            throw new CreatedUserButRolesNotAssignedException("Unable to find the identification of the currently created " +
                    "user, to assign him/her roles: " + e.getLocalizedMessage(), e);
        }

        JsonObject createdUser = null;
        try(Response response = client.newCall(loadUserRequest).execute()) {
            if(response.code() >= 300) {
                throw new CreatedUserButRolesNotAssignedException("Unable to assign roles to user. Return status: " +
                        response.code() + "\n\nMessage:\n\n" + Objects.requireNonNull(response.body()).string());
            }

            JsonArray foundUsers = new JsonArray(Objects.requireNonNull(response.body()).string());
            for(int i = 0; i < foundUsers.size(); i++) {
                JsonObject fu = foundUsers.getJsonObject(i);
                if(fu.getString("username").equals(u.getName())) {
                    createdUser = fu;
                    break;
                }
            }
        } catch(CreatedUserButRolesNotAssignedException cae) {
            throw cae;
        } catch(Exception e) {
            throw new CreatedUserButRolesNotAssignedException("Unable to assign roles to user because he/she " +
                    "has not been found: " + e.getLocalizedMessage(), e);
        }

        if(createdUser == null) {
            throw new CreatedUserButRolesNotAssignedException("Unable to assign roles to user because he/she has not " +
                    "be found, after created.");
        }

        // Add roles here
        // Routes:
        // POST /{realm}/groups/{id}/role-mappings/clients/{client} - user id? On a Group path?
        // GET /{realm}/groups/{id}/role-mappings/clients/{client} - user id? On a Group path?
        // From: https://www.keycloak.org/docs-api/5.0/rest-api/index.html#_client_role_mappings_resource
        // The routes above disagree with this information:
        // https://stackoverflow.com/questions/56254627/keycloak-using-admin-api-to-add-client-role-to-user

        Request loadClientsRequest = new Request.Builder()
                .url(microwebAdminResource + "/clients?clientId=" + clientApplication)
                .addHeader("Authorization", "Bearer " + credentials.getString("access_token"))
                .get()
                .build();
        JsonObject microwebBackendObject;
        try(Response response = client.newCall(loadClientsRequest).execute()) {
            if(response.code() >= 300) {
                throw new CreatedUserButRolesNotAssignedException("Unable to assign roles to user because the default " +
                        "application (" + clientApplication + ") has not been found. Status code: " + response.code() +
                        "\n\nMessage: " + Objects.requireNonNull(response.body()).string());
            }
            microwebBackendObject = new JsonArray(Objects.requireNonNull(response.body()).string()).getJsonObject(0);
        } catch(CreatedUserButRolesNotAssignedException ce) {
            throw ce;
        } catch(Exception e) {
            throw new CreatedUserButRolesNotAssignedException("Unable to assign roles to user because the default " +
                    "application (" + clientApplication + ") has not been found.", e);
        }

        Request loadAvailableRolesRequest = new Request.Builder()
                .url(microwebAdminResource + "/users/" + createdUser.getString("id") + "/role-mappings/clients/" +
                        microwebBackendObject.getString("id") + "/available")
                .addHeader("Authorization", "Bearer " + credentials.getString("access_token"))
                .get()
                .build();

        JsonArray availableRoles;
        try(Response response = client.newCall(loadAvailableRolesRequest).execute()) {
            if(response.code() >= 300) {
                throw new CreatedUserButRolesNotAssignedException("Unable to assign roles to user because the roles " +
                        "couldn't be loaded. Status code: " + response.code() + "\n\nMessage: " +
                        Objects.requireNonNull(response.body()).string());
            }

            availableRoles = new JsonArray(Objects.requireNonNull(response.body()).string());
        } catch(CreatedUserButRolesNotAssignedException ce) {
            throw ce;
        } catch(Exception e) {
            throw new CreatedUserButRolesNotAssignedException("Unable to assign roles to user because the " +
                    "roles couldn't be loaded: " + e.getLocalizedMessage(), e);
        }

        JsonArray rolesToAssign = new JsonArray();
        for(int i = 0; i < availableRoles.size(); i++) {
            JsonObject availableRole = availableRoles.getJsonObject(i);
            if(roles.contains(availableRole.getString("name"))) {
                rolesToAssign.add(availableRole);
            }
        }

        // Assign roles to user
        Request assignRolesRequest = new Request.Builder()
                .url(microwebAdminResource + "/users/" + createdUser.getString("id") + "/role-mappings/clients/" +
                        microwebBackendObject.getString("id"))
                .addHeader("Authorization", "Bearer " + credentials.getString("access_token"))
                .post(FormBody.create(rolesToAssign.toString(), MediaType.parse("application/json; charset=utf-8")))
                .build();

        try(Response response = client.newCall(assignRolesRequest).execute()) {
            if(response.code() >= 300) {
                throw new CreatedUserButRolesNotAssignedException("Unable to assign roles to user because the roles." +
                        " Status code: " + response.code() + "\n\nMessage: " +
                        Objects.requireNonNull(response.body()).string());
            }
        } catch(CreatedUserButRolesNotAssignedException ce) {
            throw ce;
        } catch(Exception e) {
            throw new CreatedUserButRolesNotAssignedException("Unable to assign roles to user: " +
                    e.getLocalizedMessage(), e);
        }

        // Set user password:
        JsonObject resetPasswordData = new JsonObject();
        resetPasswordData.put("type", "password");
        resetPasswordData.put("temporary", false);
        resetPasswordData.put("value", u.getPassword());

        Request resetPasswordRequest = new Request.Builder()
                .url(microwebAdminResource + "/users/" + createdUser.getString("id") + "/reset-password")
                .addHeader("Authorization", "Bearer " + credentials.getString("access_token"))
                .put(FormBody.create(resetPasswordData.toString(), MediaType.parse("application/json; charset=utf-8")))
                .build();

        try(Response response = client.newCall(resetPasswordRequest).execute()) {
            if(response.code() >= 300) {
                throw new UnableToAssignPasswordOnUserCreationException("Unable to assign password. Status code: " +
                        response.code() + "\n\nMessage: " + Objects.requireNonNull(response.body()).string());
            }
        } catch(Exception e) {
            throw new UnableToAssignPasswordOnUserCreationException("Unable to assign password " +
                    "to the new user: " + e.getLocalizedMessage(), e);
        }

        // Execute logoff:
        keyCloakLogoff(credentials);

        CentralUserDataObject userDataObject = new CentralUserDataObject();
        userDataObject.setCentralControlId(UUID.fromString(createdUser.getString("id")));
        userDataObject.setGivenName(u.getGivenName());
        userDataObject.setFamilyName(u.getFamilyName());
        userDataObject.setAlias(u.getAlias());
        userDataObject.setName(u.getName());
        User newU = evaluateUserAndRoles(userDataObject, roles);
        return UserManagement.loadUserBySecureId(newU.getId().toString());
    }

    private static JsonObject keyCloakLogin() throws StandardException {
        String keyCloakAdminName = Configuration.read("keycloak master admin name");
        String keyCloakAdminPassword = Configuration.read("keycloak master admin password");

        if(keyCloakAdminName.equals("") || keyCloakAdminPassword.equals("")) {
            throw new NoKeyCloakLoginConfigurationError(
                    "Please, verify your key cloak configurations to perform operation.");
        }

        FormBody adminAccessFormBody = new FormBody.Builder()
                .add("client_id","admin-cli")
                .add("username",keyCloakAdminName)
                .add("password",keyCloakAdminPassword)
                .add("grant_type","password")
                .build();

        Request request = new Request.Builder()
                .url(masterWellKnown().getString("token_endpoint"))
                .post(adminAccessFormBody)
                .build();

        try(Response response = client.newCall(request).execute()) {
            if(response.code() != 200) {
                throw new KeyCloakLoginException("Unable to perform login on administration console. Error code: " +
                        response.code() + "\n\nMessage: " + Objects.requireNonNull(response.body()).string());
            }
            return new JsonObject(Objects.requireNonNull(response.body()).string());
        } catch(KeyCloakLoginException ke) {
            throw ke;
        } catch(Exception e) {
            throw new KeyCloakLoginException("Unable to perform login on administration console: " +
                    e.getLocalizedMessage(), e);
        }
    }

    private static void keyCloakLogoff(JsonObject credentials) throws StandardException {
        FormBody adminLogoffFormBody = new FormBody.Builder()
                .add("client_id", "admin-cli")
                .add("refresh_token", credentials.getString("refresh_token"))
                .build();

        Request request = new Request.Builder()
                .url(masterWellKnown().getString("end_session_endpoint"))
                .header("Authorization", "Bearer " + credentials.getString("access_token"))
                .post(adminLogoffFormBody)
                .build();

        try(Response response = client.newCall(request).execute()) {
            if(response.code() >= 300) {
                throw new KeyCloakLogoffException("Unable to perform logoff from administration console. Error code: " +
                        response.code() + "\n\nMessage: " + Objects.requireNonNull(response.body()).string());
            }
        } catch(KeyCloakLogoffException ke) {
            throw ke;
        } catch(Exception e) {
            throw new KeyCloakLogoffException("Unable to perform logoff from administration console: " +
                    e.getLocalizedMessage(), e);
        }
    }

    public static void updateUserPassword(User u, String clientId, String clientSecret, String oldPassword, String newPassword)
            throws StandardException {
        //
        // The password is changed on KeyCloak and ignored completely in local database.
        //

        // Try to log in KeyCloak using the user data and the given password:
        FormBody accessFormBody = new FormBody.Builder()
                .add("grant_type", "password")
                .add("username", u.getName())
                .add("password", oldPassword)
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build();

        Request accessRequest = new Request.Builder()
                .url(wellKnown().getString("token_endpoint"))
                .post(accessFormBody)
                .build();

        JsonObject userCredentials;
        try(Response response = client.newCall(accessRequest).execute()) {
            if(response.code() >= 300) {
                throw new OldPasswordInvalidOnPasswordChangeException("Old password invalid. Unable to perform user " +
                        "login validation. No password change has been performed. Status code: " + response.code() +
                        "\n\nMessage: " + Objects.requireNonNull(response.body()).string());
            }
            userCredentials = new JsonObject(Objects.requireNonNull(response.body()).string());
        } catch(OldPasswordInvalidOnPasswordChangeException oc) {
            throw oc;
        } catch(Exception e) {
            throw new OldPasswordInvalidOnPasswordChangeException("Old password invalid. Unable to perform user " +
                    "login validation. No password change has been performed: " + e.getLocalizedMessage(), e);
        }

        // Log off from the unnecessary session:
        FormBody userLogoffFormBody = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("refresh_token", userCredentials.getString("refresh_token"))
                .build();

        Request logoffRequest = new Request.Builder()
                .url(wellKnown().getString("end_session_endpoint"))
                .header("Authorization", "Bearer " + userCredentials.getString("access_token"))
                .post(userLogoffFormBody)
                .build();

        try(Response response = client.newCall(logoffRequest).execute()) {
            if(response.code() >= 300) {
                throw new KeyCloakLogoffException("Unable to perform logoff from client. Aborting password update. " +
                        "Error code: " +  response.code() + "\n\nMessage: " +
                        Objects.requireNonNull(response.body()).string());
            }
        } catch(KeyCloakLogoffException ke) {
            throw ke;
        } catch(Exception e) {
            throw new KeyCloakLogoffException("Unable to perform logoff from client. Aborting password update: " +
                    e.getLocalizedMessage(), e);
        }

        // If successful, change the user password:
        JsonObject credentials = keyCloakLogin();
        String microwebAdminResource = Configuration.read("keycloak admin resource");
        // Load created user:
        Request loadUserRequest;
        try {
            loadUserRequest = new Request.Builder()
                    .url(microwebAdminResource + "/users?username=" + URLEncoder.encode(u.getName(),
                            StandardCharsets.UTF_8.toString()))
                    .header("Authorization", "Bearer " + credentials.getString("access_token"))
                    .get()
                    .build();
        } catch(Exception e) {
            throw new UpdatePasswordUserNotFoundException("Unable to find the identification of the current " +
                    "user, to alter his/her password: " + e.getLocalizedMessage(), e);
        }

        JsonObject loadedUser = null;
        try(Response response = client.newCall(loadUserRequest).execute()) {
            if(response.code() >= 300) {
                throw new UpdatePasswordUserNotFoundException("Unable to find the identification of the current user, " +
                        "to alter him/her password. Return status: " + response.code() + "\n\nMessage:\n\n" +
                        Objects.requireNonNull(response.body()).string());
            }

            JsonArray foundUsers = new JsonArray(Objects.requireNonNull(response.body()).string());
            for(int i = 0; i < foundUsers.size(); i++) {
                JsonObject fu = foundUsers.getJsonObject(i);
                if(fu.getString("username").equals(u.getName())) {
                    loadedUser = fu;
                    break;
                }
            }
        } catch(UpdatePasswordUserNotFoundException cae) {
            throw cae;
        } catch(Exception e) {
            throw new UpdatePasswordUserNotFoundException("Unable to find the identification of the current user, " +
                    "to alter him/her password. Return status: " + e.getLocalizedMessage(), e);
        }

        if(loadedUser == null) {
            throw new UpdatePasswordUserNotFoundException("Unable to find the identification of the current user, " +
                    "to alter him/her password.");
        }

        // Set user password:
        JsonObject resetPasswordData = new JsonObject();
        resetPasswordData.put("type", "password");
        resetPasswordData.put("temporary", false);
        resetPasswordData.put("value", newPassword);

        Request resetPasswordRequest = new Request.Builder()
                .url(microwebAdminResource + "/users/" + loadedUser.getString("id") + "/reset-password")
                .addHeader("Authorization", "Bearer " + credentials.getString("access_token"))
                .put(FormBody.create(resetPasswordData.toString(), MediaType.parse("application/json; charset=utf-8")))
                .build();

        try(Response response = client.newCall(resetPasswordRequest).execute()) {
            if(response.code() >= 300) {
                throw new UpdatePasswordException("Unable to assign password. Status code: " +
                        response.code() + "\n\nMessage: " + Objects.requireNonNull(response.body()).string());
            }
        } catch(Exception e) {
            throw new UpdatePasswordException("Unable to assign password " +
                    "to the new user: " + e.getLocalizedMessage(), e);
        }

        keyCloakLogoff(credentials);
    }

    public static JsonObject loadUserData(User u) throws StandardException {
        JsonObject credentials = keyCloakLogin();
        String setyAdminResource = Configuration.read("keycloak admin resource");
        Request userLoadRequest = new Request.Builder()
                .url(setyAdminResource + "/users/" + u.getCentralControlId().toString())
                .addHeader("Authorization", "Bearer " + credentials.getString("access_token"))
                .get()
                .build();
        try(Response response = client.newCall(userLoadRequest).execute()) {
            if(response.code()>=300) {
                throw new UnableToUpdateOpenIdUserDataException("Unable to update user data. Status code: " +
                        response.code() + "\n\nMessage: " + Objects.requireNonNull(response.body()).string());
            } else {
                return new JsonObject(Objects.requireNonNull(response.body()).string());
            }
        } catch(Exception e) {
            throw new UnableToUpdateOpenIdUserDataException(
                    "Unable to update user data: " + e.getLocalizedMessage(), e);
        } finally {
            keyCloakLogoff(credentials);
        }
    }

    public static void updateUserData(JsonObject userData) throws StandardException {
        JsonObject credentials = keyCloakLogin();
        String setyAdminResource = Configuration.read("keycloak admin resource");
        Request updateUserRequest = new Request.Builder()
                .url(setyAdminResource + "/users/" + userData.getString("id"))
                .addHeader("Authorization", "Bearer " + credentials.getString("access_token"))
                .put(FormBody.create(userData.encode(), MediaType.parse("application/json; charset=utf-8")))
                .build();

        try(Response response = client.newCall(updateUserRequest).execute()) {
            if(response.code() >= 300) {
                throw new UnableToUpdateUserException("Unable to update user. Status code: " +
                        response.code() + "\n\nMessage: " + Objects.requireNonNull(response.body()).string());
            }
        } catch(Exception e) {
            throw new UnableToUpdateUserException("Unable to update user " + e.getLocalizedMessage(), e);
        } finally {
            keyCloakLogoff(credentials);
        }
    }

    public static void redefinePassword(User user, String password) throws StandardException {
        JsonObject credentials = keyCloakLogin();
        JsonObject resetPasswordData = new JsonObject();
        resetPasswordData.put("type", "password");
        resetPasswordData.put("temporary", false);
        resetPasswordData.put("value", password);
        String setyAdminResource = Configuration.read("keycloak admin resource");

        Request resetPasswordRequest = new Request.Builder()
                .url(setyAdminResource + "/users/" + user.getCentralControlId().toString() + "/reset-password")
                .addHeader("Authorization", "Bearer " + credentials.getString("access_token"))
                .put(FormBody.create(resetPasswordData.toString(), MediaType.parse("application/json; charset=utf-8")))
                .build();

        try(Response response = client.newCall(resetPasswordRequest).execute()) {
            if(response.code() >= 300) {
                throw new RedefinePasswordException("Unable to assign password. Status code: " +
                        response.code() + "\n\nMessage: " + Objects.requireNonNull(response.body()).string());
            }
        } catch(Exception e) {
            throw new RedefinePasswordException("Unable to assign password " +
                    "to the new user: " + e.getLocalizedMessage(), e);
        } finally {
            keyCloakLogoff(credentials);
        }
    }
}


