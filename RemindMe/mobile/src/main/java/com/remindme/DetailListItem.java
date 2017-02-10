package com.remindme;

import java.util.List;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailListItem extends Activity implements OnTouchListener, AnimationListener {
    boolean animUp = false;
    String densityCheck = "";

    TextView bigSubject, smallMsg;
    ImageView img;
    static Animation slideUp, slideDown;
    int snoozNo = 0;
    String milisecToSnooze = "";
    LinearLayout backColor,below;
    RelativeLayout blackAreaClicked;
    private DatabaseHandler ourReminders;
    private SQLiteDatabase ourDatabase;
    public List<String[]> reminderWithId = null;
    static int sdk = android.os.Build.VERSION.SDK_INT;
    String bd_id, names, recipient, subject, date, time, alarm_period, alarm_type, db_milisec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_list_item);
        blackAreaClicked = (RelativeLayout) findViewById(R.id.layout1);
        backColor = (LinearLayout) findViewById(R.id.layout2);
        bigSubject = (TextView) findViewById(R.id.textView1);
        below= (LinearLayout) findViewById(R.id.below);
        smallMsg = (TextView) findViewById(R.id.textView2);
        img = (ImageView) findViewById(R.id.textView4);


        slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_out);
        slideDown.setAnimationListener(this);
        slideUp.setAnimationListener(this);

        backColor.setVisibility(View.VISIBLE);
        backColor.startAnimation(slideDown);
        blackAreaClicked.setOnClickListener(new OnClickListener() {
            public void onClick(View viewParam) {
                animSlideUp();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id_for_detail = extras.getString("id_for_detail");
            String theme_for_detail = extras.getString("theme_for_detail");
            themeColor(theme_for_detail);
            getData(Long.parseLong(id_for_detail));

            imageSelector(theme_for_detail);
        }
    }

    private void animSlideUp() {
        animUp = true;
        backColor.clearAnimation();
        backColor.setVisibility(View.INVISIBLE);
        backColor.startAnimation(slideUp);
    }



    protected void imageSelector(String themeType) {

        if (themeType.equals("white")) {


            if (alarm_type.equals("wakeup")) {
                img.setImageResource(R.drawable.alarmiconsmall_grey);
            } else if (alarm_type.equals("textmessage")) {
                img.setImageResource(R.drawable.smsiconsmall_gray);
            } else if (alarm_type.equals("email")) {
                img.setImageResource(R.drawable.emailiconsmall_gray);
            } else if (alarm_type.equals("call")) {
                img.setImageResource(R.drawable.calliconsmal_grayl);
            } else if (alarm_type.equals("todo")) {
                img.setImageResource(R.drawable.todoiconsmall_gray);
            } else if (alarm_type.equals("event")) {
                img.setImageResource(R.drawable.eventiconsmall_gray);
            }


        } else {


            if (alarm_type.equals("wakeup")) {
                img.setImageResource(R.drawable.alarmiconsmall);
            } else if (alarm_type.equals("textmessage")) {
                img.setImageResource(R.drawable.smsiconsmall);
            } else if (alarm_type.equals("email")) {
                img.setImageResource(R.drawable.emailiconsmall);
            } else if (alarm_type.equals("call")) {
                img.setImageResource(R.drawable.calliconsmall);
            } else if (alarm_type.equals("todo")) {
                img.setImageResource(R.drawable.todoiconsmall);
            } else if (alarm_type.equals("event")) {
                img.setImageResource(R.drawable.eventiconsmall);
            }


        }
    }

    protected void themeColor(String theme_selector) {
        if (theme_selector.equals("purple")) {
            backColor.setBackgroundColor((Color.parseColor("#9000d9")));
            below.setBackgroundColor((Color.parseColor("#784A90")));
        } else if (theme_selector.equals("red")) {
            backColor.setBackgroundColor((Color.parseColor("#e14c4c")));
            below.setBackgroundColor((Color.parseColor("#dd2f2e")));
        } else if (theme_selector.equals("yellow")) {
            backColor.setBackgroundColor((Color.parseColor("#FFB846")));
            below.setBackgroundColor((Color.parseColor("#ffab24")));
        } else if (theme_selector.equals("green")) {
            backColor.setBackgroundColor((Color.parseColor("#24C595")));
            below.setBackgroundColor((Color.parseColor("#1c9a74")));
        } else if (theme_selector.equals("white")) {
            backColor.setBackgroundColor((Color.parseColor("#ffffff")));
            below.setBackgroundColor((Color.parseColor("#e6e6e6")));
            bigSubject.setTextColor(Color.parseColor("#000000"));
            smallMsg.setTextColor(Color.parseColor("#000000"));
        } else if (theme_selector.equals("blue")) {
            backColor.setBackgroundColor((Color.parseColor("#2EB8FF")));
            below.setBackgroundColor((Color.parseColor("#00a5fa")));
        }
    }

    public void getData(long id) {
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
            String sub[];
            sub = alarm_period.split("_");
            String removeComman = reminderValues[1];

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
                } else if (sub[0].equals("oneDay")) {
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
            } else if (sub[0].equals("runnoAlarm")) {
                smallMsg.setText("");
                smallMsg.setVisibility(View.GONE);
                bigSubject.setPadding(15, 0, 15, 20);
            } else {
                smallMsg.setText("on " + reminderValues[4] + " at " + reminderValues[5] + snoozeNos);
            }
        }
        ourReminders.closeConnection();
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

            animSlideUp();


            //	overridePendingTransition(R.anim.slide_up_out, R.anim.slide_up_out);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // TODO Auto-generated method stub
        if (animUp) {
            finish();
            overridePendingTransition(0, 0);
            animUp = false;
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void onPause() {
        // for advertisement	adView.pause();
        super.onPause();


    }
}
