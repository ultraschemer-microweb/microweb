package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class UnableToUnauthorizeException extends StandardException {
    public UnableToUnauthorizeException(String message) {
        super("eef6108f-247c-4e6c-bfab-afd23e48a29e", 500, message);
    }
}
