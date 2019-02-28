package com.ultraschemer.microweb.domain;

import com.google.common.base.Throwables;
import com.ultraschemer.microweb.domain.error.UnableToReadRuntimeException;
import com.ultraschemer.microweb.domain.error.UnableToWriteRuntimeException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.ultraschemer.microweb.persistence.EntityUtil;

import javax.persistence.PersistenceException;
import java.util.List;

/**
 * This class is a helper to save execution temporal variables to the system. These variables aren't configurations,
 * but data used to control the GLOBAL state of all application and of all clients - data which are only utilities,
 * not justifiable to be stored in specific tables.
 *
 * This implementation is done using static calls, to ease code reading and consequentially to improve maintenance.
 */
public class Runtime {
    /**
     * Write the runtime variable to the system.
     *
     * @param name Variable name.
     * @param value The variable new value.
     */
    public static void write(String name, String value) throws UnableToWriteRuntimeException {
        try(Session session = EntityUtil.openTransactionSession()) {
            Transaction transaction = session.getTransaction();

            com.ultraschemer.microweb.entity.Runtime runtime = new com.ultraschemer.microweb.entity.Runtime();

            runtime.setName(name);
            runtime.setValue(value);

            try {
                session.persist(runtime);
                transaction.commit();
            } catch (PersistenceException cve) {
                transaction.begin();
                // Try to update an existent variable:
                session.createQuery("update Runtime set value = :value where name = :name")
                        .setParameter("name", name)
                        .setParameter("value", value)
                        .executeUpdate();

                session.getTransaction().commit();
            }
        } catch (PersistenceException pe) {
            throw new UnableToWriteRuntimeException("Unable to write runtime variable: " + pe.getLocalizedMessage() +
                    "\nStack Trace: " + Throwables.getStackTraceAsString(pe));
        }
    }

    /**
     * Read the runtime variable from the system.
     *
     * @param name Variable name.
     * @return The current variable value.
     */
    public static String read(String name) throws UnableToReadRuntimeException {
        try(Session session = EntityUtil.openTransactionSession()) {
            List<com.ultraschemer.microweb.entity.Runtime> runtimes =
                    session.createQuery("from Runtime where name = :name",
                            com.ultraschemer.microweb.entity.Runtime.class)
                            .setParameter("name", name).list();

            if (runtimes.size() == 0) {
                return "";
            }

            com.ultraschemer.microweb.entity.Runtime runtime = runtimes.iterator().next();

            return runtime.getValue();
        } catch (PersistenceException pe) {
            throw new UnableToReadRuntimeException("Unable to read runtime variable: " + pe.getLocalizedMessage() +
                    "\nStack Trace: " + Throwables.getStackTraceAsString(pe));
        }
    }
}
