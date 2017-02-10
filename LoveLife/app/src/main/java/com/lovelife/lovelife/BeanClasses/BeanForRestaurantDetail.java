package com.lovelife.lovelife.BeanClasses;

import android.graphics.Bitmap;

/**
 * Created by dx on 11/2/2016.
 */
public class BeanForRestaurantDetail {
    private String restaurantID,restaurantCity,restaurantAddr,restaurantName,restaurantState,
            restaurantArea,restaurantPCode,restaurantCountry,restaurantphone,restaurantLat,
            restaurantLang,restaurantPrice,restaurantReserveURL,restaurantMobileReserveURL,restaurantImageURL;

    private Bitmap restaurentImageBitmap;

    public Bitmap getRestaurantImageBitmap() {
        return restaurentImageBitmap;
    }

    public void setRestaurentImageBitmap(Bitmap restaurentImageBitmap) {
        this.restaurentImageBitmap = restaurentImageBitmap;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getRestaurantCity() {
        return restaurantCity;
    }

    public void setRestaurantCity(String restaurantCity) {
        this.restaurantCity = restaurantCity;
    }

    public String getRestaurantAddr() {
        return restaurantAddr;
    }

    public void setRestaurantAddr(String restaurantAddr) {
        this.restaurantAddr = restaurantAddr;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantState() {
        return restaurantState;
    }

    public void setRestaurantState(String restaurantState) {
        this.restaurantState = restaurantState;
    }

    public String getRestaurantArea() {
        return restaurantArea;
    }

    public void setRestaurantArea(String restaurantArea) {
        this.restaurantArea = restaurantArea;
    }

    public String getRestaurantPCode() {
        return restaurantPCode;
    }

    public void setRestaurantPCode(String restaurantPCode) {
        this.restaurantPCode = restaurantPCode;
    }

    public String getRestaurantCountry() {
        return restaurantCountry;
    }

    public void setRestaurantCountry(String restaurantCountry) {
        this.restaurantCountry = restaurantCountry;
    }

    public String getRestaurantphone() {
        return restaurantphone;
    }

    public void setRestaurantphone(String restaurantphone) {
        this.restaurantphone = restaurantphone;
    }

    public String getRestaurantLat() {
        return restaurantLat;
    }

    public void setRestaurantLat(String restaurantLat) {
        this.restaurantLat = restaurantLat;
    }

    public String getRestaurantLang() {
        return restaurantLang;
    }

    public void setRestaurantLang(String restaurantLang) {
        this.restaurantLang = restaurantLang;
    }

    public String getRestaurantPrice() {
        return restaurantPrice;
    }

    public void setRestaurantPrice(String restaurantPrice) {
        this.restaurantPrice = restaurantPrice;
    }

    public String getRestaurantMobileReserveURL() {
        return restaurantMobileReserveURL;
    }

    public void setRestaurantMobileReserveURL(String restaurantMobileReserveURL) {
        this.restaurantMobileReserveURL = restaurantMobileReserveURL;
    }

    public String getRestaurantReserveURL() {
        return restaurantReserveURL;
    }

    public void setRestaurantReserveURL(String restaurantReserveURL) {
        this.restaurantReserveURL = restaurantReserveURL;
    }

    public String getRestaurantImageURL() {
        return restaurantImageURL;
    }

    public void setRestaurantImageURL(String restaurantImageURL) {
        this.restaurantImageURL = restaurantImageURL;
    }
}
