package app.funcarddeals.com.Manager;

import android.app.Activity;
import android.content.Intent;

import app.funcarddeals.com.BeanClasses.BuyFuncardBeanClassForUserInfo;

/**
 * Created by Dx on 6/18/2015.
 * This class is used to switch activities.
 */
public class SwitchActivities {

    /**
     * open desired activity.
     */
    public void openActivity(Activity mThis, Class openActivity) {
        Intent intent = new Intent(mThis, openActivity);
        mThis.startActivity(intent);

    }
    /**
     * open desired activity.
     */
    public void
    openActivityByRemovingAll(Activity mThis, Class openActivity) {
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
    public void openActivity(Activity mThis, Class openActivity, String[] key, String[] value) {
        Intent intent = new Intent(mThis, openActivity);
        for(int i=0;i<key.length;i++){
            intent.putExtra(key[i], value[i]);
        }
        mThis.startActivity(intent);
        mThis.overridePendingTransition(0, 0);

    }

    /**
     * open desired activity.
     * with single message(putExtra) transfer.
     * here class object will be transferred as object.
     */


    public void openActivityWithPassBeanClassObject(Activity mThis, Class openActivity, String key, BuyFuncardBeanClassForUserInfo value) {
        Intent intent = new Intent(mThis, openActivity);
        intent.putExtra(key, value);
        mThis.startActivity(intent);
        mThis.overridePendingTransition(0, 0);

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
        mThis.overridePendingTransition(0,0);

    }


}
