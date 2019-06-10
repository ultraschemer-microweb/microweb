package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToReadConfigurationException  extends StandardException {
    public UnableToReadConfigurationException(String message) {
        super("44900448-b803-4db3-b927-667852d4f5a5", 500, message);
    }
}
