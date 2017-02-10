package com.lovelife.lovelife.LoveLifeUtility;

import android.content.Context;
import android.media.MediaPlayer;

import com.lovelife.lovelife.R;


/**
 * Created by dx on 9/4/2015.
 */
public class ToneManager {

    public static void tone(Context context){
        MediaPlayer mp = MediaPlayer.create(context, R.raw.android_latest);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }

        });
        mp.start();
    }

}
