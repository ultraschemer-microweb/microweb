package com.ultraschemer.microweb.entity;

import com.ultraschemer.microweb.persistence.Identifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="lock_control")
public class LockControl extends Identifiable {
    @Column(name="name")
    private String name;

    @Column(name="expiration")
    private Date expiration;

    @Column(name="status")
    private String status;

    @Column(name="owner")
    private String owner;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
