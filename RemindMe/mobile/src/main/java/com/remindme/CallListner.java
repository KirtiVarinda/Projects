package com.remindme;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.remindme.RemindMeSession.SharedData;
import com.remindme.manage.DeviceManager;

public class CallListner extends BroadcastReceiver {
    PhoneStateListener pListener;
    boolean call_disconnected;
    boolean runOnce = true;
    boolean call_ringing;
    boolean call_pick;
    String phone_number;
    int count = 0;
    String time;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        /** run sms catcher if enable from setting */
        SharedData sharedData = new SharedData(context);


        if (sharedData.getGeneralSaveSession(SharedData.CALL_CATCH).equals("yes")) {
            time = DeviceManager.getCurrentTimeInSpecificFormat();

            Bundle bundle = intent.getExtras();
            phone_number = "";


            if (bundle.containsKey("incoming_number")) {
                phone_number = bundle.getString("incoming_number");
                //	bundle.getString("outgoing_number");
                //String s=bundle.getString("outgoing_number", "");
            }


            if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
                TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (pListener == null) {
                    pListener = new PhoneStateListener() {
                        @Override
                        public void onCallStateChanged(int state, String incomingNumber) {
                            //  call_pick=false;
                            if (state == TelephonyManager.CALL_STATE_IDLE) {
                                if (call_ringing && !call_pick) {
                                    call_disconnected = true;
                                }

                            } else if (state == TelephonyManager.CALL_STATE_RINGING) {
                                call_ringing = true;
                            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                                call_pick = true;
                            }

                            if (!call_pick && call_disconnected) {
                                if (runOnce) {
                                    runOnce = false;
                                    if (!phone_number.equals("") || phone_number != null) {


                                        Intent in = new Intent(context, CatchMissedCall.class);
                                        in.putExtra("number", phone_number);
                                        in.putExtra("callTime", time);
                                        in.putExtra("type", "call");
                                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        context.startActivity(in);
                                    }

                                }

                            }

					/*	switch (state) {
                        case TelephonyManager.CALL_STATE_IDLE:

							break;

						case TelephonyManager.CALL_STATE_RINGING:

							break;

						case TelephonyManager.CALL_STATE_OFFHOOK:
							System.out.println("picked");
							break;
						}*/
                        }

                    };
                    tManager.listen(pListener, PhoneStateListener.LISTEN_CALL_STATE);
                }
            }
        }

    }


}
