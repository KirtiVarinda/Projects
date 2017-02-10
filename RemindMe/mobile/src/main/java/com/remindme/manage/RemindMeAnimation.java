package com.remindme.manage;

import android.text.Layout;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

/**
 * Created by dx on 7/6/2015.
 */
public class RemindMeAnimation {


    /**
     * Translate the linear layout from left to right.
     * @param layout
     */
    public static void leftToRightTranslation(LinearLayout layout) {
        TranslateAnimation anim = new TranslateAnimation(0, 500, 0, 0);
        anim.setDuration(150);
        anim.setFillAfter(false);
        layout.setVisibility(View.GONE);
        layout.setAnimation(anim);
    }

    /**
     * Translate the linear layout from right to left.
     * @param layout
     */
    public static void rightToLeftAnimation(LinearLayout layout) {
        TranslateAnimation anim1 = new TranslateAnimation(500, 0, 0, 0);
        anim1.setDuration(150);
        anim1.setFillAfter(false);
        layout.setVisibility(View.VISIBLE);
        layout.setAnimation(anim1);
    }

}
