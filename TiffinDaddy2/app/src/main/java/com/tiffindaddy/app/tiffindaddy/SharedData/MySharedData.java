package com.tiffindaddy.app.tiffindaddy.SharedData;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This class is used to save data.
 * Saved data is shared among all the activities.
 * Created by Dx on 7/20/2015.
 */
public class MySharedData {
    private static SharedPreferences tiffindaddySession;
    private final String TIFFINDADDY_PRIVATE_PREF = "com.tiffindaddy.app.sharedData";

    /** user logged in data*/
    public static String SESSION_USERID = "com.tiffindaddy.app.userID";
    public static String SESSION_USERNAME = "com.tiffindaddy.app.userName";
    public static String SESSION_USEREMAIL = "com.tiffindaddy.app.userEmail";
    public static String SESSION_USERPHONE = "com.tiffindaddy.app.userPhone";
    public static String SESSION_TOKEN = "com.tiffindaddy.app.token";


    /**
     * City data
     */
    public static String SESSION_CITY= "com.tiffindaddy.app.city";
    public static String SESSION_CITY_ID= "com.tiffindaddy.app.city_id";



    /**
     * constructor to initialize the shared preferances.
     *
     * @param context
     */
    public MySharedData(Context context) {
        tiffindaddySession = context.getSharedPreferences(TIFFINDADDY_PRIVATE_PREF, 0);
    }

    /**
     * set data  in shared preferences
     */
    public void setGeneralSaveSession(String key, String value) {
        SharedPreferences.Editor editor = tiffindaddySession.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * get  data  from shared preferences
     */
    public String getGeneralSaveSession(String key) {
        String value = tiffindaddySession.getString(key, "");
        return value;
    }

    /**
     * Remove data from shared preferences.
     */
    public void removeGeneralSaveSession(String key) {
        SharedPreferences.Editor editor = tiffindaddySession.edit();
        editor.remove(key);
        editor.commit();
    }
}
