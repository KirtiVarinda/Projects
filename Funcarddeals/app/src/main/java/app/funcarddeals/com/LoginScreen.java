package app.funcarddeals.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import app.funcarddeals.com.Manager.DataEncription;
import app.funcarddeals.com.Manager.InternetCheck;
import app.funcarddeals.com.Manager.PairValues;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.Popups.FuncardPopups;
import app.funcarddeals.com.Popups.PopupMessagesStrings;
import app.funcarddeals.com.SharedData.MySharedData;
import app.funcarddeals.com.Validator.FormValidation;
import app.funcarddeals.com.network.ServerSync;

public class LoginScreen extends Activity {

    public static CallbackManager callbackmanager;


    boolean isFacebookLogin = false;
    /**
     * call back manager for facebook.
     */
    CallbackManager callbackManager;


    /**
     * Reference to get funcard popup messages.
     */
    PopupMessagesStrings popString;

    CheckBox checkBox;
    String mEmail;
    String mPassword;
    String FUNCARD_EMAIL = "funcarddeals_email";
    String FUNCARD_PASSWORD = "funcarddeals_password";
    String USER_LOGIN_URL;
    Context context;
    Activity loginActivity;
    EditText funcard_user_emailid, funcard_user_password;

    TextView textView;

    /**
     * Reference variable for Shared Data (Session).
     */
    private static MySharedData mySharedData;

    /**
     * general class reference for change activity.
     */
    static SwitchActivities switchActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FacebookSdk.sdkInitialize(this.getApplicationContext());
        // AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login_screen);
        //use to provide data

        /** make forgor text under line */
        textView = (TextView) findViewById(R.id.textView4);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        context = getApplicationContext();
        loginActivity = LoginScreen.this;

        funcard_user_emailid = (EditText) findViewById(R.id.editText);
        funcard_user_password = (EditText) findViewById(R.id.editText2);
        checkBox = (CheckBox) findViewById(R.id.checkBox12);

/*

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "app.funcarddeals.com",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                funcard_user_emailid.setText(Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
*/

        /**
         * Initialize Shared data
         */
        mySharedData = new MySharedData(context);


        // registerFacebook();

        /**
         * Initialize the PopMessageString to get popStrings.
         */
        popString = new PopupMessagesStrings();
        popString.PopupMessagesStringsForLogin(context);


        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();



    /*    LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
       // loginButton.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "public_profile"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {
                if(Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            // profile2 is the new profile
                            Log.v("facebook - profile", profile2.getFirstName());
                            mProfileTracker.stopTracking();
                        }
                    };
                }
                else {
                    Profile profile = Profile.getCurrentProfile();
                    Log.v("facebook - profile", profile.getFirstName());
                }
            }

            @Override
            public void onCancel() {
                Log.v("facebook - onCancel", "cancelled");
            }

            @Override
            public void onError(FacebookException e) {
                Log.v("facebook - onError", e.getMessage());
            }
        });

*/

    }



    /** method to get key hash*/
