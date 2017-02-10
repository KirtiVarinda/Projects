package com.lovelife.lovelife.LoveChat;

import com.lovelife.lovelife.LoveLifeApplication;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonMethods {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public static String getCurrentTime() {

        Date today = Calendar.getInstance().getTime();
        System.out.println("ccccccc.."+timeFormat.format(today));

        return timeFormat.format(today);
    }

    public static String getCurrentDate() {

        Date today = Calendar.getInstance().getTime();

        return dateFormat.format(today);
    }



    /**
     * Create account of registered user on open fire or XMPP
     */
    public static void registerUserOnOpenFire(final String email, final String password) {


        /**
         * Call AccountManager CLass
         */
        final AccountManager manage = new AccountManager(ChatService.connection);
        new Thread() {
            public void run() {

                try {
                    /**
                     * Create Account
                     */

                    manage.createAccount(email, password);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("InterruptedException" + e);
                } catch (SmackException.NoResponseException e) {
                    System.out.println("SmackException" + e);
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                    System.out.println("NotConnectedException" + e);
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                    System.out.println("XMPPExceptionXMPPErrorException" + e);
                } catch (XMPPException e) {

                    System.out.println("XMPPException" + e);
                    e.printStackTrace();
                } catch (SmackException e) {
                    System.out.println("SmackException" + e);
                    e.printStackTrace();
                }
            }
        }.start();
    }


 /*   public static String getCurrentDate(long milliSeconds) {

        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }*/


}