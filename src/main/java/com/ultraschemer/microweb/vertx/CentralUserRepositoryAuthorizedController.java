package com.ultraschemer.microweb.vertx;

import com.ultraschemer.microweb.domain.CentralUserRepositoryManagement;
import com.ultraschemer.microweb.error.StandardException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public abstract class CentralUserRepositoryAuthorizedController extends SimpleController {
    public CentralUserRepositoryAuthorizedController(int errorHttpStatusCode, String errorCode) {
        super(errorHttpStatusCode, errorCode);
    }

    public CentralUserRepositoryAuthorizedController(String contentType, int errorHttpStatusCode, String errorCode) {
        super(contentType, errorHttpStatusCode, errorCode);
    }

    /**
     * Method used to implement "before actions" on controller
     *
     * @param context The HTTP Call context, providing information to initialization.
     */
    @Override
    public void beforeEvaluation(RoutingContext context) throws StandardException {
        HttpServerRequest request = context.request();
        String token = request.getHeader("Authorization");

        if(token == null) {
            token = context.request().getHeader("Microweb-Access-Token");

            if (token == null) {
                try {
                    token = "Bearer " + context.getCookie("Microweb-Access-Token").getValue();
                } catch (Exception e) { /* Ignore */ }
            }

            if (token == null) {
                token = context.request().getParam("Microweb-Access-Token");
            }

            if (token == null) {
                try {
                    if (context.request().getHeader("Content-type").toLowerCase().trim().startsWith("multipart/form-data")) {
                        context.request().setExpectMultipart(true);
                    }

                    token = context.request().getFormAttribute("Microweb-Access-Token");

                } catch (Exception e) { /* Ignore */ }
            }

            if (token == null) {
                try {
                    JsonObject body = context.getBodyAsJson();
                    token = body.getString("Microweb-Access-Token");
                } catch (Exception e) { /* Ignore */ }
            }

            if(token != null) {
                token = "Bearer " + token;
            }
        }

        context.put("user", CentralUserRepositoryManagement.evaluateResourcePermission(this.getMethod().toString(),
                this.getPath(), token));
    }
}

