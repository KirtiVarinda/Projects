package com.remindme;

import java.util.ArrayList;
import java.util.Arrays;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class SelectContact extends Activity {
	String themeType;
	Bitmap[] themeImages;
	EditText enterNumber;
	int count=0;
	boolean firstEntry;
	boolean notFirstEntry;
	String test,txt,part1;
	SpannableStringBuilder sb = new SpannableStringBuilder();
	StringBuilder sb1=new StringBuilder();
	StringBuilder nameToSave = new  StringBuilder();
	StringBuilder numberToSave = new  StringBuilder();
	String  densityCheck="";
	String largeScrenn="" ;
	//String nameToSave,numberToSave;
	String email_check="no";
	SharedPreferences remindMessagePref;
	String[] text;
	String[] text2;
	ArrayList<String> name_all=new ArrayList<String>();
	ArrayList<String> email_all=new ArrayList<String>();
	ArrayList<String> contactName_all=new ArrayList<String>();
	ArrayList<String> contact_all=new ArrayList<String>();
	ArrayList arrayNames = new ArrayList();
	ArrayList arrayEmails = new ArrayList();
	ArrayList arrayContacts = new ArrayList();
	MyCustomAdapter mdp;
	ListView list;
	EditText editSearch;
	LayoutInflater mInflater;
	int textlength = 0;
	int i=0;
	int[] images={R.drawable.page_bg,R.drawable.back_walk,R.drawable.sign_icon,R.drawable.border_field};
	Bitmap[] denisityBitmap=new Bitmap[images.length];
	TextView wakeup_top,back,contact_image;
	int sdk = Build.VERSION.SDK_INT;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		remindMessagePref = this.getSharedPreferences("remindMessagePref", 0);



		themeType= remindMessagePref.getString("theme_selector", "");
		int size = remindMessagePref.getInt("theme_size", 0);
		String imageArray[] = new String[size];
		for(int i=0;i<size;i++){
			imageArray[i] = remindMessagePref.getString("stringImage_"+i, "");
		}
		themeImages=new Bitmap[size];
		themeImages=Theme.decodeBase64(imageArray);

		Bundle extras =  getIntent().getExtras();
		checkScreenDensity();
		if(extras!=null){
			email_check = extras.getString("MessageForcontact");
		}
		if(email_check.equals("email")){
			String email23 = remindMessagePref.getString("key_email", null);
			String name23 = remindMessagePref.getString("key_name", null);
			email_all.addAll(Arrays.asList(TextUtils.split(email23, ",=")));
			name_all.addAll(Arrays.asList(TextUtils.split(name23, ",=")));

		}else{
			String contact24 = remindMessagePref.getString("key_contact", null);
			String name24 = remindMessagePref.getString("key_contactName", null);
			contactName_all.addAll(Arrays.asList(TextUtils.split(name24, ",=")));
			contact_all.addAll(Arrays.asList(TextUtils.split(contact24, ",=")));
		}


		getTextViewImages();
		buttonClicked();
		getImagesInBitmap();
		sdkCheck();
		enterNumber.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getUnicodeChar() == 
						(int)EditableAccomodatingLatinIMETypeNullIssues.ONE_UNPROCESSED_CHARACTER.charAt(0))
				{
					//We are ignoring this character, and we want everyone else to ignore it, too, so 
					// we return true indicating that we have handled it (by ignoring it).   
					return true; 
				}				return false;
			}
		});
	}
	public void showToast(String str){
		Toast tost=Toast.makeText(this, str,Toast.LENGTH_LONG);
		tost.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
		tost.show();
	} 
	protected void contact(){

		list = (ListView) findViewById(R.id.listView1);


		if(email_check.equals("email")){
			text= new String[name_all.size()];
			text =name_all.toArray(text);
			enterNumber.setHint("Enter Email");
			text2= new String[email_all.size()];
			text2 =email_all.toArray(text2); 
		}else{
			text= new String[contactName_all.size()];
			text =contactName_all.toArray(text);
			text2= new String[contact_all.size()];
			text2 =contact_all.toArray(text2); 
		}

		mdp=new  MyCustomAdapter(text, text2);
		list.setAdapter(mdp);
		editSearch.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s){
				test = editSearch.getText().toString();
				if(test.length()>1){
					test = test.substring(test.length() - 1);
					if(test.equals(",")){
						list.setAdapter(mdp);
					}
				}

			}
			public void beforeTextChanged(CharSequence s, int start,
					int count, int after)
			{

			}
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{

				txt=editSearch.getText().toString();

				if(txt.contains(",") && txt.length()>1){
					String[] strSearch=txt.split(",");
					for(int d=0; d<strSearch.length;d++){
						part1=strSearch[d];
					}
					notFirstEntry=true;
					firstEntry=false;
					list.setAdapter(mdp);

				}
				else{
					notFirstEntry=false;
					firstEntry=true;
				}


				arrayNames.clear();
				arrayEmails.clear();
				arrayContacts.clear();
				if(firstEntry){
					textlength = editSearch.getText().length();
					for(int i=0; i<text.length; i++)
					{
						if (textlength <= text[i].length())
						{
							if (text[i].toLowerCase().contains(txt.toLowerCase()))
							{
								arrayNames.add(text[i]);
								arrayEmails.add(text2[i]);     //error error
							}
						}
					}
				}

				if(notFirstEntry){
					textlength = part1.length();
					for(int i=0; i<text.length; i++)
					{
						if (textlength <= text[i].length())
						{
							if (text[i].toLowerCase().contains(part1.toLowerCase()))
							{
								arrayNames.add(text[i]);
								arrayEmails.add(text2[i]);
							}
						}
					}
				}
				MyCustomAdapter ff=new MyCustomAdapter
						(arrayNames, arrayEmails);
				list.setAdapter(ff);
				if(ff.isEmpty()){


					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)enterNumber.getLayoutParams();
					RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)enterNumber.getLayoutParams();
					params.height=60; 
					params1.width=editSearch.getWidth();
					enterNumber.setLayoutParams(params);
					enterNumber.setLayoutParams(params1);
				}else{
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)enterNumber.getLayoutParams();
					RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)enterNumber.getLayoutParams();
					params.height=0; 
					params1.width=editSearch.getWidth();
					enterNumber.setLayoutParams(params);
					enterNumber.setLayoutParams(params1);
				}

			}
		});



		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				View v = list.getChildAt(i);
				TextView txtNames =(TextView)view.findViewById(R.id.textView2);
				TextView txtContact =(TextView)view.findViewById(R.id.textView1);
				String strName=txtNames.getText().toString();
				String strContact=txtContact.getText().toString();
				// sb.setSpan(new BackgroundColorSpan(Color.rgb(238,233,233)), 0, edittext.length(), 0);



				//Deselect.			
				if(sb.toString().contains(strName)&&sb.toString().contains(strContact)){
					int start=sb.toString().indexOf(strName+"("+strContact+")");
					int start1=sb1.toString().indexOf(strName);
					int strLength=strName.length();
					int len=strContact.length();
					sb.replace(start, (start+strLength+1+len+2), "");
					sb1.replace(start1, start1+strLength+1, "");
					start=nameToSave.toString().indexOf(strName);
					strLength=strName.length();
					nameToSave.replace(start, (start+strLength+1), "");
					start=numberToSave.toString().indexOf(strContact);
					strLength=strContact.length();
					numberToSave.replace(start, (start+strLength+1), "");
					count=0;
				}
				//Select
				else{
					if(email_check.equals("call")){
						if(count==1){
							Toast tost=Toast.makeText(SelectContact.this, "Only one number allowed for calling.",Toast.LENGTH_SHORT);
							tost.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
							tost.show();
						}
						if(count==0){
							sb1.append(strName+",");
							sb.append(strName+"("+strContact+")"+",");
							nameToSave.append(strName+",");
							numberToSave.append(strContact+",");
							count=1;
						}	

					}else{
						sb1.append(strName+",");
						sb.append(strName+"("+strContact+")"+",");
						nameToSave.append(strName+",");
						numberToSave.append(strContact+",");
					}

				}
				editSearch.setText(sb1);
				editSearch.setSelection(editSearch.getText().length());
				if(mdp.isEmpty()){


					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)enterNumber.getLayoutParams();
					RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)enterNumber.getLayoutParams();
					params.height=60; 
					params1.width=editSearch.getWidth();
					enterNumber.setLayoutParams(params);
					enterNumber.setLayoutParams(params1);
				}else{
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)enterNumber.getLayoutParams();
					RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)enterNumber.getLayoutParams();
					params.height=0; 
					params1.width=editSearch.getWidth();
					enterNumber.setLayoutParams(params);
					enterNumber.setLayoutParams(params1);
				}
				mdp.notifyDataSetChanged();
			}
		});

		editSearch.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				String[] strSearch=sb.toString().split(",");
				String[] strSearchsb1=sb1.toString().split(",");
				String[] name=nameToSave.toString().split(",");
				String[] contact=numberToSave.toString().split(",");
				String lastString = "";
				int length=strSearch.length;
				int length1=name.length;
				int length2=contact.length;
				int lengthsb1=strSearchsb1.length;

				lastString=strSearch[length-1];
				String lastString1=name[length1-1];
				String lastString2=contact[length2-1];
				String lastStringsb1=strSearchsb1[lengthsb1-1];

				if(event.getUnicodeChar() == 
						(int)EditableAccomodatingLatinIMETypeNullIssues.ONE_UNPROCESSED_CHARACTER.charAt(0))
				{
					//We are ignoring this character, and we want everyone else to ignore it, too, so 
					// we return true indicating that we have handled it (by ignoring it).   
					return true; 
				}


				if(event.getAction()==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL){
					if(editSearch.getText().toString().equals(sb1.toString())){
						if(sb.toString().contains(lastString)){
							if(lastString!=""){
								int stard=sb.toString().indexOf(lastString);
								int strLength=lastString.length();
								sb.replace(stard, (stard+strLength+1), "");

								stard=sb1.toString().indexOf(lastStringsb1);
								strLength=lastStringsb1.length();
								sb1.replace(stard, (stard+strLength+1), "");

								stard=nameToSave.toString().indexOf(lastString1);
								strLength=lastString1.length();
								nameToSave.replace(stard, (stard+strLength+1), "");
								stard=numberToSave.toString().indexOf(lastString2);
								strLength=lastString2.length();
								numberToSave.replace(stard, (stard+strLength+1), "");
								count=0;
								editSearch.setText(sb1);
								editSearch.append(",");
							}
						}
					}
					else{
						editSearch.getText().insert(editSearch.getText().length()-1, "");
						//edittext.setText(sb);
					}
					editSearch.setSelection(editSearch.getText().length());
				}




				return false;
			}
		});
		editSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editSearch.setSelection(editSearch.getText().length());
			}
		});

	}

	protected void buttonClicked(){
		back.setOnClickListener( new OnClickListener(){
			public void onClick(View viewParam) {
				getMessage();
				finish();
				overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_left);
			}
		});
	}
	protected void getMessage(){
		SharedPreferences.Editor editor = remindMessagePref.edit();
		if(editSearch.getText().toString().equals("")||editSearch.getText().toString().equals(" ")){
			editor.putString("combine_contact","no");
			editor.putString("reminder_contact","no");
			editor.putString("reminder_contact_name","no");
			editor.putString("reminder_contact_number","no");
		}else{
			if( nameToSave.length()==0 && numberToSave.length()==0){
				editor.putString("combine_contact",editSearch.getText().toString()+"("+enterNumber.getText().toString()+")");
				editor.putString("reminder_contact","yes");
				editor.putString("reminder_contact_name",editSearch.getText().toString());
				editor.putString("reminder_contact_number",enterNumber.getText().toString());

			}else{
				editor.putString("combine_contact",editSearch.getText().toString());
				editor.putString("reminder_contact","yes");
				editor.putString("reminder_contact_name",nameToSave.toString());
				editor.putString("reminder_contact_number",numberToSave.toString()); 

			}
		}
		editor.commit();
	}
	protected void getTextViewImages(){
		wakeup_top = (TextView )findViewById(R.id.textView1);
		back = (TextView )findViewById(R.id.textView4);
		contact_image = (TextView )findViewById(R.id.textView2);
		editSearch = (EditText) findViewById(R.id.editText1);
		enterNumber= (EditText) findViewById(R.id.editText2);

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
			denisityBitmap[1]=themeImages[0];
		}
		if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
			wakeup_top.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[0]));
			back.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[1]));
			contact_image.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[2]));
			editSearch.setBackgroundDrawable(new BitmapDrawable(getResources(),denisityBitmap[3]));


		}	 else {
			wakeup_top.setBackground(new BitmapDrawable(getResources(),denisityBitmap[0]));
			back.setBackground(new BitmapDrawable(getResources(),denisityBitmap[1]));
			contact_image.setBackground(new BitmapDrawable(getResources(),denisityBitmap[2]));
			editSearch.setBackground(new BitmapDrawable(getResources(),denisityBitmap[3]));

		} 
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			getMessage();
			finish();
			overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_left);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	} 

	public class MyCustomAdapter extends BaseAdapter{
		String[] data_text;
		String[] data_text2;

		MyCustomAdapter()
		{

		}

		MyCustomAdapter(String[] text, String[] text1)
		{
			data_text = text;
			data_text2 = text1;
		}
		MyCustomAdapter(ArrayList text, ArrayList text1)
		{
			data_text = new String[text.size()];
			data_text2 = new String[text1.size()];

			for(int i=0; i<text.size(); i++)
			{
				data_text[i] = (String) text.get(i);
				data_text2[i] = (String) text1.get(i);
			}

		}

		public int getCount()
		{
			return data_text.length;
		}

		public String getItem(int position)
		{
			return null;
		}

		public long getItemId(int position)
		{
			return position;
		}
		@Override
		public View getView(int j, View arg1, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row;

			row = inflater.inflate(R.layout.sub_items_layout, parent,false);

			TextView textview = (TextView) row.findViewById(R.id.textView2);
			TextView textview2 = (TextView) row.findViewById(R.id.textView1);
			TextView textview3 = (TextView) row.findViewById(R.id.textView3);
			textview.setText(data_text[j]);
			textview2.setText(data_text2[j]);
			if(sb.toString().contains(textview2.getText().toString())){
				textview3.setBackgroundResource(R.drawable.check_box);
			}

			return row;
		}
	}
	/*------------------------------background task starts-----------------------------*/

	private class DoInBackground extends AsyncTask<Void, Void, Void>
	implements DialogInterface.OnCancelListener{
		private ProgressDialog dialog;
		protected void onPreExecute() {
			dialog = ProgressDialog.show(SelectContact.this, "", "Loading. Please wait...", true);
		}
		protected Void doInBackground(Void... unused) {			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			new Thread() {
				public void run() {
					SelectContact.this.runOnUiThread(new Runnable(){
						@Override
						public void run(){
							contact();
						}  
					});
				}
			}.start();	

			return null;
		}
		protected void onPostExecute(Void unused) {
			dialog.dismiss();
		}
		public void onCancel(DialogInterface dialog) {
			cancel(true);
			dialog.dismiss();
		}
	}
	/*------------------------------background task ends-----------------------------*/


	@Override
	protected void onResume(){
		super.onResume();

		new DoInBackground().execute();

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
