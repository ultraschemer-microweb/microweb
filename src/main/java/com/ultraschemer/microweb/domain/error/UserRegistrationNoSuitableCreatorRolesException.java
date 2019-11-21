package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UserRegistrationNoSuitableCreatorRolesException extends StandardException {
    public UserRegistrationNoSuitableCreatorRolesException(String message) {
        super("167c8dec-a334-4aa4-8934-0a730b63d676", 500, message);
    }

    public UserRegistrationNoSuitableCreatorRolesException(String message, Throwable cause) {
        super("167c8dec-a334-4aa4-8934-0a730b63d676", 500, message, cause);
    }
}
