package com.lovelife.lovelife.LoveLifeUtility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;

import com.lovelife.lovelife.R;

/**
 * Created by dx on 8/3/2015.
 */
public class ProgressBarManager {
    Activity currentActivity;
    static ProgressDialog progressDialog;

    public ProgressBarManager(Activity currentActivity) {
        this.currentActivity = currentActivity;
        progressDialog = new ProgressDialog(currentActivity);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public static void startProgressBar() {
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);


    }

    public static void stopProgressBar() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }
}
