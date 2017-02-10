package com.dx.dataapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BroadcastReceiverForLocation extends BroadcastReceiver {
    public BroadcastReceiverForLocation() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        System.out.println("test");

        /**
         * get locations data from database
         * and send to the server.
         *
         */
        GeneralMethods.getLocationsFromServerAndSendToServer(context);
       // Toast.makeText(context, "location send receiver", Toast.LENGTH_SHORT).show();

        /**
         * set alarm for 10 minute
         * for send location to server.
         */
        AlarmsManager  mngr=new AlarmsManager();
        mngr.setAlarm(10*60*1000l, context);

    }


}
