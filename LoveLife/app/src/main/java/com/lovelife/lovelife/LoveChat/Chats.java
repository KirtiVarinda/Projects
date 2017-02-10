package com.lovelife.lovelife.LoveChat;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.lovelife.lovelife.BeanClasses.BeanForChat;
import com.lovelife.lovelife.LoveLifeUtility.PairValues;
import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.R;
import com.lovelife.lovelife.SharedData.MySharedData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojiconize.Emojiconize;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class Chats extends Fragment implements OnClickListener {
    PopupWindow popupWindow;
    /**
     * variables for smily and keyboard imageButton
     */
    ImageButton smilyButton,keyboardButton;
    public static int KeyboardHeight;
//    public static ViewGroup.LayoutParams params;
    ArrayList<BeanForChat> chatArrayList;
    public static EmojiconEditText msg_edittext;
    private String user1 = "", user2 = "";// chating with self
    private Random random;
    public static ArrayList<BeanForChat> chatlist;
    public static ChatAdapter chatAdapter;
    ListView msgListView;
    String chatDate, chatMsgBody, chatTime, chatSender, chatReceiver, chatMsgID, senderName, imagepath;
    long miliSec;
    String type;
    Boolean chatIsMine;
    public static BeanForChat initialBean;

    private static String KEY_ID = "id";
    private static String KEY_TOKEN = "token";
    public static String KEY_TYPE = "transaction_type";
    public static String KEY_POINTS = "points";
    String userID, token, points;
    public static String transactionType = "chat";


    FragmentTransaction transaction;
    public static LinearLayout linearLayout;
    InputMethodManager inputMethodManager;
    static boolean isSmily = true;

    /**
     * Register user on Url
     */
    private String CHAT_POINTS_URL;

    /**
     * session variable
     */
    MySharedData mySharedData;


    /**
     * Variable for database class.
     */
    ChatDBHandler handler;

    BeanForChat beanForChat;
    private BeanForChat[] AllListData;

    /**
     * Method that create key value pair for sending dta in post
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMap(String id, String token, String transaction_type, String points) {

        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
        String key[] = {KEY_ID, KEY_TOKEN, KEY_TYPE, KEY_POINTS};      // set keys
        String value[] = {id, token, transaction_type, points};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chat_layout, container, false);
        System.out.println("chattttssss");
        Emojiconize.view(view).go();

        inputMethodManager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        /**
         * Inisialize fragments for Smily Faces.
         */
        linearLayout = (LinearLayout) view.findViewById(R.id.emojicon);
//        Chats.params = linearLayout.getLayoutParams();
        /**
         * inisialize the Parent Linear layout
         */
        LinearLayout chatLayout = (LinearLayout) view.findViewById(R.id.chatlayout);
        /**
         * show soft Keyboard
         */



        /** inisialize imagebutton variables for smilys, keyboard and for send data*/

        smilyButton = (ImageButton) view.findViewById(R.id.SmilyButton);
        keyboardButton = (ImageButton) view.findViewById(R.id.keyboard);



        /** onclick listener to send Smilies  */
        smilyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSmily();
            }
        });
        keyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openKeyboard();
            }
        });


        /**
         * set Global Layout Listener for getting the Keyboard Height on Parent Layout
         */
        chatLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub

                System.out.println("csgfduyc");
                Rect r = new Rect();
                View rootview = getActivity().getWindow().getDecorView(); // this = activity
                rootview.getWindowVisibleDisplayFrame(r);


                int screenHeight = rootview.getRootView().getHeight();
                Chats.KeyboardHeight = screenHeight - (r.bottom - r.top);
                Log.d("Keyboard Size", "Size: " + Chats.KeyboardHeight + "screen" + screenHeight);
//                Chats.params.height = (int) Chats.KeyboardHeight;
                //boolean visible = heightDiff > screenHeight / 3;
//                System.out.println("sdsdsdsdsdsdsdsdsdsd"+Chats.params.height+" "+Chats.KeyboardHeight);

                if (Chats.KeyboardHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened

                    System.out.println("keyboard Opemn");
                } else {
                    System.out.println("keyboard Close");
                    // keyboard is closed
                }


            }
        });


        /**
         * Inisialize chat arraylist
         */

        chatArrayList = new ArrayList<BeanForChat>();

