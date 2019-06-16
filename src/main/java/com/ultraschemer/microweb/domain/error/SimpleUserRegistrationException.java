package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class SimpleUserRegistrationException extends StandardException {
    public SimpleUserRegistrationException(String message) {
        super("ad2159a8-136f-4b31-8ca2-b4f4e88fb5a4", 500, message);
    }
}
