package com.ifountain.rcmdb.converter.datasource

import com.ifountain.comp.converter.ConverterRegistry
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 25, 2008
 * Time: 5:09:47 PM
 * To change this template use File | Settings | File Templates.
 */
class MapConverterTest extends RapidCmdbTestCase
{
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
        def mapToBeConverted = [:];
        MapConverter conv = new MapConverter();
        def returnedMap = conv.convert(mapToBeConverted);
        assertEquals (mapToBeConverted, returnedMap);

        mapToBeConverted.put("key1", new Integer(100));
        mapToBeConverted.put("key2", new Float(100));
        returnedMap = conv.convert(mapToBeConverted);
        assertEquals ([key1:new Long(100), key2:new Double(100)], returnedMap);
        assertSame (mapToBeConverted, returnedMap);
    }
}