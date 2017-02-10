package app.funcarddeals.com.Popups;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;

import app.funcarddeals.com.FuncardDatabase.FunCardDealsDatabase;
import app.funcarddeals.com.FuncardDatabase.FuncardDealsContract;
import app.funcarddeals.com.FuncardFavourites;
import app.funcarddeals.com.FuncardReminders;
import app.funcarddeals.com.FuncardService.FuncardService;
import app.funcarddeals.com.MainPage;
import app.funcarddeals.com.Manager.MenuManager;
import app.funcarddeals.com.Manager.SwitchActivities;
import app.funcarddeals.com.R;
import app.funcarddeals.com.Registration;

/**
 * Created by dx on 7/20/2015.
 */
public class FuncardPopups {
    /**
     * general class reference for change activity.
     */
    private static SwitchActivities switchActivity;


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
    public static void funcardPopupWithFinishCurrentActivityOnOK(final Activity mthis, final String titleText, final String messageText) {


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

                        mthis.finish();
                        dialog.cancel();

                    }
                });

                builder.create().show();
            }
        });


    }


    /**
     * AlertBox for logout functionality.
     */
    public static void funcardPopupForDeleteReminderOrFavourites(final Activity mthis, final String titleText, final String messageText, final FunCardDealsDatabase funDB, final String productID, final String type) {
        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();


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
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (type.equals("rem")) {
                            funDB.deleteReminder(productID);
                            FuncardReminders.setListView();

                        } else if (type.equals("fav")) {
                            funDB.deleteFavourite(productID);
                            FuncardFavourites.setListView();
                        }

                        dialog.cancel();


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });


    }


    /**
     * AlertBox that switch to another activity when clicked ok.
     */
    public static void funcardPopupForSwitchActivity(final Activity mthis, final String titleText, final String messageText, final Class switchToactivity) {
        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();


        /**
         * Alert box should run on UIThread.
         */
        mthis.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mthis);
                //builder.setCancelable(false);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(titleText);
                builder.setMessage(messageText);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        switchActivity.openActivity(mthis, switchToactivity);
                        dialog.cancel();
                        mthis.finish();

                    }
                });

                builder.create().show();
            }
        });


    }


    /**
     * AlertBox for logout functionality.
     */
    public static void funcardPopupForLogout(final MenuManager mathis, final Activity mthis, final Class switchToactivity, final String titleText, final String messageText) {
        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();


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
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        mathis.removeSessionAndGoToLoginPage(mthis, switchToactivity);

                        /**
                         * stop service when application is logged out
                         *
                         */
                        mthis.stopService(new Intent(mthis, FuncardService.class));


                        dialog.cancel();


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });


    }


    /**
     * AlertBox for logout functionality.
     */
    public static void funcardPopupForLogoutForSomeProblem(final MenuManager mathis, final Activity mthis, final Class switchToactivity, final String titleText, final String messageText) {
        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();


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
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        mathis.removeSessionAndGoToLoginPage(mthis, switchToactivity);

                        dialog.cancel();


                    }
                });

                builder.create().show();
            }
        });


    }


    /**
     * AlertBox with two functionalities.
     * one is for open another activity
     */
    public static void funcardPopupWithTwoActions(final Activity FromActivity, final Class switchToactivity, final String titleText, final String messageText, final String firstOption, final String secOption, final String[] key, final String[] value) {
        /**
         * Initialize activity switcher.
         */
        switchActivity = new SwitchActivities();


        /**
         * Alert box should run on UIThread.
         */
        FromActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(FromActivity);
                builder.setCancelable(false);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(titleText);
                builder.setMessage(messageText);
                builder.setPositiveButton(firstOption, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switchActivity.openActivity(FromActivity, switchToactivity, key, value);
                        dialog.cancel();


                    }
                });

                builder.setNegativeButton(secOption, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });


    }

}
