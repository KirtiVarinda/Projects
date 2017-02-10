package com.lovelife.lovelife.LoveChat;

import java.io.IOException;
import java.util.Random;

import org.jivesoftware.smack.roster.*; /*you may have been missing this*/
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.tcp.*;
import java.util.Collection;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.sm.StreamManagementException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager.AutoReceiptMode;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lovelife.lovelife.BeanClasses.BeanForChat;
import com.lovelife.lovelife.LoveLifeApplication;
import com.lovelife.lovelife.R;
import com.lovelife.lovelife.ReadReceipt;
import com.lovelife.lovelife.SharedData.MySharedData;


public class MyXMPP {
    public static boolean connected = false;
    public boolean loggedin = false;
    public static boolean isconnecting = false;
    public static boolean isToasted = true;
    private boolean chat_created = false;
    private String serverAddress;
    String messagePacketID;
    public static String loginUser;
    public static String passwordUser;
    Gson gson;
    MyService context;
    public static MyXMPP instance = null;
    public static boolean instanceCreated = false;
    ChatDBHandler handler;
    public static BeanForChat bean1;
    MySharedData mySharedData;
    public String user1 = "", user2 = "";// chating with self

    public MyXMPP(MyService context, String serverAddress, String loginUser,
                  String passwordUser) {

        this.serverAddress = serverAddress;
        this.loginUser = loginUser;
        this.passwordUser = passwordUser;
        this.context = context;
        initialiseConnection();
        mySharedData = new MySharedData(context);
        handler = new ChatDBHandler(context);
        user1 = mySharedData.getGeneralSaveSession(MySharedData.openfireUser);
        String[] splitUser2 = mySharedData.getGeneralSaveSession(MySharedData.CONNECTED_PARNER_EMAIL).split("@");
        user2 = splitUser2[0];
        ProviderManager.addExtensionProvider(ReadReceipt.ELEMENT, ReadReceipt.NAMESPACE, new ReadReceipt.Provider());
    }

    public static MyXMPP getInstance(MyService context, String server,
                                     String user, String pass) {

        if (instance == null) {
            instance = new MyXMPP(context, server, user, pass);
            instanceCreated = true;
        }
        return instance;

    }


    public org.jivesoftware.smack.chat.Chat Mychat;

