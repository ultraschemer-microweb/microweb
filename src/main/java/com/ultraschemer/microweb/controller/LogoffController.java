package com.ultraschemer.microweb.controller;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.handler.BodyHandler;
import com.ultraschemer.microweb.domain.AuthManagement;
import com.ultraschemer.microweb.domain.bean.Message;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.vertx.BasicController;

public class LogoffController implements BasicController {

    @Override
    public void evaluate(Route route) {
        route.handler(BodyHandler.create()).blockingHandler(routingContext -> {
            String token = routingContext.request().getHeader("Altec-Access-Token");

            try {
                AuthManagement.unauthorize(token);
            } catch(Exception e) {
                routingContext.put("error", e);
            }

            routingContext.next();
        }).handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            Exception error = routingContext.get("error");

            response.putHeader("Content-Type", "application/json; charset=utf-8");

            if(error == null) {
                // TODO: Atribuir códigos de mensagem para o sistema - e segui-los. Obs.: Definir em especificação.
                Message msg = new Message();
                msg.setMessage("Usuário saiu do sistema com sucesso.");
                response.setStatusCode(msg.getHttpStatus()).end(Json.encode(msg));
            } else {
                StandardException err = (StandardException) error;
                response.setStatusCode(err.getHttpStatus()).end(Json.encode(err.bean()));
            }
        });
    }
}
