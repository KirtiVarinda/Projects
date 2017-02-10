package com.dx.dataapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dx.FixrNumberManager.MyParsingMethods;
import com.dx.getfixrService.MyService;

/**
 * Created by Dx on 12/25/2014.
 * runs when internet connectivity changed.
 */
public class InternetTurnOn extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        SessionData sessionData=new SessionData(context);
        if (InternetCheck.netCheck(context)) {      // it will run when internet is connected and within the range.


            context.startService(new Intent(context,MyService.class));
            new Thread(){
                public void run(){

                    MyParsingMethods gm=new MyParsingMethods();
                    try {
                        gm.getCitiesXmlandSaveToDatabase(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                }
            }.start();

        } else {           // it will run when internet is connected but out of range.
            sessionData.setNetworkStatus("OutOfRange");
            context.stopService(new Intent(context,MyService.class));
           // Toast.makeText(context, "Internet turn off", Toast.LENGTH_LONG).show();
        }
    }
}
