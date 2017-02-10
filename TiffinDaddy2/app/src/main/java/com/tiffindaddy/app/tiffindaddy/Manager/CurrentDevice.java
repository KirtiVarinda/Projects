package com.tiffindaddy.app.tiffindaddy.Manager;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by Avnish on 2/4/2016.
 */
public class CurrentDevice {

    /**
     * get the device id
     */
    public static String getDeviceId(Context ctx){

    return Settings.Secure.getString(ctx.getContentResolver(),
            Settings.Secure.ANDROID_ID);

    }

    public static String getDeviceName(){
        return android.os.Build.MODEL;
    }

}
