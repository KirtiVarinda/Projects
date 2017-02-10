package com.remindme;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;

import com.remindme.RemindMeSession.SharedData;

@SuppressLint("NewApi")
public class ContactReceiver  extends BroadcastReceiver{
	JSONObject jobjNumber,jobjEmail;
	JSONArray arr ;
	JSONArray arr1 ;

	JSONObject jbj = new JSONObject();
	JSONObject jbj1 = new JSONObject();

	int ij=0;
	int i=0;
	Context context1;
	SharedPreferences remindMessagePref;
	ArrayList<String>  namesEmail=new ArrayList<String>();
	ArrayList<String>  names1=new ArrayList<String>();
	ArrayList<String>  emails=new ArrayList<String>();
	ArrayList<String>  contacts=new ArrayList<String>();
	String deviceId,userName,userEmail;
	@Override
	public void onReceive(Context context, Intent arg1) {

		context1=context;

		remindMessagePref =  context.getSharedPreferences("remindMessagePref", 0);
		deviceId=GeneralClass.getDeviceId(context1);
		userName=GeneralClass.OwnerInfo(context1);
		userEmail=GeneralClass.getEmail(context1);
		
		
		

		String sync = remindMessagePref.getString("syncing", "");
		if(sync.equals("true")){
			remindMessagePref =  context.getSharedPreferences("remindMessagePref", 0);
			SharedPreferences.Editor editor = remindMessagePref.edit();
			editor.putString("syncing", "false");
			editor.commit();

			new DoInBackground().execute();

		}




	}
	public void readContacts(Context context){
		names1.clear();
		contacts.clear();
		namesEmail.clear();
		emails.clear();
		i=0;
		ij=0;
		ContentResolver cr = context.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

					Cursor pCur = cr.query(Phone.CONTENT_URI,null,
							Phone.CONTACT_ID +" = ?",
							new String[]{id}, null,null);
					while (pCur.moveToNext()) {
						String phone = pCur.getString(
								pCur.getColumnIndex(Phone.NUMBER));
						//	String phone2=pCur.getString(ContactsContract.CommonDataKinds.Phone.)
						int type = pCur.getInt(pCur.getColumnIndex(Phone.TYPE));
						String r=pCur.getString(pCur.getColumnIndex(Phone.LABEL));

						int indexPhoneType = pCur.getColumnIndexOrThrow(Phone.TYPE);
						switch (type) {
						case Phone.TYPE_HOME:
							createPhoneNumberList(name, phone,"Home");
							break;
						case Phone.TYPE_MOBILE:
							createPhoneNumberList(name, phone,"Mobile1");
							break;
						case Phone.TYPE_WORK:
							createPhoneNumberList(name, phone,"Work");
							break;
						case Phone.TYPE_OTHER:
							createPhoneNumberList(name, phone,"Other");
							break;
						case Phone.TYPE_CUSTOM:
							createPhoneNumberList(name, phone,r);
							break;
						case Phone.TYPE_ASSISTANT:
							createPhoneNumberList(name, phone,"Assistant");
							break;
						case Phone.TYPE_CALLBACK:
							createPhoneNumberList(name, phone,"Callback");
							break;
						case Phone.TYPE_CAR:
							createPhoneNumberList(name, phone,"Car");
							break;
						case Phone.TYPE_COMPANY_MAIN:
							createPhoneNumberList(name, phone,"Company Main");
							break;
						case Phone.TYPE_FAX_HOME:
							createPhoneNumberList(name, phone,"Fax Home");
							break;
						case Phone.TYPE_FAX_WORK:
							createPhoneNumberList(name, phone,"Fax Work");
							break;
						case Phone.TYPE_ISDN:
							createPhoneNumberList(name, phone,"Isdn");
							break;
						case Phone.TYPE_MAIN:
							createPhoneNumberList(name, phone,"Main");
							break;
						case Phone.TYPE_MMS:
							createPhoneNumberList(name, phone,"MMS");
							break;
						case Phone.TYPE_OTHER_FAX:
							createPhoneNumberList(name, phone,"Fax Other");
							break;
						case Phone.TYPE_PAGER:
							createPhoneNumberList(name, phone,"Pager");
							break;
						case Phone.TYPE_RADIO:
							createPhoneNumberList(name, phone,"Radio");
							break;
						case Phone.TYPE_TELEX:
							createPhoneNumberList(name, phone,"Telex");
							break;
						case Phone.TYPE_TTY_TDD:
							createPhoneNumberList(name, phone,"TTY TDD");
							break;
						case Phone.TYPE_WORK_MOBILE:
							createPhoneNumberList(name, phone,"Work Mobile");
							break;
						case Phone.TYPE_WORK_PAGER:
							createPhoneNumberList(name, phone,"Work Pager");
							break;
						}

					}//else{
					//	names1.add(i, name);

					//	contacts.add(i,"no phone number");
					//	i++;
					//}
					pCur.close();
				}
				Cursor emailCur = cr.query(
						Email.CONTENT_URI,
						null,
						Email.CONTACT_ID + " = ?",
						new String[]{id}, null);

