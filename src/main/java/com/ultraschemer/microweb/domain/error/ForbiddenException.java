package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class ForbiddenException extends StandardException {
    public ForbiddenException(String message) {
        super("671e968b-0bcd-4c8f-82ae-60d0f786192e", 403, message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super("671e968b-0bcd-4c8f-82ae-60d0f786192e", 403, message, cause);
    }
}
