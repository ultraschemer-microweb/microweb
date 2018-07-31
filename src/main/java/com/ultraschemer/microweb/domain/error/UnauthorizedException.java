package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnauthorizedException extends StandardException {
    public UnauthorizedException(String message) {
        super("f74b2759-4163-4b0f-a913-929d606af26a", 401, message);
    }
}
