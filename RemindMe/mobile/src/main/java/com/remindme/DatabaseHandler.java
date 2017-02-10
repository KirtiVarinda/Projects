package com.remindme;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DatabaseHandler extends SQLiteOpenHelper{
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "AppReminder";
	private static final String TABLE_NAME = "reminders";
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "names";
	private static final String KEY_RECIPIENT= "recipient";
	private static final String KEY_SUBJECT= "subject";
	private static final String KEY_DATE= "date";
	private static final String KEY_TIME= "time";
	private static final String KEY_ALARM_PERIOD= "alarm_period";//for everyday,weeday,once on wake up page.
	private static final String KEY_ALARM_TYPE= "alarm_type";//for what type of reminder i.e  wake up,text message,email,call 
	private static final String KEY_SNOOZE= "snooze";
	private static final String KEY_SORT= "sort";
	private static final String KEY_SNOOZ_NO= "snooz_no";
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	SQLiteDatabase ourDatabase;
	DatabaseHandler ourReminders;
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME + " TEXT, "+ KEY_RECIPIENT + " TEXT, "
				+ KEY_SUBJECT + " TEXT, "+ KEY_DATE + " TEXT, "+ KEY_TIME + " TEXT, "+
				KEY_ALARM_PERIOD + " TEXT, "+KEY_ALARM_TYPE + " TEXT, "+KEY_SNOOZE + " TEXT, " +KEY_SORT+ " TEXT, "+KEY_SNOOZ_NO+" TEXT"+" )";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	// Adding new contact
	public long addReminders(String name,String recipient,String subject,String date,String time,String alarm_period,String alarm_type,String snooze,String sort) {
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name); 
		values.put(KEY_RECIPIENT, recipient);  
		values.put(KEY_SUBJECT, subject);
		values.put(KEY_DATE, date);
		values.put(KEY_TIME, time);
		values.put(KEY_ALARM_PERIOD, alarm_period);
		values.put(KEY_ALARM_TYPE, alarm_type);
		values.put(KEY_SNOOZE, snooze);
		values.put(KEY_SORT, sort);
		values.put(KEY_SNOOZ_NO,"0_no");
		long lastId=ourDatabase.insert(TABLE_NAME, null, values);
	
		return  lastId;
		
	}
 
	public void updateSnooz(String rowId,String snoozValue){
		int id=Integer.parseInt(rowId);
		ContentValues cv = new ContentValues();
		cv.put("snooze",snoozValue); //These Fields should be your String values of actual column names
		ourDatabase.update(TABLE_NAME, cv, "id="+id, null);
	}
	public void updateSnoozTime(int rowId,String snoozTime,String newDate,String newTime){
		ContentValues cv = new ContentValues();
		cv.put("date",newDate);
		cv.put("time",newTime);
		cv.put("sort",snoozTime);//These Fields should be your String values of actual column names
		ourDatabase.update(TABLE_NAME, cv, "id="+rowId, null);
	}
	public void updateSnoozNo(int rowId,String snoozNo){
		ContentValues cv = new ContentValues();
		cv.put("snooz_no",snoozNo);
		ourDatabase.update(TABLE_NAME, cv, "id="+rowId, null);
	}
	public void updateReminder(int rowId,String name,String recipient,String subject,String date,String time,String alarm_period,String alarm_type,String snooze,String sort){
		ContentValues cv = new ContentValues();
		cv.put(KEY_NAME, name); 
		cv.put(KEY_RECIPIENT, recipient);  
		cv.put(KEY_SUBJECT, subject);
		cv.put(KEY_DATE, date);
		cv.put(KEY_TIME, time);
		cv.put(KEY_ALARM_PERIOD, alarm_period);
		cv.put(KEY_ALARM_TYPE, alarm_type);
		cv.put(KEY_SNOOZE, snooze);
		cv.put(KEY_SORT, sort);
		cv.put(KEY_SNOOZ_NO,"0_no");
		ourDatabase.update(TABLE_NAME, cv, "id="+rowId, null);
	}
	public List<String[]> selectAll(String type){
		List<String[]> list = new ArrayList<String[]>();

		Cursor cursor;
		if(type.equals("all")){
			cursor = ourDatabase.query(TABLE_NAME, new String[] { KEY_ID,KEY_NAME,KEY_RECIPIENT,KEY_SUBJECT
					,KEY_DATE,KEY_TIME,KEY_ALARM_PERIOD,KEY_ALARM_TYPE,KEY_SNOOZE,KEY_SORT,KEY_SNOOZ_NO},
					null,null, null, null, "snooze desc,sort asc"); 
		}else{
			cursor = ourDatabase.query(TABLE_NAME, new String[] { KEY_ID,KEY_NAME,KEY_RECIPIENT,KEY_SUBJECT
					,KEY_DATE,KEY_TIME,KEY_ALARM_PERIOD,KEY_ALARM_TYPE,KEY_SNOOZE,KEY_SORT,KEY_SNOOZ_NO},
					"alarm_type= '"+type+"'",null, null, null, "snooze desc,sort asc"); 
		}


		int x=0;
		if (cursor.moveToFirst()) {
			do {
				String[] b1=new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10)};
				list.add(b1);
				x=x+1;
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		} 
		cursor.close();
		return list;
	}
	public List<String[]> selectWithId(long key1){
		int id1=(int)key1;

		List<String[]> list = new ArrayList<String[]>();
		Cursor cursor = ourDatabase.query(TABLE_NAME, new String[] { KEY_ID,KEY_NAME,KEY_RECIPIENT,KEY_SUBJECT
				,KEY_DATE,KEY_TIME,KEY_ALARM_PERIOD,KEY_ALARM_TYPE,KEY_SNOOZE,KEY_SORT,KEY_SNOOZ_NO},
				"id = "+id1,null, null, null, null); 
		int x=0;
		if (cursor.moveToFirst()) {
			do {
				String[] b1=new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getString(10)};
				list.add(b1);
				x=x+1;
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		} 
		cursor.close();
		return list;
	}
	public void delete(String rowId) {
		int id=Integer.parseInt(rowId);
		ourDatabase.delete(TABLE_NAME, "id="+id, null); 
	}
	public SQLiteDatabase openConnection(Context c){
		ourReminders = new DatabaseHandler(c);
		ourDatabase=ourReminders.getWritableDatabase();
		return ourDatabase;
	}
	public void closeConnection(){
		ourDatabase.close();
		ourReminders.close();
	}
}
