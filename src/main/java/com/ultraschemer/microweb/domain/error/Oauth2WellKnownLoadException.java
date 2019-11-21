package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class Oauth2WellKnownLoadException extends StandardException {
    public Oauth2WellKnownLoadException(String message) {
        super("055f1d82-0a21-4ede-9e04-fea18db7b44a", 500, message);
    }

    public Oauth2WellKnownLoadException(String message, Throwable cause) {
        super("055f1d82-0a21-4ede-9e04-fea18db7b44a", 500, message, cause);
    }
}
