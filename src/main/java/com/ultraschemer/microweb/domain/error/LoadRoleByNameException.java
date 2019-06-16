package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class LoadRoleByNameException extends StandardException {
    public LoadRoleByNameException(String message) {
        super("5aa3e6e3-53d2-4757-a00b-f19f068441a6", 500, message);
    }
}
