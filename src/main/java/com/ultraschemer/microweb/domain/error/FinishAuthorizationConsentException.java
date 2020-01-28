package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class FinishAuthorizationConsentException extends StandardException {
    public FinishAuthorizationConsentException(String message) {
        super("abc75bfc-557b-4906-a02a-15b97749ee17", 500, message);
    }

    public FinishAuthorizationConsentException(String message, Throwable cause) {
        super("abc75bfc-557b-4906-a02a-15b97749ee17", 500, message, cause);
    }
}
