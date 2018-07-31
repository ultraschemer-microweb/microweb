package com.ultraschemer.microweb.entity;

import com.ultraschemer.microweb.persistence.Timeable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name="user__phone_number")
public class User_PhoneNumber extends Timeable {
    @Column(name="user_id")
    private UUID userId;

    @Column(name="phone_number_id")
    private UUID phoneNumberId;

    @Column(name="preference_order")
    private long preferenceOrder;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getPhoneNumberId() {
        return phoneNumberId;
    }

    public void setPhoneNumberId(UUID phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }

    public long getPreferenceOrder() {
        return preferenceOrder;
    }

    public void setPreferenceOrder(long preferenceOrder) {
        this.preferenceOrder = preferenceOrder;
    }
}
