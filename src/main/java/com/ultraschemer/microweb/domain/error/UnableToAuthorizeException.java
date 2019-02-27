package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToAuthorizeException extends StandardException {
    public UnableToAuthorizeException(String message) {
        super("a21fb85d-4314-42e7-a9b8-8abcd86f04c2", 500, message);
    }
}
