package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.controller.bean.CreateUserData;
import com.ultraschemer.microweb.controller.bean.NewUserData;
import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.domain.bean.UserData;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class UserCreationController extends SimpleController {
    public UserCreationController() {
        super(500, "09cdaa86-28f3-4fc0-9fe6-8f11dee63af0");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse response) throws Throwable {
        NewUserData newUserData = Json.decodeValue(routingContext.getBodyAsString(), NewUserData.class);
        CreateUserData userData = newUserData.getUserData();
        String role = newUserData.getRoles().get(0);

        // Register the user, with the first given role:
        UserManagement.registerSimpleUser(userData, role);

        // Assign the other given roles to the user:
        UserData newUser = UserManagement.loadUser(userData.getName());
        newUserData.getRoles().remove(role);

        for(String roleName: newUserData.getRoles()) {
            UserManagement.setRoleToUser(newUser.getId(), roleName);
        }

        response.setStatusCode(200);
        response.end(Json.encode(UserManagement.loadUser(userData.getName())));
    }
}
