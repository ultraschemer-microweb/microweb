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
        String addr = System.getenv("MICROWEB_DB_ADDR");
        String userName = System.getenv("MICROWEB_DB_USER");
        String userPassword = System.getenv("MICROWEB_DB_PASSWD");

        if(addr != null) {
            addr = "jdbc:postgresql://" + addr;
            System.out.println("Using custom PostgreSQL Database Address: " + addr);
        }

        if(userName != null) {
            System.out.println("Using custom PostgreSQL Database User: " + userName);
        }

        if(userPassword != null) {
            System.out.println("Using custom PostgreSQL Database Password.");
        }

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().configure();

        if(addr != null) {
            builder.applySetting("hibernate.connection.url", addr);
        }

        if(addr != null) {
            builder.applySetting("hibernate.connection.username", userName);
        }

        if(addr != null) {
            builder.applySetting("hibernate.connection.password", userPassword);
        }

        final StandardServiceRegistry registry = builder.build();
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
