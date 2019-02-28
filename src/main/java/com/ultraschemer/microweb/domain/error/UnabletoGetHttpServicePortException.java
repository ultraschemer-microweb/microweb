package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnabletoGetHttpServicePortException extends StandardException {
    public UnabletoGetHttpServicePortException(String message) {
        super("68BE3088-E2C8-4811-BAC5-01661CB035B3", 500, message);
    }
}
