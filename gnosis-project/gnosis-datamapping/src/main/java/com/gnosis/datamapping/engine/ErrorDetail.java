package com.gnosis.datamapping.engine;

public class ErrorDetail {

    private String field;
    private String rule;
    private String message;
    private Object value;

    public ErrorDetail() {
    }

    public ErrorDetail(String field, String rule, String message, Object value) {
        this.field = field;
        this.rule = rule;
        this.message = message;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
