package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class LoadRoleByIdException extends StandardException {
    public LoadRoleByIdException(String message) {
        super("7922fc42-1f50-42a2-96d5-748d968311fb", 500, message);
    }
}
