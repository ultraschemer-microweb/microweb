package com.ultraschemer.microweb.vertx;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import okhttp3.OkHttpClient;

public abstract class CentralAuthorizedServerProxyController extends CentralUserRepositoryAuthorizedController {
    private final OkHttpClient client = new OkHttpClient();

    public CentralAuthorizedServerProxyController(int errorHttpStatusCode, String errorCode) {
        super(errorHttpStatusCode, errorCode);
    }

    protected abstract String getServerAddress() throws Throwable;

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        ProxyEvaluator.evaluate(context, response, client, getServerAddress());
    }
}

