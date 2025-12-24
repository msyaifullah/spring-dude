package com.yyggee.eggs.utils;

import java.util.HashMap;
import java.util.Map;

public class Converter {

  public static String currencyToCode(String arg) {
    final Map<String, String> map = new HashMap<>();
    map.put("USD", "840");
    map.put("MYR", "458");
    map.put("IDR", "360");
    map.put("GBP", "826");
    map.put("EUR", "978");
    map.put("INR", "356");
    map.put("SGD", "702");
    map.put("THB", "764");
    map.put("PHP", "608");
    map.put("AUD", "036");
    return map.get(arg.toUpperCase());
  }

  public static boolean isNullOrEmpty(String str) {
    return str == null || str.equals("");
  }

  public static boolean isZeroOrNull(Double dbl) {
    return dbl == null || dbl == 0 || dbl.isNaN();
  }
}
