package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class Oauth2MasterWellKnownLoadException extends StandardException {
    public Oauth2MasterWellKnownLoadException(String message) {
        super("ec87120a-bc33-47d6-8984-d36dad9b9f3a", 500, message);
    }

    public Oauth2MasterWellKnownLoadException(String message, Throwable cause) {
        super("ec87120a-bc33-47d6-8984-d36dad9b9f3a", 500, message, cause);
    }
}
