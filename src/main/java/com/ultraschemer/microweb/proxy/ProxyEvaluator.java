package com.ultraschemer.microweb.proxy;

import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.UnsupportedMethodException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import kotlin.Pair;
import okhttp3.*;

import java.util.Map;
import java.util.Objects;

public class ProxyEvaluator {
    public static void evaluate(RoutingContext context, HttpServerResponse response,
                                OkHttpClient client, String serverAddress) throws Throwable
    {
        Request req;
        HttpServerRequest serverRequest = context.request();
        MediaType json = MediaType.parse("application/json; charset=utf-8");
        String serviceAddress = serverAddress + serverRequest.uri();
        User user = context.get("user");
        Request.Builder builder =
                new Request.Builder().url(serviceAddress).addHeader("userId", user.getId().toString());

        // Set all request headers:
        for(Map.Entry<String, String> entry: context.request().headers()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        switch (serverRequest.method()) {
            case GET:
                req = builder.get().build();
                break;
            case POST:
                req = builder.post(RequestBody.create(context.getBodyAsString(), json)).build();
                break;
            case PUT:
                req = builder.put(RequestBody.create(context.getBodyAsString(), json)).build();
                break;
            case PATCH:
                req = builder.patch(RequestBody.create(context.getBodyAsString(), json)).build();
                break;
            case DELETE:
                req = builder.url(serviceAddress).delete().build();
                break;
            default:
                throw new UnsupportedMethodException("Method request not supported by Multiplexer proxy.");
        }

        // Return response - any exception Microweb handles:
        try(Response res = client.newCall(req).execute()) {
            // Set all response headers:
            for(Pair<? extends String, ? extends String> entry: res.headers()) {
                response.putHeader(entry.getFirst(), entry.getSecond());
            }

            // Call response:
            response.setStatusCode(res.code()).end(Objects.requireNonNull(res.body()).string());
        }
    }
}
