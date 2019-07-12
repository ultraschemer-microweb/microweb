package com.ultraschemer.microweb.domain;

import com.google.common.base.Throwables;
import com.ultraschemer.microweb.controller.bean.CreateUserData;
import com.ultraschemer.microweb.domain.bean.AuthenticationData;
import com.ultraschemer.microweb.domain.bean.UserData;
import com.ultraschemer.microweb.domain.bean.UserRole;
import com.ultraschemer.microweb.domain.error.*;
import com.ultraschemer.microweb.entity.Role;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.entity.User_Role;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.utils.Security;
import com.ultraschemer.microweb.validation.Validator;
import org.hibernate.Session;

import javax.persistence.PersistenceException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserManagement {
    private static List<Role> loadRolesFromUser(UUID userId) throws UnableToGetRolesFromUser {
        List<Role> roles;

        try (Session session = EntityUtil.openTransactionSession()) {
            // Load, then, the roles the user assumes (Obs.: joins are avoided to reduce database query locking):
            List<User_Role> userRoles =
                    session.createQuery("from User_Role where userId = :userId", User_Role.class)
                    .setParameter("userId", userId)
                    .list();

            ArrayList<UUID> rolesId;

            if (userRoles != null && userRoles.size() > 0) {
                rolesId = new ArrayList<>(userRoles.size());
                for (User_Role ur : userRoles) {
                    rolesId.add(ur.getRoleId());
                }
            } else {
                rolesId = new ArrayList<>(0);
            }

            List<Role> roleList = session.createQuery("from Role where id in (:rolesId)", Role.class)
                    .setParameterList("rolesId", rolesId)
                    .list();

            if (roleList != null && roleList.size() > 0) {
                roles = roleList;
            } else {
                roles = new ArrayList<>(0);
            }
        } catch (PersistenceException pe) {
            throw new UnableToGetRolesFromUser("It's not possible to get roles from user: " + pe.getLocalizedMessage() +
                    "\nStack Trace: " + Throwables.getStackTraceAsString(pe));
        }

        return roles;
    }
    /**
     * Load the user from his/her name.
     *
     * @param name This is the user name.
     * @return The found user data, as a serializable bean.
     *
     */
    public static UserData loadUserByName(String name) throws StandardException {
        User user;

        try (Session session = EntityUtil.openTransactionSession()) {
            // Load user data:
            List<User> users = session.createQuery("from User where name = :name", User.class)
                    .setParameter("name", name)
                    .list();

            if (users.size() != 1) {
                throw new UserNotFoundException("User having name \"" + name + "\" not found.");
            }

            user = users.iterator().next();
        }

        UserData uData = new UserData();
        uData.setName(user.getName());
        uData.setId(user.getId());
        uData.setAlias(user.getAlias());

        List<Role> roles = loadRolesFromUser(user.getId());
        List<UserRole> userRoles = new ArrayList<>(roles.size());
        for(Role r: roles) {
            UserRole userRole = new UserRole();
            userRole.setId(r.getId().toString());
            userRole.setName(r.getName());
            userRoles.add(userRole);
        }

        uData.setRoles(userRoles);

        return uData;
    }

    public static UserData loadUserBySecureId(String id) throws StandardException {
        User user;

        try (Session session = EntityUtil.openTransactionSession()) {
            // Load user data:
            List<User> users = session.createQuery("from User where id = :id", User.class)
                    .setParameter("id", UUID.fromString(id))
                    .list();
            if (users.size() != 1) {
                throw new UserNotFoundException("Usuário having id equals to \"" + id + "\" not found.");
            }

            user = users.iterator().next();
        } catch (PersistenceException pe) {
            throw new UnableToLoadUserBySecureId("It's not possible to load user by secure id: " +
                    pe.getLocalizedMessage() + "\nStack Trace: " + Throwables.getStackTraceAsString(pe));
        }

        UserData uData = new UserData();
        uData.setName(user.getName());
        uData.setId(user.getId());
        uData.setAlias(user.getAlias());

        List<Role> roles = loadRolesFromUser(user.getId());
        List<UserRole> userRoles = new ArrayList<>(roles.size());
        for(Role r: roles) {
            UserRole userRole = new UserRole();
            userRole.setId(r.getId().toString());
            userRole.setName(r.getName());
            userRoles.add(userRole);
        }

        uData.setRoles(userRoles);

        return uData;
    }

    public static UserData loadUser(String nameOrId) throws StandardException {
        // 1. Verifica se o campo passado é um UUID
        String uuidPattern =
                "^(\\{[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}\\}|" +
                "[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12})$";

        if(nameOrId.matches(uuidPattern)) {
            try {
                return loadUserBySecureId(nameOrId);
            } catch (UserNotFoundException unfe) {
                unfe.printStackTrace();
            }
        }

        return loadUserByName(nameOrId);
    }

    /**
     * This method verifies if the default Root user exists, and if the default Root role exists to.
     * If not, create both with the default root password.
     */
    public static void initializeRoot() {
        try(Session session = EntityUtil.openTransactionSession()) {
            try {
                loadUserByName("root");
            } catch (UserNotFoundException ue) {
                // User doesn't exist, verify role:
                Role role;

                try {
                    role = session.createQuery("from Role where name = :name", Role.class)
                            .setParameter("name", "root")
                            .getSingleResult();
                } catch (PersistenceException pe) {
                    // Role doesn't exists - create it.
                    // The root role is the most important and central role in the system, controlling "ALL ASPECTS"
                    // of the system - and must be granted only to the true managers of the system.
                    role = new Role();
                    role.setName("root");
                    session.persist(role);
                }

                // Then, create the root user:
                User user = new User();
                user.setName("root");
                user.setStatus("active");
                user.setAlias("root");
                user.setPassword(Security.hashade("rootpasswordchangemenow"));
                session.persist(user);

                // And, finally, link the user to his/her role:
                User_Role user_role = new User_Role();
                user_role.setUserId(user.getId());
                user_role.setRoleId(role.getId());
                session.persist(user_role);

                session.getTransaction().commit();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
	
	public static void updateUserPassword(String strUserId, String userName, String currentPasswd, String passwd,
                                          String passwdConfirmation)
            throws StandardException, InvalidKeySpecException, NoSuchAlgorithmException
    {
        UUID userId = UUID.fromString(strUserId);

        // Verify parameter data:
        if(passwd.equals(passwdConfirmation)) {
            AuthenticationData authData = new AuthenticationData();
            authData.setName(userName);
            authData.setPassword(currentPasswd);

            // If this call doesn't throw an exception, then the user password is valid:
            AuthManagement.authenticate(authData);
        } else {
            throw new UserPassNotEqualsException("Password and its confirmation don't match.");
        }

        try(Session session = EntityUtil.openTransactionSession()) {
            session.createQuery("update User set password = :passwd where id = :id")
                    .setParameter("passwd", Security.hashade(passwd))
                    .setParameter("id", userId)
                    .executeUpdate();
            session.getTransaction().commit();
        } catch(PersistenceException pe) {
            throw new UpdateUserPasswordException("Unable to update password to user: " + pe.getLocalizedMessage());
        }
    }

    public static void registerSimpleUser(CreateUserData u, String roleName) throws StandardException{
        Validator.ensure(u);

        try(Session session = EntityUtil.openTransactionSession()) {
            User newU = new User();
            User_Role newUR = new User_Role();
            Role role = session.createQuery("from Role where name = :name", Role.class)
                    .setParameter("name", roleName).getSingleResult();

            // Create new user:
            newU.setName(u.getName());
            newU.setAlias(u.getAlias());
            newU.setPassword(Security.hashade(u.getPassword()));
            newU.setStatus("new");
            session.persist(newU);

            // Link the user to his/her role:
            newUR.setUserId(newU.getId());
            newUR.setRoleId(role.getId());
            session.persist(newUR);

            // PErsist data in database:
            session.getTransaction().commit();

        } catch(Exception pe) {
            throw new SimpleUserRegistrationException("Unable to register user: " + pe.getLocalizedMessage());
        }
    }

    public static void setRoleToUser(UUID userId, String roleName) throws StandardException {
        try(Session session = EntityUtil.openTransactionSession()) {
            User_Role newUR = new User_Role();

            Role role = session.createQuery("from Role where name = :name", Role.class)
                    .setParameter("name", roleName).getSingleResult();

            newUR.setUserId(userId);
            newUR.setRoleId(role.getId());

            session.persist(newUR);
            session.getTransaction().commit();
        } catch(PersistenceException pe) {
            throw new UserRoleSetException("Unable to set role to user: " + pe.getLocalizedMessage());
        }
    }

    public static void updateUserName(String strUserId, String userName) throws StandardException {
        try(Session session = EntityUtil.openTransactionSession()) {
            UUID userId = UUID.fromString(strUserId);
            session.createQuery("update User set name = :name where id = :id")
                    .setParameter("name", userName)
                    .setParameter("id", userId)
                    .executeUpdate();
            session.getTransaction().commit();
        } catch(PersistenceException pe) {
            throw new UpdateUserNameException("Unable to update name to user: " + pe.getLocalizedMessage());
        }
    }

    public static void updateUserAlias(String strUserId, String userAlias) throws StandardException {
        try(Session session = EntityUtil.openTransactionSession()) {
            UUID userId = UUID.fromString(strUserId);
            session.createQuery("update User set alias = :alias where id = :id")
                    .setParameter("alias", userAlias)
                    .setParameter("id", userId)
                    .executeUpdate();
            session.getTransaction().commit();
        } catch(PersistenceException pe) {
            throw new UpdateUserAliasException("Unable to update name to user: " + pe.getLocalizedMessage());
    }
    }

    public static List<UserData> loadUsers(int count, int offset) throws StandardException {
        try(Session session = EntityUtil.openTransactionSession()) {
            List<UUID> ids = session.createQuery("select id from User", UUID.class)
                    .setFirstResult(offset)
                    .setMaxResults(count)
                    .list();

            List<UserData> returnData = new ArrayList<>(ids.size());

            for(UUID id: ids) {
                returnData.add(UserManagement.loadUserBySecureId(id.toString()));
            }

            return returnData;
        } catch(PersistenceException pe) {
            throw new LoadUsersException("Unable to load users: " + pe.getLocalizedMessage());
        }
    }
}
