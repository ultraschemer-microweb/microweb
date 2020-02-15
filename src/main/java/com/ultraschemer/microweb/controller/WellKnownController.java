package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import com.ultraschemer.microweb.domain.CentralUserRepositoryManagement;

public class WellKnownController extends SimpleController {
    public WellKnownController() {
        super(500, "9b77591b-81b7-4cf3-afac-040e4a422b1c");
    }

    @Override
    public void executeEvaluation(RoutingContext routingContext, HttpServerResponse response) throws Throwable {
        response.setStatusCode(200).end(CentralUserRepositoryManagement.wellKnown().toString());
    }
}