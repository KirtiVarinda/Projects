package com.lovelife.lovelife.LoveChat;

/**
 * Created by Kirti-PC on 11/9/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lovelife.lovelife.BeanClasses.BeanForChat;

public class ChatDBHandler extends SQLiteOpenHelper {
    public static BeanForChat[] contactList;
    public static BeanForChat bean;

    /**
     * set Database Name
     */
    public static final String DATABASE_NAME = "loveLife";

    public ChatDBHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * Create Chat Table
         */
        db.execSQL(DBConnection.ChatDetail.CREATE_CHAT_TABLE);
        db.execSQL(DBConnection.ChatDetail.CREATE_CHAT_TABLE_RESEND);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+ DBConnection.ChatDetail.CREATE_CHAT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ DBConnection.ChatDetail.CREATE_CHAT_TABLE_RESEND);
        onCreate(db);
    }

    public void drop(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS '" + DBConnection.ChatDetail.CREATE_CHAT_TABLE + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + DBConnection.ChatDetail.CREATE_CHAT_TABLE_RESEND + "'");
    }

    public boolean insertChatDetail(String msgID, String senderName, String sender, String receiver, String date, String time, String body, String inMine, String msgSeen, String miliSec, String type) {
        /**
         * Insert Chat Detail on Sqlite Database.
         */
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("msgid", msgID);
        contentValues.put("sendername", senderName);
        contentValues.put("sender", sender);
        contentValues.put("receiver", receiver);
        contentValues.put("date", date);
        contentValues.put("time", time);
        contentValues.put("body", body);
        contentValues.put("inmine", inMine);
        contentValues.put("msgseen", msgSeen);
        contentValues.put("milisec", miliSec);
        contentValues.put("type", type);

        db.insert(DBConnection.ChatDetail.CHAT_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertChatResend(String msgID, String senderName, String sender, String receiver, String date, String time, String body, String inMine, String msgSeen, String miliSec, String type) {
        /**
         * Insert Chat resnd on Sqlite Database.
         */
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("msgid", msgID);
        contentValues.put("sendername", senderName);
        contentValues.put("sender", sender);
        contentValues.put("receiver", receiver);
        contentValues.put("date", date);
        contentValues.put("time", time);
        contentValues.put("body", body);
        contentValues.put("inmine", inMine);
        contentValues.put("msgseen", msgSeen);
        contentValues.put("milisec", miliSec);
        contentValues.put("type", type);

        db.insert(DBConnection.ChatDetail.CHAT_TABLE_RESEND, null, contentValues);
        return true;
    }





    public void clearTable()   {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DBConnection.ChatDetail.CHAT_TABLE_NAME, null, null);
    }


    public  void setList(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+ DBConnection.ChatDetail.CHAT_BODY +" FROM " + DBConnection.ChatDetail.CHAT_TABLE_NAME  + "' ORDER BY " + DBConnection.ChatDetail.CHAT_MILISEC + " ASC", null);
    }




/*    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor oldestDateCursor = db.query(DBConnection.ChatDetail.CHAT_TABLE_NAME, null, null, null, null, null, "date_column ASC LIMIT 1");
        if (oldestDateCursor.moveToFirst())
        {
            String date = oldestDateCursor.getColumnName(oldestDateCursor.getColumnIndex("date_column"));
        }
        oldestDateCursor.close();
    }*/

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, DBConnection.ChatDetail.CHAT_TABLE_NAME);
        return numRows;
    }

