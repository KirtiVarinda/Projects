package com.lovelife.lovelife.Lovelife_Services;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.lovelife.lovelife.SharedData.MySharedData;

/**
 * Created by dx on 12/9/2016.
 */
public class LoveLifeIdService extends FirebaseInstanceIdService {

    private static final String TAG = "com.lovelife.lovelife.FirebaseIDService";
    MySharedData mySharedData;
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();


    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     * <p/>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {

        mySharedData=new MySharedData(context);
        mySharedData.setGeneralSaveSession(MySharedData.FCM_REGISTRATIONID,token);

        System.out.println("registration id "+token);




    }
}
