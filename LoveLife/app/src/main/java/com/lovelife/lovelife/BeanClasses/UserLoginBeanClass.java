package com.lovelife.lovelife.BeanClasses;

import java.io.Serializable;

/**
 * Created by dx on 11/3/2016.
 */
public class UserLoginBeanClass implements Serializable {

    /**
     * secured string variables in bean class
     */
    private String userPass,userName, userEmail, userId, userMobile, userToken, fbToken, profilePic,emailConfirmStatus,isConnectedToPartner,userGender,userDOB;

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getUserDOB() {
        return userDOB;
    }

    public void setUserDOB(String userDOB) {
        this.userDOB = userDOB;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getIsConnectedToPartner() {
        return isConnectedToPartner;
    }

    public void setIsConnectedToPartner(String isConnectedToPartner) {
        this.isConnectedToPartner = isConnectedToPartner;
    }

    public String getEmailConfirmStatus() {
        return emailConfirmStatus;
    }

    public void setEmailConfirmStatus(String emailConfirmStatus) {
        this.emailConfirmStatus = emailConfirmStatus;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getFbToken() {
        return fbToken;
    }

    public void setFbToken(String fbToken) {
        this.fbToken = fbToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
