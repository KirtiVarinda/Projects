package com.dx.dataapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by getfixr on 5/8/2015.
 */
public class LauncherClass {
    Context context;
    public LauncherClass(Context context){
        this.context=context;
    }

    public void makePrefered(String state,String mode) {
        PackageManager p = context.getPackageManager();
        ComponentName cN = null;
        if(mode.equals("1")){
            cN = new ComponentName(context, DataActivity.class);
        }else{
        }

        if("enable".equals(state)){
            p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }else if("disable".equals(state)){
            p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }


    }
}
