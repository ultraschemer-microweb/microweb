package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UpdateUserNameException extends StandardException {
    public UpdateUserNameException(String message) {
        super("eee6a6c8-41ff-4479-8497-035e21173467", 500, message);
    }
}
