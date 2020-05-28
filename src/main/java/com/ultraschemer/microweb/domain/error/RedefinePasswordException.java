package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class RedefinePasswordException extends StandardException {
    public RedefinePasswordException(String message) {
        super("c0623e7e-b1b2-451b-9f92-649692b78902", 500, message);
    }

    public RedefinePasswordException(String message, Throwable cause) {
        super("c0623e7e-b1b2-451b-9f92-649692b78902", 500, message, cause);
    }
}
