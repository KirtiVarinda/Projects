package com.lovelife.lovelife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.lovelife.lovelife.BeanClasses.BeanForFacebookData;
import com.lovelife.lovelife.BeanClasses.UserLoginBeanClass;
import com.lovelife.lovelife.JsonParsing.ParseJsonData;
import com.lovelife.lovelife.LoveChat.InternetCheck;
import com.lovelife.lovelife.LoveLifeUtility.PairValues;
import com.lovelife.lovelife.LoveLifeUtility.ProgressBarManager;
import com.lovelife.lovelife.LoveLifeUtility.SwitchActivities;
import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.Popups.LoveLifePopups;
import com.lovelife.lovelife.SharedData.MySharedData;

import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class SplashScreen extends Activity {


    /**
     * bean to store fb data
     */
    BeanForFacebookData bFacebookData;

    /**
     * key variables for fb user
     */
    private String EMAIl = "email";
    private String FBTOKEN = "fb_token";


    /**
     * loaded variable
     */
    private ProgressBarManager progressBarManager;

    /**
     * current activity instance
     */
    private SplashScreen currentActivity;

    /**
     * variable for activity switcher
     */

    SwitchActivities switchActivities;

    /**
     * session variable
     */
    MySharedData mySharedData;


    /**
     * login variable for url
     */

    private String LOGIN_URL;

    private Button signUp;
    private Button signIn;
    private ImageView imageView;

    private LoginButton loginButton;
    /**
     * variable for bottom up animation
     */
    Animation bottomUp;
    CallbackManager callbackManager;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        System.out.print("onCanonActivityResult = " + data.getData());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemes);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        /** initialize current ativity */
        currentActivity = SplashScreen.this;


        /** initialize progressbar object */
        progressBarManager = new ProgressBarManager(currentActivity);
