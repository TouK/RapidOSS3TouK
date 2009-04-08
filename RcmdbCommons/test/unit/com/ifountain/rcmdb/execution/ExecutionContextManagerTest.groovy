package com.ifountain.rcmdb.execution

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 7, 2009
* Time: 2:54:23 PM
* To change this template use File | Settings | File Templates.
*/
class ExecutionContextManagerTest extends RapidCmdbTestCase {
    public void testManager() {
        def context1Params = [param1: "param1Value", param2: "param2Value"]
        ExecutionContextManager manager = new ExecutionContextManager();
        ExecutionContext eContext1 = manager.startExecutionContext(context1Params);
        assertEquals(context1Params.param1, eContext1.param1);
        assertEquals(context1Params.param2, eContext1.param2);
        assertTrue(manager.hasExecutionContext());

        ExecutionContext topContext = manager.getExecutionContext();
        assertSame(eContext1, topContext);

        def context2Params = [param2: "updatedParamValue", param3: "param3Value"]
        ExecutionContext eContext2 = manager.startExecutionContext(context2Params);
        assertNotSame(eContext1, eContext2);

        assertEquals("Previous context should not change", context1Params.param1, eContext1.param1);
        assertEquals("Previous context should not change", context1Params.param2, eContext1.param2);
        assertNull("Previous context should not change", eContext1.param3);

        assertEquals("Since parameter is not specified in context2 it should be inherited", context1Params.param1, eContext2.param1);
        assertEquals("Since parameter is not specified in context2 it should be overwritten", context2Params.param2, eContext2.param2);
        assertEquals("new parameter should be added to context", context2Params.param3, eContext2.param3);
        ExecutionContext topContext2 = manager.getExecutionContext();
        assertSame(eContext2, topContext2);

        ExecutionContext endedContext = manager.endExecutionContext();
        assertSame("Last context should be removed from stack", eContext2, endedContext);
        ExecutionContext topContextAfterFirstEnd = manager.getExecutionContext();
        assertSame("Previous context should be returned current should be removed from stack", eContext1, topContextAfterFirstEnd);

        ExecutionContext endedContext2 = manager.endExecutionContext();
        assertSame("First context should be removed from stack", eContext1, endedContext2);
        ExecutionContext topContextLastEnd = manager.getExecutionContext();
        assertNull("Previous context should be returned current should be removed from stack", topContextLastEnd);
        assertFalse(manager.hasExecutionContext());
    }

    public void testClearContexts()
    {
        ExecutionContextManager manager = new ExecutionContextManager();
        ExecutionContext eContext1 = manager.startExecutionContext([:]);
        assertTrue(manager.hasExecutionContext());
        manager.clearExecutionContexts()
        assertFalse(manager.hasExecutionContext());
    }

    public void testContextsAreThreadLocaled()
    {
        ExecutionContextManager manager = new ExecutionContextManager();
        def t1ContextProps = [param1: "param1Value", param2: "param2Value"]
        ExecutionContext t1ExecutionContext;
        Thread t1 = Thread.start {
            t1ExecutionContext = manager.startExecutionContext(t1ContextProps);
        }
        t1.join();
        boolean hasContext = true;
        Thread t2 = Thread.start {
            hasContext = manager.hasExecutionContext();
        }
        t2.join();
        assertFalse(hasContext);
    }

    public void testContextsAreInheritableThreadLocaled()
    {
        ExecutionContextManager manager = new ExecutionContextManager();
        def t1ContextProps = [param1: "param1Value", param2: "param2Value"]
        ExecutionContext t1ExecutionContext;
        ExecutionContext t1ExecutionContextAfterExecutingT2;
        ExecutionContext t2ExecutionContext;
        def param3Value = "newValueFromThread2"
        Thread t1 = Thread.start {
            t1ExecutionContext = manager.startExecutionContext(t1ContextProps);
            Thread t2 = Thread.start {
                t2ExecutionContext = manager.getExecutionContext();
                t2ExecutionContext.param3 = param3Value
                manager.endExecutionContext(); //this should not affect parent context
            }
            t2.join();
            t1ExecutionContextAfterExecutingT2 = manager.getExecutionContext();
        }
        t1.join();
        assertSame("t1 stack should be seperated from child stack", t1ExecutionContext, t1ExecutionContextAfterExecutingT2);
        t1ContextProps.each {String propName, Object value ->
            assertEquals(value, t1ExecutionContext[propName])
            assertEquals(value, t2ExecutionContext[propName])
        }
        assertEquals("Parent context should be affected", param3Value, t1ExecutionContext.param3);
        assertEquals(param3Value, t2ExecutionContext.param3);
    }

    public void testChildThreadsWithParentWhichDoesNotHaveAnyContexs()
    {
        ExecutionContextManager manager = new ExecutionContextManager();
        boolean t1HasContext = true;
        boolean t2HasContext = true;
        Thread t1 = Thread.start {
            t1HasContext = manager.hasExecutionContext();
            Thread t2 = Thread.start {
                t2HasContext = manager.hasExecutionContext();
            }
            t2.join();
        }
        t1.join();
        assertFalse (t1HasContext);
        assertFalse (t2HasContext);
    }

    public void testSingleton()
    {
        ExecutionContextManager manager1 = ExecutionContextManager.getInstance();
        ExecutionContextManager manager2 = ExecutionContextManager.getInstance();
        assertSame (manager1, manager2)
        ExecutionContextManager.destroy();
        ExecutionContextManager manager3 = ExecutionContextManager.getInstance();
        assertNotSame (manager1, manager3)
    }
}