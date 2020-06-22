package com.ultraschemer.microweb.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.*;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.util.HashMap;

public class RegisteredReverseProxy {
    private final HashMap<String, String> pathToServer = new HashMap<>();
    private int port = 8080;
    HttpProxyServerBootstrap serverBootstrap;
    HttpProxyServer server;

    /**
     * Create proxy to specific port
     * @param port The port which this proxy will serve
     */
    public RegisteredReverseProxy(int port) {
        this.port = port;
        server = null;
        initialize();
    }

    /**
     * Create proxy. It waits at default port (8080)
     */
    public RegisteredReverseProxy() {
        server = null;
        initialize();
    }

    protected void initialize() {
        serverBootstrap = DefaultHttpProxyServer.bootstrap()
                .withPort(port)
                .withFiltersSource(new HttpFiltersSourceAdapter() {
                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                        return new HttpFiltersAdapter(originalRequest) {
                            @Override
                            public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                                System.out.println(httpObject);
                                return null;
                            }
                        };
                    }
                });
    }
    /**
     * Method used to register paths to be redirected. Any path not registered actively
     * will by deemed not found (HTTP 404 status)
     * @param path The registered path received in this proxy
     * @param serverAddress The server address which the proxy will redirect the call
     */
    public void registerPath(String path, String serverAddress) {
        pathToServer.put(path, serverAddress);
    }

    /**
     * Start proxy server
     */
    public void run() {
        if(server != null) {
            server.stop();
            server = null;
        }

        server = serverBootstrap.start();
    }

    /**
     * Stop proxy server
     */
    public void stop() {
        server.stop();
        server = null;
    }
}
