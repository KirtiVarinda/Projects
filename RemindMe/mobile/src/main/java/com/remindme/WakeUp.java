package com.remindme;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.NotificationCompat.WearableExtender;
public class WakeUp extends Activity {
	String themeType;
	Bitmap[] themeImages;
	String edit_date;
	String [] edit_alarm_period1;
	boolean timePass=false;
	String edit_id ;
	String edit_miliSec;
	boolean editCheck=false;
	String  densityCheck="";
	String exact_reminder_time="no";
	Intent intentAlarm;
	boolean didItWork=true;
	private DatabaseHandler ourReminders;
	private SQLiteDatabase ourDatabase;
	String reminder_date;
	String reminder_time;
	String reminder_message;
	SharedPreferences remindMessagePref;
	View layout;
	String 	alarmTypeSeleted="everyday";
	boolean container_check=false;
	Bitmap[] denisityBitmap=new Bitmap[9];
	TextView wakeup_top,gray_textview,close,wakeup_center1,wakeup_center2,wakeup_center3,wakeup_bottom,edit1,edit2,edit3,everyday,weekday,once ;
	Button yes_button;
	public static final String START_ACTIVITY_PATH = "/start/MainActivity";
	TextView alarmType_msg,combine_msg,time,message;
	int sdk = Build.VERSION.SDK_INT;
	int current_hour,current_min,current_year,current_month,current_day;
	private GoogleApiClient mGoogleApiClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wake_up);

		checkScreenDensity();
		SelectTime st=new SelectTime();
		current_hour=st.mHour;
		current_min=st.mMinute;
		current_year=st.mYear;
		current_month=st.mMonth;
		current_day=st.mDay;
		layout = findViewById(R.id.layout1);
		remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);

		/*================================= theme images session getting====================================*/
		themeType= remindMessagePref.getString("theme_selector", "");
		int size = remindMessagePref.getInt("theme_size", 0);
		String imageArray[] = new String[size];
		for(int i=0;i<size;i++){
			imageArray[i] = remindMessagePref.getString("stringImage_"+i, "");
		}
		themeImages=new Bitmap[size];
		themeImages=Theme.decodeBase64(imageArray);



		/*================================= converting images according to devices density====================================*/
		getTextViewImages();
		buttonClicked();
		getImagesInBitmap();
		sdkCheck();




		Bundle extras =  getIntent().getExtras();
		if(extras!=null){
			editModule(extras);
		}else{
			time.setText(SelectTime.CurrenttwelevFormatTime(SelectTime.timeAfterHour(),current_min));
		}
		combineMessage(alarmTypeSeleted);
		//	setImage();
	}


	protected void editModule(Bundle extras){
		editCheck=true;
		edit_id = extras.getString("edit_id");
		String edit_names = extras.getString("edit_names");
		String edit_recipient = extras.getString("edit_recipient");
		String edit_listSubject = extras.getString("edit_listSubject");
		edit_date = extras.getString("edit_date");
		String edit_time = extras.getString("edit_time");
		exact_reminder_time=SelectTime.time12To24(edit_time);
		String edit_alarm_period = extras.getString("edit_alarm_period");
		String edit_alarmType = extras.getString("edit_alarmType");
		String edit_snoozDone = extras.getString("edit_snoozDone");
		edit_miliSec = extras.getString("edit_miliSec");
		time.setText(edit_time);
		message.setText(edit_listSubject);
		edit_alarm_period1=edit_alarm_period.toString().split("_");
		if(edit_alarm_period1[0].equals("everyday")){
			alaramTypeImageChage(4,5,5); 
			alarmTypeSeleted="everyday";
			alarmType_msg.setText("Everyday of the week");
		}else if(edit_alarm_period1[0].equals("weekday")){
			alaramTypeImageChage(5,4,5); 
			alarmTypeSeleted="weekday";
			alarmType_msg.setText("Mon, Tue, Wed, Thu, Fri");
		}else if(edit_alarm_period1[0].equals("weekend")){
			alaramTypeImageChage(5,5,4); 
			alarmTypeSeleted="weekend";
			alarmType_msg.setText(" Saturday, Sunday");
		}else if(edit_alarm_period1[0].equals("once")){
			alaramTypeImageChage(5,5,5); 
			alarmTypeSeleted="once";
			alarmType_msg.setText(edit_date);
		}
	}


	protected void buttonClicked(){
		yes_button.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {

				inserReminderToDataBase();
			}
		});
		close.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				message.setText("Jogging");
				finish();
				overridePendingTransition(R.anim.push_down_out, R.anim.push_down_out);
			}
		});
		wakeup_bottom.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				Intent in = new Intent(WakeUp.this,ReminderMessage.class);
				in.putExtra("MessageFor", "wake_up");
				in.putExtra("textEdit", message.getText().toString());
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

			}
		});

		wakeup_center3.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {

				Intent in = new Intent(WakeUp.this,SelectTime.class);
				in.putExtra("for_time","getTime");
				in.putExtra("activity_time",time.getText().toString());
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

			}
		});

		wakeup_center1.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				//	alarmType_msg.setText("Tuesday");
				Intent in = new Intent(WakeUp.this,CalendarView.class);
				in.putExtra("for_time","getDate");
				if(editCheck && edit_alarm_period1[0].equals("once")){
					in.putExtra("activity_date",edit_date );
				}else{
					in.putExtra("activity_date"," ");
				} 
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left); 

			}
		});
		everyday.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				alaramTypeImageChage(4,5,5);
				alarmTypeSeleted="everyday";
				alarmType_msg.setText("Everyday of the week");
				combineMessage(alarmTypeSeleted);
			}
		});
		weekday.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				alaramTypeImageChage(5,4,5); 
				alarmTypeSeleted="weekday";
				alarmType_msg.setText("Mon, Tue, Wed, Thu, Fri");
				combineMessage(alarmTypeSeleted);
			}
		});
		once.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				alaramTypeImageChage(5,5,4); 
				alarmType_msg.setText(" Saturday, Sunday");
				alarmTypeSeleted="weekend";
				combineMessage(alarmTypeSeleted);
			}
		});
	}
	protected void combineMessage(String check){
		String combine;
		String upToNCharacters;
		if(check.equals("everyday")){
			combine="Everyday at "+time.getText().toString()+" for "+message.getText().toString();
		}else{
			combine="On "+alarmType_msg.getText().toString()+" at "+time.getText().toString()+" for "+message.getText().toString();
		}
		//upToNCharacters = combine.substring(0, Math.min(combine.length(), 25));
		combine_msg.setText(combine);
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected void alaramTypeImageChage(int everyday1,int weekday1,int once1){
		if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
			everyday.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[everyday1]));
			weekday.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[weekday1]));
			once.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[once1]));
		}else{
			everyday.setBackground(new BitmapDrawable(getResources(),denisityBitmap[everyday1]));
			weekday.setBackground(new BitmapDrawable(getResources(),denisityBitmap[weekday1]));
			once.setBackground(new BitmapDrawable(getResources(),denisityBitmap[once1]));
		}
	}
	protected void getTextViewImages(){
		wakeup_top = (TextView )findViewById(R.id.textView2);
		gray_textview = (TextView )findViewById(R.id.textView4);
		close = (TextView )findViewById(R.id.textView1);
		yes_button = (Button) findViewById(R.id.button1);
		wakeup_center1 = (TextView )findViewById(R.id.textView6);
		wakeup_center2 = (TextView )findViewById(R.id.textView5);
		wakeup_center3 = (TextView )findViewById(R.id.textView7);
		wakeup_bottom = (TextView )findViewById(R.id.textView11);
		edit1 = (TextView )findViewById(R.id.textView23);
		edit2 = (TextView )findViewById(R.id.textView24);
		edit3 = (TextView )findViewById(R.id.textView8);
		everyday = (TextView )findViewById(R.id.textView12);
		weekday = (TextView )findViewById(R.id.textView13);
		once = (TextView )findViewById(R.id.textView14);
		alarmType_msg = (TextView )findViewById(R.id.textView3);
		combine_msg = (TextView )findViewById(R.id.textView10);
		time = (TextView )findViewById(R.id.textView21);
		message = (TextView )findViewById(R.id.textView22);
	}
	protected void getImagesInBitmap(){
		denisityBitmap[0]=((BitmapDrawable) getResources().getDrawable(R.drawable.wakeup_top)).getBitmap();
		denisityBitmap[1]=((BitmapDrawable) getResources().getDrawable(R.drawable.close_reminder)).getBitmap();
		denisityBitmap[2]=((BitmapDrawable) getResources().getDrawable(R.drawable.yes_remind)).getBitmap();
		denisityBitmap[3]=((BitmapDrawable) getResources().getDrawable(R.drawable.menubg)).getBitmap();
		denisityBitmap[4]=((BitmapDrawable) getResources().getDrawable(R.drawable.hover_changebg)).getBitmap();
		denisityBitmap[5]=((BitmapDrawable) getResources().getDrawable(R.drawable.menu_bg)).getBitmap();
		denisityBitmap[6]=((BitmapDrawable) getResources().getDrawable(R.drawable.wakeup_center)).getBitmap();
		denisityBitmap[7]=((BitmapDrawable) getResources().getDrawable(R.drawable.wakeup_bottom)).getBitmap();
		denisityBitmap[8]=((BitmapDrawable) getResources().getDrawable(R.drawable.edit)).getBitmap();
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected void sdkCheck(){
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		denisityBitmap=ScreenDenesityCheck.checkScreenDensityAndResize(denisityBitmap,metrics,densityCheck,MainReminder.checkScreenSize(getApplicationContext()));

		/*==============================Converting images according to theme starts==================================*/
		if(!themeType.equals("purple")){
			denisityBitmap[1]=themeImages[1];
			denisityBitmap[4]=themeImages[3];
		}
		if(themeType.equals("white")){
			everyday.setTextColor(Color.BLACK); 
			weekday.setTextColor(Color.BLACK);  
			once.setTextColor(Color.BLACK);  
		}else{
			everyday.setTextColor(Color.WHITE); 
			weekday.setTextColor(Color.WHITE);  
			once.setTextColor(Color.WHITE);  
		}
		/*==============================Converting images according to theme ends==================================*/

		if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
			wakeup_top.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[0]));
			gray_textview.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[3]));
			close.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[1]));
			yes_button.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[2]));
			wakeup_center1.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[6]));
			wakeup_center2.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[6]));
			wakeup_center3.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[6]));
			wakeup_bottom.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[7]));
			edit1.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[8]));
			edit2.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[8]));
			edit3.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[8]));
			everyday.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[4]));
			weekday.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[5]));
			once.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[5]));

		}	 else {
			wakeup_top.setBackground(new BitmapDrawable(getResources(),denisityBitmap[0]));
			gray_textview.setBackground(new BitmapDrawable(getResources(),denisityBitmap[3]));
			close.setBackground(new BitmapDrawable(getResources(),denisityBitmap[1]));
			yes_button.setBackground(new BitmapDrawable(getResources(),denisityBitmap[2]));
			wakeup_center1.setBackground(new BitmapDrawable(getResources(),denisityBitmap[6]));
			wakeup_center2.setBackground(new BitmapDrawable(getResources(),denisityBitmap[6]));
			wakeup_center3.setBackground(new BitmapDrawable(getResources(),denisityBitmap[6]));
			wakeup_bottom.setBackground(new BitmapDrawable(getResources(),denisityBitmap[7]));
			edit1.setBackground(new BitmapDrawable(getResources(),denisityBitmap[8]));
			edit2.setBackground(new BitmapDrawable(getResources(),denisityBitmap[8]));
			edit3.setBackground(new BitmapDrawable(getResources(),denisityBitmap[8]));
			everyday.setBackground(new BitmapDrawable(getResources(),denisityBitmap[4]));
			weekday.setBackground(new BitmapDrawable(getResources(),denisityBitmap[5]));
			once.setBackground(new BitmapDrawable(getResources(),denisityBitmap[5]));

		} 
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			overridePendingTransition(R.anim.push_down_out, R.anim.push_down_out);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	} 
	@Override
	protected void onPause(){
		super.onPause(); 
		TranslateAnimation anim=new TranslateAnimation(0,500,0,0);
		anim.setDuration(150);
		anim.setFillAfter(false);
		container_check=true;
		layout.setVisibility(View.GONE);
		layout.setAnimation(anim);
	}
	@Override
	protected void onResume(){
		super.onResume();
		/*==========================google Analitics Starts=============================*/	

		AnaliticsForActivity analit=new AnaliticsForActivity();
		analit.gAnalitics(getApplicationContext(),"Wake Up Page");

		/*==========================google Analitics Ends=============================*/
		reminder_date = remindMessagePref.getString("reminder_date", "");
		if(!reminder_date.equals("no")){
			alaramTypeImageChage(5,5,5);
			alarmTypeSeleted="once";
			alarmType_msg.setText(reminder_date);
			combineMessage(alarmTypeSeleted);
			SharedPreferences.Editor editor = remindMessagePref.edit();
			editor.putString("reminder_date","no");	  	  	       
			editor.commit();
		}
		reminder_time = remindMessagePref.getString("reminder_time", "");
		if(!reminder_time.equals("no")){
			exact_reminder_time = remindMessagePref.getString("exact_reminder_time","");
			time.setText(reminder_time);
			combineMessage(alarmTypeSeleted);
			SharedPreferences.Editor editor = remindMessagePref.edit();
			editor.putString("reminder_time","no");	  	
			editor.putString("exact_reminder_time","no");	
			editor.commit();
		}
		reminder_message = remindMessagePref.getString("reminder_message", "");
		if(!reminder_message.equals("no")){
			String message1 = reminder_message;
			message.setText(message1);
			combineMessage(alarmTypeSeleted);
			SharedPreferences.Editor editor = remindMessagePref.edit();
			editor.putString("reminder_message","no");	  	  	       
			editor.commit();

		}
		if(container_check){
			container_check=false;
			TranslateAnimation anim1=new TranslateAnimation(500,0,0,0);
			anim1.setDuration(150);
			anim1.setFillAfter(false);
			layout.setVisibility(View.VISIBLE);
			layout.setAnimation(anim1);
		}
	}
	@Override
	protected void onDestroy(){
		super.onDestroy(); 
		container_check=false;
	}
	public void message(String msg){
		Builder builder = new Builder(WakeUp.this);
		builder.setCancelable(false);
		builder.setTitle("Sorry!");
		builder.setMessage(msg);
		builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}
	public void inserReminderToDataBase(){
		try{
			long lastId;
			String [] splitDate=alarmType_msg.getText().toString().split(" ");
			Long milisec;
			String milidate;
			ourReminders=new DatabaseHandler(this);
			ourDatabase=ourReminders.openConnection(this);
			//insert record
			int hour = 0;
			int min = 0;
			if(exact_reminder_time.equals("no")&& editCheck ){
				long edit_miliSec1=Long.parseLong(edit_miliSec);
				String prevTime=SelectTime.milisecToTimeFull(edit_miliSec1);
				String time21[]=prevTime.split("_");
				hour=Integer.parseInt(time21[0]);
				min=Integer.parseInt(time21[1]);
			}else{
				String hString=SelectTime.time(exact_reminder_time);
				String time21[]=hString.split("_");
				hour=Integer.parseInt(time21[0]);
				min=Integer.parseInt(time21[1]);
			}
			SelectTime st=new SelectTime();
			Long currentMilisec=SelectTime.dateToString(st.mYear,st.mMonth,st.mDay,st.mHour,st.mMinute);
			if(alarmTypeSeleted.equals("once")){ //for once alarm
				milisec=SelectTime.dateToString(Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]),hour,min);
				milidate=milisec+"";
				long pevMili=Long.parseLong(milidate);
				if(currentMilisec>pevMili){
					message("Alarm Time Passed.");
				}else if(editCheck){
					int id=Integer.parseInt(edit_id);
					long id1=(long)id;
					ourReminders.updateReminder(id,"","",message.getText().toString(),alarmType_msg.getText().toString(),time.getText().toString(),alarmTypeSeleted,"wakeup","run",milidate);
					scheduleAlarm(id1,message.getText().toString(),Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
					Afinish();
				}else{

					lastId=ourReminders.addReminders("","",message.getText().toString(),alarmType_msg.getText().toString(),time.getText().toString(),alarmTypeSeleted,"wakeup","run",milidate);
					scheduleAlarm(lastId,message.getText().toString(),Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
					Afinish();
				}



			}else{  // for weekday,weekend and everyday alarms.
				final Calendar c = Calendar.getInstance();
				milisec=SelectTime.dateToString(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH),hour,min);
				milidate=milisec+"";
				long pevMili=Long.parseLong(milidate);
				if(editCheck){
					if(currentMilisec>pevMili){
						timePass=true;
					}
					int id=Integer.parseInt(edit_id);
					long id1=(long)id;
					ourReminders.updateReminder(id,"","",message.getText().toString(),"",time.getText().toString(),alarmTypeSeleted,"wakeup","run",milidate);
					scheduleAlarm(id1,message.getText().toString(),current_year,current_month,current_day);
					Afinish();
				}else{
					if(currentMilisec>pevMili){
						timePass=true;
					}
					lastId=ourReminders.addReminders("","",message.getText().toString(),"",time.getText().toString(),alarmTypeSeleted,"wakeup","run",milidate);
					scheduleAlarm(lastId,message.getText().toString(),current_year,current_month,current_day);
					Afinish();
				}
			}
			ourReminders.closeConnection();
		}catch(Exception e){

			didItWork=false;
			System.out.println("fail to save in database"+e);

		} 
	}
	public void Afinish(){
		finish();
		overridePendingTransition(R.anim.push_down_out, R.anim.push_down_out);
	}

	public void scheduleAlarm(long requestCode,String msg,int year,int month,int day){

		int hour = 0;
		int min = 0;
		String hString=SelectTime.time(exact_reminder_time);
		String time2[]=hString.split("_");
		hour=Integer.parseInt(time2[0]);
		min=Integer.parseInt(time2[1]);

		intentAlarm = new Intent(this, AlarmReciever.class);
		intentAlarm.putExtra("Message", "Wake");
		intentAlarm.putExtra("Id", requestCode);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		GregorianCalendar calendar = new GregorianCalendar(year,month,day,hour,min);
		if(exact_reminder_time.equals("no")&& timePass){
			long edit_miliSec1=Long.parseLong(edit_miliSec);
			calendar.setTimeInMillis(edit_miliSec1);
		}
		int requestCode1=(int)requestCode;
		if(timePass){
			SelectTime st=new SelectTime();
			Long currentMilisec=SelectTime.dateToString(st.mYear,st.mMonth,st.mDay,st.mHour,st.mMinute);
			while(calendar.getTimeInMillis()<currentMilisec){
				calendar.add(Calendar.HOUR_OF_DAY, 24);
			}
			//	SelectTime.milisecToDate(calendar.getTimeInMillis());

			timePass=false;

		}
		String newDate=SelectTime.milisecToDate(calendar.getTimeInMillis());
		String newTime=SelectTime.milisecToTime(calendar.getTimeInMillis());
		ourReminders=new DatabaseHandler(WakeUp.this);
		ourDatabase=ourReminders.openConnection(WakeUp.this);
		ourReminders.updateSnoozTime(requestCode1,calendar.getTimeInMillis()+"",newDate,newTime);
		ourReminders.closeConnection();
		long time = calendar.getTimeInMillis();
		alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,requestCode1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

	}

	public void checkScreenDensity(){
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		switch(metrics.densityDpi){
		case DisplayMetrics.DENSITY_LOW:
			densityCheck="low";
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			densityCheck="medium";
			break;
		case DisplayMetrics.DENSITY_HIGH:
			densityCheck="high";
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			densityCheck="xhigh";
			break;
		case DisplayMetrics.DENSITY_XXHIGH:
			densityCheck="xxhigh";
			break;
		}
	}

}
