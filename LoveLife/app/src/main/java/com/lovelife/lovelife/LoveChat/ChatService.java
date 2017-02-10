package com.lovelife.lovelife.LoveChat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.lovelife.lovelife.BeanClasses.BeanForChat;
import com.lovelife.lovelife.LoveLifeUtility.ManageNotifications;
import com.lovelife.lovelife.R;
import com.lovelife.lovelife.SharedData.MySharedData;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.android.ServerPingWithAlarmManager;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dx on 12/20/2016.
 */
public class ChatService extends Service implements ConnectionListener, ChatMessageListener, ChatManagerListener {

    /**
     * Chat service instance
     */

    public static ChatService chatServiceInstance;

    /**
     * internet check variable
     */
    public static boolean isInternetConnected;

    /***
     * Variable to create chat
     */
    public org.jivesoftware.smack.chat.Chat Mychat;


    /**
     * variable to handle database
     */
    ChatDBHandler handler;

    /**
     * chat message packet ID
     */
  /*  String messagePacketID;*/

    private static Timer tExit;
    private int logintime = 10000;


    private boolean chat_created = false;
    public static XMPPTCPConnection connection;
    private static final String DOMAIN = "112.196.23.228";
    //private static final String DOMAIN = "xmpp.jp";
    private static String USERNAME = "";
    private static String PASSWORD = "";
    public static ConnectivityManager cm;
    public static MyXMPP xmpp;
    public static boolean ServerchatCreated = false;
    String text = "";
    public String user1 = "", user2 = "";// chating with self
    Gson gson;
    public static boolean isChatFragmentInFront = false;
    private static ServerPingWithAlarmManager pingManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        /** check internet and update variable
         * next time it will be updated from connected changed receiver
         */

        isInternetConnected = InternetCheck.netCheck(getApplicationContext());

        MySharedData mySharedData = new MySharedData(this);

        /** Get User Email */
        String email = mySharedData.getGeneralSaveSession(mySharedData.USEREMAIL);

        /**  Split the Email For Openfire USER name  */
        String[] separated = email.split("@");
        USERNAME = separated[0];
        System.out.println("Userrqqqqqqqq"+USERNAME);
//        USERNAME = "lovelife"+mySharedData.getGeneralSaveSession(MySharedData.USERID);
        PASSWORD = USERNAME + this.getResources().getString(R.string.half_pass);

        /** initialize current service variable */
        chatServiceInstance = ChatService.this;

        /** initialize database handler */
        handler = new ChatDBHandler(getApplicationContext());


        user1 = USERNAME;

        /**  Get Partner Email id */
        String partnerEmail = mySharedData.getGeneralSaveSession(MySharedData.CONNECTED_PARNER_EMAIL);

        /**  Split the email To get Partner Username of openFire.
         *  */
        String[] separatePartnerEmail = partnerEmail.split("@");
        user2 = separatePartnerEmail[0];
        user2 = user1;
        System.out.println("Userrqqqqqqqq"+user2);


//        user2 =  "lovelife"+mySharedData.getGeneralSaveSession(MySharedData.CONNECTED_PARNER_ID);


        System.out.println("MyService USERs " + USERNAME);
        System.out.println("MyService PASSs " + PASSWORD);


