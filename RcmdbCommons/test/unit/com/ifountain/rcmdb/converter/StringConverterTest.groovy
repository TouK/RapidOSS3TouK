package com.ifountain.rcmdb.converter

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import java.text.SimpleDateFormat
import java.text.DecimalFormat

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jun 15, 2009
* Time: 3:12:58 PM
* To change this template use File | Settings | File Templates.
*/
class StringConverterTest extends RapidCmdbTestCase{
    public void testConvertWithDate()
    {
        def df = new SimpleDateFormat(StringConverter.DEFAULT_DATE_FORMAT);
        def dateToBeConverted = new Date();
        StringConverter converter = new StringConverter(StringConverter.DEFAULT_DATE_FORMAT);
        def convertedValue = converter.convert (String, dateToBeConverted);
        assertEquals (df.format(dateToBeConverted), convertedValue);

        def dateFormatString = "yyyy-MM";
        converter = new StringConverter(dateFormatString);
        df = new SimpleDateFormat(dateFormatString);

        convertedValue = converter.convert (String, dateToBeConverted);
        assertEquals (df.format(dateToBeConverted), convertedValue);
    }

    public void testConvertWithBigDecimal()
    {
        DecimalFormat f = new DecimalFormat();
        StringConverter converter = new StringConverter(StringConverter.DEFAULT_DATE_FORMAT);
        BigDecimal valueToBeConverted = new BigDecimal("1.23E+3")
        def convertedValue = converter.convert (String, valueToBeConverted)
        assertEquals (f.format(valueToBeConverted), convertedValue)

    }

    public void testConvertNull()
    {
        StringConverter converter = new StringConverter(StringConverter.DEFAULT_DATE_FORMAT);
        def convertedValue = converter.convert (String, null)
        assertEquals (String.valueOf(null), convertedValue)
    }

    public void testConvertString()
    {
        def strValue = "value"
        StringConverter converter = new StringConverter(StringConverter.DEFAULT_DATE_FORMAT);
        def convertedValue = converter.convert (String, strValue)
        assertSame(strValue, convertedValue)
    }

    public void testConvertInt()
    {
        def intValue = 4
        StringConverter converter = new StringConverter(StringConverter.DEFAULT_DATE_FORMAT);
        def convertedValue = converter.convert (String, intValue)
        assertEquals(String.valueOf(intValue), convertedValue)
    }

    public void testConvertLong()
    {
        def intValue = 4l
        StringConverter converter = new StringConverter(StringConverter.DEFAULT_DATE_FORMAT);
        def convertedValue = converter.convert (String, intValue)
        assertEquals(String.valueOf(intValue), convertedValue)
    }
}