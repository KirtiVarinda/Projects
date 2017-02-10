package app.funcarddeals.com.Manager;

import com.google.android.gms.location.LocationRequest;

/**
 * Created by dx on 6/20/2015.
 */
public class ManageLocations {
    /**
     *Create the location request and set the parameters gives
     *
     */
    public static LocationRequest createLocationRequest(int interval,int fastestInterval) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastestInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return mLocationRequest;
    }
}
