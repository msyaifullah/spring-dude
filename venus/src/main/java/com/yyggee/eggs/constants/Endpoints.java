package com.yyggee.eggs.constants;

public class Endpoints {

  public static final String BASE_URL = "/api/v1/dashboard/";

  public static final String HEALTH_CHECK = "health-check";

  /*
   * Sample Egg Controller API Endpoints
   */
  public static final String EGG_ADD_ID = "/egg/add/{id}";

  /*
   * Login and Authenticate
   */

  public static final String USER_SIGN_IN = "/signin";
  public static final String USER_SIGN_UP = "/signup";
  public static final String USER_SIGN_OUT = "/signout";
  public static final String USER_SESSION = "/user/me";
  public static final String USER_SESSION_USERNAME = "/user/{username}";
  public static final String USER_REFRESH = "/extend-access";
}
