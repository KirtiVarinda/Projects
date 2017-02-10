package com.tiffindaddy.app.tiffindaddy.Manager;

import android.app.Activity;
import android.app.ProgressDialog;

import com.tiffindaddy.app.tiffindaddy.R;


/**
 * Created by dx on 8/3/2015.
 */
public class ProgressBarManager {
    Activity currentActivity;
    ProgressDialog progressDialog;

    public ProgressBarManager(Activity currentActivity) {
        this.currentActivity = currentActivity;
        progressDialog = new ProgressDialog(currentActivity);
    }

    public void startProgressBar() {


        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);


    }

    public void stopProgressBar() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
