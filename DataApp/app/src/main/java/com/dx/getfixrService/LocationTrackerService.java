package com.dx.getfixrService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.dx.dataapp.AlarmsManager;
import com.dx.dataapp.DatabaseHandler;
import com.dx.dataapp.GeneralMethods;
import com.dx.dataapp.SessionData;
import com.dx.model.FixrLocation;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

public class LocationTrackerService extends Service implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //public static final String BROADCAST_ACTION = "Tab Location";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    //private static final long TIME_INTERVAL= 60*1000;    // one minute
    //private static final long DISTANCE_INTERVAL= 10;    // 10 meters
    //public LocationManager locationManager;
   // public MyLocationListener listener;
    public Location previousBestLocation = null;
    private DatabaseHandler dbHandle;
    private SessionData session;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Intent intent;
    int counter = 0;
    String userNanme;
    @Override
    public void onCreate() {
        super.onCreate();
        dbHandle=new DatabaseHandler(getApplicationContext());
        session=new SessionData(getApplicationContext());
        userNanme=session.getGeneralSaveSession(SessionData.privateUserName);
        buildGoogleApiClient();
        mLocationRequest = createLocationRequest();
        /**
         * set alarm for 10 minute
         * for send location to server.
         */
        AlarmsManager  mngr=new AlarmsManager();
        mngr.setAlarm(10*60*1000l,getApplicationContext());


       // intent = new Intent(BROADCAST_ACTION);
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
        mGoogleApiClient.connect();
    }


    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
       // listener = new MyLocationListener();
      //  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,TIME_INTERVAL , DISTANCE_INTERVAL, listener);
      //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_INTERVAL , DISTANCE_INTERVAL, listener);


        return START_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    /**
     * Checks whether two providers are the same
     */

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        stopLocationUpdates();
        //locationManager.removeUpdates(listener);
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(final Location location) {

             if (isBetterLocation(location, previousBestLocation)) {
                 previousBestLocation=location;
                location.getLatitude();
                location.getLongitude();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            session.setGeneralSaveSession(SessionData.privateDataLatitude, location.getLatitude() + "");
                            session.setGeneralSaveSession(SessionData.privateDataLongitude, location.getLongitude() + "");
                            dbHandle.saveTabLocation(new FixrLocation(userNanme, location.getLatitude() + "", location.getLongitude() + "", GeneralMethods.currentTimeInMillisec() + ""));
                        } catch (Exception e) {
                            System.out.println("not saved " + e);
                        }
                    }
                }.start();


                // intent.putExtra("Latitude", loc.getLatitude());
                // intent.putExtra("Longitude", loc.getLongitude());
                // intent.putExtra("Provider", loc.getProvider());
                //  sendBroadcast(intent);
                //  Toast.makeText(getApplicationContext(), "Location == lat:-"+location.getLatitude()+" , long:-"+location.getLongitude(), Toast.LENGTH_SHORT).show();
                // System.out.println( "Location == lat:-"+location.getLatitude()+" , long:-"+location.getLongitude());

             }


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

/*
    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            if (isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            session.setGeneralSaveSession(SessionData.privateDataLatitude,loc.getLatitude() + "");
                            session.setGeneralSaveSession(SessionData.privateDataLongitude,loc.getLongitude() + "");
                            dbHandle.saveTabLocation(new FixrLocation(userNanme, loc.getLatitude() + "", loc.getLongitude() + "", GeneralMethods.currentTimeInMillisec() + ""));
                        }catch(Exception e){
                            System.out.println("not saved "+e);
                        }
                    }
                }.start();





               // intent.putExtra("Latitude", loc.getLatitude());
               // intent.putExtra("Longitude", loc.getLongitude());
               // intent.putExtra("Provider", loc.getProvider());
              //  sendBroadcast(intent);
                //Toast.makeText(getApplicationContext(), "Location == lat:-"+loc.getLatitude()+" , long:-"+loc.getLongitude(), Toast.LENGTH_SHORT).show();
            }
        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }
*/


    /**
     *Create the location request and set the parameters gives
     *
     */
    public static LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return mLocationRequest;
    }




}