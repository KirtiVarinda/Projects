package com.remindme;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.remindme.RemindMeSession.SharedData;
import com.remindme.manage.*;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
@SuppressLint("NewApi")
public class AddEvent extends Activity {
	String mDatePresent="";
	SharedData shareData;
	ScheduledExecutorService scheduler1;
	TextView mthly,yrly,onc,bday,anvsry,oter,toFor;
	String themeType;
	Bitmap[] themeImages;
	String Action="call";
	TextView back,end,call,text;
	String reminder_contact_name="" ;
	String reminder_contact_number="";
	String reminder_week_day="Select day";
	String alrType="";
	boolean timePass=false;
	String edit_id ;
	String edit_miliSec;
	boolean editCheck=false;
	boolean subjectOrName=true;
	String hideText="";
	String showText="";
	Intent intentAlarm;
	int current_hour,current_min,current_year,current_month,current_day,nextDay,nextMonth,nextYear;
	String yearlyType="birthday";
	String alarmTypeSeleted="monthly";
	private DatabaseHandler ourReminders;
	private SQLiteDatabase ourDatabase;
	boolean typeCheck=false;
	SelectTime st;
	boolean name=false;
	String reminder_message;
	String reminder_time;
	String exact_reminder_time="no";
	SharedPreferences remindMessagePref;
	String reminder_date="no";
	String mCurrentDate="";
	TextView event_top,gray_textview,gray_textview2,close,event_center1,event_center2,event_center3,event_center4,event_center5,event_bottom,edit1,edit2,edit3,edit4,monthly,yearly,oneDay,birthday,anniversery,otherEvent ;
	TextView message,time,date,combine_msg;
	TextView hide1,hide2,hide3;
	Button yes_button;
	String  densityCheck="";
	View layout,layout1;
	boolean container_check=false;
	int sdk = android.os.Build.VERSION.SDK_INT;
	int[] images={R.drawable.apevent,R.drawable.menubg,R.drawable.close_reminder,R.drawable.yes_remind,R.drawable.hover_changebg,R.drawable.menu_bg
			,R.drawable.wakeup_center,R.drawable.wakeup_bottom,R.drawable.edit};
	Bitmap[] denisityBitmap=new Bitmap[images.length];
	ProgressBar pBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
		st=new SelectTime();
		pBar=(ProgressBar)findViewById(R.id.progressBar1);
		shareData=new SharedData(getApplicationContext());
		pBar.setVisibility(View.GONE);
		current_hour=st.mHour;
		current_min=st.mMinute;
		current_year=st.mYear;
		current_month=st.mMonth;
		current_day=st.mDay;
		remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
		themeType= remindMessagePref.getString("theme_selector", "");
		int size = remindMessagePref.getInt("theme_size", 0);
		String imageArray[] = new String[size];
		for(int i=0;i<size;i++){
			imageArray[i] = remindMessagePref.getString("stringImage_"+i, "");
		}
		themeImages=new Bitmap[size];
		themeImages=Theme.decodeBase64(imageArray);
		layout = findViewById(R.id.layout1);
		layout1 = findViewById(R.id.textView8);
		checkScreenDensity();
		getTextViewImages();
		getImagesInBitmap();
		sdkCheck();
		buttonClicked();
		Bundle extras =  getIntent().getExtras();
		mCurrentDate=current_day+" "+st.getMonthInWords(current_month)+" "+current_year;
		if(extras!=null){
			editModule(extras);
		}else{
			mDatePresent=mCurrentDate;
			time.setText(SelectTime.CurrenttwelevFormatTime(SelectTime.timeAfterHour(),current_min));
			date.setText(mDatePresent);
		}
		combineMessage();
	}
	protected void syncContactMessage(){
		remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
		String contact = remindMessagePref.getString("key_contact", null);
		if(contact==null){
			if(alarmTypeSeleted.equals("Yearly")){
				pBar.setVisibility(View.VISIBLE);
				message.setText("Synchronizing Contacts...");
			}
		}else{
			pBar.setVisibility(View.GONE);
			if(!editCheck){
				message.setText("Select Contact");
			}
			combineMessage();

			scheduler1.shutdown();

		}
	}
	protected void editModule(Bundle extras){
		editCheck=true;
		edit_id = extras.getString("edit_id");
		String edit_names = extras.getString("edit_names");
		String edit_recipient = extras.getString("edit_recipient");
		String edit_listSubject = extras.getString("edit_listSubject");
		String edit_date = extras.getString("edit_date");
		String edit_time = extras.getString("edit_time");
		exact_reminder_time=SelectTime.time12To24(edit_time);
		String edit_alarm_period = extras.getString("edit_alarm_period");
		String edit_alarmType = extras.getString("edit_alarmType");
		String edit_snoozDone = extras.getString("edit_snoozDone");
		edit_miliSec = extras.getString("edit_miliSec");
		message.setText(edit_listSubject);
		time.setText(edit_time);
		mDatePresent=edit_date;
		date.setText(edit_date);
		String [] edit_alarm_period1=edit_alarm_period.toString().split("_");
		showText=edit_listSubject;
		if(edit_alarm_period1[0].equals("monthly")){
			subjectOrName=true;
			message.setText(edit_listSubject);
			hideText="";
			pBar.setVisibility(View.GONE);
			toFor.setText("For:");
			hide2.setText("Write Event");
			alarmTypeSeleted="monthly";
			alaramTypeImageChage(4,5,5);
			layout1.setVisibility(View.GONE);
			hideCheckForOther("yes");
		}else if(edit_alarm_period1[0].equals("Yearly")){
			reminder_contact_name=edit_names;
			reminder_contact_number=edit_recipient;
			toFor.setText("To:");
			hideText=edit_alarm_period1[1];

			if(edit_alarm_period1[1].equals("birthday")){
				typeCheck=false;
			}else if(edit_alarm_period1[1].equals("anniversery")){
				typeCheck=false;
			}else if(edit_alarm_period1[1].equals("otherTime")){
				typeCheck=true;
			}
			subjectOrName=false;
			message.setText(edit_names);
			alarmTypeSeleted="Yearly";
			//	syncContactMessage();
			scheduler1 = Executors.newSingleThreadScheduledExecutor();
			scheduler1.scheduleAtFixedRate(new Runnable() {
				public void run() {

					AddEvent.this.runOnUiThread(new Runnable(){
						@Override
						public void run(){
							syncContactMessage();
						}
					});
				}
			}, 0, 5, TimeUnit.SECONDS);
			alaramTypeImageChage(5,4,5);
			layout1.setVisibility(View.VISIBLE);
			if(typeCheck){
				hideCheckForOther("no");
			}
			if(edit_alarm_period1[1].equals("birthday")){
				//	message.setText(edit_listSubject);
				hideText="";
				hide2.setText("Write Event");
				alaramTypeImageChage2(4,5,5);
				typeCheck=false;
				hideCheckForOther("yes");
				yearlyType="birthday";
				syncContactMessage();
			}else if(edit_alarm_period1[1].equals("anniversery")){
				//	message.setText(edit_listSubject);
				hideText="";
				hide2.setText("Write Event");
				alaramTypeImageChage2(5,4,5);
				typeCheck=false;
				hideCheckForOther("yes");
				yearlyType="anniversery";
				syncContactMessage();
			}else {
				//	message.setText(edit_listSubject);
				alaramTypeImageChage2(5,5,4);
				typeCheck=true;
				hide2.setText(edit_alarm_period1[1]);
				hideCheckForOther("no");
				yearlyType="otherTime";
				syncContactMessage();
			}
			actionImages(1);
			if(edit_alarm_period1[2].equals("call")){
				Action="call";
				imageChange(4,5);
			}else if(edit_alarm_period1[2].equals("text")){
				Action="text";
				imageChange(5,4);
			}
		}else if(edit_alarm_period1[0].equals("weekly")){
			pBar.setVisibility(View.GONE);
			toFor.setText("For:");
			subjectOrName=true;
			message.setText(edit_listSubject);
			hideText="";
			hide2.setText("Write Event");
			alarmTypeSeleted="weekly";
			alaramTypeImageChage(5,5,4);
			layout1.setVisibility(View.GONE);
			hideCheckForOther("yes");
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
				//	message.setText("Jogging");
				finish();
				overridePendingTransition(R.anim.push_down_out, R.anim.push_down_out);
			}
		});
		event_center5.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {

				if(alarmTypeSeleted.equals("weekly")){
					Intent intent = new Intent(AddEvent.this,SelectWeekDay.class);
					intent.putExtra("week_day_selected",reminder_week_day);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
				}else{
					Intent in = new Intent(AddEvent.this,CalendarView.class);
					in.putExtra("for_time","getDate");
					in.putExtra("activity_date",date.getText().toString());
					startActivity(in);
					overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
				}



			}
		});
		event_bottom.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {

				Intent in = new Intent(AddEvent.this,SelectTime.class);
				in.putExtra("for_time","getTime");
				in.putExtra("activity_time",time.getText().toString());
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
			}
		});
		event_center4.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				name=true;


				if( subjectOrName){
					Intent in = new Intent(AddEvent.this,ReminderMessage.class);
					in.putExtra("MessageFor", "Event_sub");
					in.putExtra("textEdit", message.getText().toString());
					startActivity(in);

				}else{

					remindMessagePref = getSharedPreferences("remindMessagePref", 0);
					String contact = remindMessagePref.getString("key_contact", null);
					String name = remindMessagePref.getString("key_contactName", null);
					if(contact!=null && name!=null){
						Intent in = new Intent(AddEvent.this,SelectContact.class);
						in.putExtra("MessageForcontact", "call");
						startActivity(in);
						overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
					}else{
						SelectTime.showToast("Please wait while Synchronizing Contacts.",getApplicationContext());
					}


				}
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
			}
		});
		event_center3.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				name=false;
				Intent in = new Intent(AddEvent.this,ReminderMessage.class);
				in.putExtra("MessageFor", "Event_Type");
				in.putExtra("textEdit", hide2.getText().toString());
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
			}
		});
		monthly.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {

				pBar.setVisibility(View.GONE);
				toFor.setText("For:");
				subjectOrName=true;
				message.setText("Subject");
				hideText="";
				hide2.setText("Write Event");
				alarmTypeSeleted="monthly";
				date.setText(mCurrentDate);
				alaramTypeImageChage(4,5,5);
				layout1.setVisibility(View.GONE);
				hideCheckForOther("yes");
				combineMessage();
				actionImages(0);
			}
		});
		yearly.setOnClickListener( new OnClickListener(){

			public void onClick(View viewParam) {
				toFor.setText("To:");
				subjectOrName=false;
				scheduler1 = Executors.newSingleThreadScheduledExecutor();
				alarmTypeSeleted="Yearly";
				date.setText(mCurrentDate);
				//syncContactMessage();
				scheduler1.scheduleAtFixedRate(new Runnable() {
					public void run() {

						AddEvent.this.runOnUiThread(new Runnable(){
							@Override
							public void run(){
								syncContactMessage();
							}
						});
					}
				}, 0, 5, TimeUnit.SECONDS);

				alaramTypeImageChage(5,4,5);
				layout1.setVisibility(View.VISIBLE);
				actionImages(1);
				if(typeCheck){

					hideCheckForOther("no");
				}
				combineMessage();
			}
		});
		oneDay.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				pBar.setVisibility(View.GONE);
				toFor.setText("For:");
				if(reminder_week_day.equals("no")){
					date.setText("Select day");
				}else{
					date.setText(reminder_week_day);

				}

				subjectOrName=true;
				message.setText("Subject");
				hideText="";
				hide2.setText("Write Event");
				alarmTypeSeleted="weekly";
				alaramTypeImageChage(5,5,4);
				layout1.setVisibility(View.GONE);
				hideCheckForOther("yes");
				combineMessage();
				actionImages(0);
			}
		});
		birthday.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				message.setText("Select Contact");
				syncContactMessage();
				hideText="";
				hide2.setText("Write Event");
				alaramTypeImageChage2(4,5,5);
				typeCheck=false;
				hideCheckForOther("yes");
				yearlyType="birthday";
				combineMessage();
			}
		});
		anniversery.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				message.setText("Select Contact");
				syncContactMessage();
				hideText="";
				hide2.setText("Write Event");
				alaramTypeImageChage2(5,4,5);
				typeCheck=false;
				hideCheckForOther("yes");
				yearlyType="anniversery";
				combineMessage();
			}
		});
		otherEvent.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				message.setText("Select Contact");
				syncContactMessage();
				alaramTypeImageChage2(5,5,4);
				typeCheck=true;
				hideCheckForOther("no");
				yearlyType="otherTime";
				combineMessage();
			}
		});


		call.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				imageChange(4,5);
				Action="call";
			}
		});
		text.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				imageChange(5,4);
				Action="text";
			}
		});


	}
	protected void actionImages(int a){
		if(a==1){
			back.setVisibility(View.VISIBLE);
			end.setVisibility(View.VISIBLE);
			call.setVisibility(View.VISIBLE);
			text.setVisibility(View.VISIBLE);
		}else{
			back.setVisibility(View.INVISIBLE);
			end.setVisibility(View.INVISIBLE);
			call.setVisibility(View.INVISIBLE);
			text.setVisibility(View.INVISIBLE);
		}
	}
	@SuppressLint("NewApi")
	public void imageChange(int c,int t){
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			call.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[c]));
			text.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[t]));
		}else{
			call.setBackground(new BitmapDrawable(getResources(),denisityBitmap[c]));
			text.setBackground(new BitmapDrawable(getResources(),denisityBitmap[t]));
		}
	}
	protected void combineMessage(){
		String combine = "";
		if(alarmTypeSeleted.equals("monthly")){
			combine=message.getText().toString()+" at "+time.getText().toString()+" on "+date.getText().toString();
		}else if(alarmTypeSeleted.equals("weekly")){
			combine="Every "+date.getText().toString()+" at "+time.getText().toString();
			//combine=message.getText().toString()+" at "+time.getText().toString()+" on "+date.getText().toString();
		}else if(alarmTypeSeleted.equals("Yearly")){
			if(yearlyType.equals("birthday")){
				combine=message.getText().toString()+"'s"+" Birthday at "+time.getText().toString()+" on "+date.getText().toString();
			}else if(yearlyType.equals("anniversery")){
				combine=message.getText().toString()+"'s"+" Anniversery at "+time.getText().toString()+" on "+date.getText().toString();
			}else if(yearlyType.equals("otherTime")){
				combine=message.getText().toString()+"'s "+hide2.getText().toString()+" at "+time.getText().toString()+" on "+date.getText().toString();
			}
		}
		combine_msg.setText(combine);
	}
	public void hideCheckForOther(String hide){
		LayoutParams lp = (LayoutParams) event_center3.getLayoutParams();
		if(hide.equals("no")){
			lp.height = 70;
			event_center3.setLayoutParams(lp);
			hide1.setVisibility(View.VISIBLE);
			hide2.setVisibility(View.VISIBLE);
			hide3.setVisibility(View.VISIBLE);
		}else{
			lp.height = 0;
			event_center3.setLayoutParams(lp);
			event_center3.setHeight(0);
			hide1.setVisibility(View.GONE);
			hide2.setVisibility(View.GONE);
			hide3.setVisibility(View.GONE);
		}
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected void alaramTypeImageChage(int everyday1,int weekday1,int once1){
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			monthly.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[everyday1]));
			yearly.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[weekday1]));
			oneDay.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[once1]));
		}else{
			monthly.setBackground(new BitmapDrawable(getResources(),denisityBitmap[everyday1]));
			yearly.setBackground(new BitmapDrawable(getResources(),denisityBitmap[weekday1]));
			oneDay.setBackground(new BitmapDrawable(getResources(),denisityBitmap[once1]));
		}
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected void alaramTypeImageChage2(int everyday1,int weekday1,int once1){
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			birthday.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[everyday1]));
			anniversery.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[weekday1]));
			otherEvent.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[once1]));
		}else{
			birthday.setBackground(new BitmapDrawable(getResources(),denisityBitmap[everyday1]));
			anniversery.setBackground(new BitmapDrawable(getResources(),denisityBitmap[weekday1]));
			otherEvent.setBackground(new BitmapDrawable(getResources(),denisityBitmap[once1]));
		}
	}
	protected void getTextViewImages(){
		event_top = (TextView )findViewById(R.id.textView2);
		gray_textview = (TextView )findViewById(R.id.textView3);
		gray_textview2 = (TextView )findViewById(R.id.textView9);
		close = (TextView )findViewById(R.id.textView11);
		event_center1 = (TextView )findViewById(R.id.textView4);
		event_center2 = (TextView )findViewById(R.id.textView10);
		event_center3 = (TextView )findViewById(R.id.textView22);
		event_center4 = (TextView )findViewById(R.id.textView23);
		event_center5 = (TextView )findViewById(R.id.textView24);
		event_bottom = (TextView )findViewById(R.id.textView25);
		edit1 = (TextView )findViewById(R.id.textView35);
		edit2 = (TextView )findViewById(R.id.textView26);
		edit3 = (TextView )findViewById(R.id.textView27);
		edit4 = (TextView )findViewById(R.id.textView28);
		yes_button = (Button) findViewById(R.id.button1);
		monthly = (TextView )findViewById(R.id.textView5);
		yearly = (TextView )findViewById(R.id.textView6);
		oneDay = (TextView )findViewById(R.id.textView7);
		birthday = (TextView )findViewById(R.id.textView12);
		anniversery = (TextView )findViewById(R.id.textView13);
		otherEvent = (TextView )findViewById(R.id.textView14);
		hide1 = (TextView )findViewById(R.id.textView36);
		hide2 = (TextView )findViewById(R.id.textView37);
		hide3 = (TextView )findViewById(R.id.textView35);
		date = (TextView )findViewById(R.id.textView33);
		time = (TextView )findViewById(R.id.textView34);
		message = (TextView )findViewById(R.id.textView30);
		combine_msg = (TextView )findViewById(R.id.textView15);
		back = (TextView )findViewById(R.id.textView38);
		end= (TextView )findViewById(R.id.textView1);
		call = (TextView )findViewById(R.id.textView39);
		text= (TextView )findViewById(R.id.textView41);
		mthly= (TextView )findViewById(R.id.textView16);
		yrly= (TextView )findViewById(R.id.textView17);
		toFor= (TextView )findViewById(R.id.textView29);
		onc= (TextView )findViewById(R.id.textView18);
		bday= (TextView )findViewById(R.id.textView19);
		anvsry= (TextView )findViewById(R.id.textView20);
		oter= (TextView )findViewById(R.id.textView21);
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
			call.setTextColor(Color.BLACK);
			text.setTextColor(Color.BLACK);

			mthly.setTextColor(Color.BLACK);
			yrly.setTextColor(Color.BLACK);
			onc.setTextColor(Color.BLACK);
			bday.setTextColor(Color.BLACK);
			anvsry.setTextColor(Color.BLACK);
			oter.setTextColor(Color.BLACK);
		}else{
			call.setTextColor(Color.WHITE);
			text.setTextColor(Color.WHITE);

			mthly.setTextColor(Color.WHITE);
			yrly.setTextColor(Color.WHITE);
			onc.setTextColor(Color.WHITE);
			bday.setTextColor(Color.WHITE);
			anvsry.setTextColor(Color.WHITE);
			oter.setTextColor(Color.WHITE);

		}
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			event_top.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[0]));
			gray_textview.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[1]));
			gray_textview2.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[1]));
			close.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[2]));
			event_center1.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[6]));
			event_center2.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[6]));
			event_center3.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[6]));
			event_center4.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[6]));
			event_center5.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[6]));
			event_bottom.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[6]));
			yes_button.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[3]));
			edit1.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[8]));
			edit2.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[8]));
			edit3.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[8]));
			edit4.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[8]));
			monthly.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[4]));
			yearly.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[5]));
			oneDay.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[5]));
			birthday.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[4]));
			anniversery.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[5]));
			otherEvent.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[5]));
			back.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[1]));
			end.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[7]));
			call.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[4]));
			text.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[5]));

		}	else {
			event_top.setBackground(new BitmapDrawable(getResources(),denisityBitmap[0]));
			gray_textview.setBackground(new BitmapDrawable(getResources(),denisityBitmap[1]));
			gray_textview2.setBackground(new BitmapDrawable(getResources(),denisityBitmap[1]));
			close.setBackground(new BitmapDrawable(getResources(),denisityBitmap[2]));
			event_center1.setBackground(new BitmapDrawable(getResources(),denisityBitmap[6]));
			event_center2.setBackground(new BitmapDrawable(getResources(),denisityBitmap[6]));
			event_center3.setBackground(new BitmapDrawable(getResources(),denisityBitmap[6]));
			event_center4.setBackground(new BitmapDrawable(getResources(),denisityBitmap[6]));
			event_center5.setBackground(new BitmapDrawable(getResources(),denisityBitmap[6]));
			event_bottom.setBackground(new BitmapDrawable(getResources(),denisityBitmap[6]));
			yes_button.setBackground(new BitmapDrawable(getResources(),denisityBitmap[3]));
			edit1.setBackground(new BitmapDrawable(getResources(),denisityBitmap[8]));
			edit2.setBackground(new BitmapDrawable(getResources(),denisityBitmap[8]));
			edit3.setBackground(new BitmapDrawable(getResources(),denisityBitmap[8]));
			edit4.setBackground(new BitmapDrawable(getResources(),denisityBitmap[8]));
			monthly.setBackground(new BitmapDrawable(getResources(),denisityBitmap[4]));
			yearly.setBackground(new BitmapDrawable(getResources(),denisityBitmap[5]));
			oneDay.setBackground(new BitmapDrawable(getResources(),denisityBitmap[5]));
			birthday.setBackground(new BitmapDrawable(getResources(),denisityBitmap[4]));
			anniversery.setBackground(new BitmapDrawable(getResources(),denisityBitmap[5]));
			otherEvent.setBackground(new BitmapDrawable(getResources(),denisityBitmap[5]));
			back.setBackground(new BitmapDrawable(getResources(),denisityBitmap[1]));
			end.setBackground(new BitmapDrawable(getResources(),denisityBitmap[7]));
			call.setBackground(new BitmapDrawable(getResources(),denisityBitmap[4]));
			text.setBackground(new BitmapDrawable(getResources(),denisityBitmap[5]));
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
		analit.gAnalitics(getApplicationContext(),"Event Page");

		/*==========================google Analitics Ends=============================*/
		String reminder_contact = remindMessagePref.getString("reminder_contact", "");
		if(!reminder_contact.equals("no")){
			String combine_contact = remindMessagePref.getString("combine_contact", "");
			reminder_contact_name = remindMessagePref.getString("reminder_contact_name", "");
			reminder_contact_number = remindMessagePref.getString("reminder_contact_number", "");
			message.setText(combine_contact);
			combineMessage();
			SharedPreferences.Editor editor = remindMessagePref.edit();
			editor.putString("reminder_contact","no");
			editor.putString("combine_contact","no");
			editor.putString("reminder_contact_name","no");
			editor.putString("reminder_contact_number","no");
			editor.commit();
		}

		reminder_week_day = shareData.getGeneralSaveSession(SharedData.SELECTEDWEEKDAY);
		if(!reminder_week_day.equals("no")){
			date.setText(reminder_week_day);
			combineMessage();
			shareData.setGeneralSaveSession(SharedData.SELECTEDWEEKDAY, "no");

		}

		reminder_date = remindMessagePref.getString("reminder_date", "");
		if(!reminder_date.equals("no")){
			mCurrentDate=reminder_date;
			date.setText(reminder_date);
			combineMessage();
			SharedPreferences.Editor editor = remindMessagePref.edit();
			editor.putString("reminder_date","no");
			editor.commit();


		}
		reminder_time = remindMessagePref.getString("reminder_time", "");
		if(!reminder_time.equals("no")){
			exact_reminder_time = remindMessagePref.getString("exact_reminder_time","");
			time.setText(reminder_time);
			combineMessage();
			SharedPreferences.Editor editor = remindMessagePref.edit();
			editor.putString("reminder_time","no");
			editor.putString("exact_reminder_time","no");
			editor.commit();

		}
		reminder_message = remindMessagePref.getString("reminder_message", "");
		if(!reminder_message.equals("no")){
			if(name){
				showText=reminder_message;
				message.setText(reminder_message);
			}else{
				hideText=reminder_message;
				hide2.setText(reminder_message);
			}
			combineMessage();
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
		AlertDialog.Builder builder = new Builder(AddEvent.this);
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


	String [] splitDate;
	public void inserReminderToDataBase(){
		try{
			long lastId;

			if(alarmTypeSeleted.equals("weekly")){
				String weekDayName=date.getText().toString();
				int num=WeekDayManage.getSelectedDay(weekDayName);
				String mDate=WeekDayManage.timeForDaySelected(num);
				reminder_date=mDate;
				splitDate=mDate.split(" ");
			}else{
				splitDate=date.getText().toString().split(" ");
			}
			String time1[]=time.getText().toString().split(" ");
			String time2[]=time1[0].split(":");
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
			Long milisec;
			String milidate;
			ourReminders=new DatabaseHandler(this);
			ourDatabase=ourReminders.openConnection(this);
			//insert record
			if(!message.getText().toString().equals("Subject") && !message.getText().toString().equals("Select Contact")){
				if(alarmTypeSeleted.equals("weekly")){
					milisec=SelectTime.dateToString(Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]),hour,min);
					milidate=milisec+"";
					long pevMili=Long.parseLong(milidate);
					/*	if(currentMilisec>pevMili){
						message("Alarm Time Passed.");
					}else*/
					if(editCheck){
						int id=Integer.parseInt(edit_id);
						long id1=(long)id;
						if(currentMilisec>pevMili){
							timePass=true;
							alrType="weekly";
						}
						ourReminders.updateReminder(id,"","",showText,date.getText().toString(),time.getText().toString(),alarmTypeSeleted,"event","run",milidate);
						scheduleAlarm(id1,message.getText().toString(),Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
						Afinish();

					}else{
						if(currentMilisec>pevMili){
							timePass=true;
							alrType="weekly";
						}
						lastId=ourReminders.addReminders("","",showText,date.getText().toString(),time.getText().toString(),alarmTypeSeleted,"event","run",milidate);
						scheduleAlarm(lastId,showText,Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
						Afinish();
					}
				}else{


					if(yearlyType.equals("otherTime") && alarmTypeSeleted.equals("Yearly")){
						if(!hide2.getText().toString().equals("Write Event")){
							final Calendar c = Calendar.getInstance();
							milisec=SelectTime.dateToString(Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]),hour,min);
							milidate=milisec+"";
							long pevMili=Long.parseLong(milidate);
							if(editCheck){
								int id=Integer.parseInt(edit_id);
								long id1=(long)id;
								if(currentMilisec>pevMili){
									timePass=true;
									alrType="year";
								}
								ourReminders.updateReminder(id,reminder_contact_name,reminder_contact_number,showText,date.getText().toString(),time.getText().toString(),alarmTypeSeleted+"_"+hideText+"_"+Action,"event","run",milidate);

								scheduleAlarm(id1,message.getText().toString(),Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
								Afinish();

							}else{
								if(currentMilisec>pevMili){
									timePass=true;
									alrType="year";
								}
								lastId=ourReminders.addReminders(reminder_contact_name,reminder_contact_number,showText,date.getText().toString(),time.getText().toString(),alarmTypeSeleted+"_"+hideText+"_"+Action,"event","run",milidate);
								scheduleAlarm(lastId,showText,Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
								Afinish();
							}
						}else{
							message("Please Write Event.");
						}
					}else if(alarmTypeSeleted.equals("monthly")){
						final Calendar c = Calendar.getInstance();
						milisec=SelectTime.dateToString(Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]),hour,min);
						milidate=milisec+"";
						long pevMili=Long.parseLong(milidate);

						if(editCheck){
							if(currentMilisec>pevMili){
								timePass=true;
								alrType="month";
							}
							int id=Integer.parseInt(edit_id);
							long id1=(long)id;
							ourReminders.updateReminder(id,"","",showText,date.getText().toString(),time.getText().toString(),alarmTypeSeleted,"event","run",milidate);
							scheduleAlarm(id1,message.getText().toString(),Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
							Afinish();
						}else{
							if(currentMilisec>pevMili){
								timePass=true;
								alrType="month";
							}
							lastId=ourReminders.addReminders("","",showText,date.getText().toString(),time.getText().toString(),alarmTypeSeleted,"event","run",milidate);
							scheduleAlarm(lastId,showText,Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
							Afinish();
						}
					}else{
						final Calendar c = Calendar.getInstance();
						milisec=SelectTime.dateToString(Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]),hour,min);
						milidate=milisec+"";
						long pevMili=Long.parseLong(milidate);
						if(editCheck){
							if(currentMilisec>pevMili){
								timePass=true;
								alrType="year";
							}
							int id=Integer.parseInt(edit_id);
							long id1=(long)id;
							ourReminders.updateReminder(id,reminder_contact_name,reminder_contact_number,showText,date.getText().toString(),time.getText().toString(),alarmTypeSeleted+"_"+yearlyType+"_"+Action,"event","run",milidate);
							scheduleAlarm(id1,message.getText().toString(),Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
							Afinish();

						}else{
							if(currentMilisec>pevMili){
								timePass=true;
								alrType="year";
							}
							lastId=ourReminders.addReminders(reminder_contact_name,reminder_contact_number,showText,date.getText().toString(),time.getText().toString(),alarmTypeSeleted+"_"+yearlyType+"_"+Action,"event","run",milidate);
							scheduleAlarm(lastId,showText,Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
							Afinish();
						}
					}
				}
			}else {
				if(alarmTypeSeleted.equals("Yearly")){
					message("Please Select Contact");
				}else{
					message("Please Write Subject.");
				}

			}
			ourReminders.closeConnection();
		}catch(Exception e){
			//	didItWork=false;
			System.out.println("fail to save in database"+e);
		}
	}
	protected void Afinish(){
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
		intentAlarm.putExtra("Message", "Event");
		intentAlarm.putExtra("Id", requestCode);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		GregorianCalendar calendar = new GregorianCalendar(year,month,day,hour,min);
		if(exact_reminder_time.equals("no")&& timePass && reminder_date.equals("no")){
			long edit_miliSec1=Long.parseLong(edit_miliSec);
			calendar.setTimeInMillis(edit_miliSec1);
		}
		int requestCode1=(int)requestCode;
		if(timePass){
			SelectTime st=new SelectTime();
			Long currentMilisec=SelectTime.dateToString(st.mYear,st.mMonth,st.mDay,st.mHour,st.mMinute);
			while(calendar.getTimeInMillis()<currentMilisec){
				if(alrType.equals("year")){
					calendar.add(Calendar.YEAR, 1);
				}else if(alrType.equals("month")){
					calendar.add(Calendar.MONTH, 1);
				}else if(alrType.equals("weekly")){
					calendar.add(Calendar.WEEK_OF_MONTH, 1);
				}
			}

			timePass=false;

		}
		String newDate=SelectTime.milisecToDate(calendar.getTimeInMillis());
		String newTime=SelectTime.milisecToTime(calendar.getTimeInMillis());
		if(alarmTypeSeleted.equals("weekly")){
			ourReminders.updateSnoozTime(requestCode1,calendar.getTimeInMillis()+"",date.getText().toString(),newTime);
		}else{
			ourReminders.updateSnoozTime(requestCode1,calendar.getTimeInMillis()+"",newDate,newTime);
		}

		SelectTime.milisecToDate(calendar.getTimeInMillis());
		long time = calendar.getTimeInMillis();

		alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,requestCode1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

	}
	/*public void showToast(String str){
		Toast tost=Toast.makeText(this, str,Toast.LENGTH_LONG);
		tost.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
		tost.show();
	}*/
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
}