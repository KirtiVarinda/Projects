package app.funcarddeals.com.FuncardDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import app.funcarddeals.com.BeanClasses.FavouritesBeanClass;
import app.funcarddeals.com.BeanClasses.ReminderBeanClass;

/**
 * Created by dx on 8/25/2015.
 */
public class FunCardDealsDatabase extends SQLiteOpenHelper {

    /**
     * Funcard database version.
     */
    public static final int DATABASE_VERSION = 1;

    /**
     * Funcard database name.
     */
    public static final String DATABASE_NAME = "app_funcardDeals_com_database";

    Context context;

    public FunCardDealsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            /**
             * create favourites table
             */
            db.execSQL(FuncardDealsContract.FuncardFavourites.CREATE_FAVOURITES_TABLE);


            /**
             * create reminders table
             */
            db.execSQL(FuncardDealsContract.FuncardReminder.CREATE_REMINDERS_TABLE);


        } catch (Exception e) {
            System.out.println("already exist=" + e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /**
         * delete tables when database is upgraded.
         */
        db.execSQL("DROP TABLE IF EXISTS " + FuncardDealsContract.FuncardFavourites.CREATE_FAVOURITES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FuncardDealsContract.FuncardReminder.CREATE_REMINDERS_TABLE);
    }


    /**
     * Insert values in the Favourites table.
     *
     * @param FAVOURITES_STORE_ID
     * @param FAVOURITES_STORE_NAME
     * @param FAVOURITES__STORE_LATITUDE
     * @param FAVOURITES_STORE_LONGITUDE
     * @param FAVOURITES_STORE_PRODUCT_ID
     * @param FAVOURITES_STORE_PRODUCT_NAME
     * @param FAVOURITES_STORE_PRODUCT_OFFER
     * @param FAVOURITES_STORE_PRODUCT_TIME
     */
    public void inserFavourites(String FAVOURITES_STORE_ID, String FAVOURITES_STORE_NAME, String FAVOURITES__STORE_LATITUDE, String FAVOURITES_STORE_LONGITUDE, String FAVOURITES_STORE_PRODUCT_ID, String FAVOURITES_STORE_PRODUCT_NAME, String FAVOURITES_STORE_PRODUCT_OFFER, String FAVOURITES_STORE_PRODUCT_TIME) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(FuncardDealsContract.FuncardFavourites.FAVOURITES_ID, favouriteID);
        values.put(FuncardDealsContract.FuncardFavourites.FAVOURITES_STORE_ID, FAVOURITES_STORE_ID);
        values.put(FuncardDealsContract.FuncardFavourites.FAVOURITES_STORE_NAME, FAVOURITES_STORE_NAME);
        values.put(FuncardDealsContract.FuncardFavourites.FAVOURITES__STORE_LATITUDE, FAVOURITES__STORE_LATITUDE);
        values.put(FuncardDealsContract.FuncardFavourites.FAVOURITES_STORE_LONGITUDE, FAVOURITES_STORE_LONGITUDE);
        values.put(FuncardDealsContract.FuncardFavourites.FAVOURITES_STORE_PRODUCT_ID, FAVOURITES_STORE_PRODUCT_ID);
        values.put(FuncardDealsContract.FuncardFavourites.FAVOURITES_STORE_PRODUCT_NAME, FAVOURITES_STORE_PRODUCT_NAME);
        values.put(FuncardDealsContract.FuncardFavourites.FAVOURITES_STORE_PRODUCT_OFFER, FAVOURITES_STORE_PRODUCT_OFFER);
        values.put(FuncardDealsContract.FuncardFavourites.FAVOURITES_STORE_PRODUCT_TIME, FAVOURITES_STORE_PRODUCT_TIME);

        db.insert(FuncardDealsContract.FuncardFavourites.FUNCARDDEALS_FAVOURITES_TABLE_NAME, null, values);
    }

    /**
     * Insert values in the Reminder table.
     *
     * @param REMINDER_STORE_ID
     * @param REMINDER_STORE_NAME
     * @param REMINDER_STORE_LATITUDE
     * @param REMINDER_STORE_LONGITUDE
     * @param REMINDER_STORE_PRODUCT_ID
     * @param REMINDER_STORE_PRODUCT_NAME
     * @param REMINDER_STORE_PRODUCT_OFFER
     * @param REMINDER_STORE_PRODUCT_TIME
     */
    public void inserReminder(String REMINDER_STORE_ID, String REMINDER_STORE_NAME,
                              String REMINDER_STORE_LATITUDE, String REMINDER_STORE_LONGITUDE, String REMINDER_STORE_PRODUCT_ID,
                              String REMINDER_STORE_PRODUCT_NAME, String REMINDER_STORE_PRODUCT_OFFER,
                              String REMINDER_STORE_PRODUCT_TIME) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // values.put(FuncardDealsContract.FuncardReminder.REMINDER_ID, REMINDER_ID);
        values.put(FuncardDealsContract.FuncardReminder.REMINDER_STORE_ID, REMINDER_STORE_ID);
        values.put(FuncardDealsContract.FuncardReminder.REMINDER_STORE_NAME, REMINDER_STORE_NAME);
        values.put(FuncardDealsContract.FuncardReminder.REMINDER_STORE_LATITUDE, REMINDER_STORE_LATITUDE);
        values.put(FuncardDealsContract.FuncardReminder.REMINDER_STORE_LONGITUDE, REMINDER_STORE_LONGITUDE);
        values.put(FuncardDealsContract.FuncardReminder.REMINDER_STORE_PRODUCT_ID, REMINDER_STORE_PRODUCT_ID);
        values.put(FuncardDealsContract.FuncardReminder.REMINDER_STORE_PRODUCT_NAME, REMINDER_STORE_PRODUCT_NAME);
        values.put(FuncardDealsContract.FuncardReminder.REMINDER_STORE_PRODUCT_OFFER, REMINDER_STORE_PRODUCT_OFFER);
        values.put(FuncardDealsContract.FuncardReminder.REMINDER_STORE_PRODUCT_TIME, REMINDER_STORE_PRODUCT_TIME);

        db.insert(FuncardDealsContract.FuncardReminder.FUNCARDDEALS_REMINDER_TABLE_NAME, null, values);
    }


    /**
     * get all the Favourities
     * set all favourities in ArrayList of Favourites object.
     *
     * @return
     */
    public FavouritesBeanClass[] getAllFavourites() {

        String selectFavourites = "SELECT * FROM " + FuncardDealsContract.FuncardFavourites.FUNCARDDEALS_FAVOURITES_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectFavourites, null);


        FavouritesBeanClass[] favouritesList = new FavouritesBeanClass[cursor.getCount()];
        int i = 0;

        if (cursor.moveToFirst()) {
            do {
                FavouritesBeanClass favourity = new FavouritesBeanClass();
                favourity.setFAVOURITES_ID(Integer.parseInt(cursor.getString(0)));
                favourity.setFAVOURITES_STORE_ID(cursor.getString(1));
                favourity.setFAVOURITES_STORE_NAME(cursor.getString(2));
                favourity.setFAVOURITES__STORE_LATITUDE(cursor.getString(3));
                favourity.setFAVOURITES_STORE_LONGITUDE(cursor.getString(4));
                favourity.setFAVOURITES_STORE_PRODUCT_ID(cursor.getString(5));
                favourity.setFAVOURITES_STORE_PRODUCT_NAME(cursor.getString(6));
                favourity.setFAVOURITES_STORE_PRODUCT_OFFER(cursor.getString(7));
                favourity.setFAVOURITES_STORE_PRODUCT_TIME(cursor.getString(8));
                favouritesList[i] = favourity;
                i++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        // db.close();
        return favouritesList;
    }


    /**
     * get all the Reminders
     * set all Reminders in ArrayList of Favourites object.
     *
     * @return
     */
    public ReminderBeanClass[] getAllReminders() {


        String selectReminders = "SELECT * FROM " + FuncardDealsContract.FuncardReminder.FUNCARDDEALS_REMINDER_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectReminders, null);

        ReminderBeanClass[] remindersList = new ReminderBeanClass[cursor.getCount()];
        int j = 0;

        if (cursor.moveToFirst()) {
            do {
                ReminderBeanClass reminder = new ReminderBeanClass();
                reminder.setREMINDER_ID(Integer.parseInt(cursor.getString(0)));
                reminder.setREMINDER_STORE_ID(cursor.getString(1));
                reminder.setREMINDER_STORE_NAME(cursor.getString(2));
                reminder.setREMINDER_STORE_LATITUDE(cursor.getString(3));
                reminder.setREMINDER_STORE_LONGITUDE(cursor.getString(4));
                reminder.setREMINDER_STORE_PRODUCT_ID(cursor.getString(5));
                reminder.setREMINDER_STORE_PRODUCT_NAME(cursor.getString(6));
                reminder.setREMINDER_STORE_PRODUCT_OFFER(cursor.getString(7));
                reminder.setREMINDER_STORE_PRODUCT_TIME(cursor.getString(8));



                remindersList[j] = reminder;
                j++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return remindersList;
    }


    /**
     * Delete a particular Favourite with id
     *
     * @param productID
     */

    public void deleteFavourite(String productID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FuncardDealsContract.FuncardFavourites.FUNCARDDEALS_FAVOURITES_TABLE_NAME, FuncardDealsContract.FuncardFavourites.FAVOURITES_STORE_PRODUCT_ID + "=" + productID, null);
    }


    /**
     * Delete a particular Reminder with id
     *
     * @param productID
     */

    public void deleteReminder(String productID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FuncardDealsContract.FuncardReminder.FUNCARDDEALS_REMINDER_TABLE_NAME, FuncardDealsContract.FuncardReminder.REMINDER_STORE_PRODUCT_ID + "=" + productID, null);
    }


    /**
     * check if product exist or not
     */

    public boolean isFavouriteExist(String productID) {
        boolean isExist = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String sql = "SELECT " + FuncardDealsContract.FuncardFavourites.FAVOURITES_STORE_PRODUCT_ID + " FROM " + FuncardDealsContract.FuncardFavourites.FUNCARDDEALS_FAVOURITES_TABLE_NAME + " WHERE " + FuncardDealsContract.FuncardFavourites.FAVOURITES_STORE_PRODUCT_ID + "=" + productID;
        cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            isExist = true;
        } else {
            isExist = false;
        }
        cursor.close();
        return isExist;
    }


    /**
     * check if product exist or not
     */

    public boolean isReminderExist(String productID) {
        boolean isExist = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String sql = "SELECT " + FuncardDealsContract.FuncardReminder.REMINDER_STORE_PRODUCT_ID + " FROM " + FuncardDealsContract.FuncardReminder.FUNCARDDEALS_REMINDER_TABLE_NAME + " WHERE " + FuncardDealsContract.FuncardReminder.REMINDER_STORE_PRODUCT_ID + "=" + productID;
        cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            isExist = true;
        } else {
            isExist = false;
        }
        cursor.close();
        return isExist;
    }

    /**
     * update reminder time managing notification arrival.
     *
     */
    public void updateReminderTime(String productID,String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FuncardDealsContract.FuncardReminder.REMINDER_STORE_PRODUCT_TIME,time);
        db.update(FuncardDealsContract.FuncardReminder.FUNCARDDEALS_REMINDER_TABLE_NAME, cv, FuncardDealsContract.FuncardReminder.REMINDER_STORE_PRODUCT_ID + "=" + productID, null);
    }

}
