package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.domain.AuthManagement;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.error.UnknownException;
import com.ultraschemer.microweb.vertx.BasicController;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashSet;

public class AuthorizationFilter implements BasicController {
    private HashSet<String> unfilteredPaths;

    public AuthorizationFilter() {
        unfilteredPaths = new HashSet<>();

        // Adiciona todas as rotas que não são filtradas por autorização:
        unfilteredPaths.add("/v0/login");
    }

    private boolean isPathUnfiltered(String path) {
        for(String unfiltered: unfilteredPaths) {
            if(unfiltered.equals(path)) {
                return true;
            }
        }

        return false;
    }

    private void addUnfilteredPath(String path) {
        unfilteredPaths.add(path);
    }

    @Override
    public void evaluate(Route route) {
        route.handler(BodyHandler.create()).blockingHandler(routingContext -> {
            // Primeiro, elimina todas as rotas que não são filtradas por autorização:
            String path = routingContext.request().uri();

            if(isPathUnfiltered(path)) {
                routingContext.next();
            } else {
                // Verifica a token de autorização:
                String token = routingContext.request().getHeader("Microweb-Access-Token");

                // Localiza o usuário a partir da access token:
                try {
                    User u = AuthManagement.authorize(token);
                    routingContext.put("user", u);
                    routingContext.next();
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
        });
    }
}
