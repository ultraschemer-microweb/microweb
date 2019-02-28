package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToReadRuntimeException extends StandardException {
    public UnableToReadRuntimeException(String message) {
        super("EEA3FA15-3290-4D89-8050-CBB407ADC768", 500, message);
    }
}
