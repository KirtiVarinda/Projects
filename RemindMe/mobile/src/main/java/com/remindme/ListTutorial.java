package com.remindme;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

public class ListTutorial extends Activity implements OnTouchListener{
	MainReminder mR=new MainReminder();
	public List<String[]> allReminder =null ;
	String id;
	String type;
	private DatabaseHandler ourReminders;
	private SQLiteDatabase ourDatabase;
	int firstDownX,x1,deltaX  ;
	int firstDownY ,y1,deltaY;
	Animation anim;
	AnimationSet animLeftRight;
	AnimationSet fadeAnim;
	AlphaAnimation fadeIn ;
	AlphaAnimation fadeOut ;
	String  densityCheck="";
	String tutorialType;
	int[] images={R.drawable.remindertooltip6,R.drawable.remindertooltip7,R.drawable.remindertooltiparrow2left
			,R.drawable.remindertooltiparrow2right,R.drawable.remindertooltiphandicon};
	Bitmap[] denisityBitmap=new Bitmap[images.length];
	int sdk = Build.VERSION.SDK_INT;
	TextView headImage,arrowLeftRight,finger ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_tutorial);
		
		getTextViewImages();
		getImagesInBitmap();
		sdkCheck();
		arrowLeftRight.setOnTouchListener(this);
		Bundle extras =  getIntent().getExtras();
		if(extras!=null){
			tutorialType= extras.getString("tutorialType");

		}
		checkScreenDensity();
		if(tutorialType.equals("left")){
			getData();
			animation(finger,"leftToRight");
		}	if(tutorialType.equals("right")){
			if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
				headImage.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[1]));
				arrowLeftRight.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[2]));

			}	else {
				headImage.setBackground(new BitmapDrawable(getResources(),denisityBitmap[1]));
				arrowLeftRight.setBackground(new BitmapDrawable(getResources(),denisityBitmap[2]));
			} 
			animation(finger,"rightToLeft");
		}


	}
	public void getData(){
		ourReminders=new DatabaseHandler(this);
		ourDatabase=ourReminders.openConnection(this);
		allReminder=ourReminders.selectAll("all");
		for (final String[] reminderValues : allReminder) {
			id=reminderValues[0];

			type=reminderValues[8];


		}
		ourReminders.closeConnection();
	}
	protected void animation(TextView finger1,String type){
		fadeIn = new AlphaAnimation( 3.0f ,  0.0f );
		fadeIn.setRepeatCount(Animation.INFINITE);
		fadeIn.setDuration(1000);
		fadeIn.setRepeatMode(Animation.RESTART);
		fadeOut = new AlphaAnimation(0.0f, 3.0f);
		fadeOut.setRepeatCount(Animation.INFINITE);
		fadeOut.setRepeatMode(Animation.RESTART);
		//	fadeOut.setDuration(250);
		fadeAnim=new AnimationSet(false);
		fadeAnim.addAnimation(fadeOut);
		fadeAnim.addAnimation(fadeIn);
		animLeftRight=new AnimationSet(false);
		//	animIn=new AnimationSet(false);
		//	animOut.addAnimation(fadeOut);


		if(densityCheck.equals("high")||densityCheck.equals("medium")){
			if(type.equals("leftToRight")){
				anim = new TranslateAnimation(0f,300f,0f,0f);
			}else if(type.equals("rightToLeft")){
				anim = new TranslateAnimation(300f,0f,0f,0f);
			}
		}else if(densityCheck.equals("xhigh")){
			if(type.equals("leftToRight")){
				anim = new TranslateAnimation(0f,450f,0f,0f);
			}else if(type.equals("rightToLeft")){
				anim = new TranslateAnimation(450f,0f,0f,0f);
			}
		}else if(densityCheck.equals("xxhigh")){
			if(type.equals("leftToRight")){
				anim = new TranslateAnimation(0f,650f,0f,0f);
			}else if(type.equals("rightToLeft")){
				anim = new TranslateAnimation(650f,0f,0f,0f);
			}
		}else{
			if(type.equals("leftToRight")){
				anim = new TranslateAnimation(0f,300f,0f,0f);
			}else if(type.equals("rightToLeft")){
				anim = new TranslateAnimation(300f,0f,0f,0f);
			}
		}
		// anim.setDuration(2000); //You can manage the time of the blink with this parameter
		anim.setRepeatMode(Animation.RESTART);
		anim.setRepeatCount(Animation.INFINITE);

		animLeftRight.addAnimation(anim);
		animLeftRight.addAnimation(fadeAnim);
		animLeftRight.setDuration(2000);

		finger1.startAnimation(animLeftRight);
	}
	protected void getTextViewImages(){
		headImage = (TextView )findViewById(R.id.textView1);
		arrowLeftRight = (TextView )findViewById(R.id.textView2);
		finger = (TextView )findViewById(R.id.textView3);
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


		if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
			headImage.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[0]));
			arrowLeftRight.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[3]));
			finger.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[4]));

		}	else {
			headImage.setBackground(new BitmapDrawable(getResources(),denisityBitmap[0]));
			arrowLeftRight.setBackground(new BitmapDrawable(getResources(),denisityBitmap[3]));
			finger.setBackground(new BitmapDrawable(getResources(),denisityBitmap[4]));
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
	public boolean onTouch(View v, MotionEvent event) {
		PointF DownPT = new PointF(); // Record Mouse Position When Pressed Down
		PointF StartPT = new PointF();
		boolean eventConsumed = true;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:{
			if(v==arrowLeftRight){
				firstDownX = (int)event.getX();
				firstDownY = (int)event.getY(); 
				eventConsumed = true;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			x1 = (int)event.getX();
			y1 = (int)event.getY(); 
			if(v==arrowLeftRight){
				deltaX=Math.abs(x1)-Math.abs(firstDownX);
				deltaY=Math.abs(y1)-Math.abs(firstDownY);
				if(tutorialType.equals("left")){
					if(firstDownX<v.getWidth()/3 && Math.abs(deltaX)>=v.getWidth()/2 && deltaY<50 ){
						ourReminders=new DatabaseHandler(this);
						ourDatabase=ourReminders.openConnection(this);

						// finger2.setVisibility(View.VISIBLE);
						// finger.clearAnimation();
						// finger.setVisibility(View.INVISIBLE);
						if(type.equals("runnoAlarm") || type.equals("donenoAlarm")){
							ourReminders.updateSnooz(id,"donenoAlarm");
						}else{
							ourReminders.updateSnooz(id,"done");
							mR.cancleAlarms(Integer.parseInt(id));
						}
						ourReminders.closeConnection();

						MainReminder.tu2=true;
						finish();
						overridePendingTransition(0, 0);
					}
				}	if(tutorialType.equals("right")){
					if(firstDownX>v.getWidth()-v.getWidth()/3 && Math.abs(deltaX)>=v.getWidth()/2 && deltaY<50 ){
						MainReminder.tu1=true;
						finish();
						overridePendingTransition(0, 0);
					}
				}

				eventConsumed = false;
				break;

			}
		}
		case MotionEvent.ACTION_UP:{
			firstDownX=0;
			x1=0;
			deltaX=0  ;
			firstDownY=0;
			y1=0;
			deltaY=0;
			break;
		}


		}
		return eventConsumed;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	} 
}
