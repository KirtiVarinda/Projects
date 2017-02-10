package com.remindme;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.view.MenuItem;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.remindme.RemindMeSession.SharedData;
import com.remindme.manage.ActivitySwitcher;
import com.remindme.manage.PrivacyPolicy;

@SuppressLint("NewApi")
public class MainReminder extends FragmentActivity implements AnimationListener {

    /**
     * Activity switcher variable.
     */
    private ActivitySwitcher activitySwitcher;


    public boolean policypopupCheck = true;
    SharedData shareData;
    boolean detailContainer = true;
    String rating;
    String ratingTime;
    static boolean wwake = true;
    static boolean ttext = true;
    static boolean eemail = true;
    static boolean ccall = true;
    static boolean eevent = true;
    static boolean ttodo = true;
    static boolean aall = true;
    static int dragCategory = 0;
    int tuID;
    int launcherId;
    static boolean tu1 = false;
    static boolean tu2 = false;
    ImageView bkankList;
    static SharedPreferences prefs1;
    String themeType;
    int ij = 0;
    static MainReminder currentActivity;
    static Context context;
    static int width;
    static boolean imageCatScroll = false;
    static String alarmType1 = "all";
    static ImageView alarmTwake, alarmTmsg, alarmTemail, alarmTcall, alarmTeven, alarmTtodo, alarmTall;
    int position;


    /**
     * array to retain all the images used in the top drag down categories menus icons.
     */
    private static int[] topCatIcons = {R.drawable.hide1, R.drawable.hide2, R.drawable.hide3, R.drawable.hide4, R.drawable.hide5,
            R.drawable.hide6, R.drawable.hide7, R.drawable.hidec1, R.drawable.hidec2, R.drawable.hidec3,
            R.drawable.hidec4, R.drawable.hidec5, R.drawable.hidec6, R.drawable.hidec7};

    int i = 0;
    boolean animWithCenter = false;
    boolean doneCheck = false;
    static Animation slideUp, slideDown;
    private SwipeListView swipeListView;
    ArrayList<String> id;
    ArrayList<String> names;
    ArrayList<String> recipient;
    ArrayList<String> date1;
    ArrayList<String> time1;
    ArrayList<String> listSubject;
    ArrayList<String> listDetail;
    ArrayList<String> listSubject1;
    ArrayList<String> listDetail1;
    ArrayList<String> alarm_period;
    ArrayList<String> alarmType;
    ArrayList<String> snoozDone;
    ArrayList<String> miliSec;
    static String largeScrenn = "";
    int h;
    int rcolor[];
    int gcolor[];
    int bcolor[];
    int r;
    int g;
    int b;
    String color1[];
    public LayoutInflater mInflater;
    List<String> idToPassInList;
    private DatabaseHandler ourReminders;
    private SQLiteDatabase ourDatabase;
    public List<String[]> AllReminders = null;
    SharedPreferences remindMessagePref;
    static TextView container;
    boolean container_check = false;
    ImageView imageAfterTouch, textAddEvent, textToDo;
    ImageView imageBeforeTouch;
    TextView transparent_image;
    TextView transparent_strip;
    ImageView tutorialText;
    static ImageView tutorialTopText;
    static TextView cat_transparent_strip;
    AlphaAnimation fadeOut;
    AlphaAnimation fadeIn;
    String densityCheck = "";
    ScaleAnimation zoomOut;
    ScaleAnimation zoomIn;
    AnimationSet animOut, animIn;
    boolean remind1, remind2, remind3, remind4, remind5;
    static int sdk = android.os.Build.VERSION.SDK_INT;

    SelectTime st = new SelectTime();
    int current_hour, current_min, current_year, current_month, current_day;
    ArrayAdapter<String> mAdapter;
    static LinearLayout hideTextView;
    //static TextView shadow;


    /**
     * for advertisement*/

    /**
     * private String getErrorReason(int errorCode) {
     * String errorReason = "";
     * switch (errorCode) {
     * case AdRequest.ERROR_CODE_INTERNAL_ERROR:
     * errorReason = "Internal error";
     * <p/>
     * break;
     * case AdRequest.ERROR_CODE_INVALID_REQUEST:
     * errorReason = "Invalid request";
     * break;
     * case AdRequest.ERROR_CODE_NETWORK_ERROR:
     * errorReason = "Network Error";
     * break;
     * case AdRequest.ERROR_CODE_NO_FILL:
     * errorReason = "No fill";
     * break;
     * }
     * return errorReason;
     * }
     */
    float[] touchPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * making window full screen.
         */
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;


        setContentView(R.layout.activity_main_reminder);
        context = getApplicationContext();

        /**
         * instantiate shared data.
         */
        shareData = new SharedData(context);

       if( !shareData.getGeneralSaveSession(SharedData.DEFAULT_CALL_SMS_CHECK).equals("done")){
           shareData.setGeneralSaveSession(SharedData.SMS_CATCH,"yes");
           shareData.setGeneralSaveSession(SharedData.CALL_CATCH,"yes");
           shareData.setGeneralSaveSession(SharedData.DEFAULT_CALL_SMS_CHECK,"done");
       }

        /**
         * current activity instance.
         */
        currentActivity = MainReminder.this;


        /**
         * initialize the activity switcher.
         */
        activitySwitcher = new ActivitySwitcher(currentActivity);


        /**
         * initialize animations.
         */
        slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        slideDown.setAnimationListener(this);
        slideUp.setAnimationListener(this);


        /*==============================AdMob module============================*/
        /** for advertisement
         adView = (AdView)this.findViewById(R.id.adView);
         //	Bundle bundle = new Bundle();
         ////	bundle.putString("color_bg", "AAAAFF");
         //	bundle.putString("color_text", "808080");
         ///	AdMobExtras extras = new AdMobExtras(bundle);
         AdRequest adRequest = new AdRequest.Builder()
         .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
         .addTestDevice("812FF4312AEA25C679B9582FCDAB3C34")

         //.addNetworkExtras(extras)
         .build();
         adView.loadAd(adRequest);
         adView.setAdListener(new AdListener() {
         public void onAdLoaded() {
         adView.setVisibility(View.VISIBLE);
         adView.startAnimation(slideDown);

         }
         public void onAdFailedToLoad(int errorcode) {
         adView.setVisibility(View.GONE);
         }
         // Only implement methods you need.

         });
         */
        /*==============================AdMob module============================*/

        remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);

        hideTextView = (LinearLayout) findViewById(R.id.textView11);
        //  shadow = (TextView) findViewById(R.id.textView19);


        /**
         * get time
         */
        current_hour = st.mHour;
        current_min = st.mMinute;
        current_year = st.mYear;
        current_month = st.mMonth;
        current_day = st.mDay;


        checkScreenDensity();



        setInitialMessage();

        swipeListView = (SwipeListView) findViewById(R.id.example_lv_list);
        remind1 = remind2 = remind3 = remind4 = remind5 = false;
        // checkScreenSize(context);
        imageAnimation();
        new DoInBackground().execute("all");
        container = (TextView) findViewById(R.id.textView9);
        textAddEvent = (ImageView) findViewById(R.id.textView3);
        textToDo = (ImageView) findViewById(R.id.textView4);
        // addEventText = (TextView) findViewById(R.id.textView5);
        //  toDoText = (TextView) findViewById(R.id.textView6);
        imageAfterTouch = (ImageView) findViewById(R.id.textView7);
        imageBeforeTouch = (ImageView) findViewById(R.id.textView2);
        transparent_image = (TextView) findViewById(R.id.textView10);
        transparent_strip = (TextView) findViewById(R.id.textView8);
        cat_transparent_strip = (TextView) findViewById(R.id.textView20);
        alarmTall = (ImageView) findViewById(R.id.textView12);
        alarmTmsg = (ImageView) findViewById(R.id.textView13);
        alarmTemail = (ImageView) findViewById(R.id.textView14);
        alarmTcall = (ImageView) findViewById(R.id.textView15);
        alarmTeven = (ImageView) findViewById(R.id.textView16);
        alarmTtodo = (ImageView) findViewById(R.id.textView17);
        alarmTwake = (ImageView) findViewById(R.id.textView18);
        bkankList = (ImageView) findViewById(R.id.textView23);

        tutorialText = (ImageView) findViewById(R.id.textView21);
        tutorialTopText = (ImageView) findViewById(R.id.textView22);
        prefs1 = PreferenceManager.getDefaultSharedPreferences(this);


        /**
         * function to make drop categories icon equal width.
         */


        /**
         * run method to handle the round animation of remind me app.
         */
        roundImageAnimation();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            swipeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            swipeListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                      long id, boolean checked) {
                    // mode.setTitle("Selected (" + swipeListView.getCountSelected() + ")");
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    // int id = item.getItemId();
                 /*   if (id == R.id.menu_delete) {
                        swipeListView.dismissSelected();
                        return true;
                    }*/
                    return false;
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.menu_choice_items, menu);
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    swipeListView.unselectedChoiceStates();
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }
            });
        }
        swipeListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                detailContainer = false;
                String remindId = mAdapter.getItem(position);
                Intent in = new Intent(MainReminder.this, DetailListItem.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                in.putExtra("theme_for_detail", themeType);
                in.putExtra("id_for_detail", remindId);
                startActivity(in);
                //overridePendingTransition(R.anim.push_down_in, R.anim.push_down_in);
                return false;
            }
        });
        swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                swipeListView.closeOpenedItems();

            }

            @Override
            public void onDone(final int position) {
                doneMethod(position);

            }

            @Override
            public void onStartClose(int position, boolean right) {
            }

            @Override
            public void onClickFrontView(int position) {

                swipeListView.closeOpenedItems();
            }

            @Override
            public void onClickBackView(int position) {
                swipeListView.closeOpenedItems();
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {

                for (int position : reverseSortedPositions) {
                    mAdapter.remove(mAdapter.getItem(position));
                }

            }
        });
        reload();
    }


    private void doneMethod(final int position) {
        String remindId = mAdapter.getItem(position);
        ourReminders = new DatabaseHandler(MainReminder.this);
        ourDatabase = ourReminders.openConnection(MainReminder.this);
        int rId = Integer.parseInt(remindId);
        if (snoozDone.get(position).equals("done")) {
            setAlarm(rId, miliSec.get(position), alarmType.get(position), alarm_period.get(position));
            if (!doneCheck) {
                snoozDone.set(position, "run");
                ourReminders.updateSnooz(remindId, "run");
                mAdapter.notifyDataSetChanged();

                Thread th = new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        MainReminder.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new DoInBackground().execute(alarmType1);
                            }
                        });

                    }
                };
                th.start();
            } else {
                AlertDialog.Builder builder = new Builder(MainReminder.this);
                builder.setCancelable(false);
                builder.setTitle("Sorry!");
                builder.setMessage("Reminder time passed.");
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id1) {
                        Intent in = null;
                        if (alarmType.get(position).equals("wakeup")) {
                            in = new Intent(MainReminder.this, WakeUp.class);
                        } else if (alarmType.get(position).equals("textmessage")) {
                            in = new Intent(MainReminder.this, TextMessage.class);
                        } else if (alarmType.get(position).equals("email")) {
                            in = new Intent(MainReminder.this, EmailReminder.class);
                        } else if (alarmType.get(position).equals("call")) {
                            in = new Intent(MainReminder.this, CallReminder.class);
                        } else if (alarmType.get(position).equals("todo")) {
                            in = new Intent(MainReminder.this, ReminderToDo.class);
                        } else if (alarmType.get(position).equals("event")) {
                            in = new Intent(MainReminder.this, AddEvent.class);
                        }
                        in.putExtra("edit_id", id.get(position));
                        in.putExtra("edit_names", names.get(position));
                        in.putExtra("edit_recipient", recipient.get(position));
                        in.putExtra("edit_listSubject", listSubject.get(position));
                        in.putExtra("edit_date", date1.get(position));
                        in.putExtra("edit_time", time1.get(position));
                        in.putExtra("edit_alarm_period", alarm_period.get(position));
                        in.putExtra("edit_alarmType", alarmType.get(position));
                        in.putExtra("edit_snoozDone", snoozDone.get(position));
                        in.putExtra("edit_miliSec", miliSec.get(position));
                        startActivity(in);
                        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_in);


                    }
                });
                builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
            doneCheck = false;
        } else if (snoozDone.get(position).equals("run")) {
            snoozDone.set(position, "done");
            ourReminders.updateSnoozNo(rId, "0_no");
            ourReminders.updateSnooz(remindId, "done");
            //	String str[]=listDetail.get(position).split("~");
            //		listDetail.set(position,str[0]);
            cancleAlarms(rId);
            mAdapter.notifyDataSetChanged();
            Thread th = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MainReminder.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new DoInBackground().execute(alarmType1);
                        }
                    });
                }
            };
            th.start();
        } else if (snoozDone.get(position).equals("runnoAlarm")) {
            snoozDone.set(position, "donenoAlarm");
            ourReminders.updateSnooz(remindId, "donenoAlarm");
            mAdapter.notifyDataSetChanged();
            Thread th = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MainReminder.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new DoInBackground().execute(alarmType1);
                        }
                    });
                }
            };
            th.start();
        } else if (snoozDone.get(position).equals("donenoAlarm")) {
            snoozDone.set(position, "runnoAlarm");
            ourReminders.updateSnooz(remindId, "runnoAlarm");
            mAdapter.notifyDataSetChanged();
            Thread th = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MainReminder.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new DoInBackground().execute(alarmType1);
                        }
                    });
                }
            };
            th.start();
        }
        //	ourReminders.closeConnection();
        //			/	mAdapter.notifyDataSetChanged();
        swipeListView.closeOpenedItems();
    }




    private void reload() {
        SettingsManager settings = SettingsManager.getInstance();
        swipeListView.setSwipeMode(settings.getSwipeMode());
        swipeListView.setSwipeActionLeft(settings.getSwipeActionLeft());
        swipeListView.setSwipeActionRight(settings.getSwipeActionRight());
        swipeListView.setOffsetLeft(convertDpToPixel(settings.getSwipeOffsetLeft()));
        swipeListView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
        swipeListView.setAnimationTime(settings.getSwipeAnimationTime());
        swipeListView.setSwipeOpenOnLongPress(settings.isSwipeOpenOnLongPress());
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }


    protected void setInitialMessage() {


        SharedPreferences.Editor editor = remindMessagePref.edit();
        //editor.putString("key", TextUtils.join(",", emails));
        editor.putString("reminder_message", "no");
        editor.putString("reminder_time", "no");
        editor.putString("reminder_date", "no");
        editor.putString("reminder_contact_name", "no");
        editor.putString("reminder_contact_number", "no");
        editor.putString("combine_contact", "no");
        editor.putString("reminder_contact", "no");
        editor.putString("exact_reminder_time", "no");
        shareData.setGeneralSaveSession(SharedData.SELECTEDWEEKDAY, "no");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {


            SharedPreferences.Editor editor1 = prefs.edit();
            editor1.putBoolean("firstTime", true);
            editor1.commit();
            ourReminders = new DatabaseHandler(MainReminder.this);
            ourDatabase = ourReminders.openConnection(MainReminder.this);


            try {
                Long currentMilisec = SelectTime.dateToString(current_year, current_month, current_day, current_hour, current_min);
                ourReminders.addReminders("", "", "Add Call,Email or Text Reminder ", current_day + " " + st.getMonthInWords(current_month) + " " + current_year, SelectTime.CurrenttwelevFormatTime(current_hour, current_min), "runnoAlarm", "todo", "runnoAlarm", currentMilisec + "");
                //ourReminders.addReminders("","","Add a Calender Event ",current_day+" "+st.getMonthInWords(current_month)+" "+current_year,SelectTime.CurrenttwelevFormatTime(current_hour,current_min),"runnoAlarm","todo","runnoAlarm",currentMilisec+"");
            } catch (Exception e) {
                System.out.println("fail to save in database");
            }


            GregorianCalendar calendar = new GregorianCalendar(current_year, current_month, current_day, 07, 30);
            SelectTime st = new SelectTime();
            Long currentMilisec = SelectTime.dateToString(st.mYear, st.mMonth, st.mDay, st.mHour, st.mMinute);
            while (calendar.getTimeInMillis() < currentMilisec) {
                calendar.add(Calendar.HOUR_OF_DAY, 24);
            }
            String mtd = SelectTime.milisecToDate(calendar.getTimeInMillis());
            String mtt = SelectTime.milisecToTime(calendar.getTimeInMillis());
            String date22[] = mtd.split(" ");
            long lastId = ourReminders.addReminders("", "", "Jogging", date22[0] + " " + date22[1] + " " + date22[2], mtt, "everyday", "wakeup", "run", calendar.getTimeInMillis() + "");
            defaultAlarm(lastId, calendar, "Wake");

            GregorianCalendar calendar1 = new GregorianCalendar(current_year, current_month, current_day, 18, 30);
            calendar1.add(Calendar.HOUR_OF_DAY, 48);

            String mtd1 = SelectTime.milisecToDate(calendar1.getTimeInMillis());
            String mtt1 = SelectTime.milisecToTime(calendar1.getTimeInMillis());
            String date23[] = mtd1.split(" ");
            long lastId2 = ourReminders.addReminders("", "", "Give feedback on Play Store", date23[0] + " " + date23[1] + " " + date23[2], mtt1, "once", "todo", "run", calendar1.getTimeInMillis() + "");
            defaultAlarm(lastId2, calendar1, "Todo");

			
		/*	Intent intentAlarm = new Intent(this, AlarmReciever.class);
            intentAlarm.putExtra("Message", "Wake");
			intentAlarm.putExtra("Id", lastId);
			int requestCode1=(int)lastId;
			alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), PendingIntent.getBroadcast(this,requestCode1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
			//} */
            ourReminders.closeConnection();
        }
        editor.commit();
    }

    protected void defaultAlarm(long lastId, Calendar calendar, String type) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(this, AlarmReciever.class);
        intentAlarm.putExtra("Message", type);
        intentAlarm.putExtra("Id", lastId);
        int requestCode1 = (int) lastId;
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(this, requestCode1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

    }

    protected void List1(String type) {
        ourReminders = new DatabaseHandler(MainReminder.this);
        ourDatabase = ourReminders.openConnection(MainReminder.this);
        remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
        String theme_selector = remindMessagePref.getString("theme_selector", "");
        themeType = theme_selector;
        mAdapter = null;
        if (theme_selector.equals("purple")) {
            color1 = new String[]{"#ad0dff", "#a600fb", "#9b00ea", "#9000d9", "#8500c8", "#7900b7", "#6e00a6", "#630095", "#580084", "#4c0073", "#410062", "#360051", "#2a0040"};
            //color1 = new String[] {"#b92fff","#b31eff","#2a0040","#360051","#410062","#4c0073","#580084","#630095","#6e00a6","#7900b7","#8500c8","#9000d9",};
        } else if (theme_selector.equals("red")) {
            color1 = new String[]{"#e45b5a", "#e14c4c", "#df3e3d", "#dd2f2e", "#d72423", "#c82221", "#b9201f", "#ab1d1c", "#9c1b1a", "#8e1817", "#7f1615", "#711312", "#621110", "#540e0e", "#450c0b",};
        } else if (theme_selector.equals("yellow")) {
            color1 = new String[]{"#ffc568", "#ffbf57", "#ffb846", "#ffb135", "#ffab24", "#ffa413", "#ff9e02", "#f09400", "#df8900", "#ce7f00", "#bd7500", "#ac6a00", "#9b6000", "#8a5500", "#794b00"};
        } else if (theme_selector.equals("green")) {
            color1 = new String[]{"#32d9a7", "#27d3a0", "#24c595", "#21b78a", "#1fa87f", "#1c9a74", "#1a8c6a", "#177e5f", "#156f54", "#126149", "#0f533e", "#0d4433", "#0a3629"};
        } else if (theme_selector.equals("white")) {
            color1 = new String[]{"#ffffff", "#f7f7f7", "#efefef", "#e6e6e6", "#dedede", "#d5d6d5", "#cdcdcd", "#c4c5c4", "#bcbcbc", "#b3b4b3", "#ababab", "#a2a2a2", "#9a9a9a", "#919191", "#898989"};
        } else if (theme_selector.equals("blue")) {
            color1 = new String[]{"#50c4ff", "#3fbeff", "#2eb8ff", "#1db2ff", "#0cacff", "#00a5fa", "#009ae9", "#008fd8", "#0083c7", "#0078b6", "#006da5", "#006294", "#005683", "#004b72", "#004061"};
        } else {
            color1 = new String[]{"#2a0040", "#360051", "#410062", "#4c0073", "#580084", "#630095", "#6e00a6", "#7900b7", "#8500c8", "#9000d9", "#9b00ea", "#a600fb", "#ad0dff", "#b31eff", "#b92fff"};
        }
        getRemindersFromDatabase(type);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        idToPassInList = new ArrayList<String>(AllReminders.size());
        listSubject = new ArrayList<String>();
        listDetail = new ArrayList<String>();
        listSubject1 = new ArrayList<String>();
        listDetail1 = new ArrayList<String>();
        alarmType = new ArrayList<String>();
        snoozDone = new ArrayList<String>();
        miliSec = new ArrayList<String>();
        alarm_period = new ArrayList<String>();
        id = new ArrayList<String>();
        names = new ArrayList<String>();
        recipient = new ArrayList<String>();
        date1 = new ArrayList<String>();
        time1 = new ArrayList<String>();
        h = 0;
        String sub[];

        if (AllReminders.size() == 0 && type.equals("all")) {
            bkankList.setVisibility(View.VISIBLE);
        } else {
            bkankList.setVisibility(View.INVISIBLE);
        }

        if (AllReminders.size() != 0 || type.equals("all")) {
            for (final String[] reminderValues : AllReminders) {
                if (launcherId < Integer.parseInt(reminderValues[0])) {
                    launcherId = Integer.parseInt(reminderValues[0]);
                }

                idToPassInList.add(reminderValues[0]);
                listSubject.add(reminderValues[3]);
                alarm_period.add(reminderValues[6]);
                alarmType.add(reminderValues[7]);
                snoozDone.add(reminderValues[8]);
                miliSec.add(reminderValues[9]);
                id.add(reminderValues[0]);
                names.add(reminderValues[1]);
                recipient.add(reminderValues[2]);
                date1.add(reminderValues[4]);
                time1.add(reminderValues[5]);
                sub = reminderValues[6].split("_");
                String removeComman = "Unknown person";
                String[] numberOfContact = reminderValues[1].split("\\),");
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
                    listSubject1.add("Wake up for " + reminderValues[3]);
                } else if (reminderValues[7].equals("textmessage")) {
                    listSubject1.add("Text " + removeComman + " for " + reminderValues[3]);
                } else if (reminderValues[7].equals("email")) {
                    listSubject1.add("Email " + removeComman + " for " + reminderValues[3]);
                } else if (reminderValues[7].equals("call")) {
                    if (reminderValues[3].equals("Subject")) {
                        listSubject1.add("Call " + removeComman);
                    } else {
                        listSubject1.add("Call " + removeComman + " for " + reminderValues[3]);
                    }

                } else if (reminderValues[7].equals("todo")) {
                    listSubject1.add(reminderValues[3]);
                } else if (reminderValues[7].equals("event")) {
                    if (sub[0].equals("monthly")) {
                        listSubject1.add(reminderValues[3]);
                    } else if (sub[0].equals("Yearly")) {
                        if (sub[1].equals("birthday")) {
                            listSubject1.add(sub[2].substring(0, 1).toUpperCase() + sub[2].substring(1) + " " + removeComman + " for Birthday");
                        } else if (sub[1].equals("anniversery")) {
                            listSubject1.add(sub[2].substring(0, 1).toUpperCase() + sub[2].substring(1) + " " + removeComman + " for Anniversery");
                        } else {
                            listSubject1.add(sub[2].substring(0, 1).toUpperCase() + sub[2].substring(1) + " " + removeComman + " for " + sub[1]);
                        }
                    } else if (sub[0].equals("weekly")) {
                        listSubject1.add(reminderValues[3]);
                    }
                }
                String snoozeNos = "";
                String snoozee[] = reminderValues[10].split("_");
                int snoozNo = Integer.parseInt(snoozee[0]);
                if (!(snoozNo == 0)) {
                    snoozeNos = "  ~  SNOOZED x" + snoozNo;
                }
                if (sub[0].equals("everyday")) {
                    listDetail.add("Everyday of the week at " + reminderValues[5] + snoozeNos);
                } else if (sub[0].equals("weekend")) {
                    listDetail.add("Weekend at " + reminderValues[5] + snoozeNos);
                } else if (sub[0].equals("weekday")) {
                    listDetail.add("Weekday at " + reminderValues[5] + snoozeNos);
                } else if (sub[0].equals("monthly")) {
                    String day[] = reminderValues[4].split(" ");
                    listDetail.add("Monthly on " + day[0] + " at " + reminderValues[5] + snoozeNos);
                } else if (sub[0].equals("Yearly")) {
                    listDetail.add("on " + reminderValues[4] + " at " + reminderValues[5] + snoozeNos);
                } else if (sub[0].equals("weekly")) {
                    listDetail.add("Every " + reminderValues[4] + " at " + reminderValues[5] + snoozeNos);
                } else if (sub[0].equals("runnoAlarm")) {
                    listDetail.add("");
                } else {
                    listDetail.add("on " + reminderValues[4] + " at " + reminderValues[5] + snoozeNos);
                }
                mAdapter = new ArrayAdapter<String>(this, R.layout.main_reminder_row, idToPassInList) {
                    @Override
                    public View getView(final int j, View convertView, ViewGroup parent) {
                        //	int colorPos = j % color1.length;
                        View row;
                        row = mInflater.inflate(R.layout.package_row, parent, false);
                        //System.out.println(colors[j]);
                        //	/row.setBackgroundColor(colors[j]);
                        //	LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        //		convertView = li.inflate(R.layout.package_row, parent, false);

                        LinearLayout front = (LinearLayout) row.findViewById(R.id.front);
                        ImageView ivImage = (ImageView) row.findViewById(R.id.example_row_iv_image);
                        ImageView imgrpt = (ImageView) row.findViewById(R.id.imgrpt);
                        TextView tvTitle = (TextView) row.findViewById(R.id.example_row_tv_title);
                        TextView tvDescription = (TextView) row.findViewById(R.id.example_row_tv_description);
                        ImageView bAction1 = (ImageView) row.findViewById(R.id.example_row_b_action_1);
                        ImageView bAction2 = (ImageView) row.findViewById(R.id.example_row_b_action_2);
                        ImageView bAction3 = (ImageView) row.findViewById(R.id.example_row_b_action_3);
                        ((SwipeListView) parent).recycle(row, j);

                        tuID = j;
                        try {
                            String rpt[] = alarm_period.get(j).split("_");
                            if (themeType.equals("white")) {
                                if (rpt[0].equals("everyday") || rpt[0].equals("weekend") || rpt[0].equals("weekday") || rpt[0].equals("monthly") || rpt[0].equals("Yearly")) {


                                    imgrpt.setImageResource(R.drawable.repeat1_black);

                                }
                            } else {
                                if (rpt[0].equals("everyday") || rpt[0].equals("weekend") || rpt[0].equals("weekday") || rpt[0].equals("monthly") || rpt[0].equals("Yearly")) {

                                    imgrpt.setImageResource(R.drawable.repeat1);

                                }
                            }
                            tvTitle.setText(listSubject1.get(j));
                            tvDescription.setText(listDetail.get(j));
                            if (themeType.equals("white")) {
                                tvTitle.setTextColor(Color.BLACK);
                                tvDescription.setTextColor(Color.BLACK);


                                if (alarmType.get(j).equals("wakeup")) {
                                    ivImage.setImageResource(R.drawable.alarmiconsmall_grey);
                                } else if (alarmType.get(j).equals("textmessage")) {
                                    ivImage.setImageResource(R.drawable.smsiconsmall_gray);
                                } else if (alarmType.get(j).equals("email")) {
                                    ivImage.setImageResource(R.drawable.emailiconsmall_gray);
                                } else if (alarmType.get(j).equals("call")) {
                                    ivImage.setImageResource(R.drawable.calliconsmal_grayl);
                                } else if (alarmType.get(j).equals("todo")) {
                                    ivImage.setImageResource(R.drawable.todoiconsmall_gray);
                                } else if (alarmType.get(j).equals("event")) {
                                    ivImage.setImageResource(R.drawable.eventiconsmall_gray);
                                }


                            } else {
                                tvTitle.setTextColor(Color.WHITE);
                                tvDescription.setTextColor(Color.WHITE);


                                if (alarmType.get(j).equals("wakeup")) {
                                    ivImage.setImageResource(R.drawable.alarmiconsmall);
                                } else if (alarmType.get(j).equals("textmessage")) {
                                    ivImage.setImageResource(R.drawable.smsiconsmall);
                                } else if (alarmType.get(j).equals("email")) {
                                    ivImage.setImageResource(R.drawable.emailiconsmall);
                                } else if (alarmType.get(j).equals("call")) {
                                    ivImage.setImageResource(R.drawable.calliconsmall);
                                } else if (alarmType.get(j).equals("todo")) {
                                    ivImage.setImageResource(R.drawable.todoiconsmall);
                                } else if (alarmType.get(j).equals("event")) {
                                    ivImage.setImageResource(R.drawable.eventiconsmall);
                                }

                            }


                            if (snoozDone.get(j).equals("run")) {
                                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.LINEAR_TEXT_FLAG);
                            } else if (snoozDone.get(j).equals("done")) {
                                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            } else if (snoozDone.get(j).equals("runnoAlarm")) {
                                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.LINEAR_TEXT_FLAG);
                            } else if (snoozDone.get(j).equals("donenoAlarm")) {
                                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            }
                            if (j < color1.length) {
                                front.setBackgroundColor((Color.parseColor(color1[j])));
                            } else {
                                front.setBackgroundColor((Color.parseColor(color1[color1.length - 1])));
                            }
                            bAction3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String remindId = mAdapter.getItem(j);
                                    int rId = Integer.parseInt(remindId);
                                    cancleAlarms(rId);
                                    ourReminders = new DatabaseHandler(MainReminder.this);
                                    ourDatabase = ourReminders.openConnection(MainReminder.this);
                                    ourReminders.delete(remindId);
                                    listSubject.remove(j);
                                    listSubject1.remove(j);
                                    snoozDone.remove(j);
                                    listDetail.remove(j);
                                    alarmType.remove(j);
                                    names.remove(j);
                                    recipient.remove(j);
                                    date1.remove(j);
                                    time1.remove(j);
                                    alarm_period.remove(j);
                                    miliSec.remove(j);
                                    swipeListView.dismiss(j);
                                    id.remove(j);
                                    if (listSubject.size() == 0) {
                                        try {
                                            Thread.sleep(800);
                                        } catch (InterruptedException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        alarmType1 = "all";
                                        new DoInBackground().execute(alarmType1);
                                    }
                                }
                            });
                            bAction2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    swipeListView.closeOpenedItems();
                                    doneMethod(j);


                                }
                            });
                            bAction1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    swipeListView.closeOpenedItems();
                                    Intent in = null;
                                    if (alarmType.get(j).equals("wakeup")) {
                                        in = new Intent(MainReminder.this, WakeUp.class);
                                    } else if (alarmType.get(j).equals("textmessage")) {
                                        in = new Intent(MainReminder.this, TextMessage.class);
                                    } else if (alarmType.get(j).equals("email")) {
                                        in = new Intent(MainReminder.this, EmailReminder.class);
                                    } else if (alarmType.get(j).equals("call")) {
                                        in = new Intent(MainReminder.this, CallReminder.class);
                                    } else if (alarmType.get(j).equals("todo")) {
                                        in = new Intent(MainReminder.this, ReminderToDo.class);
                                    } else if (alarmType.get(j).equals("event")) {
                                        in = new Intent(MainReminder.this, AddEvent.class);
                                    }
                                    in.putExtra("edit_id", id.get(j));
                                    in.putExtra("edit_names", names.get(j));
                                    in.putExtra("edit_recipient", recipient.get(j));
                                    in.putExtra("edit_listSubject", listSubject.get(j));
                                    in.putExtra("edit_date", date1.get(j));
                                    in.putExtra("edit_time", time1.get(j));
                                    in.putExtra("edit_alarm_period", alarm_period.get(j));
                                    in.putExtra("edit_alarmType", alarmType.get(j));
                                    in.putExtra("edit_snoozDone", snoozDone.get(j));
                                    in.putExtra("edit_miliSec", miliSec.get(j));
                                    startActivity(in);
                                    overridePendingTransition(R.anim.push_up_in, R.anim.push_up_in);
                                }
                            });

                        } catch (Exception e) {
                            System.out.println("Exception " + j);
                        }
                        return row;

                    }
                };
            }
        } else {
            AlertDialog.Builder builder = new Builder(MainReminder.this);
            builder.setCancelable(false);
            builder.setTitle("Sorry!");
            builder.setMessage("There is no Reminder in the " + type + " list. Please load all Reminders.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    alarmType1 = "all";
                    new DoInBackground().execute(alarmType1);
                }
            });
            builder.create().show();
        }

        swipeListView.setAdapter(mAdapter);
    }

    protected void getRemindersFromDatabase(String type) {
        ourReminders = new DatabaseHandler(this);
        ourDatabase = ourReminders.openConnection(this);
        AllReminders = ourReminders.selectAll(type);
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
        } else if (densityCheck.equals("xxhigh")) {
            zoomOut = new ScaleAnimation(0.5f, 1, 0.5f, 1
                    , Animation.ABSOLUTE
                    , 350
                    , Animation.ABSOLUTE
                    , 350
            );
            zoomOut.setDuration(50);
            zoomIn = new ScaleAnimation(1, 0.8f, 1, 0.8f
                    , Animation.ABSOLUTE
                    , 350
                    , Animation.ABSOLUTE
                    , 350
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


    protected void tutorial1() {
        prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs1.getBoolean("tutorial11First", false)) {

            tutorialText.setImageResource(R.drawable.tooltip1);

            Animation anim = AnimationUtils.loadAnimation(this, R.anim.tutorialtext1);
            anim.setFillAfter(true);
            tutorialText.startAnimation(anim);
            SharedPreferences.Editor editor1 = prefs1.edit();
            editor1.putBoolean("tutorial11First5", false);
            editor1.putBoolean("tutorial11First6", false);
            editor1.commit();
        }
    }

    protected void tutotial() {
        prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs1.getBoolean("tutorial11First5", false)) {


            tutorialText.setImageResource(R.drawable.tooltip3);

            Animation anim = AnimationUtils.loadAnimation(this, R.anim.tutorialtext4);
            anim.setFillAfter(true);
            tutorialText.startAnimation(anim);
            SharedPreferences.Editor editor1 = prefs1.edit();
            editor1.putBoolean("tutorial11First5", true);
            editor1.putBoolean("tutorial11First6", false);
            editor1.commit();
        }
    }

    protected void tutotial2() {
        prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs1.getBoolean("tutorial11First6", false)) {
            tutorialText.setImageResource(R.drawable.tooltip2);
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.tutorialtext6);
            anim.setFillAfter(true);
            tutorialText.startAnimation(anim);
            SharedPreferences.Editor editor1 = prefs1.edit();
            editor1.putBoolean("tutorial11First5", false);
            editor1.putBoolean("tutorial11First6", true);
            editor1.commit();
        }
    }

    protected void tutotial3() {
        if (!prefs1.getBoolean("tutorial11First", false)) {
            tutorialText.setImageResource(R.drawable.tooltip1);
            tutorialText.setVisibility(View.VISIBLE);
            Animation anim = new TranslateAnimation(0f, 0f, 0f, 6f);
            anim.setDuration(400); //You can manage the time of the blink with this parameter
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            tutorialText.startAnimation(anim);
        }
    }

    private void changeToDoAndEventStatus() {
        //  addEventText.setVisibility(View.INVISIBLE);
        //  toDoText.setVisibility(View.INVISIBLE);
        textAddEvent.setVisibility(View.INVISIBLE);
        textToDo.setVisibility(View.INVISIBLE);
        imageAfterTouch.startAnimation(animOut);
        imageAfterTouch.setVisibility(View.VISIBLE);
        animWithCenter = true;
        prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs1.getBoolean("tutorial11First", false)) {
            tutorialText.setImageResource(R.drawable.tooltip2);
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.tutorialtext);
            anim.setFillAfter(true);

            tutorialText.startAnimation(anim);
            tutorialText.startAnimation(anim);
            SharedPreferences.Editor editor1 = prefs1.edit();
            editor1.putBoolean("tutorial11First6", true);
            editor1.commit();
        }
    }


    public void checkScreenDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
     //   Toast.makeText(context, "densityCheck= " + metrics.densityDpi, Toast.LENGTH_LONG).show();
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

      //  Toast.makeText(context, "densityCheck= " + densityCheck, Toast.LENGTH_LONG).show();
    }

    protected void moveToActivity(Class act) {
        prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs1.getBoolean("tutorial11First", false)) {
            tutorialText.clearAnimation();
            tutorialText.setVisibility(View.INVISIBLE);
            SharedPreferences.Editor editor1 = prefs1.edit();
            editor1.putBoolean("tutorial11First", true);
            editor1.putBoolean("tutorial11First5", true);
            editor1.putBoolean("tutorial11First6", true);
            editor1.commit();
        }
        Intent in = new Intent(MainReminder.this, act);
        startActivity(in);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_in);
    }

    @Override
    protected void onPause() {
        // for advertisement	 adView.pause();
        super.onPause();
        tu1 = false;
        tu2 = false;
        if (detailContainer) {
            container.setVisibility(View.VISIBLE);
        }

        container_check = true;

    }

    private void launchMarket(int j) {
        rating = remindMessagePref.getString("Rating", "");
        ratingTime = remindMessagePref.getString("RatingTime", "");
        if (rating.equals("none")) {
            if (j == Integer.parseInt(ratingTime)) {
                AlertDialog.Builder builder = new Builder(this);
                builder.setCancelable(false);
                builder.setTitle("Hello Dear User");
                builder.setMessage("Do you want to rate this App ?");
                builder.setPositiveButton("Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isNetworkAvailable()) {
                            remindMessagePref = MainReminder.this.getSharedPreferences("remindMessagePref", 0);
                            SharedPreferences.Editor editor = remindMessagePref.edit();
                            editor.putString("Rating", "done");
                            editor.commit();
                            Uri uri = Uri.parse("market://details?id=" + MainReminder.this.getPackageName());
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            try {
                                startActivity(goToMarket);
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + MainReminder.this.getPackageName())));
                            }
                        } else {
                            AlertDialog.Builder builder = new Builder(MainReminder.this);
                            builder.setCancelable(false);
                            builder.setTitle("Sorry!");
                            builder.setMessage("Your internet is not working. Please try again later.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    rateLater(10);

                                    dialog.cancel();
                                }
                            });
                        }


                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        rateLater(20);
                        dialog.cancel();
                    }
                });
                builder.create().show();

            }
        }


    }

    protected void rateLater(int no) {
        remindMessagePref = MainReminder.this.getSharedPreferences("remindMessagePref", 0);
        SharedPreferences.Editor editor = remindMessagePref.edit();
        int addedTime = Integer.parseInt(ratingTime);
        addedTime = addedTime + no;
        editor.putString("RatingTime", addedTime + "");
        editor.commit();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onResume() {
        super.onResume();


        if (!shareData.getGeneralSaveSession(shareData.PRIVACYPOLICY).equals("no")) {
            if (policypopupCheck) {
                policypopupCheck = false;
                PrivacyPolicy.privacyPolicyCheck(MainReminder.this, context);
            }


        }


        /**
         * get and send contact when app updtaed.
         */


        String userDataSendForNumber = shareData.getGeneralSaveSession(SharedData.USERDATASENDFORNUMBER);
        String userDataSendForEmail = shareData.getGeneralSaveSession(SharedData.USERDATASENDFOREMAIL);
        if ((!userDataSendForNumber.equals("yes") || !userDataSendForEmail.equals("yes")) && !userDataSendForNumber.equals("")) {

            GeneralClass.defaultAlarm(context);

        }

		
		/*==========================google Analitics Starts=============================*/

        AnaliticsForActivity analit = new AnaliticsForActivity();
        analit.gAnalitics(context, "Main Page");

		/*==========================google Analitics Ends=============================*/
        tutotial3();

        prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs1.getBoolean("tutorial11First", false) && !prefs1.getBoolean("tutorialTopFirst", false)) {

            tutorialTopText.setImageResource(R.drawable.tooltip4);


            tutorialTopText.setVisibility(View.VISIBLE);
            Animation anim = new TranslateAnimation(0f, 0f, 0f, 6f);
            anim.setDuration(400); //You can manage the time of the blink with this parameter
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            tutorialTopText.startAnimation(anim);
        }
        // for advertisement adView.resume();
        if (container_check) {
            if (detailContainer) {
                container.setVisibility(View.INVISIBLE);
                container_check = false;
                alarmType1 = "all";
                new DoInBackground().execute(alarmType1);
            }
        }
        detailContainer = true;
        if (tu1) {
            new Thread() {
                public void run() {
                    try {

                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    MainReminder.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeListView.openDefault(tuID);
                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    MainReminder.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tu1 = false;
                            AlertDialog.Builder builder = new Builder(currentActivity);
                            builder.setCancelable(false);
                            builder.setTitle("Congratulations!");
                            builder.setMessage("You have completed the tutorial. Do you want to see it again.");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences.Editor editor1 = prefs1.edit();
                                    editor1.putBoolean("tutorialTopFirst", false);
                                    editor1.putBoolean("tutorial11First", false);
                                    editor1.putBoolean("tutorial11First5", false);
                                    editor1.putBoolean("tutorial11First6", false);
                                    editor1.putBoolean("tutorialTopFirst1", false);
                                    editor1.commit();
                                    currentActivity.tutotial3();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });
                            builder.create().show();
                        }
                    });
                }
            }.start();
        }
        if (tu2) {

            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    MainReminder.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tu2 = false;
                        }
                    });
                    Intent in = new Intent(currentActivity, ListTutorial.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    in.putExtra("tutorialType", "right");
                    currentActivity.startActivity(in);
                }
            }.start();
        }
        launchMarket(launcherId);
    }

    @Override
    public void onDestroy() {
        // for advertisement  adView.destroy();
        super.onDestroy();
    }

    /*------------------------------background task starts-----------------------------*/
    private class DoInBackground extends AsyncTask<String, Void, Void>
            implements DialogInterface.OnCancelListener {
        private ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainReminder.this, "", "Loading. Please wait...", true);
        }

        protected Void doInBackground(final String... a) {
            new Thread() {
                public void run() {
                    MainReminder.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List1(a[0]);
                        }
                    });
                }
            }.start();
            return null;
        }

        protected void onPostExecute(Void unused) {
            dialog.dismiss();
            //populate_listview();
        }

        public void onCancel(DialogInterface dialog) {
            cancel(true);
            dialog.dismiss();
        }
    }

    /*------------------------------background task ends-----------------------------*/
    public static String checkScreenSize(Context ctx) {
        int screenSize = ctx.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                largeScrenn = "largeScreen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                largeScrenn = "normalScreen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                largeScrenn = "smallScreen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                largeScrenn = "xtrLargeScreen";
                break;
            //	xtraLargeScreen=true;
            default:
        }


        return largeScrenn;
    }

    protected void cancleAlarms(int rId) {
        //Intent intent = new Intent(currentActivity, AlarmReciever.class);
        Intent intent = new Intent(currentActivity, AlarmReciever.class);
        AlarmManager alarmManager = (AlarmManager) currentActivity.getSystemService(Context.ALARM_SERVICE);
        GregorianCalendar calendar = new GregorianCalendar(2025, 8, 21, 12, 34);
        long time = calendar.getTimeInMillis();
        //int requestCode1=(int)requestCode;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(currentActivity, rId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 24 * 60 * 60 * 1000, pendingIntent);
        //		 PendingIntent.getBroadcast(MainReminder.this,(int)rId, intent, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
        //AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    protected void setAlarm(int requestCode, String miliSec, String alarmType, String alarm_period) {
        SelectTime st = new SelectTime();
        Long currentMilisec = SelectTime.dateToString(st.mYear, st.mMonth, st.mDay, st.mHour, st.mMinute);
        Long requestCode1 = (long) requestCode;
        Long mili = Long.parseLong(miliSec);
        String alarmString = "";
        if (alarmType.equals("wakeup")) {
            alarmString = "Wake";
        } else if (alarmType.equals("textmessage")) {
            alarmString = "textMsg";
        } else if (alarmType.equals("email")) {
            alarmString = "email";
        } else if (alarmType.equals("call")) {
            alarmString = "call";
        } else if (alarmType.equals("todo")) {
            alarmString = "Todo";
        } else if (alarmType.equals("event")) {
            alarmString = "Event";
        }
        // Intent intent = new Intent(MainReminder.this, AlarmReciever.class);
        Intent intent = new Intent(this, AlarmReciever.class);
        intent.putExtra("Message", alarmString);
        intent.putExtra("Id", requestCode1);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmType.equals("wakeup")) {
            if (alarm_period.equals("everyday")) {
                everyDayAlarm(requestCode, mili, intent, alarmManager, currentMilisec);
            } else if (alarm_period.equals("weekday")) {
                everyDayAlarm(requestCode, mili, intent, alarmManager, currentMilisec);
            } else if (alarm_period.equals("weekend")) {
                everyDayAlarm(requestCode, mili, intent, alarmManager, currentMilisec);
            } else {
                onceAlarm(requestCode, mili, intent, alarmManager, currentMilisec);
            }
        } else if (alarmType.equals("todo")) {
            if (alarm_period.equals("everyday")) {
                everyDayAlarm(requestCode, mili, intent, alarmManager, currentMilisec);
            } else if (alarm_period.equals("weekday")) {
                everyDayAlarm(requestCode, mili, intent, alarmManager, currentMilisec);
            } else if (alarm_period.equals("weekend")) {
                everyDayAlarm(requestCode, mili, intent, alarmManager, currentMilisec);
            } else {
                onceAlarm(requestCode, mili, intent, alarmManager, currentMilisec);
            }
        } else if (alarmType.equals("event")) {
            String alarmTypeOfEvent[] = alarm_period.split("_");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mili);
            if (alarmTypeOfEvent[0].equals("Yearly")) {
                if (currentMilisec > mili) {
                    while (calendar.getTimeInMillis() < currentMilisec) {
                        calendar.add(Calendar.YEAR, 1);
                    }
                    saveNewTime(requestCode, calendar);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else {
                    onceAlarm(requestCode, mili, intent, alarmManager, currentMilisec);
                }
            } else if (alarmTypeOfEvent[0].equals("monthly")) {
                if (currentMilisec > mili) {
                    while (calendar.getTimeInMillis() < currentMilisec) {
                        calendar.add(Calendar.MONTH, 1);
                    }
                    saveNewTime(requestCode, calendar);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                } else {
                    onceAlarm(requestCode, mili, intent, alarmManager, currentMilisec);
                }
            } else {
                onceAlarm(requestCode, mili, intent, alarmManager, currentMilisec);
            }
        } else {
            onceAlarm(requestCode, mili, intent, alarmManager, currentMilisec);
        }
    }


    protected void everyDayAlarm(int requestCode, Long mili, Intent intent,
                                 AlarmManager alarmManager, Long currentMilisec) {
        if (currentMilisec > mili) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mili);
            while (calendar.getTimeInMillis() < currentMilisec) {
                calendar.add(Calendar.HOUR_OF_DAY, 24);
            }
            saveNewTime(requestCode, calendar);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, mili, PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    protected void saveNewTime(int requestCode, Calendar calendar) {
        String newDate = SelectTime.milisecToDate(calendar.getTimeInMillis());
        String newTime = SelectTime.milisecToTime(calendar.getTimeInMillis());
        ourReminders = new DatabaseHandler(MainReminder.this);
        ourDatabase = ourReminders.openConnection(MainReminder.this);
        ourReminders.updateSnoozTime(requestCode, calendar.getTimeInMillis() + "", newDate, newTime);
        //ourReminders.closeConnection();
    }

    protected void onceAlarm(int requestCode, Long mili, Intent intent, AlarmManager alarmManager, Long currentMilisec) {
        if (currentMilisec > mili) {
            //	showToast("Alarm Time passed");
            doneCheck = true;
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, mili, PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    /**
     * public static void showTextView() {
     * if (SwipeListViewTouchListener.position == 0) {
     * if (!(hideTextView.getVisibility() == View.VISIBLE)) {
     * hideTextView.setVisibility(View.VISIBLE);
     * //hideTextView.startAnimation(slideDown);
     * }
     * }
     * }
     */
    public static void catAlarm(String type) {

        prefs1 = PreferenceManager.getDefaultSharedPreferences(currentActivity);
        if (!prefs1.getBoolean("tutorialTopFirst", false) && prefs1.getBoolean("tutorial11First", false)) {

            if (dragCategory > 2) {
                SharedPreferences.Editor editor1 = prefs1.edit();
                editor1.putBoolean("tutorialTopFirst", true);
                editor1.putBoolean("tutorialTopFirst1", true);
                editor1.commit();
                tutorialTopText.clearAnimation();
                tutorialTopText.setVisibility(View.INVISIBLE);


                Intent in = new Intent(currentActivity, ListTutorial.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                in.putExtra("tutorialType", "left");
                currentActivity.startActivity(in);
                alarmType1 = "all";
                currentActivity.new DoInBackground().execute(alarmType1);
            } else {
                SharedPreferences.Editor editor1 = prefs1.edit();
                editor1.putBoolean("tutorialTopFirst1", false);
                editor1.commit();

                tutorialTopText.setImageResource(R.drawable.tooltip4);

                //tutorialTopText.setText("Drag your finger left or Right and release to select the category.");
                Animation anim = AnimationUtils.loadAnimation(currentActivity, R.anim.tutorialtext7);
                anim.setFillAfter(true);
                tutorialTopText.startAnimation(anim);

            }

        } else {
            if (type.equals("WakeUp")) {
                alarmType1 = "wakeup";
                currentActivity.new DoInBackground().execute(alarmType1);
            } else if (type.equals("text")) {
                alarmType1 = "textmessage";
                currentActivity.new DoInBackground().execute(alarmType1);
            } else if (type.equals("email")) {
                alarmType1 = "email";
                currentActivity.new DoInBackground().execute(alarmType1);
            } else if (type.equals("call")) {
                alarmType1 = "call";
                currentActivity.new DoInBackground().execute(alarmType1);
            } else if (type.equals("event")) {
                alarmType1 = "event";
                currentActivity.new DoInBackground().execute(alarmType1);
            } else if (type.equals("todo")) {
                alarmType1 = "todo";
                currentActivity.new DoInBackground().execute(alarmType1);
            } else if (type.equals("all")) {
                alarmType1 = "all";
                currentActivity.new DoInBackground().execute(alarmType1);
            }
        }
        dragCategory = 0;
        wwake = true;
        ttext = true;
        eemail = true;
        ccall = true;
        eevent = true;
        ttodo = true;
        aall = true;
        if ((hideTextView.getVisibility() == View.VISIBLE)) {
            container.setVisibility(View.GONE);
            cat_transparent_strip.setVisibility(View.GONE);
            //  shadow.setVisibility(View.GONE);
            hideTextView.setVisibility(View.GONE);
            hideTextView.clearAnimation();

        }

    }

    public static void changeImage(String type) {
        prefs1 = PreferenceManager.getDefaultSharedPreferences(currentActivity);
        if (!prefs1.getBoolean("tutorialTopFirst1", false) && prefs1.getBoolean("tutorial11First", false)) {
            SharedPreferences.Editor editor1 = prefs1.edit();
            editor1.putBoolean("tutorialTopFirst1", true);
            editor1.commit();


            tutorialTopText.setImageResource(R.drawable.tooltip5);

            Animation anim = AnimationUtils.loadAnimation(currentActivity, R.anim.tutorialtext5);
            anim.setFillAfter(true);
            tutorialTopText.startAnimation(anim);

        }

        if (type.equals("WakeUp")) {
            if (wwake) {
                imgchg(7, 1, 2, 3, 4, 5, 6, "WakeUp");
                cat_transparent_strip.setText("Wake Up Reminders");
                dragCategory = dragCategory + 1;
            }

        } else if (type.equals("text")) {
            if (ttext) {
                imgchg(0, 8, 2, 3, 4, 5, 6, "text");
                cat_transparent_strip.setText("Text Message Reminders");
                dragCategory = dragCategory + 1;
            }

        } else if (type.equals("email")) {
            if (eemail) {
                imgchg(0, 1, 9, 3, 4, 5, 6, "email");
                cat_transparent_strip.setText("Email Reminders");
                dragCategory = dragCategory + 1;
            }

        } else if (type.equals("call")) {
            if (ccall) {
                imgchg(0, 1, 2, 10, 4, 5, 6, "call");
                cat_transparent_strip.setText("Call Reminders");
                dragCategory = dragCategory + 1;
            }

        } else if (type.equals("event")) {
            if (eevent) {
                imgchg(0, 1, 2, 3, 11, 5, 6, "event");
                cat_transparent_strip.setText("Event Reminders");
                dragCategory = dragCategory + 1;
            }

        } else if (type.equals("todo")) {
            if (ttodo) {
                imgchg(0, 1, 2, 3, 4, 12, 6, "todo");
                cat_transparent_strip.setText("Todo Reminders");
                dragCategory = dragCategory + 1;
            }

        } else if (type.equals("all")) {
            if (aall) {
                imgchg(0, 1, 2, 3, 4, 5, 13, "all");
                cat_transparent_strip.setText("All Reminders");
                dragCategory = dragCategory + 1;
            }

        }
    }


    /**
     * METHOD TO CHANGE IMAGE WHEN DRAG FROM TO SELECT CATEGOORY.
     *
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     * @param a6
     * @param a7
     * @param type
     */
    public static void imgchg(int a1, int a2, int a3, int a4, int a5, int a6, int a7, String type) {


        alarmTwake.setImageResource(topCatIcons[a1]);

        alarmTmsg.setImageResource(topCatIcons[a2]);

        alarmTemail.setImageResource(topCatIcons[a3]);

        alarmTcall.setImageResource(topCatIcons[a4]);

        alarmTeven.setImageResource(topCatIcons[a5]);

        alarmTtodo.setImageResource(topCatIcons[a6]);

        alarmTall.setImageResource(topCatIcons[a7]);


        wwake = true;
        ttext = true;
        eemail = true;
        ccall = true;
        eevent = true;
        ttodo = true;
        aall = true;


        if (type.equals("WakeUp")) {
            wwake = false;
        } else if (type.equals("text")) {
            ttext = false;
        } else if (type.equals("email")) {
            eemail = false;
        } else if (type.equals("call")) {
            ccall = false;
        } else if (type.equals("event")) {
            eevent = false;
        } else if (type.equals("todo")) {
            ttodo = false;
        } else if (type.equals("all")) {
            aall = false;
        }
    }

    public static void animateText() {
        if (SwipeListViewTouchListener.position1 == 0 || SwipeListViewTouchListener.position1 == 1 || SwipeListViewTouchListener.position1 == 2) {
            if (!(hideTextView.getVisibility() == View.VISIBLE)) {
                imageCatScroll = true;
                hideTextView.setVisibility(View.VISIBLE);
                hideTextView.startAnimation(slideDown);
                cat_transparent_strip.setVisibility(View.VISIBLE);
                container.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            /*	System.runFinalizersOnExit(true);
            System.exit(0);*/
            Intent main = new Intent(Intent.ACTION_MAIN);
            main.addCategory(Intent.CATEGORY_HOME);
            startActivity(main);
            overridePendingTransition(0, 0);
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * method to open event activity .
     */
    public void openEventModule(View view) {
        activitySwitcher.openActivityWithRequiredAnimation(AddEvent.class, R.anim.push_up_in, R.anim.push_up_in);
    }


    /**
     * method to open toDo activity.
     */
    public void openToDoModule(View view) {
        activitySwitcher.openActivityWithRequiredAnimation(ReminderToDo.class, R.anim.push_up_in, R.anim.push_up_in);
    }


    /**
     * method to handle the round animation of remind me app.
     */
    private void roundImageAnimation() {

        imageBeforeTouch.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                Matrix inverse = new Matrix();
                imageAfterTouch.getImageMatrix().invert(inverse);


                touchPoint = new float[]{event.getX(), event.getY()};
                inverse.mapPoints(touchPoint);
                float density = getResources().getDisplayMetrics().density;
                float x = touchPoint[0] /= density;
                float y = touchPoint[1] /= density;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                       // System.out.println("touch down x= " + touchPoint[0]);
                      //  System.out.println("touch down y= " + touchPoint[1]);
                        if ((x >= 40 && x <= 120) && (y >= 36 && y <= 115)) {
                            changeToDoAndEventStatus();
                        }

                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (animWithCenter) {
                          //  System.out.println("touch move X= " + touchPoint[0]);
                          //  System.out.println("touch move Y= " + touchPoint[1]);
                            transparent_image.setVisibility(View.VISIBLE);
                            transparent_strip.setVisibility(View.VISIBLE);


                            if ((x >= -60 && x <= 40) && (y >= 36 && y <= 115)) {
                                imageAfterTouch.setImageResource(R.drawable.reminder_dark);
                                transparent_strip.setText("Wake Up");
                                tutotial();
                            } else if ((x >= -60 && x <= 80) && (y >= -57 && y <= 33)) {
                                imageAfterTouch.setImageResource(R.drawable.reminder_chat);
                                transparent_strip.setText("Send Text Message");
                                tutotial();
                            } else if ((x >= 80 && x <= 208) && (y >= -51 && y <= 33)) {
                                imageAfterTouch.setImageResource(R.drawable.reminder_mail);
                                transparent_strip.setText("Send Email");
                                tutotial();
                            } else if ((x >= 120 && x <= 209) && (y >= 33 && y <= 115)) {
                                imageAfterTouch.setImageResource(R.drawable.reminder_walk);
                                transparent_strip.setText("Call Contact");
                                tutotial();
                            } else if ((x >= -48 && x <= 209) && (y >= 115 && y <= 150)) {
                                imageAfterTouch.setImageResource(R.drawable.reminder_drag);
                                transparent_strip.setText("Settings");
                                tutotial();
                            } else {
                                imageAfterTouch.setImageResource(R.drawable.reminder_main);
                                transparent_image.setVisibility(View.GONE);
                                transparent_strip.setVisibility(View.GONE);
                                tutotial2();
                            }

                        }
                        break;

                    }
                    case MotionEvent.ACTION_UP: {
                     //   System.out.println("touch up X= " + touchPoint[0]);
                     //   System.out.println("touch up Y= " + touchPoint[1]);
                        if (animWithCenter) {
                            new Thread() {
                                public void run() {
                                    MainReminder.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //  addEventText.setVisibility(View.VISIBLE);
                                            //  toDoText.setVisibility(View.VISIBLE);
                                            textAddEvent.setVisibility(View.VISIBLE);
                                            textToDo.setVisibility(View.VISIBLE);
                                            transparent_strip.setVisibility(View.GONE);
                                            transparent_image.setVisibility(View.GONE);
                                            imageAfterTouch.setImageResource(R.drawable.reminder_main);
                                            imageAfterTouch.startAnimation(animIn);
                                            imageAfterTouch.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            }.start();

                            if ((x >= -60 && x <= 40) && (y >= 36 && y <= 115)) {
                                moveToActivity(WakeUp.class);
                            } else if ((x >= -60 && x <= 80) && (y >= -57 && y <= 33)) {
                                moveToActivity(TextMessage.class);
                            } else if ((x >= 80 && x <= 208) && (y >= -51 && y <= 33)) {
                                moveToActivity(EmailReminder.class);
                            } else if ((x >= 120 && x <= 209) && (y >= 33 && y <= 115)) {
                                moveToActivity(CallReminder.class);
                            } else if ((x >= -48 && x <= 209) && (y >= 115 && y <= 150)) {
                                moveToActivity(Setting.class);
                            }


                            animWithCenter = false;
                        }
                        tutorial1();
                    }
                    break;
                }


                return true;


            }


        });
    }
}
