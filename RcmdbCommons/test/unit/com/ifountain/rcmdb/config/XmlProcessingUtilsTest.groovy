package com.ifountain.rcmdb.config

import com.ifountain.comp.test.util.RCompTestCase

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 30, 2010
 * Time: 4:45:28 PM
 * To change this template use File | Settings | File Templates.
 */
class XmlProcessingUtilsTest extends RCompTestCase{

  public void testGetAttributeAs(){
    def xmlStr = """
      <Root>
        <Obj boolAttr= "true" intAttr = "12" doubleAttr="1.0" floatAttr = "1.1"  longAttr="${Long.MAX_VALUE}"></Obj>
      </Root>
    """
    def xmlNode = new XmlSlurper().parseText(xmlStr).Obj[0];
    def res = XmlProcessingUtils.getAttributeAs(xmlNode, "boolAttr", Boolean)
    assertEquals (true, res);

    res = XmlProcessingUtils.getAttributeAs(xmlNode, "intAttr", Integer)
    assertEquals (12, res);

    res = XmlProcessingUtils.getAttributeAs(xmlNode, "doubleAttr", Double)
    assertEquals ((int)1.0, (int)res);

    res = XmlProcessingUtils.getAttributeAs(xmlNode, "floatAttr", Float)
    assertEquals ((int)1.0, (int)res);

    res = XmlProcessingUtils.getAttributeAs(xmlNode, "longAttr", Long)
    assertEquals (Long.MAX_VALUE, res);
  }

  public void testGetAttributeAsThrowsExceptionIfInvalidValueSpecified(){
    def xmlStr = """
      <Root>
        <Obj boolAttr= "asd" intAttr = "asd" doubleAttr="asd" floatAttr = "asd"  longAttr="asd"></Obj>
      </Root>
    """
    def xmlNode = new XmlSlurper().parseText(xmlStr).Obj[0];
    try{
      XmlProcessingUtils.getAttributeAs(xmlNode, "boolAttr", Boolean)
      fail("Should throw exception")
    }
    catch(XmlAttributeConversionException ex){
      XmlAttributeConversionException expectedEx = new XmlAttributeConversionException("boolAttr", "asd", Boolean, null);
      assertEquals (expectedEx.toString(), ex.toString());
    }

    try{
      XmlProcessingUtils.getAttributeAs(xmlNode, "intAttr", Integer)
      fail("Should throw exception")
    }
    catch(XmlAttributeConversionException ex){
      XmlAttributeConversionException expectedEx = new XmlAttributeConversionException("intAttr", "asd", Integer, null);
      assertEquals (expectedEx.toString(), ex.toString());
    }

    try{
      XmlProcessingUtils.getAttributeAs(xmlNode, "doubleAttr", Double)
      fail("Should throw exception")
    }
    catch(XmlAttributeConversionException ex){
      XmlAttributeConversionException expectedEx = new XmlAttributeConversionException("doubleAttr", "asd", Double, null);
      assertEquals (expectedEx.toString(), ex.toString());
    }

    try{
      XmlProcessingUtils.getAttributeAs(xmlNode, "floatAttr", Float)
      fail("Should throw exception")
    }
    catch(XmlAttributeConversionException ex){
      XmlAttributeConversionException expectedEx = new XmlAttributeConversionException("floatAttr", "asd", Float, null);
      assertEquals (expectedEx.toString(), ex.toString());
    }

    try{
      XmlProcessingUtils.getAttributeAs(xmlNode, "longAttr", Long)
      fail("Should throw exception")
    }
    catch(XmlAttributeConversionException ex){
      XmlAttributeConversionException expectedEx = new XmlAttributeConversionException("longAttr", "asd", Long, null);
      assertEquals (expectedEx.toString(), ex.toString());
    }
  }

}