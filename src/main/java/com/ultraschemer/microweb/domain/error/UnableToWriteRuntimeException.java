package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToWriteRuntimeException extends StandardException {
    public UnableToWriteRuntimeException(String message) {
        super("6D6FFAEB-70BD-445E-98EC-7631215EDAFC", 500, message);
    }
}
