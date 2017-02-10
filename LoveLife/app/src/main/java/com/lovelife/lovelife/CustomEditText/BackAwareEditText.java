package com.lovelife.lovelife.CustomEditText;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.lovelife.lovelife.LoginActivity;

public class BackAwareEditText extends EditText {

    public BackAwareEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public BackAwareEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public BackAwareEditText(Context context) {
        super(context);

    }

    public static final String INTENT_FILTER = "INTENT_FILTER";

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            LoginActivity.keyPressed();
            dispatchKeyEvent(event);
            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }
}