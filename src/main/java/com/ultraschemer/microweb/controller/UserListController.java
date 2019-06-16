package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.domain.bean.Message;
import com.ultraschemer.microweb.domain.bean.UserData;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public class UserListController extends SimpleController {
    public UserListController() {
        super(500, "eadaa124-1e2f-437e-a8e8-3b77e25e4377");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse response) throws Throwable {
        HttpServerRequest request = routingContext.request();
        String userIdOrName = request.getParam("userIdOrName");

        if(userIdOrName == null) {
            String count = request.getParam("count");
            String offset = request.getParam("offset");

            if(offset == null || count == null) {
                Message message = new Message();
                message.setCode("e47fc9cd-033b-4b57-9c8b-f4eb0a9017c2");
                message.setHttpStatus(400);
                response.setStatusCode(message.getHttpStatus()).end(Json.encode(message));
            }

            assert count != null;
            assert offset != null;

            List<UserData> users = UserManagement.loadUsers(Integer.parseInt(count), Integer.parseInt(offset));
            response.setStatusCode(200).end(Json.encode(users));
        } else {
            UserData user = UserManagement.loadUser(userIdOrName);
            response.setStatusCode(200).end(Json.encode(user));
        }
    }
}
