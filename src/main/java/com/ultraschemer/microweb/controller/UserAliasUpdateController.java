package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.domain.bean.Message;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class UserAliasUpdateController extends SimpleController {
    public UserAliasUpdateController() {
        super(500, "19369cad-681b-4da8-b917-81c68907dd0c");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse response) throws StandardException {
        JsonObject params = new JsonObject(routingContext.getBodyAsString());
        String newAlias = params.getString("value");
        User user = routingContext.get("User");
        Message msg = new Message();

        if(newAlias == null) {
            msg.setCode("8284f330-3975-47ed-b938-9d83bf24fcaf");
            msg.setHttpStatus(400);
            msg.setMessage("The field \"value\" is required.");
        } else {
            UserManagement.updateUserAlias(user.getId().toString(), newAlias);
            msg.setCode("bf7903f7-20fb-4c27-9308-a244af35c6f9");
            msg.setHttpStatus(200);
            msg.setMessage("User alias has been succrssfully updated");
        }

        response.setStatusCode(msg.getHttpStatus()).end(Json.encode(msg));
    }
}
