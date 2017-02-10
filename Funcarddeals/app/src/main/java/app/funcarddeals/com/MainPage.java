package app.funcarddeals.com;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.location.LocationListener;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import app.funcarddeals.com.BeanClasses.MainPageBeanClass;
import app.funcarddeals.com.CustomListAdapters.MainPageListAdapter;
import app.funcarddeals.com.FuncardService.FuncardService;
import app.funcarddeals.com.Manager.General;
import app.funcarddeals.com.Manager.ManageLocations;
import app.funcarddeals.com.Manager.MapManager;
import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.PairValues;
import app.funcarddeals.com.Manager.ProgressBarManager;
import app.funcarddeals.com.Manager.StandardMenus;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.SharedData.MySharedData;
import app.funcarddeals.com.network.ServerSync;


public class MainPage extends Activity implements StandardMenus, OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private boolean isLocationRequestReset=false;

    private String FUNCRAD_USER_ID = "funcard_user_id";
    public static String KEY_CITYID = "city_id";
    private boolean syncCities;
    private String jsonCities;
    private ProgressBarManager progressBarManager;

    private static boolean denyOnce=false;


    /**
     * list loading error layout
     */

    LinearLayout networkError;

    /**
     * general class reference for change activity.
     */
    private static SwitchActivities switchActivity;

    /**
     * Reference variable for Shared Data (Session).
     */
    private static MySharedData mySharedData;

    /**
     * Funcard Cities url.
     */
    private static String FUNCARD_CITIES_URL;

    /**
     * boolean variable for only first load map with animation.
     */

    /**
     * Array of object that stores data for city.
     */
    MainPageBeanClass[] mainPageBeanClasses;

    /**
     * Array of city id.
     */
    private String[] allCityID;
    private static ListView cityList;
    private boolean firstAnim = true;
    private Marker marker;
    private static float zoomValue = 14.8f;
    private LocationRequest mLocationRequest;
    private double mLatitude, mLogitude;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap map;
    private Activity currentActivity;
    private Context context;
    private MenuManager menuManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        context = getApplicationContext();
        syncCities = true;
        networkError = (LinearLayout) findViewById(R.id.network_error);
        ImageView imageView = (ImageView) findViewById(R.id.imageView8);
        /**
         * start service for location tracking
         */
        startService(new Intent(context, FuncardService.class));


        /**
         * check for the location setting
         */
        General.checkLocationSetting(this,context);

        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();

        /**
         * gGet cities url from string resource.
         */

        FUNCARD_CITIES_URL = context.getResources().getString(R.string.funcrad_cities);

        /**
         * Initialize Shared data
         */
        mySharedData = new MySharedData(context);

        /**
         * current activity reference
         */
        currentActivity = MainPage.this;

        // Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        // startActivity(viewIntent);


        menuManager = new MenuManager(context);

        /**
         * call Google api client for map and location.
         */
        buildGoogleApiClient();


        /**
         * make a location request for location updates.
         */


