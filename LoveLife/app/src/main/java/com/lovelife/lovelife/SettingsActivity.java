package com.lovelife.lovelife;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lovelife.lovelife.Popups.LoveLifePopups;
import com.lovelife.lovelife.SharedData.MySharedData;


public class SettingsActivity extends Activity {
    /**
     * session variable
     */
    MySharedData mySharedData;

    /**
     * current activity instance
     */
    private SettingsActivity currentActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemes);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        /** initialize session variable */
        mySharedData = new MySharedData(getApplicationContext());

        /** initialize current activity variable */
        currentActivity = SettingsActivity.this;


    }

    public void logoutApp(View view){
        LoveLifePopups.loveLifePopupFinishCurrentActivityAndLogout(currentActivity, "Alert!", getResources().getString(R.string.logoutAlert), LoginActivity.class, mySharedData);


    }
    public void shareWithFriends(View view){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.share_string));  /** change string resourse to latest LoveLife app's playStore link */
        startActivity(Intent.createChooser(sharingIntent, "Choose LoveLife"));
    }

}
