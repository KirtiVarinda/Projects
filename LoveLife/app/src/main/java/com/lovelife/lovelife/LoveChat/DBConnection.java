package com.lovelife.lovelife.LoveChat;

import android.provider.BaseColumns;

/**
 * Created by Kirti-PC on 11/9/2016.
 */
public class DBConnection {

    public DBConnection(){

    }


    /**
     * Connection for chat Message details
     */
    public static abstract class ChatDetail implements BaseColumns {

        public static final String CHAT_TABLE_NAME = "chatDetail";
        public static final String CHAT_TABLE_RESEND = "chatDetail_resend";
        public static final String KEY_ID = "id";
        public static final String CHAT_MSG_ID = "msgid";
        public static final String CHAT_SENDER_NAME = "sendername";
        public static final String CHAT_SENDER = "sender";
        public static final String CHATRECEIVER = "receiver";
        public static final String CHAT_DATE = "date";
        public static final String CHAT_TIME = "time";
        public static final String CHAT_BODY = "body";
        public static final String CHAT_INMINE = "inmine";
        public static final String CHAT_MSG_SEEN = "msgseen";
        public static final String CHAT_MILISEC = "milisec";
        public static final String CHAT_TYPE = "type";

        /**
         * Query To Create Chat Message Table
         */
        protected static final String CREATE_CHAT_TABLE = "CREATE TABLE " + CHAT_TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + CHAT_MSG_ID + " TEXT,"
                + CHAT_SENDER_NAME + " TEXT," + CHAT_SENDER + " TEXT," + CHATRECEIVER + " TEXT,"
                + CHAT_DATE + " TEXT," + CHAT_TIME + " TEXT," + CHAT_BODY + " TEXT," + CHAT_INMINE + " TEXT,"
                + CHAT_MSG_SEEN+ " TEXT," + CHAT_MILISEC + " TEXT," + CHAT_TYPE + " TEXT" + " )";


        /**
         * Query To Create Chat Message Table
         */
        protected static final String CREATE_CHAT_TABLE_RESEND = "CREATE TABLE " + CHAT_TABLE_RESEND + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + CHAT_MSG_ID + " TEXT,"
                + CHAT_SENDER_NAME + " TEXT," + CHAT_SENDER + " TEXT," + CHATRECEIVER + " TEXT,"
                + CHAT_DATE + " TEXT," + CHAT_TIME + " TEXT," + CHAT_BODY + " TEXT," + CHAT_INMINE + " TEXT,"
                + CHAT_MSG_SEEN+ " TEXT," + CHAT_MILISEC + " TEXT," + CHAT_TYPE + " TEXT" + " )";
    }


    /**
     * Connection for chat Attachment Images or vedios.
     */
    public static abstract class ChatAttachedFile implements BaseColumns {

        public static final String CHAT_TABLE_NAME = "attachedFileDetail";
        public static final String KEY_ID = "id";
        public static final String KEY_FILENALE = "filename";
        public static final String KEY_FILETYPE = "filetype";
        public static final String KEY_FILESTATUS = "filestatus";

        /**
         * Query To Create Chat Message Table
         */
        protected static final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + CHAT_TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"  + KEY_FILENALE + " TEXT," +  KEY_FILESTATUS + " TEXT,"  + KEY_FILETYPE + " TEXT" + " )";
    }


  /*  public static abstract class ChatAttachment implements BaseColumns {
        public static final String CHAT_TABLE_NAME = "chatDetail";
        public static final String KEY_ID = "id";
        public static final String CHAT_MSG_ID = "msgid";

    }*/
}
