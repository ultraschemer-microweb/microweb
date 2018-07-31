package com.ultraschemer.microweb.domain.error;


import com.ultraschemer.microweb.error.StandardException;

public class QueryParseException extends StandardException {
    public QueryParseException(String message) {
        super("57be3f3d-d51c-4f68-bd15-9da8489f4e88", 500, message);
    }
}
