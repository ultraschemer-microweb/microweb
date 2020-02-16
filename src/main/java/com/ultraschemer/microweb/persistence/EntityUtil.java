package com.ultraschemer.microweb.persistence;

import com.google.common.base.Throwables;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

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
        String configurationURL = System.getenv("MICROWEB_HIBERNATE_URL_CONFIG");

        if(address != null) {
            System.out.println("Using custom Database Address: " + address);
        }

        if(userName != null) {
            System.out.println("Using custom Database User: " + userName);
        }

        if(userPassword != null) {
            System.out.println("Using custom Database Password.");
        }

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        if(configurationURL != null) {
            try {
                System.out.println("Using custom Database configuration file. " +
                        "Environment variables configurations have preference over this file.");
                builder.configure(new URL(configurationURL));
            } catch(MalformedURLException me) {
                Logger logger = LoggerFactory.getLogger(EntityUtil.class);
                logger.error("Unable to load configuration data from address: " + configurationURL + ". " +
                        "Using defaults. Error: " + Throwables.getStackTraceAsString(me));
                builder.configure();
            }
        } else {
            builder.configure();
        }

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
