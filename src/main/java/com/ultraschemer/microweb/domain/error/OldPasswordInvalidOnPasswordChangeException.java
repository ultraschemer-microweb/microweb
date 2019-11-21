package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class OldPasswordInvalidOnPasswordChangeException extends StandardException {
    public OldPasswordInvalidOnPasswordChangeException(String message) {
        super("26f069c5-e8bd-43c6-9af0-9b199eb9e78f", 500, message);
    }

    public OldPasswordInvalidOnPasswordChangeException(String message, Throwable cause) {
        super("26f069c5-e8bd-43c6-9af0-9b199eb9e78f", 500, message, cause);
    }
}
