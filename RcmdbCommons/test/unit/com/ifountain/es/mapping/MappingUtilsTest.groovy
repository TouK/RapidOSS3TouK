package com.ifountain.es.mapping

import com.ifountain.core.test.util.RapidCoreTestCase
import java.text.DateFormat
import java.text.SimpleDateFormat
import com.ifountain.rcmdb.config.XmlAttributeConversionException

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 25, 2010
 * Time: 5:11:04 PM
 * To change this template use File | Settings | File Templates.
 */
class MappingUtilsTest extends RapidCoreTestCase{
  public void testCreateDefaultValue(){
    String type = TypeProperty.STRING_TYPE;
    String defaultValueStr = "str"
    assertEquals("str", MappingUtils.createDefaultValue(type, defaultValueStr));

    defaultValueStr = "  "
    assertEquals(TypeProperty.EMPTY_STRING, MappingUtils.createDefaultValue(type, defaultValueStr));

    type = TypeProperty.BOOLEAN_TYPE;
    defaultValueStr = "true"
    assertEquals(true, MappingUtils.createDefaultValue(type, defaultValueStr));

    type = TypeProperty.BOOLEAN_TYPE;
    defaultValueStr = "False"
    assertEquals(false, MappingUtils.createDefaultValue(type, defaultValueStr));

    type = TypeProperty.INTEGER_TYPE;
    defaultValueStr = "12"
    assertEquals(Integer.parseInt(defaultValueStr), MappingUtils.createDefaultValue(type, defaultValueStr));

    type = TypeProperty.LONG_TYPE;
    defaultValueStr = ""+Long.MAX_VALUE
    assertEquals(Long.MAX_VALUE, MappingUtils.createDefaultValue(type, defaultValueStr));

    type = TypeProperty.DOUBLE_TYPE;
    defaultValueStr = "1.0"
    assertEquals((int)1.0, (int)MappingUtils.createDefaultValue(type, defaultValueStr));

    type = TypeProperty.FLOAT_TYPE;
    defaultValueStr = "1.0"
    assertEquals((int)1.0, (int)MappingUtils.createDefaultValue(type, defaultValueStr));

    type = TypeProperty.DATE_TYPE;
    defaultValueStr = "2000-11-11 23:12:00"
    DateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:MM:ss");
    assertEquals(format.parse(defaultValueStr), MappingUtils.createDefaultValue(type, defaultValueStr));
  }

  public void testCreateDefaultThrowsExceptionIfCannotConvert(){

    String type = TypeProperty.INTEGER_TYPE;
    String defaultValueStr = "abcd"
    try{
      MappingUtils.createDefaultValue(type, defaultValueStr);
      fail("Should throw exception");
    }
    catch(MappingException e){
      MappingException expectedException = MappingProviderException.defaultValueException(type, defaultValueStr, null);
      assertEquals (expectedException.toString(), e.toString());
    }

    type = TypeProperty.BOOLEAN_TYPE;
    defaultValueStr = "abcd"
    try{
      MappingUtils.createDefaultValue(type, defaultValueStr);
      fail("Should throw exception");
    }
    catch(MappingException e){
      MappingException expectedException = MappingProviderException.defaultValueException(type, defaultValueStr, null);
      assertEquals (expectedException.toString(), e.toString());
    }

    type = TypeProperty.LONG_TYPE;
    defaultValueStr = "abcd"
    try{
      MappingUtils.createDefaultValue(type, defaultValueStr);
      fail("Should throw exception");
    }
    catch(MappingException e){
      MappingException expectedException = MappingProviderException.defaultValueException(type, defaultValueStr, null);
      assertEquals (expectedException.toString(), e.toString());
    }

    type = TypeProperty.DOUBLE_TYPE;
    defaultValueStr = "abcd"
    try{
      MappingUtils.createDefaultValue(type, defaultValueStr);
      fail("Should throw exception");
    }
    catch(MappingException e){
      MappingException expectedException = MappingProviderException.defaultValueException(type, defaultValueStr, null);
      assertEquals (expectedException.toString(), e.toString());
    }
    type = TypeProperty.FLOAT_TYPE;
    defaultValueStr = "abcd"
    try{
      MappingUtils.createDefaultValue(type, defaultValueStr);
      fail("Should throw exception");
    }
    catch(MappingException e){
      MappingException expectedException = MappingProviderException.defaultValueException(type, defaultValueStr, null);
      assertEquals (expectedException.toString(), e.toString());
    }

    type = TypeProperty.DATE_TYPE;
    defaultValueStr = "abcd"
    try{
      MappingUtils.createDefaultValue(type, defaultValueStr);
      fail("Should throw exception");
    }
    catch(MappingException e){
      MappingException expectedException = MappingProviderException.defaultValueException(type, defaultValueStr, null);
      assertEquals (expectedException.toString(), e.toString());
    }
  }

  public void testCreateDefaultThrowsExceptionIfTypeIsInvalid(){

    String type = "invalidtype";
    String defaultValueStr = "abcd"
    try{
      MappingUtils.createDefaultValue(type, defaultValueStr);
      fail("Should throw exception");
    }
    catch(MappingException e){
      MappingException expectedException = MappingException.invalidPropertyTypeException(type);
      assertEquals (expectedException.toString(), e.toString());
    }

  }

}
