package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToEnterCriticalSectionException extends StandardException {
    public UnableToEnterCriticalSectionException(String message) {
        super("5fdce217-8ece-4ebb-8fbe-d5e7aa079d85", 500, message);
    }
}
