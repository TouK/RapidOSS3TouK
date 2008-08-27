package com.ifountain.rcmdb.converter

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.converter.BooleanConverter

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 26, 2008
 * Time: 4:46:45 PM
 * To change this template use File | Settings | File Templates.
 */
class BooleanConverterTest extends RapidCmdbTestCase{
    public void testConvert()
    {
        BooleanConverter converter = new BooleanConverter();
        def returnedBoolean = converter.convert (Boolean, "true");
        assertEquals (new Boolean(true), returnedBoolean);
    }

    public void testConvertWithEmptyString()
    {
        BooleanConverter converter = new BooleanConverter();
        def returnedBoolean = converter.convert (Boolean, "");
        assertNull (returnedBoolean);
    }

    public void testIfInstanceOfBooleanReturnThatInstance()
    {
        BooleanConverter converter = new BooleanConverter();
        def inputInstance = new Boolean(true);
        def returnedBoolean = converter.convert (Boolean, inputInstance);
        assertSame (inputInstance, returnedBoolean);
    }
}