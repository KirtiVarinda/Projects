package app.funcarddeals.com.Popups;

import android.content.Context;

import app.funcarddeals.com.R;

/**
 * Created by dx on 7/21/2015.
 */
public class PopupMessagesStrings {
    /**
     * Pop up messages for login page
     */
    public static String titleMessage;
    public static String invalidUserFromServerMessage;
    public static String networkError;
    public static String invalidUserFromLocalMessage;
    public static String userDisableFromservers;

    /**
     * pop up messages for registration page.
     */

    public static String regFullName;
    public static String regPhone,regLastName;
    public static String regZip;
    public static String regCity;
    public static String regState;
    public static String regEmail;
    public static String regPass;
    public static String regRetype_pass;
    public static String regpolicy_check;
    public static String regSuccess;
    public static String regUserAlreadyExist;
    public static String regSuccessTitle;
    /**
     * Method to initialize login page popup messages from string resource.
     *
     * @param ctx
     */
    public void PopupMessagesStringsForLogin(Context ctx) {

        titleMessage = ctx.getResources().getString(R.string.pop_title);
        invalidUserFromServerMessage = ctx.getResources().getString(R.string.pop_message_invalid_from_server);
        networkError = ctx.getResources().getString(R.string.network_error);
        invalidUserFromLocalMessage = ctx.getResources().getString(R.string.pop_message_invalid_from_local);
        userDisableFromservers = ctx.getResources().getString(R.string.pop_message_disable_user);

    }

    /**
     * Method to initialize login page popup messages from string resource.
     *
     * @param ctx
     */

    public void PopupMessagesStringsForRegistration(Context ctx) {
        titleMessage = ctx.getResources().getString(R.string.pop_title);
        regFullName = ctx.getResources().getString(R.string.reg_fullName);
        regLastName = ctx.getResources().getString(R.string.reg_lastName);
        regPhone = ctx.getResources().getString(R.string.reg_phone);
        regZip = ctx.getResources().getString(R.string.reg_zip);
        regCity = ctx.getResources().getString(R.string.reg_city);
        regState = ctx.getResources().getString(R.string.reg_state);
        regEmail = ctx.getResources().getString(R.string.reg_email);
        regPass = ctx.getResources().getString(R.string.reg_pass);
        regRetype_pass = ctx.getResources().getString(R.string.reg_re_pass);
        regpolicy_check = ctx.getResources().getString(R.string.reg_policy_check);
        regSuccess = ctx.getResources().getString(R.string.reg_success_message);
        regUserAlreadyExist = ctx.getResources().getString(R.string.reg_user_aleady_exist);
        regSuccessTitle= ctx.getResources().getString(R.string.reg_success_title);
    }





}
