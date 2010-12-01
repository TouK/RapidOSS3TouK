package com.ifountain.es.mapping

import java.text.DateFormat
import java.text.SimpleDateFormat

import com.ifountain.rcmdb.config.ConfigConverterUtils

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 25, 2010
 * Time: 5:09:33 PM
 * To change this template use File | Settings | File Templates.
 */
class MappingUtils {
  public static DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:MM:ss");

  public static Object createDefaultValue(String expectedType, String defaultValueStr) {
    try {
      if (expectedType == TypeProperty.STRING_TYPE) {
        String trimmedValue = defaultValueStr.trim();
        if(trimmedValue == ""){
          return TypeProperty.EMPTY_STRING;  
        }
        return defaultValueStr;
      }
      else if (expectedType == TypeProperty.INTEGER_TYPE) {
        return Integer.parseInt(defaultValueStr);
      }
      else if (expectedType == TypeProperty.LONG_TYPE) {
        return Long.parseLong(defaultValueStr);
      }
      else if (expectedType == TypeProperty.DOUBLE_TYPE) {
        return Double.parseDouble(defaultValueStr);
      }
      else if (expectedType == TypeProperty.FLOAT_TYPE) {
        return Float.parseFloat(defaultValueStr);
      }
      else if (expectedType == TypeProperty.DATE_TYPE) {
        return dateFormat.parse(defaultValueStr);
      }
      else if (expectedType == TypeProperty.BOOLEAN_TYPE) {
        return ConfigConverterUtils.convertValueToBoolean(defaultValueStr);
      }
    }
    catch (Exception e) {
      throw MappingProviderException.defaultValueException(expectedType, defaultValueStr, e);
    }
    throw MappingException.invalidPropertyTypeException(expectedType);
  }

  
  
}
