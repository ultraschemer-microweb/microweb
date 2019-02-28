package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToGetRolesFromUser extends StandardException {
    public UnableToGetRolesFromUser(String message) {
        super("24E6CA3A-B007-400A-B953-1B35066979BA", 500, message);
    }
}