        System.out.println("MyService user2s " + user2);

    }


    /**
     * initialize resources for making connection
     */
    private void initialiseConnection() {
      System.out.println("inisializeeeeee");
        gson = new Gson();
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration
                .builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        //config.setSocketFactory(SSLSocketFactory.getDefault());
        config.setServiceName(DOMAIN);
        config.setHost(DOMAIN);
        config.setPort(5222);
        // config.setSendPresence(true);
        config.setDebuggerEnabled(true);
        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
        connection = new XMPPTCPConnection(config.build());
        connection.setPacketReplyTimeout(10000);
        connection.addConnectionListener(this);
        new Thread() {
            public void run() {
                try {
                    System.out.println("connectcheck");
                    connection.connect();
                    System.out.println("connectcheck11");
                    /** pings server to check the connection is closed or not */

               /*     pingManager.setPingInterval(10000);
                    pingManager.registerPingFailedListener(ChatService.this);*/
                } catch (IOException e) {

                    Log.e("", "connectcheck22: " + e.getMessage());

                } catch (SmackException e) {

                    Log.e("", "connectcheck33: " + e.getMessage());

                } catch (XMPPException e) {

                    Log.e("connect", "connectcheck44: " + e.getMessage());

                }

            }
        }.start();


    }


    /**
     * login user in open fire server
     */
    public void login() {
        new Thread() {
            public void run() {

                System.out.println("xmpp login" + USERNAME + " " + PASSWORD);

                try {
                    SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
                    SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");

                    if (!connection.isConnected()) {
                        System.out.println("my connect");
                        initialiseConnection();
                        connection.connect();
                    }
                    Log.i("LOGIN", "Yey! We're connected to the");
                    System.out.println("xdddddddd"+USERNAME+" "+PASSWORD);
                    connection.login(USERNAME, PASSWORD);
                    Log.i("LOGIN", "Yey! We're connected to the Xmpp server!");

                } catch (XMPPException | SmackException | IOException e) {
                    System.out.println("myxmpp exception");
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("myxmpp exception all");
                }
            }
        }.start();

    }


    public static boolean isconnecting = false;

    /**
     * method to connect user with server
     */
    public void connect(final String caller) {
       System.out.println("connecttttttttttt");
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {

                /** connection already created
                 * but not login
                 */
                if (connection.isConnected()) {
                    System.out.println("connectAuthhhaaaaa");
                    if (!connection.isAuthenticated()) {
                        System.out.println("connectAuthhh");
                        login();
                    }
                    return false;
                }else{
                    System.out.println("no connectAuthhhaaaaazzzz");
                }


                isconnecting = true;


                try {
                    System.out.println("connectAuthhh1111qqqq");
                    connection.connect();
                    System.out.println("connectAuthhh1111222qqqq");
                    /** pings server to check the connection is closed or not */

               /*     pingManager.setPingInterval(10000);
                    pingManager.registerPingFailedListener(ChatService.this);*/
                } catch (IOException e) {

                    Log.e("(" + caller + ")", "IOException: " + e.getMessage());

                } catch (SmackException e) {

                    Log.e("(" + caller + ")", "SMACKException: " + e.getMessage());

                } catch (XMPPException e) {

                    Log.e("connect(" + caller + ")", "XMPPException: " + e.getMessage());

                }

                return isconnecting = false;
            }
        };
        connectionThread.execute();
    }


    /**
     * send message to client received from chat
     */
    public void sendMessage(ChatMessage chatMessage, BeanForChat beanForChat) {
        sendMessageToServer(chatMessage, chatMessage.msgid, beanForChat, false);
    }


    private void sendMessageToServer(ChatMessage chatMessage, String messageID, final BeanForChat beanForChat, boolean isResend) {
        String body = gson.toJson(chatMessage);
        System.out.println("dgdyfgdfyugyugfff"+body);


        if (!chat_created) {
            Mychat = ChatManager.getInstanceFor(connection).createChat(beanForChat.getReceiver() + "@" + getApplicationContext().getString(R.string.server), this);
            chat_created = true;
        }


        if (!isResend) {
            /** insert data in chat table if chat send successfully*/
            handler.insertChatDetail(beanForChat.getMsg_id(), beanForChat.getSender_name(), beanForChat.getSender(), beanForChat.getReceiver(), beanForChat.getChatDate()
                    , beanForChat.getChatTime(), beanForChat.getChatMsg(), String.valueOf(beanForChat.getIsmine()), "aaa", String.valueOf(beanForChat.getMiliSec()), beanForChat.getType());


            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    Chats.chatlist.add(beanForChat);
                    Chats.chatAdapter.notifyDataSetChanged();
                }
            });

        }


        final Message message = new Message();
        message.setBody(body);
        message.setStanzaId(messageID);
        message.setType(Message.Type.chat);
        System.out.println("send=1");
        try {
            if (connection.isAuthenticated()) {
                System.out.println("send=2 "+message.getBody());

                Mychat.sendMessage(message);
                System.out.println("checkMessageses"+message);


            } else {

                /** insert data in Resend chat table if chat not sent*/
                if (!handler.isResendMsgExist(beanForChat.getMsg_id())) {
                    handler.insertChatResend(beanForChat.getMsg_id(), beanForChat.getSender_name(), beanForChat.getSender(), beanForChat.getReceiver(), beanForChat.getChatDate()
                            , beanForChat.getChatTime(), beanForChat.getChatMsg(), String.valueOf(beanForChat.getIsmine()), "aaa", String.valueOf(beanForChat.getMiliSec()), beanForChat.getType());

                }
                System.out.println("send=3");
                login();
            }


        } catch (SmackException.NotConnectedException e) {
            Log.e("xmpp.SendMessage()", "msg Not sent!-Not Connected!");

        } catch (Exception e) {
            Log.e("xmpp.SendMessage()-Exception", "msg Not sent!" + e.getMessage());
        }
    }


    @Override
    public int onStartCommand(final Intent intent, final int flags,
                              final int startId) {
        initialiseConnection();
        System.out.println("onStartCommand...........");
        connect("ChatService");

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceTask = new Intent(getApplicationContext(), this.getClass());
        restartServiceTask.setPackage(getPackageName());

        PendingIntent restartPendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartPendingIntent);

        super.onTaskRemoved(rootIntent);
    }


    @Override
    public void connected(XMPPConnection xmppConnection) {
        if (!xmppConnection.isAuthenticated()) {

            System.out.println("connectAuthhhconnected");
            CommonMethods.registerUserOnOpenFire(USERNAME, PASSWORD);
            System.out.println("userConnecteddddd0"+USERNAME + "PASSWORD");
            login();


        }
    }

    @Override
    public void authenticated(XMPPConnection xmppConnection, boolean b) {
        chat_created = false;

        System.out.println("ssssssssaaa");
        ChatManager.getInstanceFor(connection).addChatListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);

                    /** get messages to resend and send again when device connected.*/
                    BeanForChat initialBean;
                    BeanForChat[] AllListData = handler.getAllChatResend();
                    ChatMessage chatMessage = null;
                    Random random = null;

                    for (int i = 0; i < AllListData.length; i++) {
                        random = new Random();
                        chatMessage = new ChatMessage(user1, user2, AllListData[i].getChatMsg(), "" + random.nextInt(1000), true);

                        initialBean = new BeanForChat();
                        initialBean.setChatMsg(AllListData[i].getChatMsg());
                        initialBean.setChatDate(AllListData[i].getChatDate());
                        initialBean.setChatTime(AllListData[i].getChatTime());
                        initialBean.setIsmine(AllListData[i].getIsmine());
                        initialBean.setMiliSec(AllListData[i].getMiliSec());
                        initialBean.setReceiver(AllListData[i].getReceiver());
                        initialBean.setSender(AllListData[i].getSender());
                        initialBean.setSender_name(AllListData[i].getSender_name());
                        initialBean.setType(AllListData[i].getType());
                        initialBean.setMsg_id(AllListData[i].getMsg_id());
                        initialBean.setId(AllListData[i].getId());
                        sendMessageToServer(chatMessage, AllListData[i].getMsg_id(), initialBean, true);

                        /** deleted resend row from database */
                        handler.delete(AllListData[i].getId());
                    }


                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();


        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                System.out.println("Fffffffffffff");
            }
        });
    }

    @Override
    public void connectionClosed() {

        System.out.println("myxmpp exception connectionClosed");
        chat_created = false;
        connection.disconnect();
//         Reconnect the server
        tExit = new Timer();
        tExit.schedule(new Timetask(), logintime);
    }

    @Override
    public void connectionClosedOnError(Exception e) {

        System.out.println("sevicesssssss");


        System.out.println("myxmpp exception connectionClosedOnError");

        /** reinitialize and re connect connection
         * if internet is not closed
         * */

        boolean error = e.getMessage().equals("stream:error (conflict)");
        if (!error) {
            // Close the connection
            connection.disconnect();

            initialiseConnection();
            connect("PingFailed");

            System.out.println("connectionClosedOnError");

//             Reconnect the server
            tExit = new Timer();
            tExit.schedule(new Timetask(), logintime, logintime);
        }

       /* if (isInternetConnected && !e.toString().contains("StreamErrorException")) {
            initialiseConnection();
            connect("connectionClosedOnError");
        }*/

        chat_created = false;
    }

    @Override
    public void reconnectionSuccessful() {
        System.out.println("myxmpp exception reconnectionSuccessful");
    }

    @Override
    public void reconnectingIn(int i) {

    }

    @Override
    public void reconnectionFailed(Exception e) {
        System.out.println("myxmpp exception reconnectionFailed");
        chat_created = false;
    }


    class Timetask extends TimerTask {
        @Override
        public void run() {

            System.out.println("timer task");
            if (!connection.isAuthenticated()) {
                initialiseConnection();
                connect("ChatService");
            } /*else {
                tExit.schedule(new Timetask(), logintime);
            }
*/
        }
    }


    @Override
    public void processMessage(Chat chat, Message message) {
        Log.i("ChatService_MESSAGE_LISTENER", "Xmpp message received: '"
                + message);

        if (message.getType() == Message.Type.chat
                && message.getBody() != null) {
            final ChatMessage chatMessage = gson.fromJson(
                    message.getBody(), ChatMessage.class);

            BeanForChat bean1;
            if (!isMessageRepeat(chatMessage.msgid)) {

                String boby = chatMessage.body;
                Boolean inMine = chatMessage.isMine;
                String msgID = chatMessage.msgid;
                String senderName = chatMessage.senderName;
                String chatDate = chatMessage.Date = CommonMethods.getCurrentDate();
                String chatTime = chatMessage.Time = CommonMethods.getCurrentTime();
                String chatSender = chatMessage.sender;
                String chatReceiver = chatMessage.receiver;
                String type = "receiver";
                long miliSec = Chats.milliseconds(chatDate, chatTime);
                bean1 = new BeanForChat();
                bean1.setChatMsg(boby);
                bean1.setChatDate(chatDate);
                bean1.setChatTime(chatTime);
                bean1.setIsmine(inMine);
                bean1.setMiliSec(miliSec + "");
                bean1.setReceiver(chatReceiver);
                bean1.setSender(chatSender);
                bean1.setSender_name(senderName);
                bean1.setType(type);
                bean1.setMsg_id(msgID);


                /**
                 * Insert chat data into sqlite database
                 *
                 */


                handler.insertChatDetail(msgID, senderName, chatSender, chatReceiver, chatDate, chatTime, boby, String.valueOf(inMine), "aaa", String.valueOf(miliSec), type);


                processMessage(bean1);

            } else {
                System.out.println("message already exist = " + chatMessage.body);
            }


        }


    }


    private void processMessage(final BeanForChat beanForChat) {
        /** if chat page open then set in list view
         * otherwise send notification
         * */
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {

                if (isChatFragmentInFront) {
                    Chats.chatlist.add(beanForChat);
                    Chats.chatAdapter.notifyDataSetChanged();
                } else {
                    //send notification
                    System.out.println("Send notification");
                    int notificationId = new Random().nextInt(1000);

                    /**    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context
                     .NOTIFICATION_SERVICE);
                     mNotificationManager.notify(notificationId, ManageNotifications.createNotification(
                     true,chatServiceInstance,notificationId,beanForChat.getChatDate()+" "+beanForChat.getChatTime(),beanForChat.getChatMsg()));


                     ToneManager.tone(getApplicationContext());*/

                    ManageNotifications.funcardNotify(getApplicationContext(), chatServiceInstance, notificationId, beanForChat.getChatDate() + " " + beanForChat.getChatTime(), beanForChat.getChatMsg());


                }

            }
        });
    }


    /**
     * method that checks for replicated messages.
     * i.e if openfire sends same message that is already received
     * then these messages will not be saved again
     *
     * @param msgID
     * @return
     */
    private boolean isMessageRepeat(String msgID) {
        boolean isMessageExist = false;

        BeanForChat receivedMessages[] = handler.getlastReceivedMessages();
        for (int i = 0; i < receivedMessages.length; i++) {
      /*      System.out.println("bulk message type= " + receivedMessages[i].getType());
            System.out.println("bulk message = " + receivedMessages[i].getChatMsg());
            System.out.println("=================================");*/

            if (receivedMessages[i].getMsg_id().equals(msgID)) {
                isMessageExist = true;
                break;
            }
        }
        return isMessageExist;
    }


    @Override
    public void chatCreated(Chat chat, boolean b) {
        if (!b)
            chat.addMessageListener(this);
    }

/*    @Override
    public void pingFailed() {
        *//** reinitialize and re connect connection
     * if internet is not closed
     * *//*

            initialiseConnection();
            connect("PingFailed");
        System.out.println("ping failed ");

        ManageNotifications.funcardNotify(getApplicationContext(), chatServiceInstance, 1, "ping failed run","Ping failed run");



    }*/
}

