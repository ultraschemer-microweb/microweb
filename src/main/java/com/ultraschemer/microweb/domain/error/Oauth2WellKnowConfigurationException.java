package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class Oauth2WellKnowConfigurationException extends StandardException {
    public Oauth2WellKnowConfigurationException(String message) {
        super("c28f5118-e47a-4e15-b9e0-acebd2fa3a38", 500, message);
    }
}
