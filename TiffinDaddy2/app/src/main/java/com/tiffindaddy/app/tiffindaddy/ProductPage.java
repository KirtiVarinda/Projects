package com.tiffindaddy.app.tiffindaddy;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.Cities;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.CouponData;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.TiffinCategories;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.TiffinData;
import com.tiffindaddy.app.tiffindaddy.Manager.ManageLocations;
import com.tiffindaddy.app.tiffindaddy.Manager.PairValues;
import com.tiffindaddy.app.tiffindaddy.Manager.ProgressBarManager;
import com.tiffindaddy.app.tiffindaddy.Manager.TimeManager;
import com.tiffindaddy.app.tiffindaddy.Popups.TiffinDaddyPopups;
import com.tiffindaddy.app.tiffindaddy.SharedData.MySharedData;
import com.tiffindaddy.app.tiffindaddy.network.ServerSync;

import java.util.HashMap;
import java.util.Map;

public class ProductPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public static boolean citySelected=false;

    boolean isLocationRequestReset = false;

    /**
     * variables for managing location
     */
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;


    /**
     * PROGRESS BAR VARIABLE.
     */
    private ProgressBarManager progressBarManager;


    /**
     * Tiffin addon  url
     */
    private static String addonsURL;


    /**
     * today date variable for check
     */


    private String todayDateForCheck = "";


    /**
     * variable to hold current day date.
     */

    public static String selectedDate;


    /**
     * variable to select tiffin for today or tomorrow.
     */

    public static boolean checkDayIsToday = true;


    /**
     * different variables for holding tiffin data.
     */
    public static TiffinData todayTiffinDataBreakFast[], tomorrowTiffinDataBreakFast[];
    public static TiffinData todayTiffinDataLunch[], tomorrowTiffinDataLunch[];
    public static TiffinData todayTiffinDataDinner[], tomorrowTiffinDataDinner[];
    public static TiffinData todayAddons[], tomorrowAddons[];

    public static CouponData couponData;



    private Button today, tomorrow;


    /**
     * variable for holding addons response.
     */

    public static String addonResponse = "";


    public static FloatingActionButton fab;


    ViewPager viewPager;
    PagerAdapter adapter;


    /**
     * map object for selected products
     */

    public static Map<String, TiffinData> theMap;

    private TextView navUserProfile;

    private MySharedData mySharedData;
    /**
     * variable for serializable bean class
     */
    public static TiffinCategories tiffinCategories[];


    /**
     * current date variable
     */
    TextView todayDate;


    /**
     * Cities data
     */
    public static Cities cities[];


    private static String SERIALIZED_KEY = "serialized_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);


        tiffinCategories = MainActivity.tiffinCategories;
        cities = MainActivity.cities;
        /**
         * check for the location setting
         */
        ManageLocations.checkLocationSetting(this, getApplicationContext());


        /**
         * call Google api client for map and location.
         */


            buildGoogleApiClient();




        /**
         * make a location request for location updates.
         */
        mLocationRequest = ManageLocations.createLocationRequest(10000, 5000);


        /**
         * initialize progress bar.
         */
        progressBarManager = new ProgressBarManager(ProductPage.this);

        /**
         * update layout with today date
         */
        todayDate = (TextView) findViewById(R.id.textView14);
        today = (Button) findViewById(R.id.Button);
        tomorrow = (Button) findViewById(R.id.Button1);

        /**
         * variable to store data in session
         */
        mySharedData = new MySharedData(getApplicationContext());


        /**
         * get addon url;
         */
        addonsURL = getResources().getString(R.string.get_addons);


        /**
         * initialize view pager
         */
        viewPager = (ViewPager) findViewById(R.id.pager);

        progressBarManager.startProgressBar();
        /***
         * load addons from server.
         */
        new Thread() {
            public void run() {

                getAddons();


            }


        }.start();


        todayDate.setText("Today:- " + TimeManager.todayDate());
        selectedDate = TimeManager.todayDate();
        todayDateForCheck = TimeManager.todayDate();


        if (theMap == null) {

            theMap = new HashMap<String, TiffinData>();
        }


        /** get serialized data from previous activity.*/
