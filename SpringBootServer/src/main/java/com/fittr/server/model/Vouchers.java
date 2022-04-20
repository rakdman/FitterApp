package com.fittr.server.model;

import javax.persistence.*;

/**
 * This entity class to represent the voucher of the user.
 *
 * @author Rakesh Kumar
 */
@Entity
public class Vouchers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int voucherId;

    @Column(nullable = false)
    private String voucherTitle;

    private String voucherDesc;

    @Column(nullable = false)
    private int voucherCoins;

    @Lob
    @Column(nullable = true, columnDefinition = "LONGTEXT")
    private String voucherImage;

    public Vouchers() {
    }


    public Vouchers(int voucherId, String voucherTitle, String voucherDesc, int voucherCoins, String voucherImageName, String voucherImage) {
        this.voucherId = voucherId;
        this.voucherTitle = voucherTitle;
        this.voucherDesc = voucherDesc;
        this.voucherCoins = voucherCoins;
        this.voucherImage = voucherImage;
    }

    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
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

    public String getVoucherImage() {
        return voucherImage;
    }

    public void setVoucherImage(String voucherImage) {
        this.voucherImage = voucherImage;
    }

    @Override
    public String toString() {
        return "Vouchers{" +
                "voucherId=" + voucherId +
                ", voucherTitle='" + voucherTitle + '\'' +
                ", voucherDesc='" + voucherDesc + '\'' +
                ", voucherCoins=" + voucherCoins +
                ", voucherImage=" + voucherImage +
                '}';
    }
}

