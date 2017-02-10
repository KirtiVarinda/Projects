package com.remindme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.remindme.RemindMeSession.SharedData;

/**
 * Created by Dx on 12/25/2014.
 * runs when internet connectivity changed.
 */
public class InternetTurnOn extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        SharedData sessionData = new SharedData(context);
        if (InternetCheck.netCheck(context)) {      // it will run when internet is connected and within the range.
        	//Toast.makeText(context, "internet on", Toast.LENGTH_SHORT);
           // sessionData.setGeneralSaveSession(SharedData.INTERNETCHECK,"InRange") ;
            // Toast.makeText(context, "Internet turn on", Toast.LENGTH_LONG).show();
            new Thread(){
            	public void run(){
            		
            		GeneralClass.sendUserDataToserver(context);
            		
            	}
            }.start();

        } else {    
        	//Toast.makeText(context, "internet off", Toast.LENGTH_SHORT);
        	// it will run when internet is connected but out of range.
        	  // sessionData.setGeneralSaveSession(SharedData.INTERNETCHECK,"OutOfRange") ;
            // Toast.makeText(context, "Internet turn off", Toast.LENGTH_LONG).show();
        }
    }
}
