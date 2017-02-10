package com.lovelife.lovelife.LoveLifeUtility;

import android.app.Activity;
import android.content.Intent;

import com.lovelife.lovelife.R;
import com.lovelife.lovelife.StringResources.LoginResource;

import java.io.Serializable;


/**
 * Created by Dx on 6/18/2015.
 * This class is used to switch activities.
 */
public class SwitchActivities {

    /**
     * open desired activity.
     */
    public void openActivityWithAnim(Activity mThis, Class openActivity) {
        Intent intent = new Intent(mThis, openActivity);
        mThis.startActivity(intent);
        mThis.overridePendingTransition(R.anim.open, R.anim.close);

    }  /**
     * open desired activity.
     */
    public void openActivity(Activity mThis, Class openActivity) {
        Intent intent = new Intent(mThis, openActivity);
        mThis.startActivity(intent);

    }
    /**
     * open desired activity.
     */
    public void openActivityByRemovingAll(Activity mThis, Class openActivity) {
        Intent intent = new Intent(mThis, openActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mThis.startActivity(intent);

    }
    /**
     * open desired activity.
     */



    public void openActivityFinishCurrent(Activity mThis, Class openActivity) {
        Intent intent = new Intent(mThis, openActivity);
        mThis.startActivity(intent);
//        mThis.finish();

    }

    /**
     * open desired activity.
     */
    public void openActivityAndClearAllOtherActivities(Activity mThis, Class openActivity) {
        Intent intent = new Intent(mThis, openActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mThis.startActivity(intent);
    }

    public void exits(Activity mThis, Class AddFunCard) {
        Intent intent = new Intent(mThis, AddFunCard);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mThis.startActivity(intent);
    }

    /**
     * open desired activity.
     * with single message(putExtra) transfer.
     */
    public void openActivityWithPutExtra(Activity mThis, Class openActivity, String[] key, String[] value) {
        Intent intent = new Intent(mThis, openActivity);
        for(int i=0;i<key.length;i++){
            intent.putExtra(key[i], value[i]);
        }
        mThis.startActivity(intent);
       // mThis.overridePendingTransition(R.anim.right_to_left_anim, R.anim.exit);

    }
    public void openActivityWithPutExtraAndAnim(Activity mThis, Class openActivity, String[] key, String[] value) {
        Intent intent = new Intent(mThis, openActivity);
        for(int i=0;i<key.length;i++){
            intent.putExtra(key[i], value[i]);
        }
        mThis.startActivity(intent);
        mThis.overridePendingTransition(0, 0);
        // mThis.overridePendingTransition(R.anim.right_to_left_anim, R.anim.exit);

    }
    /**
     * open desired activity.
     * with single message(putExtra) transfer.
     */
    public void openActivityWithObjectAsPutExtra(Activity mThis, Class openActivity, String[] key, Serializable[] value) {
        Intent intent = new Intent(mThis, openActivity);
        for(int i=0;i<key.length;i++){
            intent.putExtra(key[i], value[i]);
        }
        mThis.startActivity(intent);
        // mThis.overridePendingTransition(R.anim.right_to_left_anim, R.anim.exit);

    }

    /**
     * open desired activity.
     * with single message(putExtra) transfer.
     */
    public void socialActivityTransitionFromRegistration(Activity mThis, Class openActivity, String[] key, Serializable[] value) {
        Intent intent = new Intent(mThis, openActivity);
        for(int i=0;i<key.length;i++){
            intent.putExtra(key[i], value[i]);
        }
        intent.putExtra(LoginResource.TRANSITION_TYPE,LoginResource.SOCIAL_TRANSITION);
        mThis.startActivity(intent);
        // mThis.overridePendingTransition(R.anim.right_to_left_anim, R.anim.exit);

    }

    public void loginActivityTransitionWithData(Activity mThis, Class openActivity, String[] key, Serializable[] value) {
        System.out.println("sssssssssaaapass");
        Intent intent = new Intent(mThis, openActivity);
        for(int i=0;i<key.length;i++){
            intent.putExtra(key[i], value[i]);

        }
        intent.putExtra(LoginResource.TRANSITION_TYPE,LoginResource.SIMPLE_TRANSITION_SLASH);
        mThis.startActivity(intent);
        // mThis.overridePendingTransition(R.anim.right_to_left_anim, R.anim.exit);

    }


    /**
     * open desired activity.
     * with single message(putExtra) transfer.
     */
    public void openActivityReorderToFront(Activity mThis, Class openActivity, String[] key, String[] value) {
        Intent intent = new Intent(mThis, openActivity);
        for(int i=0;i<key.length;i++){
            intent.putExtra(key[i], value[i]);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mThis.startActivity(intent);


    }


}
