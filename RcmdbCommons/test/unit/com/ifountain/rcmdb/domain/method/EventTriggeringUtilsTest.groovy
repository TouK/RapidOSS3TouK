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
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction

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
        EventTriggeringUtils.getInstance().triggerEvent (obj, EventTriggeringUtils.ONLOAD_EVENT);
        assertTrue (obj.isOnLoadCalled);
        try
        {
            EventTriggeringUtils.getInstance().triggerEvent (obj, "undefinedevent");
        }catch(t)
        {
            fail("Should not throw exception if event does not exist")
        }

    }

    public void testTriggerEventWithParameter()
    {
        EventTriggeringUtilsTestObject obj = new EventTriggeringUtilsTestObject();
        def res = EventTriggeringUtils.getInstance().triggerEvent (obj, EventTriggeringUtils.BEFORE_DELETE_EVENT);
        assertEquals (1, obj.beforeDeleteParams.size());
        assertEquals (null, obj.beforeDeleteParams[0]);
        assertEquals("beforeDeleteWrapperRes", res);

        def mapToBePassed = [:];
        obj = new EventTriggeringUtilsTestObject();
        res = EventTriggeringUtils.getInstance().triggerEvent (obj, EventTriggeringUtils.BEFORE_DELETE_EVENT, mapToBePassed);
        assertEquals (1, obj.beforeDeleteParams.size());
        assertSame(mapToBePassed, obj.beforeDeleteParams[0]);
        assertEquals("beforeDeleteWrapperRes", res);

        obj = new EventTriggeringUtilsTestObject();
        res = EventTriggeringUtils.getInstance().triggerEvent (obj, EventTriggeringUtils.BEFORE_UPDATE_EVENT, null);
        assertEquals(1, obj.beforeUpdateParams.size());
        assertEquals("beforeUpdateCalledRes", res);

        obj = new EventTriggeringUtilsTestObject();
        res = EventTriggeringUtils.getInstance().triggerEvent (obj, EventTriggeringUtils.BEFORE_UPDATE_EVENT);
        assertEquals(1, obj.beforeUpdateParams.size());
        assertEquals("beforeUpdateCalledRes", res);
    }

    public void testAfterEventsAreTriggeredAfterBatchExecutionFinished(){
       EventTriggeringUtilsTestObject obj = new EventTriggeringUtilsTestObject();
       Object waitLock = new Object();
       def t1state = 0;
       def t1 = Thread.start{
           EventTriggeringUtils.getInstance().batchStarted();
           EventTriggeringUtils.getInstance().triggerEvent (obj, EventTriggeringUtils.BEFORE_UPDATE_EVENT);
           EventTriggeringUtils.getInstance().triggerEvent (obj, EventTriggeringUtils.AFTER_UPDATE_EVENT);
           t1state = 1;
           synchronized(waitLock){
               waitLock.wait();
           }
           EventTriggeringUtils.getInstance().batchFinished();
       }

       CommonTestUtils.waitFor(new ClosureWaitAction({
           assertEquals(1, t1state)
       }))
       assertEquals (1, obj.beforeUpdateParams.size());
       assertEquals (0, obj.afterUpdateParams.size());

       synchronized(waitLock){
           waitLock.notifyAll();
       }
       t1.join();
       assertEquals (1, obj.afterUpdateParams.size());
    }

    public void testIfOneAfterEventThrowsExceptionOtherEventsAreTriggeredAfterBatchExecutionFinish(){
       EventTriggeringUtilsTestObject obj = new EventTriggeringUtilsTestObject();
       Object waitLock = new Object();
       def t1state = 0;
       def t1 = Thread.start{
           EventTriggeringUtils.getInstance().batchStarted();
           obj.afterUpdateWillThrowException = true;
           EventTriggeringUtils.getInstance().triggerEvent (obj, EventTriggeringUtils.AFTER_UPDATE_EVENT);
           EventTriggeringUtils.getInstance().triggerEvent (obj, EventTriggeringUtils.AFTER_DELETE_EVENT);
           EventTriggeringUtils.getInstance().batchFinished();
       }
       t1.join();
       assertEquals (0, obj.afterUpdateParams.size());
       assertEquals (1, obj.afterDeleteParams.size());
    }

    public void testBatchExecutionContextIsThreadLocal() {
        EventTriggeringUtilsTestObject obj = new EventTriggeringUtilsTestObject();
        Object waitLock = new Object();
        def t1state = 0;
        def t1 = Thread.start {
            EventTriggeringUtils.getInstance().batchStarted();
            EventTriggeringUtils.getInstance().triggerEvent (obj, EventTriggeringUtils.AFTER_UPDATE_EVENT);
            t1state = 1;
            synchronized (waitLock) {
                waitLock.wait();
            }
            EventTriggeringUtils.getInstance().batchFinished();
            t1state = 2;
        }

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, t1state);
        }))
        assertEquals (0, obj.afterUpdateParams.size());

        def t2 = Thread.start {
            EventTriggeringUtils.getInstance().triggerEvent (obj, EventTriggeringUtils.AFTER_UPDATE_EVENT);
        }
        t2.join();
        assertEquals (1, obj.afterUpdateParams.size());
        synchronized (waitLock) {
            waitLock.notifyAll();
        }
        t1.join();
        assertEquals (2, obj.afterUpdateParams.size());
    }
}

class EventTriggeringUtilsTestObject
{
    boolean isOnLoadCalled = false;
    List beforeDeleteParams = [];
    List afterDeleteParams = [];
    List beforeUpdateParams = [];
    List afterUpdateParams = [];
    boolean afterUpdateWillThrowException = false;
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
    def afterUpdateWrapper(){
        if(afterUpdateWillThrowException){
            throw new Exception("after update exception")
        }
        afterUpdateParams.add("afterUpdateCalled");
        return "afterUpdateCalledRes"
    }
    def afterDeleteWrapper(){
        afterDeleteParams.add("afterDeleteCalled");
        return "afterDeleteCalledRes"
    }
}
