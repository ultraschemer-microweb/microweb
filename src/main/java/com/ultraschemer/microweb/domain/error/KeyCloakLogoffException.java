package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class KeyCloakLogoffException extends StandardException {
    public KeyCloakLogoffException(String message) {
        super("769b26a9-ab54-436f-8804-23ccc777923a", 500, message);
    }

    public KeyCloakLogoffException(String message, Throwable cause) {
        super("769b26a9-ab54-436f-8804-23ccc777923a", 500, message, cause);
    }
}
