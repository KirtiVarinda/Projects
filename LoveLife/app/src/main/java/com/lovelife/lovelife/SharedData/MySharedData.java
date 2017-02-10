package com.lovelife.lovelife.SharedData;

import android.content.Context;
import android.content.SharedPreferences;

import com.lovelife.lovelife.BeanClasses.ConnectedPartnerBean;
import com.lovelife.lovelife.BeanClasses.UserLoginBeanClass;


/**
 * This class is used to save data.
 * Saved data is shared among all the activities.
 * Created by Dx on 7/20/2015.
 */
public class MySharedData {


    private static SharedPreferences loveLifeSessionPrefreneces;
    private final String LOVELIFE_PRIVATE_PREF = "com.loveLife.loveLife.PrivateSession";
    /**
     * Static keys for login Session
     */
    public static String USERNAME = "com.loveLife.loveLife.PrivateSession.userName";
    public static String USEREMAIL = "com.loveLife.loveLife.PrivateSession.userEmail";
    public static String USERID = "com.loveLife.loveLife.PrivateSession.userId";
    public static String USERMOBILE = "com.loveLife.loveLife.PrivateSession.userMobile";
    public static String TOKEN = "com.loveLife.loveLife.PrivateSession.userToken";
    public static String CHECKNET = "com.loveLife.loveLife.PrivateSession.checkNet";
    public static String USER_DOB = "com.loveLife.loveLife.PrivateSession.USER_DOB";
    public static String USER_GENDER = "com.loveLife.loveLife.PrivateSession.USER_GENDER";

    public static String FBTOKEN = "com.loveLife.loveLife.PrivateSession.fbToken";

    public static String openfireUser = "com.loveLife.loveLife.PrivateSession.openFireUser";
    // public static String openFirePass = "com.loveLife.loveLife.PrivateSession.openFirePass";


    /**
     * partner connected data
     */
    public static String isConnectedToPartner = "com.loveLife.loveLife.PrivateSession.isConnectedToPartner";
    public static String CONNECTED_PARNER_DISPLAY_NAME = "com.loveLife.loveLife.PrivateSession.connectedPartnerDisplayName";
    public static String CONNECTED_PARNER_GENDER = "com.loveLife.loveLife.PrivateSession.connectedPartnerGender";
    public static String CONNECTED_PARNER_ID = "com.loveLife.loveLife.PrivateSession.connectedPartnerID";
    public static String CONNECTED_PARNER_EMAIL = "com.loveLife.loveLife.PrivateSession.connectedPartnerEmail";
    public static String CONNECTED_PARNER_CONNECTION_ID = "com.loveLife.loveLife.PrivateSession.connectedPartner_connectionID";
    public static String openfireConnectedPartner = "com.loveLife.loveLife.PrivateSession.openFireConnectedPartner";
    public static String PARTNER_PROFILEPIC = "com.loveLife.loveLife.PrivateSession.partner_profilepic";


    public static String USER_PROFILE_PIC = "com.loveLife.loveLife.PrivateSession.profilePic";
    public static String USER_PASSMD5 = "com.loveLife.loveLife.PrivateSession.userPassMD5";
    public static String CONFIRM_EMAIL = "com.loveLife.loveLife.PrivateSession.emailConfirm";


    public static String COMPLETE_LOGGGED_IN = "com.loveLife.loveLife.PrivateSession.complete_logged_in";
    public static String FIRST_GETUSERPROFILE_UPDATE = "com.loveLife.loveLife.PrivateSession.FIRST_GETUSERPROFILE_UPDATE";
    public static String FIRST_ADDRESSANDFAVORITES_UPDATE = "com.loveLife.loveLife.PrivateSession.FIRST_ADDRESSANDFAVORITES_UPDATE";



    public static String saveProfileImage = "com.loveLife.loveLife.PrivateSession.saveProfileImage";


    /**
     * FCM registeration ID
     */
    public static String FCM_REGISTRATIONID = "com.loveLife.loveLife.PrivateSession.FCMRegistrationId";

    /**
     * constructor to initialize the shared preferances.
     *
     * @param context
     */
    public MySharedData(Context context) {
        loveLifeSessionPrefreneces = context.getSharedPreferences(LOVELIFE_PRIVATE_PREF, 0);
    }