    public static ChatManagerListenerImpl mChatManagerListener;
    public static MMessageListener mMessageListener;


    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException ex) {
            // problem loading reconnection manager
        }
    }


    private void initialiseConnection() {
        gson = new Gson();
        mMessageListener = new MMessageListener(context);
        mChatManagerListener = new ChatManagerListenerImpl();
        System.out.println("initializeeeeeeeeeeee");
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration
                .builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        //config.setSocketFactory(SSLSocketFactory.getDefault());
        config.setServiceName(serverAddress);
        config.setHost(serverAddress);
        config.setPort(5222);
        // config.setSendPresence(true);
        config.setDebuggerEnabled(true);
        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
        MyService.connection = new XMPPTCPConnection(config.build());
        //LoveLifeApplication.connection.setPacketReplyTimeout(10000);
        XMPPConnectionListener connectionListener = new XMPPConnectionListener();
        MyService.connection.addConnectionListener(connectionListener);
    }

    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyService.connection.disconnect();
                initialiseConnection();
            }
        }).start();
    }


    /**
     * method to connect user with server
     */
    public void connect(final String caller) {

        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {
                if (MyService.connection.isConnected())
                    return false;
                isconnecting = true;
                if (isToasted)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(context, caller + "=>connecting....", Toast.LENGTH_LONG).show();

                        }
                    });


                try {

                    MyService.connection.connect();


                    DeliveryReceiptManager dm = DeliveryReceiptManager.getInstanceFor(MyService.connection);
                    dm.setAutoReceiptMode(AutoReceiptMode.always);

                    dm.addReceiptReceivedListener(new ReceiptReceivedListener() {
                        @Override
                        public void onReceiptReceived(final String fromid,
                                                      final String toid, final String msgid,
                                                      final Stanza packet) {
                            System.out.println("packet packet");
                        }
                    });
                    connected = true;

                } catch (IOException e) {


                    if (isToasted)
                        new Handler(Looper.getMainLooper())
                                .post(new Runnable() {
                                    @Override
                                    public void run() {

                                        Toast.makeText(context, "(" + caller + ")" + "IOException: ", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    Log.e("(" + caller + ")", "IOException: " + e.getMessage());

                } catch (SmackException e) {


                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(context, "(" + caller + ")" + "SMACKException: ", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.e("(" + caller + ")", "SMACKException: " + e.getMessage());

                } catch (XMPPException e) {


                    if (isToasted)

                        new Handler(Looper.getMainLooper())
                                .post(new Runnable() {

                                    @Override
                                    public void run() {

                                        Toast.makeText(context, "(" + caller + ")" + "XMPPException: ", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    Log.e("connect(" + caller + ")", "XMPPException: " + e.getMessage());

                }

                return isconnecting = false;
            }
        };
        connectionThread.execute();
    }


    /**
     * login user in open fire server
     */
    public void login() {
        new Thread() {
            public void run() {

                System.out.println("xmpp login" + loginUser + " " + passwordUser);

                try {
                    SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
                    SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");

                    if (!MyService.connection.isConnected()) {
                        System.out.println("my connect");
                        initialiseConnection();
                        MyService.connection.connect();
                    }

                    MyService.connection.login(loginUser, passwordUser);
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


    private class ChatManagerListenerImpl implements ChatManagerListener {
        @Override
        public void chatCreated(final org.jivesoftware.smack.chat.Chat chat,
                                final boolean createdLocally) {
            if (!createdLocally)
                chat.addMessageListener(mMessageListener);
        }
    }


    /**
     * send message to client
     */
    public void sendMessage(ChatMessage chatMessage, BeanForChat beanForChat) {

        if (!MyService.connection.isConnected()) {
            try {
                initialiseConnection();
                MyService.connection.connect();

            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }


        sendMessageToServer(chatMessage, chatMessage.msgid, beanForChat, false);

    }


    private boolean isPartnerConnected() {
        boolean isConnected=false;
        OfflineMessageManager offlineMessageManager = new OfflineMessageManager(MyService.connection);//This is the method get the offline message
        try {
            System.out.println("aasswew" + offlineMessageManager.getMessageCount());

            if(offlineMessageManager.getMessageCount() != 0 ){
                isConnected=true;
            }else{

            }


        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }

        return isConnected;
    }


    private void sendMessageToServer(ChatMessage chatMessage, String messageID, final BeanForChat beanForChat, boolean isResend) {
        String body = gson.toJson(chatMessage);





        if (!chat_created) {
            Mychat = ChatManager.getInstanceFor(MyService.connection).createChat(beanForChat.getReceiver() + "@" + context.getString(R.string.server), mMessageListener);
            chat_created = true;
        }

  /*      Roster roster = Roster.getInstanceFor(LoveLifeApplication.connection);
        Presence availability = roster.getPresence(user2);
        System.out.println("TTTTTTTTTTTTT = "+availability.getStatus());*/

        Roster roster = Roster.getInstanceFor(MyService.connection);

        if (!roster.isLoaded()) {
            try {
                roster.reloadAndWait();
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (NotConnectedException e) {
                e.printStackTrace();
            }
        }
        Collection <RosterEntry> entries = roster.getEntries();

        for (RosterEntry entry : entries)
            System.out.println("TTTTTTTTTdfTTTT: " + entry.getStatus());





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
            if (MyService.connection.isAuthenticated()) {
                System.out.println("send=2");

                Mychat.sendMessage(message);
                isPartnerConnected();

            } else {

                /** insert data in Resend chat table if chat not sent*/
                if (!handler.isResendMsgExist(beanForChat.getMsg_id())) {
                    handler.insertChatResend(beanForChat.getMsg_id(), beanForChat.getSender_name(), beanForChat.getSender(), beanForChat.getReceiver(), beanForChat.getChatDate()
                            , beanForChat.getChatTime(), beanForChat.getChatMsg(), String.valueOf(beanForChat.getIsmine()), "aaa", String.valueOf(beanForChat.getMiliSec()), beanForChat.getType());

                }
                System.out.println("send=3");
                login();
            }


            //   ........kirti.........


            /**
             * Get Delivery Receipt after deliver Message on Receiver Side.
             */
            DeliveryReceiptManager.getInstanceFor(MyService.connection).autoAddDeliveryReceiptRequests();
            DeliveryReceiptManager.getInstanceFor(MyService.connection).addReceiptReceivedListener(new ReceiptReceivedListener() {
                @Override
                public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
                    Log.v("appdsssss", fromJid + ",toJid " + toJid + ", receiptId" + receiptId + ",receipt " + receipt);

                }

            });


            /**
             * Get packet id after send Message.
             */
            if (MyService.connection.isSmEnabled()) {
                try {
                    MyService.connection.addStanzaIdAcknowledgedListener(message.getStanzaId(), new StanzaListener() {
                        @Override
                        public void processPacket(Stanza packet) throws NotConnectedException {
                            System.out.println("packetttts" + packet.getStanzaId() + " " + packet);
                            messagePacketID = packet.getStanzaId();
                            ReadReceipt read = new ReadReceipt(messagePacketID);
                            message.addExtension(read);

                        }
                    });
                } catch (StreamManagementException.StreamManagementNotEnabledException e) {
                    e.printStackTrace();
                }
            }
            MyService.connection.sendStanza(message);

            ReceiptReceivedListener readListener = new ReceiptReceivedListener() {
                @Override
                public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
                    Log.i("Read", "Message Read Successfully");
                }

            };

            //  ReadReceiptManager.getInstanceFor(connection).addReadReceivedListener(readListener);


        } catch (NotConnectedException e) {
            Log.e("xmpp.SendMessage()", "msg Not sent!-Not Connected!");

        } catch (Exception e) {
            Log.e("xmpp.SendMessage()-Exception", "msg Not sent!" + e.getMessage());
        }
    }


    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {
            Log.d("xmpp", "Connected!");
            connected = true;

            System.out.println("ddeeeeeee" + loginUser + passwordUser);

            if (!connection.isAuthenticated()) {
//                CommonMethods.registerUserOnOpenFire(loginUser, passwordUser);
                login();
            }


            //  ....... kirti ......

            Roster roster = Roster.getInstanceFor(connection);
            Presence availability = roster.getPresence(user2);
            Presence.Mode userMode = availability.getMode();
            retrieveState_mode(availability.getMode(), availability.isAvailable());
            System.out.println("xxxxxxxxxxx " + userMode);
        }


        //  ....... kirti ......

        /**
         * retrive the state of present User.
         */

        public int retrieveState_mode(Presence.Mode userMode, boolean isOnline) {
            int userState = 0;
            /** 0 for offline, 1 for online, 2 for away,3 for busy*/
            if (userMode == Presence.Mode.dnd) {
                userState = 3;
            } else if (userMode == Presence.Mode.away || userMode == Presence.Mode.xa) {
                userState = 2;
            } else if (isOnline) {
                userState = 1;
            }

            if (isOnline) {
                System.out.println("User333 online");
            } else {
                System.out.println("User333 offline");
            }
            System.out.println("swswswsw" + userState + " " + userMode);
            return userState;
        }


        @Override
        public void connectionClosed() {
            if (isToasted)
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Toast.makeText(context, "ConnectionCLosed!", Toast.LENGTH_SHORT).show();

                    }
                });

            disconnect();

            Log.d("xmpp", "ConnectionCLosed!");
            // disconnect();
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(context, "ConnectionClosedOn Error!!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ConnectionClosedOn Err   " + arg0);

            /** disconnect connection and initialize resource again */
            disconnect();

            if (!arg0.toString().contains("StreamErrorException")) {


                try {
                    // initialiseConnection();
                    MyService.connection.connect();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }

            connected = false;
            chat_created = false;
            loggedin = false;
        }


        @Override
        public void reconnectingIn(int arg0) {

       /*     Log.d("xmpp", "Reconnectingin " + arg0);
            try {
                LoveLifeApplication.connection.connect();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            }*/
            loggedin = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(context, "ReconnectionFailed!", Toast.LENGTH_SHORT).show();
                    }
                });

            disconnect();

            Log.d("xmpp", "ReconnectionFailed!");
            connected = false;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectionSuccessful() {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Toast.makeText(context, "REConnected!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ReconnectionSuccessful");
            connected = true;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.d("xmpp", "Authenticated!");
            loggedin = true;

            ChatManager.getInstanceFor(MyService.connection).addChatListener(
                    mChatManagerListener);

            chat_created = false;


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
                            chatMessage = new ChatMessage(user1, user2,
                                    AllListData[i].getChatMsg(), "" + random.nextInt(1000), true);

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
                            handler.delete(AllListData[i].getId());
                        }


                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();


            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Toast.makeText(context, "Connected!", Toast.LENGTH_SHORT).show();
                        System.out.println("Fffffffffffff");

                    }
                });
        }
    }

    private class MMessageListener implements ChatMessageListener {

        public MMessageListener(Context contxt) {
        }

        @Override
        public void processMessage(final org.jivesoftware.smack.chat.Chat chat,
                                   final Message message) {
            Log.i("MyXMPP_MESSAGE_LISTENER", "Xmpp message received: '"
                    + message);
//            bean.setImageType("receiver");

            if (message.getType() == Message.Type.chat
                    && message.getBody() != null) {
                final ChatMessage chatMessage = gson.fromJson(
                        message.getBody(), ChatMessage.class);


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

            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    Chats.chatlist.add(beanForChat);
                    Chats.chatAdapter.notifyDataSetChanged();
                }
            });
        }
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


}