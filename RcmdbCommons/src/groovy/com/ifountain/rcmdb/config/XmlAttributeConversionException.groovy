package com.ifountain.rcmdb.config

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 26, 2010
 * Time: 10:15:33 AM
 * To change this template use File | Settings | File Templates.
 */
class XmlAttributeConversionException extends Exception{
  String attributeName;
  String attributeValue;
  Class expectedClass;

  public XmlAttributeConversionException(String attributeName, String attributeValue, Class expectedClass, Throwable c) {
    super("Connect convert value <${attributeValue}> of attribute <${attributeName}> to ${expectedClass.name}".toString(), c);
    this.attributeName = attributeName;
    this.attributeValue = attributeValue;
    this.expectedClass = expectedClass;
  }
}
