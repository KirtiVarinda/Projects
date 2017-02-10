package com.remindme.FuncardServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.remindme.CatchMissedCall;
import com.remindme.RemindMeSession.SharedData;
import com.remindme.manage.DeviceManager;

public class SMSReceiver extends BroadcastReceiver {
    private static String time = "";
    private static String phone_number="" ;
    @Override
    public void onReceive(Context context, Intent intent) {

        /** run sms catcher if enable from setting */
        SharedData sharedData =new SharedData(context);

        if(sharedData.getGeneralSaveSession(SharedData.SMS_CATCH).equals("yes")){

            // TODO Auto-generated method stub
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

                time = DeviceManager.getCurrentTimeInSpecificFormat();


                Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
                SmsMessage[] msgs = null;

                String msgBody = "";
                if (bundle != null) {
                    //---retrieve the SMS message received---
                    try {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for (int i = 0; i < msgs.length; i++) {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            phone_number = msgs[i].getOriginatingAddress();
                            msgBody = msgs[i].getMessageBody();
                        }

                        Intent in=new Intent(context,CatchMissedCall.class);
                        in.putExtra("number", phone_number);
                        in.putExtra("callTime", time);
                        in.putExtra("type", "sms");
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(in);

                    } catch (Exception e) {
//                            Log.d("Exception caught",e.getMessage());
                    }
                }


            }

        }

    }
}
