package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToAssignPasswordOnUserCreationException extends StandardException {
    public UnableToAssignPasswordOnUserCreationException(String message) {
        super("97753782-749c-4a83-b951-69a4a205f6e1", 500, message);
    }

    public UnableToAssignPasswordOnUserCreationException(String message, Throwable cause) {
        super("97753782-749c-4a83-b951-69a4a205f6e1", 500, message, cause);
    }
}
