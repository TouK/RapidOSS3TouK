package com.ifountain.rcmdb.util

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import java.text.SimpleDateFormat

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Oct 28, 2008
 * Time: 6:47:26 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidDateUtilitiesTest extends RapidCmdbTestCase{
    public void testRegisterDateUtils()
    {
        RapidDateUtilities.registerDateUtils();
        long time = System.currentTimeMillis()-100000;
        int timeInInt = 10000;
        assertTrue (Date.now() >= System.currentTimeMillis()-100);
        assertEquals (new Date(time), Date.toDate(time));
        assertEquals (new Date(time), Date.toDate(new Long(time)));
        assertEquals (new Date(timeInInt), Date.toDate(timeInInt));
        assertEquals (new Date(time), Date.toDate(""+time));
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        String dateString = "2008-10-10";
        assertEquals (format.parse(dateString), Date.toDate(dateString, dateFormat));
        assertEquals(dateString, format.parse(dateString).toString(dateFormat));

        try
        {
            Date.toDate("invalidFormat", dateFormat);
            fail("Should throw exception");
        }
        catch(java.text.ParseException ex)
        {
        }

        try
        {
            new Date().toString("invalidDateFormat");
            fail("Should throw exception");
        }
        catch(IllegalArgumentException ex)
        {
        }
    }
}