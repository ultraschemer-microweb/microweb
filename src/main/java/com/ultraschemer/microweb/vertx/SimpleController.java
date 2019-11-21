package com.ultraschemer.microweb.vertx;

import com.google.common.base.Throwables;
import com.ultraschemer.microweb.domain.bean.Message;
import com.ultraschemer.microweb.error.StandardException;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public abstract class SimpleController implements BasicController {
    private String contentType;
    private int httpStatusCode;
    private String errorCode;
    private HttpMethod method;
    private String path;

    public SimpleController (int errorHttpStatusCode, String errorCode) {
        contentType = "application/json; charset=utf-8";
        this.httpStatusCode = errorHttpStatusCode;
        this.errorCode = errorCode;
    }

    public SimpleController(String contentType, int errorHttpStatusCode, String errorCode) {
        this.contentType = contentType;
        this.httpStatusCode = errorHttpStatusCode;
        this.errorCode = errorCode;
    }

    /**
     * Method used to implement "before actions" on controller
     * @param context The HTTP Call context, providing information to initialization.
     */
    protected void beforeEvaluation(RoutingContext context) throws Throwable {
        // To be overridden
    }

    /**
     * Method used to implement "after actions" on controller
     * @param context The Http call context, providing information to finalization.
     */
    protected void afterEvaluation(RoutingContext context) throws Throwable {
        // To be overridden
    }

    @Override
    public void evaluate(Route route) {
        route.handler(BodyHandler.create()).blockingHandler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("Content-Type", contentType);

            try {
                beforeEvaluation(routingContext);
                executeEvaluation(routingContext, response);
                afterEvaluation(routingContext);
            } catch(StandardException se) {
                // Ensure the correct content-type in the case of error:
                response.putHeader("Content-Type", contentType);
                // Finish the response:
                response.setStatusCode(se.getHttpStatus()).end(Json.encode(se.bean()));
            } catch(Throwable t) {
                Message msg = new Message(this.errorCode, this.httpStatusCode,
                        "Unknown error: " + t.getMessage(),
                        Throwables.getStackTraceAsString(t));
                // Ensure the correct content-type in the case of error:
                response.putHeader("Content-Type", contentType);
                // Finish the response:
                response.setStatusCode(msg.getHttpStatus()).end(Json.encode(msg));
            }
        });
    }

    public abstract void executeEvaluation(RoutingContext routingContext, HttpServerResponse response)
            throws Throwable;

    protected String getContentType() {
        return contentType;
    }

    protected void setContentType(String contentType) {
        this.contentType = contentType;
    }

    protected void asyncEvaluation(int errorHttpStatusCode, String errorCode,
                                   RoutingContext routingContext, AsyncExecutor executor)
    {
        HttpServerResponse response = routingContext.response();

        try {
            executor.execute();
        } catch(StandardException se) {
            // Ensure the correct content-type in the case of error:
            response.putHeader("Content-Type", "application/json; charset=utf-8");
            // Finish the response:
            response.setStatusCode(se.getHttpStatus()).end(Json.encode(se.bean()));
        } catch(Throwable t) {
            Message msg = new Message(errorCode, errorHttpStatusCode,
                    "Unknown error: " + t.getMessage(),
                    Throwables.getStackTraceAsString(t));
            // Ensure the correct content-type in the case of error:
            response.putHeader("Content-Type", "application/json; charset=utf-8");
            // Finish the response:
            response.setStatusCode(msg.getHttpStatus()).end(Json.encode(msg));
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}


