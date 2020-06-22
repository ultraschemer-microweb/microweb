package com.ultraschemer.microweb.proxy;

import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.UnsupportedMethodException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import okhttp3.*;

import java.util.Objects;

public class ProxyEvaluator {
    public static void evaluate(RoutingContext context, HttpServerResponse response,
                                OkHttpClient client, String serverAddress) throws Throwable
    {
        Request req;
        HttpServerRequest serverRequest = context.request();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String serviceAddress = serverAddress + serverRequest.uri();
        User user = context.get("user");
        Request.Builder builder =
                new Request.Builder().url(serviceAddress).addHeader("userId", user.getId().toString());
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
                req = builder.url(serviceAddress).delete().build();
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
