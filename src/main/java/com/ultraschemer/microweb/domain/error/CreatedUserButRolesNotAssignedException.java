package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class CreatedUserButRolesNotAssignedException extends StandardException {
    public CreatedUserButRolesNotAssignedException(String message) {
        super("8df14c1a-9f94-4ad5-b091-ea4b343a1d80", 500, message);
    }

    public CreatedUserButRolesNotAssignedException(String message, Throwable cause) {
        super("8df14c1a-9f94-4ad5-b091-ea4b343a1d80", 500, message, cause);
    }
}
