package com.ultraschemer.microweb.error;

public class ValidationException extends StandardException {
    public ValidationException(String message, int httpStatus) {
        super("9e5999d1-8daf-4b94-a426-cd5894d8b808", httpStatus, message);
    }
}