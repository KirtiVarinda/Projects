package com.remindme;

import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.shephertz.app42.paas.sdk.android.util.Util;
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class SplashScreen extends Activity {


    SharedPreferences prefs;
    SharedPreferences remindMessagePref;
    static boolean settingDone = false;
    TextView stripImage, stripRound, text, leftButton, rightButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
        SharedPreferences.Editor editor = remindMessagePref.edit();
        editor.putString("syncing", "true");
        editor.commit();


        remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
        String contact = remindMessagePref.getString("key_contact", null);


        /**
         * fetch contacts.
         */
        if (contact == null) {

            defaultAlarm(getApplicationContext());
        }


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime1", false)) {
            setContentView(R.layout.activity_splashscreen);
            runOnce();
            getTextViewImages();

        } else {

            mainActivity();

        }


    }

    protected void runOnce() {
        remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
        SharedPreferences.Editor editor = remindMessagePref.edit();
        editor.putString("theme_selector", "purple");
        editor.putString("syncing", "true");
        editor.putString("vibration", "yes");
        editor.putString("Rating", "none");
        editor.putString("RatingTime", "10");
        editor.putString("snoozPeriod", "5");
        editor.putString("snoozVolume", "80");
        editor.putString("uri10", "");
        editor.commit();
        /**=============add shortcut on home screen==============*/
        if (!prefs.getBoolean("firstTime2", false)) {
            //StartService();

            /**
             * for push notifications
             *
             * App42API.initialize(this,"a21ddb80e16bebbf940743aaaed5274269e240b74f0d6b493d281248e7907914","0b3b84155746efb6dbd150773709c0d1db65f83e58a303b0d133a71482146f82");
             *App42API.setLoggedInUser("avnish.dx@gmail.com") ;
             *com.shephertz.app42.paas.sdk.android.util.Util.registerWithApp42("<Your Google Project No>");
             *
             */
            Intent shortcut = new Intent(getApplicationContext(), SplashScreen.class);
            shortcut.setAction(Intent.ACTION_MAIN);
            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut);
            intent.putExtra("duplicate", false);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Remind Me");
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                            R.mipmap.launcher));
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(intent);
            SharedPreferences.Editor editor1 = prefs.edit();
            editor1.putBoolean("firstTime2", true);
            editor1.commit();
			/**=============add shortcut on home screen==============*/


        }

    }

    protected void mainActivity() {

        Intent in = new Intent(SplashScreen.this, MainReminder.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(in);
        finish();
        overridePendingTransition(0, 0);
    }

    protected void getTextViewImages() {
        stripImage = (TextView) findViewById(R.id.textView1);
        text = (TextView) findViewById(R.id.textView2);
        leftButton = (TextView) findViewById(R.id.textView5);
        rightButton = (TextView) findViewById(R.id.textView4);
        stripRound = (TextView) findViewById(R.id.textView6);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (settingDone) {
            SharedPreferences.Editor editor1 = prefs.edit();
            editor1.putBoolean("firstTime1", true);
            editor1.commit();
            mainActivity();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    protected void defaultAlarm(Context context) {
        SelectTime st = new SelectTime();
        GregorianCalendar calendar = new GregorianCalendar(st.mYear, st.mMonth, st.mDay, st.mHour, st.mMinute);
        Intent intentAlarm = new Intent(context, ContactReceiver.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(context, 100000, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

    }


    /**
     * method to implement custom button
     */
    public void custom(View view) {
        Intent in = new Intent(SplashScreen.this, Setting.class);
        startActivity(in);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_in);
    }


    /**
     * method to implement default button
     */
    public void appDefault(View view) {

        SharedPreferences.Editor editor1 = prefs.edit();
        editor1.putBoolean("firstTime1", true);
        editor1.commit();
        mainActivity();

    }

}
