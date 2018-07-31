package com.ultraschemer.microweb.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * This class is the access boilerplate to Hibernate facilities.
 */
public class EntityUtil {
    /**
     * Global session factory to the program.
     */
    private static SessionFactory sessionFactory;

    /**
     * Static initialization, called the first time the EntityUtil class is called.
     */
    static {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            e.printStackTrace();
        }
    }

    /**
     * This is the safe access to the session factory, to create communication sessions to database.
     * @return The session.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Get, directly, session access.
     */
    public static Session openSession() {
        return sessionFactory.openSession();
    }

    /**
     * Get access to a session, with a transaction already open.
     */
    public static Session openTransactionSession() {
        Session s = sessionFactory.openSession();
        s.beginTransaction();

        return s;
    }

}
