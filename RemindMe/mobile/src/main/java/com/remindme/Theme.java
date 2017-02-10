package com.remindme;

import java.io.ByteArrayOutputStream;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class Theme extends Activity {
	Bitmap[] themeImages;
	Bitmap[] denisityBitmap1;
	String themeType;
	DisplayMetrics metrics = new DisplayMetrics();
	SharedPreferences remindMessagePref;
	String  densityCheck="";
	int[] images={R.drawable.theme_head,R.drawable.back_walk,R.drawable.wakeup_center,R.drawable.wakeup_bottom,R.drawable.check1,R.drawable.check2
			,R.drawable.yes_remind};
	Bitmap[] denisityBitmap=new Bitmap[images.length];
	int sdk = Build.VERSION.SDK_INT;
	TextView text,top,center1,center2,center3,center4,center5,bottom,back;
	Button button1,button2,button3,button4,button5,button6,yes_button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_theme);
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		/*==========================google Analitics Starts=============================*/	

		AnaliticsForActivity analit=new AnaliticsForActivity();
		analit.gAnalitics(getApplicationContext(),"Theme Page");

		/*==========================google Analitics Ends=============================*/	
		remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);
		int size = remindMessagePref.getInt("theme_size", 0);
		 String imageArray[] = new String[size];
		 for(int i=0;i<size;i++){
			 imageArray[i] = remindMessagePref.getString("stringImage_"+i, "");
		 }
		 themeImages=new Bitmap[size];
		 themeImages=Theme.decodeBase64(imageArray);
		
		
		String theme_selector= remindMessagePref.getString("theme_selector", "");
		themeType=theme_selector;
		checkScreenDensity();
		getTextViewImages();
		getImagesInBitmap();
		sdkCheck();
		buttonClicked();
		if(theme_selector.equals("purple")){
			selectedTheme(5,4,4,4,4,4,"purple");
		}else if(theme_selector.equals("red")){
			selectedTheme(4,5,4,4,4,4,"red");
		}else if(theme_selector.equals("white")){
			selectedTheme(4,4,5,4,4,4,"white");
		}else if(theme_selector.equals("blue")){
			selectedTheme(4,4,4,5,4,4,"blue");
		}else if(theme_selector.equals("yellow")){
			selectedTheme(4,4,4,4,5,4,"yellow");
		}else if(theme_selector.equals("green")){
			selectedTheme(4,4,4,4,4,5,"green");
		}

	}
	protected void setYourTheme(){
		if(themeType.equals("purple")){
			int[] images1={R.drawable.back_walk,R.drawable.close_reminder,R.drawable.dailybg,
					R.drawable.hover_changebg};
			denisityBitmap1=new Bitmap[images1.length];
			denisityBitmap1=imageInBitmap(images1);
		}else if(themeType.equals("red")){
			int[] images1={R.drawable.back_walk_red,R.drawable.close_reminder_red,R.drawable.dailybg_red,
					R.drawable.hover_changebg_red};
			denisityBitmap1=new Bitmap[images1.length];
			denisityBitmap1=imageInBitmap(images1);
		}else if(themeType.equals("white")){
			int[] images1={R.drawable.back_walk_white,R.drawable.close_reminder_white,R.drawable.dailybg_white,
					R.drawable.hover_changebg_white};
			denisityBitmap1=new Bitmap[images1.length];
			denisityBitmap1=imageInBitmap(images1);
		}else if(themeType.equals("blue")){
			int[] images1={R.drawable.back_walk_blue,R.drawable.close_reminder_blue,R.drawable.dailybg_blue,
					R.drawable.hover_changebg_blue};
			denisityBitmap1=new Bitmap[images1.length];
			denisityBitmap1=imageInBitmap(images1);
		}else if(themeType.equals("yellow")){
			int[] images1={R.drawable.back_walk_yellow,R.drawable.close_reminder_yellow,R.drawable.dailybg_yellow,
					R.drawable.hover_changebg_yellow};
			denisityBitmap1=new Bitmap[images1.length];
			denisityBitmap1=imageInBitmap(images1);
		}else if(themeType.equals("green")){
			int[] images1={R.drawable.back_walk_green,R.drawable.close_reminder_green,R.drawable.dailybg_green,
					R.drawable.hover_changebg_green};
			denisityBitmap1=new Bitmap[images1.length];
			denisityBitmap1=imageInBitmap(images1);
		}
		
		denisityBitmap1=ScreenDenesityCheck.checkScreenDensityAndResize(denisityBitmap1,metrics,densityCheck,MainReminder.checkScreenSize(getApplicationContext()));
		setTheme(themeType,denisityBitmap1);
	}
	protected Bitmap[] imageInBitmap(int[] images2){
		for(int i=0;i<images2.length;i++){
			denisityBitmap1[i]=((BitmapDrawable) getResources().getDrawable(images2[i])).getBitmap();
		}
		return denisityBitmap1;
	}
	protected void buttonClicked(){
		yes_button.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				 setYourTheme();
				finish();
				overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_left);
			}
		});
		back.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				finish();
				overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_left);
		 
			}
		});
		button1.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				selectedTheme(5,4,4,4,4,4,"purple");
				themeType="purple";

			}
		});
		button2.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				selectedTheme(4,5,4,4,4,4,"red");
				themeType="red";
			}
		});
		button3.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				selectedTheme(4,4,5,4,4,4,"white");
				themeType="white";
			}
		});
		button4.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				selectedTheme(4,4,4,5,4,4,"blue");
				themeType="blue";
			}
		});
		button5.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				selectedTheme(4,4,4,4,5,4,"yellow");
				themeType="yellow";
			}
		});
		button6.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				selectedTheme(4,4,4,4,4,5,"green");
				themeType="green";
			}
		});
		center1.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				selectedTheme(5,4,4,4,4,4,"purple");
				themeType="purple";

			}
		});
		center2.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				selectedTheme(4,5,4,4,4,4,"red");
				themeType="red";
			}
		});
		center3.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				selectedTheme(4,4,5,4,4,4,"white");
				themeType="white";
			}
		});
		center4.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				selectedTheme(4,4,4,5,4,4,"blue");
				themeType="blue";
			}
		});
		center5.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				selectedTheme(4,4,4,4,5,4,"yellow");
				themeType="yellow";
			}
		});
		bottom.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				selectedTheme(4,4,4,4,4,5,"green");
				themeType="green";
			}
		});
	}
	protected void setTheme(String theme,Bitmap themeBitmap[]){
		String stringImages[]=encodeTobase64(themeBitmap);
		SharedPreferences.Editor editor = remindMessagePref.edit();
		editor.putString("theme_selector",theme);	
		editor.putInt("theme_size",stringImages.length);	
		 for(int i=0;i<stringImages.length;i++){  
		        editor.putString("stringImage_" + i, stringImages[i]); 
		 }
		editor.commit();

	}
	protected void getImagesInBitmap(){
		for(int i=0;i<images.length;i++){
			denisityBitmap[i]=((BitmapDrawable) getResources().getDrawable(images[i])).getBitmap();
		}
	}  
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected void sdkCheck(){

		denisityBitmap=ScreenDenesityCheck.checkScreenDensityAndResize(denisityBitmap,metrics,densityCheck,MainReminder.checkScreenSize(getApplicationContext()));
	if(!themeType.equals("purple")){
		denisityBitmap[1]=themeImages[0];
	}
		
		
		if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
			top.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[0]));
			center1.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[2]));
			center2.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[2]));
			center3.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[2]));
			center4.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[2]));
			center5.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[2]));
			bottom.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[3]));
			back.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[1]));
			yes_button.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[6]));
		}	else {
			top.setBackground(new BitmapDrawable(getResources(),denisityBitmap[0]));
			center1.setBackground(new BitmapDrawable(getResources(),denisityBitmap[2]));
			center2.setBackground(new BitmapDrawable(getResources(),denisityBitmap[2]));
			center3.setBackground(new BitmapDrawable(getResources(),denisityBitmap[2]));
			center4.setBackground(new BitmapDrawable(getResources(),denisityBitmap[2]));
			center5.setBackground(new BitmapDrawable(getResources(),denisityBitmap[2]));
			bottom.setBackground(new BitmapDrawable(getResources(),denisityBitmap[3]));
			back.setBackground(new BitmapDrawable(getResources(),denisityBitmap[1]));
			yes_button.setBackground(new BitmapDrawable(getResources(),denisityBitmap[6]));
		} 
	}
	protected void selectedTheme(int puple,int red,int white,int blue,int yellow,int green,String selectedColor){
		if(sdk < Build.VERSION_CODES.JELLY_BEAN) {

			button1.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[puple]));
			button2.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[red]));
			button3.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[white]));
			button4.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[blue]));
			button5.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[yellow]));
			button6.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[green]));


		}	else {

			button1.setBackground(new BitmapDrawable(getResources(),denisityBitmap[puple]));
			button2.setBackground(new BitmapDrawable(getResources(),denisityBitmap[red]));
			button3.setBackground(new BitmapDrawable(getResources(),denisityBitmap[white]));
			button4.setBackground(new BitmapDrawable(getResources(),denisityBitmap[blue]));
			button5.setBackground(new BitmapDrawable(getResources(),denisityBitmap[yellow]));
			button6.setBackground(new BitmapDrawable(getResources(),denisityBitmap[green]));
		} 

		text.setText( selectedColor.substring (0,1).toUpperCase() + selectedColor.substring (1).toLowerCase()+" theme is selected");
	}


	protected void getTextViewImages(){
		text = (TextView )findViewById(R.id.textView9);
		top = (TextView )findViewById(R.id.textView1);
		center1 = (TextView )findViewById(R.id.textView4);
		center2 = (TextView )findViewById(R.id.textView5);
		center3 = (TextView )findViewById(R.id.textView3);
		center4 = (TextView )findViewById(R.id.textView6);
		center5 = (TextView )findViewById(R.id.textView7);
		bottom = (TextView )findViewById(R.id.textView8);
		back = (TextView )findViewById(R.id.textView2);
		button1 = (Button )findViewById(R.id.button1);
		button2 = (Button )findViewById(R.id.button2);
		button3 = (Button )findViewById(R.id.button3);
		button4 = (Button )findViewById(R.id.button4);
		button5 = (Button) findViewById(R.id.button5);
		button6 = (Button )findViewById(R.id.button6);
		yes_button = (Button )findViewById(R.id.button7);


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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.theme, menu);
		return true;
	}
	// method for bitmap to base64
	public static String[] encodeTobase64(Bitmap image[]) {
		String imageEncoded[]=new String[image.length];
		Bitmap immage ;
		for(int i=0;i<image.length;i++){
			immage = image[i];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] b = baos.toByteArray();
			imageEncoded[i] = Base64.encodeToString(b, Base64.DEFAULT);
		}

		return imageEncoded;

	}
	// method for base64 to bitmap
    public static Bitmap[] decodeBase64(String[] input) {
    	Bitmap temp[]=new Bitmap[input.length];
    	byte[] decodedByte;
    	for(int i=0;i<input.length;i++){
    		 decodedByte = Base64.decode(input[i], 0);
    		 temp[i]=BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    		
    	}
       
        return temp;
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
}
