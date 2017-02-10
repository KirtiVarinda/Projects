package com.tiffindaddy.app.tiffindaddy.BeanClasses;

/**
 * Created by dx on 2/15/2016.
 */


public class CartData {

    private TiffinData tiffinData;
    private int position;
    private String selectedDate;
    private String whatDay, address, specialNote, timeSlot, mkey, tiffinTYpe;

    public String getTiffinTYpe() {
        return tiffinTYpe;
    }

    public void setTiffinTYpe(String tiffinTYpe) {
        this.tiffinTYpe = tiffinTYpe;
    }

    public String getMkey() {
        return mkey;
    }

    public void setMkey(String mkey) {
        this.mkey = mkey;
    }


    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getSpecialNote() {
        return specialNote;
    }

    public void setSpecialNote(String specialNote) {
        this.specialNote = specialNote;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWhatDay() {
        return whatDay;
    }


    public void setWhatDay(String whatDay) {
        this.whatDay = whatDay;
    }

    public TiffinData getTiffinData() {
        return tiffinData;
    }

    public void setTiffinData(TiffinData tiffinData) {
        this.tiffinData = tiffinData;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }
}
