package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToGenerateAccessTokenException extends StandardException {
    public UnableToGenerateAccessTokenException(String message) {
        super("7ad6016c-7602-42f6-9482-291adacb331b", 500, message);
    }
}
