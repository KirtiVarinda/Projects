package com.lovelife.lovelife;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.widget.AutoCompleteTextView;

import com.lovelife.lovelife.BeanClasses.UserLoginBeanClass;
import com.lovelife.lovelife.JsonParsing.ParseJsonData;
import com.lovelife.lovelife.LoveLifeUtility.DataEncription;
import com.lovelife.lovelife.LoveLifeUtility.PairValues;
import com.lovelife.lovelife.LoveLifeUtility.ProgressBarManager;
import com.lovelife.lovelife.LoveLifeUtility.SwitchActivities;
import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.Popups.LoveLifePopups;
import com.lovelife.lovelife.SharedData.MySharedData;
import com.lovelife.lovelife.Validator.FormValidation;

import java.util.Map;

public class RegisterActivity extends Activity {

    /**
     * session variable
     */
    MySharedData mySharedData;

    /**
     * Parameter String keys
     */
    private static String FIRST_NAME = "first_name";
    private static String LAST_NAME = "last_name";
    public static String EMAIL = "email";              // variable is public because it is used in another activity for getting put extra value.
    public static String PASSWORD = "password";       // variable is public because it is used in another activity for getting put extra value.
    private static String MOBILE = "mobile";
    private static String SOCIAL_LOGIN = "social_id";
    private static String FBTOKEN = "fb_token";

    public static String GENDER = "gender";              // variable is public because it is used in another activity for getting put extra value.
    public static String BIRTHDAY = "birthday";
    public static String LOCATION = "location";              // variable is public because it is used in another activity for getting put extra value.
    public static String PROFILE_PIC = "profile_pic";
    public static String TRANSITION_FROM = "transition_from";

    /**
     * variable for activity switcher (to open another activity)
     */

    SwitchActivities switchActivity;
    String firstName;
    public String md5password;
    /**
     * form field data variables.
     */
    private AutoCompleteTextView mFirstName;
    private AutoCompleteTextView mLastName;
    private AutoCompleteTextView mEmail;
    private AutoCompleteTextView mMobile;
    private AutoCompleteTextView mPassword;
    private AutoCompleteTextView mRetypePassword;


    /**
     * loaded variable
     */
    private ProgressBarManager progressBarManager;

    /**
     * current activity instance
     */
    private RegisterActivity currentActivity;


    /**
     * Register user on Url
     */
    private String REGISTRATION_URL;


    /**
     * get data
     */

