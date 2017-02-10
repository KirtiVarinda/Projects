package de.rwth.setups;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Kirti-PC on 10/10/2016.
 */
public class GetLocation extends Activity implements LocationListener {
    protected LocationManager locationManager;
    protected Context context;
    double lat,lang;


    public GetLocation(){

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }
    @Override
    public void onLocationChanged(Location location) {
        System.out.println("checkkkkkkkkkkkkkk");
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this,location.getLongitude()+ "hlo "+location.getLatitude() , duration);
        toast.show();
        lat = location.getLongitude() ;
        lang = location.getLatitude() ;
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }
}
