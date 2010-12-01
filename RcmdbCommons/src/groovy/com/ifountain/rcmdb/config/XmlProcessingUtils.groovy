package com.ifountain.rcmdb.config

import groovy.util.slurpersupport.GPathResult

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 30, 2010
 * Time: 4:44:15 PM
 * To change this template use File | Settings | File Templates.
 */
class XmlProcessingUtils {
  public static getAttributeAs(GPathResult xmlNode, String attributeName, Class expectedType) throws XmlAttributeConversionException {
    def attribute = xmlNode.@"${attributeName}";
    if (attribute.isEmpty()) return null;
    String attributeValue = attribute.text();
    try {
      if (expectedType == Boolean) {
        return ConfigConverterUtils.convertValueToBoolean(attributeValue)
      }
      else {
        return attributeValue."to${expectedType.simpleName}"();
      }
    }
    catch (Exception e) {
      throw new XmlAttributeConversionException(attributeName, attributeValue, expectedType, e);
    }

  }


  public static List getMissingMandatoryAttributes(GPathResult xmlNode, Map<String, Boolean> validAttributeNames) {
    List missingMandatoryAttributes = new ArrayList();
    Map xmlNodeAttributes = xmlNode.attributes();
    validAttributeNames.each {String propName, Boolean isMandatory ->
      if (isMandatory && !xmlNodeAttributes.containsKey(propName)) {
        missingMandatoryAttributes.add(propName);
      }
    }
    return missingMandatoryAttributes.sort();
  }

  public static List getInvalidAttributes(GPathResult xmlNode, Map<String, Boolean> validAttributeNames) {
    List invalidAttributeList = new ArrayList();
    xmlNode.attributes().each {name, value ->
      if (!validAttributeNames.containsKey(name)) {
        invalidAttributeList.add(name);
      }
    }
    return invalidAttributeList.sort();
  }
}
