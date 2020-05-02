package com.ultraschemer.microweb.vertx;

import com.google.common.base.Throwables;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import com.ultraschemer.microweb.domain.ServiceConfiguration;
import io.vertx.ext.web.handler.CorsHandler;
import net.bytebuddy.implementation.bytecode.Throw;

import java.util.Set;

/**
 *
 * This is a personalized implementation of AbstractVerticle, to simplify the controllers and filters creation.
 *
 * These controllers and filters are just routing handlers of Vert.x HTTP server. However, this additional abstraction
 * layer brings meaning to these handlers.
 *
 */
public abstract class WebAppVerticle extends AbstractVerticle {
    /**
     * This is the routing object, used in all router registration in this specialized Verticle.
     */
    private Router router;

    /**
     * This is the HTTP Por to be configured to serve.
     */
    private int httpPort;

    /**
     * Safe access to this Verticle Router.
     * @return A router object to be used by the caller.
     */
    protected Router getRouter() {
        return router;
    }

    protected void setCors(String path, Set<HttpMethod> methods, Set<String> headerNames) {
        getRouter().route().handler(CorsHandler.create(path).allowedMethods(methods).allowedHeaders(headerNames));
    }

    protected void setCors(String path, HttpMethod method, String headerName) {
        getRouter().route().handler(CorsHandler.create(path).allowedMethod(method).allowedHeader(headerName));
    }

    protected void setCors(String path, HttpMethod method) {
        getRouter().route().handler(CorsHandler.create(path).allowedMethod(method));
    }

    protected void setCors(String path, String headerName) {
        getRouter().route().handler(CorsHandler.create(path).allowedHeader(headerName));
    }

    protected void setCorsMethods(String path, Set<HttpMethod> methods) {
        getRouter().route().handler(CorsHandler.create(path).allowedMethods(methods));
    }

    protected void setCorsHeaderNames(String path, Set<String> headerNames) {
        getRouter().route().handler(CorsHandler.create(path).allowedHeaders(headerNames));
    }

    /**
     * A more complete method to enable CORS
     *
     * @param allowedOriginPattern allowed origin
     * @param allowCredentials     allow credentials (true/false)
     * @param maxAge               in seconds
     * @param allowedHeaders       set of allowed headers
     * @param methods              list of methods ... if empty all methods are allowed  @return self
     */
    public void enableCors(String allowedOriginPattern,
                           boolean allowCredentials,
                           int maxAge,
                           Set<String> allowedHeaders,
                           HttpMethod... methods) {

        CorsHandler corsHandler = CorsHandler.create(allowedOriginPattern)
                .allowCredentials(allowCredentials)
                .maxAgeSeconds(maxAge);

        if (methods == null || methods.length == 0) { // if not given than all
            methods = HttpMethod.values();
        }

        for (HttpMethod method : methods) {
            corsHandler.allowedMethod(method);
        }

        corsHandler.allowedHeaders(allowedHeaders);

        getRouter().route().handler(corsHandler);
    }

    /**
     * Http port Getter.
     * @return The Http listening port number.
     */
    public int getHttpPort() {
        return httpPort;
    }

    /**
     * Http port Setter.
     * @param httpPort The port which Http server will listen.
     */
    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        // Initialize the router object to be used in controller registration:
        router = Router.router(vertx);

        // Initialize a default Http Port:
        setHttpPort(8000);
    }

    /**
     * This method is a helper to controller registration on routes.
     *
     * All route is defined by an HTTP verb, the method, and by a path or URI.
     *
     * @param method The HTTP verb on which the Controller will be set.
     * @param path The path or resource linked to the route.
     * @param basicController The controller receiving the route.
     */
    protected void registerController(HttpMethod method, String path, BasicController basicController) {
        if(basicController instanceof SimpleController) {
            SimpleController simpleController = (SimpleController) basicController;
            simpleController.setMethod(method);
            simpleController.setPath(path);
        }

        basicController.evaluate(getRouter().route(method, path));
    }

    /**
     * This method is a helper to controller registration as filters.
     *
     * Filters are called on ALL routes, and can be initialization filters, when registered before router registration,
     * or finalization filters, when registered after routes.
     *
     * It's possible to register filters between routes, however, no use case for this kind of registration has been
     * found.
     *
     * @param basicController The filter controller.
     */
    protected void registerFilter(BasicController basicController) {
        basicController.evaluate(getRouter().route());
    }

    /**
     * Http server port initialization. It can be customized.
     * @return The service port
     */
    protected int getInitialHttpPort() throws Throwable {
        return ServiceConfiguration.getHttpServicePort();
    }

    @Override
    public void start() {
        vertx.executeBlocking(future -> {
            try {
                this.setHttpPort(getInitialHttpPort());
                // Initialize all routes and configurations:
                this.initialization();
                // Return the configuration success:
                future.complete();
            } catch(Throwable e) {
                future.fail(e);
            }
        }, res -> {
            if (res.succeeded()) {
                HttpServer server = vertx.createHttpServer();

                // Registra o handler genérico sobre todos as chamadas de WebSockets:
                server.websocketHandler(this::webSocketInitialization);

                // Registras as chamadas padrão sobre as rotas HTTP:
                server.requestHandler(getRouter()).listen(getHttpPort());

                System.out.println("HTTP Server started on port " + getHttpPort());
            } else {
                System.out.println("Configuration error: " + res.cause().getMessage() +
                        "\nStack Trace: " + Throwables.getStackTraceAsString(res.cause()));
            }
        });
    }

    /**
     * Method to be implemented by this abstract class realizations, and it must call all routes and filters
     * registrations.
     */
    public abstract void initialization() throws Exception;

    /**
     * Method that can be used to implement a WebSocket hangler:
     */
    public void webSocketInitialization(ServerWebSocket webSocket) { /* Empty: for override */  }
}
