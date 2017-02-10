package com.remindme;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ReminderMessage extends Activity {
    String themeType;
    Bitmap[] themeImages;
    String densityCheck = "";
    SharedPreferences remindMessagePref;
    EditText getMessage;
    String messageFor;
    int[] images = {R.drawable.sms_bg, R.drawable.back_walk, R.drawable.wake_icon, R.drawable.border_field};
    Bitmap[] denisityBitmap = new Bitmap[images.length];
    ImageView back, edit_image;
    TextView top_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_message);
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
        buttonClicked();
        getImagesInBitmap();
        sdkCheck();
        MessageFor();
    }

    protected void buttonClicked() {
        back.setOnClickListener(new OnClickListener() {
            public void onClick(View viewParam) {
                getMessage();
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_left);
            }
        });
    }

    protected void getMessage() {
        SharedPreferences.Editor editor = remindMessagePref.edit();
        if (getMessage.getText().toString().equals("")) {
            editor.putString("reminder_message", "no");
        } else {
            editor.putString("reminder_message", getMessage.getText().toString());
        }
        editor.commit();
    }

    protected void MessageFor() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            messageFor = extras.getString("MessageFor");
            String textEdit = extras.getString("textEdit");
            if (textEdit.equals("Subject") || textEdit.equals("Write Event")) {
                getMessage.setText("");
            } else {
                getMessage.setText(textEdit);
            }
            getMessage.setSelection(getMessage.getText().length());
            if (messageFor.equals("wake_up")) {
                top_message.setText("Wake up for");
            } else if (messageFor.equals("text_message")) {
                top_message.setText("Message Subject");
            } else if (messageFor.equals("email_message")) {
                top_message.setText("Email Subject");
            } else if (messageFor.equals("call_message")) {
                top_message.setText("Call contact for");
            } else if (messageFor.equals("to_do")) {
                top_message.setText("To Do Subject");
            } else if (messageFor.equals("Event_name")) {
                top_message.setText("Name");
            } else if (messageFor.equals("Event_Type")) {
                top_message.setText("Event Type");
            } else if (messageFor.equals("Event_sub")) {
                top_message.setText("Subject");
            } else if (messageFor.equals("hourly_reminder")) {
                top_message.setText(getResources().getString(R.string.medicine_name));
            }

        }
    }

    protected void getTextViewImages() {
        //wakeup_top = (TextView )findViewById(R.id.textView1);
        back = (ImageView) findViewById(R.id.textView4);
        edit_image = (ImageView) findViewById(R.id.textView2);
        top_message = (TextView) findViewById(R.id.textView3);
        getMessage = (EditText) findViewById(R.id.editText1);


    }

    protected void getImagesInBitmap() {
        for (int i = 0; i < images.length; i++) {
            denisityBitmap[i] = ((BitmapDrawable) getResources().getDrawable(images[i])).getBitmap();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void sdkCheck() {
        //DisplayMetrics metrics = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //denisityBitmap=ScreenDenesityCheck.checkScreenDensityAndResize(denisityBitmap,metrics,densityCheck,MainReminder.checkScreenSize(getApplicationContext()));

        if (!themeType.equals("purple")) {
            denisityBitmap[1] = themeImages[0];
        }

        //wakeup_top.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[0]));
        back.setImageBitmap(denisityBitmap[1]);
        edit_image.setImageBitmap(denisityBitmap[2]);
        //getMessage.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[3]));

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            getMessage();
            finish();
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_left);
            return false;
        }
        return super.onKeyDown(keyCode, event);
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
}
