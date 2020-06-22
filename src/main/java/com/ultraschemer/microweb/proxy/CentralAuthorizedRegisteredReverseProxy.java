package com.ultraschemer.microweb.proxy;

import com.ultraschemer.microweb.error.StandardException;

public class CentralAuthorizedRegisteredReverseProxy extends RegisteredReverseProxy {
    public CentralAuthorizedRegisteredReverseProxy() {
        super();
    }

    public CentralAuthorizedRegisteredReverseProxy(int port) {
        super(port);
    }

    @Override
    protected void evaluateUriPermission(String method, String path, String authorization) throws StandardException {
       // TODO: continue from here
    }
}
