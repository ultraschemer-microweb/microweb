package com.ultraschemer.microweb.domain.bean;

public class StandardMessage {
    private String message;
    private String code;
    private int httpStatus;
    private String stackTrace;

    public StandardMessage() {
        this.code = "";
        this.httpStatus = 200;
        this.message = "Sucesso.";
        this.stackTrace = "";
    }

    public StandardMessage(String code, int httpStatus, String message, String stackTrace) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
        this.stackTrace = stackTrace;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
