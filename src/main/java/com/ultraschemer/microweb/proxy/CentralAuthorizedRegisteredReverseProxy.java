package com.ultraschemer.microweb.proxy;

import com.ultraschemer.microweb.domain.CentralUserRepositoryManagement;
import com.ultraschemer.microweb.error.StandardException;
import io.netty.handler.codec.http.DefaultHttpRequest;

public class CentralAuthorizedRegisteredReverseProxy extends RegisteredReverseProxy {
    public CentralAuthorizedRegisteredReverseProxy() {
        super();
    }

    public CentralAuthorizedRegisteredReverseProxy(int port) {
        super(port);
    }

    @Override
    protected void evaluateRequestPermission(DefaultHttpRequest request) throws StandardException {
       request.headers().add("user", CentralUserRepositoryManagement.evaluateResourcePermission(request.getMethod().toString(),
               request.getUri(), request.headers().get("Authorization")));
    }
}
