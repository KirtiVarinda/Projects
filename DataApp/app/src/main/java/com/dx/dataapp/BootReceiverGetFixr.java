package com.dx.dataapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dx.getfixrService.LocationTrackerService;

/**
 * Created by dx on 12/16/2014.
 */
public class BootReceiverGetFixr extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * set alarm to get cities again if the phone is rebooted.
         * alarm for 24 hours.
         */
        System.out.println("ddffrrrrr");

        SessionData session=new SessionData(context);
        String userType=session.getGeneralSaveSession(SessionData.privateUserType);
        if(userType.equals("user") || userType.equals("admin")){

            context.startService(new Intent(context, LocationTrackerService.class));

        }else{
            AlarmsManager alarm=new AlarmsManager();
            alarm.setAlarm(10*60*1000l,context);
        }


    }
}
