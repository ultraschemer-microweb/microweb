package com.ultraschemer.microweb.domain.bean;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import java.io.Serializable;

public class Oauth2AuthorizationConsentedObject implements Serializable {
    /** The Foreign client Id: */
    @NotNull
    @NotEmpty
    private String clientId;

    /** The foreign client secret, correspondent to the given id: */
    @NotNull
    @NotEmpty
    private String clientSecret;

    /** The expected redirect uri: */
    @NotNull
    @NotEmpty
    private String redirectUri;

    /** The expected Session State: */
    @NotNull
    @NotEmpty
    private String sessionState;

    /** The expected code: */
    @NotNull
    @NotEmpty
    private String code;

    /** The optional state: */
    private String state;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getSessionState() {
        return sessionState;
    }

    public void setSessionState(String sessionState) {
        this.sessionState = sessionState;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

