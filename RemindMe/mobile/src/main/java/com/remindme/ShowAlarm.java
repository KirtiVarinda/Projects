package com.remindme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("Wakelock")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ShowAlarm extends Activity implements OnTouchListener, MessageApi.MessageListener, DataApi.DataListener, ConnectionCallbacks, OnConnectionFailedListener {
    public static String START_ACTIVITY_PATH;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient mGoogleApiClient1;
    Context contxt;
    NotificationManager notificationManager;
    Notifications notify = new Notifications();
    String themeType;
    Bitmap[] themeImages;
    int snoozNo = 0;
    String milisecToSnooze = "";
    boolean imgCheck = true;
    int centerY;
    int centerX;
    String snoozVolume;
    long[] vibPattern;
    String vib;
    private TextToSpeech mTts;
    ImageView img1, img2, img3, img4;
    int sdk = android.os.Build.VERSION.SDK_INT;
    AnimationSet animOut, animIn;
    AlphaAnimation fadeOut;
    AlphaAnimation fadeIn;
    String densityCheck = "";
    ScaleAnimation zoomIn;
    Animation zoomIn2;
    //TextView snozText,doneText;


    TextView  smallMsg, bigSubject;


    private static ImageView centerDrag;
    private static ImageView snooz, use;
    private static ImageView smallicon;
    private static LinearLayout purpleStrip;
    ImageView circ;
    ScaleAnimation zoomOut;
    AnimationSet animationSet1, animationSet2, animationSet3, animationSet4;
    Animation fadeAnimation, fadeAnimation2, fadeAnimation3, fadeAnimation4;
    Animation pulse, pulse2, pulse3, pulse4;
    ImageView v;
    ImageView v2, v3, v4;
    boolean snoozSelected = false, useSelected = false;
    boolean dragging = false;
    private RelativeLayout mainLayout;
    Thread th;
    String snoozTime = "";
    boolean smartTime = true;
    SharedPreferences remindMessagePref;
    Vibrator vibrator;
    private MediaPlayer mMediaPlayer; //
    String bd_id, names, recipient, subject, date, time, alarm_period, alarm_type, db_milisec;
    Long id;
    String msg;
    PowerManager.WakeLock wl;
    public List<String[]> reminderWithId = null;
    private DatabaseHandler ourReminders;
    private SQLiteDatabase ourDatabase;
    View layout;
    Window window;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * open screen for alarm.
         */
        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "My Tag");
        wl.acquire();
        setContentView(R.layout.activity_show_alarm);
        contxt = getApplicationContext();


        /**
         * google Api call.
         */
        mGoogleApiClient1 = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(ShowAlarm.this)
                .addOnConnectionFailedListener(this)
                .build();


        notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // TODO for text to speech code   mTts = new TextToSpeech(this, this);
        // mTts.setLanguage(Locale.US);
        zoomIn2 = AnimationUtils.loadAnimation(this, R.anim.zoomin);

        mainLayout = (RelativeLayout) findViewById(R.id.layout1);
        remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
        themeType = remindMessagePref.getString("theme_selector", "");
        int size = remindMessagePref.getInt("theme_size", 0);
        String imageArray[] = new String[size];
        for (int i = 0; i < size; i++) {
            imageArray[i] = remindMessagePref.getString("stringImage_" + i, "");
        }
        themeImages = new Bitmap[size];
        themeImages = Theme.decodeBase64(imageArray);
        checkScreenDensity();

        getTextViewImages();


        setThemeColor();


        imageAnimation();
        snoozTime = remindMessagePref.getString("snoozPeriod", "");
        snoozVolume = remindMessagePref.getString("snoozVolume", "");
        mainLayout.setOnTouchListener(this);
        centerDrag.setOnTouchListener(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getLong("Id");
            msg = extras.getString("Message");
            getData();
            if (themeType.equals("white")) {
                bigSubject.setTextColor(Color.BLACK);
                smallMsg.setTextColor(Color.BLACK);
                setImage(R.drawable.alarmiconsmall_grey, R.drawable.smsiconsmall_gray, R.drawable.emailiconsmall_gray, R.drawable.calliconsmal_grayl, R.drawable.todoiconsmall_gray, R.drawable.eventiconsmall_gray);
            } else {
                bigSubject.setTextColor(Color.WHITE);
                smallMsg.setTextColor(Color.WHITE);
                setImage(R.drawable.alarmiconsmall, R.drawable.smsiconsmall, R.drawable.emailiconsmall, R.drawable.calliconsmall, R.drawable.todoiconsmall, R.drawable.eventiconsmall);
            }
        }


        pulseAnimation();
        smartAlarmSnooz();
        AudioManager manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (manager.getMode() == AudioManager.MODE_IN_CALL || manager.getMode() == AudioManager.MODE_RINGTONE) {
            smartTime = false;
            snooz1(id, msg);
            if (snoozNo == 1) {
                notify.showNotification(id.intValue(), bigSubject.getText().toString(), smallMsg.getText().toString(), contxt, notificationManager, msg, recipient, subject, alarm_period);
            }
            endAlarm();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient1.connect();

    }


    @Override
    protected void onStop() {
        if (null != mGoogleApiClient1 && mGoogleApiClient1.isConnected()) {
            Wearable.MessageApi.removeListener(mGoogleApiClient1, this);
            mGoogleApiClient1.disconnect();
        }
        super.onStop();
    }


    protected void setImage(int wake1, int msg1, int email1, int call1, int todo1, int event1) {

        if (msg.equals("Wake")) {
            centerDrag.setImageResource(R.drawable.reminderalarm2);
            smallicon.setImageResource(wake1);

        } else if (msg.equals("textMsg")) {
            centerDrag.setImageResource(R.drawable.reminderpbgmass);
            smallicon.setImageResource(msg1);
        } else if (msg.equals("email")) {
            centerDrag.setImageResource(R.drawable.reminderpbgemail);
            smallicon.setImageResource(email1);
        } else if (msg.equals("call")) {
            centerDrag.setImageResource(R.drawable.reminderpbgcall);
            smallicon.setImageResource(call1);
        } else if (msg.equals("Todo")) {
            centerDrag.setImageResource(R.drawable.reminderpbgcheck);
            smallicon.setImageResource(todo1);
        } else if (msg.equals("Event")) {
            centerDrag.setImageResource(R.drawable.reminderpbgclen);
            smallicon.setImageResource(event1);
        }

    }


    protected void imageAnimation() {
        if (densityCheck.equals("medium")) {
            zoomOut = new ScaleAnimation(0.5f, 1, 0.5f, 1
                    , Animation.ABSOLUTE
                    , 150
                    , Animation.ABSOLUTE
                    , 150
            );

            zoomOut.setDuration(50);

            zoomIn = new ScaleAnimation(1, 0.8f, 1, 0.8f
                    , Animation.ABSOLUTE
                    , 150
                    , Animation.ABSOLUTE
                    , 150
            );

            zoomIn.setDuration(50);
            fadeIn = new AlphaAnimation(1.0f, 0.0f);
            fadeIn.setDuration(100);
        } else if (densityCheck.equals("low")) {

            zoomOut = new ScaleAnimation(0.5f, 1, 0.5f, 1
                    , Animation.ABSOLUTE
                    , 100
                    , Animation.ABSOLUTE
                    , 100
            );

            zoomOut.setDuration(50);

            zoomIn = new ScaleAnimation(1, 0.8f, 1, 0.8f
                    , Animation.ABSOLUTE
                    , 100
                    , Animation.ABSOLUTE
                    , 100
            );

            zoomIn.setDuration(50);
            fadeIn = new AlphaAnimation(1.0f, 0.0f);
            fadeIn.setDuration(100);
        } else {

            zoomOut = new ScaleAnimation(0.5f, 1, 0.5f, 1
                    , Animation.ABSOLUTE
                    , 200
                    , Animation.ABSOLUTE
                    , 200
            );

            zoomOut.setDuration(50);
            zoomIn = new ScaleAnimation(1, 0.8f, 1, 0.8f
                    , Animation.ABSOLUTE
                    , 200
                    , Animation.ABSOLUTE
                    , 200
            );

            zoomIn.setDuration(50);
            fadeIn = new AlphaAnimation(1.0f, 0.0f);
            fadeIn.setDuration(100);
        }
        fadeOut = new AlphaAnimation(0.0f, 1.0f);
        fadeOut.setDuration(250);
        animOut = new AnimationSet(false);
        animIn = new AnimationSet(false);
        animOut.addAnimation(zoomOut);
        animOut.addAnimation(fadeOut);
        animIn.addAnimation(fadeIn);
        animIn.addAnimation(zoomIn);
    }

    protected void getTextViewImages() {
        purpleStrip = (LinearLayout) findViewById(R.id.textView3);
        centerDrag = (ImageView) findViewById(R.id.textView56);
        snooz = (ImageView) findViewById(R.id.textView6);
        use = (ImageView) findViewById(R.id.textView7);
        smallicon = (ImageView) findViewById(R.id.textView4);
        bigSubject = (TextView) findViewById(R.id.textView5);
        smallMsg = (TextView) findViewById(R.id.textView8);
        circ = (ImageView) findViewById(R.id.textView20);
        img1 = (ImageView) findViewById(R.id.imageView1);
        img2 = (ImageView) findViewById(R.id.imageView2);
        img3 = (ImageView) findViewById(R.id.imageView3);
        img4 = (ImageView) findViewById(R.id.imageView4);

    }


    /**
     * Method to set the theme color to the textbar.
     */
    protected void setThemeColor() {
        if (!themeType.equals("purple")) {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                purpleStrip.setBackgroundDrawable(new BitmapDrawable(getResources(), themeImages[2]));
            } else {
                purpleStrip.setBackground(new BitmapDrawable(getResources(), themeImages[2]));
            }
        }

    }

    protected void pulseAnimation() {
        v2 = (ImageView) findViewById(R.id.imageView2);
        v = (ImageView) findViewById(R.id.imageView1);
        v3 = (ImageView) findViewById(R.id.imageView3);
        v4 = (ImageView) findViewById(R.id.imageView4);
        pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        pulse2 = AnimationUtils.loadAnimation(this, R.anim.pulse);
        pulse3 = AnimationUtils.loadAnimation(this, R.anim.pulse);
        pulse4 = AnimationUtils.loadAnimation(this, R.anim.pulse);
        fadeAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeAnimation.setDuration(3000);
        fadeAnimation.setRepeatCount(Animation.INFINITE);
        fadeAnimation2 = new AlphaAnimation(1.0f, 0.0f);
        fadeAnimation2.setDuration(3000);
        fadeAnimation2.setRepeatCount(Animation.INFINITE);
        fadeAnimation3 = new AlphaAnimation(1.0f, 0.0f);
        fadeAnimation3.setDuration(3000);
        fadeAnimation3.setRepeatCount(Animation.INFINITE);
        fadeAnimation4 = new AlphaAnimation(1.0f, 0.0f);
        fadeAnimation4.setDuration(3000);
        fadeAnimation4.setRepeatCount(Animation.INFINITE);
        animationSet1 = new AnimationSet(false);
        animationSet1.addAnimation(pulse);
        animationSet1.addAnimation(fadeAnimation);
        animationSet1.setRepeatCount(Animation.INFINITE);
        animationSet1.setDuration(3000);
        animationSet2 = new AnimationSet(false);
        animationSet2.addAnimation(pulse2);
        animationSet2.addAnimation(fadeAnimation2);
        animationSet2.setRepeatCount(Animation.INFINITE);
        animationSet2.setDuration(3000);
        animationSet3 = new AnimationSet(false);
        animationSet3.addAnimation(pulse3);
        animationSet3.addAnimation(fadeAnimation3);
        animationSet3.setRepeatCount(Animation.INFINITE);
        animationSet3.setDuration(3000);
        animationSet4 = new AnimationSet(false);
        animationSet4.addAnimation(pulse4);
        animationSet4.addAnimation(fadeAnimation4);
        animationSet4.setRepeatCount(Animation.INFINITE);
        animationSet4.setDuration(3000);
    }

    protected void smartAlarmSnooz() {
        th = new Thread() {
            public void run() {
                // TODO for text to speech code
                try {
                    Thread.sleep(40 * 1000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

                ShowAlarm.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (smartTime) {
                            snooz1(id, msg);
                            if (snoozNo == 1) {
                                notify.showNotification(id.intValue(), bigSubject.getText().toString(), smallMsg.getText().toString(), contxt, notificationManager, msg, recipient, subject, alarm_period);
                            }
                            endAlarm();
                        }
                    }
                });

            }
        };
        th.start();
    }

    public void showToast(String str) {
        Toast tost = Toast.makeText(this, str, Toast.LENGTH_LONG);
        tost.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
        tost.show();
    }


    public void dialNumber() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + recipient));
            startActivity(intent);
        } catch (Exception e) {
            System.out.println("Problem ==" + e);
        }


    }

    public void sendEmail() {
        try {
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            String[] aa = recipient.split(",");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aa);
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Reminder Email");
            emailIntent.setType("message/rfc822");

            this.startActivity(emailIntent);
        } catch (Exception e) {
            showToast("You dont have any email sender");
        }
    }

    public void sendSms2() {
        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        String aa = recipient.replace(",", ";");
        i.putExtra("address", aa);
        i.putExtra("sms_body", subject);
        i.setType("vnd.android-dir/mms-sms");
        this.startActivity(i);
    }

    String sub[];

    public void getData() {
        ourReminders = new DatabaseHandler(this);
        ourDatabase = ourReminders.openConnection(this);
        reminderWithId = ourReminders.selectWithId(id);
        for (final String[] reminderValues : reminderWithId) {
            bd_id = reminderValues[0];
            names = reminderValues[1];
            recipient = reminderValues[2];
            subject = reminderValues[3];
            date = reminderValues[4];
            time = reminderValues[5];
            alarm_period = reminderValues[6];
            alarm_type = reminderValues[7];
            db_milisec = reminderValues[9];
            String snoozee[] = reminderValues[10].split("_");
            snoozNo = Integer.parseInt(snoozee[0]);
            milisecToSnooze = snoozee[1];
            if (snoozee[1].equals("no")) {
                milisecToSnooze = db_milisec;
            }

            sub = alarm_period.split("_");
            String removeComman = "Unknown person";
            String[] numberOfContact = reminderValues[1].split(",");
            int count = 0;
            for (int i = 0; i < numberOfContact.length; i++) {
                count++;
            }
            if (count == 1) {
                if (reminderValues[1].endsWith(",")) {
                    removeComman = reminderValues[1].substring(0, reminderValues[1].length() - 1);
                    String[] firstName = removeComman.split(" ");
                    removeComman = firstName[0];
                } else {
                    removeComman = reminderValues[1];
                    String[] firstName = removeComman.split(" ");
                    removeComman = firstName[0];
                }
            } else {
                removeComman = count + " People";
            }
            if (reminderValues[7].equals("wakeup")) {
                bigSubject.setText("Wake up for " + reminderValues[3]);
            } else if (reminderValues[7].equals("textmessage")) {
                bigSubject.setText("Text " + removeComman + " for " + reminderValues[3]);
            } else if (reminderValues[7].equals("email")) {
                bigSubject.setText("Email " + removeComman + " for " + reminderValues[3]);
            } else if (reminderValues[7].equals("call")) {
                if (reminderValues[3].equals("Subject")) {
                    bigSubject.setText("Call " + removeComman);
                } else {
                    bigSubject.setText("Call " + removeComman + " for " + reminderValues[3]);
                }
            } else if (reminderValues[7].equals("todo")) {
                bigSubject.setText(reminderValues[3]);
            } else if (reminderValues[7].equals("event")) {
                if (sub[0].equals("monthly")) {
                    bigSubject.setText(reminderValues[3]);

                } else if (sub[0].equals("Yearly")) {
                    if (sub[1].equals("birthday")) {
                        bigSubject.setText(sub[2].substring(0, 1).toUpperCase() + sub[2].substring(1) + " " + removeComman + " for Birthday");
                    } else if (sub[1].equals("anniversery")) {
                        bigSubject.setText(sub[2].substring(0, 1).toUpperCase() + sub[2].substring(1) + " " + removeComman + " for Anniversery");
                    } else {
                        bigSubject.setText(sub[2].substring(0, 1).toUpperCase() + sub[2].substring(1) + " " + removeComman + " for " + sub[1]);
                    }
                } else if (sub[0].equals("weekly")) {
                    bigSubject.setText(reminderValues[3]);

                }

            }
            String snoozeNos = "";
            if (!(snoozNo == 0)) {
                snoozeNos = "  ~  SNOOZED x" + snoozNo;
            }
            if (sub[0].equals("everyday")) {
                smallMsg.setText("Everyday of the week at " + reminderValues[5] + snoozeNos);
            } else if (sub[0].equals("weekend")) {
                smallMsg.setText("Weekend at " + reminderValues[5] + snoozeNos);
            } else if (sub[0].equals("weekday")) {
                smallMsg.setText("Weekday at " + reminderValues[5] + snoozeNos);
            } else if (sub[0].equals("monthly")) {
                smallMsg.setText("Monthly at " + reminderValues[5] + snoozeNos);
            } else if (sub[0].equals("Yearly")) {
                smallMsg.setText("on " + reminderValues[4] + " at " + reminderValues[5] + snoozeNos);
            } else if (sub[0].equals("weekly")) {
                smallMsg.setText("Every " + reminderValues[4] + " at " + reminderValues[5] + snoozeNos);

            } else {
                smallMsg.setText("on " + reminderValues[4] + " at " + reminderValues[5] + snoozeNos);
            }
        }
        new DoInBackground().execute("alarm");
        ourReminders.closeConnection();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        smartTime = false;
        wl.release();
        window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    protected void endAlarm() {

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }

        finish();

    }

    protected void snooz1(Long requestCode, String alarmType) {
        new DoInBackground().execute("mCloseWatchFace");
        ourReminders = new DatabaseHandler(ShowAlarm.this);
        ourDatabase = ourReminders.openConnection(ShowAlarm.this);
        snoozNo = snoozNo + 1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milisecToSnooze));
        calendar.add(Calendar.MINUTE, Integer.parseInt(snoozTime));
        Intent intentAlarm = new Intent(this, AlarmReciever.class);
        intentAlarm.putExtra("Message", alarmType);
        intentAlarm.putExtra("Id", requestCode);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int requestCode1 = (int) (long) requestCode;
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(this, requestCode1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        ourReminders.updateSnoozNo(requestCode1, snoozNo + "_" + calendar.getTimeInMillis());
        ourReminders.closeConnection();
    }

    protected void setAlarmAgain(Long requestCode, String alarmType, Calendar calendar) {
        String newDate = SelectTime.milisecToDate(calendar.getTimeInMillis());
        String newTime = SelectTime.milisecToTime(calendar.getTimeInMillis());
        ourReminders = new DatabaseHandler(ShowAlarm.this);
        ourDatabase = ourReminders.openConnection(ShowAlarm.this);
        int requestCode1 = (int) (long) requestCode;
        Intent intent = new Intent(this, AlarmReciever.class);
        intent.putExtra("Message", alarmType);
        intent.putExtra("Id", requestCode);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(this, requestCode1, intent, PendingIntent.FLAG_UPDATE_CURRENT));

        if (sub[0].equals("weekly")) {
            ourReminders.updateSnoozTime(requestCode1, calendar.getTimeInMillis() + "", date, newTime);
        } else {
            ourReminders.updateSnoozTime(requestCode1, calendar.getTimeInMillis() + "", newDate, newTime);
        }


        ourReminders.updateSnoozNo(requestCode1, "0_no");
        ourReminders.closeConnection();
    }

    protected void doneAlarm() {
        ourReminders = new DatabaseHandler(ShowAlarm.this);
        ourDatabase = ourReminders.openConnection(ShowAlarm.this);
        int requestCode1 = (int) (long) id;
        ourReminders.updateSnoozNo(requestCode1, "0_no");
        ourReminders.updateSnooz(id + "", "done");
        ourReminders.closeConnection();
    }

    private Rect hitRectSnooz = new Rect();
    private Rect hitRectUse = new Rect();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (imgCheck) {
            centerX = (int) centerDrag.getX();
            centerY = (int) centerDrag.getY();
            imgCheck = false;
        }
        PointF DownPT = new PointF(); // Record Mouse Position When Pressed Down
        PointF StartPT = new PointF();
        boolean eventConsumed = true;
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (v == centerDrag ) {
                    dragging = true;
                    snooz.setImageResource(R.drawable.reminderalarm);
                    use.setImageResource(R.drawable.reminderalarm1);

                    circ.setVisibility(View.INVISIBLE);
                    centerDrag.setVisibility(View.VISIBLE);
                    snooz.setVisibility(View.VISIBLE);
                    use.setVisibility(View.VISIBLE);
                    snooz.startAnimation(animOut);
                    use.startAnimation(animOut);
                    eventConsumed = false;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (dragging) {
                    snooz.getHitRect(hitRectSnooz);
                    use.getHitRect(hitRectUse);
                    moveImage(event, DownPT, StartPT);
                    if (hitRectSnooz.contains(x, y)) {

                        snooz.setImageResource(R.drawable.reminderalarmhover);

                        centerDrag.setVisibility(View.INVISIBLE);
                        snoozSelected = true;
                        useSelected = false;
                    } else if (hitRectUse.contains(x, y)) {
                        use.setImageResource(R.drawable.reminderalarmhover1);


                        centerDrag.setVisibility(View.INVISIBLE);
                        snoozSelected = false;
                        useSelected = true;
                    } else {
                        centerDrag.setVisibility(View.VISIBLE);
                        snoozSelected = false;
                        useSelected = false;

                        snooz.setImageResource(R.drawable.reminderalarm);
                        use.setImageResource(R.drawable.reminderalarm1);
                    }

                }
                eventConsumed = false;
                break;

            }
            case MotionEvent.ACTION_UP: {
                if (dragging) {
                    snooz.setImageResource(R.drawable.reminderalarm);
                    use.setImageResource(R.drawable.reminderalarm1);

                    centerDrag.setVisibility(View.VISIBLE);

                    moveImage1();
                    circ.setVisibility(View.VISIBLE);
                    snooz.startAnimation(zoomIn2);
                    snooz.setVisibility(View.INVISIBLE);
                    snooz.startAnimation(zoomIn2);
                    use.setVisibility(View.INVISIBLE);
                    use.startAnimation(zoomIn2);
                }
                if (snoozSelected) {
                    snoozeAlarmSelected();
                } else if (useSelected) {
                    useAlarmSelected();
                }
                dragging = false;
                snoozSelected = false;
                useSelected = false;
            }

            break;
        }
        return eventConsumed;
    }

    private void useAlarmSelected() {
        new DoInBackground().execute("mCloseWatchFace");
        if (msg.equals("Wake")) {
            smartTime = false;
            String alarmTypeOfEvent[] = alarm_period.split("_");
            if (!alarmTypeOfEvent[0].equals("once")) {
                long longMili = Long.parseLong(db_milisec);
                Calendar calendar = Calendar.getInstance();
                long cTime = calendar.getTimeInMillis();
                calendar.setTimeInMillis(longMili);
                while (calendar.getTimeInMillis() < cTime) {
                    calendar.add(Calendar.HOUR_OF_DAY, 24);
                }
                setAlarmAgain(id, "Wake", calendar);
            } else {
                doneAlarm();
            }
            endAlarm();
        } else if (msg.equals("textMsg")) {
            smartTime = false;
            doneAlarm();
            sendSms2();
            endAlarm();

        } else if (msg.equals("email")) {
            smartTime = false;
            doneAlarm();
            sendEmail();
            endAlarm();

        } else if (msg.equals("call")) {
            smartTime = false;
            dialNumber();
            doneAlarm();
            endAlarm();


        } else if (msg.equals("Todo")) {

            smartTime = false;
            String alarmTypeOfEvent[] = alarm_period.split("_");
            if (!alarmTypeOfEvent[0].equals("once")) {
                long longMili = Long.parseLong(db_milisec);
                Calendar calendar = Calendar.getInstance();
                long cTime = calendar.getTimeInMillis();
                calendar.setTimeInMillis(longMili);
                while (calendar.getTimeInMillis() < cTime) {
                    calendar.add(Calendar.HOUR_OF_DAY, 24);
                }
                setAlarmAgain(id, "Todo", calendar);
            } else {
                if (subject.equals("Give feedback on Play Store")) {
                    remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
                    SharedPreferences.Editor editor = remindMessagePref.edit();
                    editor.putString("Rating", "done");
                    editor.commit();
                    Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                    }
                }
                doneAlarm();
            }
            endAlarm();
        } else if (msg.equals("Event")) {
            smartTime = false;
            String alarmTypeOfEvent[] = alarm_period.split("_");
            //if(!alarmTypeOfEvent[0].equals("oneDay")){
            long longMili = Long.parseLong(db_milisec);
            Calendar calendar = Calendar.getInstance();
            long cTime = calendar.getTimeInMillis();
            calendar.setTimeInMillis(longMili);
            while (calendar.getTimeInMillis() < cTime) {
                if (alarmTypeOfEvent[0].equals("monthly")) {
                    calendar.add(Calendar.MONTH, 1);

                } else if (alarmTypeOfEvent[0].equals("Yearly")) {
                    calendar.add(Calendar.YEAR, 1);
                } else if (alarmTypeOfEvent[0].equals("weekly")) {

                    calendar.add(Calendar.WEEK_OF_MONTH, 1);
                    //System.out.println("time ffff1= "+SelectTime.milisecToDate(calendar.getTimeInMillis()));
                }
            }
            setAlarmAgain(id, "Event", calendar);
            //}else{
            //	doneAlarm();
            //}

            if (alarmTypeOfEvent[0].equals("Yearly")) {
                if (alarmTypeOfEvent[2].equals("call")) {
                    dialNumber();
                } else if (alarmTypeOfEvent[2].equals("text")) {
                    sendSms2();
                }
            }
            endAlarm();

        }
    }

    private void snoozeAlarmSelected() {

        if (msg.equals("Wake")) {
            smartTime = false;
            snooz1(id, "Wake");
            endAlarm();
        } else if (msg.equals("textMsg")) {
            smartTime = false;
            snooz1(id, "textMsg");
            endAlarm();
        } else if (msg.equals("email")) {
            smartTime = false;
            snooz1(id, "email");
            endAlarm();
        } else if (msg.equals("call")) {
            smartTime = false;
            snooz1(id, "call");
            endAlarm();
        } else if (msg.equals("Todo")) {
            smartTime = false;
            snooz1(id, "Todo");
            endAlarm();
        } else if (msg.equals("Event")) {
            smartTime = false;
            snooz1(id, "Event");
            endAlarm();
        }
    }

    protected void moveImage1() {
        PointF StartPT;
        centerDrag.setX((centerX));
        centerDrag.setY((centerY));
        StartPT = new PointF(centerDrag.getX(), centerDrag.getY());
    }

    protected void moveImage(MotionEvent event, PointF DownPT, PointF StartPT) {
        PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
        centerDrag.setX((int) (StartPT.x + mv.x - (centerDrag.getWidth() / 2)));
        centerDrag.setY((int) (StartPT.y + mv.y - (centerDrag.getWidth() / 2)));
        StartPT = new PointF(centerDrag.getX(), centerDrag.getY());
    }

    void startanimation1() {
        Thread th = new Thread() {
            public void run() {
                ShowAlarm.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        v.setVisibility(View.VISIBLE);
                        v.startAnimation(animationSet1);
                    }
                });
            }
        };
        th.start();
    }

    void startanimation2() {
        Thread th = new Thread() {
            public void run() {
                try {
                    Thread.sleep(650);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ShowAlarm.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        v2.setVisibility(View.VISIBLE);
                        v2.startAnimation(animationSet2);
                    }
                });
            }
        };
        th.start();
    }

    void startanimation3() {
        Thread th = new Thread() {
            public void run() {
                try {
                    Thread.sleep(1300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ShowAlarm.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        v3.setVisibility(View.VISIBLE);
                        v3.startAnimation(animationSet3);
                    }
                });

            }
        };
        th.start();
    }

    void startanimation4() {
        Thread th = new Thread() {
            public void run() {

                try {
                    Thread.sleep(1950);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ShowAlarm.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        v4.setVisibility(View.VISIBLE);
                        v4.startAnimation(animationSet4);
                    }
                });
            }
        };
        th.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "My Tag");
        wl.acquire();

        startanimation1();
        startanimation2();
        startanimation3();
        startanimation4();
        AudioManager audioManager =
                (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        // Set the volume of played media to maximum.
        //mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Integer.parseInt(snoozVolume) / 5, 0);
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mMediaPlayer = MediaPlayer.create(getBaseContext(), alert);
        if (mMediaPlayer != null) {
            //	mMediaPlayer.setVolume(Integer.parseInt(snoozVolume), Integer.parseInt(snoozVolume));

            mMediaPlayer.setLooping(true);
            try {
                mMediaPlayer.prepare();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mMediaPlayer.start();

        }
        vibPattern = new long[]{0L, 100L, 250L, 1000L, 250L, 500L};
        remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
        vib = remindMessagePref.getString("vibration", "");
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vib.equals("yes")) {
            vibrator.vibrate(vibPattern, 2);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        animationSet1.cancel();
        animationSet2.cancel();
        animationSet3.cancel();
        animationSet4.cancel();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }

    }

    public void checkScreenDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        switch (metrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                densityCheck = "low";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                densityCheck = "medium";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                densityCheck = "high";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                densityCheck = "xhigh";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                densityCheck = "xxhigh";
                break;
        }
    }

    private class DoInBackground extends AsyncTask<String, Void, Void>
            implements DialogInterface.OnCancelListener {
        private ProgressDialog dialog;

        protected void onPreExecute() {
        }

        protected Void doInBackground(final String... a) {

            new Thread() {
                public void run() {
                    /*	mGoogleApiClient = new GoogleApiClient.Builder(WakeUp.this)
                    .addApi(Wearable.API)
					.build();
					mGoogleApiClient.connect();

					 */
                    if (a[0].equals("mCloseWatchFace")) {
                        START_ACTIVITY_PATH = "mCloseWatchFace";
                    } else if (a[0].equals("alarm")) {
                        START_ACTIVITY_PATH = "/start/MainActivity";
                    }

                    mGoogleApiClient = new GoogleApiClient.Builder(ShowAlarm.this)
                            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle connectionHint) {
                                    Log.d(" ", "onConnected: " + connectionHint);
                                    // tellWatchConnectedState("connected");
                                    //  "onConnected: null" is normal.
                                    //  There's nothing in our bundle.
                                }

                                @Override
                                public void onConnectionSuspended(int cause) {
                                    Log.d(" ", "onConnectionSuspended: " + cause);
                                }
                            })
                            .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                                @Override
                                public void onConnectionFailed(ConnectionResult result) {
                                    Log.d(" ", "onConnectionFailed: " + result);
                                }
                            })
                            .addApi(Wearable.API)
                            .build();

                    mGoogleApiClient.connect();


                    List<Node> nodeList = getNodes();
                    for (Node node : nodeList) {
                        Log.v(" ", "telling " + node.getId());

                        PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(
                                mGoogleApiClient,
                                node.getId(),
                                START_ACTIVITY_PATH + "_--" + bigSubject.getText().toString() + "_--" + smallMsg.getText().toString() + "_--" + msg + "_--" + themeType,
                                null
                        );

                        result.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                            @Override
                            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                Log.v("   ", "Phone: " + sendMessageResult.getStatus().getStatusMessage());
                            }
                        });
                    }

                }
            }.start();
            return null;
        }

        protected void onPostExecute(Void unused) {
            //populate_listview();
        }

        public void onCancel(DialogInterface dialog) {
            cancel(true);
        }
    }


    private List<Node> getNodes() {
        List<Node> nodes = new ArrayList<Node>();
        NodeApi.GetConnectedNodesResult rawNodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : rawNodes.getNodes()) {
            nodes.add(node);
            String nodeID = node.getId();
        }
        return nodes;

    }




    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        // TODO Auto-generated method stub

        if (messageEvent.getPath().equals("done")) {
            useAlarmSelected();
        } else if (messageEvent.getPath().equals("snooze")) {
            snoozeAlarmSelected();
        }

        //	new DoInBackground().execute("mCloseWatchFace");
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (Log.isLoggable(" ", Log.DEBUG)) {
            Log.d(" ", "Connected to Google Api Service");
        }
        Wearable.MessageApi.addListener(mGoogleApiClient1, this);
    }

    @Override
    public void onDataChanged(DataEventBuffer arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

    }


}
