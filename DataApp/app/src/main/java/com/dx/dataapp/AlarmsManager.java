package com.dx.dataapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.dx.FixrNumberManager.MyParsingMethods;

/**
 * Created by DESIGNERSX on 12/16/2014.
 * this class set the service that run after specific period of time
 */
public class AlarmsManager {


    public void setAlarm(Long milisecond, final Context context) {
        /**
         * this class set the service that run after specific period of time
         * */


        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BroadcastReceiverForLocation.class);
        alarmIntent = PendingIntent.getBroadcast(context, 4, intent, 0);
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + milisecond, alarmIntent);
        //   alarmMgr.set(AlarmManager.RTC_WAKEUP, 1000, alarmIntent);


    }


}
