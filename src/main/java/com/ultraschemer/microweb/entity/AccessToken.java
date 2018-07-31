package com.ultraschemer.microweb.entity;

import com.ultraschemer.microweb.persistence.Timeable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="access_token")
public class AccessToken extends Timeable {
    @Column(name="token")
    private String token;

    @Column(name="user_id")
    private UUID userId;

    @Column(name="status")
    private String status;

    @Column(name="expiration")
    private Date expiration;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
}
