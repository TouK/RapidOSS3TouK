package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
* Created by IntelliJ IDEA.
* User: mustafa seker
* Date: Jun 14, 2008
* Time: 5:52:18 AM
* To change this template use File | Settings | File Templates.
*/
class EventTriggeringUtilsTest extends RapidCmdbTestCase{
    public void testTriggerEvent()
    {
        EventTriggeringUtilsTestObject obj = new EventTriggeringUtilsTestObject();
        EventTriggeringUtils.triggerEvent (obj, EventTriggeringUtils.ONLOAD_EVENT);
        assertTrue (obj.isOnLoadCalled);
        try
        {
            EventTriggeringUtils.triggerEvent (obj, "undefinedevent");
        }catch(t)
        {
            fail("Should not throw exception if event does not exist")
        }

    }
}

class   EventTriggeringUtilsTestObject
{
    boolean isOnLoadCalled = false;
    def onLoad = {
        isOnLoadCalled = true;
    }
}
