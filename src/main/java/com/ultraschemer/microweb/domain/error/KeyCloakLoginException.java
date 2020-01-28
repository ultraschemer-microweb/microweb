package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class KeyCloakLoginException extends StandardException {
    public KeyCloakLoginException(String message) {
        super("a8662dff-d928-40ca-9972-4fe2bcd1d742", 500, message);
    }

    public KeyCloakLoginException(String message, Throwable cause) {
        super("a8662dff-d928-40ca-9972-4fe2bcd1d742", 500, message, cause);
    }
}
