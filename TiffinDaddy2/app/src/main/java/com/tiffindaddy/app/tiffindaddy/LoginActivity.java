package com.tiffindaddy.app.tiffindaddy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.tiffindaddy.app.tiffindaddy.BeanClasses.UserLoginData;
import com.tiffindaddy.app.tiffindaddy.JsonParsing.ParseJsonData;
import com.tiffindaddy.app.tiffindaddy.Manager.CurrentDevice;
import com.tiffindaddy.app.tiffindaddy.Manager.DataEncription;
import com.tiffindaddy.app.tiffindaddy.Manager.PairValues;
import com.tiffindaddy.app.tiffindaddy.Manager.ProgressBarManager;
import com.tiffindaddy.app.tiffindaddy.Popups.TiffinDaddyPopups;
import com.tiffindaddy.app.tiffindaddy.SharedData.MySharedData;
import com.tiffindaddy.app.tiffindaddy.Validator.FormValidation;
import com.tiffindaddy.app.tiffindaddy.network.ServerSync;

import java.util.Map;

public class LoginActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private ProgressBarManager progressBarManager;


    UserLoginData userLoginData;

    /**
     * variable for sessions.
     */
    private MySharedData mySharedData;
    /***
     * user data keys.
     */

    private static String EMAIL_KEY = "user_email";
    private static String PASSWORD_KEY = "user_password";
    private static String DEVICE_ID_KEY = "device_id";
    private static String DEVICE_NAME_KEY = "device_name";
    private static String DEVICE_TYPE_KEY = "device_type";


    /**
     * Tiffin login url
     */
    private static String loginURL;
    private ParseJsonData parseJsonData;
    private Context context;


    private EditText userEmail, userPassword;
    private String mEmail, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayShowHomeEnabled(true);



        context = getApplicationContext();
        loginURL = getResources().getString(R.string.login_url);
        parseJsonData = new ParseJsonData();
        userEmail = (EditText) findViewById(R.id.uemail);
        userPassword = (EditText) findViewById(R.id.upass);
        mySharedData = new MySharedData(context);

        progressBarManager = new ProgressBarManager(this);


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


    public void login(View view) {
        progressBarManager.startProgressBar();

        mEmail = userEmail.getText().toString();
        mPassword = userPassword.getText().toString();

        if (!FormValidation.isValidEmail(mEmail)) {
            TiffinDaddyPopups.funcardPopup(LoginActivity.this, "Sorry!", "Wrong Email id.");
            progressBarManager.stopProgressBar();
        } else if (!FormValidation.isValidPassword(mPassword)) {
            TiffinDaddyPopups.funcardPopup(LoginActivity.this, "Sorry!", "Password should contain 4 letters atleast.");
            progressBarManager.stopProgressBar();
        } else {

            new Thread() {
                public void run() {

                    getCategories();

                }

            }.start();

        }


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
        getMenuInflater().inflate(R.menu.login, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Get Tiffin Categories from the server.
     */
    private void getCategories() {


        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
//        System.out.println("email= "+EMAIL_KEY+"" +mEmail);
//        System.out.println("email= "+PASSWORD_KEY+"" +mPassword);
//        System.out.println("email= "+DEVICE_ID_KEY+"" +CurrentDevice.getDeviceId(context));
//        System.out.println("email= "+DEVICE_NAME_KEY+"" +CurrentDevice.getDeviceName());

        String key[] = {EMAIL_KEY, PASSWORD_KEY, DEVICE_ID_KEY, DEVICE_NAME_KEY, DEVICE_TYPE_KEY};      // set keys
        String value[] = {mEmail, DataEncription.encriptToMD5(mPassword), CurrentDevice.getDeviceId(context), CurrentDevice.getDeviceName(), "2"};      /** 2 for android */          //set values


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
        final String reponse = serverSync.SyncServer(theMap, loginURL);

        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {

            /**
             * if there is a network problem while loading cities from server for the first time.
             * them shows the network error below the listview on page.
             */

            LoginActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TiffinDaddyPopups.funcardPopup(LoginActivity.this, "Sorry!", "Network problem.");

                }
            });


            //  progressBarManager.stopProgressBar();

        } else {


            /**
             * if response status is success then only parse data.
             */
            if (parseJsonData.checkStatus(reponse, "status").toLowerCase().equals("success")) {
                userLoginData = parseJsonData.parseUserLoginJson(reponse);
                setUserDataInSession(userLoginData);

                finish();

            } else {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        TiffinDaddyPopups.funcardPopup(LoginActivity.this, "Sorry!", "Wrong username or password.");

                    }
                });


            }


        }
        LoginActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarManager.stopProgressBar();
            }
        });
        System.out.println("Response= " + reponse);


    }

    private void setUserDataInSession(UserLoginData userLoginData) {

        mySharedData.setGeneralSaveSession(MySharedData.SESSION_USEREMAIL, userLoginData.getUserEmail());
        mySharedData.setGeneralSaveSession(MySharedData.SESSION_USERID, userLoginData.getUserID());
        mySharedData.setGeneralSaveSession(MySharedData.SESSION_TOKEN, userLoginData.getToken());
        mySharedData.setGeneralSaveSession(MySharedData.SESSION_USERNAME, userLoginData.getUserName());
        mySharedData.setGeneralSaveSession(MySharedData.SESSION_USERPHONE, userLoginData.getUserPhone());

    }


    public void signUp(View view){
        Intent intent=new Intent(LoginActivity.this,SignUp.class);
        startActivity(intent);
        finish();

    }
}
