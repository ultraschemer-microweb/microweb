package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.domain.AuthManagement;
import com.ultraschemer.microweb.domain.bean.Message;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogoffController extends SimpleController {
    public LogoffController() {
        super(500, "1467dee6-8f1a-4e79-80b7-0559b4bbccae");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse response) throws StandardException {
        String contentType = routingContext.request().getHeader("Content-type");
        if(contentType == null) {
            // This ia an exception prone situation - force Multipart:
            try {
                routingContext.request().setExpectMultipart(true);
            } catch(Throwable t) { /* Ignore */ }
        }

        String authorization = routingContext.request().getHeader("Authorization");
        String token = null;

        String regex = "^\\s*[Bb][Ee][Aa][Rr][Ee][Rr]\\s+(.+)$";
        if(authorization != null && authorization.matches(regex)) {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(authorization);
            if(m.find()) {
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

        AuthManagement.unauthorize(token);

        Message msg = new Message();
        msg.setHttpStatus(200);
        msg.setCode("154840d6-edd4-4636-a2fb-a2c34080abd3");
        msg.setMessage("User logoff has been successful.");
        response.setStatusCode(msg.getHttpStatus()).end(Json.encode(msg));
    }
}