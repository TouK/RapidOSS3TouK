package com.ifountain.rcmdb.converter.datasource

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.comp.converter.ConverterRegistry

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 25, 2008
 * Time: 4:47:46 PM
 * To change this template use File | Settings | File Templates.
 */
class ListConverterTest extends RapidCmdbTestCase{
    protected void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        ConverterRegistry.getInstance().unregisterAll();
    }

    protected void tearDown() {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
        ConverterRegistry.getInstance().unregisterAll();
    }
    public void testConvert()
    {
        ConverterRegistry.getInstance().register (Integer, new DefaultConverter(Integer, Long));
        ConverterRegistry.getInstance().register (Float, new DefaultConverter(Float, Double));
        def listToBeConverted = [];
        ListConverter conv = new ListConverter();
        def returnedList = conv.convert(listToBeConverted);
        assertEquals (listToBeConverted, returnedList);

        listToBeConverted.add(new Integer(100));
        listToBeConverted.add(new Float(100));
        returnedList = conv.convert(listToBeConverted);
        assertEquals ([new Long(100), new Double(100)], returnedList);
    }
}