package com.ultraschemer.microweb.domain;

import com.ultraschemer.microweb.domain.bean.AuthenticationData;
import com.ultraschemer.microweb.domain.bean.AuthorizationData;
import com.ultraschemer.microweb.domain.error.UnableToGenerateAccessTokenException;
import com.ultraschemer.microweb.domain.error.UnauthorizedException;
import com.ultraschemer.microweb.entity.AccessToken;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.persistence.EntityUtil;
import org.hibernate.Session;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
     * TODO: Create a configuration for AccessToken TTL, since currently it's hard-coded.
     */
    private static final int TOKEN_TTL = 3600;

    /**
     * An access token is a Base85 string, varying between five and sixty four characters.
     * @return The generated access token.
     */
    private static String generateAccessToken() {
        final String characterTable = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-:+=^!/*?&<>()[]{}@%$#";
        int size = ThreadLocalRandom.current().nextInt(5, 65);
        StringBuffer tokenBuffer = new StringBuffer();
        for(int i=0; i<size; i++) {
            int pos = ThreadLocalRandom.current().nextInt(0, 85);
            tokenBuffer.append(characterTable.charAt(pos));
        }
        return tokenBuffer.toString();
    }

    /**
     * Method used to authenticate a user from his/her basic data.
     *
     * TODO: Authorization implementation doesn't validate TTL, yet. It's necessary to implement it.
     *
     * @param authenticationData Necesssary data for authentication, as this data is provided by the user interface.
     * @return Authorization data, with autorization token and its TTL (time-to-live) value.
     * @throws UnauthorizedException Raised if the user can't enter in the system, due some access authentication limitation.
     * @throws UnableToGenerateAccessTokenException Raised if a random authorization access token couldn't be generated.
     */
    public static AuthorizationData authenticate(AuthenticationData authenticationData)
            throws UnauthorizedException, UnableToGenerateAccessTokenException {
        // TODO: Implement authorization token TTL.

        AuthorizationData authorizationData = new AuthorizationData();
        Boolean go = true;

        Session session = EntityUtil.openTransactionSession();

        // Load the user by its name:
        User user;
        try {
            user = session.createQuery("from User where name = :name", User.class)
                    .setParameter("name", authenticationData.getName())
                    .getSingleResult();
        } catch(NoResultException ne) {
            session.close();
            String message =
                    "It has been not possible to authenticate user/password information. " +
                            "It's not possible to proceed.";
            throw new UnauthorizedException(message);
        }

        if(!validate(authenticationData.getPassword(), user.getPassword())) {
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

        while(go) {
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

        session.close();
        authorizationData.setAccessToken(ac.getToken());
        authorizationData.setTtl(TOKEN_TTL);

        return authorizationData;
    }

    /**
     *
     * @param token A token de autorização.
     * @return Os dados de usuário autorizado.
     * @throws UnauthorizedException Se a token não for válida ou se tiver expirada, essa exceção é lançada.
     */
    public static User authorize(String token) throws UnauthorizedException {
        // TODO: Implementar o TTL da token de autorização, para melhorar a segurança.

        Session session = EntityUtil.openTransactionSession();

        // Carrega a token de autorização:
        List<AccessToken> tokens = session.createQuery("from AccessToken where token = :token and status = :status", AccessToken.class)
                .setParameter("token", token)
                .setParameter("status", VALID)
                .list();

        if(tokens.size() == 0) {
            // Caso não haja Token de usuário, ele não está autorizado a continuar:
            throw new UnauthorizedException("Acesso inválido - não autorizado a continuar.");
        }

        AccessToken accessToken = tokens.iterator().next();

        @SuppressWarnings("unchecked")
        User user = (User) session.createQuery("from User where id = :id")
                .setParameter("id", accessToken.getUserId()).list().iterator().next();

        session.close();

        return user;
    }

    /**
     * Invalida a token de acesso, não permitindo mais acesso daquela token ao sistema.
     * @param token A token de
     */
    public static void unauthorize(String token) throws UnauthorizedException {
        Session session = EntityUtil.openTransactionSession();
        int updated = session.createQuery("update AccessToken set status = :status where token = :token")
                .setParameter("status", INVALID)
                .setParameter("token", token)
                .executeUpdate();
        session.getTransaction().commit();
        session.close();

        if(updated == 0) {
            String message = "Usuário não autorizado ou acesso já expirado.";
            throw new UnauthorizedException(message);
        }
    }
}
