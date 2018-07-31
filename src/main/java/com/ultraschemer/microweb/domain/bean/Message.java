package com.ultraschemer.microweb.domain.bean;

import java.io.Serializable;

public class Message extends StandardMessage implements Serializable {
    public Message() {
        super();
    }

    public Message(String code, int httpStatus, String message) {
        super(code, httpStatus, message);
    }
}
