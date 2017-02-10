package app.funcarddeals.com.Manager;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.Random;

import app.funcarddeals.com.BeanClasses.ReminderBeanClass;
import app.funcarddeals.com.FuncardService.FuncardService;
import app.funcarddeals.com.R;
import app.funcarddeals.com.StorePage;

/**
 * Created by dx on 9/4/2015.
 */
public class ManageNotifications {


    public static void funcardNotify(Context context, ReminderBeanClass reminder, Service activity, int randomID, String msg) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(activity)
                        .setSmallIcon(R.drawable.fun)
                        .setContentTitle("Fun Card Location Reminder")
                        .setContentText(msg);
        // Creates an explicit intent for an Activity in your app
        mBuilder.setAutoCancel(true);
        Intent resultIntent = new Intent(activity, StorePage.class);
        resultIntent.putExtra(FuncardService.STORE_ID, reminder.getREMINDER_STORE_ID());
        resultIntent.putExtra(FuncardService.STORE_NAME, reminder.getREMINDER_STORE_NAME());
        resultIntent.putExtra(FuncardService.STORE_LAT, reminder.getREMINDER_STORE_LATITUDE());
        resultIntent.putExtra(FuncardService.STORE_LNG, reminder.getREMINDER_STORE_LONGITUDE());


        // resultIntent.putExtra(FuncardService.FUNCARD_PRODUCT_ID, reminder.getREMINDER_STORE_PRODUCT_ID());
        // resultIntent.putExtra(FuncardService.FUNCARD_PRODUCT_NAME, reminder.getREMINDER_STORE_PRODUCT_NAME());
        //  resultIntent.putExtra(FuncardService.FUNCARD_PRODUCT_OFFERS, reminder.getREMINDER_STORE_PRODUCT_OFFER());


        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        /**
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        */
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(StorePage.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        randomID,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        resultPendingIntent.getActivity(activity, randomID, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(randomID, mBuilder.build());


        /**
         * notification with voice.
         */
        ToneManager.tone(context);

    }

}
