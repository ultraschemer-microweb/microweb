package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class Oauth2MasterWellKnowConfigurationException extends StandardException {
    public Oauth2MasterWellKnowConfigurationException(String message) {
        super("88bc65ed-0e13-4563-9f75-9b5573801380", 500, message);
    }

    public Oauth2MasterWellKnowConfigurationException(String message, Throwable cause) {
        super("88bc65ed-0e13-4563-9f75-9b5573801380", 500, message, cause);
    }
}
