package com.remindme;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.remindme.RemindMeSession.SharedData;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class Setting extends Activity {
    ProgressBar pBar;
    ScheduledExecutorService scheduler;
    TextView mint1, mint2, mint3, mtm1, mtm2, mtm3, syncContact, syncContactText;
    String themeType;
    Bitmap[] themeImages;
    Parcelable ringtone;
    TextView themeImage;
    MediaPlayer mMediaPlayer;
    int progressChanged;
    String snoozVolume;
    SeekBar sb;
    String snoozTime = "";
    String vibMode = null;
    SharedPreferences remindMessagePref;
    Uri uri = null;
    String densityCheck = "";
    View layout;
    boolean container_check = false;
    protected static final int ACTIVITY_SET_RINGTONE = 0;
    TextView setting_top, gray_textview, close, setting_center1, setting_center2, setting_center3, setting_center4, setting_center5, gray_textview2;
    TextView setting_bottom, edit1, vibrate, vibrateOff, vibrate_back, vibrateOff_back, minute1, minute2, minute3;
    Button yes_button;
    int[] images = {R.drawable.appsetting, R.drawable.menubg, R.drawable.close_reminder, R.drawable.yes_remind, R.drawable.hover_changebg, R.drawable.menu_bg
            , R.drawable.wakeup_center, R.drawable.wakeup_bottom, R.drawable.edit, R.drawable.vibration, R.drawable.vibrationoff, R.drawable.shadow, R.drawable.choosetheme
            , R.drawable.runing_button, R.drawable.check1, R.drawable.check2};
    Bitmap[] denisityBitmap = new Bitmap[images.length];
    TextView toneSelected;
    int sdk = Build.VERSION.SDK_INT;

    ImageView callCatchCheck, smsCatchCheck;
    TextView callCatchArea, smsCatchArea;

    SharedData sharedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        main();
        schedular();
    }

    protected void main() {
        setContentView(R.layout.activity_setting);

        layout = findViewById(R.id.layout1);
        remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
        pBar = (ProgressBar) findViewById(R.id.progressBar1);
        int size = remindMessagePref.getInt("theme_size", 0);
        String imageArray[] = new String[size];
        for (int i = 0; i < size; i++) {
            imageArray[i] = remindMessagePref.getString("stringImage_" + i, "");
        }
        themeImages = new Bitmap[size];
        themeImages = Theme.decodeBase64(imageArray);
        themeType = remindMessagePref.getString("theme_selector", "");

        String vib = remindMessagePref.getString("vibration", "");
        snoozTime = remindMessagePref.getString("snoozPeriod", "");
        snoozVolume = remindMessagePref.getString("snoozVolume", "");
        String uri10 = remindMessagePref.getString("uri10", "");
        /*String sssss = remindMessagePref.getString("syncing", "");
		System.out.println("sdsgdsuyugfuudfge="+sssss);*/
        uri = Uri.parse(uri10);
        checkScreenDensity();
        getTextViewImages();
        getImagesInBitmap();
        sdkCheck();


        selectedSnooz(snoozTime);
        setVolume();
        buttonClicked();
        if (vib.equals("yes")) {
            vibrationClicked(4, 5);
        } else {
            vibrationClicked(5, 4);
        }
        ring();


        /**set Call and Sms catch value in view.*/
        sharedData = new SharedData(getApplicationContext());

        if (sharedData.getGeneralSaveSession(SharedData.CALL_CATCH).equals("yes")) {
            callCatchCheck.setImageBitmap(denisityBitmap[15]);
        } else {
            callCatchCheck.setImageBitmap(denisityBitmap[14]);
        }

        if (sharedData.getGeneralSaveSession(SharedData.SMS_CATCH).equals("yes")) {
            smsCatchCheck.setImageBitmap(denisityBitmap[15]);
        } else {
            smsCatchCheck.setImageBitmap(denisityBitmap[14]);
        }


    }

    protected void schedular() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            scheduler.scheduleWithFixedDelay(new Runnable() {
                public void run() {
                    Setting.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            syncContactMessage();
                        }
                    });

                }
            }, 0, 5, TimeUnit.SECONDS);

        } catch (Exception e) {
            finish();
        }
    }

    protected void syncContactMessage() {

        remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
        String sync = remindMessagePref.getString("syncing", "");
        if (sync.equals("false")) {
            pBar.setVisibility(View.VISIBLE);
            syncContactText.setText("Synchronizing Contacts..");
        } else {
            pBar.setVisibility(View.GONE);
            syncContactText.setText("Synchronize contacts now.");


        }
    }

    protected void selectedSnooz(String snoozTime) {
        if (snoozTime.equals("5")) {
            minuteClicked(4, 5, 5);
        } else if (snoozTime.equals("15")) {
            minuteClicked(5, 4, 5);
        } else if (snoozTime.equals("30")) {
            minuteClicked(5, 5, 4);
        }
    }

    public void ring() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert != null) {
            Ringtone ringName = RingtoneManager.getRingtone(this, alert);
            if (ringName != null) {
                String ringToneName = ringName.getTitle(this);

                String time2[] = ringToneName.split("\\(");
                String time3[];
                if (time2.length != 1) {
                    time3 = time2[1].split("\\)");
                } else {
                    time3 = time2[0].split("\\)");
                }

				/*	String time2[]=ringToneName.split("\\("); previous code remove for Mmax phone and added above
				String time3[]=time2[1].split("\\)");

				 */

                if (time3[0].equals("Unknown ringtone")) {
                    toneSelected.setText("Silent");
                } else {
                    toneSelected.setText(time3[0]);
                }
            } else {
                toneSelected.setText("Silent");
            }
        }
    }

    protected void getTextViewImages() {
        setting_top = (TextView) findViewById(R.id.textView2);
        gray_textview = (TextView) findViewById(R.id.textView3);
        close = (TextView) findViewById(R.id.textView25);
        setting_center1 = (TextView) findViewById(R.id.textView4);
        setting_center2 = (TextView) findViewById(R.id.textView8);
        setting_center3 = (TextView) findViewById(R.id.textView9);
        setting_center5 = (TextView) findViewById(R.id.textView1);
        setting_center4 = (TextView) findViewById(R.id.textView90);
        gray_textview2 = (TextView) findViewById(R.id.textView6);
        setting_bottom = (TextView) findViewById(R.id.textView10);
        edit1 = (TextView) findViewById(R.id.textView23);
        vibrate = (TextView) findViewById(R.id.textView14);
        vibrateOff = (TextView) findViewById(R.id.textView15);
        vibrate_back = (TextView) findViewById(R.id.textView5);
        vibrateOff_back = (TextView) findViewById(R.id.textView7);
        minute1 = (TextView) findViewById(R.id.textView11);
        minute2 = (TextView) findViewById(R.id.textView13);
        minute3 = (TextView) findViewById(R.id.textView12);
        yes_button = (Button) findViewById(R.id.button1);
        toneSelected = (TextView) findViewById(R.id.textView24);
        themeImage = (TextView) findViewById(R.id.textView28);
        sb = (SeekBar) findViewById(R.id.seekBar1);
        mint1 = (TextView) findViewById(R.id.textView19);
        mint2 = (TextView) findViewById(R.id.textView20);
        mint3 = (TextView) findViewById(R.id.textView21);
        mtm1 = (TextView) findViewById(R.id.textView16);
        mtm2 = (TextView) findViewById(R.id.textView17);
        mtm3 = (TextView) findViewById(R.id.textView18);
        syncContact = (TextView) findViewById(R.id.textView30);
        syncContactText = (TextView) findViewById(R.id.textView31);
        callCatchCheck = (ImageView) findViewById(R.id.imageView13);
        smsCatchCheck = (ImageView) findViewById(R.id.imageView14);
        callCatchArea = (TextView) findViewById(R.id.callCatch);
        smsCatchArea = (TextView) findViewById(R.id.smsCatch);
    }

    protected void setVolume() {

        //		sb.setProgress(Color.rgb(119, 73, 144));
        sb.setThumb(new BitmapDrawable(getResources(), denisityBitmap[13]));

        if (themeType.equals("green")) {
            sb.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_green));
        } else if (themeType.equals("yellow")) {
            sb.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_yellow));
        } else if (themeType.equals("white")) {
            sb.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_white));
        } else if (themeType.equals("blue")) {
            sb.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_blue));
        } else if (themeType.equals("red")) {
            sb.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_red));
        } else if (themeType.equals("purple")) {
            sb.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar));
        }
        progressChanged = Integer.parseInt(snoozVolume);
        sb.setProgress(Integer.parseInt(snoozVolume));
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                volumeCheck(progressChanged);
                //    Toast.makeText(Setting.this,"seek bar progress:"+progressChanged,Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void volumeCheck(int vol) {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        AudioManager audioManager =
                (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol / 5, 0);
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mMediaPlayer = MediaPlayer.create(getBaseContext(), alert);
        if (mMediaPlayer != null) {

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
    }

    protected void getImagesInBitmap() {
        for (int i = 0; i < images.length; i++) {
            denisityBitmap[i] = ((BitmapDrawable) getResources().getDrawable(images[i])).getBitmap();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void sdkCheck() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        denisityBitmap = ScreenDenesityCheck.checkScreenDensityAndResize(denisityBitmap, metrics, densityCheck, MainReminder.checkScreenSize(getApplicationContext()));
        if (!themeType.equals("purple")) {
            denisityBitmap[2] = themeImages[1];
            denisityBitmap[4] = themeImages[3];
        }
        if (themeType.equals("white")) {
            mtm1.setTextColor(Color.BLACK);
            mtm2.setTextColor(Color.BLACK);
            mtm3.setTextColor(Color.BLACK);
            mint1.setTextColor(Color.BLACK);
            mint2.setTextColor(Color.BLACK);
            mint3.setTextColor(Color.BLACK);

        } else {
            mtm1.setTextColor(Color.WHITE);
            mtm2.setTextColor(Color.WHITE);
            mtm3.setTextColor(Color.WHITE);
            mint1.setTextColor(Color.WHITE);
            mint2.setTextColor(Color.WHITE);
            mint3.setTextColor(Color.WHITE);

        }
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            setting_top.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[0]));
            gray_textview.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[1]));
            close.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[2]));
            setting_center1.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[6]));
            setting_center2.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[6]));
            setting_center3.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[6]));
            setting_center4.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[6]));
            callCatchArea.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[6]));
            smsCatchArea.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[6]));
            syncContact.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[6]));
            setting_center5.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[6]));
            gray_textview2.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[1]));
            setting_bottom.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[7]));
            edit1.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[8]));
            vibrate.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[9]));
            vibrateOff.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[10]));
            vibrate_back.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[4]));
            vibrateOff_back.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[5]));
            minute1.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[4]));
            minute2.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[5]));
            minute3.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[5]));
            yes_button.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[3]));
            themeImage.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[12]));
            callCatchCheck.setImageBitmap(denisityBitmap[14]);
            smsCatchCheck.setImageBitmap(denisityBitmap[14]);

        } else {

            setting_top.setBackground(new BitmapDrawable(getResources(), denisityBitmap[0]));
            gray_textview.setBackground(new BitmapDrawable(getResources(), denisityBitmap[1]));
            close.setBackground(new BitmapDrawable(getResources(), denisityBitmap[2]));
            setting_center1.setBackground(new BitmapDrawable(getResources(), denisityBitmap[6]));
            setting_center2.setBackground(new BitmapDrawable(getResources(), denisityBitmap[6]));
            setting_center3.setBackground(new BitmapDrawable(getResources(), denisityBitmap[6]));
            setting_center4.setBackground(new BitmapDrawable(getResources(), denisityBitmap[6]));
            callCatchArea.setBackground(new BitmapDrawable(getResources(), denisityBitmap[6]));
            smsCatchArea.setBackground(new BitmapDrawable(getResources(), denisityBitmap[6]));
            syncContact.setBackground(new BitmapDrawable(getResources(), denisityBitmap[6]));
            setting_center5.setBackground(new BitmapDrawable(getResources(), denisityBitmap[6]));
            gray_textview2.setBackground(new BitmapDrawable(getResources(), denisityBitmap[1]));
            setting_bottom.setBackground(new BitmapDrawable(getResources(), denisityBitmap[7]));
            edit1.setBackground(new BitmapDrawable(getResources(), denisityBitmap[8]));
            vibrate.setBackground(new BitmapDrawable(getResources(), denisityBitmap[9]));
            vibrateOff.setBackground(new BitmapDrawable(getResources(), denisityBitmap[10]));
            vibrate_back.setBackground(new BitmapDrawable(getResources(), denisityBitmap[4]));
            vibrateOff_back.setBackground(new BitmapDrawable(getResources(), denisityBitmap[5]));
            minute1.setBackground(new BitmapDrawable(getResources(), denisityBitmap[4]));
            minute2.setBackground(new BitmapDrawable(getResources(), denisityBitmap[5]));
            minute3.setBackground(new BitmapDrawable(getResources(), denisityBitmap[5]));
            yes_button.setBackground(new BitmapDrawable(getResources(), denisityBitmap[3]));
            themeImage.setBackground(new BitmapDrawable(getResources(), denisityBitmap[12]));
            callCatchCheck.setImageBitmap(denisityBitmap[14]);
            smsCatchCheck.setImageBitmap(denisityBitmap[14]);
        }
    }

    protected void buttonClicked() {
        yes_button.setOnClickListener(new OnClickListener() {
            public void onClick(View viewParam) {
                SplashScreen.settingDone = true;
                if (mMediaPlayer != null) {
                    mMediaPlayer.release();
                }
                if (vibMode != null) {
                    //for vibration
                    SharedPreferences.Editor editor = remindMessagePref.edit();
                    editor.putString("vibration", vibMode);
                    editor.commit();
                }
                //for snooz time
                setSnoozTimeAndVolume(snoozTime, progressChanged + "");
                finish();
                overridePendingTransition(R.anim.push_down_out, R.anim.push_down_out);
            }
        });
        setting_center2.setOnClickListener(new OnClickListener() {
            public void onClick(View viewParam) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.release();
                }
                Intent i = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                i.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
                i.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, uri);
                startActivityForResult(i, ACTIVITY_SET_RINGTONE);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        setting_center5.setOnClickListener(new OnClickListener() {
            public void onClick(View viewParam) {


                Intent in = new Intent(Setting.this, Theme.class);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

            }
        });
        close.setOnClickListener(new OnClickListener() {
            public void onClick(View viewParam) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.release();
                }
                //	message.setText("Jogging");
                finish();
                overridePendingTransition(R.anim.push_down_out, R.anim.push_down_out);
            }
        });


        minute1.setOnClickListener(new OnClickListener() {
            public void onClick(View viewParam) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.release();
                }
                minuteClicked(4, 5, 5);
                snoozTime = "5";
            }
        });
        minute2.setOnClickListener(new OnClickListener() {
            public void onClick(View viewParam) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.release();
                }
                minuteClicked(5, 4, 5);
                snoozTime = "15";

            }
        });
        minute3.setOnClickListener(new OnClickListener() {
            public void onClick(View viewParam) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.release();
                }
                minuteClicked(5, 5, 4);
                snoozTime = "30";


            }
        });
        vibrate_back.setOnClickListener(new OnClickListener() {
            public void onClick(View viewParam) {
                vibrationClicked(4, 5);
                if (mMediaPlayer != null) {
                    mMediaPlayer.release();
                }
                vibMode = "yes";
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(200);

            }
        });
        vibrateOff_back.setOnClickListener(new OnClickListener() {
            public void onClick(View viewParam) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.release();
                }
                vibrationClicked(5, 4);
                vibMode = "no";

            }
        });
        syncContact.setOnClickListener(new OnClickListener() {
            public void onClick(View viewParam) {

                //System.out.println("sedfuwgfogwogwedfugweduiogqweduigqwedugqwedgqwedyug11");
                //	stopService(new Intent(Setting.this, ServiceContactFetch.class));

                //	syncContactText.setText("Synchronizing Contacts..");
                pBar.setVisibility(View.VISIBLE);
                syncContactText.setText("Synchronizing Contacts..");
                GeneralClass.defaultAlarm(getApplicationContext());
                //schedular();
                //System.out.println("sedfuwgfogwogwedfugweduiogqweduigqwedugqwedgqwedyug12");
                //	startService(new Intent(Setting.this, ServiceContactFetch.class));


            }
        });
    }

    protected void setSnoozTimeAndVolume(String snoozTime, String snoozVolume) {
        SharedPreferences.Editor editor = remindMessagePref.edit();
        editor.putString("snoozPeriod", snoozTime);
        editor.putString("snoozVolume", snoozVolume);
        editor.commit();
    }

    protected void minuteClicked(int everyday1, int weekday1, int once1) {
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            minute1.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[everyday1]));
            minute2.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[weekday1]));
            minute3.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[once1]));
        } else {
            minute1.setBackground(new BitmapDrawable(getResources(), denisityBitmap[everyday1]));
            minute2.setBackground(new BitmapDrawable(getResources(), denisityBitmap[weekday1]));
            minute3.setBackground(new BitmapDrawable(getResources(), denisityBitmap[once1]));
        }


    }

    protected void vibrationClicked(int vibration, int vibrationOff) {
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            vibrate_back.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[vibration]));
            vibrateOff_back.setBackgroundDrawable(new BitmapDrawable(getResources(), denisityBitmap[vibrationOff]));
        } else {
            vibrate_back.setBackground(new BitmapDrawable(getResources(), denisityBitmap[vibration]));
            vibrateOff_back.setBackground(new BitmapDrawable(getResources(), denisityBitmap[vibrationOff]));
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {

            case RESULT_OK:
                uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (uri != null) {
                    Ringtone ringName = RingtoneManager.getRingtone(this, uri);
                    String ringToneName = ringName.getTitle(this);
                    toneSelected.setText(ringToneName);
                } else {
                    toneSelected.setText("Silent");
                }
                //for rintone
                RingtoneManager.setActualDefaultRingtoneUri(Setting.this, RingtoneManager.TYPE_ALARM, uri);
                SharedPreferences.Editor editor = remindMessagePref.edit();
                editor.putString("uri10", uri + "");
                editor.commit();


                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();

        TranslateAnimation anim = new TranslateAnimation(0, 500, 0, 0);
        anim.setDuration(150);
        anim.setFillAfter(false);
        container_check = true;
        layout.setVisibility(View.GONE);
        layout.setAnimation(anim);
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
		/*==========================google Analitics Starts=============================*/

        AnaliticsForActivity analit = new AnaliticsForActivity();
        analit.gAnalitics(getApplicationContext(), "Setting Page");

		/*==========================google Analitics Ends=============================*/
        main();


        if (container_check) {
            container_check = false;
            TranslateAnimation anim1 = new TranslateAnimation(500, 0, 0, 0);
            anim1.setDuration(150);
            anim1.setFillAfter(false);
            layout.setVisibility(View.VISIBLE);
            layout.setAnimation(anim1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scheduler.shutdownNow();
        container_check = false;
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
            }
            finish();
            overridePendingTransition(R.anim.push_down_out, R.anim.push_down_out);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void checkCall(View view) {

        if (sharedData.getGeneralSaveSession(SharedData.CALL_CATCH).equals("yes")) {
            sharedData.setGeneralSaveSession(SharedData.CALL_CATCH, "no");
            callCatchCheck.setImageBitmap(denisityBitmap[14]);

        } else {
            sharedData.setGeneralSaveSession(SharedData.CALL_CATCH, "yes");
            callCatchCheck.setImageBitmap(denisityBitmap[15]);
        }


    }

    public void checkSms(View view) {

        if (sharedData.getGeneralSaveSession(SharedData.SMS_CATCH).equals("yes")) {
            sharedData.setGeneralSaveSession(SharedData.SMS_CATCH, "no");
            smsCatchCheck.setImageBitmap(denisityBitmap[14]);

        } else {
            sharedData.setGeneralSaveSession(SharedData.SMS_CATCH, "yes");
            smsCatchCheck.setImageBitmap(denisityBitmap[15]);
        }


    }
}
