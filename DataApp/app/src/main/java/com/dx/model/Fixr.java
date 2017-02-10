package com.dx.model;

/**
 * Created by getfixr on 16/03/2015.
 */
public class Fixr {

    public Fixr(){

    }
    public Fixr(String name, String address, String city, String phone , String work_type, String trade, String level,
                String work_hours, String cost, String certification, String experience, String verify, String loginUser
                ,String dataLatitude, String dataLongitude) {
        this.name = name;
        this.address = address;
        this.work_type = work_type;
        this.trade = trade;
        this.level = level;
        this.work_hours = work_hours;
        this.cost = cost;
        this.certification = certification;
        this.experience = experience;
        this.city = city;
        this.phone = phone;
        this.verify = verify;
        this.loginUser = loginUser;
        this.dataLatitude = dataLatitude;
        this.dataLongitude = dataLongitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWork_type() {
        return work_type;
    }

    public void setWork_type(String work_type) {
        this.work_type = work_type;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getWork_hours() {
        return work_hours;
    }

    public void setWork_hours(String work_hours) {
        this.work_hours = work_hours;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getVerification() {
        return verify;
    }

    public void setVerification(String verify) {
        this.verify = verify;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getDataLatitude() {
        return dataLatitude;
    }

    public void setDataLatitude(String dataLatitude) {
        this.dataLatitude = dataLatitude;
    }

    public String getDataLongitude() {
        return dataLongitude;
    }

    public void setDataLongitude(String dataLongitude) {
        this.dataLongitude = dataLongitude;
    }

    private int id;
    private String city;
    private String phone;
    private String name;
    private String address;
    private String work_type;
    private String trade;
    private String level;
    private String work_hours;
    private String cost;
    private String certification;
    private String experience;
    private String verify;
    private String loginUser;
    private String dataLatitude;
    private String dataLongitude;
}
