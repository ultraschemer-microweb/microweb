package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToExitCriticalSectionException extends StandardException {
    public UnableToExitCriticalSectionException(String message) {
        super("2B02276D-B24F-4787-ACF8-2394AA029FC8", 500, message);
    }
}
