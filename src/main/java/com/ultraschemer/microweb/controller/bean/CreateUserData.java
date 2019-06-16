package com.ibgateway.controller.bean;

import com.ultraschemer.microweb.domain.bean.UserRole;
import net.sf.oval.constraint.*;

import java.io.Serializable;
import java.util.List;

public class CreateUserData implements Serializable {
    /**
     * Unique identifier to the user.
     */
    @NotNull
    @NotEmpty
    @Length(min = 5, max = 128)
    private String id;

    /**
     * User name.
     */
    @NotNull
    @NotEmpty
    @Length(min = 5, max = 128)
    private String name;

    /**
     * The user alias, or social name:
     */
    @NotNull
    @NotEmpty
    @Email
    private String alias;

    /**
     * The password
     */
    @NotNull
    @NotEmpty
    @Length(min = 5, max = 64)
    private String password;

    /**
     * The password confirmation
     */
    @NotNull
    @NotEmpty
    @Length(min = 5, max = 64)
    @EqualToField("password")
    private String passwordConfirmation;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }
}
