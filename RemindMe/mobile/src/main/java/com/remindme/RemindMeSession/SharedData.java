package com.remindme.RemindMeSession;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Dx on 12/19/2014.
 * set and get the networkState from shared preferences
 */
public class SharedData {
    public final String privatePref = "remindMessagePref";
    public static final String USEREMAIL="com_remindme_user_email_data";
    public static final String USERPHONE="com_remindme_user_mobile_data";
    public static final String USERDATASENDFORNUMBER="com_remindme_user_data_send_for_number";
    public static final String USERDATASENDFOREMAIL="com_remindme_user_data_send_for_email";
    public static final String REMINDER_MESSAGE="reminder_message";
    public static final String INTERNETCHECK="com_remindme_internet_check";
    public static final String SELECTEDWEEKDAY="com_remindme_selected_weekday";
    public static final String PRIVACYPOLICY="com_remindme_rpivacy_policy";

    public static final String CALL_CATCH ="com_remindme_callCatch";
    public static final String SMS_CATCH="com_remindme_smsCatch";
    public static final String DEFAULT_CALL_SMS_CHECK="com_remindme_smsCatchdefault";


    private SharedPreferences remindMeSession;

    public SharedData(Context context) {
    	remindMeSession = context.getSharedPreferences(privatePref, 0);
    }
 
 
    /*
   * get   getOwnerName from shared preferences
   *
   * */
    public String getGeneralSaveSession(String key) {
        String value = remindMeSession.getString(key, "");
        return value;
    }


    /*
     * set setOwnerNumber  in shared preferences
     *
     * */
    public void setGeneralSaveSession(String key,String value) {
        SharedPreferences.Editor editor = remindMeSession.edit();
        editor.putString(key, value);
        // Commit the edits!
        editor.commit();
    }


   

}
