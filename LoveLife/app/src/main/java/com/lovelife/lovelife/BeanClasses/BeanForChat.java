package com.lovelife.lovelife.BeanClasses;

/**
 * Created by Kirti-PC on 11/18/2016.
 */
public class BeanForChat {

    private String id,chatMsg,chatDate,chatTime,msg_id,sender_name,sender,receiver,msgSeen,miliSec;
    Boolean immine;
    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMiliSec() {
        return miliSec;
    }

    public void setMiliSec(String miliSec) {
        this.miliSec = miliSec;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsgSeen() {
        return msgSeen;
    }

    public void setMsgSeen(String msgSeen) {
        this.msgSeen = msgSeen;
    }

    public Boolean getIsmine() {
        return immine;
    }

    public void setIsmine(Boolean immine) {
        this.immine = immine;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatMsg() {
        return chatMsg;
    }

    public void setChatMsg(String chatMsg) {
        this.chatMsg = chatMsg;
    }

    public String getChatDate() {
        return chatDate;
    }

    public void setChatDate(String chatDate) {
        this.chatDate = chatDate;
    }

    public String getChatTime() {
        return chatTime;
    }

    public void setChatTime(String chatTime) {
        this.chatTime = chatTime;
    }
}
