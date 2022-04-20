package com.fittr.server.request;

/**
 * This class is created to get the input object while changing password.
 *
 * @author Rakesh Kumar
 */
public class ChangePassword {
    String currentPassword;
    String newPassword;

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
}