//        ArrayList<TiffinCategories> categories  = (ArrayList<TiffinCategories>) getIntent().getSerializableExtra(SERIALIZED_KEY);
//        tiffinCategories=new TiffinCategories[categories.size()];
//        for(int j=0;j<categories.size();j++){
//            tiffinCategories[j]=categories.get(j);
//
//        }


        /**
         * setting floating icon
         */
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {


                goToCart();


            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nav_header = LayoutInflater.from(this).inflate(R.layout.nav_header_product_page, null);
        navUserProfile = ((TextView) nav_header.findViewById(R.id.textView));
        navigationView.addHeaderView(nav_header);


    }

    private void goToCart() {
       String  cityID = mySharedData.getGeneralSaveSession(MySharedData.SESSION_CITY_ID);
        if (!mySharedData.getGeneralSaveSession(MySharedData.SESSION_USEREMAIL).equals("")) {

            Intent intent = new Intent(ProductPage.this, CartPage.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        } else if(cityID.equals("")){
            TiffinDaddyPopups.funcardPopup(ProductPage.this, "Alert!", "Please select city from top.");
        }else {
            TiffinDaddyPopups.funcardPopup(ProductPage.this, "Alert!", "Please login to Tiffindaddy first.", LoginActivity.class);
        }
    }


    public void Today(View v) {


        if (adapter != null) {
            today.setBackgroundResource(R.drawable.green_with_tab_below);
            tomorrow.setBackgroundResource(R.drawable.green_rectangle_border);
            todayDate.setText("Today:- " + TimeManager.todayDate());
            checkDayIsToday = true;
            viewPager.setAdapter(adapter);
            selectedDate = TimeManager.todayDate();
        } else {

            /***
             * load addons from server.
             */
            progressBarManager.startProgressBar();
            new Thread() {
                public void run() {

                    getAddons();


                }


            }.start();


        }

    }

    public void Tomarrow(View v1) {
        if (adapter != null) {
            today.setBackgroundResource(R.drawable.green_rectangle_border);
            tomorrow.setBackgroundResource(R.drawable.green_with_tab_below);
            todayDate.setText("Tomorrow:- " + TimeManager.tomorrowDate());
            checkDayIsToday = false;
            viewPager.setAdapter(adapter);
            selectedDate = TimeManager.tomorrowDate();
        } else {

            /***
             * load addons from server.
             */
            progressBarManager.startProgressBar();
            new Thread() {
                public void run() {

                    getAddons();


                }


            }.start();


        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        /**
         * stop etting location.
         */
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();




        /**
         * initialize google play services.
         */
        mGoogleApiClient.connect();
        /**
         * Resume getting locations.
         */

        if(citySelected){

          //  city_Name.setVisible(true);
           // locationicon.setVisible(false);

            city_Name.setTitle(mySharedData.getGeneralSaveSession(MySharedData.SESSION_CITY));

            /**
             * stop etting location.
             */
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdates();
              //  mGoogleApiClient.disconnect();
            }



        }else{
            if (mGoogleApiClient.isConnected()/* && !mRequestingLocationUpdates*/) {
                startLocationUpdates();
            }

        }




        /**
         * reset the data if the day passed.
         */
        if (theMap.size() < 1 || !TimeManager.todayDate().equals(todayDateForCheck)) {
            todayTiffinDataBreakFast = null;
            tomorrowTiffinDataBreakFast = null;
            todayTiffinDataLunch = null;
            tomorrowTiffinDataLunch = null;
            todayTiffinDataDinner = null;
            tomorrowTiffinDataDinner = null;


            /**
             * reset dates if day pass
             */
            todayDateForCheck = TimeManager.todayDate();
            todayDate.setText("Today:- " + TimeManager.todayDate());
            selectedDate = TimeManager.todayDate();

        }


        //todayAddons[], tomorrowAddons[];


        if (adapter != null) {
            viewPager.setAdapter(adapter);
        }

        new Thread() {

            @Override
            public void run() {
                ServerSync serverSync = new ServerSync();
                String string = serverSync.SyncServeByGet(getResources().getString(R.string.lifecheck));
                System.out.println("check= " + string);
                if (string.toLowerCase().equals("on")) {
                    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                }

                super.run();
            }
        }.start();


        if (!mySharedData.getGeneralSaveSession(MySharedData.SESSION_USEREMAIL).equals("")) {

            navUserProfile.setText(mySharedData.getGeneralSaveSession(MySharedData.SESSION_USERNAME));

        } else {

            navUserProfile.setText("Guest");

        }

    }


    /**
     * getting addons from server.
     */


    private void getAddons() {


        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
        String key[] = {};      // set keys
        String value[] = {};                //set values


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
        String reponse = serverSync.SyncServer(theMap, addonsURL);

        if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.HTTP_ERROR)) {

            /**
             * if there is a network problem while loading cities from server for the first time.
             * them shows the network error below the listview on page.
             */


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    todayDate.setText("Network problem. click to reload.");
                }
            });


        } else {

            addonResponse = reponse;


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    todayDate.setText("Today:- " + TimeManager.todayDate());


                    /**
                     * setting tabs for lunch , breakfast and dinner
                     */
                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

                    if (tiffinCategories.length == 1) {
                        tabLayout.addTab(tabLayout.newTab().setText("Dinner"));
                        // tabLayout.addTab(tabLayout.newTab().setText("Addons"));
                    } else if (tiffinCategories.length == 2) {
                        tabLayout.addTab(tabLayout.newTab().setText("Lunch"));
                        tabLayout.addTab(tabLayout.newTab().setText("Dinner"));
                        //  tabLayout.addTab(tabLayout.newTab().setText("Addons"));
                    } else if (tiffinCategories.length == 3) {
                        tabLayout.addTab(tabLayout.newTab().setText("Breakfast"));
                        tabLayout.addTab(tabLayout.newTab().setText("Lunch"));
                        tabLayout.addTab(tabLayout.newTab().setText("Dinner"));
                        //  tabLayout.addTab(tabLayout.newTab().setText("Addons"));
                    }

                    //  tabLayout.addTab(tabLayout.newTab().setText("Addons"));
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


                    adapter = new PagerAdapter
                            (getSupportFragmentManager(), tabLayout.getTabCount());
                    viewPager.setAdapter(adapter);
                    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(final TabLayout.Tab tab) {
                            viewPager.setCurrentItem(tab.getPosition());

                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });
                }
            });


        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarManager.stopProgressBar();
            }
        });


    }


    public void reload(View view) {
        /***
         * load addons from server.
         */
        progressBarManager.startProgressBar();
        new Thread() {
            public void run() {

                getAddons();


            }


        }.start();


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
//            this.finish();
        }
    }


    MenuItem city_Name, locationicon;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product_page, menu);

        city_Name = menu.findItem(R.id.city_name);
        locationicon =menu.findItem(R.id.location);

