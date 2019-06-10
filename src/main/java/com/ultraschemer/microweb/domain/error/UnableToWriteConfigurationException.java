package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToWriteConfigurationException extends StandardException {
    public UnableToWriteConfigurationException(String message) {
        super("21fd7faa-f1dc-4e04-9c83-bab3d739c29e", 500, message);
    }
}
