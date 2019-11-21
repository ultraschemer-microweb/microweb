package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToCreateUserException extends StandardException {
    public UnableToCreateUserException(String message) {
        super("c05d8ac0-526b-4596-9636-b292098dc5c9", 500, message);
    }

    public UnableToCreateUserException(String message, Throwable cause) {
        super("c05d8ac0-526b-4596-9636-b292098dc5c9", 500, message, cause);
    }
}
