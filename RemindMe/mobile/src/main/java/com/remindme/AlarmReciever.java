package com.remindme;

import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
public class AlarmReciever extends BroadcastReceiver{
	private DatabaseHandler ourReminders;
	private SQLiteDatabase ourDatabase;
	public List<String[]> reminderWithId =null ;
	String bd_id,names,recipient,subject,date,time,alarm_period,alarm_type,db_milisec;
	@Override
	public void onReceive(Context context, Intent intent){
		Long id = null;
		String msg = null;
		Bundle extras = intent.getExtras();
		if(extras!=null){
			msg = extras.getString("Message");
			id = extras.getLong("Id");
			int id2=(int)(long)id;
			getData(context,id2);
			ourReminders=new DatabaseHandler(context);
			ourDatabase=ourReminders.openConnection(context);
			String[] period=alarm_period.split("_");
			if(period[0].equals("weekday")){
				Calendar calendar = Calendar.getInstance();
				int day = calendar.get(Calendar.DAY_OF_WEEK);
				if(day==7 || day==1){
					//			System.out.println("Saturday and sunday");
					Long miliSecInLong=Long.parseLong(db_milisec);
					Calendar calendar1 = Calendar.getInstance();
					calendar1.setTimeInMillis(miliSecInLong);
					calendar1.add(Calendar.HOUR_OF_DAY, 24);
					Intent intentAlarm = new Intent(context, AlarmReciever.class);
					intentAlarm.putExtra("Message", msg);
					intentAlarm.putExtra("Id", id);
					AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
					int requestCode1=(int)(long)id;
					alarmManager.set(AlarmManager.RTC_WAKEUP,calendar1.getTimeInMillis(), PendingIntent.getBroadcast(context,requestCode1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
					String newDate=SelectTime.milisecToDate(calendar1.getTimeInMillis());
					String newTime=SelectTime.milisecToTime(calendar1.getTimeInMillis());
					ourReminders.updateSnoozTime(requestCode1,calendar1.getTimeInMillis()+"",newDate,newTime);
				}else{
					//		System.out.println("day of weekday="+day);
					Intent in1=new Intent(context,ShowAlarm.class);
					in1.putExtra("Message", msg);
					in1.putExtra("Id", id);
					in1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					in1.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					context.startActivity(in1); 
				}
			}else if(period[0].equals("weekend")){
				Calendar calendar = Calendar.getInstance();
				int day = calendar.get(Calendar.DAY_OF_WEEK);
				if(day==7 || day==1){
					//		System.out.println("day of week end="+day);
					Intent in=new Intent(context,ShowAlarm.class);
					in.putExtra("Message", msg);
					in.putExtra("Id", id);
					in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					in.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					context.startActivity(in); 

				}else{
				//	System.out.println("day of week ="+day);
					Long miliSecInLong=Long.parseLong(db_milisec);
					Calendar calendar1 = Calendar.getInstance();
					calendar1.setTimeInMillis(miliSecInLong);
					calendar1.add(Calendar.HOUR_OF_DAY, 24);
					Intent intentAlarm = new Intent(context, AlarmReciever.class);
					intentAlarm.putExtra("Message", msg);
					intentAlarm.putExtra("Id", id);
					AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
					int requestCode1=(int)(long)id;
					alarmManager.set(AlarmManager.RTC_WAKEUP,calendar1.getTimeInMillis(), PendingIntent.getBroadcast(context,requestCode1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
					String newDate=SelectTime.milisecToDate(calendar1.getTimeInMillis());
					String newTime=SelectTime.milisecToTime(calendar1.getTimeInMillis());
					ourReminders.updateSnoozTime(requestCode1,calendar1.getTimeInMillis()+"",newDate,newTime);
				}
			}else{
				// create dummy activity to solve third party lock problem and call before showalarm activity.
				//		System.out.println("asfaghfaf======Everyday");
			/*	 Intent in1=new Intent(context,Dummy.class);
				 in1.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
				in1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(in1); */
				Intent in=new Intent(context,ShowAlarm.class);
				in.putExtra("Message", msg);
				in.putExtra("Id", id);
				in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				in.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
				in.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				context.startActivity(in); 
			}
		}
		ourReminders.closeConnection();
	}
	public void getData(Context context,int id){
		ourReminders=new DatabaseHandler(context);
		ourDatabase=ourReminders.openConnection(context);
		reminderWithId= ourReminders.selectWithId(id);
		for (final String[] reminderValues : reminderWithId) {
			bd_id=reminderValues[0];
			names=reminderValues[1];
			recipient=reminderValues[2];
			subject=reminderValues[3];
			date=reminderValues[4];
			time=reminderValues[5];
			alarm_period=reminderValues[6];
			alarm_type=reminderValues[7];
			db_milisec=reminderValues[9];
		}
		ourReminders.closeConnection();
	}
}
