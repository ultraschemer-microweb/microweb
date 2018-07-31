package com.ultraschemer.microweb.domain.bean;

import java.io.Serializable;
import java.util.List;

public class UserData implements Serializable {
    /**
     * Unique identifier to the user.
     */
    private String id;

    /**
     * User name.
     */
    private String name;

    /**
     * The user alias, or social name:
     */
    private String alias;

    /**
     * User roles in the system - this data is important to evaluate the system access control.
     */
    private List<UserRole> roles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
