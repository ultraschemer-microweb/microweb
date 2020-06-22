package com.ultraschemer.microweb.proxy;

import com.google.common.base.Throwables;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.error.StandardRuntimeException;
import com.ultraschemer.microweb.error.UnableToFindMappedServerForUriException;
import com.ultraschemer.microweb.utils.Resource;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.vertx.core.json.Json;
import org.littleshoot.proxy.*;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;

public class RegisteredReverseProxy {
    private final HashMap<String, String> pathToServer = new HashMap<>();
    private final LinkedList<String> pathRegistration = new LinkedList<>();
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

    protected String findUriMappedServer(String uri) throws StandardException {
        for(String path: pathRegistration) {
            if(Resource.pathAreEquivalent(path, uri)) {
                return pathToServer.get(path);
            }
        }

        throw new UnableToFindMappedServerForUriException("Unable to find mapped resource to be redirected by reverse proxy.");
    }

    protected void evaluateUriPermission(String method, String path, String authorization) throws StandardException {
    }

    protected void initialize() {
        serverBootstrap = DefaultHttpProxyServer.bootstrap()
                .withPort(port)
                .withFiltersSource(new HttpFiltersSourceAdapter() {
                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                        return new HttpFiltersAdapter(originalRequest) {
                            @Override
                            public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                                ByteBuf buffer = null;
                                if(httpObject instanceof DefaultHttpRequest) {
                                    DefaultHttpRequest request = (DefaultHttpRequest) httpObject;
                                    // Connect method is not supported, because it's used to proxy to HTTPS servers, which
                                    // contents are encrypted, and, so, inaccessible to this proxy evaluate permissions.
                                    if(request.getMethod().equals(HttpMethod.CONNECT)) {
                                        try {
                                            buffer = Unpooled.wrappedBuffer(("Unsupported connection type. SSL is " +
                                                    "not allowed in this endpoint").getBytes(StandardCharsets.UTF_8));
                                        } catch(Exception ignored) { }
                                    } else {
                                        String path = request.getUri();
                                        if(path.charAt(0) == '/') {
                                            try {
                                                evaluateUriPermission(request.getMethod().toString(), path, request.headers().get("Authorization"));
                                                String host = findUriMappedServer(request.getUri());
                                                request.setUri(host + request.getUri());
                                                request.headers().remove("Host");
                                                request.headers().add("Host", host);
                                            } catch(StandardException|StandardRuntimeException se) {
                                                buffer = Unpooled.wrappedBuffer(Json.encode(se.bean()).getBytes(StandardCharsets.UTF_8));
                                            } catch(Throwable t) {
                                                buffer = Unpooled.wrappedBuffer(("Unknown error: " + t.getMessage() +
                                                        "\n\n" + Throwables.getStackTraceAsString(t))
                                                        .getBytes(StandardCharsets.UTF_8));
                                            }
                                        } else {
                                            buffer = Unpooled.wrappedBuffer(("No external redirection is supported, " +
                                                    "only direct calls to registered URIs. Aborting.")
                                                    .getBytes(StandardCharsets.UTF_8));
                                        }
                                    }
                                }

                                if(buffer != null) {
                                    HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN, buffer);
                                    HttpHeaders.setContentLength(response, buffer.readableBytes());
                                    HttpHeaders.setHeader(response, HttpHeaders.Names.CONTENT_TYPE, "text/html");
                                    return response;
                                }

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
        pathRegistration.add(path);
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
