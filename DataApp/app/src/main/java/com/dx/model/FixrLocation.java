package com.dx.model;

/**
 * Created by getfixr on 5/25/2015.
 */
public class FixrLocation {
    private int Locationid;
    private String userName;
    private String latitude;
    private String longitude;
    private String timeIniliSec;

    public FixrLocation(String userName,String latitude,String longitude,String timeIniliSec){
        this.userName=userName;
        this.latitude=latitude;
        this.longitude=longitude;
        this.timeIniliSec=timeIniliSec;
    }
    public FixrLocation(){

    }
    public int getLocationid() {
        return Locationid;
    }

    public String getUserName() {
        return userName;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getTimeIniliSec() {
        return timeIniliSec;
    }

    public void setLocationid(int locationid) {
        Locationid = locationid;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setTimeIniliSec(String timeIniliSec) {
        this.timeIniliSec = timeIniliSec;
    }
}
