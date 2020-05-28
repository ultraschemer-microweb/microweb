package com.ultraschemer.microweb.error;

public class ParameterDateFormat extends StandardRuntimeException {
    public ParameterDateFormat(String message) {
        super("c03d03af-3e27-4495-83e0-55198cb8beec", 400, message);
    }

    public ParameterDateFormat(String message, Throwable cause) {
        super("c03d03af-3e27-4495-83e0-55198cb8beec", 400, message, cause);
    }
}
