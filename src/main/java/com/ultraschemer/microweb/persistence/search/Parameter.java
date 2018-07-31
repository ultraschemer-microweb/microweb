package com.ultraschemer.microweb.persistence.search;

public class Parameter<T> implements Item {
    private String field;
    private String criterion;
    private T value;

    public Parameter(String field, String criterion, T value) {
        this.field = field;
        this.criterion = criterion;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getCriterion() {
        return criterion;
    }

    public void setCriterion(String criterion) {
        this.criterion = criterion;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
