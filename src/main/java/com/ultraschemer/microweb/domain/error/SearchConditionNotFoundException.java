package com.ultraschemer.microweb.domain.error;


import com.ultraschemer.microweb.error.StandardException;

public class SearchConditionNotFoundException extends StandardException {
    public SearchConditionNotFoundException(String message) {
        super("2c2344a6-add2-4e5d-87fb-3a12302eead7", 500, message);
    }
}
