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
import com.ifountain.compass.utils.QueryParserUtils

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jun 6, 2008
* Time: 2:45:30 PM
* To change this template use File | Settings | File Templates.
*/
class RapidStringUtilitiesTest extends RapidCmdbTestCase {

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        ExpandoMetaClass.enableGlobally();
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        ExpandoMetaClass.disableGlobally();
    }


    public void testRegisterStringUtilsMethods()
    {
        RapidStringUtilities.registerStringUtils();
        assertEquals("tri", "trial".substringBefore("al"));
        assertEquals("java.lang.String", String.name);
        assertTrue("trial".equals("trial"));
        try
        {
            "trial".undefinedMethod("al");
            fail("Should throw exception");
        }
        catch (MissingMethodException e)
        {

        }
    }

    public void testToQuery()
    {
        RapidStringUtilities.registerStringUtils();
        def originalString = "queryParam"
        def str = originalString.toQuery();
        assertEquals(originalString, str);

        originalString = "queryParam\"";
        str = originalString.toQuery();
        assertEquals("queryParam\\\"", str);

        originalString = "\"queryParam\"";
        str = originalString.toQuery();
        assertEquals("\\\"queryParam\\\"", str);
    }

    public void testExactQuery()
    {
        RapidStringUtilities.registerStringUtils();
        def originalString = "queryParam"
        def str = originalString.exactQuery();
        assertEquals("\"${QueryParserUtils.EXACT_QUERY_START}$originalString${QueryParserUtils.EXACT_QUERY_END}\"", str);

        originalString = "query(Param\"";
        str = originalString.exactQuery();
        assertEquals("\"${QueryParserUtils.EXACT_QUERY_START}query\\(Param\\\"${QueryParserUtils.EXACT_QUERY_END}\"", str);
    }

    public void testExactQueryWithNullStringObject()
    {
        RapidStringUtilities.registerStringUtils();
        def originalString = null;
        def str = originalString.exactQuery();
        assertEquals("\"${QueryParserUtils.EXACT_QUERY_START}$originalString${QueryParserUtils.EXACT_QUERY_END}\"", str);

    }
    public void testToQueryWithNullStringObject()
    {
        RapidStringUtilities.registerStringUtils();
        String originalString = null;
        def str = originalString.toQuery();
        assertEquals("null", str);
    }

    public void testToQueryWithGString()
    {
        RapidStringUtilities.registerStringUtils();
        def originalString = "${"queryParam"}";
        def str = originalString.toQuery();
        assertEquals(originalString, str);

        originalString = "${"queryParam\""}";
        str = originalString.toQuery();
        assertEquals("queryParam\\\"", str);

        originalString = "${"\"queryParam\""}";
        str = originalString.toQuery();
        assertEquals("\\\"queryParam\\\"", str);
    }

}