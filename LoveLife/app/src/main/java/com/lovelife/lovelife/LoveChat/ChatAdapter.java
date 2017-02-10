package com.lovelife.lovelife.LoveChat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lovelife.lovelife.BeanClasses.BeanForChat;
import com.lovelife.lovelife.BeanClasses.ChatAttachmentBeanClass;
import com.lovelife.lovelife.Dashboard;
import com.lovelife.lovelife.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    ArrayList<BeanForChat> sentChatMessageList;
    HashMap<String, String> setMessageList;
    ArrayList<String> list;
    ArrayList<String> listType;
    ImageView view;
//    ChatAttachmentBeanClass bean = Dashboard.bean;
    Bitmap bitmap;
    BeanForChat chatbean;
    String key, value;
    BeanForChat bean1;
    String message, type;
    TextView msg;

    public ChatAdapter(Activity activity, ArrayList<BeanForChat> list) {
        sentChatMessageList = list;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }
//    public ChatAdapter(Activity activity, ImageView view) {
//        view = view;
//        inflater = (LayoutInflater) activity
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//    }


    @Override
    public int getCount() {
        return sentChatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (convertView == null)

            System.out.println("falseeee" + sentChatMessageList.size());
        vi = inflater.inflate(R.layout.chatbubble, null);
        /**
         * LinearLayouts to show Chat Messages
         */
        LinearLayout layout = (LinearLayout) vi
                .findViewById(R.id.bubble_layout);
        LinearLayout parent_layout = (LinearLayout) vi
                .findViewById(R.id.bubble_layout_parent);

        msg = (TextView) vi.findViewById(R.id.message_text);

        bean1 =  sentChatMessageList.get(position);

        message = bean1.getChatMsg();
        type = bean1.getType();
        msg.setText(message);
        if(message!=null) {

            if (type.equals("sender")) {

                layout.setBackgroundResource(R.drawable.bubble2);
                parent_layout.setGravity(Gravity.RIGHT);

            } else if (type.equals("receiver")) {
                layout.setBackgroundResource(R.drawable.bubble1);
                parent_layout.setGravity(Gravity.LEFT);

            }
        }

//            setMessageList.put(Type,msg1);

//            if (Type.equals("sender")) {
        /**
         * Check if the Sms type is sender
         */


        // If not mine then align to left
        /*    } else if (Type.equals("receiver")) {
                *//**
         * Check if the Sms type is sender
         *//*
                msg.setText(msg1);
                layout.setBackgroundResource(R.drawable.bubble1);
                parent_layout.setGravity(Gravity.LEFT);
                System.out.println("nooo");
            }*/


//        }
//        String message = (String) list.get(position);
      /*  System.out.println("meessssss"+message);
        msg.setText(message);

        System.out.println("mineeee");
        layout.setBackgroundResource(R.drawable.bubble2);
        parent_layout.setGravity(Gravity.RIGHT);*/

        /**
         * get keys and values from hash map.
         */
     /*   for (Map.Entry<String, String> entry : sentChatMessageList.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            System.out.println("didhdfffff" + key + value);


            if (key.equals("sender")) {
                *//**
         * Check if the Sms type is sender
         *//*
                msg.setText(value);

                System.out.println("mineeee");
                layout.setBackgroundResource(R.drawable.bubble2);
                parent_layout.setGravity(Gravity.RIGHT);

                // If not mine then align to left
            } else if (key.equals("receiver")) {
                *//**
         * Check if the Sms type is sender
         *//*
                msg.setText(value);
                layout.setBackgroundResource(R.drawable.bubble1);
                parent_layout.setGravity(Gravity.LEFT);
                System.out.println("nooo");
            }
        }*/

        msg.setTextColor(Color.BLACK);


        return vi;
    }


    public Bitmap createVideoThumbNail(String path) {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
        return bitmap;
    }

    public void add(String msg, String type) {

//            sentChatMessageList.add(type,msg);
//        sentChatMessageList.add(msg);


        System.out.println("dddddddddd" + type);


    }
}