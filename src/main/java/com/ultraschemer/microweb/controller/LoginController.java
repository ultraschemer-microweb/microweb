package com.ultraschemer.microweb.controller;

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
    public void evaluate(Route route) {
        // Carrega algum dado do banco de dados, em um "blocking handler":
        route.handler(BodyHandler.create()).blockingHandler(routingContext -> {
            AuthenticationData authenticationData =
                    Json.decodeValue(routingContext.getBodyAsString(), AuthenticationData.class);

            try {
                AuthorizationData authorizationData = AuthManagement.authenticate(authenticationData);
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
                    routingContext.response().end(Json.encode(((StandardException) error).bean()));
                } else {
                    UnknownException unknown = new UnknownException("Erro desconhecido: " + error.getMessage());
                    routingContext.response().end(Json.encode(unknown.bean()));
                }
            } else {
                routingContext.response().end(Json.encode(data));
            }
        });
    }
}
