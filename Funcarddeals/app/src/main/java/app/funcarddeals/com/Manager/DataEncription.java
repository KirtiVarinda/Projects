package app.funcarddeals.com.Manager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by dx on 7/20/2015.
 */
public class DataEncription {


    /**
     * Encript data to md5 encription.
     *
     * @param str
     * @return
     */
    public static String encriptToMD5(String str) {
        String signature = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes(), 0, str.length());
            signature = new BigInteger(1, md5.digest()).toString(16);

        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return signature;
    }


}
