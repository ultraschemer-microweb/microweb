package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UserNotFoundException extends StandardException {
    public UserNotFoundException(String message) {
        super("2be1295e-e98c-4029-a0ee-61d51f5c040a", 500, message);
    }
}
