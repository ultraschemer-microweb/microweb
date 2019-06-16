package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UpdateUserAliasException extends StandardException {
    public UpdateUserAliasException(String message) {
        super("2d8dd593-338b-473e-bc00-01259fe077f9", 500, message);
    }
}
