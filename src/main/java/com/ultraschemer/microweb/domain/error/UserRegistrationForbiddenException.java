package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UserRegistrationForbiddenException extends StandardException {
    public UserRegistrationForbiddenException(String message) {
        super("5dce9872-0dc9-4094-9743-248dc7832955", 403, message);
    }

    public UserRegistrationForbiddenException(String message, Throwable cause) {
        super("5dce9872-0dc9-4094-9743-248dc7832955", 403, message, cause);
    }
}
