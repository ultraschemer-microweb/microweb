package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.domain.JwtSecurityManager;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.handler.BodyHandler;
import com.ultraschemer.microweb.domain.AuthManagement;
import com.ultraschemer.microweb.domain.bean.AuthenticationData;
import com.ultraschemer.microweb.domain.bean.AuthorizationData;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.error.UnknownException;
import com.ultraschemer.microweb.vertx.BasicController;

public class LoginController implements BasicController {
    private Vertx vertx;

    public LoginController(Vertx vertx) {
        this.vertx = vertx;
    }

    public void evaluate(Route route) {
        // Carrega algum dado do banco de dados, em um "blocking handler":
        route.handler(BodyHandler.create()).blockingHandler(routingContext -> {
            AuthenticationData authenticationData =
                    Json.decodeValue(routingContext.getBodyAsString(), AuthenticationData.class);

            try {
                AuthorizationData authorizationData = AuthManagement.authenticate(authenticationData);
                JwtSecurityManager.generateBearer(this.vertx, authorizationData);

                routingContext.put("data", authorizationData);
            } catch(Exception e) {
                routingContext.put("error", e);
            }

            routingContext.next();
        }).handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            AuthorizationData data = routingContext.get("data");

            response.putHeader("Content-Type", "application/json; charset=utf-8");

            if(data == null) {
                Exception error = routingContext.get("error");
                if(error instanceof StandardException) {
                    routingContext.response().setStatusCode(401).end(Json.encode(((StandardException) error).bean()));
                } else {
                    UnknownException unknown = new UnknownException("Unknown error: " + error.getMessage());
                    routingContext.response().setStatusCode(500).end(Json.encode(unknown.bean()));
                }
            } else {
                routingContext.response().end(Json.encode(data));
            }
        });
    }
}