//        doBindService();


        random = new Random();

        /**Inisialize Database Class */

        handler = new ChatDBHandler(getContext());

        /**
         * set status bar on focus on edit text
         */
        msg_edittext = (EmojiconEditText) view.findViewById(R.id.editEmojicon);
        msg_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
               /*     inputMethodManager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*/

                } else {

                }
            }
        });
        /**  get user email and partner email split with "@" */
        MySharedData mySharedData = new MySharedData(getActivity());
        System.out.println("user nn ");



        /** Get User Email */
        String email = mySharedData.getGeneralSaveSession(mySharedData.USEREMAIL);

        /**  Split the Email For Openfire USER name  */
        String[] separated = email.split("@");
        user1 = separated[0];
        System.out.println("Userrqqqqqqqq"+user1);


//        user1 = "lovelife" + mySharedData.getGeneralSaveSession(MySharedData.USERID);

        /**
         * get user id and token from session.
         */
        userID = mySharedData.getGeneralSaveSession(MySharedData.USERID);
        token = mySharedData.getGeneralSaveSession(MySharedData.TOKEN);


        /**  Get Partner Email id */
        String partnerEmail = mySharedData.getGeneralSaveSession(MySharedData.CONNECTED_PARNER_EMAIL);

        /**  Split the email To get Partner Username of openFire.
         *  */
        String[] separatePartnerEmail = partnerEmail.split("@");
        user2 = separatePartnerEmail[0];
        user2 = user1;
        System.out.println("Userrqqqqqqqq"+user2);
