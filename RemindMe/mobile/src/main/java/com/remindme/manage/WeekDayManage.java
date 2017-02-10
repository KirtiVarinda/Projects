package com.remindme.manage;

import java.util.Calendar;

import com.remindme.SelectTime;

public class WeekDayManage {

	public static int getSelectedDay(String day){
		String selectedDay=day.toLowerCase();
		int IntDay = 0;
		if (selectedDay.equals("sunday")) {
			IntDay=1;
		} else if (selectedDay.equals("monday")) {
			IntDay=2;
		}  else if (selectedDay.equals("tuesday")) {
			IntDay=3;
		} else if (selectedDay.equals("wednesday")){
			IntDay=4;
		} else if (selectedDay.equals("thursday")) {
			IntDay=5;
		} else if (selectedDay.equals("friday")){
			IntDay=6;
		} else if (selectedDay.equals("saturday")){
			IntDay=7;
		}
		return IntDay;
	}




	public static String timeForDaySelected(int week) {
		SelectTime st=new SelectTime();
		Calendar calSet =Calendar.getInstance(); 
		calSet.set(Calendar.DAY_OF_WEEK, week);
		calSet.set(Calendar.HOUR_OF_DAY, st.mHour);
		calSet.set(Calendar.MINUTE, 5);
		calSet.set(Calendar.SECOND, 0);
		calSet.set(Calendar.MILLISECOND, 0);
		return SelectTime.milisecToDate(calSet.getTimeInMillis());
	}
}
