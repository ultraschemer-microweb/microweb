package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToUpdateOpenIdUserDataException extends StandardException {
    public UnableToUpdateOpenIdUserDataException(String message) {
        super("c1889464-b855-4e5e-bb7c-b0daa54b30ad", 500, message);
    }

    public UnableToUpdateOpenIdUserDataException(String message, Throwable cause) {
        super("c1889464-b855-4e5e-bb7c-b0daa54b30ad", 500, message, cause);
    }
}
