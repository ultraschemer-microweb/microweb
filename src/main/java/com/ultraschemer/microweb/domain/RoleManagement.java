package com.ultraschemer.microweb.domain;

import com.ultraschemer.microweb.domain.error.LoadRoleByIdException;
import com.ultraschemer.microweb.domain.error.LoadRoleByNameException;
import com.ultraschemer.microweb.entity.Role;
import com.ultraschemer.microweb.persistence.EntityUtil;
import org.hibernate.Session;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoleManagement {
    public static void initializeDefault() {
        try(Session session = EntityUtil.openTransactionSession()) {
            Role role = new Role();
            role.setName("user");
            session.persist(role);
            session.getTransaction().commit();
        } catch (PersistenceException pe) {
            System.out.println("Default role already registered. Continuing...");
        }
        try(Session session = EntityUtil.openTransactionSession()) {
            Role role = new Role();
            role.setName("user-manager");
            session.persist(role);
            session.getTransaction().commit();
        } catch (PersistenceException pe) {
            System.out.println("Default role already registered. Continuing...");
        }
        try(Session session = EntityUtil.openTransactionSession()) {
            Role role = new Role();
            role.setName("system-manager");
            session.persist(role);
            session.getTransaction().commit();
        } catch (PersistenceException pe) {
            System.out.println("Default role already registered. Continuing...");
        }
    }

    public static List<Role> loadAllRoles() {
        List<Role> res = new ArrayList<>();

        try(Session session = EntityUtil.openTransactionSession()) {
            res = session.createQuery("from Role", Role.class).list();
        } catch(PersistenceException ignored) { }

        return res;
    }

    public static Role loadRoleByName(String name) throws LoadRoleByNameException {
        try(Session session = EntityUtil.openTransactionSession()) {
            return session.createQuery("from Role where name = :name", Role.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch(PersistenceException pe) {
            throw new LoadRoleByNameException("Unable to load role by name: " + pe.getLocalizedMessage());
        }
    }

    public static Object loadRoleById(String id) throws LoadRoleByIdException {
        try(Session session = EntityUtil.openTransactionSession()) {
            return session.createQuery("from Role where id = :id", Role.class)
                    .setParameter("id", UUID.fromString(id))
                    .getSingleResult();
        } catch(PersistenceException pe) {
            throw new LoadRoleByIdException("Unable to load role by name: " + pe.getLocalizedMessage());
        }
    }
}
