package com.dx.dataapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dx on 4/4/2015.
 */


public class SessionData {

    public final String privateNetworkStatus = "com.designersx.getfixerdataApp.networkstatus";
    public final static String privateLoginCheck="com.designersx.getfixerdataApp.privateLoginCheck";
    public final static String privateUserType="com.designersx.getfixerdataApp.privateUserType";
    public final static String privateUserName="com.designersx.getfixerdataApp.privateUserName";
    public final static String privateDataLongitude="com.designersx.getfixerdataApp.privateDataLongitude";
    public final static String privateDataLatitude="com.designersx.getfixerdataApp.privateDataLatitude";
    public final static String FixrNumberXmlTime="com.designersx.getfixerdataApp.FixrNumberXmlTime";
    public final String privateOwnerId="com.designersx.getfixerdataApp.privateOwnerId";
    public final String privateOwnerEmail="com.designersx.getfixerdataApp.privateOwnerEmail";
    public final String privateOwnerPhone="com.designersx.getfixerdataApp.privateOwnerPhone";

    static SharedPreferences GetFixerSessionDataApp;
    public final String privatePref = "com.designersx.getfixerdataApp.privatesession";

    public SessionData(Context context) {
        GetFixerSessionDataApp = context.getSharedPreferences(privatePref, 0);
    }
    protected void setNetworkStatus(String status) {
        SharedPreferences.Editor editor = GetFixerSessionDataApp.edit();
        editor.putString(privateNetworkStatus, status);
        // Commit the edits!
        editor.commit();
    }


    /*
   * get the networkState from shared preferences
   *
   * */
    protected String getNetworkStatus() {
        String network = GetFixerSessionDataApp.getString(privateNetworkStatus, "");
        return network;
    }


    /*
   * set setOwnerNumber  in shared preferences
   *
   * */
    public void setGeneralSaveSession(String key, String value) {
        SharedPreferences.Editor editor = GetFixerSessionDataApp.edit();
        editor.putString(key, value);
        // Commit the edits!
        editor.commit();
    }


    /*
     * get   getOwnerName from shared preferences
     *
     * */
    public String getGeneralSaveSession(String key) {
        String value = GetFixerSessionDataApp.getString(key, "");
        return value;
    }


}

