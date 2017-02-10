package com.remindme;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class CalendarView extends Activity {
	String themeType;
	Bitmap[] themeImages;
	Button btn;
	int sdk = android.os.Build.VERSION.SDK_INT;
	String densityCheck="";
	RelativeLayout calHeader;
	int[] images={R.drawable.calendar_header,R.drawable.yes_remind,R.drawable.close_reminder};
	Bitmap[] denisityBitmap=new Bitmap[images.length];
	TextView daySet,monthSet,yearSet,yes;
	String selectDate;
	String selectedGridDate ;
	SharedPreferences remindMessagePref;
	public GregorianCalendar month, itemmonth;// calendar instances.

	public CalendarAdapter adapter;// adapter instance
	public Handler handler;// for grabbing some event values for showing the dot
	// marker.
	public ArrayList<String> items; // container to store calendar items which
	// needs showing the event marker

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.calendar);
		if(sdk > android.os.Build.VERSION_CODES.GINGERBREAD) {
 			this.setFinishOnTouchOutside(false);

		}
		Locale.setDefault( Locale.US );
		daySet=(TextView) findViewById(R.id.textView2);
		monthSet=(TextView) findViewById(R.id.textView3);
		yearSet=(TextView) findViewById(R.id.textView4);
		yes=(TextView) findViewById(R.id.textView5);
		TextView hidden = (TextView) findViewById(R.id.textView6);
		calHeader=(RelativeLayout) findViewById(R.id.header);
		btn = (Button) findViewById(R.id.button1);
		remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
		themeType= remindMessagePref.getString("theme_selector", "");
		int size = remindMessagePref.getInt("theme_size", 0);
		String imageArray[] = new String[size];
		for(int i=0;i<size;i++){
			imageArray[i] = remindMessagePref.getString("stringImage_"+i, "");
		}
		themeImages=new Bitmap[size];
		themeImages=Theme.decodeBase64(imageArray);
		checkScreenDensity();
		getImagesInBitmap();
		sdkCheck();
		if(densityCheck.equals("xhigh")){
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)hidden.getLayoutParams();       	    
			params.height = 140;
			hidden.setLayoutParams(params);
		}else if(densityCheck.equals("xxhigh")){
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)hidden.getLayoutParams();       	    
			params.height = 160;
			hidden.setLayoutParams(params);
		}else{
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)hidden.getLayoutParams();       	    
			params.height = 80;
			hidden.setLayoutParams(params);
		}
		int activityDay = 0;
		int activityMonth = 0;
		int activityYear = 0;
		GregorianCalendar gg=(GregorianCalendar) GregorianCalendar.getInstance();

		Bundle extras = getIntent().getExtras();
		String messageFor = extras.getString("for_time");
		if(messageFor.equals("getDate")){
			String activity_date = extras.getString("activity_date");
			if(activity_date==null || activity_date.equals(" ")){
				activityDay=SelectTime.mDay;
				activityMonth=SelectTime.mMonth+1;
				activityYear=SelectTime.mYear;
				selectDate=activityDay+" "+SelectTime.getMonthInWords(activityMonth-1)+" "+activityYear;
			}else{
				String ddate[]=activity_date.split(" ");
				activityDay=Integer.parseInt(ddate[0]);
				activityMonth=SelectTime.getMonthInInteger(ddate[1])+1;
				activityYear=Integer.parseInt(ddate[2]);
				selectDate=activityDay+" "+ddate[1]+" "+activityYear;
			}
		} 
		setDateShow(selectDate);
		String someDate =activityMonth+"."+activityDay+"."+activityYear ;
		SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy");
		Date date = null;
		try {
			date = sdf.parse(someDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String aa=date.getTime()+"";
		long milliseconds=Long.parseLong(aa);
		gg.setTimeInMillis(milliseconds);
		month =  (GregorianCalendar) gg;
		itemmonth = (GregorianCalendar) month.clone();

		items = new ArrayList<String>();
		adapter = new CalendarAdapter(this, month,themeImages,themeType);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(adapter);

		handler = new Handler();
		handler.post(calendarUpdater);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

		RelativeLayout previous = (RelativeLayout) findViewById(R.id.previous);

		previous.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setPreviousMonth();
				refreshCalendar();
			}
		});

		RelativeLayout next = (RelativeLayout) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setNextMonth();
				refreshCalendar();

			}
		});

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				((CalendarAdapter) parent.getAdapter()).setSelected(v);
				selectedGridDate = CalendarAdapter.dayString
						.get(position);
				String[] separatedTime = selectedGridDate.split("-");
				selectDate=separatedTime[2]+" "+SelectTime.getMonthInWords(Integer.parseInt(separatedTime[1])-1)+" "+separatedTime[0];
				String gridvalueString = separatedTime[2].replaceFirst("^0*",
						"");// taking last part of date. ie; 2 from 2012-12-02.
				int gridvalue = Integer.parseInt(gridvalueString);
				// navigate to next or previous month on clicking offdays.
				if ((gridvalue > 10) && (position < 8)) {
					setPreviousMonth();
					refreshCalendar();
				} else if ((gridvalue < 7) && (position > 28)) {
					setNextMonth();
					refreshCalendar();
				}
				((CalendarAdapter) parent.getAdapter()).setSelected(v);
				setDateShow(selectDate);
				//	showToast(selectDate);

			}
		});
		yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_left);
			}
		});
		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setDateMessage();
				finish();
				overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_left);
			}
		});
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
	protected void getImagesInBitmap(){
		for(int i=0;i<images.length;i++){
			denisityBitmap[i]=((BitmapDrawable) getResources().getDrawable(images[i])).getBitmap();
		}
	} 
	@SuppressLint("NewApi")
	protected void sdkCheck(){
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		denisityBitmap=ScreenDenesityCheck.checkScreenDensityAndResize(denisityBitmap,metrics,densityCheck,MainReminder.checkScreenSize(getApplicationContext()));
		if(!themeType.equals("purple")){
			denisityBitmap[2]=themeImages[1];
		}
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			btn.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[1]));
			calHeader.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[0]));
			yes.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[2]));

		}	else {
			btn.setBackground(new BitmapDrawable(getResources(),denisityBitmap[1]));
			calHeader.setBackground(new BitmapDrawable(getResources(),denisityBitmap[0]));
			yes.setBackground(new BitmapDrawable(getResources(),denisityBitmap[2]));

		} 
	}
	protected void setDateShow(String date){
		String splitDate[]=date.split(" ");
		daySet.setText(splitDate[0]);
		monthSet.setText(splitDate[1].substring(0,3).toUpperCase());
		yearSet.setText(splitDate[2]);
	}
	protected void setDateMessage(){
		SharedPreferences.Editor editor = remindMessagePref.edit();
		editor.putString("reminder_date",selectDate);
		editor.commit();
	}
	protected void setNextMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMaximum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) + 1),
					month.getActualMinimum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) + 1);
		}

	}

	protected void setPreviousMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMinimum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}

	}

	/*protected void showToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();

	}*/

	public void refreshCalendar() {
		TextView title = (TextView) findViewById(R.id.title);

		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		handler.post(calendarUpdater); // generate some calendar items

		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}

	public Runnable calendarUpdater = new Runnable() {

		@Override
		public void run() {
			items.clear();

			// Print dates of the current week
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
			String itemvalue;
			for (int i = 0; i < 7; i++) {
				itemvalue = df.format(itemmonth.getTime());
				itemmonth.add(GregorianCalendar.DATE, 1);
				items.add("2012-09-12");
				items.add("2012-10-07");
				items.add("2012-10-15");
				items.add("2012-10-20");
				items.add("2012-11-30");
				items.add("2012-11-28");
			}

			adapter.setItems(items);
			adapter.notifyDataSetChanged();
		}
	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {

			finish();
			overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_left);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	} 	
}
