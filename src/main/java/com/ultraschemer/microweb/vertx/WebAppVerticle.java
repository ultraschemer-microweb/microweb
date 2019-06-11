package com.ultraschemer.microweb.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import com.ultraschemer.microweb.domain.ServiceConfiguration;

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
    private Router getRouter() {
        return router;
    }

    /**
     * Http port Getter.
     * @return The Http listening port number.
     */
    private int getHttpPort() {
        return httpPort;
    }

    /**
     * Http port Setter.
     * @param httpPort The port which Http server will listen.
     */
    private void setHttpPort(int httpPort) {
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

    @Override
    public void start() {
        vertx.executeBlocking(future -> {
            try {
                this.setHttpPort(ServiceConfiguration.getHttpServicePort());
                // Initialize all routes and configurations:
                this.initialization();
                // Return the configuration success:
                future.complete();
            } catch(Exception e) {
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
                System.out.println("Configuration error: " + res.cause().getMessage());
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
