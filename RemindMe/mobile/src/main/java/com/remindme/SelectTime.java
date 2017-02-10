package com.remindme;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

@SuppressLint("NewApi")
public class SelectTime extends Activity {
	static Context ctxtx;
	TextView amPmBoth;
	String themeType;
	Bitmap[] themeImages;
	TextView close_button;
	int sdk = android.os.Build.VERSION.SDK_INT;
	String  densityCheck="";
	int[] images={R.drawable.time_head,R.drawable.wakeup_center,R.drawable.wakeup_bottom,R.drawable.yes_remind,
			R.drawable.close_reminder,R.drawable.runing_button};
	Bitmap[] denisityBitmap=new Bitmap[images.length];
	TextView top,center2,footer;
	String minString;
	String hourString; 
	TextView am,pm;
	Button yes_button;
	String amPmString;
	TextView hourText,minText;
	int progressChanged1;
	int progressChanged2;
	SeekBar sb1;
	SeekBar sb2;
	DecimalFormat formatter = new DecimalFormat("0.0");
	SharedPreferences remindMessagePref;
	String time;
	String date;
	int hour1,min1;
	public static int mYear, mMonth, mDay,mHour,mMinute,nextDay,nextMonth,nextYear;
	int activityHour,activityMin,activityDay,activityMonth,activityYear;
	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID=1;
	public SelectTime(){
		ctxtx=SelectTime.this;
		final Calendar c = Calendar.getInstance();
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		Date now = new Date(); 
		c.setTime(now);  
		c.add(Calendar.DAY_OF_YEAR, 1);
		c.add(Calendar.MONTH, 1);
		c.add(Calendar.YEAR, 1);// <--  
		Date tomorrow = c.getTime();  
		nextDay=tomorrow.getDate();
	}
	protected static String twoDecimalTime(int minute){
		String	minute1 = "";
		if(minute>=00 && minute <=9){
			minute1="0"+minute;
		}else{
			minute1=""+minute;
		}
		return minute1;
	}
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_time);
		ctxtx=SelectTime.this;
		remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
		themeType= remindMessagePref.getString("theme_selector", "");
		int size = remindMessagePref.getInt("theme_size", 0);
		String imageArray[] = new String[size];
		for(int i=0;i<size;i++){
			imageArray[i] = remindMessagePref.getString("stringImage_"+i, "");
		}
		themeImages=new Bitmap[size];
		themeImages=Theme.decodeBase64(imageArray);

		hourText=(TextView )findViewById(R.id.textView9);
		minText=(TextView )findViewById(R.id.textView11);
		am = (TextView )findViewById(R.id.textView12);
		pm = (TextView )findViewById(R.id.textView4);
		amPmBoth=(TextView )findViewById(R.id.textView13);
		sb1 = (SeekBar)findViewById(R.id.seekBar1);
		sb2=(SeekBar)findViewById(R.id.seekBar2);
		yes_button = (Button )findViewById(R.id.button2);
		top= (TextView )findViewById(R.id.textView6);
		close_button=(TextView )findViewById(R.id.textView2);
		center2= (TextView )findViewById(R.id.textView1);

		footer= (TextView )findViewById(R.id.textView3);
		checkScreenDensity();
		getImagesInBitmap();
		sdkCheck();
		try {
			setPreviousTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setHourAndMinute();
		close_button.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				finish();
				overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_left);
			}
		}); 

		amPmBoth.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {


				if(amPmString.equals("AM")){
					am.setTextColor(Color.parseColor("#636363"));
					pm.setTextColor(Color.parseColor("#ffffff"));
					amPmString="PM";
				}else if(amPmString.equals("PM")){
					am.setTextColor(Color.parseColor("#ffffff"));
					pm.setTextColor(Color.parseColor("#636363"));
					amPmString="AM";
				}
			}
		});
 
		yes_button.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				int hhour=Integer.parseInt(hourText.getText().toString());
				int mmin=Integer.parseInt(minText.getText().toString());

				String amPm1=amPmString;
				min1=mmin;
				if(amPm1.equals("AM")){
					if(hhour==12){
						time="0:"+minText.getText().toString()+" AM";
						hour1=0;
					}else{
						time=hhour+":"+minText.getText().toString()+" AM";
						hour1=hhour;
					}
				}else if(amPm1.equals("PM")){
					int hour24=hhour+12;
					if(hour24==24){
						hour1=12;
						time=hour1+":"+minText.getText().toString()+" PM";
					}else{
						hour1=hour24;
						time=hhour+":"+minText.getText().toString()+" PM";
					}
				}

				setTimeMessage();
				finish();
				overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_left);
			}
		});
	}
	protected void setPreviousTime() throws ParseException{
		Bundle extras = getIntent().getExtras();
		String messageFor = extras.getString("for_time");

		if(messageFor.equals("getTime")){
			String activity_time = extras.getString("activity_time");
			String ttime[]=activity_time.split(" ");
			String ttime1[]=ttime[0].split(":");
			activityHour=Integer.parseInt(ttime1[0]);
			activityMin=Integer.parseInt(ttime1[1]);
			amPmString=ttime[1].toUpperCase();

		}
		if(amPmString.equals("AM")){
			am.setTextColor(Color.parseColor("#ffffff"));
			pm.setTextColor(Color.parseColor("#636363"));
		}else if(amPmString.equals("PM")){
			am.setTextColor(Color.parseColor("#636363"));
			pm.setTextColor(Color.parseColor("#ffffff"));
		}
		if(activityHour>=1 && activityHour<=9){
			hourString="0"+activityHour;
		}else{
			hourString=""+activityHour;
		}
		if(activityMin>=0 && activityMin<=9){
			minString="0"+activityMin;
		}else{
			minString=""+activityMin;
		}
		hourText.setText(hourString);
		minText.setText(minString);
	}
	protected void setHourAndMinute(){
		if(themeType.equals("green")){
			sb1.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_green));
			sb2.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_green));
		}else if(themeType.equals("yellow")){
			sb1.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_yellow));
			sb2.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_yellow));
		}else if(themeType.equals("white")){
			sb1.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_white));
			sb2.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_white));
		}else if(themeType.equals("blue")){
			sb1.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_blue));
			sb2.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_blue));
		}else if(themeType.equals("red")){
			sb1.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_red));
			sb2.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_red));
		}else if(themeType.equals("purple")){
			sb1.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar));
			sb2.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar));
		}
		sb1.setProgress(activityHour-1);
		sb1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				progress=progress+1;
				if(progress>=1 && progress<=9){
					hourString="0"+progress;
				}else{
					hourString=""+progress;
				}
				hourText.setText(hourString);
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			public void onStopTrackingTouch(SeekBar seekBar) {

				//Toast.makeText(SelectTime.this,"seek bar progress:"+progressChanged1,Toast.LENGTH_SHORT).show();
			}
		});
		sb2.setProgress(activityMin);
		sb2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				if(progress>=0 && progress<=9){
					minString="0"+progress;
				}else{
					minString=""+progress;
				}
				minText.setText(minString);
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			public void onStopTrackingTouch(SeekBar seekBar) {

				//	Toast.makeText(SelectTime.this,"seek bar progress:"+progressChanged2,Toast.LENGTH_SHORT).show();
			}
		});
	}

	protected void setTimeMessage(){
		SharedPreferences.Editor editor = remindMessagePref.edit();
		editor.putString("reminder_time",time);
		editor.putString("exact_reminder_time",hour1+"_"+min1);
		editor.commit();
	}
	protected void getImagesInBitmap(){
		for(int i=0;i<images.length;i++){
			denisityBitmap[i]=((BitmapDrawable) getResources().getDrawable(images[i])).getBitmap();
		}
	} 
	protected void sdkCheck(){
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		denisityBitmap=ScreenDenesityCheck.checkScreenDensityAndResize(denisityBitmap,metrics,densityCheck,MainReminder.checkScreenSize(getApplicationContext()));

		if(!themeType.equals("purple")){
			denisityBitmap[4]=themeImages[1];
		}
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			top.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[0]));
			center2.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[1]));
			close_button.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[4]));
			footer.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[2]));
			yes_button.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[3]));

		}	else {
			top.setBackground(new BitmapDrawable(getResources(),denisityBitmap[0]));
			center2.setBackground(new BitmapDrawable(getResources(),denisityBitmap[1]));
			close_button.setBackground(new BitmapDrawable(getResources(),denisityBitmap[4]));
			footer.setBackground(new BitmapDrawable(getResources(),denisityBitmap[2]));
			yes_button.setBackground(new BitmapDrawable(getResources(),denisityBitmap[3]));


		} 
		sb1.setThumb(new BitmapDrawable(getResources(),denisityBitmap[5]));

		sb2.setThumb(new BitmapDrawable(getResources(),denisityBitmap[5])); 
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
	public static String getMonthInWords(int year){
		String month = null;
		if(year==0){
			month="January";
		}else if(year==1){
			month="February";
		}else if(year==2){
			month="March";
		}else if(year==3){
			month="April";
		}else if(year==4){
			month="May";
		}else if(year==5){
			month="June";
		}else if(year==6){
			month="July";
		}else if(year==7){
			month="August";
		}else if(year==8){
			month="September";
		}else if(year==9){
			month="October";
		}else if(year==10){
			month="November";
		}else if(year==11){
			month="December";
		}
		return month;
	}
	public static int getMonthInInteger(String year){
		int month = 0;
		if(year.equals("January")){
			month=0;
		}else if(year.equals("February")){
			month=1;
		}else if(year.equals("March")){
			month=2;
		}else if(year.equals("April")){
			month=3;
		}else if(year.equals("May")){
			month=4;
		}else if(year.equals("June")){
			month=5;
		}else if(year.equals("July")){
			month=6;
		}else if(year.equals("August")){
			month=7;
		}else if(year.equals("September")){
			month=8;
		}else if(year.equals("October")){
			month=9;
		}else if(year.equals("November")){
			month=10;
		}else if(year.equals("December")){
			month=11;
		}
		return month;
	}
	public void splitDate(){

	}
	public static long dateToString(int year,int month,int day,int hour,int min){
		GregorianCalendar calendar = new GregorianCalendar(year,month,day,hour,min);
		long startTime = calendar.getTimeInMillis();
		return startTime;
	}
	public static String CurrenttwelevFormatTime(int current_hour,int current_min){
		int h;
		String current_time;
		String current_min1=twoDecimalTime(current_min);
		if(current_hour>=0 && current_hour<12){
			current_time=current_hour+":"+current_min1+" am";
		}
		else{
			h=current_hour-12;
			if(h==0){
				h=12;
			}
			current_time=h+":"+current_min1+" pm";
		}
		return current_time;
	}
	public static String time(String exact_reminder_time){
		int hour = 0;
		String min = "";
		if(!exact_reminder_time.equals("no")){

			String time2[]=exact_reminder_time.split("_");
			hour=Integer.parseInt(time2[0]);
			min=time2[1];

		}else{
			hour = timeAfterHour();
			min = twoDecimalTime(mMinute);
		}
		return hour+"_"+min+"";
	}
	public static String milisecToDate(long milisec){
		Calendar c = Calendar.getInstance(); 
		//Set time in milliseconds
		c.setTimeInMillis(milisec);
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH); 
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		String mm=getMonthInWords(mMonth);
		String date=mDay+" "+mm+" "+mYear;
		return date;
	}
	public static String milisecToTime(long milisec){
		Calendar c = Calendar.getInstance(); 
		//Set time in milliseconds
		c.setTimeInMillis(milisec);
		int hr = c.get(Calendar.HOUR_OF_DAY);
		int min = c.get(Calendar.MINUTE);
		String min1=twoDecimalTime(min);
		String	time="";
		if(hr>=0 && hr<12){
			time=hr+":"+min1+" am";
		}
		else{
			hr=hr-12;
			if(hr==0){
				hr=12;
			}
			time=hr+":"+min1+" pm";
		}
		return time;
	}
	public static String milisecToTimeFull(long milisec){
		Calendar c = Calendar.getInstance(); 
		//Set time in milliseconds
		c.setTimeInMillis(milisec);
		int hr = c.get(Calendar.HOUR_OF_DAY);
		int min = c.get(Calendar.MINUTE);
		String min1=twoDecimalTime(min);
		return hr+"_"+min1;
	}
	public static int timeAfterHour(){
		Calendar c = Calendar.getInstance();
		Date now = new Date(); 
		c.setTime(now);  
		c.add(Calendar.HOUR_OF_DAY,1);
		Date afetrHour = c.getTime();
		int current_hour=afetrHour.getHours();
		return current_hour;
	}
	public static String time12To24(String time){
		int hour=0;
		String splitAmPm[]=time.split(" ");
		String splitHoutMin[]=splitAmPm[0].split(":");
		int hhour=Integer.parseInt(splitHoutMin[0]);
		if(splitAmPm[1].equals("AM") || splitAmPm[1].equals("am")){
			if(hhour==12){
				hour=0;
			}else{
				hour=hhour;
			}
		}else if(splitAmPm[1].equals("PM")|| splitAmPm[1].equals("pm")){
			int hour24=hhour+12;
			if(hour24==24){
				hour=12;
			}else{
				hour=hour24;
			}
		}
		return hour+"_"+splitHoutMin[1];
	}


	public static void showToast(String str,Context ctx){
		Toast tost=Toast.makeText(ctx, str,Toast.LENGTH_LONG);
		tost.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
		tost.show();
	} 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_left);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	} 
	
	/*static void launchMarket() {

		Uri uri = Uri.parse("market://details?id=" + ctxtx.getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			ctxtx.startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			ctxtx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + ctxtx.getPackageName())));
		}
	}*/
	/*public  static String checkScreenSize(){
		int screenSize = ctxtx.getResources().getConfiguration().screenLayout &Configuration.SCREENLAYOUT_SIZE_MASK;
		String ScreenSize="";
		switch(screenSize) {
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			 ScreenSize="largeScreen";
			break;
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			ScreenSize="normalScreen";
			break;
		case Configuration.SCREENLAYOUT_SIZE_SMALL:
			ScreenSize="smallScreen";
			break;
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			ScreenSize="xtrLargeScreen";
			break;
			//	xtraLargeScreen=true;
		default:
		}
		return ScreenSize;
	}*/
}
