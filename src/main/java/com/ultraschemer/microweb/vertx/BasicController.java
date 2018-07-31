package com.ultraschemer.microweb.vertx;


import io.vertx.ext.web.Route;

/**
 *
 * This class is used as model to all controller classes. All routes must be explicit in the main application class,
 * with the available methods (GET, POST, PUT, DELETE, etc).
 *
 * This class is fundamentally procedural, because HTTP call treating is, usually, procedural, too.
 *
 */
public interface BasicController {
    /**
     * All controller handlers are registered through this method. All registration techniques available to a route by
     * Vert.x are available here.
     *
     * Usually, to maintain the Controller organized, we shouldn't register more than one controller to the same route,
     * so all linked handler to a single route need to be declared in the evaluate method of a single controller.
     *
     * Controllers can be used to register filters, too.
     *
     * @param route The routing object in which the controller will register handlers.
     */
    void evaluate(Route route);
}
