package com.ultraschemer.microweb.domain;

import com.google.common.base.Throwables;
import com.ultraschemer.microweb.domain.error.UnableToReadConfigurationException;
import com.ultraschemer.microweb.domain.error.UnableToWriteConfigurationException;
import com.ultraschemer.microweb.persistence.EntityUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.PersistenceException;
import java.util.List;

/**
 * This class is a helper to save execution temporal variables to the system. These variables aren't configurations,
 * but data used to control the GLOBAL state of all application and of all clients - data which are only utilities,
 * not justifiable to be stored in specific tables.
 *
 * This implementation is done using static calls, to ease code reading and consequentially to improve maintenance.
 */
public class Configuration {
    /**
     * Write the runtime variable to the system.
     *
     * @param name Variable name.
     * @param value The variable new value.
     */
    public static void write(String name, String value) throws UnableToWriteConfigurationException {
        try(Session session = EntityUtil.openTransactionSession()) {
            Transaction transaction = session.getTransaction();

            com.ultraschemer.microweb.entity.Configuration configuration = new com.ultraschemer.microweb.entity.Configuration();

            configuration.setName(name);
            configuration.setValue(value);

            try {
                session.persist(configuration);
                transaction.commit();
            } catch (PersistenceException cve) {
                transaction.begin();
                // Try to update an existent variable:
                session.createQuery("update Configuration set value = :value where name = :name")
                        .setParameter("name", name)
                        .setParameter("value", value)
                        .executeUpdate();

                session.getTransaction().commit();
            }
        } catch (PersistenceException pe) {
            throw new UnableToWriteConfigurationException("Unable to write configuration: " + pe.getLocalizedMessage() +
                    "\nStack Trace: " + Throwables.getStackTraceAsString(pe));
        }
    }

    /**
     * Read the runtime variable from the system.
     *
     * @param name Variable name.
     * @return The current variable value.
     */
    public static String read(String name) throws UnableToReadConfigurationException {
        try(Session session = EntityUtil.openTransactionSession()) {
            List<com.ultraschemer.microweb.entity.Configuration> configuration =
                    session.createQuery("from Configuration where name = :name",
                            com.ultraschemer.microweb.entity.Configuration.class)
                            .setParameter("name", name).list();

            if (configuration.size() == 0) {
                return "";
            }

            com.ultraschemer.microweb.entity.Configuration configRetVal = configuration.iterator().next();

            return configRetVal.getValue();
        } catch (PersistenceException pe) {
            throw new UnableToReadConfigurationException("Unable to read configuration: " + pe.getLocalizedMessage() +
                    "\nStack Trace: " + Throwables.getStackTraceAsString(pe));
        }
    }
}