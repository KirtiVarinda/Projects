package com.tiffindaddy.app.tiffindaddy.BeanClasses;

import android.graphics.Bitmap;

/**
 * Created by dx on 2/3/2016.
 */
public class TiffinData {

    private String tiffinId;

    public String getTiffinId() {
        return tiffinId;
    }

    public void setTiffinId(String tiffinId) {
        this.tiffinId = tiffinId;
    }

    private String tiffinName;
    private String tiffinDescription;
    private String tiffinPrice;
    private String tiffinImage;
    private String categoryType;
    private String categoryName;
    private String deliverySlot;
    private String deliveryDate;


    private Bitmap tiffinImageBitmap;
    private int quantityPurchased;

    public String getDeliverySlot() {
        return deliverySlot;
    }

    public void setDeliverySlot(String deliverySlot) {
        this.deliverySlot = deliverySlot;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getQuantityPurchased() {
        return quantityPurchased;
    }

    public void setQuantityPurchased(int quantityPurchased) {
        this.quantityPurchased = quantityPurchased;
    }

    public Bitmap getTiffinImageBitmap() {
        return tiffinImageBitmap;
    }

    public void setTiffinImageBitmap(Bitmap tiffinImageBitmap) {
        this.tiffinImageBitmap = tiffinImageBitmap;
    }

    public String getTiffinName() {
        return tiffinName;
    }

    public void setTiffinName(String tiffinName) {
        this.tiffinName = tiffinName;
    }

    public String getTiffinDescription() {
        return tiffinDescription;
    }

    public void setTiffinDescription(String tiffinDescription) {
        this.tiffinDescription = tiffinDescription;
    }

    public String getTiffinPrice() {
        return tiffinPrice;
    }

    public void setTiffinPrice(String tiffinPrice) {
        this.tiffinPrice = tiffinPrice;
    }

    public String getTiffinImage() {
        return tiffinImage;
    }

    public void setTiffinImage(String tiffinImage) {
        this.tiffinImage = tiffinImage;
    }
}
