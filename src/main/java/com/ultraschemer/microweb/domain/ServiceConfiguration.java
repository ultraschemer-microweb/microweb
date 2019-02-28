package com.ultraschemer.microweb.domain;

import com.google.common.base.Throwables;
import com.ultraschemer.microweb.domain.error.UnabletoGetHttpServicePortException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import com.ultraschemer.microweb.entity.Configuration;
import com.ultraschemer.microweb.domain.error.InvalidHttpServerPortConfigurationException;
import com.ultraschemer.microweb.persistence.EntityUtil;

import javax.persistence.PersistenceException;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * This class load basic service configuration, necessary to access all other services.
 */
public class ServiceConfiguration {
    private static int httpServicePort = 0;

    public static int getHttpServicePort()
            throws InvalidHttpServerPortConfigurationException, UnabletoGetHttpServicePortException {
        if(httpServicePort == 0) {
            try(Session session = EntityUtil.openTransactionSession()) {

                Query q = session.createQuery("from Configuration c where c.name = :name")
                        .setParameter("name", "Java backend port");

                @SuppressWarnings("unchecked")
                List<Configuration> confs = q.list();

                if (confs.size() != 1) {
                    String message = "Internal service port configuration is invalid - it's impossible to load service.";
                    throw new InvalidHttpServerPortConfigurationException(message);
                }

                httpServicePort = parseInt(confs.iterator().next().getValue());
            } catch(PersistenceException pe) {
                throw new UnabletoGetHttpServicePortException("It`s not possible to get http service port: " +
                        pe.getLocalizedMessage() + "\nStack Trace: " + Throwables.getStackTraceAsString(pe));
            }
        }

        return httpServicePort;

    }
}
