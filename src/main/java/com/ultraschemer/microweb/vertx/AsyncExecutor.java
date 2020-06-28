package com.ultraschemer.microweb.vertx;

@FunctionalInterface
public interface AsyncExecutor {
    void execute() throws Exception;
}
