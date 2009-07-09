/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package com.ifountain.rcmdb.converter

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import java.text.SimpleDateFormat
import org.apache.commons.beanutils.ConversionException
import com.ifountain.rcmdb.converter.DateConverter

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

    public void testConvertToString()
    {
        String formatString = "yyyy-dd-MM";
        DateConverter converter = new DateConverter(formatString);
        def df = new SimpleDateFormat(formatString)
        def date = new Date(System.currentTimeMillis()+10000);
        def returnedStringDate = converter.convert(String.class, date)
        assertEquals (df.format(date), returnedStringDate);
    }

    public void testConvertToStringThrowsExceptionIfValueIsNotADateObject()
    {
        String formatString = "yyyy-dd-MM";
        DateConverter converter = new DateConverter(formatString);
        try{
            converter.convert(String.class, "invalid date")
            fail("Should throw exception since value is noty a date object");
        }
        catch(org.apache.commons.beanutils.ConversionException ex)
        {
        }
    }

    public void testConvertToStringWithNullObject()
    {
        String formatString = "yyyy-dd-MM";
        DateConverter converter = new DateConverter(formatString);
        assertNull (converter.convert(String.class, null));
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
        try{
            converter.convert (Date, "");
            fail("Should throw exception");
        }catch(org.apache.commons.beanutils.ConversionException ex)
        {

        }
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