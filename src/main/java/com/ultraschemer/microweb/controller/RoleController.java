package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.domain.RoleManagement;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class RoleController extends SimpleController {
    public RoleController() {
        super(500, "a0c70296-14d4-4118-9ea7-59840e26c451");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse response) throws StandardException {
        HttpServerRequest request = routingContext.request();
        String roleIdOrName = request.getParam("roleIdOrName");
        response.setStatusCode(200);

        if(roleIdOrName == null) {
            response.end(Json.encode(RoleManagement.loadAllRoles()));
        } else {
            try {
                response.end(Json.encode(RoleManagement.loadRoleById(roleIdOrName)));
            } catch(Exception e) {
                response.end(Json.encode(RoleManagement.loadRoleByName(roleIdOrName)));
            }
        }
    }
}