//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Check Permissions Now
//            private static final int REQUEST_LOCATION = 2;
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                // Display UI and wait for user interaction
//            } else {
//                ActivityCompat.requestPermissions(
//                        this, new String[]{Manifest.permission.LOCATION_FINE},
//                        ACCESS_FINE_LOCATION);
//            }
//        } else {
//            // permission has been granted, continue as usual
//            Location myLocation =
//                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        }





        mLocationRequest = ManageLocations.createLocationRequest(10000,5000);





        /**
         * load fragment map.
         */
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        /**
         * Get listview reference.
         */
        cityList = (ListView) findViewById(R.id.cities);
        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectCityId = parent.getItemAtPosition(position).toString();
                mySharedData.setGeneralSaveSession(MySharedData.FUNCARD_CITY_ID, selectCityId);
                switchActivity.openActivity(currentActivity, CategoryPage.class, new String[]{KEY_CITYID}, new String[]{selectCityId});

            }
        });

        progressBarManager = new ProgressBarManager(currentActivity);


        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                Toast.makeText(getApplicationContext(), "Clicked Second Image",
//                        Toast.LENGTH_SHORT).show();
                openOptionsMenu();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common_menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (MenuManager.selectedMenu(id, currentActivity)) return true;


        return super.onOptionsItemSelected(item);
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
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();


        /**
         * Resume getting locations.
         */
        if (mGoogleApiClient.isConnected()/* && !mRequestingLocationUpdates*/) {
            startLocationUpdates();
        }


        /**
         * get cities json from session.
         */
        jsonCities = getCityJsonToSession();


        /**
         * start progress bar.
         */
        progressBarManager.startProgressBar();


        /**
         * Sync cities from server only if the new instance of activity is created
         * and if there is no cities json stored in session.
         */
        if (jsonCities.equals("") || syncCities) {
            /**
             * Get cities json from funcard server.
             */
            syncCities = false;
            getCitiesFromServerSetView();

        }

        new Thread() {
            public void run() {
                /**
                 * set cities from session.
                 */

                if (!jsonCities.equals("")) {
                    readDataFromJasonAndSetInView(jsonCities);
                }

            }
        }.start();


    }


    /**
     * Get cities json from funcard server.
     */
    private void getCitiesFromServerSetView() {

        new Thread() {
            public void run() {

                /**
                 * Get user id from session.
                 */
                String USER_ID = mySharedData.getGeneralSaveSession(MySharedData.FUNCARDSERVER_USER_ID);


                /**
                 * make key and values array for all the strings.
                 * index of both should be same.
                 */
                String key[] = {FUNCRAD_USER_ID};      // set keys
                String value[] = {USER_ID};                //set values


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
                String reponse = serverSync.SyncServer(theMap, FUNCARD_CITIES_URL);
              // System.out.println("Response1234"+reponse);



                /**
                 * Check for error in response.
                 */
                if (reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || reponse.equals(ServerSync.DEFAULT_RESPONSE_VALUE)) {

                    /**
                     * if there is a network problem while loading cities from server for the first time.
                     * them shows the network error below the listview on page.
                     */
                    if (jsonCities.equals("")) {
                        MainPage.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                networkError.setVisibility(View.VISIBLE);
                            }
                        });

                    }


                    progressBarManager.stopProgressBar();

                } else {

                    if (reponse.equals(ServerSync.FUNCARD_USER_DISABLED) || reponse.equals("funcarddeals_sorry")) {

                        /**
                         * give message to user and log out him from app if disable from server.
                         */
                        menuManager.logoutDisableUser(currentActivity, LoginScreen.class);


                        progressBarManager.stopProgressBar();

                    } else if (reponse.equals(ServerSync.INVALID_FUNCARD_USER)) {

                        MainPage.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Server Problem", Toast.LENGTH_LONG).show();
                            }
                        });





                    }


                    else {

                        setCityJsonToSession(reponse);
                        readDataFromJasonAndSetInView(reponse);

                    }


                }


            }
        }.start();
    }

    /**
     * Set json response to session.
     *
     * @param jsonReponse
     */
    private void setCityJsonToSession(String jsonReponse) {
        mySharedData.setGeneralSaveSession(MySharedData.FUNCARD_CITY_JSON, jsonReponse);

    }

    /**
     * get json response to session.
     */
    private String getCityJsonToSession() {
        return mySharedData.getGeneralSaveSession(MySharedData.FUNCARD_CITY_JSON);
    }

    private void readDataFromJasonAndSetInView(String jsonData) {
        try {

            MainPageBeanClass tempBean;
            JSONObject jsonRootObject = new JSONObject(jsonData);
            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray = jsonRootObject.optJSONArray("City");

            /**
             * initialize the beansClass array of object.
             */
            mainPageBeanClasses = new MainPageBeanClass[jsonArray.length()];
            allCityID = new String[jsonArray.length()];
            /**Iterate the jsonArray and print the info of JSONObjects*/

            for (int i = 0; i < jsonArray.length(); i++) {
                tempBean = new MainPageBeanClass();
                JSONObject jsonObject = jsonArray.getJSONObject(i);


                String id = jsonObject.getString("id");           //City id
                String name = jsonObject.getString("name");       //City name
                String cprice = jsonObject.getString("cprice");   //City price
                String count = jsonObject.getString("count");     // offer counts
                String status = jsonObject.getString("status");   //status city enable or disable.
                tempBean.setCity_id(id);
                tempBean.setCity_name(name);
                tempBean.setCity_cprice(cprice);
                tempBean.setCity_count(count);
                tempBean.setCity_status(status);

                allCityID[i] = id;
                mainPageBeanClasses[i] = tempBean;


            }

            /**
             * set the values to layout after getting from json.
             */
            setListView();
        } catch (JSONException e) {
            MainPage.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarManager.stopProgressBar();
                }
            });
            e.printStackTrace();
        }


    } // protected void onPostExecute(Void v)

    /**
     * set list view.
     */
    private void setListView() {
        final MainPageListAdapter cityAdapter = new MainPageListAdapter(context, R.layout.mainpage_list_row, allCityID, mainPageBeanClasses);
        MainPage.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cityList.setAdapter(cityAdapter);
                progressBarManager.stopProgressBar();
            }
        });
    }


    /**
     * resfresh listview
     *
     * @param view
     */
    public void refreshList(View view) {
        networkError.setVisibility(View.INVISIBLE);
        /**
         * start progress bar.
         */
        progressBarManager.startProgressBar();


        /**
         *  sync cities on click refresh cities.
         */
        getCitiesFromServerSetView();


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

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


    private static final int REQUEST_LOCATION = 2;
    protected void startLocationUpdates() {



        // System.out.println("google api connected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now

            if(!denyOnce){
                ActivityCompat.requestPermissions(MainPage.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);

                denyOnce=true;
            }

        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }



    }



    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                LocationServices.FusedLocationApi.requestLocationUpdates(
//                        mGoogleApiClient, mLocationRequest, this);



            } else {
                // Permission was denied or request was cancelled
            }
        }
    }


    @Override
    public void onConnected(Bundle bundle) {



        //  if (mRequestingLocationUpdates) {
        startLocationUpdates();
        // }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {


        if (location != null) {

            mLatitude = location.getLatitude();
            mLogitude = location.getLongitude();


            /**
             * remove the previous marker
             */
            if (marker != null) {
                marker.remove();
            }


            /**
             * Add a marker.
             */
            marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(mLatitude, mLogitude))
                    .title("You")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.youpin)));


            if(!isLocationRequestReset){
                isLocationRequestReset=true;
                mLocationRequest = ManageLocations.createLocationRequest(120000, 60000);
                if (mGoogleApiClient.isConnected()/* && !mRequestingLocationUpdates*/) {
                    startLocationUpdates();
                }


            }


            /**
             * animation map camera to current location.
             */
            MapManager moveToCurrent = new MapManager();
            if (firstAnim) {
                moveToCurrent.reMoveMapPosition(true, currentActivity, map, mLatitude, mLogitude, mLatitude, mLogitude, 0, zoomValue);
            } else {
                moveToCurrent.reMoveMapPosition(true, currentActivity, map, mLatitude, mLogitude, mLatitude, mLogitude, zoomValue, zoomValue);
            }
            firstAnim = false;
        }
    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onBackPressed() {
        /**
         * Nothing happen on click of back press button
         */

    }

    @Override
    public void home(View view) {

    }

    @Override
    public void favorite(View view) {
        switchActivity.openActivity(currentActivity, FuncardFavourites.class);
    }

    @Override
    public void reminder(View view) {
        switchActivity.openActivity(currentActivity, FuncardReminders.class);
    }

    @Override
    public void scanner(View view) {
        switchActivity.openActivity(currentActivity, BarCodeScanner.class);

    }



    public void exit(View view) {
        switchActivity.openActivityFinishCurrent(currentActivity, AddFunCrad.class);
//        menuManager=new MenuManager(this);
//        menuManager.closeAppGoToDeviceHome(currentActivity);
    }

    public void menu(View view) {
        menuManager=new MenuManager(this);
        menuManager.openMenu(currentActivity);
    }

}
