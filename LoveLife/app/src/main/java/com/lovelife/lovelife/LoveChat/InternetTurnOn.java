package com.lovelife.lovelife.LoveChat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.lovelife.lovelife.SharedData.MySharedData;

/**
 * Created by Dx on 12/25/2014.
 * runs when internet connectivity changed.
 */
public class InternetTurnOn extends BroadcastReceiver {

    /**
     * session variable
     */
    MySharedData mySharedData;
    Boolean checkNet;


    @Override
    public void onReceive(final Context context, Intent intent) {
        //  SessionData sessionData=new SessionData(context);

        /** initialize session variable */
        mySharedData = new MySharedData(context);
        if (InternetCheck.netCheck(context)) {      /** it will run when internet is connected and within the range. */

            mySharedData.setGeneralSaveSession(MySharedData.CHECKNET, "true");
            Toast.makeText(context, "Internet connected " + mySharedData.getGeneralSaveSession(MySharedData.USERID), Toast.LENGTH_LONG).show();


            /** restart chat service if internet turn on again */
            if (mySharedData.getGeneralSaveSession(MySharedData.COMPLETE_LOGGGED_IN).equals("yes")) {

                Toast.makeText(context, "IStart service " , Toast.LENGTH_LONG).show();
                ChatService.isInternetConnected = true;
                context.startService(new Intent(context, ChatService.class));
            }



          /*  new Thread() {
                public void run() {


                    try {

                        if (MyService.connection != null) {
                            MyService.connection.disconnect();
                            MyService.connection.connect();
                        }

                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }


                }
            }.start();*/


        } else {           // it will run when internet is connected but out of range.

            mySharedData.setGeneralSaveSession(MySharedData.CHECKNET, "false");
            Toast.makeText(context, "Internet disconnected", Toast.LENGTH_LONG).show();
            /** stop chat service if internet turn off */
            if (mySharedData.getGeneralSaveSession(MySharedData.COMPLETE_LOGGGED_IN).equals("yes")) {
                Toast.makeText(context, "Internet stop service", Toast.LENGTH_LONG).show();
                context.stopService(new Intent(context, ChatService.class));
                ChatService.isInternetConnected = false;
            }
        }
    }
}
