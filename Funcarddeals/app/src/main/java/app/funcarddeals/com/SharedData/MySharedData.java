package app.funcarddeals.com.SharedData;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This class is used to save data.
 * Saved data is shared among all the activities.
 * Created by Dx on 7/20/2015.
 */
public class MySharedData {
    private static SharedPreferences FuncarddealsSession;
    private final String FUNCARD_PRIVATE_PREF = "app_funcarddeals_com_funcardPrivateSession";
    public static String LOGIN_USER_EMAIL = "app_funcarddeals_com_userLoginEmail";                              // login email.
    public static String LOGIN_USER_ENCRIPTED_MD5_PASSWORD = "app_funcarddeals_com_userMD5password";           // login passwords.
    public static String FUNCARDSERVER_USER_ID = "app_funcarddeals_com_userID";
    public static String FUNCARDSERVER_USER_NAME = "app_funcarddeals_com_username";

    /**
     * Variable for main page
     */
    public static String FUNCARD_CITY_JSON = "app_funcarddeals_com_cityjson";
    public static String FUNCARD_CITY_ID = "app_funcarddeals_com_cityID";

    /**
     * Variable for category page for categories
     */
    public static String FUNCARD_CITY_CATEGORY = "app_funcarddeals_com_categories";
    public static String FUNCARD_CITY_CATEGORY_ADS = "app_funcarddeals_com_categories_ads";


    /**
     * login activity.
     * variable for keep me logged in .
     */
    public static String FUNCARD_IS_KEEP_LOGGED_IN_EMAIL= "app_funcarddeals_com_keep_me_logged_in_email";
    public static String FUNCARD_IS_KEEP_LOGGED_IN_PASS= "app_funcarddeals_com_keep_me_logged_in_pass";

    /**
     * constructor to initialize the shared preferances.
     *
     * @param context
     */
    public MySharedData(Context context) {
        FuncarddealsSession = context.getSharedPreferences(FUNCARD_PRIVATE_PREF, 0);
    }

    /**
     * set data  in shared preferences
     */
    public void setGeneralSaveSession(String key, String value) {
        SharedPreferences.Editor editor = FuncarddealsSession.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * get  data  from shared preferences
     */
    public String getGeneralSaveSession(String key) {
        String value = FuncarddealsSession.getString(key, "");
        return value;
    }

    /**
     * Remove data from shared preferences.
     */
    public void removeGeneralSaveSession(String key) {
        SharedPreferences.Editor editor = FuncarddealsSession.edit();
        editor.remove(key);
        editor.commit();
    }
}
