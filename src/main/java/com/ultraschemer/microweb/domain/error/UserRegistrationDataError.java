package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

import javax.persistence.PersistenceException;

public class UserRegistrationDataError extends StandardException {
    public UserRegistrationDataError(String message) {
        super("67befe59-fc40-4e01-a637-fa36eb0d8a4b", 500, message);
    }

    public UserRegistrationDataError(String message, Throwable cause) {
        super("67befe59-fc40-4e01-a637-fa36eb0d8a4b", 500, message, cause);
    }
}
