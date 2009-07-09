package com.ifountain.rcmdb.converter.datasource

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.thoughtworks.xstream.converters.ConversionException

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 9, 2009
* Time: 2:09:32 PM
* To change this template use File | Settings | File Templates.
*/
class StringConverterTest extends RapidCmdbTestCase{
    public void testConvertWithNullObject()
    {
        StringConverter converter = new StringConverter();
        assertNull (converter.convert(null));
    }

    public void testConvert()
    {
        Integer realValue = 5;
        StringConverter converter = new StringConverter();
        assertEquals (String.valueOf(realValue), converter.convert(realValue));
    }
}