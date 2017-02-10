package com.lovelife.lovelife.LoveChat;

import android.graphics.Bitmap;

import java.util.Random;

public class ChatMessage {

    public String body, sender, receiver, senderName;
    Bitmap imagePath;
    public String Date, Time,milliSec;
    public String msgid;
    public boolean isMine;// Did I send the message.

    public ChatMessage(String Sender, String Receiver, String messageString,
                       String ID, boolean isMINE) {
        body = messageString;
        isMine = isMINE;
        sender = Sender;
        msgid = ID;
        receiver = Receiver;
        senderName = Sender;

    }

    public ChatMessage(String Sender, String Receiver,
                       String ID, boolean isMINE,Bitmap ImagePath) {

        isMine = isMINE;
        sender = Sender;
        msgid = ID;
        receiver = Receiver;
        senderName = Sender;
        imagePath  = ImagePath;
    }


    public void setMsgID() {

        msgid += "-" + String.format("%02d", new Random().nextInt(100));

    }
}