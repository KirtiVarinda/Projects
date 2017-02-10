package app.funcarddeals.com.BeanClasses;

import java.io.Serializable;

/**
 * Created by dx on 9/1/2015.
 */
public class BuyFuncardBeanClassForUserInfo implements Serializable {

    private String cardQuantity, selectedCity, selectedCityID, resellerID,
            totalPrice,userFullName, userAddress, userCity, userState, userZip;
    private String payWith;

    public String getPayWith() {
        return payWith;
    }

    public void setPayWith(String payWith) {
        this.payWith = payWith;
    }

    public String getCardQuantity() {
        return cardQuantity;
    }

    public void setCardQuantity(String cardQuantity) {
        this.cardQuantity = cardQuantity;
    }

    public String getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(String selectedCity) {
        this.selectedCity = selectedCity;
    }

    public String getSelectedCityID() {
        return selectedCityID;
    }

    public void setSelectedCityID(String selectedCityID) {
        this.selectedCityID = selectedCityID;
    }

    public String getResellerID() {
        return resellerID;
    }

    public void setResellerID(String resellerID) {
        this.resellerID = resellerID;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getUserZip() {
        return userZip;
    }

    public void setUserZip(String userZip) {
        this.userZip = userZip;
    }
}
