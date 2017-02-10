package com.dx.dataapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dx.readcallLogs.CallLogData;

import java.util.ArrayList;

/**
 * Created by dx on 6/3/2015.
 */
public class CallLogAdapter extends ArrayAdapter<CallLogData> {
    ArrayList<CallLogData> logData;
    Context context;
    public CallLogAdapter(Context context, int resource,ArrayList<CallLogData> logData) {
        super(context,resource,logData);
        this.logData=logData;
        this.context=context;

        System.out.println(" list record const="+logData.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.call_list_row, parent, false);
        TextView textView1 = (TextView) rowView.findViewById(R.id.firstLine);
        TextView textView2 = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView img=(ImageView)rowView.findViewById(R.id.icon);
        if(logData.get(position).getmType().equals("OUTGOING")){
            img.setImageResource(R.drawable.dial);
        }else if(logData.get(position).getmType().equals("INCOMING")){
            img.setImageResource(R.drawable.incoming);
        }else if(logData.get(position).getmType().equals("MISSED")){
            img.setImageResource(R.drawable.missed);
        }

        if(logData.get(position).getmName()==null){
            textView1.setText(logData.get(position).getmNumber());
        }else{
            textView1.setText(logData.get(position).getmName()+"("+logData.get(position).getmNumber()+")");
        }
        textView2.setText(logData.get(position).getmDate()+"            Duration:- "+logData.get(position).getmDuration());
        System.out.println(" list record"+logData.get(position).getmName()+logData.get(position).getmDate());

        return rowView;
    }



}
