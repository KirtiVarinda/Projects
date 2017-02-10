package com.tiffindaddy.app.tiffindaddy.Manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by  on 2/10/2016.
 */
public class TimeManager {

    public static String CurrenttwelevFormatTime(String current_hour1, String current_min2) {

        int current_hour = Integer.parseInt(current_hour1);
        int current_min = Integer.parseInt(current_min2);

        int h;
        String current_time;

        String current_min1 = twoDecimalMinute(current_min);
        if (current_hour >= 0 && current_hour < 12) {
            current_time = twoDecimalHour(current_hour) + ":" + current_min1 + " AM";
        } else {
            h = current_hour - 12;
            if (h == 0) {
                h = 12;
            }
            current_time = twoDecimalHour(h) + ":" + current_min1 + " PM";
        }
        return current_time;
    }

    protected static String twoDecimalMinute(int minute) {
        String minute1 = "";
        if (minute >= 00 && minute <= 9) {
            minute1 = "0" + minute;
        } else {
            minute1 = "" + minute;
        }
        return minute1;
    }

    protected static String twoDecimalHour(int hour) {
        String hour1 = "";
        if (hour >= 00 && hour <= 9) {
            hour1 = "0" + hour;
        } else {
            hour1 = "" + hour;
        }
        return hour1;
    }


    public static int getNumberOfHours(String firstTime, String secondTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:MM:SS");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = simpleDateFormat.parse(firstTime);
            date2 = simpleDateFormat.parse(secondTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        long difference = date2.getTime() - date1.getTime();
        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        hours = (hours < 0 ? -hours : hours);

        return hours;

    }

    public static int timeAfterHour() {
        Calendar c = Calendar.getInstance();
        Date now = new Date();
        c.setTime(now);
        c.add(Calendar.HOUR_OF_DAY, 1);
        Date afetrHour = c.getTime();
        int current_hour = afetrHour.getHours();
        return current_hour;
    }


    public static long dateToString(int hour, int min) {
        Calendar c = Calendar.getInstance();
        Date now = new Date();
        c.setTime(now);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        GregorianCalendar calendar = new GregorianCalendar(year, month+1, day, hour, min);
        long startTime = calendar.getTimeInMillis();
        return startTime;
    }

    public static long currentTimedateToString() {
        Calendar c = Calendar.getInstance();
        Date now = new Date();
        c.setTime(now);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        GregorianCalendar calendar = new GregorianCalendar(year, month+1, day, hour, min);
        long startTime = calendar.getTimeInMillis();
        return startTime;
    }

    public static long timeToMillisecond(int year,int month,int day,int hour, int min) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day, hour, min);
        long startTime = calendar.getTimeInMillis();
        return startTime;
    }



    public static long timeBeforeHour(int hour, int min) {
        Calendar c = Calendar.getInstance();
        Date now = new Date();
        c.setTime(now);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        GregorianCalendar calendar = new GregorianCalendar(year, month, day, hour, min);
        calendar.add(Calendar.HOUR, -1);

        long time = calendar.getTimeInMillis();


        return time;
    }


    public static long timeAfterHourinMilli(long milisec) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milisec);

        c.add(Calendar.HOUR, 1);


        long time = c.getTimeInMillis();


        return time;
    }


    public static String milisecToTime(long milisec) {
        Calendar c = Calendar.getInstance();
        //Set time in milliseconds
        c.setTimeInMillis(milisec);
        int hr = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        String min1 = twoDecimalMinute(min);
        String time = "";
        if (hr >= 0 && hr < 12) {
            time = hr + ":" + min1 + " AM";
        } else {
            hr = hr - 12;
            if (hr == 0) {
                hr = 12;
            }
            time = hr + ":" + min1 + " PM";
        }
        return time;
    }


    /**
     * today date in dd/mm/yy
     *
     * @return
     */
    public static String todayDate() {
        Calendar c = Calendar.getInstance();
        Date now = new Date();
        c.setTime(now);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        return day + "/" + month + "/" + year;
    }

    /**
     * tomorrow date in dd/mm/yy
     *
     * @return
     */
    public static String tomorrowDate() {
        Calendar c = Calendar.getInstance();
        Date now = new Date();
        c.setTime(now);
        c.add(Calendar.DAY_OF_MONTH, 1);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        return day + "/" + month + "/" + year;
    }


}
