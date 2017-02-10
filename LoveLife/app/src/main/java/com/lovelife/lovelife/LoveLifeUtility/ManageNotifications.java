package com.lovelife.lovelife.LoveLifeUtility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.lovelife.lovelife.Dashboard;
import com.lovelife.lovelife.R;


/**
 * Created by dx on 9/4/2015.
 */
public class ManageNotifications {


    public static void funcardNotify(Context context,Service activity, int randomID, String title, String message) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(activity)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message);
        // Creates an explicit intent for an Activity in your app
        mBuilder.setAutoCancel(true);
        Intent resultIntent = new Intent(activity, Dashboard.class);


        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        /**
         // The stack builder object will contain an artificial back stack for the
         // started Activity.
         // This ensures that navigating backward from the Activity leads out of
         // your application to the Home screen.
         */
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(Dashboard.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        randomID,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        resultPendingIntent.getActivity(activity, randomID, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(randomID, mBuilder.build());


        /**
         * notification with voice.
         */
        ToneManager.tone(context);

    }


    public static Notification createNotification(boolean makeHeadsUpNotification, Service activity, int randomID, String title, String message) {
        Notification.Builder notificationBuilder = new Notification.Builder(activity)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContentTitle(title)
                .setContentText(message);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            notificationBuilder.setCategory(Notification.CATEGORY_MESSAGE);
        }
        if (makeHeadsUpNotification) {
            Intent push = new Intent();
            push.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            push.setClass(activity, Dashboard.class);

            PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(activity, randomID,
                    push, PendingIntent.FLAG_CANCEL_CURRENT);
            notificationBuilder
                    .setContentText("Heads-Up Notification on Android L or above.")
                    .setFullScreenIntent(fullScreenPendingIntent, true);
        }
        return notificationBuilder.build();
    }

}
