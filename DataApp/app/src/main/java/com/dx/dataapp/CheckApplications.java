package com.dx.dataapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;

import java.util.List;

/**
 * Created by dx on 19/03/2015.
 */
public class CheckApplications {


    public   void checkRunningApplications(Context ctx) {
        ActivityManager am1 = (ActivityManager) ctx.getSystemService(Activity.ACTIVITY_SERVICE);
        String packs=null;
        packs = getPackages(am1);
        PackageManager mPackageManager = ctx.getPackageManager();
        ApplicationInfo mApplicationInfo;
        try{
            mApplicationInfo = mPackageManager.getApplicationInfo(packs, 0);
        } catch (PackageManager.NameNotFoundException e) {
            mApplicationInfo = null;
        }
        String appName = (String) (mApplicationInfo != null ?
                mPackageManager.getApplicationLabel(mApplicationInfo) : "(unknown)");
        //   System.out.println("apkList: "+apkList);

        System.out.println("appName: "+appName);
        if(!appName.equals("DataApp") && !appName.toLowerCase().equals("camera")){
            //		showToast("cross first condition", ctx);
            if(isAppActive(ctx)  && !checkCallMode(ctx) ){
                try{
                    //				showToast("cross 2nd condition", ctx);
                    Intent i1=new Intent(ctx,DataActivity.class);
                    i1.putExtra("key_set_on_top", "yes");
                    i1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    i1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(i1);
                    i1=null;
                }catch(Exception e){

                    System.out.println("activity already finish");

                }

                mApplicationInfo=null;
                mPackageManager=null;
                packs=null;
                //	appName=null;
                //apkList=null;
                am1=null;
                ctx=null;
                appName=null;
            }
        }}
    public  boolean checkCallMode(Context ctx){
        AudioManager manager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
        if(manager.getMode()==AudioManager.MODE_IN_CALL || manager.getMode()==AudioManager.MODE_RINGTONE){
            // System.out.println("in call mode");
            //	cancelAlarms(LockHome.this);
            //    moveTaskToBack(true);
            //	showToast("In call mode",ctx);
            return true;
        }else{
            //   System.out.println("Not in call mode");
            return false;
        }
    }
    boolean isAppActive(Context ctx){
        SharedPreferences sp = ctx.getSharedPreferences("SessionData", ctx.MODE_PRIVATE);
        boolean status = sp.getBoolean("app_active", false);
        sp=null;
        return status;
    }
    String getPackages(ActivityManager am1) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            List<ActivityManager.RunningAppProcessInfo> processInfos = am1.getRunningAppProcesses();
			/*  for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
			    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
			      activePackages.addAll(Arrays.asList(processInfo.pkgList));
			    }
			  }*/
            String pack=processInfos.get(0).processName;
            processInfos=null;
            return pack;
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am1.getRunningTasks(1);
            ComponentName componentName = taskInfo.get(0).topActivity;
            String pack =null;
            pack = componentName.getPackageName();
            taskInfo=null;
            componentName=null;
            // String packageName = componentName.getPackageName();
            return pack;
        }

    }

}
