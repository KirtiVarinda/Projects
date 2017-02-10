package com.remindme;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

public class BootReceiver extends BroadcastReceiver{
	private DatabaseHandler ourReminders;
	private SQLiteDatabase ourDatabase;
	public List<String[]> AllReminders =null ;
	@Override
	public void onReceive(Context context, Intent arg1) {
		getRemindersFromDatabase(context);
		makeSnoozZero(context);
		setAlarmOnphoneBooting(context);
		defaultAlarm(context);
	}
	protected void makeSnoozZero(Context context){
		int requestCode;
		ourReminders=new DatabaseHandler(context);
		ourDatabase=ourReminders.openConnection(context);
		for (final String[] reminderValues : AllReminders) {
			 requestCode=Integer.parseInt(reminderValues[0]);
			ourReminders.updateSnoozNo(requestCode,"0_no");
		}
		ourReminders.closeConnection();
	}
	protected void setAlarmOnphoneBooting(Context context){
		ourReminders=new DatabaseHandler(context);
		ourDatabase=ourReminders.openConnection(context);
		for (final String[] reminderValues : AllReminders) {
			if(reminderValues[8].equals("run")){
				String alarmString = "";
				if(reminderValues[7].equals("wakeup")){
					alarmString="Wake";
				}else if(reminderValues[7]. equals("textmessage")){
					alarmString="textMsg";
				}else if(reminderValues[7].equals("email")){
					alarmString="email";
				}else if(reminderValues[7].equals("call")){
					alarmString="call";
				}else if(reminderValues[7].equals("todo")){
					alarmString="Todo";
				}else if(reminderValues[7].equals("event")){
					alarmString="Event";
				}
				long requestCode=Long.parseLong(reminderValues[0]);
				Long miliSecInLong=Long.parseLong(reminderValues[9]);
				Calendar calendar = Calendar.getInstance();
				long cTime= calendar.getTimeInMillis() ;
				calendar.setTimeInMillis(miliSecInLong);
				int requestCode1=(int)(long)requestCode;
				String alarmTypeOfEvent[]; 
				if(alarmString.equals("Wake") || alarmString.equals("Event")||alarmString.equals("Todo")){
					alarmTypeOfEvent=reminderValues[6].split("_");
					while(calendar.getTimeInMillis()<cTime){
						if(alarmTypeOfEvent[0].equals("monthly")){
							calendar.add(Calendar.MONTH, 1);

						}else if(alarmTypeOfEvent[0].equals("Yearly")){
							calendar.add(Calendar.YEAR, 1);

						}else if(alarmTypeOfEvent[0].equals("weekly")){
							calendar.add(Calendar.WEEK_OF_MONTH, 1);

						}else if(alarmTypeOfEvent[0].equals("everyday")){
							calendar.add(Calendar.HOUR_OF_DAY, 24);

						}else if(alarmTypeOfEvent[0].equals("weekday")){
							calendar.add(Calendar.HOUR_OF_DAY, 24);

						} else if(alarmTypeOfEvent[0].equals("weekend")){
							calendar.add(Calendar.HOUR_OF_DAY, 24);

						}
					}
					String newDate=SelectTime.milisecToDate(calendar.getTimeInMillis());
					String newTime=SelectTime.milisecToTime(calendar.getTimeInMillis());
					  if(alarmTypeOfEvent[0].equals("weekly")){
						  ourReminders.updateSnoozTime(requestCode1,calendar.getTimeInMillis()+"",reminderValues[4],newTime);

					}else{
						  ourReminders.updateSnoozTime(requestCode1,calendar.getTimeInMillis()+"",newDate,newTime);
					  }

				}
				Intent intentAlarm = new Intent(context, AlarmReciever.class);
				intentAlarm.putExtra("Message", alarmString);
				intentAlarm.putExtra("Id", requestCode);
				AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), PendingIntent.getBroadcast(context,requestCode1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
			}
		}
		ourReminders.closeConnection();
	}
	protected void getRemindersFromDatabase(Context context){
		ourReminders=new DatabaseHandler(context);
		ourDatabase=ourReminders.openConnection(context);
		AllReminders=ourReminders.selectAll("all");
		ourReminders.closeConnection();
	}
	protected void defaultAlarm(Context context){
		SelectTime st=new SelectTime();
		GregorianCalendar calendar = new GregorianCalendar(st.mYear,st.mMonth,st.mDay,st.mHour,st.mMinute);
		calendar.add(Calendar.MINUTE, 5);
		 
		Intent intentAlarm = new Intent(context, ContactReceiver.class);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), PendingIntent.getBroadcast(context,100000,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
		 
	}

}
