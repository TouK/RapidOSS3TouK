package com.ifountain.rcmdb.util

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.commons.lang.StringUtils

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jun 6, 2008
* Time: 2:45:30 PM
* To change this template use File | Settings | File Templates.
*/
class RapidStringUtilitiesTest extends RapidCmdbTestCase{
    public void testRegisterStringUtilsMethods()
    {
        RapidStringUtilities.registerStringUtils();
        assertEquals ("tri", "trial".substringBefore("al"));
        assertEquals ("java.lang.String", String.name);
        assertTrue ( "trial".equals("trial"));
        try
        {
            "trial".undefinedMethod("al");
            fail("Should throw exception");
        }
        catch(MissingMethodException  e)
        {

        }
    }



}