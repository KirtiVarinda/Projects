package com.lovelife.lovelife.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dx on 7/17/2015.
 */
public class FormValidation {
    /**
     * Pattern for email validation.
     */
    private static String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    /**
     * method for email validation.
     *
     * @return
     */
    public static boolean isValidEmail(String email) {


        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    /**
     * method for password validation.
     *
     * @return
     */
    public static boolean isValidPassword(String password) {

        if (password.length() < 4 || removeWhiteSpaces(password).equals("")) {
            return false;
        } else {
            return true;
        }

    }
    /**
     * method for password validation.
     *
     * @return
     */
    public static boolean isValidPassword2(String password) {

        if (password.length() < 8 || removeWhiteSpaces(password).equals("")) {
            return false;
        } else {
            return true;
        }

    }



    /**
     * Method for Phone number validation.
     *
     * @param number
     * @return
     */
    public static boolean isValidPhoneNumber(String number) {

        if (number.length() == 10 /*&& number.length() <= 13*/) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Method for Phone number validation.
     *
     * @param number
     * @return
     */
    public static boolean isValidZip(String number) {

        if (number.length() == 5 /*&& number.length() <= 13*/) {
            return true;
        } else {
            return false;
        }
    }



    /**
     * validation for white spaces
     * it removes all the continous white spaces to "".
     */

    public static String removeWhiteSpaces(String whiteSpces) {
        String emptySpace = whiteSpces.replaceAll("\\s+", "");
        return emptySpace;
    }

}
