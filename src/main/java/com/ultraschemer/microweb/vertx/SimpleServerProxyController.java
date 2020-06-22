package com.ultraschemer.microweb.vertx;

import com.ultraschemer.microweb.error.UnsupportedMethodException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import okhttp3.*;

import java.util.Objects;

public abstract class SimpleServerProxyController extends SimpleController {
    private final OkHttpClient client = new OkHttpClient();

    public SimpleServerProxyController(int errorHttpStatusCode, String errorCode) {
        super(errorHttpStatusCode, errorCode);
    }

    protected abstract String getServerAddress() throws Throwable;

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        Request req;
        HttpServerRequest serverRequest = context.request();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String serviceAddress = getServerAddress() + serverRequest.uri();
        Request.Builder builder = new Request.Builder().url(serviceAddress);

        switch (serverRequest.method()) {
            case GET:
                req = builder.get().build();
                break;
            case POST:
                req = builder.post(RequestBody.create(context.getBodyAsString(), JSON)).build();
                break;
            case PUT:
                req = builder.put(RequestBody.create(context.getBodyAsString(), JSON)).build();
                break;
            case DELETE:
                req = builder.delete().build();
                break;
            default:
                throw new UnsupportedMethodException("Method request not supported by Multiplexer proxy.");
        }

        // Return response - any exception Microweb handles:
        try(Response res = client.newCall(req).execute()) {
            response.putHeader("Content-type", res.header("Content-type"));
            response.setStatusCode(res.code()).end(Objects.requireNonNull(res.body()).string());
        }
    }
}

