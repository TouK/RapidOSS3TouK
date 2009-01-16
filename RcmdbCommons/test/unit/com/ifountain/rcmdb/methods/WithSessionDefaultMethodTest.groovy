package com.ifountain.rcmdb.methods

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.session.SessionManager

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 15, 2009
* Time: 5:51:02 PM
* To change this template use File | Settings | File Templates.
*/
class WithSessionDefaultMethodTest extends RapidCmdbTestCase{

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        SessionManager.destroyInstance();
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        SessionManager.destroyInstance();
    }

    public void testRun()
    {
        String username = "user1";
        String closureCalledFor = "";
        def closureToBeExecute = {
            closureCalledFor+=SessionManager.getInstance().getSession().get(SessionManager.SESSION_USERNAME_KEY);
        }
        String userBeforeMethodCall = "user2";
        SessionManager.getInstance().startSession (userBeforeMethodCall);
        WithSessionDefaultMethod method = new WithSessionDefaultMethod(username, closureToBeExecute)
        method.run();
        assertEquals (username, closureCalledFor);
        assertEquals (userBeforeMethodCall, SessionManager.getInstance().getSession().get(SessionManager.SESSION_USERNAME_KEY));

    }

    public void testRunWithClosureThrowingException()
    {
        String username = "user1";
        String closureCalledFor = "";
        def exceptionWillBeThrown = new Exception();
        def closureToBeExecute = {
            closureCalledFor+=SessionManager.getInstance().getSession().get(SessionManager.SESSION_USERNAME_KEY);
            throw exceptionWillBeThrown;
        }
        String userBeforeMethodCall = "user2";
        SessionManager.getInstance().startSession (userBeforeMethodCall);
        WithSessionDefaultMethod method = new WithSessionDefaultMethod(username, closureToBeExecute)
        try
        {
            method.run();
            fail("Should throw exception");
        }
        catch(Exception e)
        {
            assertSame (exceptionWillBeThrown, e);
        }
        assertEquals (username, closureCalledFor);
        assertEquals (userBeforeMethodCall, SessionManager.getInstance().getSession().get(SessionManager.SESSION_USERNAME_KEY));

    }
}