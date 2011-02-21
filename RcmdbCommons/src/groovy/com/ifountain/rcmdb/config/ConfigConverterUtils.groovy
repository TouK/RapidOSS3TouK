package com.ifountain.rcmdb.config

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Dec 1, 2010
 * Time: 8:39:26 AM
 * To change this template use File | Settings | File Templates.
 */
class ConfigConverterUtils {
  public static boolean convertValueToBoolean(String value) {
    if (value.equalsIgnoreCase("true")) return true;
    else if (value.equalsIgnoreCase("false")) return false;
    else throw new Exception("Invalid boolean value ${value}");
  }
}
