package com.ultraschemer.microweb.domain;

import com.google.common.base.Throwables;
import com.ultraschemer.microweb.domain.bean.AuthenticationData;
import com.ultraschemer.microweb.domain.bean.AuthorizationData;
import com.ultraschemer.microweb.domain.error.*;
import com.ultraschemer.microweb.entity.AccessToken;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.utils.Security;
import org.hibernate.Session;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import java.util.Date;
import java.util.List;

import static com.ultraschemer.microweb.utils.Security.validate;

/**
 * This class has implementation of business rules to authorization and authentication.
 */
public class AuthManagement {
    /**
     * Token status, showing if it's valid.
     */
    private static final String VALID = "valid";

    /**
     * Token status, showing if it's invalid.
     */
    private static final String INVALID = "invalid";

    /**
     * TTL value for access token.
     */
    private static final int TOKEN_TTL = 3600;

    /**
     * An access token is a Base85 string, varying between five and sixty four characters.
     * @return The generated access token.
     */
    private static String generateAccessToken() {
        return Security.randomToken();
    }

    /**
     * Method used to authenticate a user from his/her basic data.
     *
     * @param authenticationData Necessary data for authentication, as this data is provided by the user interface.
     * @return Authorization data, with authorization token and its TTL (time-to-live) value.
     * @throws UnauthorizedException Raised if the user can't enter in the system, due some access authentication limitation.
     * @throws UnableToGenerateAccessTokenException Raised if a random authorization access token couldn't be generated.
     */
    public static AuthorizationData authenticate(AuthenticationData authenticationData)
            throws UnauthorizedException, UnableToGenerateAccessTokenException, UnableToAuthenticateException {

        AuthorizationData authorizationData = new AuthorizationData();
        boolean go = true;
        int tokenTtl;
        try {
            tokenTtl = Integer.parseInt(Configuration.read("access token ttl in seconds"));
        } catch(UnableToReadConfigurationException|NumberFormatException e) {
            tokenTtl = TOKEN_TTL;
        }

        try(Session session = EntityUtil.openTransactionSession()) {
            // Load the user by its name:
            User user;
            try {
                user = session.createQuery("from User where name = :name", User.class)
                        .setParameter("name", authenticationData.getName())
                        .getSingleResult();
            } catch (NoResultException ne) {
                session.close();
                String message =
                        "It has been not possible to authenticate user/password information. " +
                                "It's not possible to proceed.";
                throw new UnauthorizedException(message);
            }

            if (!validate(authenticationData.getPassword(), user.getPassword())) {
                session.close();
                String message =
                        "It has been not possible to authenticate user/password information. " +
                                "It's not possible to proceed.";
                throw new UnauthorizedException(message);
            }

            int tryCount = 0;

            AccessToken ac = new AccessToken();
            ac.setStatus(VALID);
            ac.setUserId(user.getId());

            while (go) {
                try {
                    ac.setToken(generateAccessToken());

                    // Persist - if any error occurs, try again, until ten times - then give up.
                    session.persist(ac);

                    session.getTransaction().commit();
                    go = false;
                } catch (Exception e) {
                    go = true;
                    tryCount++;

                    if (tryCount >= 10) {
                        session.close();

                        String message = "Não foi possível criar token de autorização. Tente novamente.";
                        throw new UnableToGenerateAccessTokenException(message);
                    }
                }
            }

            authorizationData.setAccessToken(ac.getToken());

            authorizationData.setTtl(tokenTtl);
        } catch (PersistenceException pe) {
            throw new UnableToAuthenticateException("Unable to authenticate: " + pe.getLocalizedMessage() +
                    "\nStack Trace: " + Throwables.getStackTraceAsString(pe));
        }

        return authorizationData;
    }

    /**
     *
     * @param token A token de autorização.
     * @return Os dados de usuário autorizado.
     * @throws UnauthorizedException Se a token não for válida ou se tiver expirada, essa exceção é lançada.
     */
    public static User authorize(String token) throws UnauthorizedException, UnableToAuthorizeException {
        int tokenTtl;
        try {
            tokenTtl = Integer.parseInt(Configuration.read("access token ttl in seconds"));
        } catch(UnableToReadConfigurationException|NumberFormatException e) {
            tokenTtl = TOKEN_TTL;
        }

        AccessToken accessToken;
        try(Session session = EntityUtil.openTransactionSession()) {
            // Carrega a token de autorização:
            List<AccessToken> tokens = session.createQuery("from AccessToken where token = :token and status = :status", AccessToken.class)
                    .setParameter("token", token)
                    .setParameter("status", VALID)
                    .list();

            if (tokens.size() == 0) {
                // Caso não haja Token de usuário, ele não está autorizado a continuar:
                throw new UnauthorizedException("Acesso inválido - não autorizado a continuar.");
            }

            accessToken = tokens.iterator().next();

            // Atualiza a token de autorização:
            if(accessToken.getUpdatedAt().before(new Date(new Date().getTime() - tokenTtl * 1000))) {
                // Se chegou-se aqui, significa que a token não foi atualizada por um período maior do que o permitido pelo TTL
                accessToken.setStatus(INVALID);
                accessToken.setExpiration(new Date());
                session.persist(accessToken);
                session.getTransaction().commit();
                throw new UnableToAuthorizeException("Authorization expired.");
            } else {
                accessToken.setStatus(VALID);
                session.persist(accessToken);
                session.getTransaction().commit();
            }

            return session.createQuery("from User where id = :id", User.class)
                    .setParameter("id", accessToken.getUserId()).list().iterator().next();
        } catch (PersistenceException pe) {
            throw new UnableToAuthorizeException("Unable to authorize: " + pe.getLocalizedMessage() +
                    "\nStack Trace: " + Throwables.getStackTraceAsString(pe));
        }
    }

    /**
     * Invalida a token de acesso, não permitindo mais acesso daquela token ao sistema.
     * @param token A token de
     */
    public static void unauthorize(String token) throws UnauthorizedException, UnableToUnauthorizeException {
        try(Session session = EntityUtil.openTransactionSession()) {
            int updated = session.createQuery("update AccessToken set status = :status, updatedAt = :uat where token = :token")
                    .setParameter("status", INVALID)
                    .setParameter("token", token)
                    .setParameter("uat", new Date())
                    .executeUpdate();
            session.getTransaction().commit();

            if (updated == 0) {
                String message = "Usuário não autorizado ou acesso já expirado.";
                throw new UnauthorizedException(message);
            }
        } catch (PersistenceException pe) {
            throw new UnableToUnauthorizeException("Unable to unauthorize: " + pe.getLocalizedMessage() +
                    "\nStack Trace: " + Throwables.getStackTraceAsString(pe));
        }
    }
}
