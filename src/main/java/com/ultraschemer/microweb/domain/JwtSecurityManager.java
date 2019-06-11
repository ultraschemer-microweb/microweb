package com.ultraschemer.microweb.domain;

import com.ultraschemer.microweb.domain.bean.AuthorizationData;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.utils.Security;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

import java.util.ArrayList;

public class JwtSecurityManager {
    public static void initializeSKey() throws StandardException {
        // Get the symmetric key:
        String key = Configuration.read("jwt-s-key");
        if(key.equals("")) {
            // It is needed to insert a random symmetric security key:
            Configuration.write("jwt-s-key", Security.randomToken());
        }
    }

    private static JWTAuth generateProvider(Vertx vertx) throws StandardException {
        String key = Configuration.read("jwt-s-key");

        JWTAuthOptions options = new JWTAuthOptions();
        ArrayList<PubSecKeyOptions> pubSecKeyOptionsList = new ArrayList<>(1);

        PubSecKeyOptions pubSecKeyOptions = new PubSecKeyOptions(new JsonObject());
        pubSecKeyOptions.setAlgorithm("HS256");
        pubSecKeyOptions.setPublicKey(Configuration.read("jwt-s-key"));
        pubSecKeyOptions.setSymmetric(true);

        pubSecKeyOptionsList.add(pubSecKeyOptions);
        options.setPubSecKeys(pubSecKeyOptionsList);

        return JWTAuth.create(vertx, options);
    }

    public static void generateBearer(Vertx vertx, AuthorizationData authorizationData) throws StandardException {
        JsonObject tokenObject = new JsonObject();
        tokenObject.put("sub", authorizationData.getAccessToken());
        JWTAuth provider = generateProvider(vertx);
        authorizationData.setBearer(provider.generateToken(tokenObject));
    }

    public static void authenticateBearer(Vertx vertx, String bearer, java.util.function.Consumer<User> handler)
            throws StandardException {
        JWTAuth provider = generateProvider(vertx);
        JsonObject params = new JsonObject();
        params.put("jwt", bearer);

        // Just authenticate - don't perform authorization
        provider.authenticate(params, event -> {
            User u = event.result();
            System.out.println(u.principal().toString());
            handler.accept(u);
        });
    }
}