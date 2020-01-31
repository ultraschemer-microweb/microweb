package com.ultraschemer.microweb.controller;

import com.ultraschemer.microweb.domain.CentralUserRepositoryManagement;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.vertx.SimpleController;
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
    protected void beforeEvaluation(RoutingContext context) throws StandardException {
        context.put("user", CentralUserRepositoryManagement.evaluateResourcePermission(this.getMethod().toString(),
                this.getPath(), context.request().getHeader("Authorization")));
    }
}
