package com.ultraschemer.microweb.error;

import com.ultraschemer.microweb.domain.bean.Message;

public interface StandardExceptionSignature {
    public int getHttpStatus();
    public String getCode();
    public Message bean();
}
