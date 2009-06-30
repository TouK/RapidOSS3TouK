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

    public void testTriggerEventWithParameter()
    {
        EventTriggeringUtilsTestObject obj = new EventTriggeringUtilsTestObject();
        def res = EventTriggeringUtils.triggerEvent (obj, EventTriggeringUtils.BEFORE_DELETE_EVENT);
        assertEquals (1, obj.beforeDeleteParams.size());
        assertEquals (null, obj.beforeDeleteParams[0]);
        assertEquals("beforeDeleteWrapperRes", res);

        def mapToBePassed = [:];
        obj = new EventTriggeringUtilsTestObject();
        res = EventTriggeringUtils.triggerEvent (obj, EventTriggeringUtils.BEFORE_DELETE_EVENT, mapToBePassed);
        assertEquals (1, obj.beforeDeleteParams.size());
        assertSame(mapToBePassed, obj.beforeDeleteParams[0]);
        assertEquals("beforeDeleteWrapperRes", res);

        obj = new EventTriggeringUtilsTestObject();
        res = EventTriggeringUtils.triggerEvent (obj, EventTriggeringUtils.BEFORE_UPDATE_EVENT, null);
        assertEquals(1, obj.beforeUpdateParams.size());
        assertEquals("beforeUpdateCalledRes", res);

        obj = new EventTriggeringUtilsTestObject();
        res = EventTriggeringUtils.triggerEvent (obj, EventTriggeringUtils.BEFORE_UPDATE_EVENT);
        assertEquals(1, obj.beforeUpdateParams.size());
        assertEquals("beforeUpdateCalledRes", res);
    }
}

class   EventTriggeringUtilsTestObject
{
    boolean isOnLoadCalled = false;
    List beforeDeleteParams = [];
    List beforeUpdateParams = [];
    def onLoadWrapper = {
        isOnLoadCalled = true;
    }

    def beforeDeleteWrapper = {params->
        beforeDeleteParams.add(params);
        return "beforeDeleteWrapperRes"
    }

    def beforeUpdateWrapper(){
        beforeUpdateParams.add("beforeUpdateCalled");
        return "beforeUpdateCalledRes"
    }
}
