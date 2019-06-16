package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UserRoleSetException extends StandardException {
    public UserRoleSetException(String message) {
        super("2ce671d3-1403-4dcd-b41b-278ccda34b11", 500, message);
    }
}
