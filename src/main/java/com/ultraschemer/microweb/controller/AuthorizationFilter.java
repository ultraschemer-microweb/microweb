package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.domain.AuthManagement;
import com.ultraschemer.microweb.domain.JwtSecurityManager;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.error.UnknownException;
import com.ultraschemer.microweb.vertx.AsyncExecutor;
import com.ultraschemer.microweb.vertx.BasicController;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashSet;

public class AuthorizationFilter implements BasicController {
    private HashSet<String> unfilteredPaths;
    private Vertx vertx;

    public AuthorizationFilter(Vertx vertx) {
        unfilteredPaths = new HashSet<>();

        // Adiciona todas as rotas que não são filtradas por autorização:
        unfilteredPaths.add("/v0/login");

        this.vertx = vertx;
    }

    private boolean isPathUnfiltered(String path) {
        for(String unfiltered: unfilteredPaths) {
            if(unfiltered.equals(path)) {
                return true;
            }
        }

        return false;
    }

    protected void addUnfilteredPath(String path) {
        unfilteredPaths.add(path);
    }

    private void executeHandler(RoutingContext routingContext, AsyncExecutor consumer) {
        try {
            consumer.execute();
        } catch (StandardException se) {
            routingContext.response()
                    .putHeader("Content-Type", "application/json; encoding=utf-8")
                    .setStatusCode(se.getHttpStatus())
                    .end(Json.encode(se.bean()));
        } catch (Exception e) {
            UnknownException ue = new UnknownException("Erro desconhecido: " + e.getMessage());
            routingContext.response()
                    .putHeader("Content-Type", "application/json; encoding=utf-8")
                    .setStatusCode(ue.getHttpStatus())
                    .end(Json.encode(ue.bean()));
        }
    }

    private void finishAuthorization(final RoutingContext routingContext, final String token) {
        this.executeHandler(routingContext, () -> {
            User u = AuthManagement.authorize(token);
            routingContext.put("user", u);
            routingContext.next();
        });
    }

    private void finishBearerAuthorization(final RoutingContext routingContext, final String bearer) {
        this.executeHandler(routingContext, () ->
                JwtSecurityManager.authenticateBearer(this.vertx, bearer, user ->
                        this.executeHandler(routingContext, () -> {
                            JsonObject jwtToken = user.principal();
                            User u = AuthManagement.authorize(jwtToken.getString("sub"));
                            routingContext.put("user", u);
                            routingContext.put("jwtToken", jwtToken);
                            routingContext.next();
                        })));
    }

    @Override
    public void evaluate(Route route) {
        route.handler(BodyHandler.create()).blockingHandler(routingContext -> {
            // Primeiro, elimina todas as rotas que não são filtradas por autorização:
            String path = routingContext.request().uri();

            if(isPathUnfiltered(path)) {
                routingContext.next();
            } else {
                // Verify bearer:
                HttpServerRequest request = routingContext.request();
                String strBearer = request.getHeader("Authorization");
                if(strBearer != null) {
                    String[] fullBearer = strBearer.replaceAll("\\s{2,}", " ").split("\\s");
                    if(fullBearer.length > 1) {
                        // Verifies the given bearer:
                        if(fullBearer[0].toLowerCase().equals("bearer")) {
                            finishBearerAuthorization(routingContext, fullBearer[1]);
                        } else {
                            // Bearer inválido: Verifica a token de autorização:
                            String token = request.getHeader("Microweb-Access-Token");
                        }

                    } else {
                        // Verifica a token de autorização:
                        String token = request.getHeader("Microweb-Access-Token");
                        finishAuthorization(routingContext, token);
                    }
                } else {
                    // Verifica a token de autorização:
                    String token = request.getHeader("Microweb-Access-Token");
                    finishAuthorization(routingContext, token);
                }

            }
        });
    }
}