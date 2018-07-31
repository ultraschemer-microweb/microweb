package com.ultraschemer.microweb.entity;

import com.ultraschemer.microweb.persistence.Createable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name="user__role")
public class User_Role extends Createable {
    @Column(name="user_id")
    private UUID userId;

    @Column(name="role_id")
    private UUID roleId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }
}
