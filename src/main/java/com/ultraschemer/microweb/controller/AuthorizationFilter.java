package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.domain.AuthManagement;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.error.StandardRuntimeException;
import com.ultraschemer.microweb.error.UnknownException;
import com.ultraschemer.microweb.error.ValidationException;
import com.ultraschemer.microweb.vertx.AsyncExecutor;
import com.ultraschemer.microweb.vertx.BasicController;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthorizationFilter implements BasicController {
    private final HashSet<String> unfilteredPaths;

    public AuthorizationFilter() {
        unfilteredPaths = new HashSet<>();

        // Adiciona todas as rotas que não são filtradas por autorização:
        unfilteredPaths.add("/v0/login");
    }

    protected boolean isPathUnfiltered(String path) {
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

    protected void executeHandler(RoutingContext routingContext, AsyncExecutor consumer) {
        try {
            consumer.execute();
        } catch (StandardException | StandardRuntimeException se) {
            routingContext.response()
                    .putHeader("Content-Type", "application/json; encoding=utf-8")
                    .setStatusCode(se.getHttpStatus())
                    .end(Json.encode(se.bean()));
        } catch (Throwable e) {
            UnknownException ue = new UnknownException("Unknown error: " + e.getMessage());
            routingContext.response()
                    .putHeader("Content-Type", "application/json; encoding=utf-8")
                    .setStatusCode(ue.getHttpStatus())
                    .end(Json.encode(ue.bean()));
        }
    }

    protected void finishAuthorization(final RoutingContext routingContext, final String token) {
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
            String contentType = routingContext.request().getHeader("Content-type");
            if(contentType == null) {
                // This ia an exception prone situation - since every Microweb call MUST HAVE CONTENT-TYPE HEADER.
                // Force multipart to get a possible Content-type header!
                routingContext.request().setExpectMultipart(true);
            }

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

                if (token == null) {
                    try {
                        token = routingContext.getCookie("Microweb-Access-Token").getValue();
                    } catch(Exception e) { /* Ignore */ }
                }

                if(token == null) {
                    token = routingContext.request().getParam("Microweb-Access-Token");
                }

                if(token == null) {
                    try {
                        token = routingContext.request().getFormAttribute("Microweb-Access-Token");
                    } catch (Exception e) { /* Ignore */ }
                }

                if(token == null) {
                    try {
                        JsonObject body = routingContext.getBodyAsJson();
                        token = body.getString("Microweb-Access-Token");
                    } catch(Exception e) { /* Ignore */ }
                }

                finishAuthorization(routingContext, token);
            }
        });
    }
}