/*    public ArrayList<String> getAllCotacts() {
        *//**
     * To see the Entered Chat Detail From Table.
     *//*
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DBConnection.ChatDetail.CHAT_TABLE_NAME + " where id=2", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(DBConnection.ChatDetail.KEY_ID)));
            array_list.add(res.getString(res.getColumnIndex(DBConnection.ChatDetail.CHAT_MSG_ID)));
            array_list.add(res.getString(res.getColumnIndex(DBConnection.ChatDetail.CHAT_SENDER_NAME)));
            array_list.add(res.getString(res.getColumnIndex(DBConnection.ChatDetail.CHAT_SENDER)));
            array_list.add(res.getString(res.getColumnIndex(DBConnection.ChatDetail.CHATRECEIVER)));
            array_list.add(res.getString(res.getColumnIndex(DBConnection.ChatDetail.CHAT_DATE)));
            array_list.add(res.getString(res.getColumnIndex(DBConnection.ChatDetail.CHAT_TIME)));
            array_list.add(res.getString(res.getColumnIndex(DBConnection.ChatDetail.CHAT_BODY)));
            array_list.add(res.getString(res.getColumnIndex(DBConnection.ChatDetail.CHAT_INMINE)));
            array_list.add(res.getString(res.getColumnIndex(DBConnection.ChatDetail.CHAT_MSG_SEEN)));
            res.moveToNext();


        }
        System.out.println("ffrrfder" + array_list);
        return array_list;
    }*/


   /** Getting All Contacts*/
    public BeanForChat[] getAllChat() {

        // Select All Query
        //  String selectQuery = "SELECT  * FROM " + DBConnection.ChatDetail.CHAT_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBConnection.ChatDetail.CHAT_TABLE_NAME  + " ORDER BY " + DBConnection.ChatDetail.CHAT_MILISEC + " DESC", null);
        // Cursor cursor = db.rawQuery("SELECT "+ DBConnection.ChatDetail.CHAT_BODY +" FROM " + DBConnection.ChatDetail.CHAT_TABLE_NAME  + "' ORDER BY " + DBConnection.ChatDetail.CHAT_MILISEC + " ASC", null);

        contactList = new BeanForChat[cursor.getCount()];
        // looping through all rows and adding to list
        int i=0;
        if (cursor.moveToFirst()) {
            do {
                bean = new BeanForChat();

                bean.setId(String.valueOf(Integer.parseInt(cursor.getString(0))));
                bean.setMsg_id(cursor.getString(1));
                bean.setSender_name(cursor.getString(2));
                bean.setSender(cursor.getString(3));
                bean.setReceiver(cursor.getString(4));
                bean.setChatDate(cursor.getString(5));
                bean.setChatTime(cursor.getString(6));
                bean.setChatMsg(cursor.getString(7));
                bean.setIsmine(Boolean.valueOf(cursor.getString(8)));
                bean.setMsgSeen(cursor.getString(9));
                bean.setMiliSec(cursor.getString(10));
                bean.setType(cursor.getString(11));
                contactList[i]=bean;
                i++;
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }


    /** Getting last 10 received messages  */
    public  BeanForChat[] getlastReceivedMessages() {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBConnection.ChatDetail.CHAT_TABLE_NAME  +" WHERE "+DBConnection.ChatDetail.CHAT_TYPE+" = 'receiver' ORDER BY " + DBConnection.ChatDetail.KEY_ID + " DESC LIMIT 10", null);

        contactList = new BeanForChat[cursor.getCount()];
        int i=0;
        if (cursor.moveToFirst()) {
            do {
                bean = new BeanForChat();

                bean.setId(String.valueOf(Integer.parseInt(cursor.getString(0))));
                bean.setMsg_id(cursor.getString(1));
                bean.setSender_name(cursor.getString(2));
                bean.setSender(cursor.getString(3));
                bean.setReceiver(cursor.getString(4));
                bean.setChatDate(cursor.getString(5));
                bean.setChatTime(cursor.getString(6));
                bean.setChatMsg(cursor.getString(7));
                bean.setIsmine(Boolean.valueOf(cursor.getString(8)));
                bean.setMsgSeen(cursor.getString(9));
                bean.setMiliSec(cursor.getString(10));
                bean.setType(cursor.getString(11));
                contactList[i]=bean;
                i++;
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }




    /** Getting All Contacts*/
    public BeanForChat[] getAllChatResend() {

        // Select All Query
        //  String selectQuery = "SELECT  * FROM " + DBConnection.ChatDetail.CHAT_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBConnection.ChatDetail.CHAT_TABLE_RESEND  + " ORDER BY " + DBConnection.ChatDetail.CHAT_MILISEC + " DESC", null);
        // Cursor cursor = db.rawQuery("SELECT "+ DBConnection.ChatDetail.CHAT_BODY +" FROM " + DBConnection.ChatDetail.CHAT_TABLE_NAME  + "' ORDER BY " + DBConnection.ChatDetail.CHAT_MILISEC + " ASC", null);

        contactList = new BeanForChat[cursor.getCount()];
        // looping through all rows and adding to list
        int i=0;
        if (cursor.moveToFirst()) {
            do {
                bean = new BeanForChat();

                bean.setId(String.valueOf(Integer.parseInt(cursor.getString(0))));
                bean.setMsg_id(cursor.getString(1));
                bean.setSender_name(cursor.getString(2));
                bean.setSender(cursor.getString(3));
                bean.setReceiver(cursor.getString(4));
                bean.setChatDate(cursor.getString(5));
                bean.setChatTime(cursor.getString(6));
                bean.setChatMsg(cursor.getString(7));
                bean.setIsmine(Boolean.valueOf(cursor.getString(8)));
                bean.setMsgSeen(cursor.getString(9));
                bean.setMiliSec(cursor.getString(10));
                bean.setType(cursor.getString(11));
                contactList[i]=bean;
                i++;
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }


    public boolean isResendMsgExist(String msg) {
        boolean doesExist=false;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = null;
        String sql ="SELECT * FROM "+DBConnection.ChatDetail.CHAT_TABLE_RESEND +" WHERE "+ DBConnection.ChatDetail.CHAT_MSG_ID +"="+msg;
        cursor= db.rawQuery(sql,null);

        if(cursor.getCount()>0){
            doesExist=true;
        }
        return doesExist;
    }


    public void delete(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+DBConnection.ChatDetail.CHAT_TABLE_RESEND+" where "+ DBConnection.ChatDetail.KEY_ID +" = "+Integer.parseInt(id));
    }

}