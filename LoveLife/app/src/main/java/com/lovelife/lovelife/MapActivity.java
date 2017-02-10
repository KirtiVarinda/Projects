package com.lovelife.lovelife;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lovelife.lovelife.LoveLifeUtility.MapManager;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {


    /**
     * current activity instance
     */
    private MapActivity currentActivity;
    private static float zoomValue = 14.8f;
    double lat= 30.721552;
    double lang=76.768684;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemes);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        /** initialize current ativity */
        currentActivity = MapActivity.this;


    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {


        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(lat,lang);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        new Thread() {
            public void run() {
                MapManager moveToCurrent = new MapManager();
                moveToCurrent.reMoveMapPosition(true, currentActivity, googleMap, lat, lang, lat, lang, 0, zoomValue);

            }
        }.start();


    }
}