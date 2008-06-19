package com.ifountain.rcmdb.domain.converter

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 30, 2008
* Time: 1:34:18 PM
* To change this template use File | Settings | File Templates.
*/
class DoubleConverterTest extends RapidCmdbTestCase{
    public void testConvert()
    {
        DoubleConverter converter = new DoubleConverter();
        def returnedDouble = converter.convert (Double, "5.0");
        assertEquals (new Double(5.0), returnedDouble);
    }
    public void testConvertWithEmptyString()
    {
        DoubleConverter converter = new DoubleConverter();
        def returnedDouble = converter.convert (Double, "");
        assertNull (returnedDouble);
    }

    public void testIfInstanceOfLongReturnThatInstance()
    {
        DoubleConverter converter = new DoubleConverter();
        def inputInstance = new Double(1111);
        def returnedDouble = converter.convert (Double, inputInstance);
        assertSame (inputInstance, returnedDouble);
    }
}