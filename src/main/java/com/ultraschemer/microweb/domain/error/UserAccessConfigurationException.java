package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UserAccessConfigurationException extends StandardException {
    public UserAccessConfigurationException(String message) {
        super("d72fb99b-f8b5-41b1-acf4-237d9272c67d", 500, message);
    }
}
