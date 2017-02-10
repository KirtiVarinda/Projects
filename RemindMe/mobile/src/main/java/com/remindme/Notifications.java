package com.remindme;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;

@SuppressLint("NewApi")
public class Notifications {
	int HELLO_ID1 = 1;
	String message="Current Server Time is : ";
	//	String title="Server Time";
	long time;
	String time1;
	StringBuffer responseString;
	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	Notification notification;
	public boolean started;
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void showNotification(int id,String title,String text,Context ctx,NotificationManager notificationManager,String alarmType,String recipient,String subject,String alarm_period){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			//	Context context = ctx;
			Intent notificationIntent = null ;
			Bitmap largeIcon = null;
			if(alarmType.equals("Wake")){
				largeIcon=((BitmapDrawable) ctx.getResources().getDrawable(R.drawable.notifysmall_wake)).getBitmap();
				notificationIntent = new Intent(ctx,MainReminder.class);
			}else if(alarmType.equals("textMsg")){
				largeIcon=((BitmapDrawable) ctx.getResources().getDrawable(R.drawable.notifysmall_text)).getBitmap();
				notificationIntent = new Intent(Intent.ACTION_VIEW);
				String aa=recipient.replace(",", ";");
				notificationIntent.putExtra("address", aa);
				notificationIntent.putExtra("sms_body",subject);
				notificationIntent.setType("vnd.android-dir/mms-sms");
			}else if(alarmType.equals("email")){
				largeIcon=((BitmapDrawable) ctx.getResources().getDrawable(R.drawable.notifysmall_email)).getBitmap();
				notificationIntent = new Intent(Intent.ACTION_SEND);
				String[] aa=recipient.split(",");
				notificationIntent.putExtra(Intent.EXTRA_EMAIL, aa);
				notificationIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
				notificationIntent.putExtra(Intent.EXTRA_TEXT, "Reminder Email");
				notificationIntent.setType("message/rfc822");
			}else if(alarmType.equals("call")){
				 notificationIntent = new Intent(Intent.ACTION_DIAL);
				notificationIntent.setData(Uri.parse("tel:"+ recipient));
				largeIcon=((BitmapDrawable) ctx.getResources().getDrawable(R.drawable.notifysmall_call)).getBitmap();
			}else if(alarmType.equals("Todo")){
				largeIcon=((BitmapDrawable) ctx.getResources().getDrawable(R.drawable.notifysmall_todo)).getBitmap();
				notificationIntent = new Intent(ctx,MainReminder.class);
			}else if(alarmType.equals("Event")){
				largeIcon=((BitmapDrawable) ctx.getResources().getDrawable(R.drawable.notifysmall_event)).getBitmap();
				String alarmTypeOfEvent[]=alarm_period.split("_");
				if(alarmTypeOfEvent[0].equals("Yearly")){
					if(alarmTypeOfEvent[2].equals("call")){
						 notificationIntent = new Intent(Intent.ACTION_DIAL);
							notificationIntent.setData(Uri.parse("tel:"+ recipient)); 
					}else if(alarmTypeOfEvent[2].equals("text")){
						notificationIntent = new Intent(Intent.ACTION_VIEW);
						String aa=recipient.replace(",", ";");
						notificationIntent.putExtra("address", aa);
						notificationIntent.putExtra("sms_body",subject);
						notificationIntent.setType("vnd.android-dir/mms-sms");
					}
				}else{
					notificationIntent = new Intent(ctx,MainReminder.class);
				}
			}
		//	Intent notificationIntent = new Intent(ctx,MainReminder.class);             //context.getClass() otherwise
	 	    PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);
			int smallIcon=R.drawable.notify_small;

			Notification.Builder build1 = new Notification.Builder(ctx)
			.setLargeIcon(largeIcon)
			.setSmallIcon(smallIcon)
			.setAutoCancel(true)
			.setTicker("Alarm Snnozed")
			//                    .setWhen(time)                                 // or System.currentTimeMillis() for tour system
			.setContentTitle(title)
			//	.setDefaults(Notification.DEFAULT_ALL)
				     .setContentIntent(contentIntent)
			.setContentText(text);

			notification = new Notification.BigTextStyle(build1).bigText(text).build();
			//	notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.defaults|= Notification.DEFAULT_LIGHTS;

			//	noti = new Notification.BigTextStyle(build1).build();
			//             noti.defaults|= Notification.DEFAULT_SOUND;
			//             noti.defaults|= Notification.DEFAULT_LIGHTS;
			//             noti.defaults|= Notification.DEFAULT_VIBRATE;
			//Show the notification
			notificationManager.notify(id, notification);
			//	HELLO_ID1+=1;
			//   notificationManager.cancel(1);
		}
	}

}
