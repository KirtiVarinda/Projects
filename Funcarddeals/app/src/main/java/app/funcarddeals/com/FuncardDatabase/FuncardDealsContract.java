package app.funcarddeals.com.FuncardDatabase;

import android.provider.BaseColumns;

/**
 * Created by dx on 8/25/2015.
 */
public class FuncardDealsContract {

    public FuncardDealsContract() {
    }

    /**
     * inner class for FuncardFavourites table
     */
    public static abstract class FuncardFavourites implements BaseColumns {
        public static final String FUNCARDDEALS_FAVOURITES_TABLE_NAME = "funcarddeals_favourites";
        public static final String FAVOURITES_ID = "funcarddeals_favourites_id";
        public static final String FAVOURITES_STORE_ID = "funcarddeals_favourites_store_id";
        public static final String FAVOURITES_STORE_NAME = "funcarddeals_favourites_store_name";
        public static final String FAVOURITES__STORE_LATITUDE = "funcarddeals_favourites_store_latitude";
        public static final String FAVOURITES_STORE_LONGITUDE = "funcarddeals_favourites_store_longitude";
        public static final String FAVOURITES_STORE_PRODUCT_ID = "funcarddeals_favourites_store_product_id";
        public static final String FAVOURITES_STORE_PRODUCT_NAME = "funcarddeals_favourites_store_product_name";
        public static final String FAVOURITES_STORE_PRODUCT_OFFER = "funcarddeals_favourites_store_product_offer";
        public static final String FAVOURITES_STORE_PRODUCT_TIME = "funcarddeals_favourites_store_product_time";
        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        protected static final String CREATE_FAVOURITES_TABLE =
                "CREATE TABLE " + FUNCARDDEALS_FAVOURITES_TABLE_NAME + " (" +
                        FAVOURITES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FAVOURITES_STORE_ID + TEXT_TYPE + COMMA_SEP +
                        FAVOURITES_STORE_NAME + TEXT_TYPE + COMMA_SEP +
                        FAVOURITES__STORE_LATITUDE + TEXT_TYPE + COMMA_SEP +
                        FAVOURITES_STORE_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                        FAVOURITES_STORE_PRODUCT_ID + TEXT_TYPE + COMMA_SEP +
                        FAVOURITES_STORE_PRODUCT_NAME + TEXT_TYPE + COMMA_SEP +
                        FAVOURITES_STORE_PRODUCT_OFFER + TEXT_TYPE + COMMA_SEP +
                        FAVOURITES_STORE_PRODUCT_TIME + TEXT_TYPE +
                        " )";
    }

    /**
     * inner class for FuncradReminder table
     */
    public static abstract class FuncardReminder implements BaseColumns {
        public static final String FUNCARDDEALS_REMINDER_TABLE_NAME = "funcarddeals_reminders";
        public static final String REMINDER_ID = "funcarddeals_reminder_id";
        public static final String REMINDER_STORE_ID = "funcarddeals_reminder_store_id";
        public static final String REMINDER_STORE_NAME = "funcarddeals_reminder_store_name";
        public static final String REMINDER_STORE_LATITUDE = "funcarddeals_reminder_store_latitude";
        public static final String REMINDER_STORE_LONGITUDE = "funcarddeals_reminder_store_longitude";
        public static final String REMINDER_STORE_PRODUCT_ID = "funcarddeals_reminder_store_product_id";
        public static final String REMINDER_STORE_PRODUCT_NAME = "funcarddeals_reminder_store_product_name";
        public static final String REMINDER_STORE_PRODUCT_OFFER = "funcarddeals_reminder_store_product_offer";
        public static final String REMINDER_STORE_PRODUCT_TIME = "funcarddeals_reminder_store_product_time";
        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        protected static final String CREATE_REMINDERS_TABLE =
                "CREATE TABLE " + FUNCARDDEALS_REMINDER_TABLE_NAME + " (" +
                        REMINDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        REMINDER_STORE_ID + TEXT_TYPE + COMMA_SEP +
                        REMINDER_STORE_NAME + TEXT_TYPE + COMMA_SEP +
                        REMINDER_STORE_LATITUDE + TEXT_TYPE + COMMA_SEP +
                        REMINDER_STORE_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                        REMINDER_STORE_PRODUCT_ID + TEXT_TYPE + COMMA_SEP +
                        REMINDER_STORE_PRODUCT_NAME + TEXT_TYPE + COMMA_SEP +
                        REMINDER_STORE_PRODUCT_OFFER + TEXT_TYPE + COMMA_SEP +
                        REMINDER_STORE_PRODUCT_TIME + TEXT_TYPE +
                        " )";
    }

}
