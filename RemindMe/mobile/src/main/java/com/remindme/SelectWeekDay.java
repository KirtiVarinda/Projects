package com.remindme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.remindme.RemindMeSession.SharedData;

public class SelectWeekDay extends Activity {
	private static SharedData shareData;
	private static  String selectedDay="";
	private static ImageView sunImg,monImg,tueImg,wedImg,thuImg,friImg,satImg;
	private static int checkedImage[]={R.drawable.check1,R.drawable.check2};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_week_day);
		shareData=new SharedData(getApplicationContext());
		
		selectedDay=getIntent().getStringExtra("week_day_selected").toLowerCase();
		//System.out.println("selectedDay"+selectedDay);
		
		
		sunImg=(ImageView)findViewById(R.id.imageView1);
		monImg=(ImageView)findViewById(R.id.imageView2);
		tueImg=(ImageView)findViewById(R.id.imageView3);
		wedImg=(ImageView)findViewById(R.id.imageView4);
		thuImg=(ImageView)findViewById(R.id.imageView5);
		friImg=(ImageView)findViewById(R.id.imageView6);
		satImg=(ImageView)findViewById(R.id.imageView7);
		
		
	 
		
		if(selectedDay.equals("sunday")){
			resetImage(checkedImage[1],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0]);
		}else if(selectedDay.equals("monday")){
			resetImage(checkedImage[0],checkedImage[1],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0]);
		}else if(selectedDay.equals("tuesday")){
			resetImage(checkedImage[0],checkedImage[0],checkedImage[1],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0]);
		}else if(selectedDay.equals("wednesday")){
			resetImage(checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[1],checkedImage[0],checkedImage[0],checkedImage[0]);
		}else if(selectedDay.equals("thursday")){
			resetImage(checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[1],checkedImage[0],checkedImage[0]);
		}else if(selectedDay.equals("friday")){
			resetImage(checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[1],checkedImage[0]);
		}else if(selectedDay.equals("saturday")){
			resetImage(checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[1]);
		}else{
			resetImage(checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0]);
		}
		
	}
	
	/*
	 * Reset Images from image array
	 * 
	 */
	private void resetImage(int img1,int img2,int img3,int img4,int img5,int img6,int img7){
		sunImg.setImageResource(img1);
		monImg.setImageResource(img2);
		tueImg.setImageResource(img3);
		wedImg.setImageResource(img4);
		thuImg.setImageResource(img5);
		friImg.setImageResource(img6);
		satImg.setImageResource(img7);
		
	}
	

	public void sunday(View view){
		resetImage(checkedImage[1],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0]);
		shareData.setGeneralSaveSession(SharedData.SELECTEDWEEKDAY, "Sunday");
		finish();
	}
	public void monday(View view){
		resetImage(checkedImage[0],checkedImage[1],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0]);
		shareData.setGeneralSaveSession(SharedData.SELECTEDWEEKDAY, "Monday");
		finish();
	 
	}
	public void tuesday(View view){
		resetImage(checkedImage[0],checkedImage[0],checkedImage[1],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0]);
		shareData.setGeneralSaveSession(SharedData.SELECTEDWEEKDAY, "Tuesday");
		finish();
	}
	public void wednesday(View view){
		resetImage(checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[1],checkedImage[0],checkedImage[0],checkedImage[0]);
		shareData.setGeneralSaveSession(SharedData.SELECTEDWEEKDAY, "Wednesday");
		finish();
	}
	public void thursday(View view){
		resetImage(checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[1],checkedImage[0],checkedImage[0]);
		shareData.setGeneralSaveSession(SharedData.SELECTEDWEEKDAY, "Thursday");
		finish();
	}
	public void friday(View view){
		resetImage(checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[1],checkedImage[0]);
		shareData.setGeneralSaveSession(SharedData.SELECTEDWEEKDAY, "Friday");
		finish();
	}
	public void saturday(View view){
		resetImage(checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[0],checkedImage[1]);
		shareData.setGeneralSaveSession(SharedData.SELECTEDWEEKDAY, "Saturday");
		finish();
	}
	  @Override
	    public void onBackPressed() {
		  if(!selectedDay.equals("no")){
			  selectedDay= Character.toUpperCase(selectedDay.charAt(0)) + selectedDay.substring(1);
		  }
		  
		  shareData.setGeneralSaveSession(SharedData.SELECTEDWEEKDAY, selectedDay);
		  finish();
	    }

}
