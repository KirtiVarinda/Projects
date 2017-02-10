package com.lovelife.lovelife.Popups;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.lovelife.lovelife.LoginActivity;
import com.lovelife.lovelife.LoveChat.ChatService;
import com.lovelife.lovelife.LoveLifeUtility.SwitchActivities;
import com.lovelife.lovelife.R;
import com.lovelife.lovelife.SharedData.MySharedData;


/**
 * Created by dx on 7/20/2015.
 */
public class LoveLifePopups {
    public static SwitchActivities switchActivity;
    /**
     * AlertBox with customized Title and Message area.
     * only alert box will be closed on click of the ok button.
     */
    public static void loveLifePopup(final Activity mthis, final String titleText, final String messageText) {

        /**
         * Alert box should run on UIThread.
         */
        mthis.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mthis);
                builder.setCancelable(false);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(titleText);
                builder.setMessage(messageText);
                // builder.setCustomTitle(title);
                //builder.setView(message);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        dialog.cancel();

                    }
                });

                builder.create().show();
            }
        });


    }


    /**
     * AlertBox with customized Title and Message area.
     */
    public static void loveLifePopupWithFinishCurrentFavt(final Activity mthis, final String titleText, final String messageText) {
        switchActivity = new SwitchActivities();

        mthis.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mthis);
                builder.setCancelable(false);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(titleText);
                builder.setMessage(messageText);
                // builder.setCustomTitle(title);
                //builder.setView(message);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                builder.create().show();
            }
        });


    }


    public static void loveLifePopupForChangePass(final Activity mthis, final String titleText, final String messageText) {
        switchActivity = new SwitchActivities();

        mthis.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mthis);
                builder.setCancelable(false);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(titleText);
                builder.setMessage(messageText);
                // builder.setCustomTitle(title);
                //builder.setView(message);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        dialog.cancel();
                        mthis.finish();
                        mthis.overridePendingTransition(0, 0);
                        Intent i = new Intent(mthis, LoginActivity.class);
                        mthis.startActivity(i);
                    }
                });

                builder.create().show();
            }
        });


    }



    public static void loveLifePopupWithFinishCurrent(final Activity mthis, final String titleText, final String messageText) {
        switchActivity = new SwitchActivities();

        mthis.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mthis);
                builder.setCancelable(false);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(titleText);
                builder.setMessage(messageText);
                // builder.setCustomTitle(title);
                //builder.setView(message);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        dialog.cancel();
                        mthis.finish();
                        mthis.overridePendingTransition(0, 0);
                    }
                });

                builder.create().show();
            }
        });


    }

    /**
     * AlertBox with customized Title and Message area.
     * open next activity on click of ok button
     * and finish the current activity.
     */
    public static void loveLifePopupFinishCurrentActivity(final Activity mthis, final String titleText, final String messageText, final Class class1) {


        mthis.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mthis);
                builder.setCancelable(false);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(titleText);
                builder.setMessage(messageText);
                // builder.setCustomTitle(title);
                //builder.setView(message);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        dialog.cancel();
                        Intent intent = new Intent(mthis, class1);
                        mthis.startActivity(intent);
                        mthis.finish();

                    }
                });

                builder.create().show();
            }
        });


    }



    /**
     * AlertBox with customized Title and Message area.
     * open next activity on click of ok button
     * and finish the current activity.
     */
    public static void loveLifePopupFinishCurrentActivityAndLogout(final Activity mthis, final String titleText, final String messageText, final Class class1, final MySharedData mySharedData) {


        mthis.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mthis);
                builder.setCancelable(false);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(titleText);
                builder.setMessage(messageText);
                // builder.setCustomTitle(title);
                //builder.setView(message);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        dialog.cancel();

                        /** ougout from facebook Api also */
                        if (AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null){


                                    LoginManager.getInstance().logOut();


                        }else{

                            System.out.print("lovelifeopups:- already log out from facebook ");
                        }


                        /** stop chat service if logged out */

                        mthis.stopService(new Intent(mthis, ChatService.class));

                        /*if(MyService.connection.isConnected()){

                            MyService.connection.disconnect();

                        }*/


                        /** remove session for logout */
                        mySharedData.logoutSession(mySharedData);

                        Intent intent = new Intent(mthis, class1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        mthis.startActivity(intent);
                        mthis.finish();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        dialog.cancel();


                    }
                });

                builder.create().show();
            }
        });


    }


    /**
     * AlertBox with customized Title and Message area.
     * *open next activity on click of ok button
     */
    public static void loveLifePopup(final Activity mthis, final String titleText, final String messageText, final Class class1) {


        mthis.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mthis);
                builder.setCancelable(false);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(titleText);
                builder.setMessage(messageText);
                // builder.setCustomTitle(title);
                //builder.setView(message);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        dialog.cancel();
                        Intent intent = new Intent(mthis, class1);
                        mthis.startActivity(intent);


                    }
                });

                builder.create().show();
            }
        });


    }

}
