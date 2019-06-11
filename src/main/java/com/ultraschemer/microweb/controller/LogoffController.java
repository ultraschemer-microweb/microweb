package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.domain.AuthManagement;
import com.ultraschemer.microweb.domain.bean.Message;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class LogoffController extends SimpleController {
    public LogoffController() {
        super(500, "1467dee6-8f1a-4e79-80b7-0559b4bbccae");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse response) throws StandardException {
        JsonObject jwtToken = routingContext.get("jwtToken");

        if(jwtToken == null) {
            AuthManagement.unauthorize(routingContext.request().getHeader("Microweb-Access-Token"));
        } else {
            AuthManagement.unauthorize(jwtToken.getString("sub"));
        }

        Message msg = new Message();
        msg.setHttpStatus(200);
        msg.setCode("154840d6-edd4-4636-a2fb-a2c34080abd3");
        msg.setMessage("User logoff has been successful.");
        response.setStatusCode(msg.getHttpStatus()).end(Json.encode(msg));
    }
}