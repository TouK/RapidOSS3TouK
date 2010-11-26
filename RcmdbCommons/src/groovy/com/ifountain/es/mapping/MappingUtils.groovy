package com.ifountain.es.mapping

import java.text.DateFormat
import java.text.SimpleDateFormat
import groovy.util.slurpersupport.Attribute
import groovy.util.slurpersupport.GPathResult

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
        return convertValueToBoolean(defaultValueStr);
      }
    }
    catch (Exception e) {
      throw MappingProviderException.defaultValueException(expectedType, defaultValueStr, e);
    }
    throw MappingException.invalidPropertyTypeException(expectedType);
  }

  public static getAttributeAs(GPathResult xmlNode, String attributeName, Class expectedType){
    def attribute = xmlNode.@"${attributeName}";
    if(attribute.isEmpty()) return null;
    String attributeValue = attribute.text();
    try{
      if(expectedType == Boolean){
        return convertValueToBoolean(attributeValue)
      }
      else{
        return attributeValue."to${expectedType.simpleName}"();  
      }
    }
    catch(Exception e){
      throw new XmlAttributeConversionException(attributeName, attributeValue, expectedType, e);
    }

  }

  private static boolean convertValueToBoolean(String value){
    if(value.equalsIgnoreCase("true")) return true;
    else if(value.equalsIgnoreCase("false")) return false;
    else throw new Exception("Invalid boolean value ${value}");    
  }
}
