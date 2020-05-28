package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToUpdateUserException extends StandardException {
    public UnableToUpdateUserException(String message) {
        super("68009862-090e-4a6d-b9f6-60b6c4eed369", 500, message);
    }

    public UnableToUpdateUserException(String message, Throwable cause) {
        super("68009862-090e-4a6d-b9f6-60b6c4eed369", 500, message, cause);
    }
}
