package app.funcarddeals.com.FuncardService;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Random;

import app.funcarddeals.com.BeanClasses.ReminderBeanClass;
import app.funcarddeals.com.FuncardDatabase.FunCardDealsDatabase;
import app.funcarddeals.com.Manager.ManageLocations;
import app.funcarddeals.com.Manager.ManageNotifications;

public class FuncardService extends Service implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static String STORE_ID = "store_id", STORE_NAME = "store_name", STORE_LAT = "store_latitude", STORE_LNG = "store_longitude";
    public static String FUNCARD_PRODUCT_ID = "funcard_product_id";
    public static String FUNCARD_PRODUCT_NAME = "funcard_product_name";
    public static String FUNCARD_PRODUCT_OFFERS = "funcard_product_offers";
    private Service currentActivity;

    int notificationId;
    String msg;
    Random roundID = new Random();


    /**
     * fixed reminder time
     * repeat after 30 minute
     */
    private int REPEAT_REMINDER_TIME = 1000 * 60 * 30;

    /**
     * notify user if he has less than 400 distance from selected stores in reminders.
     */
    private int DISTANCE_CHECKED = 400;

    private static ReminderBeanClass[] reminderList;

    private static FunCardDealsDatabase funDB;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public Location previousBestLocation = null;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        currentActivity = FuncardService.this;
        buildGoogleApiClient();
        /**
         * initialize database
         */
        funDB = new FunCardDealsDatabase(context);

        mLocationRequest = ManageLocations.createLocationRequest(15000, 10000);

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
      // System.out.println("google api connected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }



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
       // System.out.println("service started");


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


    int i = 0;

    @Override
    public void onLocationChanged(final Location location) {

        if (isBetterLocation(location, previousBestLocation)) {
            previousBestLocation = location;
            notifyIfReminderInRange(location);


        }


    }

    synchronized private void notifyIfReminderInRange(final Location location) {
        new Thread() {
            @Override
            public void run() {
                /**
                 * get data from reminder database
                 */
                reminderList = funDB.getAllReminders();


                /**
                 * check each reminder for notification;
                 */
                for (i = 0; i < reminderList.length; i++) {
                    /**
                     * start different thread for each reminder.
                     */

                    try {

                        /**
                         * set current location of user in Location object.
                         */
                        Location myLocation = new Location("You");
                        myLocation.setLatitude(location.getLatitude());
                        myLocation.setLongitude(location.getLongitude());

                        /**
                         * set store location in location object.
                         */
                        Location storeLocation = new Location("Store");
                        storeLocation.setLatitude(Double.parseDouble(reminderList[i].getREMINDER_STORE_LATITUDE()));
                         storeLocation.setLongitude(Double.parseDouble(reminderList[i].getREMINDER_STORE_LATITUDE()));

                       // storeLocation.setLatitude(30.702656);
                       // storeLocation.setLongitude(76.790099);


                        /**
                         * get distance between two locations.
                         */
                        float distance = myLocation.distanceTo(storeLocation);


                        /**
                         * generate notifications  if distance is less than 400 meter.
                         */
                        if (distance < DISTANCE_CHECKED) {

                            /**
                             * get current system time
                             */
                            long currentTime = System.currentTimeMillis();
                            long reminderTime = Long.parseLong(reminderList[i].getREMINDER_STORE_PRODUCT_TIME());
                            if (currentTime > reminderTime) {
                                reminderTime = currentTime + REPEAT_REMINDER_TIME;

                                funDB.updateReminderTime(reminderList[i].getREMINDER_STORE_PRODUCT_ID(), String.valueOf(reminderTime));

                                notificationId = roundID.nextInt(1000);
                                msg = " You are near " + reminderList[i].getREMINDER_STORE_NAME() + ".";
                                ManageNotifications.funcardNotify(context, reminderList[i], currentActivity, notificationId, msg);
                            }


                        }


                        //System.out.println("distance= " + distance);

                    } catch (Exception e) {
                        System.out.println("not saved " + e);
                    }


                }
            }
        }.start();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }



}