    private String fbFirstName, lastName, email, gender, birthday, location, profilePic, token, social_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemes);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        /**
         * set background image
         */
        getWindow().setBackgroundDrawableResource(R.drawable.love) ;

        /** get data that came from previous activity*/

        Bundle extras = getIntent().getExtras();
        social_login = extras.getString("social_login");

        if (social_login.equals("true")) {
            fbFirstName = extras.getString("firstName");
            lastName = extras.getString("lastName");
            email = extras.getString("email");
            gender = extras.getString("gender");
            birthday = extras.getString("birthday");
            location = extras.getString("location");
            profilePic = extras.getString("profile_pic");
            token = extras.getString("token");


        } else {
            fbFirstName = "";
            lastName = "";
            email = "";
            gender = "";
            birthday = "";
            location = "";
            profilePic = "";
            token = "";
        }


        /**
         * handle floating button here.
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                /**attempted Registration when float button is clicked. */
                attemptRegistration();
            }

        });

        /**    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
         this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
         drawer.setDrawerListener(toggle);
         toggle.syncState();*/
        /** NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
         navigationView.setNavigationItemSelectedListener(this);*/


        /** initialize current ativity */
        currentActivity = RegisterActivity.this;


        /** initialize progressbar object */
        progressBarManager = new ProgressBarManager(currentActivity);

        /** initialize session variable */
        mySharedData = new MySharedData(getApplicationContext());


        /**
         * initializing the form variables
         */

        mFirstName = (AutoCompleteTextView) findViewById(R.id.regFirstName);
        mLastName = (AutoCompleteTextView) findViewById(R.id.regLastName);
        mEmail = (AutoCompleteTextView) findViewById(R.id.regEmail);
        mMobile = (AutoCompleteTextView) findViewById(R.id.regMobile);
        mPassword = (AutoCompleteTextView) findViewById(R.id.regPassword);
        mRetypePassword = (AutoCompleteTextView) findViewById(R.id.regRetypePassword);


        mFirstName.setText(fbFirstName);
        mLastName.setText(lastName);
        mEmail.setText(email);

        if (social_login.equals("true")) {
            mPassword.setVisibility(View.GONE);
            mRetypePassword.setVisibility(View.GONE);
        } else {
            mPassword.setVisibility(View.VISIBLE);
            mRetypePassword.setVisibility(View.VISIBLE);
        }


        /**
         * initialize activity switcher
         */
        switchActivity = new SwitchActivities();


        /** set url for user registration */
        REGISTRATION_URL = getResources().getString(R.string.serverDomain) + getResources().getString(R.string.userRegistration);


    }


    /**
     * Attempts to register the account specified by the registration form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual registration attempt is made.
     */
    private void attemptRegistration() {

           /*   // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);*/

        // Store values at the time of the login attempt.
        firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        final String email = mEmail.getText().toString();
        String mobile = mMobile.getText().toString();
        String password = mPassword.getText().toString();
        String retypePassword = mRetypePassword.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid First Name.
        if (TextUtils.isEmpty(firstName)) {
            mFirstName.setError(getString(R.string.invalid_firstName));
            focusView = mFirstName;
            cancel = true;
        } else if (TextUtils.isEmpty(lastName)) {
            mLastName.setError(getString(R.string.invalid_lastName));
            focusView = mLastName;
            cancel = true;
        } else if (!FormValidation.isValidEmail(email)) {
            mEmail.setError(getString(R.string.invalid_Email));
            focusView = mEmail;
            cancel = true;
        } else if (!FormValidation.isValidPhoneNumber(mobile)) {
            mMobile.setError(getString(R.string.invalid_mobile));
            focusView = mMobile;
            cancel = true;
        } else if (!FormValidation.isValidPassword(password) && !social_login.equals("true")) {
            mPassword.setError(getString(R.string.invalid_password));
            focusView = mPassword;
            cancel = true;
        } else if (!isRetypePasswordValid(password, retypePassword)) {
            mRetypePassword.setError(getString(R.string.invalid_retypePassword));
            focusView = mRetypePassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            /** start progress bar */
            progressBarManager.startProgressBar();


            /** Encrypt password to MD5 */

            final String md5password = DataEncription.encriptToMD5(password);

            final Map<String, String> postData = getKeyPairValueInHashMap(firstName, lastName, email, mobile, md5password);


            /** register user with collected data on different thread to server */
            new Thread() {
                public void run() {
                    registerUser(postData, email, md5password);

                }
            }.start();

        }
    }


    /**
     * Method that create key value pair for sending dta in post
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMap(String firstName, String lastName, String email, String mobile, String MD5Password) {

        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */


        System.out.println("firstName " + firstName);
        System.out.println("lastName " + lastName);
        System.out.println("email " + email);
        System.out.println("mobile " + mobile);
        System.out.println("MD5Password " + MD5Password);
        System.out.println("social_login " + social_login);
        System.out.println("token " + token);
        System.out.println("resgiseter ffcm id " +  mySharedData.getGeneralSaveSession(MySharedData.FCM_REGISTRATIONID));

        String key[] = {FIRST_NAME, LAST_NAME, EMAIL, MOBILE, PASSWORD, SOCIAL_LOGIN, FBTOKEN,LoveLifeApplication.FCM_REGISTERATION_ID};      // set keys
        String value[] = {firstName, lastName, email, mobile, MD5Password, social_login, token,mySharedData.getGeneralSaveSession(MySharedData.FCM_REGISTRATIONID)};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }


    /**
     * Register user on server
     *
     * @param postData contains the post data that would be send to server.
     */
    private void registerUser(Map<String, String> postData, String email, String MD5Password) {


        /** Register user on server */
        ServerSync serverSync = new ServerSync();

        String reponse = serverSync.SyncServer(postData, REGISTRATION_URL);

        System.out.println("response " + reponse);


        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {/** If network error occurs */

            /**
             * if there is a network problem while registering user on server.
             * them shows the network error.
             */

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    popup("Network problem.Please check your internet connectivity.", "Sorry!");
                    // Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_LONG).show();
                }
            });


        } else {

            String status = ParseJsonData.checkStatus(reponse);
            String message = ParseJsonData.getMessage(reponse);

            if (status.equals(ParseJsonData.STATUS_SUCCESS)) {/** if response status is success then registration successful. */

                //  popup(message, "Congratulations!");


                /** parse the response and retrieve the bean class that contains
                 * parsed data of successful response.
                 * Set data from the bean class into the session
                 * Then make login and open the app door for the user
                 *but first time will get the profile detail.
                 */
                UserLoginBeanClass userLoginBeanClass = ParseJsonData.parseUserLoginJson(reponse);
//                mySharedData.loginSession(userLoginBeanClass.getUserName(), userLoginBeanClass.getUserEmail(), userLoginBeanClass.getUserId(), userLoginBeanClass.getUserMobile(), userLoginBeanClass.getUserToken(), userLoginBeanClass.getFbToken(),userLoginBeanClass.getProfilePic() ,MD5Password, mySharedData);


                /**
                 * open profile page to get the user profile data if registration is successful
                 */
                switchActivity.openActivityWithPutExtra(currentActivity, GetUserProfile.class, new String[]{EMAIL, PASSWORD, GENDER, BIRTHDAY, LOCATION, PROFILE_PIC,TRANSITION_FROM}, new String[]{email, MD5Password, gender, birthday, location, profilePic,"registration"});
                progressBarManager.stopProgressBar();


                /**
                 *Create account of registered user on open fire or XMPP
                 */

                String[] spitEmail = email.split("@");

                mySharedData.setGeneralSaveSession(MySharedData.openfireUser, spitEmail[0]);


                finish();


            } else if (status.equals(ParseJsonData.STATUS_FAIL)) { /** if response status Fail. */

                popup(message, "Sorry!");

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        popup("Server problem please try after some time.", "Sorry!");

                    }
                });

            }


        }


    }

    /**
     * Pop for registration activity.
     *
     * @param message
     * @param titleText
     */
    private void popup(final String message, final String titleText) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /** Stop progress bar */
                progressBarManager.stopProgressBar();
                LoveLifePopups.loveLifePopup(RegisterActivity.this, titleText, message);
            }
        });


    }


    /**
     * Check if password is valid entered by user.
     *
     * @param password
     * @return
     */
    private boolean isRetypePasswordValid(String password, String retypePassword) {
        //TODO: Replace this with your own logic
        return password.equals(retypePassword);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
