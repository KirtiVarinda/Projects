package com.remindme.manage;

import java.util.Calendar;

/**
 * Created by dx on 10/1/2015.
 */
public class DeviceManager {


    /**
     * Method to get time in format YY:MM:DD HH:MM:SS
     * @return
     */
    public static String getCurrentTimeInSpecificFormat() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        return "" + c.get(Calendar.YEAR) + "-" + month + "-" + c.get(Calendar.DAY_OF_MONTH) + "  " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);

    }


}
