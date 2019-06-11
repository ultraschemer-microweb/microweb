package com.ultraschemer.microweb.domain;

import com.google.common.base.Throwables;
import com.ultraschemer.microweb.domain.bean.UserData;
import com.ultraschemer.microweb.domain.bean.UserRole;
import com.ultraschemer.microweb.domain.error.UnableToGetRolesFromUser;
import com.ultraschemer.microweb.domain.error.UnableToLoadUserBySecureId;
import com.ultraschemer.microweb.domain.error.UserAccessConfigurationException;
import com.ultraschemer.microweb.domain.error.UserNotFoundException;
import com.ultraschemer.microweb.entity.Role;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.entity.User_Role;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.utils.Security;
import org.hibernate.Session;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserManagement {
    private static List<Role> loadRolesFromUser(UUID userId) throws UnableToGetRolesFromUser {
        List<Role> roles;

        try (Session session = EntityUtil.openTransactionSession()) {
            // Load, then, the roles the user assumes (Obs.: joins are avoided to reduce database query locking):
            @SuppressWarnings("unchecked")
            List<User_Role> userRoles = session.createQuery("from User_Role where userId = :userId")
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
     * @throws UserNotFoundException
     * @throws UserAccessConfigurationException
     */
    public static UserData loadUserByName(String name) throws StandardException {
        User user;

        try (Session session = EntityUtil.openTransactionSession()) {
            // Load user data:
            @SuppressWarnings("unchecked")
            List<User> users = (List<User>) session.createQuery("from User where name = :name")
                    .setParameter("name", name)
                    .list();

            if (users.size() != 1) {
                throw new UserNotFoundException("User having name \"" + name + "\" not found.");
            }

            user = users.iterator().next();
        }

        UserData uData = new UserData();
        uData.setName(user.getName());
        uData.setId(user.getId().toString());
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
            @SuppressWarnings("unchecked")
            List<User> users = session.createQuery("from User where id = :id")
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
        uData.setId(user.getId().toString());
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
            } catch (UserNotFoundException unfe) { }
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
}
