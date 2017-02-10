package com.lovelife.lovelife.LoveChat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.lovelife.lovelife.R;
import com.lovelife.lovelife.SharedData.MySharedData;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

public class MyService extends Service {
    public static XMPPTCPConnection connection;

    private static final String DOMAIN = "112.196.23.228";
    //private static final String DOMAIN = "xmpp.jp";
    private static String USERNAME = "";
    private static String PASSWORD = "";
    public static ConnectivityManager cm;
    public static MyXMPP xmpp;
    public static boolean ServerchatCreated = false;
    String text = "";


    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    public Chat chat;

    @Override
    public void onCreate() {
        super.onCreate();
        MySharedData mySharedData = new MySharedData(this);

        USERNAME = mySharedData.getGeneralSaveSession(MySharedData.openfireUser);
        PASSWORD = USERNAME + this.getResources().getString(R.string.half_pass);

        System.out.println("MyService USER " + USERNAME);
        System.out.println("MyService PASS " + PASSWORD);


        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        xmpp = MyXMPP.getInstance(MyService.this, DOMAIN, USERNAME, PASSWORD);
        xmpp.connect("onCreate");
    }


    @Override
    public int onStartCommand(final Intent intent, final int flags,
                              final int startId) {


        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

    }

/*    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //LoveLifeApplication.connection.disconnect();
    }

    public static boolean isNetworkConnected() {
        return cm.getActiveNetworkInfo() != null;
    }*/
}