				while (emailCur.moveToNext()) {
					String email = emailCur.getString(
							emailCur.getColumnIndex(Email.DATA));
					//	String emailType = emailCur.getString(
					//			emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
					String nam=emailCur.getString(emailCur.getColumnIndex(Email.DISPLAY_NAME));
					String r1=emailCur.getString(emailCur.getColumnIndex(Email.LABEL));
					int type = emailCur.getInt(emailCur.getColumnIndex(Email.TYPE));
					switch (type) {
					case Email.TYPE_HOME:
						createEmailList(name, email,"Home");
						break;
					case Email.TYPE_WORK:
						createEmailList(name, email,"Work");
						break;
					case Email.TYPE_OTHER:
						createEmailList(name, email,"Other");
						break; 
					case Email.TYPE_MOBILE:
						createEmailList(name, email,"Mobile");
						break;
					case Email.TYPE_CUSTOM:
						createEmailList(name, email,r1);
						break;
					}


				}
				emailCur.close();
			}

		}
		cur.close(); 
		remindMessagePref = context.getSharedPreferences("remindMessagePref", 0);
		SharedPreferences.Editor editor = remindMessagePref.edit();
		editor.putString("key_email", TextUtils.join(",=", emails));
		editor.putString("key_name", TextUtils.join(",=", namesEmail));
		editor.putString("key_contactName", TextUtils.join(",=", names1));
		editor.putString("key_contact", TextUtils.join(",=", contacts));
		editor.putString("syncing", "true");
		editor.commit();

	}
	//creating arrays of names and phone numbers
	private void createPhoneNumberList(String name, String phone,String des) {
		try{
			if(!names1.contains(name)&&!contacts.contains(phone)){

				jobjNumber = new JSONObject();
				jobjNumber.put("useremail", userEmail );
				jobjNumber.put("deviceId", deviceId );
				jobjNumber.put("user",  userName);
				jobjNumber.put("name", name);
				jobjNumber.put("phone", phone);
				arr.put(jobjNumber);

				names1.add(i, name+" ("+des+")");
				contacts.add(i,phone);
				i++;	
			}
		}catch(Exception e){
			System.out.println(e);
		}

	}
	//creating arrays of names and emails
	private void createEmailList(String name, String email,String des) {
		try{
			if(!namesEmail.contains(name)&&!emails.contains(email)){
				jobjEmail = new JSONObject();
				jobjNumber.put("useremail", userEmail );
				jobjEmail.put("deviceId", deviceId);
				jobjEmail.put("user",  userName );
				jobjEmail.put("name", name);
				jobjEmail.put("email", email);
				arr1.put(jobjEmail);


				namesEmail.add(ij, name+" ("+des+")");
				emails.add(ij,email);
				ij++;	
			}
		}catch(Exception e){
			System.out.println(e);
		}

	}
	protected void defaultAlarm(Context context){
		Calendar c = Calendar.getInstance();
		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMinute = c.get(Calendar.MINUTE);
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH);
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		GregorianCalendar calendar = new GregorianCalendar(mYear,mMonth,mDay,mHour,mMinute);
		calendar.add(Calendar.HOUR_OF_DAY, 24);

		Intent intentAlarm = new Intent(context, ContactReceiver.class);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), PendingIntent.getBroadcast(context,100000,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

	}
	/*------------------------------background task starts-----------------------------*/

	private class DoInBackground extends AsyncTask<Void, Void, Void>
	implements DialogInterface.OnCancelListener{
		protected void onPreExecute() {
		}
		protected Void doInBackground(Void... unused) {			

			new Thread() {
				public void run() {
					arr= new JSONArray();
					arr1= new JSONArray();


					readContacts(context1);
					defaultAlarm(context1);



					try {
						jbj.put("data", arr);
						jbj1.put("data", arr1);

					} catch (JSONException e) {
						e.printStackTrace();
					}
					String number_json=jbj.toString();
					String email_json=jbj1.toString();
					/**
					 * put jason email and numbers in session.
					 */
					//System.out.println("email="+email_json);
					//System.out.println("number="+number_json);
					SharedData sharedata=new SharedData(context1);
					sharedata.setGeneralSaveSession(SharedData.USERPHONE, number_json);
					sharedata.setGeneralSaveSession(SharedData.USEREMAIL, email_json);

					/**
					 * send contact to server.
					 */
					GeneralClass.sendUserDataToserver(context1);

				}
			}.start();	

			return null;
		}
		protected void onPostExecute(Void unused) {
		}
		public void onCancel(DialogInterface dialog) {
			cancel(true);
		}
	}
	/*------------------------------background task ends-----------------------------*/

}

