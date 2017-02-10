package com.tiffindaddy.app.tiffindaddy.Manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.android.gms.location.LocationRequest;
import com.tiffindaddy.app.tiffindaddy.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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



    /**
     * method for checking location setting in device
     */
    public static void checkLocationSetting(final Activity activity, Context ctx) {


        if (!isLocationEnabled(ctx)) {


            /**
             * Alert box should run on UIThread.
             */
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setCancelable(false);
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setTitle("Alert!");
                    builder.setMessage("Please enable locations.");
                    // builder.setCustomTitle(title);
                    //builder.setView(message);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivity(callGPSSettingIntent);
                            dialog.cancel();

                        }
                    });

                    builder.create().show();
                }
            });


        }

    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }




    /**
     * get the current city with latitude and longitude.
     *
     * @param ctx
     * @param lat
     * @param lang
     * @return
     */
    public static String getCurrentity(Context ctx, double lat, double lang) {
        String currentCity = "";
        Geocoder gcd = new Geocoder(ctx, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lang, 1);
            if (addresses.size() > 0) {
                final List<Address> finalAddresses = addresses;
                if (finalAddresses.get(0).getLocality().equals("Sahibzada Ajit Singh Nagar")) {
                    currentCity = "Mohali";
                } else {
                    currentCity = finalAddresses.get(0).getLocality();
                }

            } else {
                currentCity = "";
            }
        } catch (IOException e) {
            currentCity = "";
            e.printStackTrace();
        }
        return currentCity;
    }

    public  String getAddress(Context ctx, double latitude, double longitude) {
        String currentCity = "";
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address>  addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                String locality=address.getLocality();
                if(locality==null){
                    locality="";
                }
                if(locality.toLowerCase().contains("sahibzada ajit singh nagar")){
                    locality="Mohali";
                }

                String city=address.getCountryName();
                String region_code=address.getCountryCode();
                String zipcode=address.getPostalCode();

                result.append(locality+" ");
                result.append(city+" "+ region_code+" ");
                result.append(zipcode);

            }
            currentCity= result.toString();
        } catch (IOException e) {
            currentCity="";        }

        System.out.println("address+"+currentCity);
        return currentCity;
    }

}
