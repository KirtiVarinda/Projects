package com.remindme.manage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.remindme.R;

/**
 * Created by getfixr on 7/4/2015.
 */
public class ActivitySwitcher {
    private static Activity mthis;

    /**
     * constructer to initialize the
     * context for particular activity
     *
     */
    public ActivitySwitcher(Activity mthis) {
        this.mthis = mthis;
    }


    /**
     * method to switch the activity.
     */

    public void switchActivity(Class activity,String messageFor,String textEdit){
        Intent intent=new Intent(mthis,activity);
        if(!messageFor.equals("no")){
            intent.putExtra("MessageFor", messageFor);
            intent.putExtra("textEdit",textEdit);
        }

        mthis.startActivity(intent);
        mthis.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }


    public void openActivityWithRequiredAnimation(Class activity,int startAnim,int endAnim){
        Intent intent=new Intent(mthis,activity);
        mthis.startActivity(intent);
        mthis.overridePendingTransition(startAnim, endAnim);


    }

}
