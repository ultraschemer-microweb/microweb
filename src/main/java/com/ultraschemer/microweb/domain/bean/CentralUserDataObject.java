package com.ultraschemer.microweb.domain.bean;

import java.util.UUID;

public class CentralUserDataObject {
    private UUID centralControlId;
    private String givenName;
    private String familyName;
    private String alias;
    private String name;
    private String eMail;
    private String eMailStatus;

    public UUID getCentralControlId() {
        return centralControlId;
    }

    public void setCentralControlId(UUID centralControlId) {
        this.centralControlId = centralControlId;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String geteMailStatus() {
        return eMailStatus;
    }

    public void seteMailStatus(String eMailStatus) {
        this.eMailStatus = eMailStatus;
    }
}
