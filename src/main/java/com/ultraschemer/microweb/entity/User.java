package com.ultraschemer.microweb.entity;

import com.ultraschemer.microweb.persistence.Timeable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name="user_")
public class User extends Timeable {
    @Column(name="person_id")
    private UUID personId;

    @Column(name="password")
    private String password;

    @Column(name="name")
    private String name;

    @Column(name="alias")
    private String alias;

    @Column(name="status")
    private String status;

    @Column(name="central_control_id")
    private UUID centralControlId;

    @Column(name="given_name")
    private String givenName;

    @Column(name="family_name")
    private String familyName;

    public UUID getPersonId() {
        return personId;
    }

    public void setPersonId(UUID personId) {
        this.personId = personId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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
}
