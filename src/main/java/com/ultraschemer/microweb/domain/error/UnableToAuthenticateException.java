package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToAuthenticateException extends StandardException {
    public UnableToAuthenticateException(String message) {
        super("ec6cd5ce-af34-4d8d-a12c-b74c982856da", 500, message);
    }
}
