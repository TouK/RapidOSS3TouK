package com.ifountain.rcmdb.domain.converter

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import java.text.SimpleDateFormat
import java.text.ParseException
import org.apache.commons.beanutils.converters.LongConverter
import org.apache.commons.beanutils.ConversionException

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 22, 2008
* Time: 10:05:49 AM
* To change this template use File | Settings | File Templates.
*/
class DateConverterTest extends RapidCmdbTestCase{

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testConvert()
    {
        String formatString = "yyyy-dd-MM";
        DateConverter converter = new DateConverter(formatString);
        def input = "2000-01-01";
        def returnedDate = converter.convert (Date, input);
        SimpleDateFormat formater = new SimpleDateFormat(formatString)
        assertEquals (formater.parse(input), returnedDate);
        assertEquals (formatString, converter.format);
    }

    public void testConvertWithNull()
    {
        String formatString = "yyyy-dd-MM";
        DateConverter converter = new DateConverter(formatString);
        def returnedDate = converter.convert (Date, null);
        assertNull (returnedDate);
    }

    public void testConvertWithEmptyString()
    {
        String formatString = "yyyy-dd-MM";
        DateConverter converter = new DateConverter(formatString);
        def returnedDate = converter.convert (Date, "");
        assertNull (returnedDate);
    }

    public void testConvertWithInvalidFormat()
    {
        String formatString = "yyyy-dd-MM";
        DateConverter converter = new DateConverter(formatString);
        try
        {
            converter.convert (Date, "invalid formatted date");
            fail("Should throw exception since date is invalid");
        }catch(ConversionException e)
        {
        }
    }

    public void testWithGString()
    {
        String formatString = "yyyy-dd-MM";
        DateConverter converter = new DateConverter(formatString);
        def inputString = "2000-01-01";
        def inputGString = "${inputString}";
        def returnedDate = converter.convert (Long, inputGString);
        SimpleDateFormat formater = new SimpleDateFormat(formatString)
        assertEquals (formater.parse(inputGString.toString()), returnedDate);
    }

    public void testIfInstanceOfDateReturnThatInstance()
    {
        String formatString = "yyyy-dd-MM";
        DateConverter converter = new DateConverter(formatString);
        def inputInstance = new Date();
        def returnedDate = converter.convert (Long, inputInstance);
        assertSame (inputInstance, returnedDate);
    }

}