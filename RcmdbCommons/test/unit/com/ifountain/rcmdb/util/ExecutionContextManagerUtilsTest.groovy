package com.ifountain.rcmdb.util

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.execution.ExecutionContextManager
import com.ifountain.rcmdb.execution.ExecutionContext
import org.apache.log4j.Logger
import org.springframework.mock.web.MockHttpServletResponse

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 7, 2009
* Time: 6:40:21 PM
* To change this template use File | Settings | File Templates.
*/
class ExecutionContextManagerUtilsTest extends RapidCmdbTestCase {

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        ExecutionContextManager.destroy();
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        ExecutionContextManager.destroy();
    }
    public void testAddObjectAndRemoveObjectAndGetObject()
    {
        def key="myobject";
        def object=new Long(8);

        assertFalse(ExecutionContextManager.getInstance().hasExecutionContext());
        assertNull(ExecutionContextManagerUtils.getObjectFromCurrentContext(key));

        ExecutionContextManagerUtils.addObjectToCurrentContext(key,object);
        assertFalse("Since there is no context object should not be added", ExecutionContextManager.getInstance().hasExecutionContext());
        ExecutionContextManager.getInstance().startExecutionContext([:]);
        assertTrue(ExecutionContextManager.getInstance().hasExecutionContext());
        assertNull(ExecutionContextManagerUtils.getObjectFromCurrentContext(key));

        ExecutionContextManagerUtils.addObjectToCurrentContext(key,object);

        ExecutionContext currContext = ExecutionContextManager.getInstance().getExecutionContext();
        assertSame ("context object should be same as given object", object, currContext[key]);

        assertSame(object,ExecutionContextManagerUtils.getObjectFromCurrentContext(key));

        ExecutionContextManagerUtils.removeObjectFromCurrentContext(key);
        assertNull("context object should be null since it is removed",currContext[key]);

        assertNull(ExecutionContextManagerUtils.getObjectFromCurrentContext(key));

        //add again
        ExecutionContextManagerUtils.addObjectToCurrentContext(key,object);
        assertSame ("context object should be same as given object", object, currContext[key]);
        assertSame(object,ExecutionContextManagerUtils.getObjectFromCurrentContext(key));

        //try deletion with no context, nothing will happen
        ExecutionContextManager.getInstance().endExecutionContext();
        assertNull(ExecutionContextManagerUtils.getObjectFromCurrentContext(key));
        
        ExecutionContextManagerUtils.removeObjectFromCurrentContext(key);
        assertFalse("Since the is no context nothing is done", ExecutionContextManager.getInstance().hasExecutionContext());
        assertNull(ExecutionContextManagerUtils.getObjectFromCurrentContext(key));

    }

    public void testAddLogger()
    {
        assertNull(ExecutionContextManagerUtils.getLoggerFromCurrentContext());

        Logger logger = Logger.getLogger("logger1")
        ExecutionContextManagerUtils.addLoggerToCurrentContext(logger);
        assertFalse("Since the is no context logger should not be added", ExecutionContextManager.getInstance().hasExecutionContext());
        ExecutionContextManager.getInstance().startExecutionContext([:]);
        ExecutionContextManagerUtils.addLoggerToCurrentContext(logger);

        ExecutionContext currContext = ExecutionContextManager.getInstance().getExecutionContext();
        assertSame ("context logger should be same as given logger", logger, currContext[RapidCMDBConstants.LOGGER]);
        assertSame(logger,ExecutionContextManagerUtils.getLoggerFromCurrentContext());
    }
    public void testAddUsername()
    {
        assertNull(ExecutionContextManagerUtils.getUsernameFromCurrentContext());

        String userName = "user1"
        ExecutionContextManagerUtils.addUsernameToCurrentContext(userName);
        assertFalse("Since the is no context username should not be added", ExecutionContextManager.getInstance().hasExecutionContext());
        ExecutionContextManager.getInstance().startExecutionContext([:]);
        ExecutionContextManagerUtils.addUsernameToCurrentContext(userName);

        ExecutionContext currContext = ExecutionContextManager.getInstance().getExecutionContext();
        assertSame ("context username should be same as given username", userName, currContext[RapidCMDBConstants.USERNAME]);
        assertSame(userName,ExecutionContextManagerUtils.getUsernameFromCurrentContext());
    }
    public void testAddWebResponse()
    {
        assertNull(ExecutionContextManagerUtils.getWebResponseFromCurrentContext());

        def mockResponse=new MockHttpServletResponse();
        ExecutionContextManagerUtils.addWebResponseToCurrentContext(mockResponse);
        assertFalse("Since the is no context response should not be added", ExecutionContextManager.getInstance().hasExecutionContext());
        ExecutionContextManager.getInstance().startExecutionContext([:]);
        ExecutionContextManagerUtils.addWebResponseToCurrentContext(mockResponse);

        ExecutionContext currContext = ExecutionContextManager.getInstance().getExecutionContext();
        assertSame ("context response should be same as given username", mockResponse, currContext[RapidCMDBConstants.WEB_RESPONSE]);
        assertSame(mockResponse, ExecutionContextManagerUtils.getWebResponseFromCurrentContext());
    }
    public void testExecuteInContext()
    {
        def contextProps = [param1: "param1Value"]
        ExecutionContext executedContext = null;
        ExecutionContextManagerUtils.executeInContext(contextProps) {
            executedContext = ExecutionContextManager.getInstance().getExecutionContext();
        }
        assertFalse(ExecutionContextManager.getInstance().hasExecutionContext());
        contextProps.each {String propName, Object propvalue ->
            assertEquals(propvalue, executedContext[propName]);
        }

    }

    public void testExecuteInContextEndsContextIfExceptionIsThrown()
    {
        def contextProps = [param1: "param1Value"]
        ExecutionContext executedContext = null;
        Exception e = new Exception("Exception to be thrown");
        try {
            ExecutionContextManagerUtils.executeInContext(contextProps) {
                executedContext = ExecutionContextManager.getInstance().getExecutionContext();
                throw e;
            }
            fail("Should throw exception");
        }
        catch (Exception thrownException) {
            assertSame(thrownException, e);
        }
        assertFalse(ExecutionContextManager.getInstance().hasExecutionContext());
        contextProps.each {String propName, Object propvalue ->
            assertEquals(propvalue, executedContext[propName]);
        }
    }

}

