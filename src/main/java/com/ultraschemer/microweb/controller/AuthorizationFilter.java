package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.domain.AuthManagement;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.error.UnknownException;
import com.ultraschemer.microweb.vertx.AsyncExecutor;
import com.ultraschemer.microweb.vertx.BasicController;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Override
    public void evaluate(Route route) {
        route.handler(BodyHandler.create());
        route.blockingHandler(routingContext -> {
            // Primeiro, elimina todas as rotas que não são filtradas por autorização:
            String path = routingContext.request().uri();

            if (isPathUnfiltered(path)) {
                routingContext.next();
            } else {
                // Verifica a token de autorização:
                String authorization = routingContext.request().getHeader("Authorization");
                String token = null;

                String regex = "^\\s*[Bb][Ee][Aa][Rr][Ee][Rr]\\s+(.+)$";
                if (authorization != null && authorization.matches(regex)) {
                    Pattern p = Pattern.compile(regex);
                    Matcher m = p.matcher(authorization);
                    if (m.find()) {
                        token = m.group(1);
                    }
                }

                if (token == null) {
                    token = routingContext.request().getHeader("Microweb-Access-Token");
                }

                finishAuthorization(routingContext, token);
            }
        });
    }
}