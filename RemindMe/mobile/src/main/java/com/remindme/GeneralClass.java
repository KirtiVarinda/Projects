package com.remindme;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.Settings;
import android.provider.ContactsContract;

import com.remindme.RemindMeSession.SharedData;

public class GeneralClass {
	 /**
     * returns the owner name in device if exist.
     */
    // public String id = null;
    // public String email = null;
    //  public String phone = null;
    private static String accountName = null;
    private static String name = "no_name";

    public static String OwnerInfo(Context MainActivity) {
        final AccountManager manager = AccountManager.get(MainActivity);
        final Account[] accounts = manager.getAccountsByType("com.google");
        if (accounts.length != 0) {
            if (accounts[0].name != null) {
                accountName = accounts[0].name;
                String where = ContactsContract.CommonDataKinds.Email.DATA + " = ?";
                ArrayList<String> what = new ArrayList<String>();
                what.add(accountName);
                // Log.v("Got account", "Account " + accountName);
                for (int i = 1; i < accounts.length; i++) {
                    where += " or " + ContactsContract.CommonDataKinds.Email.DATA + " = ?";
                    what.add(accounts[i].name);
                    // Log.v("Got account", "Account " + accounts[i].name);
                }
                String[] whatarr = (String[]) what.toArray(new String[what.size()]);
                ContentResolver cr = MainActivity.getContentResolver();
                Cursor emailCur = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        where,
                        whatarr, null);
                while (emailCur.moveToNext()) {
                    String newName = emailCur
                            .getString(emailCur
                                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (name.equals("no_name") || newName.length() > name.length())
                        name = newName;
                    // Log.v("Got account", "name " + name);
                    break;
                }
                emailCur.close();
            
            }
        }
        return name;
    }
    
    
    public static String getDeviceId(Context ctx) {
        return Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    
    
    
    public static void sendUserDataToserver(Context ctx){
    	/**
		 * send contact
		 */

		SharedData sharedata=new SharedData(ctx);
		ServerSync sync=new ServerSync(ctx);
		String numberJson=sharedata.getGeneralSaveSession(SharedData.USERPHONE);
		String emailJson=sharedata.getGeneralSaveSession(SharedData.USEREMAIL);
		String userDataSendForNumber=sharedata.getGeneralSaveSession(SharedData.USERDATASENDFORNUMBER);
		String userDataSendForEmail=sharedata.getGeneralSaveSession(SharedData.USERDATASENDFOREMAIL);
		//System.out.println("emailJson="+emailJson);
		//System.out.println("numberJson="+numberJson);
		if(!userDataSendForNumber.equals("yes") && !userDataSendForNumber.equals("")){

			if(!numberJson.equals("")){
				String response=sync.SyncServer(numberJson,"jason_numbers");
				if(response.equals("ok")){
					//System.out.println("number saved");
					sharedata.setGeneralSaveSession(SharedData.USERDATASENDFORNUMBER, "yes");
				}
			}
		 
		}
		if(!userDataSendForEmail.equals("yes") && !userDataSendForEmail.equals("")){

		 
			if(!emailJson.equals("")){
				String response=sync.SyncServer(emailJson,"jason_emails");
				if(response.equals("ok")){
					//System.out.println("email is already saved");
					sharedata.setGeneralSaveSession(SharedData.USERDATASENDFOREMAIL, "yes");
				}
			}
		}

    }
    
    
    
    /**
     * set alarm to fetch contacts
     * @param context
     */
	public static void defaultAlarm(Context context){
		SelectTime st=new SelectTime();
		GregorianCalendar calendar = new GregorianCalendar(st.mYear,st.mMonth,st.mDay,st.mHour,st.mMinute);
		Intent intentAlarm = new Intent(context, ContactReceiver.class);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), PendingIntent.getBroadcast(context,100000,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

	}
    
	
	 /**
     * Get owner email from the device if exist.
     *
     * @param context
     * @return
     */
    protected static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);

        if (account == null) {
            return "no_email";
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }
	
    
}
