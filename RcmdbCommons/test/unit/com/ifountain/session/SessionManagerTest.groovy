package com.ifountain.session

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.test.util.ClosureRunnerThread
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import junit.framework.Assert

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 15, 2009
* Time: 11:50:49 AM
* To change this template use File | Settings | File Templates.
*/
class SessionManagerTest extends RapidCmdbTestCase
{
    public void testCreateSession()
    {
        String username = "user1";
        SessionManager manager = new SessionManager();
        Session createdSession = manager.startSession(username);

        assertEquals(username, createdSession.get(SessionManager.SESSION_USERNAME_KEY));

        Session currentSession = manager.getSession();
        assertSame(createdSession, currentSession);

        currentSession = manager.getSession();
        assertSame(createdSession, currentSession);
        assertFalse(currentSession.isDestroyed());
        manager.endSession();
        assertTrue(currentSession.isDestroyed());
        assertNotSame(currentSession, manager.getSession());
        assertFalse(manager.getSession().isDestroyed());

    }

    public void testIfSesionIsNotCreatedGetCreatesOne()
    {
        SessionManager manager = new SessionManager();
        Session session = manager.getSession();
        assertNull(session.get(SessionManager.SESSION_USERNAME_KEY));
    }

    public void testCreatingNewSessionWillClosePreviousOne()
    {
        SessionManager manager = new SessionManager();
        Session session1 = manager.startSession("user1");
        Session session2 = manager.startSession("user2");
        assertTrue(session1.isDestroyed());
        assertFalse(session2.isDestroyed());
    }

    public void testCreateNewSessionWillNotReCreateSessionIfUserIsSame()
    {
        SessionManager manager = new SessionManager();
        Session session1 = manager.startSession("user1");
        Session session2 = manager.startSession("user1");
        assertSame(session2, session1);
    }

    public void testSessionManagerWithThreading()
    {
        SessionManager manager = new SessionManager();
        Session thread1Session;
        Session thread2Session;
        Session childThreadSession;
        String userName = "user1";
        ClosureRunnerThread thread1 = new ClosureRunnerThread();
        thread1.closure = {
            thread1Session = manager.startSession(userName)
            ClosureRunnerThread thread3 = new ClosureRunnerThread();
            thread3.closure = {
                childThreadSession = manager.startSession(userName);
            }
            thread3.start();
            thread3.join();
        }
        thread1.start();
        thread1.join();
        ClosureRunnerThread thread2 = new ClosureRunnerThread();
        thread2.closure = {
            thread2Session = manager.startSession(userName)
        }
        thread2.start();
        thread2.join();
        assertNotSame(thread1Session, thread2Session);
        assertSame(thread1Session, childThreadSession);
    }

    public void testAddSessionListener()
    {
        String userName = "user1";
        SessionManager manager = new SessionManager();
        MockSessionListener listener = new MockSessionListener();
        manager.addSessionListener(listener);
        Session session = manager.startSession(userName);
        assertEquals(2, session.size());
        assertEquals("started0", session.get("started0"));
        assertEquals(userName, session.get(SessionManager.SESSION_USERNAME_KEY));

        manager.endSession();
        assertEquals(3, session.size());
        assertEquals("started0", session.get("started0"));
        assertEquals("ended0", session.get("ended0"));
        assertEquals(userName, session.get(SessionManager.SESSION_USERNAME_KEY));

        //Add same listener twice
        manager.addSessionListener(listener);
        session = manager.startSession(userName);
        assertEquals(2, session.size());
        manager.endSession();
        assertEquals(3, session.size());

        //remove listener
        manager.removeSessionListener(listener);
        session = manager.startSession(userName);
        assertEquals(1, session.size());
        manager.endSession();

        //remove all listeners
        manager.addSessionListener(listener);
        manager.removeAllListeners();
        session = manager.startSession(userName);
        assertEquals(1, session.size());
        manager.endSession();
    }

    public void testSessionsAreUniqueToTheirOwnThreads() {
        def threads = [];
        Object waitLock = new Object();
        100.times {
            Thread t = new ClosureRunnerThread(closure: {
                Session currentSession = SessionManager.getInstance().getSession();
                synchronized (waitLock) {
                    waitLock.wait();
                }
                100.times {
                    10.times {
                        Session session = SessionManager.getInstance().getSession();
                        assertSame(currentSession, session);
                    }
                    SessionManager.getInstance().endSession();
                    Session newSession = SessionManager.getInstance().getSession();
                    assertNotSame(currentSession, newSession);
                    currentSession = newSession;
                }
            })
            t.start();
            threads.add(t);
        }

        Thread.sleep(1000);
        synchronized (waitLock) {
            waitLock.notifyAll();
        }
        Thread.sleep(1000);
        CommonTestUtils.waitFor(new ClosureWaitAction({
            threads.each {ClosureRunnerThread t ->
                assertTrue(t.isFinished);
                assertNull(t.exception);
            }
        }))
    }

}

class MockSessionListener implements SessionListener {
    int numberOfEnd = 0;
    int numberOfStart = 0;
    public void sessionEnded(Session session) {
        session.put("ended" + numberOfEnd, "ended" + numberOfEnd);
        numberOfEnd++;
    }

    public void sessionStarted(Session session) {
        session.put("started" + numberOfStart, "started" + numberOfStart);
        numberOfStart++;
    }

}