/*
    public void getKeyHash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "app.funcarddeals.com",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }
*/


    /**
     * Method to register for facebook API.
     */
    public void registerFacebook(View view) {

      /*  System.out.println("fb= "+InternetCheck.netCheck(getApplicationContext()));*/

        if (!InternetCheck.netCheck(getApplicationContext())) {


            FuncardPopups.funcardPopup(loginActivity, popString.titleMessage, popString.networkError);
            return;

        }


        isFacebookLogin = true;

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                       // System.out.println("success1");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {

                                        if (response.getError() != null) {
                                            // handle error

                                        } else {

                                            try {

                                                String jsonresult = String.valueOf(json);
                                                String str_email = json.getString("email");
                                                String str_id = json.getString("id");
                                                String name = json.getString("name");
                                                if (name.equals("")) {
                                                    name = "noName";
                                                }
                                                mEmail = name;
                                                mPassword = str_id;

                                                new DoInBackground().execute();


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link,gender,birthday,email");
                        request.setParameters(parameters);
                        request.executeAsync();

                        // App code
                    }

                    @Override
                    public void onCancel() {

                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                      //  System.out.println("error message while open facebook " + exception.getMessage());
                        FuncardPopups.funcardPopup(loginActivity, popString.titleMessage, popString.networkError);
                    }
                });
    }

    /*
        public void funcardFacebookLogin(View view) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
        }

    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        AccessToken token = AccessToken.getCurrentAccessToken();
//        if (resultCode == Activity.RESULT_OK) {
//            callbackManager.onActivityResult(requestCode, resultCode, data);
//        }


    }

/*
    // Private method to handle Facebook login and callback
    private void onFblogin() {
        callbackmanager = CallbackManager.Factory.create();

        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "public_profile"));

        LoginManager.getInstance().registerCallback(callbackmanager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        System.out.println("Success");
                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                            System.out.println("ERROR");
                                        } else {
                                            System.out.println("Success");
                                            try {

                                                String jsonresult = String.valueOf(json);
                                                System.out.println("JSON Result" + jsonresult);

                                                String str_email = json.getString("email");
                                                String str_id = json.getString("id");
                                                String str_firstname = json.getString("first_name");
                                                String str_lastname = json.getString("last_name");

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }).executeAsync();

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });
    }
*/

    /**
     * go for sign in page.
     *
     * @param view
     */
    public void signIn(View view) {
        isFacebookLogin = false;              /** simple login not a facebook login */
        mEmail = funcard_user_emailid.getText().toString();
        mPassword = funcard_user_password.getText().toString();
        new DoInBackground().execute();


    }

    public void forgotPassword(View view) {
        switchActivity.openActivity(LoginScreen.this, ForgotPassword.class);
    }


    /**
     * open registeration form.
     *
     * @param view
     */
    public void register(View view) {
        switchActivity.openActivity(LoginScreen.this, Registration.class);

    }


    /**
     * class for background tasks
     */
    private class DoInBackground extends AsyncTask<Void, Void, Void>
            implements DialogInterface.OnCancelListener {
        private ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = ProgressDialog.show(LoginScreen.this, "", "Loading Funcard. Please wait...", true);
        }

        protected Void doInBackground(Void... unused) {

            if (isFacebookLogin) {
                serverSyncing();
            } else {
                if (FormValidation.isValidEmail(mEmail) && FormValidation.isValidPassword(mPassword)) {  // if email and password are valid.
                    serverSyncing();
                } else {
                    FuncardPopups.funcardPopup(loginActivity, popString.titleMessage, popString.invalidUserFromLocalMessage);
                }


            }


            return null;
        }

        protected void onPostExecute(Void unused) {
            dialog.dismiss();

        }

        public void onCancel(DialogInterface dialog) {
            cancel(true);
            dialog.dismiss();
        }
    }

    private void serverSyncing() {
        /**
         * Encript Password to MD5 if simple login.
         * do not encript if faebook login because it is used as pass.
         *
         * Also Initializing login url according to facebook login or simple login
         */
        String md5Password = "";
        if (isFacebookLogin) {
            md5Password = mPassword;

            USER_LOGIN_URL = context.getResources().getString(R.string.funcard_facebook_login_url);
        } else {
            md5Password = DataEncription.encriptToMD5(mPassword);
            USER_LOGIN_URL = context.getResources().getString(R.string.funcard_login_url);
        }

        /**
         * make key and values array for all the strings.
         * index of both shuold be same.
         */
        String key[] = {FUNCARD_EMAIL, FUNCARD_PASSWORD};      // set keys
        String value[] = {mEmail, md5Password};                //set values


        /**
         * Get key value pairs for email and password.
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        Map<String, String> theMap = pairValues.funcardPairValue(key, value);

        /**
         * Authenticate user from funcard deals server.
         */
        ServerSync serverSync = new ServerSync();
        String reponse = serverSync.SyncServer(theMap, USER_LOGIN_URL);


        /**
         * Split the response to get the user id if user is valid.
         */

        String[] splitResponse = reponse.split("-");

        if (splitResponse[0].equals(ServerSync.VALID_FUNCARD_USER)) {
            /**
             * Save user_id,Email,Password in Session if user if valid.
             */

                setSessionForLogin(mEmail, md5Password, splitResponse[1], splitResponse[2]);


            /**
             * Redirect to Main page if user is valid.
             */
            switchActivity.openActivity(LoginScreen.this, MainPage.class);
            finish();

        } else if (splitResponse[0].equals(ServerSync.INVALID_FUNCARD_USER)) {
            FuncardPopups.funcardPopup(loginActivity, popString.titleMessage, popString.invalidUserFromServerMessage);

        } else if (splitResponse[0].equals(ServerSync.FUNCARD_USER_DISABLED)) {



            FuncardPopups.funcardPopupWithTwoActions(LoginScreen.this,ResendActivity.class,"Alert!", getResources().getString(R.string.email_verification_register),"Resend","Ok",new String[]{"old_email"},new String[]{mEmail});

          //  FuncardPopups.funcardPopup(loginActivity, popString.titleMessage, getResources().getString(R.string.email_verification_login));





        } else {
            FuncardPopups.funcardPopup(loginActivity, popString.titleMessage, popString.networkError);
        }
    }

    private void setSessionForLogin(String mEmail, String md5Password, String value, String userName) {
        /**
         * Save user_id,Email,Password in Session if user if valid.
         */

        mySharedData.setGeneralSaveSession(MySharedData.FUNCARDSERVER_USER_NAME, userName);
        mySharedData.setGeneralSaveSession(MySharedData.FUNCARDSERVER_USER_ID, value);
        mySharedData.setGeneralSaveSession(MySharedData.LOGIN_USER_EMAIL, mEmail);
        mySharedData.setGeneralSaveSession(MySharedData.LOGIN_USER_ENCRIPTED_MD5_PASSWORD, md5Password);


        if (checkBox.isChecked() &&  !isFacebookLogin) {
            mySharedData.setGeneralSaveSession(MySharedData.FUNCARD_IS_KEEP_LOGGED_IN_PASS, mPassword);
            mySharedData.setGeneralSaveSession(MySharedData.FUNCARD_IS_KEEP_LOGGED_IN_EMAIL, mEmail);
        } else {
            mySharedData.setGeneralSaveSession(MySharedData.FUNCARD_IS_KEEP_LOGGED_IN_PASS, "");
            mySharedData.setGeneralSaveSession(MySharedData.FUNCARD_IS_KEEP_LOGGED_IN_EMAIL, "");
        }

        /**
         * retain password for keep logged in
         */


    }

    @Override
    public void onBackPressed() {
        /**
         * Nothing happen on click of back press button
         */

    }

    @Override
    protected void onResume() {
        super.onResume();


        if (!mySharedData.getGeneralSaveSession(MySharedData.FUNCARD_IS_KEEP_LOGGED_IN_PASS).equals("")) {

            funcard_user_emailid.setText(mySharedData.getGeneralSaveSession(MySharedData.FUNCARD_IS_KEEP_LOGGED_IN_EMAIL));
            funcard_user_password.setText(mySharedData.getGeneralSaveSession(MySharedData.FUNCARD_IS_KEEP_LOGGED_IN_PASS));


        }
    }
}