//        user2 = "lovelife" + mySharedData.getGeneralSaveSession(MySharedData.CONNECTED_PARNER_ID);


        System.out.println("Chats USER 1 " + user1);
        System.out.println("Chats USER 2 " + user2);


        /** set url for user registration */
        CHAT_POINTS_URL = getResources().getString(R.string.serverDomain) + getResources().getString(R.string.points);

        /** initialize session variable */


        msgListView = (ListView) view.findViewById(R.id.msgListView);
        ImageButton sendButton = (ImageButton) view
                .findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(this);

        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);


        /** start service for xmpp messages */
        System.out.println("ChattStart");
        Toast.makeText(getActivity(), "chatstart", Toast.LENGTH_LONG).show();
        getActivity().startService(new Intent(getActivity(), ChatService.class));


        /**  onClick Listener For send data */
        sendButton.setOnClickListener(this);

        return view;

    }

    private void openSmily() {

//            isSmily = false;
        /** show Smilies Fragment  */

        linearLayout.setVisibility(View.VISIBLE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);


    }
    public void openKeyboard(){
        linearLayout.setVisibility(View.GONE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

//        linearLayout.setVisibility(View.GONE);
    }

    public static void onBackPressed() {
        linearLayout.setVisibility(View.GONE);


        //Pop Fragments off backstack and do your other checks
      /*  if (!isSmily) {
            System.out.println("grfgggg");
            linearLayout.setVisibility(View.GONE);
        } else {
            System.out.println("fvjdvh");
        }*/
    }

    /**
     * get messages from the database and set in listview
     */
    private void setList() {

        chatlist = new ArrayList<BeanForChat>();
        AllListData = handler.getAllChat();
        for (int i = 0; i < AllListData.length; i++) {
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
            chatlist.add(initialBean);
        }

        chatAdapter = new ChatAdapter(getActivity(), chatlist);
        msgListView.setAdapter(chatAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    public void sendTextMessage(View v) {
        ChatMessage chatMessage = null;
        beanForChat = new BeanForChat();

        String message = null;
//        imagePath = bean.getImagePath();

        if (msg_edittext != null) {
            message = msg_edittext.getEditableText().toString();
        }
        if (!message.equalsIgnoreCase("")) {
            chatMessage = new ChatMessage(user1, user2,
                    message, "" + random.nextInt(1000), true);
            chatMessage.setMsgID();
            /**
             * Put the Chat Info in Perticuler string to pass it on local Database.
             */

            chatMsgBody = chatMessage.body = message;
            chatDate = chatMessage.Date = CommonMethods.getCurrentDate();
            System.out.println("dfjuigh" + chatDate);
            chatTime = chatMessage.Time = CommonMethods.getCurrentTime();
            chatSender = chatMessage.sender;
            chatReceiver = chatMessage.receiver;
            chatIsMine = chatMessage.isMine;
            chatMsgID = chatMessage.msgid;
            senderName = chatMessage.senderName;
            type = "sender";
            miliSec = milliseconds(chatDate, chatTime);
            msg_edittext.setText("");

            beanForChat.setChatMsg(chatMsgBody);
            beanForChat.setChatDate(chatDate);
            beanForChat.setChatTime(chatTime);
            beanForChat.setIsmine(chatIsMine);
            beanForChat.setMiliSec(miliSec + "");
            beanForChat.setReceiver(chatReceiver);
            beanForChat.setSender(chatSender);
            beanForChat.setSender_name(senderName);
            beanForChat.setType(type);
            beanForChat.setMsg_id(chatMsgID);
            System.out.println("fhvuyfvhdd" + miliSec);


            /**
             * Insert chat data into sqlite database
             *
             */
            /** handler.insertChatDetail(chatMsgID, senderName, chatSender, chatReceiver, chatDate, chatTime, chatMsgBody, String.valueOf(chatIsMine), "aaa", String.valueOf(miliSec), type);
             */

            ChatService.chatServiceInstance.sendMessage(chatMessage, beanForChat);

//            int numberOfMessage = chatArrayList.size();


        }
    }


    /**
     * add points of user on server for lovemeter
     *
     * @param postData contains the post data that would be send to server.
     */

    public void addPoints(Map<String, String> postData) {

        ServerSync serverSync = new ServerSync();

        String reponse = serverSync.SyncServer(postData, CHAT_POINTS_URL);

        System.out.println("responsesss " + reponse);
    }


    /**
     * Check If the Internet is On or Off
     */
      /*  System.out.println("ofjsdf fv" + mySharedData.getGeneralSaveSession(MySharedData.CHECKNET));
        if (mySharedData.getGeneralSaveSession(MySharedData.CHECKNET).equals("true")) {
            *//** If internet is on*//*


        } else {
            */

    /**
     * If Internet Is off.
     *//*
            Toast.makeText(getContext(), "Network Problem", Toast.LENGTH_LONG).show();

        }*/
    public static long milliseconds(String dates, String time) {
        String toParse = dates + " " + time; // Results in "2-5-2012 20:43"
        SimpleDateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm"); // I assume d-M, you may refer to M-d for month-day instead.
        Date date = null; // You will need try/catch around this
        try {
            date = formatter.parse(toParse);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();
        System.out.println("millis" + millis);
        return millis;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMessageButton:
                sendTextMessage(v);

        }
    }


  /*  @Override
    public void onResume() {
        super.onResume();

        new Thread(){
            public void run(){

                if(!MyXMPP.connection.isConnected()){
                    try {
                        MyXMPP.connection.connect();
                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();

    }*/

  /*  @Override
    public void onPause() {
        super.onPause();
        doUnbindService();
    }*/

  /*  @Override
    public void onResume() {
        super.onResume();
        */

    /**
     * bind service for chating
     *//*
        doBindService();

   /* }
*/
 /*   @Override
    public void onDestroy() {
        super.onDestroy();
         doUnbindService();
    }*/

/*    void doBindService() {
        getActivity().bindService(new Intent(getActivity(), MyService.class), mConnection,
                Context.BIND_AUTO_CREATE);
        System.out.println("bindddd");
    }*/

  /*  void doUnbindService() {
        if (mConnection != null) {
            getActivity().unbindService(mConnection);
        }
    }
*/
 /*   public MyService getmService() {
        return mService;
    }
*/
    @Override
    public void onResume() {
        super.onResume();
        /*--Start Service--*/
        setList();
        Toast.makeText(getActivity(), "onResumeeeee", Toast.LENGTH_LONG).show();
        ChatService.isChatFragmentInFront = true;

    }

    @Override
    public void onPause() {
        super.onPause();
        ChatService.isChatFragmentInFront = false;
    }
}