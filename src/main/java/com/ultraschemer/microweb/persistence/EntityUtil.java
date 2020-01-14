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
    public static void initialize() {
        if(sessionFactory == null) {
            initialize(EntityUtil.initConfiguration());
        }
    }

    protected static void initialize(StandardServiceRegistryBuilder initialConfiguration) {
        if(sessionFactory == null) {
            final StandardServiceRegistry registry = initialConfiguration.build();
            try {
                sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            } catch (Exception e) {
                StandardServiceRegistryBuilder.destroy(registry);
                e.printStackTrace();
            }
        }
    }

    /**
     * This class can be overridden, to enable custom database access initialization
     * @return The database builder to be used by Hibernate, globally, in the project
     */
    public static StandardServiceRegistryBuilder initConfiguration() {
        String address = System.getenv("MICROWEB_DB_ADDRESS");
        String userName = System.getenv("MICROWEB_DB_USER");
        String userPassword = System.getenv("MICROWEB_DB_PASSWORD");

        if(address != null) {
            System.out.println("Using custom Database Address: " + address);
        }

        if(userName != null) {
            System.out.println("Using custom Database User: " + userName);
        }

        if(userPassword != null) {
            System.out.println("Using custom Database Password.");
        }

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().configure();

        if(address != null) {
            builder.applySetting("hibernate.connection.url", address);
        }

        if(address != null) {
            builder.applySetting("hibernate.connection.username", userName);
        }

        if(address != null) {
            builder.applySetting("hibernate.connection.password", userPassword);
        }

        return builder;
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
