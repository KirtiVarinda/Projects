package com.remindme;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.remindme.RemindMeSession.SharedData;
import com.remindme.manage.ActivitySwitcher;
import com.remindme.manage.RemindMeAnimation;


public class HourlyReminders extends Activity {

    private static int SemiRoundImage[] = {R.drawable.semiround_grey, R.drawable.clicked_change};


    private static TextView mMedicine, mWorkout, mRoutine;


    /**
     * default alarm type is medicine
     */
    private static String selectedReminderType = "medicine";
    private static ActivitySwitcher activitySwitcher;
    private static Context context;
    private static LinearLayout hourly_layout;
    private static TextView medicineName;
    private static SharedData shareData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_reminders);

        /**
         * Activity context
         */
        context = getApplicationContext();

        hourly_layout = (LinearLayout) findViewById(R.id.hourly_layout);
        medicineName = (TextView) findViewById(R.id.textView46);
        mMedicine = (TextView) findViewById(R.id.textView42);
        mWorkout = (TextView) findViewById(R.id.textView43);
        mRoutine = (TextView) findViewById(R.id.textView44);


        activitySwitcher = new ActivitySwitcher(HourlyReminders.this);
        shareData = new SharedData(context);
    }


    /**
     * Method for adding medicine type of reminders.
     */
    public void medicineReminder(View view) {
        selectedReminderType = "medicine";
        selectReminderType(SemiRoundImage[1], SemiRoundImage[0], SemiRoundImage[0]);
    }

    /**
     * Method for adding workout type of reminders.
     */
    public void workoutReminder(View view) {
        selectedReminderType = "workout";
        selectReminderType(SemiRoundImage[0], SemiRoundImage[1], SemiRoundImage[0]);
    }

    /**
     * Method for adding routine type of reminders.
     */
    public void routineReminder(View view) {
        selectedReminderType = "routine";
        selectReminderType(SemiRoundImage[0], SemiRoundImage[0], SemiRoundImage[1]);
    }


    /**
     * Method to get the Medicine name.
     */
    public void getmedicineName(View view) {
        activitySwitcher.switchActivity(ReminderMessage.class, "hourly_reminder", "");

    }


    /**
     * Method to get the Medicine name.
     */
    public void getReminderTimes(View view) {
        activitySwitcher.switchActivity(HourlyReminderTimes.class, "no", "");
    }


    /**
     * Method to get the Medicine name.
     */
    public void getSchedule(View view) {

    }

    /**
     * Method to get the Medicine name.
     */
    public void closePage(View view) {
        finish();
        overridePendingTransition(R.anim.push_down_out, R.anim.push_down_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RemindMeAnimation.leftToRightTranslation(hourly_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * animate layout from right to left.
         */
        RemindMeAnimation.rightToLeftAnimation(hourly_layout);


        /**
         * reset the text in the layout.
         */
        String medicineName = shareData.getGeneralSaveSession(SharedData.REMINDER_MESSAGE);
        if (!medicineName.equals("no")) {
            setTextInLayout(medicineName);
        }


    }


    private void setTextInLayout(String medicineName) {
        this.medicineName.setText(medicineName);
    }


    public static void selectReminderType(int img1, int img2, int img3) {
        mMedicine.setBackgroundResource(img1);
        mWorkout.setBackgroundResource(img2);
        mRoutine.setBackgroundResource(img3);

    }


}
