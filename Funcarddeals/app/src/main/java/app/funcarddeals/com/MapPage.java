package app.funcarddeals.com;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.funcarddeals.com.Manager.General;
import app.funcarddeals.com.Manager.ManageLocations;
import app.funcarddeals.com.Manager.MapManager;
import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.StandardMenus;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.MapManager.JSONParser;
import app.funcarddeals.com.Popups.FuncardPopups;


public class MapPage extends Activity implements StandardMenus, OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static final int STORE_DISTANCE= 5000;
    String pathUrl;
    /**
     * boolean variable for only first load map with animation.
     */

    private static boolean denyOnce=false;


    private boolean firstAnim = true;
    private Marker marker;
    private static float zoomValue = 14.8f;
    private LocationRequest mLocationRequest;
    double mLatitude, mLogitude;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap map;
    private Activity currentActivity;
    private MenuManager menuManager;
    private Context context;
    MapFragment mapFragment;
    /**
     * general class reference for change activity.
     */
    private static SwitchActivities switchActivity;

    /**
     * variables to get values from previous activity that are send with intent
     */
    String store_id, store_name, store_lat, store_lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_page);
        context = getApplicationContext();
        menuManager= new MenuManager(this);
        /**
         * current activity reference
         */
        currentActivity = this;

        /**
         * check for the location setting
         */
        General.checkLocationSetting(this, context);

        /**
         * Initialize bottom menus.
         */
        menuManager = new MenuManager(context);
        /**
         * call Google api client for map and location.
         */
        buildGoogleApiClient();

        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();

        /**
         * make a location request for location updates.
         */
        mLocationRequest = ManageLocations.createLocationRequest(10000, 5000);


        /**
         * Get values from the previous activity send in putExtra.
         */
        store_id = getIntent().getStringExtra(Stores.STORE_ID);
        store_name = getIntent().getStringExtra(Stores.STORE_NAME);
        store_lat = getIntent().getStringExtra(Stores.STORE_LAT);
        store_lng = getIntent().getStringExtra(Stores.STORE_LNG);





        TextView textView = (TextView) findViewById(R.id.textView18);
        textView.setText(store_name);


        /**
         * load fragment map.
         */
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
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
                ActivityCompat.requestPermissions(MapPage.this,
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


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
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

        if (MenuManager.selectedMenu(id, currentActivity)) return true;


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

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

            marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(store_lat), Double.parseDouble(store_lng)))
                    .title("Store")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.storepin)));
            /**
             * animation map camera to current location.
             */
            MapManager moveToCurrent = new MapManager();
            if (firstAnim) {

                /**
                 moveToCurrent.reMoveMapPosition(true, currentActivity, map, mLatitude, mLogitude, mLatitude, mLogitude, 0, zoomValue);
                 moveToCurrent.reMoveMapPosition(true, currentActivity, map,52.28582710000001,-1.1141665,52.2777244,-1.1581097, 0, zoomValue);
                 pathUrl=makeURL(52.28582710000001,-1.1141665,52.2777244,-1.1581097);
                 */

                /**
                 * set current location of user in Location object.
                 */
                Location myLocation = new Location("You");
                myLocation.setLatitude(mLatitude);
                myLocation.setLongitude(mLogitude);

                /**
                 * set store location in location object.
                 */
                Location storeLocation = new Location("Store");
                storeLocation.setLatitude(Double.parseDouble(store_lat));
                storeLocation.setLongitude(Double.parseDouble(store_lng));

                // storeLocation.setLatitude(30.702656);
                // storeLocation.setLongitude(76.790099);


                /**
                 * get distance between two locations.
                 */
                float distance = myLocation.distanceTo(storeLocation);


                if(distance>STORE_DISTANCE){
                    moveToCurrent.reMoveMapPosition(true, currentActivity, map, Double.parseDouble(store_lat), Double.parseDouble(store_lng), Double.parseDouble(store_lat), Double.parseDouble(store_lng), 0, zoomValue);

                    FuncardPopups.funcardPopup(currentActivity, getResources().getString(R.string.attention), getResources().getString(R.string.store_distance));
                }else{
                    moveToCurrent.reMoveMapPosition(true, currentActivity, map, mLatitude, mLogitude, mLatitude, mLogitude, 0, zoomValue);

                    pathUrl = makeURL(mLatitude, mLogitude, Double.parseDouble(store_lat), Double.parseDouble(store_lng));

                    new connectAsyncTask().execute();
                }




            } else {
                // moveToCurrent.reMoveMapPosition(true, currentActivity, map, mLatitude, mLogitude, mLatitude, mLogitude, zoomValue, zoomValue);
            }
            firstAnim = false;

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void home(View view) {
        menuManager.goToHomePage(currentActivity, MainPage.class);
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



    public String makeURL(double sourcelat, double sourcelog, double destlat, double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=true&mode=driving&alternatives=true");
        // urlString.append("&key=AIzaSyAOiOX7CQ-WOrOjflpE427cxWfD5aHZUl8");
        return urlString.toString();
    }


    public void drawPath(String result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = map.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(12)
                            .color(Color.parseColor("#FF2A2A"))//Google maps blue color
                            .geodesic(true)
            );
           /*
           for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
                .width(2)
                .color(Color.BLUE).geodesic(true));
            }
           */
        } catch (JSONException e) {

        }
    }


    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private class connectAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(MapPage.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getpathJson(pathUrl);
           // System.out.println("path json= " + json);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            if (result != null) {
                drawPath(result);
            }
        }
    }
}
