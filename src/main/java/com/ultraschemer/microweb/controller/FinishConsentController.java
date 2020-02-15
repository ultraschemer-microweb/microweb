package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.domain.bean.Oauth2AuthorizationConsentedObject;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import com.ultraschemer.microweb.domain.CentralUserRepositoryManagement;

public class FinishConsentController extends SimpleController {
    public FinishConsentController() {
        super(500, "9c549d31-0143-4268-966a-94e510c59baa");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse response) throws Throwable {
        Oauth2AuthorizationConsentedObject foreignConsent = new Oauth2AuthorizationConsentedObject();
        HttpServerRequest request = routingContext.request();
        foreignConsent.setState(request.getParam("state"));
        foreignConsent.setSessionState(request.getParam("session_state"));
        foreignConsent.setCode(request.getParam("code"));
        foreignConsent.setRedirectUri(request.getParam("redirect_uri"));
        foreignConsent.setClientSecret(request.getParam("client_secret"));
        foreignConsent.setClientId(request.getParam("client_id"));
        JsonObject accessTokenData = CentralUserRepositoryManagement.finishAuthorizationConsent(foreignConsent);
        response.setStatusCode(200).end(accessTokenData.toString());
    }
}
