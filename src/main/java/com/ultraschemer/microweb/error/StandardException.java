package com.ultraschemer.microweb.error;

import com.google.common.base.Throwables;
import com.ultraschemer.microweb.domain.bean.Message;

/**
 * This is the standard error class to all business domain classes.
 */
public class StandardException extends Exception {
    /**
     * This field represents the expected HTTP status to be presented to the controller and this will be
     * potentially presented to the user interface.
     */
    private int httpStatus;

    /**
     * The error code, standardized to be, always the name "ERROR", followed by the colon (":") and of
     * an UUID which identifies uniquely the error.
     */
    private String code;

    public StandardException(String code, int httpStatus, String message) {
        super(message);
        this.code = "ERROR:" + code;
        this.httpStatus = httpStatus;
    }

    public StandardException(String code, int httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.code = "ERROR:" + code;
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public Message bean() {
        Message b = new Message();
        b.setCode(getCode());
        b.setHttpStatus(getHttpStatus());
        b.setMessage(getMessage());
        b.setStackTrace(Throwables.getStackTraceAsString(this));

        return b;
    }
}
