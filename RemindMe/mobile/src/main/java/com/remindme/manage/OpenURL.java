package com.remindme.manage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by dx on 7/16/2015.
 */
public class OpenURL {

    public static void openUrlInBrowser(Activity ctx,String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        ctx.startActivity(i);
    }

}
