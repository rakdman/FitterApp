package com.fittr.server.model;

import javax.persistence.*;

/**
 * This entity class for create the users for fitter app
 *
 * @author Rakesh Kumar
 */

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userId;

    @Column(nullable = false)
    private String fullName;

    @Column( name = "email", unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private int totalCoins=0;

    @Lob
    @Column(nullable = true, columnDefinition = "LONGTEXT")
    private String userImage;

    public Users() {
    }

    public Users(int userId, String fullName, String email, String password, int totalCoins, String userImage) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.totalCoins = totalCoins;
        this.userImage = userImage;

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTotalCoins() {
        return totalCoins;
    }

    public void setTotalCoins(int totalCoins) {
        this.totalCoins = totalCoins;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    @Override
    public String toString() {
        return "Users{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", totalCoins=" + totalCoins +
                ", userImage=" + userImage +
                '}';
    }
}
