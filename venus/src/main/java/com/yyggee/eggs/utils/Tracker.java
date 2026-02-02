package com.yyggee.eggs.utils;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tracker {

  private static Tracker single_instance = null;

  final Map<String, Long> map = new HashMap<>();

  /**
   * Get instance for tracker singleton
   *
   * @return Tracker
   */
  public static Tracker getInstance() {
    if (single_instance == null) single_instance = new Tracker();

    return single_instance;
  }

  /**
   * To start counting the performance TODO: added tracker id so we can measure multiple function
   *
   * @param trackerId trackerId
   */
  public static void start(String trackerId) {
    Logger logger = LoggerFactory.getLogger(Tracker.class);
    Tracker.getInstance().map.put(trackerId, System.nanoTime());
    logger.info(
        "Performance time {} start {}", trackerId, Tracker.getInstance().map.get(trackerId));
  }

  /**
   * To stop counting the performance
   *
   * @param trackerId trackerId
   */
  public static void stop(String trackerId) {
    Logger logger = LoggerFactory.getLogger(Tracker.class);
    long start = Tracker.getInstance().map.get(trackerId);
    long end = System.nanoTime();
    logger.info(
        "Performance time {} finish {} - {} = {} {}",
        trackerId,
        end,
        start,
        (end - start),
        Tracker.getCallerClassName());
  }

  /**
   * Getting class name
   *
   * @return class and and function
   */
  public static String getCallerClassName() {
    StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
    for (int i = 1; i < stElements.length; i++) {
      StackTraceElement ste = stElements[i];
      if (!ste.getClassName().equals(Tracker.class.getName())
          && ste.getClassName().indexOf("java.lang.Thread") != 0) {
        return "class_." + ste.getClassName() + "._function_." + ste.getMethodName();
      }
    }
    return null;
  }
}
