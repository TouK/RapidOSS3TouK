package com.ifountain.rcmdb.domain.converter

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 22, 2008
* Time: 11:04:25 AM
* To change this template use File | Settings | File Templates.
*/
class LongConverterTest extends RapidCmdbTestCase{
    
    public void testConvert()
    {
        LongConverter converter = new LongConverter();
        def returnedLong = converter.convert (Long, "5");
        assertEquals (5, returnedLong);
    }
    public void testConvertWithEmptyString()
    {
        LongConverter converter = new LongConverter();
        def returnedLong = converter.convert (Long, "");
        assertNull (returnedLong);
    }

    public void testIfInstanceOfLongReturnThatInstance()
    {
        LongConverter converter = new LongConverter();
        def inputInstance = new Long(1111);
        def returnedLong = converter.convert (Long, inputInstance);
        assertSame (inputInstance, returnedLong);
    }
}