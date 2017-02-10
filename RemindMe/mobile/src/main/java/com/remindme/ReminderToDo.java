package com.remindme;

import java.util.Calendar;
import java.util.GregorianCalendar;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ReminderToDo extends Activity {
	String themeType;
	Bitmap[] themeImages;
	String edit_time;
	boolean checkButtonForAlarm=false;
	Button check_button;
	boolean timePass=false;
	String edit_date;
	String [] edit_alarm_period1;
	String edit_id ;
	String edit_miliSec;
	boolean editCheck=false;
	Intent intentAlarm;
	private DatabaseHandler ourReminders;
	private SQLiteDatabase ourDatabase;
	SelectTime st;
	String reminder_date;
	String exact_reminder_time="no";
	String reminder_time;
	String reminder_message;
	SharedPreferences remindMessagePref;
	boolean container_check=false;
	String alarmTypeSeleted="everyday";
	String  densityCheck="";
	int current_hour,current_min,current_year,current_month,current_day,nextDay,nextMonth,nextYear;
	View layout;
	int[] images={R.drawable.todo,R.drawable.menubg,R.drawable.close_reminder,R.drawable.yes_remind,R.drawable.hover_changebg,R.drawable.menu_bg
			,R.drawable.wakeup_center,R.drawable.wakeup_bottom,R.drawable.edit,R.drawable.check1,R.drawable.check2};
	Bitmap[] denisityBitmap=new Bitmap[images.length];
	TextView todo_top,gray_textview,close,todo_center1,todo_center2,todo_center3,todo_center4,todo_bottom,edit1,edit2,edit3,today,tomorrow,otherDay ;
	TextView message,time,date,combine_msg;
	TextView evrdy,wekdy,wekend,on,at;
	Button yes_button;
	int sdk = Build.VERSION.SDK_INT;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reminder_to_do);
			
		layout = findViewById(R.id.layout1);
		remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
		themeType= remindMessagePref.getString("theme_selector", "");
		int size = remindMessagePref.getInt("theme_size", 0);
		String imageArray[] = new String[size];
		for(int i=0;i<size;i++){
			imageArray[i] = remindMessagePref.getString("stringImage_"+i, "");
		}
		themeImages=new Bitmap[size];
		themeImages=Theme.decodeBase64(imageArray);
		st=new SelectTime();
		current_hour=st.mHour;
		current_min=st.mMinute;
		current_year=st.mYear;
		current_month=st.mMonth;
		current_day=st.mDay;
		nextDay=st.nextDay;
		checkScreenDensity();
		getTextViewImages();
		getImagesInBitmap();
		sdkCheck();
		buttonClicked();
		Bundle extras =  getIntent().getExtras();
		if(extras!=null){
			editModule(extras);
		}else{
			time.setText(SelectTime.CurrenttwelevFormatTime(SelectTime.timeAfterHour(),current_min));
			combineMessage(alarmTypeSeleted);
		}

	}
	protected void editModule(Bundle extras){
		editCheck=true;
		edit_id = extras.getString("edit_id");
		String edit_names = extras.getString("edit_names");
		String edit_recipient = extras.getString("edit_recipient");
		String edit_listSubject = extras.getString("edit_listSubject");
		edit_date = extras.getString("edit_date");
		edit_time = extras.getString("edit_time");
		exact_reminder_time=SelectTime.time12To24(edit_time);
		String edit_alarm_period = extras.getString("edit_alarm_period");
		String edit_alarmType = extras.getString("edit_alarmType");
		String edit_snoozDone = extras.getString("edit_snoozDone");
		edit_miliSec = extras.getString("edit_miliSec");
		message.setText(edit_listSubject);
		time.setText(edit_time);
		edit_alarm_period1=edit_alarm_period.toString().split("_");
		if(edit_alarm_period1[0].equals("runnoAlarm")){
			if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
				check_button.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[9]));
			}else{
				check_button.setBackground(new BitmapDrawable(getResources(),denisityBitmap[9]));
			}
		}else{
			if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
				check_button.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[10]));
			}else{
				check_button.setBackground(new BitmapDrawable(getResources(),denisityBitmap[10]));
			}
		}

		if(edit_alarm_period1[0].equals("runnoAlarm")){
			showHide(1);
			checkButtonForAlarm=false;
			combineMessage(" ");
			time.setText(SelectTime.CurrenttwelevFormatTime(SelectTime.timeAfterHour(),current_min));
		}else if(edit_alarm_period1[0].equals("everyday")){
			showHide(0);
			checkButtonForAlarm=true;
			alaramTypeImageChage(4,5,5);
			alarmTypeSeleted="everyday";
			date.setText("Everyday of the week");
			combineMessage(alarmTypeSeleted);

		}else if(edit_alarm_period1[0].equals("weekday")){
			showHide(0);
			checkButtonForAlarm=true;
			alaramTypeImageChage(5,4,5); 
			alarmTypeSeleted="weekday";
			date.setText("Mon, Tue, Wed, Thu, Fri");
			combineMessage(alarmTypeSeleted);
		}else if(edit_alarm_period1[0].equals("weekend")){
			showHide(0);
			checkButtonForAlarm=true;
			alaramTypeImageChage(5,5,4); 
			date.setText(" Saturday, Sunday");
			alarmTypeSeleted="weekend";
			combineMessage(alarmTypeSeleted);
		}else if(edit_alarm_period1[0].equals("once")){
			showHide(0);
			checkButtonForAlarm=true;
			date.setText(edit_date);
			alaramTypeImageChage(5,5,5); 
			alarmTypeSeleted="once";
			combineMessage(alarmTypeSeleted);

		}
	}
	protected void getTextViewImages(){
		todo_top = (TextView )findViewById(R.id.textView2);
		gray_textview = (TextView )findViewById(R.id.textView3);
		close = (TextView )findViewById(R.id.textView11);
		todo_center1 = (TextView )findViewById(R.id.textView4);
		todo_center2 = (TextView )findViewById(R.id.textView8);
		todo_center3 = (TextView )findViewById(R.id.textView9);
		todo_center4 = (TextView )findViewById(R.id.textView1);
		todo_bottom = (TextView )findViewById(R.id.textView10);
		edit1 = (TextView )findViewById(R.id.textView12);
		edit2 = (TextView )findViewById(R.id.textView13);
		edit3 = (TextView )findViewById(R.id.textView14);
		yes_button = (Button) findViewById(R.id.button1);
		check_button = (Button) findViewById(R.id.button2);
		today = (TextView )findViewById(R.id.textView5);
		tomorrow = (TextView )findViewById(R.id.textView6);
		otherDay = (TextView )findViewById(R.id.textView7);
		message= (TextView )findViewById(R.id.textView22);
		time= (TextView )findViewById(R.id.textView24);
		date= (TextView )findViewById(R.id.textView23);
		combine_msg= (TextView )findViewById(R.id.textView15);
		evrdy= (TextView )findViewById(R.id.textView16);
		wekdy= (TextView )findViewById(R.id.textView17);
		wekend= (TextView )findViewById(R.id.textView18);
		on= (TextView )findViewById(R.id.textView20);
		at= (TextView )findViewById(R.id.textView21);


	}
	protected void getImagesInBitmap(){
		for(int i=0;i<images.length;i++){
			denisityBitmap[i]=((BitmapDrawable) getResources().getDrawable(images[i])).getBitmap();
		}
	}  
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected void sdkCheck(){
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		denisityBitmap=ScreenDenesityCheck.checkScreenDensityAndResize(denisityBitmap,metrics,densityCheck,MainReminder.checkScreenSize(getApplicationContext()));

		if(!themeType.equals("purple")){
			denisityBitmap[2]=themeImages[1];
			denisityBitmap[4]=themeImages[3];
		}
		if(themeType.equals("white")){
			evrdy.setTextColor(Color.BLACK); 
			wekdy.setTextColor(Color.BLACK);  
			wekend.setTextColor(Color.BLACK);  
		}else{
			evrdy.setTextColor(Color.WHITE); 
			wekdy.setTextColor(Color.WHITE);  
			wekend.setTextColor(Color.WHITE);  
		}
		if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
			todo_top.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[0]));
			gray_textview.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[1]));
			close.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[2]));
			todo_center1.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[6]));
			todo_center2.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[6]));
			todo_center3.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[6]));
			todo_center4.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[6]));
			todo_bottom.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[7]));
			edit1.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[8]));
			edit2.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[8]));
			edit3.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[8]));
			yes_button.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[3]));
			check_button.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[9]));
			today.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[4]));
			tomorrow.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[5]));
			otherDay.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[5]));

		}	else {
			todo_top.setBackground(new BitmapDrawable(getResources(),denisityBitmap[0]));
			gray_textview.setBackground(new BitmapDrawable(getResources(),denisityBitmap[1]));
			close.setBackground(new BitmapDrawable(getResources(),denisityBitmap[2]));
			todo_center1.setBackground(new BitmapDrawable(getResources(),denisityBitmap[6]));
			todo_center2.setBackground(new BitmapDrawable(getResources(),denisityBitmap[6]));
			todo_center3.setBackground(new BitmapDrawable(getResources(),denisityBitmap[6]));
			todo_center4.setBackground(new BitmapDrawable(getResources(),denisityBitmap[6]));
			todo_bottom.setBackground(new BitmapDrawable(getResources(),denisityBitmap[7]));
			edit1.setBackground(new BitmapDrawable(getResources(),denisityBitmap[8]));
			edit2.setBackground(new BitmapDrawable(getResources(),denisityBitmap[8]));
			edit3.setBackground(new BitmapDrawable(getResources(),denisityBitmap[8]));
			yes_button.setBackground(new BitmapDrawable(getResources(),denisityBitmap[3]));
			check_button.setBackground(new BitmapDrawable(getResources(),denisityBitmap[9]));
			today.setBackground(new BitmapDrawable(getResources(),denisityBitmap[4]));
			tomorrow.setBackground(new BitmapDrawable(getResources(),denisityBitmap[5]));
			otherDay.setBackground(new BitmapDrawable(getResources(),denisityBitmap[5]));
		} 
	}
	protected void buttonClicked(){
		yes_button.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {

				if(checkButtonForAlarm){
					inserReminderToDataBase();
				}else{
					insertWithoutReminder();
				}
			}
		});
		close.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				finish();
				overridePendingTransition(R.anim.push_down_out, R.anim.push_down_out);
			}
		});
		todo_center2.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				Intent in = new Intent(ReminderToDo.this,ReminderMessage.class);
				in.putExtra("textEdit", message.getText().toString());
				in.putExtra("MessageFor", "to_do");
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

			}
		});
		todo_center3.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				Intent in = new Intent(ReminderToDo.this,CalendarView.class);
				in.putExtra("for_time","getDate");
				if(editCheck && edit_alarm_period1[0].equals("once")){
					in.putExtra("activity_date",edit_date );
				}else{
					in.putExtra("activity_date"," ");
				} 
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
				//			}

			}
		});
		todo_bottom.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {

				Intent in = new Intent(ReminderToDo.this,SelectTime.class);
				in.putExtra("for_time","getTime");
				in.putExtra("activity_time",time.getText().toString());
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

			}
		});
		today.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				alaramTypeImageChage(4,5,5);
				alarmTypeSeleted="everyday";
				date.setText("Everyday of the week");
				combineMessage(alarmTypeSeleted);
			}
		});
		tomorrow.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				alaramTypeImageChage(5,4,5); 
				alarmTypeSeleted="weekday";
				date.setText("Mon, Tue, Wed, Thu, Fri");
				combineMessage(alarmTypeSeleted);
			}
		});
		otherDay.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				alaramTypeImageChage(5,5,4); 
				date.setText(" Saturday, Sunday");
				alarmTypeSeleted="weekend";
				combineMessage(alarmTypeSeleted);
			}
		});
		check_button.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {

				if(checkButtonForAlarm){
					if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
						check_button.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[9]));
					}else{
						check_button.setBackground(new BitmapDrawable(getResources(),denisityBitmap[9]));
					}
					showHide(1);
					checkButtonForAlarm=false;
					combineMessage(" ");

				}else{
					if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
						check_button.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[10]));
					}else{
						check_button.setBackground(new BitmapDrawable(getResources(),denisityBitmap[10]));
					}
					showHide(0);
					checkButtonForAlarm=true;
					combineMessage(alarmTypeSeleted);
				}
			}
		});
		todo_center4.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {

				if(checkButtonForAlarm){
					if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
						check_button.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[9]));
					}else{
						check_button.setBackground(new BitmapDrawable(getResources(),denisityBitmap[9]));
					}
					showHide(1);
					checkButtonForAlarm=false;
					combineMessage(" ");

				}else{
					if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
						check_button.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[10]));
					}else{
						check_button.setBackground(new BitmapDrawable(getResources(),denisityBitmap[10]));
					}
					showHide(0);
					checkButtonForAlarm=true;
					combineMessage(alarmTypeSeleted);
				}
			}
		});
	}
	public void showHide(int visibility){ 
		if(visibility==0){
			gray_textview.setVisibility(View.VISIBLE);
			today.setVisibility(View.VISIBLE);
			tomorrow.setVisibility(View.VISIBLE);
			otherDay.setVisibility(View.VISIBLE);
			evrdy.setVisibility(View.VISIBLE);
			wekdy.setVisibility(View.VISIBLE);
			wekend.setVisibility(View.VISIBLE);
			todo_center1.setVisibility(View.VISIBLE);
			todo_center3.setVisibility(View.VISIBLE);
			on.setVisibility(View.VISIBLE);
			at.setVisibility(View.VISIBLE);
			date.setVisibility(View.VISIBLE);
			edit2.setVisibility(View.VISIBLE);
			time.setVisibility(View.VISIBLE);
			edit3.setVisibility(View.VISIBLE);
			todo_bottom.setVisibility(View.VISIBLE);
		}else{
			gray_textview.setVisibility(View.INVISIBLE);
			today.setVisibility(View.INVISIBLE);
			tomorrow.setVisibility(View.INVISIBLE);
			otherDay.setVisibility(View.INVISIBLE);
			evrdy.setVisibility(View.INVISIBLE);
			wekdy.setVisibility(View.INVISIBLE);
			wekend.setVisibility(View.INVISIBLE);
			todo_center1.setVisibility(View.INVISIBLE);
			todo_center3.setVisibility(View.INVISIBLE);
			on.setVisibility(View.INVISIBLE);
			at.setVisibility(View.INVISIBLE);
			date.setVisibility(View.INVISIBLE);
			edit2.setVisibility(View.INVISIBLE);
			time.setVisibility(View.INVISIBLE);
			edit3.setVisibility(View.INVISIBLE);
			todo_bottom.setVisibility(View.INVISIBLE);
		}

	}

	protected void combineMessage(String check){
		String combine;
		String upToNCharacters;
		if(!checkButtonForAlarm){
			combine="Reminder for "+message.getText().toString();
		}else if(check.equals("everyday")){
			combine="Everyday at "+time.getText().toString()+" for "+message.getText().toString();
		}else if(check.equals("once")){
			combine="On "+date.getText().toString()+" at "+time.getText().toString()+" for "+message.getText().toString();
		}else{
			combine="On "+check+" at "+time.getText().toString()+" for "+message.getText().toString();
		}
		//upToNCharacters = combine.substring(0, Math.min(combine.length(), 25));
		combine_msg.setText(combine);
	}
	protected void alaramTypeImageChage(int everyday1,int weekday1,int once1){
		if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
			today.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[everyday1]));
			tomorrow.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[weekday1]));
			otherDay.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[once1]));
		}else{
			today.setBackground(new BitmapDrawable(getResources(),denisityBitmap[everyday1]));
			tomorrow.setBackground(new BitmapDrawable(getResources(),denisityBitmap[weekday1]));
			otherDay.setBackground(new BitmapDrawable(getResources(),denisityBitmap[once1]));
		}


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
		analit.gAnalitics(getApplicationContext(),"Todo Page");

		/*==========================google Analitics Ends=============================*/
		reminder_date = remindMessagePref.getString("reminder_date", "");
		if(!reminder_date.equals("no")){
			alaramTypeImageChage(5,5,5);
			alarmTypeSeleted="once";
			date.setText(reminder_date);
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
			//String message1 = reminder_message.substring(0, Math.min(reminder_message.length(), 20));
			message.setText(reminder_message);
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
	public void message(String msg){
		Builder builder = new Builder(ReminderToDo.this);
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
	protected void insertWithoutReminder(){
		ourReminders=new DatabaseHandler(this);
		ourDatabase=ourReminders.openConnection(this);
		if(message.getText().toString().equals("Subject")){
			message("Please Write Subject.");
		}else if(editCheck){
			int id=Integer.parseInt(edit_id);
			cancleAlarms(id);
			ourReminders.updateReminder(id,"","",message.getText().toString(),edit_date,edit_time,"runnoAlarm","todo","runnoAlarm",edit_miliSec);
			Afinish();
		}else{
			try{
				Long currentMilisec=SelectTime.dateToString(current_year,current_month,current_day,current_hour,current_min);
				time.setText(SelectTime.CurrenttwelevFormatTime(SelectTime.timeAfterHour(),current_min));
				ourReminders.addReminders("","",message.getText().toString(),current_day+" "+st.getMonthInWords(current_month)+" "+current_year,SelectTime.CurrenttwelevFormatTime(current_hour,current_min),"runnoAlarm","todo","runnoAlarm",currentMilisec+"");
				Afinish();
			}catch(Exception e){
				System.out.println("fail to save in database");
			}
		}

		ourReminders.closeConnection();
	}
	public void inserReminderToDataBase(){
		try{
			long lastId;
			String [] splitDate=date.getText().toString().split(" ");
			Long milisec;
			String milidate;
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
			ourReminders=new DatabaseHandler(this);
			ourDatabase=ourReminders.openConnection(this);
			if(message.getText().toString().equals("Subject")){
				message("Please Write Subject.");
			}else if(alarmTypeSeleted.equals("once")){ //for once alarm
				milisec=SelectTime.dateToString(Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]),hour,min);
				milidate=milisec+"";
				milisec=SelectTime.dateToString(Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]),hour,min);
				milidate=milisec+"";
				long pevMili=Long.parseLong(milidate);
				if(currentMilisec>pevMili){
					message("Alarm Time Passed.");
				}else if(editCheck){
					int id=Integer.parseInt(edit_id);
					long id1=(long)id;
					ourReminders.updateReminder(id,"","",message.getText().toString(),date.getText().toString(),time.getText().toString(),alarmTypeSeleted,"todo","run",milidate);
					scheduleAlarm(id1,message.getText().toString(),Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
					Afinish();
				}else{
					lastId=ourReminders.addReminders("","",message.getText().toString(),date.getText().toString(),time.getText().toString(),alarmTypeSeleted,"todo","run",milidate);
					scheduleAlarm(lastId,message.getText().toString(),Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
					Afinish();
				}
			}else{
				final Calendar c = Calendar.getInstance();
				milisec=SelectTime.dateToString(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH),hour,min);
				milidate=milisec+"";
				long pevMili=Long.parseLong(milidate);

				if(editCheck){
					int id=Integer.parseInt(edit_id);
					long id1=(long)id;
					if(currentMilisec>pevMili){
						timePass=true;
						//	ourReminders.updateReminder(id,"","",message.getText().toString(),date.getText().toString(),time.getText().toString(),alarmTypeSeleted,"todo","done",milidate);	
					} 
					ourReminders.updateReminder(id,"","",message.getText().toString(),"",time.getText().toString(),alarmTypeSeleted,"todo","run",milidate);
					//	scheduleAlarm(id1,message.getText().toString(),Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
					scheduleAlarm(id1,message.getText().toString(),current_year,current_month,current_day);
				}else{

					if(currentMilisec>pevMili){
						timePass=true;
					}
					lastId=ourReminders.addReminders("","",message.getText().toString(),"",time.getText().toString(),alarmTypeSeleted,"todo","run",milidate);
					//scheduleAlarm(lastId,message.getText().toString(),Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
					scheduleAlarm(lastId,message.getText().toString(),current_year,current_month,current_day);
				}
				ourReminders.closeConnection();
				finish();
				overridePendingTransition(R.anim.push_down_out, R.anim.push_down_out);
			}
		}catch(Exception e){

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
		String time21[]=hString.split("_");
		hour=Integer.parseInt(time21[0]);
		min=Integer.parseInt(time21[1]);
		intentAlarm = new Intent(this, AlarmReciever.class);
		intentAlarm.putExtra("Message", "Todo");
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
		ourReminders=new DatabaseHandler(ReminderToDo.this);
		ourDatabase=ourReminders.openConnection(ReminderToDo.this);
		ourReminders.updateSnoozTime(requestCode1,calendar.getTimeInMillis()+"",newDate,newTime);
		ourReminders.closeConnection();
		long time = calendar.getTimeInMillis();
		alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,requestCode1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
	}
	@Override
	protected void onDestroy(){
		super.onDestroy(); 
		container_check=false;
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
	protected void cancleAlarms(int rId) {
		Intent intent = new Intent(ReminderToDo.this, AlarmReciever.class);
		intent = new Intent(ReminderToDo.this, AlarmReciever.class);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		GregorianCalendar calendar = new GregorianCalendar(2025,8,21,12,34);
		long time = calendar.getTimeInMillis();
		PendingIntent pendingIntent=PendingIntent.getBroadcast(ReminderToDo.this,rId,  intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,time,24*60*60*1000,pendingIntent );
		alarmManager.cancel(pendingIntent);
	}
}
