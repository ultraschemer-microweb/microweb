package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.domain.bean.UserData;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class UserController extends SimpleController {
    public UserController() {
        super(500, "0cefde6d-6896-4353-bc20-c949744e831");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse response) throws StandardException {
        User user = routingContext.get("user");
        UserData userData = UserManagement.loadUserBySecureId(user.getId().toString());
        response.setStatusCode(200).end(Json.encode(userData));
    }
}
