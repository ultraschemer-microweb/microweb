package com.ultraschemer.microweb.domain.error;

import com.ultraschemer.microweb.error.StandardException;

public class QueryTypeUnsupportedException extends StandardException {
    public QueryTypeUnsupportedException(String message) {
        super("5cf6c3d9-d100-47f1-b0b2-3e3fe3e7060d", 500, message);
    }
}
