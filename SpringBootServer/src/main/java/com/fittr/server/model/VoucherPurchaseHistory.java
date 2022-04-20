package com.fittr.server.model;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;


/**
 * This entity class to represent the voucher purchase history of the user.
 *
 * @author Rakesh Kumar
 */
@Entity
public class VoucherPurchaseHistory {

    @Id
    private int voucherId;

    @Column(nullable = false)
    private int userId;

    @Column(nullable = false)
    private String voucherTitle;

    private String voucherDesc;

    @Column(nullable = false)
    private int voucherCoins;

    @Lob
    @Column(nullable = true, columnDefinition = "LONGTEXT")
    private String voucherImage;

    @Column
    private Date purchaseDate;

    public VoucherPurchaseHistory() {
    }

    public VoucherPurchaseHistory(int voucherId, int userId, String voucherTitle, String voucherDesc, int voucherCoins, String  voucherImage, Date purchaseDate) {
        this.voucherId = voucherId;
        this.userId = userId;
        this.voucherTitle = voucherTitle;
        this.voucherDesc = voucherDesc;
        this.voucherCoins = voucherCoins;
        this.voucherImage = voucherImage;
        this.purchaseDate = purchaseDate;
    }

    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getVoucherTitle() {
        return voucherTitle;
    }

    public void setVoucherTitle(String voucherTitle) {
        this.voucherTitle = voucherTitle;
    }

    public String getVoucherDesc() {
        return voucherDesc;
    }

    public void setVoucherDesc(String voucherDesc) {
        this.voucherDesc = voucherDesc;
    }

    public int getVoucherCoins() {
        return voucherCoins;
    }

    public void setVoucherCoins(int voucherCoins) {
        this.voucherCoins = voucherCoins;
    }

    public String  getVoucherImage() {
        return voucherImage;
    }

    public void setVoucherImage(String  voucherImage) {
        this.voucherImage = voucherImage;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Override
    public String toString() {
        return "VoucherPurchaseHistory{" +
                "voucherId=" + voucherId +
                ", userId=" + userId +
                ", voucherTitle='" + voucherTitle + '\'' +
                ", voucherDesc='" + voucherDesc + '\'' +
                ", voucherCoins=" + voucherCoins +
                ", voucherImage=" + voucherImage +
                ", purchaseDate=" + purchaseDate +
                '}';
    }
}

