package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UserPassNotEqualsException extends StandardException {
    public UserPassNotEqualsException(String message) {
        super(" aae7871c-3b93-41ec-a19a-b8f3a676f672", 500, message);
    }
}
