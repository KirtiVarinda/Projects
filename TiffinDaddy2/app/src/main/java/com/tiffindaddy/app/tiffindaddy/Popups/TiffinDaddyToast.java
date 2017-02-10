package com.tiffindaddy.app.tiffindaddy.Popups;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by dx on 8/26/2015.
 */
public class TiffinDaddyToast {

    public static void showToast(String message, Context context) {


        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();


    }


}
