package com.ifountain.rcmdb.converter.datasource

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.converter.datasource.DefaultConverter
import java.sql.Timestamp
import java.sql.Time
import java.sql.Connection
import org.apache.commons.beanutils.ConversionException

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 25, 2008
 * Time: 10:37:41 AM
 * To change this template use File | Settings | File Templates.
 */
class DefaultConverterTest extends RapidCmdbTestCase{
    public void testConvert()
    {
        DefaultConverter converter = new DefaultConverter(Integer, Long);
        def convertedInteger = converter.convert(new Integer(Integer.MAX_VALUE))
        assertEquals (Long.class.name, convertedInteger.class.name);
        assertEquals (new Long(Integer.MAX_VALUE), convertedInteger);
    }

    public void testDefaultConverterThrowsExceptionIfNoApacheConverterExist()
    {
        DefaultConverter converter = new DefaultConverter(Object, Long);
        try
        {
            converter.convert(new Object())
            fail("Should throw exception");
        }catch(ConversionException ex)
        {
            assertTrue(ex.getMessage().indexOf("No converter exists") >= 0);
        }
    }

}