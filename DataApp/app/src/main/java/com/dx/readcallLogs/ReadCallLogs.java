package com.dx.readcallLogs;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.Contacts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dx on 12/16/2014.
 * This will get the Call Logs of the Device
 */
public class ReadCallLogs {
    static boolean isLogPresent;

    public static ArrayList<CallLogData> getCallDetails(Context context) {
        isLogPresent = false;
        StringBuffer stringBuffer = new StringBuffer();
        ArrayList<CallLogData> logData = new ArrayList<CallLogData>();
        CallLogData callLogData;

         /*
         * get the miliseconds of date from shared preferences
         *
         * */
        // SharedData shareData = new SharedData(context);
        // String fromDate = shareData.getDataInSharedPreferences();


        /*current time*/
        Calendar calendar = Calendar.getInstance();

        String toDate = String.valueOf(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        String fromDate = String.valueOf(calendar.getTimeInMillis());
        String[] whereValue = {fromDate, toDate};

        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.DATE + " BETWEEN ? AND ?", whereValue, CallLog.Calls.DATE + " DESC");
        int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
        while (cursor.moveToNext()) {

            isLogPresent = true;
            String phName = cursor.getString(name);
            String phNumber = cursor.getString(number);
            String callType = cursor.getString(type);
            String callDate = cursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = cursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }

            callLogData = new CallLogData();
            callLogData.setmName(phName);
            callLogData.setmNumber(phNumber);
            callLogData.setmType(dir);
            callLogData.setmDate(callDayTime + "");
            callLogData.setmDuration(ConvertSecToHourMinSec(callDuration));
            logData.add(callLogData);
/**
            stringBuffer.append("\nName:--- " + phName + "\nNumber:--- " + phNumber + " \nCall Type:--- "
                    + dir + " \nCall Date:--- " + callDayTime
                    + " \nCall duration in sec :--- " + callDuration);
            stringBuffer.append("\n----------------------------------");

*/


        }


        cursor.close();
        return logData;
    }


    public static String ConvertSecToHourMinSec(String sec ){
        int totalSecs=Integer.parseInt(sec);
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
