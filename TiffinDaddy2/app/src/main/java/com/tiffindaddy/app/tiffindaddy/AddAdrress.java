package com.tiffindaddy.app.tiffindaddy;

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

import com.tiffindaddy.app.tiffindaddy.JsonParsing.ParseJsonData;
import com.tiffindaddy.app.tiffindaddy.Manager.PairValues;
import com.tiffindaddy.app.tiffindaddy.Manager.ProgressBarManager;
import com.tiffindaddy.app.tiffindaddy.Popups.TiffinDaddyPopups;
import com.tiffindaddy.app.tiffindaddy.SharedData.MySharedData;
import com.tiffindaddy.app.tiffindaddy.Validator.FormValidation;
import com.tiffindaddy.app.tiffindaddy.network.ServerSync;

import java.util.Map;

public class AddAdrress extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    EditText editText, editText2;

    String addressTitle, address;
    private ProgressBarManager progressBarManager;

    private String addressURL;

    /**
     *
     */
    private ParseJsonData parseJsonData;
    private static String DEVICE_TOKEN = "device_token";
    MySharedData mySharedData;
    private static String UID = "uid";
    private static String ADDRESS_TITLE = "address_title";
    private static String ADDRESS = "address";
    private static String CITY_ID = "city_id";
    private String cityID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_adrress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        parseJsonData = new ParseJsonData();
        mySharedData = new MySharedData(getApplicationContext());

        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);


        progressBarManager = new ProgressBarManager(this);

        addressURL = getResources().getString(R.string.add_address);


    }


    public void addAddress(View view) {
         progressBarManager.startProgressBar();
        addressTitle = editText.getText().toString();
        address = editText2.getText().toString();
        cityID = mySharedData.getGeneralSaveSession(MySharedData.SESSION_CITY_ID);
        if (cityID.equals("")) {
            TiffinDaddyPopups.funcardPopup(AddAdrress.this, "Sorry!", "Select City from top first.");

            progressBarManager.stopProgressBar();
        }else if (FormValidation.removeWhiteSpaces(addressTitle).equals("")) {
            TiffinDaddyPopups.funcardPopup(AddAdrress.this, "Sorry!", "Please enter Address Title.");
            progressBarManager.stopProgressBar();
        } else if (FormValidation.removeWhiteSpaces(addressTitle).equals("")) {
            TiffinDaddyPopups.funcardPopup(AddAdrress.this, "Sorry!", "Please enter Address.");
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
    protected void onResume() {
        super.onResume();

            if(city_Name!=null){
                city_Name.setTitle(mySharedData.getGeneralSaveSession(MySharedData.SESSION_CITY));
            }
        }

    /**
     * Get Tiffin Categories from the server.
     */
    private void getCategories() {
        String uid = mySharedData.getGeneralSaveSession(MySharedData.SESSION_USERID);
        String token = mySharedData.getGeneralSaveSession(MySharedData.SESSION_TOKEN);




        String key[] = {DEVICE_TOKEN, UID, ADDRESS_TITLE, ADDRESS, CITY_ID};      // set keys
        String value[] = {token, uid, addressTitle, address, cityID};      /** 2 for android */          //set values


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
        final String reponse = serverSync.SyncServer(theMap, addressURL);

        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {

            /**
             * if there is a network problem while loading cities from server for the first time.
             * them shows the network error below the listview on page.
             */

            AddAdrress.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TiffinDaddyPopups.funcardPopup(AddAdrress.this, "Sorry!", "Network problem.");

                }
            });


            //  progressBarManager.stopProgressBar();

        } else {


            /**
             * if response status is success then only parse data.
             */
            if (parseJsonData.checkStatus(reponse, "status").toLowerCase().equals("success")) {

                TiffinDaddyPopups.funcardPopup(AddAdrress.this, "Congratulations!", "Address added successfully.", AddAdrress.this);
                CartPage.isNewAddressAdded=true;


            } else if(parseJsonData.checkStatus(reponse, "status").toLowerCase().equals("error")){
                AddAdrress.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        TiffinDaddyPopups.funcardPopup(AddAdrress.this, "Sorry!", "Unable to add new address.");

                    }
                });


            } else{
                AddAdrress.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        TiffinDaddyPopups.funcardPopup(AddAdrress.this, "Sorry!", "Server error.");

                    }
                });


            }


        }
        AddAdrress.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarManager.stopProgressBar();
            }
        });
        System.out.println("Response= " + reponse);


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            overridePendingTransition(0,0);

            super.onBackPressed();
        }
    }


    MenuItem city_Name, locationicon;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_adrress, menu);
        city_Name = menu.findItem(R.id.city_name);
        locationicon = menu.findItem(R.id.location);
        city_Name.setTitle(mySharedData.getGeneralSaveSession(MySharedData.SESSION_CITY));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.location) {
            Intent intent=new Intent(AddAdrress.this,SelectCity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
