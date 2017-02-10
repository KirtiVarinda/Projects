package com.lovelife.lovelife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.lovelife.lovelife.BeanClasses.BeanForFacebookData;
import com.lovelife.lovelife.BeanClasses.ConnectedPartnerBean;
import com.lovelife.lovelife.BeanClasses.UserLoginBeanClass;
import com.lovelife.lovelife.CustomEditText.BackAwareEditText;
import com.lovelife.lovelife.JsonParsing.ParseJsonData;
import com.lovelife.lovelife.LoveChat.InternetCheck;
import com.lovelife.lovelife.LoveLifeUtility.DataEncription;
import com.lovelife.lovelife.LoveLifeUtility.PairValues;
import com.lovelife.lovelife.LoveLifeUtility.ProgressBarManager;
import com.lovelife.lovelife.LoveLifeUtility.SwitchActivities;
import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.Popups.LoveLifePopups;
import com.lovelife.lovelife.SharedData.MySharedData;
import com.lovelife.lovelife.StringResources.LoginResource;
import com.lovelife.lovelife.Validator.FormValidation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    public static boolean checks = false;

    boolean forget = false;
    CallbackManager callbackManager;
    private LoginButton loginButton;
    /**
     * bean to store fb data
     */
    BeanForFacebookData bFacebookData;

    /**
     * variable to hide login functionality
     */
    TextInputLayout emailOuter, passwordOuter;

    /**
     * variable for activity switcher
     */

    SwitchActivities switchActivities;

    /**
     * session variable
     */
    MySharedData mySharedData;

    /**
     * current activity instance
     */
    private LoginActivity currentActivity;

    /**
     * loaded variable
     */
    private ProgressBarManager progressBarManager;

    /**
     * Register user on Url
     */
    private String LOGIN_URL, FB_LOGIN,CONFORM_EMAIL_URL;


    /**
     * Parameter String keys
     */
    private String EMAIL = "email";
    private String PASSWORD = "password";
    private String FBTOKEN = "fb_token";

    /**
     * variable for bottom up animation
     */
    Animation bottomUp,slideUp;
    static Animation imageDown,imageUp,slideDown ;

    Button mEmailSignInButton;
    static FrameLayout passwordSignInButton;

    // UI references.
    static private BackAwareEditText mEmailView;
    static private BackAwareEditText mPasswordView;

    static LinearLayout animImage, dummy,loginArea,buttonArea;


    private String password;

    /**
     * Variable for Forget PAssword
     */

    TextView forgetPass;


    static BackAwareEditText hidden;
    ProgressBar loader;
    TextView networkErrorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        // Set up the login form.


        /***
         * Inisialize forget Password variable
         */
        forgetPass = (TextView) findViewById(R.id.forget);

        /**
         * Onclick Listener FOr Forget PAssword TextView.
         */

        forgetPass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               forgetPassword();
            }
        });




        /** initialize variables for hide layout */
        emailOuter = (TextInputLayout) findViewById(R.id.emailOut);
        passwordOuter = (TextInputLayout) findViewById(R.id.passwordOut);
        animImage = (LinearLayout) findViewById(R.id.anim);
        dummy = (LinearLayout) findViewById(R.id.dummy);
        loginArea = (LinearLayout) findViewById(R.id.loginArea);
        buttonArea = (LinearLayout) findViewById(R.id.buttonArea);
        hidden = (BackAwareEditText) findViewById(R.id.hidden);
        loader = (ProgressBar) findViewById(R.id.loader);
        networkErrorText = (TextView) findViewById(R.id.networkError);


        inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);


        /** initialize animation */
        bottomUp = AnimationUtils.loadAnimation(getApplicationContext(),             R.anim.bottom_up);
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(),             R.anim.slideup);
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(),             R.anim.slidedown);

        imageDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slidedown);
        imageUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.image_up);



        /** initialize current ativity */
        currentActivity = LoginActivity.this;


        /** initialize session variable */
        mySharedData = new MySharedData(getApplicationContext());

        /** set url for user Login */
        LOGIN_URL = getResources().getString(R.string.serverDomain) + getResources().getString(R.string.userLogin);
        CONFORM_EMAIL_URL = "http://lovelife.aicoach.net/forgotPassword";
        /** initialize progressbar object */
        progressBarManager = new ProgressBarManager(currentActivity);


        if (InternetCheck.netCheck(this)) {     /** it will run when internet is connected and within the range. */

            mySharedData.setGeneralSaveSession(MySharedData.CHECKNET, "true");

        } else {           // it will run when internet is connected but out of range.

            mySharedData.setGeneralSaveSession(MySharedData.CHECKNET, "false");
        }


        /**
         * Initialize activity switcher
         */
        switchActivities = new SwitchActivities();

        System.out.println("sesssion  token" + mySharedData.getGeneralSaveSession(MySharedData.USERID));

        /** thread that checks if already login then opens main page
         * otherwise shows button on profilePicView */


        if (mySharedData.getGeneralSaveSession(MySharedData.TOKEN).equals("")) {

            new Thread() {
                public void run() {

                    try {
                        Thread.sleep(3 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            initResources();
                            loginWithFacebook();

                            /** animate buttons from below to up while making them visible */
                            loader.setVisibility(View.GONE);
                            networkErrorText.setVisibility(View.GONE);
                            passwordOuter.setVisibility(View.VISIBLE);
                            //signUp.setVisibility(View.VISIBLE);
                            emailOuter.setVisibility(View.VISIBLE);
                            mEmailSignInButton.setVisibility(View.VISIBLE);
                            passwordSignInButton.setVisibility(View.VISIBLE);
                            forgetPass.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }.start();
        } else {

            if (mySharedData.getGeneralSaveSession(MySharedData.COMPLETE_LOGGGED_IN).equals("yes")) {

                System.out.println("ccccccccc");
                new Thread() {
                    public void run() {

                        try {
                            Thread.sleep(3 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        switchActivities.openActivity(LoginActivity.this, Dashboard.class);
                        finish();
                    }
                }.start();

            } else {


                final Map<String, String> postData1 = getKeyPairValueInHashMap(mySharedData.getGeneralSaveSession(MySharedData.USEREMAIL), mySharedData.getGeneralSaveSession(MySharedData.TOKEN));


                /** register user with collected data on different thread to server */
                new Thread() {
                    public void run() {
                        registerUser(postData1, true);

                    }
                }.start();

/*
                if (mySharedData.getGeneralSaveSession(MySharedData.CONNECTED_PARNER_EMAIL).equals("")) {
                    switchActivities.openActivity(LoginActivity.this, GetUserProfile.class);
                } else {
                    switchActivities.openActivity(LoginActivity.this, GetUserProfile.class);
                }*/

            }
        }
    }

    public void forgetPassword(){
        if (animImage.getVisibility() == View.VISIBLE) {
            sildeloginAreaUp();
            ifKeyBoardOpen = true;
        }
        passwordOuter.setVisibility(View.GONE);
        forgetPass.setVisibility(View.GONE);
        mEmailSignInButton.setText("");
        mEmailSignInButton.setText("Confirm Email");
        mEmailSignInButton.setTextColor(Color.WHITE);
        forget = true;

    }


    private void initResources() {
        mEmailView = (BackAwareEditText) findViewById(R.id.email);
        mPasswordView = (BackAwareEditText) findViewById(R.id.password);

        mEmailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (animImage.getVisibility() == View.VISIBLE) {
                    sildeloginAreaUp();
                    ifKeyBoardOpen = true;
                }
            }
        });


        mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (animImage.getVisibility() == View.VISIBLE) {
                    sildeloginAreaUp();
                    ifKeyBoardOpen = true;
                }
            }
        });


        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        /** initialize facebook button */
        passwordSignInButton = (FrameLayout) findViewById(R.id.facebook_signin);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(forget == true){

                    changePassword();

                }else{
                    attemptLogin();
                }

            }
        });


    }

    public void changePassword(){


        // Reset errors.
        mEmailView.setError(null);


        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        else if (!TextUtils.isEmpty(email)) {

            if (!FormValidation.isValidEmail(email)) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                cancel = true;
            }else{

                /** start progress bar */
                progressBarManager.startProgressBar();



                final Map<String, String> postData = getKeyPairValueInHashMapForConfirmEmail(email);


                /** register user with collected data on different thread to server */
                new Thread() {
                    public void run() {
                        ChangePass(postData, false);

                    }
                }.start();




            }
        }




        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.


            //  Toast.makeText(currentActivity,"ifKeyBoardOpen "+ifKeyBoardOpen,Toast.LENGTH_LONG).show();
            if (!ifKeyBoardOpen) {
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                ifKeyBoardOpen = true;
            }

            focusView.requestFocus();

        }




            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);
     /*       *//** start progress bar *//*
            progressBarManager.startProgressBar();


            *//** Encrypt password to MD5 *//*

            password = DataEncription.encriptToMD5(password);

            final Map<String, String> postData = getKeyPairValueInHashMap(email, password);


            *//** register user with collected data on different thread to server *//*
            new Thread() {
                public void run() {
                    registerUser(postData, false);

                }
            }.start();*/




    }


    private void ChangePass(Map<String, String> postData, final boolean isCallFromSplash) {


        /** Register user on server */
        ServerSync serverSync = new ServerSync();

        final String reponse = serverSync.SyncServer(postData, CONFORM_EMAIL_URL);

        System.out.println("responseaaaassss " + reponse);


        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {/** If network error occurs */

            /**
             * if there is a network problem while registering user on server.
             * them shows the network error.
             */


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isCallFromSplash) {
                        loader.setVisibility(View.GONE);
                        networkErrorText.setVisibility(View.VISIBLE);
                    } else {
                        popup("Network problem. Please check your internet connectivity.", "Sorry!");
                    }
                }
            });


        } else {

            String status = ParseJsonData.checkStatus(reponse);
            final String message = ParseJsonData.getMessage(reponse);

            if (status.equals("success")) {/** if response status is success then login successful. */
                progressBarManager.stopProgressBar();
//                Intent i = new Intent(LoginActivity.this,ForgetPassword.class);
//                startActivity(i);
                LoveLifePopups.loveLifePopupWithFinishCurrentFavt(LoginActivity.this, "", " Please Check your email for Change Password");
//                Toast.makeText(this, "Please Check your email for Change Password", Toast.LENGTH_LONG).show();

                 /*     if(email.equals("aaa@gmail.com")){
                    Intent i = new Intent(LoginActivity.this,ForgetPassword.class);
                    startActivity(i);
                    Toast.makeText(currentActivity,"next "+ifKeyBoardOpen,Toast.LENGTH_LONG).show();
                }else{
                    mEmailView.setError(getString(R.string.no_exist_email));
                    focusView = mEmailView;
                    cancel = true;
                }*/

            } else if (status.equals(ParseJsonData.STATUS_FAIL)) { /** if response status Fail. */


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        popup(message, "Sorry!");

                    }
                });


            } else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isCallFromSplash) {
                            loader.setVisibility(View.GONE);
                            networkErrorText.setVisibility(View.VISIBLE);
                        } else {
                            popup("Server problem please try after some time.", "Sorry!");
                        }
                    }
                });


            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    /** Stop progress bar */
                    progressBarManager.stopProgressBar();

                }
            });

        }
    }



    private void sildeloginAreaUp() {
        animImage.setVisibility(View.GONE);
        animImage.startAnimation(imageUp);
        buttonArea.startAnimation(slideUp);
        buttonArea.setVisibility(View.VISIBLE);
        loginArea.startAnimation(slideUp);
        loginArea.setVisibility(View.VISIBLE);
        dummy.setVisibility(View.VISIBLE);
        passwordSignInButton.setVisibility(View.INVISIBLE);
    }


    private void loginWithFacebook() {
        /** initialize login variable */
        FB_LOGIN = getResources().getString(R.string.serverDomain) + getResources().getString(R.string.fbLogin);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {


            @Override
            public void onSuccess(final LoginResult loginResult) {
                System.out.println("loginResult " + loginResult.getAccessToken().getToken());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        /** start progress bar */
                        progressBarManager.startProgressBar();

                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
                        bFacebookData = getFacebookData(object);
                        loginButton.setVisibility(View.GONE);

                        /** setToken in bean*/
                        bFacebookData.setToken(loginResult.getAccessToken().getToken());


                        if (bFacebookData != null) {

                            System.out.println("first_name " + bFacebookData.getFirstName());
                            System.out.println("last_name " + bFacebookData.getLastName());
                            System.out.println("email " + bFacebookData.getEmail());
                            System.out.println("gender " + bFacebookData.getGender());
                            System.out.println("birthday " + bFacebookData.getBirthday());
                            System.out.println("location " + bFacebookData.getLocation());


                            final Map<String, String> postData = getKeyPairValueInHashMapForFB(bFacebookData.getEmail(), bFacebookData.getToken());

                            /** check  user with collected data from facebook on different thread to server that user already exist or not */
                            new Thread() {
                                public void run() {
                                    checkUserAndLogin(postData);

                                }
                            }.start();

                        }


                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                System.out.print("onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                System.out.print("onError " + error);
            }
        });


    }


    /**
     * handle customize facebook button clicked event
     */
    public void onClickFacebook(View v) {

        loginButton.performClick();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
//        System.out.print("onCanonActivityResult = " + data.getData());
    }


    static boolean ifKeyBoardOpen = false;

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;



        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!FormValidation.isValidEmail(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        } // Check for a valid password, if the user entered one.
        else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        else if (!FormValidation.isValidPassword(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.


            //  Toast.makeText(currentActivity,"ifKeyBoardOpen "+ifKeyBoardOpen,Toast.LENGTH_LONG).show();
            if (!ifKeyBoardOpen) {
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                ifKeyBoardOpen = true;
            }

            focusView.requestFocus();

        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);
            /** start progress bar */
            progressBarManager.startProgressBar();


            /** Encrypt password to MD5 */

            password = DataEncription.encriptToMD5(password);

            final Map<String, String> postData = getKeyPairValueInHashMap(email, password);


            /** register user with collected data on different thread to server */
            new Thread() {
                public void run() {
                    registerUser(postData, false);

                }
            }.start();
        }
    }


    /**
     * Method that create key value pair for sending dta in post
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMap(String email, String MD5Password) {

        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */


        System.out.println("Fcm ID at login " + mySharedData.getGeneralSaveSession(MySharedData.FCM_REGISTRATIONID));
        String key[] = {EMAIL, PASSWORD, LoveLifeApplication.FCM_REGISTERATION_ID};      // set keys
        String value[] = {email, MD5Password, mySharedData.getGeneralSaveSession(MySharedData.FCM_REGISTRATIONID)};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }


    /**
     * Method that create key value pair for conform Email
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMapForConfirmEmail(String email) {

        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */

        String key[] = {EMAIL};      // set keys
        String value[] = {email};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }



    /**
     * Method that create key value pair for sending dta in post
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMapForFB(String email, String fbToken) {

        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */


        System.out.println("splash ffcm id " + mySharedData.getGeneralSaveSession(MySharedData.FCM_REGISTRATIONID));
        String key[] = {EMAIL, FBTOKEN, LoveLifeApplication.FCM_REGISTERATION_ID};      // set keys
        String value[] = {email, fbToken, mySharedData.getGeneralSaveSession(MySharedData.FCM_REGISTRATIONID)};                //set values


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
    private void registerUser(Map<String, String> postData, final boolean isCallFromSplash) {


        /** Register user on server */
        ServerSync serverSync = new ServerSync();

        final String reponse = serverSync.SyncServer(postData, LOGIN_URL);

        System.out.println("response " + reponse);


        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {/** If network error occurs */

            /**
             * if there is a network problem while registering user on server.
             * them shows the network error.
             */


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isCallFromSplash) {
                        loader.setVisibility(View.GONE);
                        networkErrorText.setVisibility(View.VISIBLE);
                    } else {
                        popup("Network problem. Please check your internet connectivity.", "Sorry!");
                    }
                }
            });


        } else {

            String status = ParseJsonData.checkStatus(reponse);
            final String message = ParseJsonData.getMessage(reponse);

            if (status.equals(ParseJsonData.LOGIN_STATUS_SUCCESS)) {/** if response status is success then login successful. */

                /** parse the response and retrieve the bean class that contains
                 * parsed data of successful response.
                 * Set data from the bean class into the session
                 * Then make login and open the app door for the user
                 *
                 */
                UserLoginBeanClass userLoginBeanClass = setLoginSession(reponse);


                if (isCallFromSplash || userLoginBeanClass.getEmailConfirmStatus().equals("0") || userLoginBeanClass.getUserName().equals("")) { //check if user login or self login
                    switchActivities.loginActivityTransitionWithData(currentActivity, GetUserProfile.class, new String[]{LoginResource.LOGIN_DATA}, new Serializable[]{userLoginBeanClass});

                } else {
//                    Toast.makeText(LoginActivity.this, "afterresponse", Toast.LENGTH_LONG).show();
                    mySharedData.setGeneralSaveSession(MySharedData.FIRST_GETUSERPROFILE_UPDATE, "yes");
                    mySharedData.setGeneralSaveSession(MySharedData.FIRST_ADDRESSANDFAVORITES_UPDATE, "yes");
                    mySharedData.setGeneralSaveSession(MySharedData.COMPLETE_LOGGGED_IN, "yes");
                    switchActivities.openActivity(currentActivity, Dashboard.class);

                }
                finish();

            } else if (status.equals(ParseJsonData.REGISTERATION_STATUS_SUCCESS)) {

                /** parse the response and retrieve the bean class that contains
                 * parsed data of successful response.
                 * Set data from the bean class into the session
                 * Then make login and open the app door for the user
                 *
                 */
                setLoginSession(reponse);
                switchActivities.openActivityWithPutExtra(currentActivity, GetUserProfile.class, new String[]{LoginResource.TRANSITION_TYPE}, new String[]{LoginResource.SIMPLE_TRANSITION});
                finish();

            } else if (status.equals(ParseJsonData.STATUS_FAIL)) { /** if response status Fail. */


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      /*  if (isCallFromSplash) {
                            loader.setVisibility(View.GONE);
                            setSnackBar(message);
                            snackbar.show();
                        } else {*/
                        popup(message, "Sorry!");
                        // }
                    }
                });


            } else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isCallFromSplash) {
                            loader.setVisibility(View.GONE);
                            networkErrorText.setVisibility(View.VISIBLE);
                        } else {
                            popup("Server problem please try after some time.", "Sorry!");
                        }
                    }
                });


            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    /** Stop progress bar */
                    progressBarManager.stopProgressBar();

                }
            });

        }
    }

    /**
     * set login session when simple login or registeration
     */
    private UserLoginBeanClass setLoginSession(String response) {
        UserLoginBeanClass userLoginBeanClass = ParseJsonData.parseUserLoginJson(response);
        mySharedData.loginSession(userLoginBeanClass, mySharedData);

        /** create login session for partner if connected */
        if (userLoginBeanClass.getIsConnectedToPartner().equals("yes")) {
            ConnectedPartnerBean connectedPartnerBean = ParseJsonData.setConnectedPartnerJson(response, "partner");
            mySharedData.connectedPartnerSession(connectedPartnerBean, mySharedData);
        }

        return userLoginBeanClass;
    }


    /**
     * Pop for login activity.
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
                /** Stop progress bar */
                LoveLifePopups.loveLifePopup(LoginActivity.this, titleText, message);
            }
        });


    }


    private BeanForFacebookData getFacebookData(JSONObject object) {

        BeanForFacebookData beanForFacebookData = new BeanForFacebookData();


        String id = null;
        try {
            id = object.getString("id");


            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                beanForFacebookData.setProfile_pic(profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                beanForFacebookData.setProfile_pic("");

            }

            beanForFacebookData.setUserId(id);
            if (object.has("first_name")) {
                beanForFacebookData.setFirstName(object.getString("first_name"));
            }

            if (object.has("last_name")) {
                beanForFacebookData.setLastName(object.getString("last_name"));
            }

            if (object.has("email")) {
                beanForFacebookData.setEmail(object.getString("email"));
            }

            if (object.has("gender")) {
                beanForFacebookData.setGender(object.getString("gender"));
            }

            if (object.has("birthday")) {
                beanForFacebookData.setBirthday(object.getString("birthday"));
            }
            if (object.has("location")) {
                beanForFacebookData.setLocation(object.getJSONObject("location").getString("name"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return beanForFacebookData;
    }

    /**
     * check if user already exit with fb data
     * otherwise goto registered page to register with fb data
     */
    private void checkUserAndLogin(Map<String, String> postData) {
        /** Register user on server */
        ServerSync serverSync = new ServerSync();

        final String reponse = serverSync.SyncServer(postData, FB_LOGIN);

        System.out.println("responsefb " + reponse);


        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {/** If network error occurs */

            /**
             * if there is a network problem while registering user on server.
             * them shows the network error.
             */

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    popup("Network problem.Please check your internet connectivity. " + reponse, "Sorry!");
//                    loginButton.setVisibility(View.VISIBLE);
                    // Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_LONG).show();
                }
            });

            /** ougout from facebook Api also */
            if (AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null) {


                LoginManager.getInstance().logOut();


            }
        } else {

            String status = ParseJsonData.checkStatus(reponse);

            if (status.equals(ParseJsonData.FB_LOGIN_STATUS_SUCCESS)) {/** if response status is success then login successful. */

                /** parse the response and retrieve the bean class that contains
                 * parsed data of successful response.
                 * Set data from the bean class into the session
                 * Then make login and open the app door for the user
                 *
                 */
                setLoginSession(reponse);

                /** Stop progress bar */
                progressBarManager.stopProgressBar();
                finish();

                switchActivities.openActivity(currentActivity, GetUserProfile.class);

                // popup(message, "Congratulations!");


            } else if (status.equals(ParseJsonData.FB_REGISTERATION_STATUS_SUCCESS)) {
                /** parse the response and retrieve the bean class that contains
                 * parsed data of successful response.
                 * Set data from the bean class into the session
                 * Then make login and open the app door for the user
                 *
                 */
                setLoginSession(reponse);

                /** Stop progress bar */
                progressBarManager.stopProgressBar();
                switchActivities.socialActivityTransitionFromRegistration(currentActivity, GetUserProfile.class, new String[]{LoginResource.FACEBOOK_DATA}, new Serializable[]{bFacebookData});
                finish();


            }/*else if (status.equals(ParseJsonData.STATUS_FAIL)) { *//** if response status Fail. *//*

                *//** open registration activity if user not exist *//*
                switchActivities.openActivityWithPutExtra(currentActivity, RegisterActivity.class, new String[]{"firstName", "lastName", "email", "gender", "birthday", "location", "profile_pic", "token", "social_login"},
                        new String[]{bFacebookData.getFirstName(), bFacebookData.getLastName(), bFacebookData.getEmail(), bFacebookData.getGender(), bFacebookData.getBirthday(), bFacebookData.getLocation(), bFacebookData.getProfile_pic(), bFacebookData.getToken(), "true"});
                *//** Stop progress bar *//*
                progressBarManager.stopProgressBar();
                finish();
            } */
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        popup("Server problem please try after some time.", "Sorry!");

                    }
                });

            }
        }

    }


    public void rootviewClicked(View view) {

        if (networkErrorText.getVisibility() == View.VISIBLE) {
            loader.setVisibility(View.VISIBLE);
            networkErrorText.setVisibility(View.GONE);
            final Map<String, String> postData = getKeyPairValueInHashMap(mySharedData.getGeneralSaveSession(MySharedData.USEREMAIL), mySharedData.getGeneralSaveSession(MySharedData.TOKEN));
            loader.setVisibility(View.VISIBLE);

            /** register user with collected data on different thread to server */
            new Thread() {
                public void run() {
                    registerUser(postData, true);

                }
            }.start();

        } else {
            if (animImage.getVisibility() == View.GONE) {
                hidden.requestFocus();

                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }

            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {


                            if (animImage.getVisibility() == View.GONE) {

                                slideDownLoginArea();
                                ifKeyBoardOpen = false;

                            }
                        }
                    });

                }
            }.start();


            // Toast.makeText(this, "root clicked", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {


            passwordOuter.setVisibility(View.VISIBLE);
            mEmailSignInButton.setVisibility(View.VISIBLE);
        mEmailSignInButton.setText("");
        mEmailSignInButton.setText("Login");
        forgetPass.setVisibility(View.VISIBLE);


    }

    InputMethodManager inputMethodManager;

    public static void keyPressed() {
        hidden.requestFocus();

        new Thread() {
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {


                        if (animImage.getVisibility() == View.GONE) {
                            slideDownLoginArea();
                            ifKeyBoardOpen = false;
                        }
                    }
                });

            }
        }.start();


    }

    private static void slideDownLoginArea() {
        animImage.setVisibility(View.VISIBLE);
        animImage.startAnimation(imageDown);
        loginArea.startAnimation(slideDown);
        loginArea.setVisibility(View.VISIBLE);
        buttonArea.startAnimation(slideDown);
        buttonArea.setVisibility(View.VISIBLE);
        dummy.setVisibility(View.GONE);
        passwordSignInButton.setVisibility(View.VISIBLE);
    }


}

