package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.controller.bean.PasswordModificationData;
import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.domain.bean.Message;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.validation.Validator;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class UserPasswordUpdateController extends SimpleController {
    public UserPasswordUpdateController() {
        super(500, "e1593bc3-c820-42d7-b6e3-0a0079317b61");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse response) throws Exception {
        // Get the user:
        User user = routingContext.get("user");

        // Get the password data:
        PasswordModificationData passwordData =
                Json.decodeValue(routingContext.getBodyAsString(), PasswordModificationData.class);

        // Validate the inputs:
        Validator.ensure(passwordData);

        // Call the business rule which change the user password:
        UserManagement.updateUserPassword(user.getId().toString(), user.getName(),
                passwordData.getCurrentPassword(), passwordData.getNewPassword(),
                passwordData.getNewPasswordConfirmation());

        // Finish controller, returning message to caller:
        Message msg = new Message();
        msg.setCode("");
        msg.setHttpStatus(200);
        msg.setMessage("User password has been successfully updated.");

        response.setStatusCode(msg.getHttpStatus()).end(Json.encode(msg));

    }
}
