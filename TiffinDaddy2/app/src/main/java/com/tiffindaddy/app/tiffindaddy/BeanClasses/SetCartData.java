package com.tiffindaddy.app.tiffindaddy.BeanClasses;

/**
 * Created by dx on 2/23/2016.
 */
public class SetCartData {


    public void setAddressInCartBean(CartData cartData[],String address,String specialNote,String setTimeSlot){

        for(int i=0;i<cartData.length;i++){
            cartData[i].setAddress(address);
            cartData[i].setSpecialNote(specialNote);
            cartData[i].setTimeSlot(setTimeSlot);
        }

    }

}