//        LoveLifeApplication application = ((LoveLifeApplication)getApplicationContext());


        /** initialize login variable */
        LOGIN_URL = getResources().getString(R.string.serverDomain) + getResources().getString(R.string.fbLogin);


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


                            final Map<String, String> postData = getKeyPairValueInHashMap(bFacebookData.getEmail(), bFacebookData.getToken());

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


        /** initialize buttons */
        signUp = (Button) findViewById(R.id.button);
        signIn = (Button) findViewById(R.id.button2);
        imageView = (ImageView) findViewById(R.id.imageView2);

        bottomUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_up);
        imageView.setAnimation(bottomUp);
        imageView.setVisibility(View.VISIBLE);

        /**
         * Initialize activity switcher
         */
        switchActivities = new SwitchActivities();

        /** initialize session variable */
        mySharedData = new MySharedData(getApplicationContext());

        System.out.println("sesssion  token" + mySharedData.getGeneralSaveSession(MySharedData.TOKEN));

        /** thread that checks if already login then opens main page
         * otherwise shows button on profilePicView */
        new Thread() {
            public void run() {

                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                if (mySharedData.getGeneralSaveSession(MySharedData.TOKEN).equals("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            /** animate buttons from below to up while making them visible */

                            signIn.startAnimation(bottomUp);
                            signUp.startAnimation(bottomUp);
                            loginButton.setAnimation(bottomUp);
                            signUp.setVisibility(View.VISIBLE);
                            //signUp.setVisibility(View.VISIBLE);
                            signIn.setVisibility(View.VISIBLE);
                            loginButton.setVisibility(View.VISIBLE);
                        }
                    });

                } else {

                    if (mySharedData.getGeneralSaveSession(MySharedData.CONNECTED_PARNER_EMAIL).equals("")) {
                        switchActivities.openActivity(SplashScreen.this, PartnerProfile.class);
                    } else {
                        switchActivities.openActivity(SplashScreen.this, Dashboard.class);
                    }


                    finish();
                }


            }
        }.start();


        if (InternetCheck.netCheck(this)) {     /** it will run when internet is connected and within the range. */

            mySharedData.setGeneralSaveSession(MySharedData.CHECKNET, "true");

        } else {           // it will run when internet is connected but out of range.

            mySharedData.setGeneralSaveSession(MySharedData.CHECKNET, "false");
        }
    }


    /**
     * Pop for splashscreen activity.
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
                LoveLifePopups.loveLifePopup(SplashScreen.this, titleText, message);
            }
        });


    }


    /**
     * check if user already exit with fb data
     * otherwise goto registered page to register with fb data
     */
    private void checkUserAndLogin(Map<String, String> postData) {
        /** Register user on server */
        ServerSync serverSync = new ServerSync();

        String reponse = serverSync.SyncServer(postData, LOGIN_URL);

        System.out.println("responsefb " + reponse);


        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {/** If network error occurs */

            /**
             * if there is a network problem while registering user on server.
             * them shows the network error.
             */

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    popup("Network problem.Please check your internet connectivity.", "Sorry!");
                    loginButton.setVisibility(View.VISIBLE);
                    // Toast.makeText(getApplicationContext(), "Network Problem", Toast.LENGTH_LONG).show();
                }
            });

            /** ougout from facebook Api also */
            if (AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null) {


                LoginManager.getInstance().logOut();


            }
        } else {

            String status = ParseJsonData.checkStatus(reponse);

            if (status.equals(ParseJsonData.STATUS_SUCCESS)) {/** if response status is success then login successful. */

                /** parse the response and retrieve the bean class that contains
                 * parsed data of successful response.
                 * Set data from the bean class into the session
                 * Then make login and open the app door for the user
                 *
                 */
                UserLoginBeanClass userLoginBeanClass = ParseJsonData.parseUserLoginJson(reponse);
                System.out.println("splash g " + userLoginBeanClass.getProfilePic());
//                mySharedData.loginSession(userLoginBeanClass.getUserName(), userLoginBeanClass.getUserEmail(), userLoginBeanClass.getUserId(), userLoginBeanClass.getUserMobile(), userLoginBeanClass.getUserToken(), userLoginBeanClass.getFbToken(), bFacebookData.getProfile_pic(), "", mySharedData);

                switchActivities.openActivity(currentActivity, PartnerProfile.class);
                /** Stop progress bar */
                progressBarManager.stopProgressBar();

                String[] spitEmail = userLoginBeanClass.getUserEmail().split("@");


                mySharedData.setGeneralSaveSession(MySharedData.openfireUser, spitEmail[0]);


                finish();
                // popup(message, "Congratulations!");


            } else if (status.equals(ParseJsonData.STATUS_FAIL)) { /** if response status Fail. */

                /** open registration activity if user not exist */
                switchActivities.openActivityWithPutExtra(currentActivity, RegisterActivity.class, new String[]{"firstName", "lastName", "email", "gender", "birthday", "location", "profile_pic", "token", "social_login"},
                        new String[]{bFacebookData.getFirstName(), bFacebookData.getLastName(), bFacebookData.getEmail(), bFacebookData.getGender(), bFacebookData.getBirthday(), bFacebookData.getLocation(), bFacebookData.getProfile_pic(), bFacebookData.getToken(), "true"});
                /** Stop progress bar */
                progressBarManager.stopProgressBar();
                finish();
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
     * Method that create key value pair for sending dta in post
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMap(String email, String fbToken) {

        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */


        System.out.println("splash ffcm id " + mySharedData.getGeneralSaveSession(MySharedData.FCM_REGISTRATIONID));
        String key[] = {EMAIl, FBTOKEN, LoveLifeApplication.FCM_REGISTERATION_ID};      // set keys
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
     * goto sign in page
     *
     * @param view
     */
    public void signIn(View view) {

        switchActivities.openActivity(SplashScreen.this, LoginActivity.class);
        finish();
    }


    /**
     * goto sign up page
     *
     * @param view
     */
    public void signUp(View view) {
        switchActivities.openActivityWithPutExtra(currentActivity, RegisterActivity.class, new String[]{"social_login"},
                new String[]{"false"});

        finish();
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


}
