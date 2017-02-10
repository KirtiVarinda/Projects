package com.tiffindaddy.app.tiffindaddy.Popups;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.tiffindaddy.app.tiffindaddy.R;

/**
 * Created by dx on 7/20/2015.
 */
public class TiffinDaddyPopups {



    /**
     * AlertBox with customized Title and Message area.
     */
    public static void funcardPopup(final Activity mthis, final String titleText, final String messageText) {

        /**
         * Customized title text area and text for the Pop.
         */
   /*     TextView title = new TextView(mthis);
        title.setText(titleText);
        title.setBackgroundColor(Color.WHITE);
        title.setPadding(10, 10, 10, 10);
        // title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTypeface(title.getTypeface(), Typeface.BOLD);
        title.setTextSize(22);
*/

        /**
         * Customized message text area and text for the Pop.
         */
    /*    TextView message = new TextView(mthis);
        message.setText(messageText);
        message.setBackgroundColor(Color.WHITE);
        message.setPadding(20, 20, 20, 20);
        //message.setGravity(Gravity.CENTER);
        message.setTextColor(Color.GRAY);
        message.setTextSize(18);
*/
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
    public static void funcardPopup(final Activity mthis, final String titleText, final String messageText,final Activity activity) {


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
                        activity.finish();
                        activity.overridePendingTransition(0,0);
                    }
                });

                builder.create().show();
            }
        });


    }
    /**
     * AlertBox with customized Title and Message area.
     */
    public static void funcardPopup(final Activity mthis, final String titleText, final String messageText, final Class class1) {


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
                        Intent intent=new Intent(mthis,class1);
                        mthis.startActivity(intent);

                    }
                });

                builder.create().show();
            }
        });


    }


}
