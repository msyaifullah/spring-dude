package com.yyggee.eggs.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

public class Helper {

  public static String generateTimestamp() {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    long ts = timestamp.getTime();

    return Long.toString(ts);
  }

  public static String formatToEmv(String amount, String exponent) {
    int exp = Integer.parseInt(exponent);
    amount = amount.replace(",", "");
    double amt = Double.parseDouble(amount);

    BigDecimal bd = new BigDecimal(amt).setScale(exp, RoundingMode.HALF_UP);
    BigDecimal newAmount = bd.movePointRight(exp);

    return newAmount.toString();
  }

  public static String formatFromEnv(String amount, String exponent) {
    int exp = Integer.parseInt(exponent);
    float amt = Float.parseFloat(amount);
    BigDecimal bd = new BigDecimal(amt).setScale(exp, RoundingMode.HALF_UP);
    BigDecimal newAmount = bd.movePointLeft(exp).stripTrailingZeros();

    return newAmount.toString();
  }
}
