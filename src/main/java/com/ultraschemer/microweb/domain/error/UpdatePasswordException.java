package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UpdatePasswordException extends StandardException {
    public UpdatePasswordException(String message) {
        super("ce05abfc-8aec-4ca0-aa1a-8f96ac73f5b3", 500, message);
    }

    public UpdatePasswordException(String message, Throwable cause) {
        super("ce05abfc-8aec-4ca0-aa1a-8f96ac73f5b3", 500, message, cause);
    }
}
