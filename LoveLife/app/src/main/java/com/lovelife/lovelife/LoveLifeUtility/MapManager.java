package com.lovelife.lovelife.LoveLifeUtility;

import android.app.Activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by dx on 6/20/2015.
 */
public class MapManager {

    /**
     * move the map to current position with animation.
     */
    public void reMoveMapPosition(boolean loadfirst, Activity ctx, final GoogleMap googleMap, double currentLat, double currentLang, double recurrentLat, double recurrentLang, final float firstZoom, final float secondZoom) {
        final LatLng moveTo = new LatLng(currentLat, currentLang);
        final LatLng moveFrom = new LatLng(recurrentLat, recurrentLang);


        if (loadfirst) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ctx.runOnUiThread(new Runnable() {
            public void run() {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(moveTo, firstZoom)); //

                googleMap.animateCamera(CameraUpdateFactory.zoomIn());

                googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.8f), 3000, null);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(moveFrom)      // Sets the center of the map to Mountain View
                        .zoom(secondZoom)                   // 13.6f Sets the zoom
                        .bearing(20)                // Sets the orientation of the camera to east
                        .tilt(60)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }







}
