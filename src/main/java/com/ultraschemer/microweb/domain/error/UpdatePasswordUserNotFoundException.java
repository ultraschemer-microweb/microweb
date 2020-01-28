package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UpdatePasswordUserNotFoundException extends StandardException {
    public UpdatePasswordUserNotFoundException(String message) {
        super("6e49e70c-46ec-422d-89d6-f29d9dc7ae2c", 500, message);
    }

    public UpdatePasswordUserNotFoundException(String message, Throwable cause) {
        super("6e49e70c-46ec-422d-89d6-f29d9dc7ae2c", 500, message, cause);
    }
}
