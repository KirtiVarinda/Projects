package com.tiffindaddy.app.tiffindaddy;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.tiffindaddy.app.tiffindaddy.JsonParsing.ParseJsonData;
import com.tiffindaddy.app.tiffindaddy.Manager.PairValues;
import com.tiffindaddy.app.tiffindaddy.Manager.ProgressBarManager;
import com.tiffindaddy.app.tiffindaddy.Popups.TiffinDaddyPopups;
import com.tiffindaddy.app.tiffindaddy.Validator.FormValidation;
import com.tiffindaddy.app.tiffindaddy.network.ServerSync;

import java.util.Map;

public class SignUp extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private ProgressBarManager progressBarManager;

    private EditText userName, userEmail, userPhone, userPassword, userrepassword;

    private Button signUp;
    /***
     * user data keys.
     */

    private static String USER_NAME = "user_name";
    private static String USER_EMAIL = "user_email";
    private static String USER_PHONE = "user_phone";
    private static String USER_PASSRWOD = "user_password";

    private Context context;

    private String mUsername, memail, mPhone, mPassword, mrePassword;


    /**
     * Tiffin Signup url
     */
    private static String SignUpURL;
    private ParseJsonData parseJsonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);


        context = getApplicationContext();
        SignUpURL = getResources().getString(R.string.register_url);
        progressBarManager = new ProgressBarManager(this);

        userName = (EditText) findViewById(R.id.editText);
        userEmail = (EditText) findViewById(R.id.editText2);
        userPhone = (EditText) findViewById(R.id.editText3);
        userPassword = (EditText) findViewById(R.id.editText4);
        userrepassword = (EditText) findViewById(R.id.editText5);
        signUp = (Button) findViewById(R.id.button);
        parseJsonData = new ParseJsonData();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
    }


    public void signUp(View view) {
        progressBarManager.startProgressBar();
        mUsername = userName.getText().toString();
        memail = userEmail.getText().toString();
        mPhone = userPhone.getText().toString();
        mPassword = userPassword.getText().toString();
        mrePassword = userrepassword.getText().toString();


        if (FormValidation.removeWhiteSpaces(mUsername).equals("")) {
            TiffinDaddyPopups.funcardPopup(SignUp.this, "Sorry!", "Please enter a valid user name.");
            progressBarManager.stopProgressBar();
        } else if (!FormValidation.isValidPassword(memail)) {
            TiffinDaddyPopups.funcardPopup(SignUp.this, "Sorry!", "Please enter a valid email id.");
            progressBarManager.stopProgressBar();
        } else if (!FormValidation.isValidPhoneNumber(mPhone)) {
            TiffinDaddyPopups.funcardPopup(SignUp.this, "Sorry!", "Phone number should contain 10 numbers.");
            progressBarManager.stopProgressBar();
        } else if (!FormValidation.isValidPassword2(mPassword)) {
            TiffinDaddyPopups.funcardPopup(SignUp.this, "Sorry!", "Password should be of minimum 8 characters");
            progressBarManager.stopProgressBar();
        } else if (!mPassword.equals(mrePassword)) {
            TiffinDaddyPopups.funcardPopup(SignUp.this, "Sorry!", "Retype password does not match.");
            progressBarManager.stopProgressBar();
        } else {

            new Thread() {
                public void run() {
                    registerUser();

                }

            }.start();


        }


    }

    /**
     * Get Tiffin Categories from the server.
     */
    private void registerUser() {


        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
//        System.out.println("email= "+EMAIL_KEY+"" +mEmail);
//        System.out.println("email= "+PASSWORD_KEY+"" +mPassword);
//        System.out.println("email= "+DEVICE_ID_KEY+"" +CurrentDevice.getDeviceId(context));
//        System.out.println("email= "+DEVICE_NAME_KEY+"" +CurrentDevice.getDeviceName());

        String key[] = {USER_NAME, USER_EMAIL, USER_PHONE, USER_PASSRWOD};      // set keys
        String value[] = {mUsername, memail, mPhone, mPassword};      /** 2 for android */          //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        Map<String, String> theMap = pairValues.funcardPairValue(key, value);


        /**
         * Authenticate user from funcard deals server.
         */

        ServerSync serverSync = new ServerSync();
        final String reponse = serverSync.SyncServer(theMap, SignUpURL);

        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {

            /**
             * if there is a network problem while loading cities from server for the first time.
             * them shows the network error below the listview on page.
             */

            SignUp.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TiffinDaddyPopups.funcardPopup(SignUp.this, "Sorry!", "Network problem.");

                }
            });


            //  progressBarManager.stopProgressBar();

        } else {


            /**
             * if response status is success then only parse data.
             */
            if (parseJsonData.checkStatus(reponse, "status").toLowerCase().equals("success")) {

                SignUp.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        TiffinDaddyPopups.funcardPopup(SignUp.this, "Congratulations!", "User Register successfully.", SignUp.this);

                    }
                });


            } else if (parseJsonData.checkStatus(reponse, "status").toLowerCase().equals("error")) {

                if (parseJsonData.checkStatus(reponse, "user_email").toLowerCase().equals("The Email field must contain a unique value.")) {
                    SignUp.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            TiffinDaddyPopups.funcardPopup(SignUp.this, "Sorry!", "Email already exist.");

                        }
                    });

                } else {
                    SignUp.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            TiffinDaddyPopups.funcardPopup(SignUp.this, "Sorry!", "Unable to register.");

                        }
                    });
                }


            }


        }
        SignUp.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarManager.stopProgressBar();
            }
        });
        System.out.println("register Response= " + reponse);


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }
}
