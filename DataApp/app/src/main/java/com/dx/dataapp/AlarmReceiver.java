package com.dx.dataapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by dx on 19/03/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    CheckApplications chkApp;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(closeDialog);
         //   System.out.println("receiver");
        if(chkApp==null){
            chkApp=new CheckApplications();
        }
        chkApp.checkRunningApplications(context);

    }
}
