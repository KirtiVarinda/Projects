package app.funcarddeals.com.Manager;

import java.math.BigDecimal;

/**
 * Created by dxx on 9/1/2015.
 */
public class MathFunctions {

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }
}
