package com.ultraschemer.microweb.domain.bean;

import java.io.Serializable;

public class AuthenticationData implements Serializable {
    private String storeSecureId;
    private String name;
    private String password;

    public String getStoreSecureId() {
        return storeSecureId;
    }

    public void setStoreSecureId(String storeSecureId) {
        this.storeSecureId = storeSecureId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
