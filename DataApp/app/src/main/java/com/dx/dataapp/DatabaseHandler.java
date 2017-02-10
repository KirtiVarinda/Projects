package com.dx.dataapp;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dx.model.Fixr;
import com.dx.model.FixrImage;
import com.dx.model.FixrLocation;
import com.dx.model.FixrMobile;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;


    private static final String FIXR_DB = "FixrDB";
    static final String TABLE_FIXR = "tblFixr";
    static final String TABLE_FIXR_IMAGES = "tblFixrImages";
    static final String TABLE_LOCATION= "tblFixrLocation";
    static final String TABLE_USER_MOBILE= "TABLE_USER_MOBILE";

    private static final String FIXR_ID = "fixr_id";
    private static final String FIXR_NAME = "fixr_name";
    private static final String FIXR_PHONE = "fixr_phone";
    private static final String FIXR_ADDRESS = "fixr_address";
    private static final String FIXR_CITY = "fixr_city";
    private static final String FIXR_WORK_TYPE = "fixr_work_type";
    private static final String FIXR_TRADE = "fixr_trade";
    private static final String FIXR_LEVEL = "fixr_level";
    private static final String FIXR_WORK_HOURS = "fixr_work_hours";
    private static final String FIXR_COST = "fixr_cost";
    private static final String FIXR_CERTIFICATION = "fixr_certification";
    private static final String FIXR_EXPERIENCE = "fixr_experience";
    private static final String FIXR_VERIFY = "fixr_verify";
    private static final String FIXR_LOGIN_USER = "fixr_login_user";
    private static final String FIXR_DATA_LATITUDE = "fixr_data_latitude";
    private static final String FIXR_DATA_LONGITUDE = "fixr_data_longitude";

    private static final String FIXR_IMAGE_ID = "fixr_image_id";
    private static final String FIXR_IMAGE_URI = "fixr_image";
    private static final String FIXR_IMAGE_NAME = "fixr_image_name";

    private static final String FIXR_LOCATION_ID = "fixr_location_id";
    private static final String FIXR_USER = "fixr_user";
    private static final String FIXR_LATITUDE = "fixr_latitude";
    private static final String FIXR_LONGITUDE = "fixr_longitude";
    private static final String FIXR_TIME = "fixr_time";

    private static final String FIXR_MOBILE_ID = "fixr_mobile_id";
    private static final String FIXR_MOBILE_NUMBER = "fixr_mobile_number";

    public boolean isThere;
    private static String KEY_COUNT_ID = "countId";


    public DatabaseHandler(Context context) {
        super(context, FIXR_DB, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        String CREATE_FIXR_TABLE = "CREATE TABLE " + TABLE_FIXR + "("
                + FIXR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FIXR_NAME + " TEXT,"
                + FIXR_PHONE + " TEXT,"
                + FIXR_ADDRESS + " TEXT,"
                + FIXR_CITY + " TEXT,"
                + FIXR_WORK_TYPE + " TEXT,"
                + FIXR_TRADE + " TEXT,"
                + FIXR_LEVEL + " TEXT,"
                + FIXR_WORK_HOURS + " TEXT,"
                + FIXR_COST + " TEXT,"
                + FIXR_CERTIFICATION + " TEXT, "
                + FIXR_EXPERIENCE + " TEXT, "
                + FIXR_VERIFY + " TEXT,"
                + FIXR_LOGIN_USER + " TEXT,"
                + FIXR_DATA_LATITUDE + " TEXT,"
                + FIXR_DATA_LONGITUDE + " TEXT"
                + ")";

        String CREATE_FIXR_IMAGE_TABLE = "CREATE TABLE " + TABLE_FIXR_IMAGES + "("
                + FIXR_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FIXR_IMAGE_NAME + " TEXT,"
                + FIXR_IMAGE_URI + " TEXT" + ")";

        String CREATE_TABLE_LOCATION = "CREATE TABLE " + TABLE_LOCATION + "("
                + FIXR_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FIXR_USER + " TEXT,"
                + FIXR_LATITUDE + " TEXT,"
                + FIXR_LONGITUDE + " TEXT,"
                + FIXR_TIME + " TEXT" + ")";


        String CREATE_TABLE_MOBILE = "CREATE TABLE " + TABLE_USER_MOBILE + "("
                + FIXR_MOBILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FIXR_MOBILE_NUMBER + " TEXT" + ")";

        db.execSQL(CREATE_FIXR_TABLE);
        db.execSQL(CREATE_FIXR_IMAGE_TABLE);
        db.execSQL(CREATE_TABLE_LOCATION);
        db.execSQL(CREATE_TABLE_MOBILE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIXR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIXR_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_MOBILE);
        // Create tables again
        onCreate(db);
    }


    /**
     * delete a single row from fixr table
     *
     * @param id
     * @return
     */
    public int deleteFixrDetails(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int a = db.delete(TABLE_FIXR, FIXR_ID + " = ?", new String[]{String.valueOf(id)});

        //db.close();
        return a;
    }

    /**
     * delete a single row from image table
     * @param id
     * @return
     */
    public int deleteFixrImage(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        int a = 0;

        a = db.delete(TABLE_FIXR_IMAGES, FIXR_IMAGE_ID + " = ?", new String[]{String.valueOf(id)});


        //db.close();
        return a;
    }
    /**
     * delete a single row from location table
     *
     * @return
     */
    public int deleteRowFromLocationTrackTable(String strId) {

        int id=Integer.parseInt(strId);
        SQLiteDatabase db = this.getWritableDatabase();
        int a = db.delete(TABLE_LOCATION, FIXR_LOCATION_ID + " = ?", new String[]{String.valueOf(id)});

       // db.close();
        return a;
    }

    /**
     * get length of  fixr rows
     *
     * @return
     */
    public int getSavedFixrLength() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + TABLE_FIXR;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        //db.close();
        return count;
    }

    /**
     * get length of images rows
     *
     * @return
     */
    public int getSavedImagesLength() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + TABLE_FIXR_IMAGES;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        //db.close();
        return count;
    }

    /**
     * add a fixr image row in database
     *
     * @param fImg
     */
    public long saveFixrImage(FixrImage fImg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIXR_IMAGE_NAME, fImg.getImageName());
        values.put(FIXR_IMAGE_URI, fImg.getImageUri());
        long id = db.insert(TABLE_FIXR_IMAGES, null, values);
        //   isThere = false;
       // db.close();

        // }
        // else{
        // isThere=true;
        // }
        return id;
    }

    /**
     * add a fixr image row in database
     *
     * @param fImg
     */
    public long saveTabLocation(FixrLocation fImg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIXR_USER, fImg.getUserName());
        values.put(FIXR_LATITUDE, fImg.getLatitude());
        values.put(FIXR_LONGITUDE, fImg.getLongitude());
        values.put(FIXR_TIME, fImg.getTimeIniliSec());
        long id = db.insert(TABLE_LOCATION, null, values);
        //   isThere = false;
       // db.close();

        // }
        // else{
        // isThere=true;
        // }
        return id;
    }

    /**
     * add a fixr detail row inj database
     *
     * @param f
     * @return
     */
    public long saveFixrMobile(FixrMobile f) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIXR_MOBILE_NUMBER, f.getmMobileNumber());
        long id = db.insert(TABLE_USER_MOBILE, null, values);
       // db.close();

        return id;
    }
    /**
     * add a fixr detail row inj database
     *
     * @param f
     * @return
     */
    public long saveFixr(Fixr f) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIXR_NAME, f.getName());
        values.put(FIXR_ADDRESS, f.getAddress());
        values.put(FIXR_CITY, f.getCity());
        values.put(FIXR_PHONE, f.getPhone());
        values.put(FIXR_WORK_TYPE, f.getWork_type());
        values.put(FIXR_TRADE, f.getTrade());
        values.put(FIXR_LEVEL, f.getLevel());
        values.put(FIXR_WORK_HOURS, f.getWork_hours());
        values.put(FIXR_COST, f.getCost());
        values.put(FIXR_CERTIFICATION, f.getCertification());
        values.put(FIXR_EXPERIENCE, f.getExperience());
        values.put(FIXR_VERIFY, f.getVerification());
        values.put(FIXR_LOGIN_USER, f.getLoginUser());
        values.put(FIXR_DATA_LATITUDE, f.getDataLatitude());
        values.put(FIXR_DATA_LONGITUDE, f.getDataLongitude());
        // if (!checkRecordExist(TABLE_PLAN, new String[] {KEY_ROLL}, new
        // String[] {String.valueOf(studentrecord.getsRollNumber())})) {
        long id = db.insert(TABLE_FIXR, null, values);
        //   isThere = false;
        //db.close();
        System.out.println("row id is: " + id);
        // }
        // else{
        // isThere=true;
        // }
        return id;
    }


    /**
     * get a single record from fixr table
     *
     * @param id
     * @return
     */
    public FixrImage getSingleFixrImage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        FixrImage fixrImg = null;
        Cursor cursor = db.query(TABLE_FIXR_IMAGES, new String[]{FIXR_IMAGE_NAME,
                        FIXR_IMAGE_URI}, FIXR_IMAGE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            fixrImg = new FixrImage(cursor.getString(0), cursor.getString(1));
        }
        cursor.close();
       // db.close();
        return fixrImg;
    }


    /**
     * get a single record from fixr image table
     *
     * @param id
     * @return
     */
    public Fixr getSingleFixr(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Fixr fixr = null;
        Cursor cursor = db.query(TABLE_FIXR, new String[]{FIXR_NAME,
                        FIXR_PHONE, FIXR_ADDRESS, FIXR_CITY, FIXR_WORK_TYPE,
                        FIXR_TRADE, FIXR_LEVEL, FIXR_WORK_HOURS, FIXR_COST, FIXR_CERTIFICATION,
                        FIXR_EXPERIENCE, FIXR_VERIFY}, FIXR_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            fixr = new Fixr(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5),
                    cursor.getString(6), cursor.getString(7),
                    cursor.getString(8), cursor.getString(9),
                    cursor.getString(10), cursor.getString(11),
                    cursor.getString(12), cursor.getString(13),
                    cursor.getString(14));
        }
        cursor.close();
        //db.close();
        return fixr;
    }

    /**
     * get all records from fixr image table
     *
     * @return List
     */
    public List<FixrImage> getAllFixrImages() {
        List<FixrImage> fixrImgList = new ArrayList<FixrImage>();
        String selectquery = "SELECT * FROM " + TABLE_FIXR_IMAGES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectquery, null);
        if (cursor.moveToFirst()) {
            do {
                FixrImage fixrImg = new FixrImage();
                fixrImg.setImageId(Integer.parseInt(cursor.getString(0)));
                fixrImg.setImageName(cursor.getString(1));
                fixrImg.setImageUri(cursor.getString(2));

                fixrImgList.add(fixrImg);
            } while (cursor.moveToNext());
        }
        cursor.close();
       // db.close();
        return fixrImgList;
    }



    /**
     *get all the tab locations data from the database
     *
     */
    public List<FixrLocation> get100TabLocations() {
        List<FixrLocation> fixrLocation = new ArrayList<FixrLocation>();
        String selectquery = "SELECT * FROM " +TABLE_LOCATION+" ORDER BY "+FIXR_TIME+" ASC LIMIT 100";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectquery, null);
        if (cursor.moveToFirst()) {
            do {
                FixrLocation fixrImg = new FixrLocation();
                fixrImg.setLocationid(Integer.parseInt(cursor.getString(0)));
                fixrImg.setUserName(cursor.getString(1));
                fixrImg.setLatitude(cursor.getString(2));
                fixrImg.setLongitude(cursor.getString(3));
                fixrImg.setTimeIniliSec(cursor.getString(4));
                fixrLocation.add(fixrImg);
            } while (cursor.moveToNext());
        }
        cursor.close();
       // db.close();
        return fixrLocation;
    }



    /**
     * get all records from fixr table
     *
     * @return List
     */
    public List<Fixr> getAllFixrs() {
        List<Fixr> fixrList = new ArrayList<Fixr>();
        String selectquery = "SELECT * FROM " + TABLE_FIXR;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectquery, null);
        if (cursor.moveToFirst()) {
            do {
                Fixr fixr = new Fixr();
                fixr.setId(Integer.parseInt(cursor.getString(0)));
                fixr.setName(cursor.getString(1));
                fixr.setPhone(cursor.getString(2));
                fixr.setAddress(cursor.getString(3));
                fixr.setCity(cursor.getString(4));

                fixr.setWork_type(cursor.getString(5));
                fixr.setTrade(cursor.getString(6));
                fixr.setLevel(cursor.getString(7));
                fixr.setWork_hours(cursor.getString(8));
                fixr.setCost(cursor.getString(9));
                fixr.setCertification(cursor.getString(10));
                fixr.setExperience(cursor.getString(11));
                fixr.setVerification(cursor.getString(12));
                fixr.setLoginUser(cursor.getString(13));
                fixr.setDataLatitude(cursor.getString(14));
                fixr.setDataLongitude(cursor.getString(15));
                fixrList.add(fixr);
            } while (cursor.moveToNext());
        }
        cursor.close();
        //db.close();
        return fixrList;
    }
    /**
     * get all records from fixr image table
     *
     * @return List
     */
    public List<FixrMobile> getAllFixrMObile() {
        List<FixrMobile> fixrMobList = new ArrayList<FixrMobile>();
        FixrMobile fixrMob;
        String selectquery = "SELECT * FROM " + TABLE_USER_MOBILE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectquery, null);
        if (cursor.moveToFirst()) {
            do {
                fixrMob = new FixrMobile();
                fixrMob.setmMobileid(cursor.getString(0));
                fixrMob.setmMobileNumber(cursor.getString(1));
                fixrMobList.add(fixrMob);
            } while (cursor.moveToNext());
        }
        cursor.close();
        //db.close();
        return fixrMobList;
    }

    public boolean CheckIsDataAlreadyInDBorNot(String fieldValue) {

        String Query = "Select * from " + TABLE_USER_MOBILE + " where " + FIXR_MOBILE_NUMBER + " like '%" + fieldValue+"%'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
    /**
     * private boolean checkRecordExist(String tableName, String[] keys, String
     * [] values) { SQLiteDatabase db = this.getWritableDatabase();
     * StringBuilder sb = new StringBuilder(); for (int i = 0; i < keys.length;
     * i++) { sb.append(keys[i]) .append("=\"") .append(values[i])
     * .append("\" "); if (i<keys.length-1) sb.append("AND "); }
     *
     * Cursor cursor = db.query(tableName, null, sb.toString(), null, null,
     * null, null); boolean exists = (cursor.getCount() > 0); cursor.close();
     *
     * return exists; } StudentRecords checkRecord(int sroll){ SQLiteDatabase
     * db=this.getReadableDatabase(); StudentRecords studentrecord=null; if
     * (!checkRecordExist(TABLE_RECORDS, new String[] {KEY_ROLL}, new String[]
     * {String.valueOf(sroll) })) {
     *
     * return null; } else{
     *
     * return studentrecord; }
     *
     *
     * }
     */


}

