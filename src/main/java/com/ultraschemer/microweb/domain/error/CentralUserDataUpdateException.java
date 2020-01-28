package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class CentralUserDataUpdateException extends StandardException {
    public CentralUserDataUpdateException(String message) {
        super("fc40944c-842b-430d-b2b1-760bb9c30568", 500, message);
    }

    public CentralUserDataUpdateException(String message, Throwable cause) {
        super("fc40944c-842b-430d-b2b1-760bb9c30568", 500, message, cause);
    }
}
