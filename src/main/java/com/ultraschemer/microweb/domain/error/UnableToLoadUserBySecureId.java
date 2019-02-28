package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToLoadUserBySecureId extends StandardException {
    public UnableToLoadUserBySecureId(String message) {
        super("AD44420D-FCF4-4D86-B2F4-8CA1C0E986B1", 500, message);
    }
}
