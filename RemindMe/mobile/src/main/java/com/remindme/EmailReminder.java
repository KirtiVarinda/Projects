package com.remindme;

import java.util.GregorianCalendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
public class EmailReminder extends Activity {
	ScheduledExecutorService scheduler;
	String themeType;
	Bitmap[] themeImages;
	String edit_id ;
	String edit_miliSec;
	boolean editCheck=false;
	String  densityCheck="";
	String largeScrenn="" ;	
	Intent intentAlarm;
	String exact_reminder_time="no";
	boolean didItWork=true;
	int current_hour,current_min,current_year,current_month,current_day;
	private DatabaseHandler ourReminders;
	private SQLiteDatabase ourDatabase;
	String reminder_contact_name ;
	String reminder_contact_number;
	String reminder_time;
	TextView time;
	TextView name;
	TextView combine;
	TextView message;
	TextView date;
	String reminder_message;
	String reminder_date;
	SharedPreferences remindMessagePref;
	View layout;
	boolean container_check=false;
	int[] images={R.drawable.email_top,R.drawable.close_reminder,R.drawable.yes_remind,R.drawable.wakeup_center,R.drawable.edit,R.drawable.wakeup_bottom};
	Bitmap[] denisityBitmap=new Bitmap[images.length];
	TextView message_top,close,message_center1,message_center2,message_center3,message_bottom,edit1,edit2,edit3,edit4 ;
	Button yes_button;
	int sdk = Build.VERSION.SDK_INT;
	ProgressBar pBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email_reminder);
			
		layout = findViewById(R.id.layout1);
		pBar=(ProgressBar)findViewById(R.id.progressBar1);
		checkScreenSize();
		checkScreenDensity();
		SelectTime st=new SelectTime();
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
		getTextViewImages();
		date.setText(current_day+" "+st.getMonthInWords(current_month)+" "+current_year);
		time.setText(SelectTime.CurrenttwelevFormatTime(current_hour,current_min));
		buttonClicked();
		getImagesInBitmap();
		sdkCheck();



		Bundle extras =  getIntent().getExtras();
		if(extras!=null){
			editModule(extras);
		}else{
			date.setText(current_day+" "+st.getMonthInWords(current_month)+" "+current_year);
			time.setText(SelectTime.CurrenttwelevFormatTime(SelectTime.timeAfterHour(),current_min));
		}
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				EmailReminder.this.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						syncContactMessage();
					}  
				});
			}
		}, 0, 5, TimeUnit.SECONDS); 
		combineMessage();
	}

	protected void syncContactMessage(){
		remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
		String email23 = remindMessagePref.getString("key_email", null);
		if(email23==null){
			pBar.setVisibility(View.VISIBLE);
			name.setText("Synchronizing Contacts...");
		}else{
			pBar.setVisibility(View.GONE);
			if(!editCheck){
				name.setText("Select Contact");
			}
			combineMessage();

			scheduler.shutdown();


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
		time.setText(edit_time);
		date.setText(edit_date);
		message.setText(edit_listSubject);
		name.setText(edit_names);
		reminder_contact_name=edit_names;
		reminder_contact_number=edit_recipient;
	}
	protected void buttonClicked(){
		yes_button.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				inserReminderToDataBase();
			}
		});
		close.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				finish();
				overridePendingTransition(R.anim.push_down_out, R.anim.push_down_out);
			}
		});
		message_center2.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				Intent in = new Intent(EmailReminder.this,ReminderMessage.class);
				in.putExtra("MessageFor", "email_message");
				in.putExtra("textEdit", message.getText().toString());
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

			}
		});
		message_center1.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {

				remindMessagePref = getSharedPreferences("remindMessagePref", 0);
				String email23 = remindMessagePref.getString("key_email", null);
				String name23 = remindMessagePref.getString("key_name", null);
				if(email23!=null && name23!=null){
					Intent in = new Intent(EmailReminder.this,SelectContact.class);
					in.putExtra("MessageForcontact", "email");
					startActivity(in);
					overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
				}else{
					SelectTime.showToast("Please wait while Synchronizing Contacts.",getApplicationContext());
				}



			}
		});
		message_center3.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				Intent in = new Intent(EmailReminder.this,CalendarView.class);
				in.putExtra("for_time","getDate");
				in.putExtra("activity_date",date.getText().toString());
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
			}
		});
		message_bottom.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				Intent in = new Intent(EmailReminder.this,SelectTime.class);
				in.putExtra("for_time","getTime");
				in.putExtra("activity_time",time.getText().toString());
				startActivity(in);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
			}
		});
	}
	protected void combineMessage(){
		String combine1;
		combine1="Email "+name.getText().toString()+" for "+message.getText().toString();
		combine.setText(combine1);
	}
	protected void getTextViewImages(){
		message_top = (TextView )findViewById(R.id.textView2);
		close = (TextView )findViewById(R.id.textView1);
		message_center1 = (TextView )findViewById(R.id.textView5);
		message_center2 = (TextView )findViewById(R.id.textView6);
		message_center3 = (TextView )findViewById(R.id.textView7);
		message_bottom = (TextView )findViewById(R.id.textView11);
		edit1 = (TextView )findViewById(R.id.textView4);
		edit2 = (TextView )findViewById(R.id.textView12);
		edit3 = (TextView )findViewById(R.id.textView23);
		edit4 = (TextView )findViewById(R.id.textView24);
		yes_button = (Button) findViewById(R.id.button1);
		message = (TextView )findViewById(R.id.textView3);
		name = (TextView )findViewById(R.id.textView14);
		combine = (TextView )findViewById(R.id.textView10);
		time = (TextView )findViewById(R.id.textView22);
		date = (TextView )findViewById(R.id.textView21);

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
			denisityBitmap[1]=themeImages[1];
		}
		if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
			message_top.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[0]));
			close.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[1]));
			message_center1.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[3]));
			message_center2.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[3]));
			message_center3.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[3]));
			message_bottom.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[5]));
			edit1.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[4]));
			edit2.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[4]));
			edit3.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[4]));
			edit4.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[4]));
			yes_button.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[2]));

		}	 else {
			message_top.setBackground(new BitmapDrawable(getResources(),denisityBitmap[0]));
			close.setBackground(new BitmapDrawable(getResources(),denisityBitmap[1]));
			message_center1.setBackground(new BitmapDrawable(getResources(),denisityBitmap[3]));
			message_center2.setBackground(new BitmapDrawable(getResources(),denisityBitmap[3]));
			message_center3.setBackground(new BitmapDrawable(getResources(),denisityBitmap[3]));
			message_bottom.setBackground(new BitmapDrawable(getResources(),denisityBitmap[5]));
			edit1.setBackground(new BitmapDrawable(getResources(),denisityBitmap[4]));
			edit2.setBackground(new BitmapDrawable(getResources(),denisityBitmap[4]));
			edit3.setBackground(new BitmapDrawable(getResources(),denisityBitmap[4]));
			edit4.setBackground(new BitmapDrawable(getResources(),denisityBitmap[4]));
			yes_button.setBackground(new BitmapDrawable(getResources(),denisityBitmap[2]));
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
		analit.gAnalitics(getApplicationContext(),"Email Reminder Page");

		/*==========================google Analitics Ends=============================*/
		String reminder_contact = remindMessagePref.getString("reminder_contact", "");
		if(!reminder_contact.equals("no")){
			String combine_contact = remindMessagePref.getString("combine_contact", "");
			reminder_contact_name = remindMessagePref.getString("reminder_contact_name", "");
			reminder_contact_number = remindMessagePref.getString("reminder_contact_number", "");
			name.setText(combine_contact);
			combineMessage();
			SharedPreferences.Editor editor = remindMessagePref.edit();
			editor.putString("reminder_contact","no");	 
			editor.putString("combine_contact","no");
			editor.putString("reminder_contact_name","no");
			editor.putString("reminder_contact_number","no"); 
			editor.commit();

		}
		reminder_date = remindMessagePref.getString("reminder_date", "");
		if(!reminder_date.equals("no")){
			date.setText(reminder_date);
			SharedPreferences.Editor editor = remindMessagePref.edit();
			editor.putString("reminder_date","no");	  	  	       
			editor.commit();

		}
		reminder_time = remindMessagePref.getString("reminder_time", "");
		if(!reminder_time.equals("no")){
			exact_reminder_time = remindMessagePref.getString("exact_reminder_time","");
			time.setText(reminder_time);
			SharedPreferences.Editor editor = remindMessagePref.edit();
			editor.putString("reminder_time","no");	  	  
			editor.putString("exact_reminder_time","no");
			editor.commit();

		}
		reminder_message = remindMessagePref.getString("reminder_message", "");
		if(!reminder_message.equals("no")){
			message.setText(reminder_message);
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
	@Override
	protected void onDestroy(){
		super.onDestroy(); 
		container_check=false;
	}

	public void inserReminderToDataBase(){
		long lastId;
		String [] splitDate=date.getText().toString().split(" ");
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
		Long milisec=SelectTime.dateToString(Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]),hour,min);
		String milidate=milisec+"";
		SelectTime st=new SelectTime();
		Long currentMilisec=SelectTime.dateToString(st.mYear,st.mMonth,st.mDay,st.mHour,st.mMinute);
		try{
			ourReminders=new DatabaseHandler(this);
			ourDatabase=ourReminders.openConnection(this);
			long pevMili=Long.parseLong(milidate);
			if(reminder_contact_name==null && reminder_contact_number==null || reminder_contact_name.equals("no")&&reminder_contact_number.equals("no")){
				message("Please select contact first.");
			}else if(message.getText().toString().equals("Subject")){
				message("Please write subject.");
			}else if(currentMilisec>pevMili){
				message("Alarm Time Passed.");
			}else{
				if(editCheck){
					int id=Integer.parseInt(edit_id);
					long id1=(long)id;
					ourReminders.updateReminder(id,reminder_contact_name,reminder_contact_number,message.getText().toString(),date.getText().toString(),time.getText().toString(),"","email","run",milidate);
					scheduleAlarm(id1,Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
				}else{
					lastId = ourReminders.addReminders(reminder_contact_name,reminder_contact_number,message.getText().toString(),date.getText().toString(),time.getText().toString(),"","email","run",milidate);
					scheduleAlarm(lastId,Integer.parseInt(splitDate[2]),SelectTime.getMonthInInteger(splitDate[1]),Integer.parseInt(splitDate[0]));
				}
				finish();
				overridePendingTransition(R.anim.push_down_out, R.anim.push_down_out);
			}
			ourReminders.closeConnection();
		}catch(Exception e){
			didItWork=false;
			System.out.println("fail to save in database");
		}
	}
	public void message(String msg){
		Builder builder = new Builder(EmailReminder.this);
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
	public void scheduleAlarm(long requestCode,int year,int month,int day){
		int hour = 0;
		int min = 0;
		String hString=SelectTime.time(exact_reminder_time);
		String time2[]=hString.split("_");
		hour=Integer.parseInt(time2[0]);
		min=Integer.parseInt(time2[1]);
		intentAlarm = new Intent(this, AlarmReciever.class);
		intentAlarm.putExtra("Message", "email");
		intentAlarm.putExtra("Id", requestCode);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		GregorianCalendar calendar = new GregorianCalendar(year,month,day,hour,min);
		long time = calendar.getTimeInMillis();
		int requestCode1=(int)requestCode;
		alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,requestCode1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
	}

	public void checkScreenSize(){
		int screenSize = getResources().getConfiguration().screenLayout &
				Configuration.SCREENLAYOUT_SIZE_MASK;
		switch(screenSize) {
		case Configuration.SCREENLAYOUT_SIZE_LARGE:

			largeScrenn="largeScreen";
			break;
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			largeScrenn="normalScreen";
			break;
		case Configuration.SCREENLAYOUT_SIZE_SMALL:
			largeScrenn="smallScreen";
			break;
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			largeScrenn="xtrLargeScreen";
			break;
			//	xtraLargeScreen=true;
		default:
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
}
