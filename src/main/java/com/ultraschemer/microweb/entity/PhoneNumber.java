package com.ultraschemer.microweb.entity;

import com.ultraschemer.microweb.persistence.Timeable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="phone_number")
public class PhoneNumber extends Timeable {
    @Column(name="number")
    private String number;

    @Column(name="status")
    private String status;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
