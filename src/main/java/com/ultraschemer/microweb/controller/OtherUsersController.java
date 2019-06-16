package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.domain.bean.UserData;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class OtherUsersController extends SimpleController {
    public OtherUsersController() {
        super(500, "fceb6bbd-2aa8-4e50-89b6-778c5ccae735");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse response) throws StandardException {
        String userIdOrName = routingContext.request().getParam("userIdOrName");
        UserData userData;

        try {
            userData = UserManagement.loadUserBySecureId(userIdOrName);
        } catch(Exception e) {
            userData = UserManagement.loadUserByName(userIdOrName);
        }

        response.setStatusCode(200).end(Json.encode(userData));
    }
}
