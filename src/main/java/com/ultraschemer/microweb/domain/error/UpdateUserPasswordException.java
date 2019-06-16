package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UpdateUserPasswordException extends StandardException {
    public UpdateUserPasswordException(String message) {
        super("76396791-26f7-4662-b73c-5391b472afdb", 500, message);
    }
}
