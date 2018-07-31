package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class CriticalSectionExitFailureException extends StandardException {
    public CriticalSectionExitFailureException(String message) {
        super("703bb159-a2bd-4221-8589-10891d093d49", 500, message);
    }
}
