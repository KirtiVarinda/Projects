package com.lovelife.lovelife;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.lovelife.lovelife.SharedData.MySharedData;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by dx on 11/17/2016.
 */
public class LoveLifeApplication extends Application {



    /** parameter key to send FCM registration ID */
    public static String FCM_REGISTERATION_ID="fcm_id";


    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Application runsApplication runs.");
        printHashKey();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    /**
     * Method to get the hash key.
     */
    public void printHashKey() {


        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.lovelife.lovelife",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("FunacrddealsHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
              /*  MySharedData my = new MySharedData(this);
                my.setGeneralSaveSession("hash", Base64.encodeToString(md.digest(), Base64.DEFAULT));*/

             //   Toast.makeText(LoveLifeApplication.this, Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }
}
