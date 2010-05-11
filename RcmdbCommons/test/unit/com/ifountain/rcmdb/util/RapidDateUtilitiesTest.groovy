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

        //try to format 24 hours with 12 hour format should throw exception
        //first try valid format
        println Date.toDate("04-May-2010 15:08:06 GMT", "dd-MMM-yyyy HH:mm:ss z");          
        try{
            println Date.toDate("04-May-2010 15:08:06 GMT", "dd-MMM-yyyy h:mm:ss z");
            fail("Should throw exception");
        }
        catch(java.text.ParseException ex)
        {
        }


    }
}