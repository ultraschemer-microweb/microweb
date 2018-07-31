package com.ultraschemer.microweb.domain.bean;

import java.io.Serializable;

public class AuthorizationData implements Serializable {
    private String accessToken;
    private int ttl;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
