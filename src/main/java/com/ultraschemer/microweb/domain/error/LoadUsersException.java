package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class LoadUsersException extends StandardException {
    public LoadUsersException(String message) {
        super("37fa31b5-79a9-49fc-a881-d67c96a67fb6", 500, message);
    }
}
