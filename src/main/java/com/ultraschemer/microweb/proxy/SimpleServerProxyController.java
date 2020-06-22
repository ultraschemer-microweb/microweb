package com.ultraschemer.microweb.proxy;

import com.ultraschemer.microweb.vertx.ProxyEvaluator;
import com.ultraschemer.microweb.vertx.SimpleController;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import okhttp3.OkHttpClient;

public abstract class SimpleServerProxyController extends SimpleController {
    private final OkHttpClient client = new OkHttpClient();

    public SimpleServerProxyController(int errorHttpStatusCode, String errorCode) {
        super(errorHttpStatusCode, errorCode);
    }

    protected abstract String getServerAddress() throws Throwable;

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        ProxyEvaluator.evaluate(context, response, client, getServerAddress());
    }
}

