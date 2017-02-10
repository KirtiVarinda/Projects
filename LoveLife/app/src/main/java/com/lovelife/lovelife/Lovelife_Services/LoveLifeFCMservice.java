package com.lovelife.lovelife.Lovelife_Services;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class LoveLifeFCMservice extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    public static final String INTENT_FILTER = "INTENT_FILTER";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        Intent intent = new Intent(INTENT_FILTER);
        intent.putExtra("message",remoteMessage.getNotification().getBody());
        sendBroadcast(intent);
    }
}