//
//        String[] allCities=new String[cities.length+1];
//        allCities[0]="Select cit";
//        for(int t=0;t<cities.length;t++) {
//            allCities[t+1]=cities[t].getCityName();
//        }
//
//        final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(this, R.layout.spinner_item, allCities);
//        spinnerArrayAdapter1.setDropDownViewResource(R.layout.spinner_dropdown_item); // The drop down view
//
//        Spinner spinner = (Spinner) MenuItemCompat.getActionView(menu_spinner1);
//
//        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this.getActionBar()
//                .getThemedContext(), R.array.my_menu_spinner_list, android.R.layout.simple_spinner_dropdown_item); //  create the adapter from a StringArray
//
//
//
//        spinner.setAdapter(spinnerArrayAdapter1); // set the adapter to provide layout of rows and content
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


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
            goToCart();
            return true;
        }
        if (id == R.id.location) {
            Intent intent=new Intent(ProductPage.this,SelectCity.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.sign_in) {
            Intent intent = new Intent(ProductPage.this, LoginActivity.class);
            startActivity(intent);

            // Handle the camera action.
        } else if (id == R.id.sign_up) {
            Intent intent = new Intent(ProductPage.this, SignUp.class);
            startActivity(intent);

        } else if (id == R.id.my_account) {

        } else if (id == R.id.my_order) {

        } else if (id == R.id.about_tiffindaddy) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, ProductPage.this);
    }

    /**
     * implement locati api for getting current city to match in cities from server.
     */


    @Override
    public void onLocationChanged(Location location) {

        //Toast.makeText(getApplicationContext(), "Location =" + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();


        if(!citySelected){
            matchCurrentCity(location);
        }


        if (!isLocationRequestReset) {
            isLocationRequestReset = true;
            mLocationRequest = ManageLocations.createLocationRequest(120000, 60000);
            if (mGoogleApiClient.isConnected()/* && !mRequestingLocationUpdates*/) {
                startLocationUpdates();
            }


        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * connect to google services.
     */


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    /**
     * check if  current city exist in server cities.
     */

    private void matchCurrentCity(Location location) {
        String matchedCity = "";

        ManageLocations manageLocations = new ManageLocations();
        String currentCity = manageLocations.getAddress(getApplicationContext(), location.getLatitude(), location.getLongitude());
        boolean isExist = false;
        for (int num = 0; num < cities.length; num++) {
            if (currentCity.toLowerCase().contains(cities[num].getCityName().toLowerCase())) {
                isExist = true;
                mySharedData.setGeneralSaveSession(MySharedData.SESSION_CITY, cities[num].getCityName());
                mySharedData.setGeneralSaveSession(MySharedData.SESSION_CITY_ID, cities[num].getCityId());
                city_Name.setTitle(cities[num].getCityName());
              //  city_Name.setVisible(true);
             //   locationicon.setVisible(false);
            }
        }

        if (!isExist) {
            mySharedData.setGeneralSaveSession(MySharedData.SESSION_CITY, "");
            mySharedData.setGeneralSaveSession(MySharedData.SESSION_CITY_ID, "");
            city_Name.setTitle("");
          //  city_Name.setVisible(false);
           // locationicon.setVisible(true);
        }

    }


}