    /**
     * set data  in shared preferences
     */
    public void setGeneralSaveSession(String key, String value) {
        SharedPreferences.Editor editor = loveLifeSessionPrefreneces.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * get  data  from shared preferences
     */
    public String getGeneralSaveSession(String key) {
        String value = loveLifeSessionPrefreneces.getString(key, "");
        return value;
    }

    /**
     * Remove data from shared preferences.
     */
    public void removeGeneralSaveSession(String key) {
        SharedPreferences.Editor editor = loveLifeSessionPrefreneces.edit();
        editor.remove(key);
        editor.commit();
    }


    /**
     * login user session
     */
    public void loginSession(UserLoginBeanClass userLoginBeanClass, MySharedData mySharedData) {

        String[] spitEmail = userLoginBeanClass.getUserEmail().split("@");
        mySharedData.setGeneralSaveSession(openfireUser, spitEmail[0]);

        mySharedData.setGeneralSaveSession(USERNAME, userLoginBeanClass.getUserName());
        mySharedData.setGeneralSaveSession(USEREMAIL, userLoginBeanClass.getUserEmail());
        mySharedData.setGeneralSaveSession(USERID, userLoginBeanClass.getUserId());
        mySharedData.setGeneralSaveSession(USERMOBILE, userLoginBeanClass.getUserMobile());
        mySharedData.setGeneralSaveSession(TOKEN, userLoginBeanClass.getUserToken());
        mySharedData.setGeneralSaveSession(FBTOKEN, userLoginBeanClass.getFbToken());
        mySharedData.setGeneralSaveSession(USER_PROFILE_PIC, userLoginBeanClass.getProfilePic());
        //  mySharedData.setGeneralSaveSession(USER_PASSMD5, passMD5);
        mySharedData.setGeneralSaveSession(CONFIRM_EMAIL, userLoginBeanClass.getEmailConfirmStatus());
        mySharedData.setGeneralSaveSession(USER_GENDER, userLoginBeanClass.getUserGender());
        mySharedData.setGeneralSaveSession(USER_DOB, userLoginBeanClass.getUserDOB());
    }

    /**
     * connected partner session
     */

    public void connectedPartnerSession(ConnectedPartnerBean connectedPartnerBean, MySharedData mySharedData) {
        System.out.println(" dd1 "+connectedPartnerBean.getPartnerDisplayName());
        System.out.println(" dd2 "+connectedPartnerBean.getPartnerGender());
        System.out.println(" dd3 "+connectedPartnerBean.getPartnerProfilePic());
        System.out.println(" dd4 "+connectedPartnerBean.getPartnerID());
        System.out.println(" dd5 "+connectedPartnerBean.getPartnerEmail());
        System.out.println(" dd6 "+connectedPartnerBean.getPartnerConnectionID());
        System.out.println(" dd7 "+connectedPartnerBean.getPartnerEmail());

        mySharedData.setGeneralSaveSession(CONNECTED_PARNER_DISPLAY_NAME, connectedPartnerBean.getPartnerDisplayName());
        mySharedData.setGeneralSaveSession(PARTNER_PROFILEPIC, connectedPartnerBean.getPartnerProfilePic());
        mySharedData.setGeneralSaveSession(CONNECTED_PARNER_GENDER, connectedPartnerBean.getPartnerGender());
        mySharedData.setGeneralSaveSession(CONNECTED_PARNER_ID, connectedPartnerBean.getPartnerID());
        mySharedData.setGeneralSaveSession(CONNECTED_PARNER_EMAIL, connectedPartnerBean.getPartnerEmail());
        mySharedData.setGeneralSaveSession(CONNECTED_PARNER_CONNECTION_ID, connectedPartnerBean.getPartnerConnectionID());
        mySharedData.setGeneralSaveSession(isConnectedToPartner, "yes");

        String[] spitEmail = connectedPartnerBean.getPartnerEmail().split("@");
        mySharedData.setGeneralSaveSession(openfireConnectedPartner, spitEmail[0]);

    }


    /**
     * remove connected partner session
     */
    public void removeConnectedPartnerSession(MySharedData mySharedData) {
        mySharedData.removeGeneralSaveSession(CONNECTED_PARNER_DISPLAY_NAME);
        mySharedData.removeGeneralSaveSession(CONNECTED_PARNER_GENDER);
        mySharedData.removeGeneralSaveSession(CONNECTED_PARNER_ID);
        mySharedData.removeGeneralSaveSession(CONNECTED_PARNER_EMAIL);
        mySharedData.removeGeneralSaveSession(CONNECTED_PARNER_CONNECTION_ID);
        mySharedData.removeGeneralSaveSession(openfireConnectedPartner);
        mySharedData.removeGeneralSaveSession(isConnectedToPartner);
    }



    /**
     * login user session
     */
    public void logoutSession(MySharedData mySharedData) {
        mySharedData.removeGeneralSaveSession(USERNAME);
        mySharedData.removeGeneralSaveSession(USEREMAIL);
        mySharedData.removeGeneralSaveSession(USERID);
        mySharedData.removeGeneralSaveSession(USERMOBILE);
        mySharedData.removeGeneralSaveSession(TOKEN);
        mySharedData.removeGeneralSaveSession(FBTOKEN);
        mySharedData.removeGeneralSaveSession(openfireUser);
        // mySharedData.removeGeneralSaveSession(openFirePass);
        mySharedData.removeGeneralSaveSession(CONNECTED_PARNER_CONNECTION_ID);
        mySharedData.removeGeneralSaveSession(CONNECTED_PARNER_EMAIL);
        mySharedData.removeGeneralSaveSession(USER_PROFILE_PIC);
        // mySharedData.removeGeneralSaveSession(USER_PASSMD5);
        mySharedData.removeGeneralSaveSession(CONFIRM_EMAIL);
        mySharedData.removeGeneralSaveSession(COMPLETE_LOGGGED_IN);
        mySharedData.removeGeneralSaveSession(USER_GENDER);
        mySharedData.removeGeneralSaveSession(USER_DOB);
        mySharedData.removeGeneralSaveSession(FIRST_GETUSERPROFILE_UPDATE);
        mySharedData.removeGeneralSaveSession(FIRST_ADDRESSANDFAVORITES_UPDATE);
        mySharedData.removeGeneralSaveSession(saveProfileImage);

        removeConnectedPartnerSession(mySharedData);

    }

}
