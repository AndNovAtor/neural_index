package com.andnovator.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by novator on 21.05.2016.
 */
public class MathUtils {

    public static double roundToDec(double val, int decimal) {
//        return roundToDecByRound(val, decimal);
        return roundToDecByBigDecimal(val, decimal);
    }

    // fast, but has errors ( 265.335 * 100 (precision of 2 digits) is 26533.499999999996, and round will be 265.33)
    private static double roundToDecByRound(double val, int decimal) {
        if (decimal < 0) {
            decimal = 0;
        }
        int decTenPow = (int)Math.pow(10, decimal);
        return Math.round(val*decTenPow)/decTenPow;
    }

    // slower, but more correctly
    private static double roundToDecByBigDecimal(double val, int decimal) {
        BigDecimal bd = new BigDecimal(val).setScale(decimal, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }
}
