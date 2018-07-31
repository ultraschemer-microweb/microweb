package com.ultraschemer.microweb.persistence.search;

public enum Condition implements Item {
    // The headers and trailing strings are necessary to class Searcher assembly queries
    AND(" and "),
    OR(" or ");

    private String type;

    Condition(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
