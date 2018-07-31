package com.ultraschemer.microweb.entity;

import com.ultraschemer.microweb.persistence.Timeable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name="user__email_address")
public class User_EmailAddress extends Timeable {
    @Column(name="user_id")
    private UUID userId;

    @Column(name="email_address_id")
    private UUID emailAddressId;

    @Column(name="preference_order")
    private long preferenceOrder;

    @Column(name="status")
    private String status;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getEmailAddressId() {
        return emailAddressId;
    }

    public void setEmailAddressId(UUID emailAddressId) {
        this.emailAddressId = emailAddressId;
    }

    public long getPreferenceOrder() {
        return preferenceOrder;
    }

    public void setPreferenceOrder(long preferenceOrder) {
        this.preferenceOrder = preferenceOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
