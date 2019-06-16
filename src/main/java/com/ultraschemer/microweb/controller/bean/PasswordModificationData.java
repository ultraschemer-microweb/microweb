package com.ultraschemer.microweb.controller.bean;

import net.sf.oval.constraint.EqualToField;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

public class PasswordModificationData {
    @NotNull
    @NotEmpty
    private String currentPassword;

    @NotNull
    @NotEmpty
    private String newPassword;

    @NotNull
    @NotEmpty
    @EqualToField("newPassword")
    private String newPasswordConfirmation;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirmation() {
        return newPasswordConfirmation;
    }

    public void setNewPasswordConfirmation(String newPasswordConfirmation) {
        this.newPasswordConfirmation = newPasswordConfirmation;
    }
}
