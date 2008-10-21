package com.ifountain.compass

import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import org.compass.core.CompassSession
import org.compass.core.Compass
import org.apache.commons.io.FileUtils
import org.compass.core.CompassTransaction

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 25, 2008
 * Time: 5:48:34 PM
 * To change this template use File | Settings | File Templates.
 */
class SingleCompassSessionManagerTest extends AbstractSearchableCompassTests{
    Compass compass;
    void setUp() {
        compass = TestCompassFactory.getCompass([CompassTestObject]);

    }

    protected void tearDown() {
        compass.close();
        SingleCompassSessionManager.destroy(10);
    }
    public void testBeginTransaction()
    {
        SingleCompassSessionManager.initialize(compass);
        RapidCompassTransaction tr = SingleCompassSessionManager.beginTransaction();
        Thread.sleep (1000);
        CompassSession session = tr.getSession();
        assertNotNull (tr);
        assertFalse (SingleCompassSessionManager.getUncommittedTransactions().contains(tr));
        assertFalse (tr.getSession().isClosed());
        tr.commit();
        assertTrue (tr.getSession().isClosed());
        assertFalse (SingleCompassSessionManager.getUncommittedTransactions().contains(tr));
        tr = SingleCompassSessionManager.beginTransaction();
        assertFalse (tr.getSession().isClosed());
        assertNotSame(session, tr.getSession());
    }


    public void testBeginTransactionWithBatchSupport()
    {
        int maxNumberOfTransactions = 100;
        int maxWaitTime = 1000;
        SingleCompassSessionManager.initialize(compass, maxNumberOfTransactions, maxWaitTime);
        RapidCompassTransaction tr1 = SingleCompassSessionManager.beginTransaction();
        RapidCompassTransaction tr2 = SingleCompassSessionManager.beginTransaction();
        RapidCompassTransaction tr3 = SingleCompassSessionManager.beginTransaction();
        tr1.commit();
        tr2.commit();
        Thread.sleep (1100);
        assertFalse (tr1.getSession().isClosed());
        assertFalse (tr2.getSession().isClosed());
        assertFalse (tr3.getSession().isClosed());
        tr3.commit();
        Thread.sleep (1100);
        assertTrue (tr1.getSession().isClosed());
        assertTrue (tr2.getSession().isClosed());
        assertTrue (tr3.getSession().isClosed());
        RapidCompassTransaction tr4 = SingleCompassSessionManager.beginTransaction();
        assertNotSame(tr1.getSession(), tr4.getSession());
    }


    public void testBeginTransactionWithBatchSupportAndIfThereExistUncommittedTransactionsDoesnotCloseSessionWaitsToFinishTransaction()
    {
        int maxNumberOfTransactions = 2;
        SingleCompassSessionManager.initialize(compass, maxNumberOfTransactions, 1000);
        RapidCompassTransaction tr1 = SingleCompassSessionManager.beginTransaction();
        RapidCompassTransaction tr2 = SingleCompassSessionManager.beginTransaction();
        RapidCompassTransaction tr3 = null;
        boolean isFinished = false;
        boolean isStarted = false;
        Thread.start {
            isStarted = true;
            tr3 = SingleCompassSessionManager.beginTransaction();
            isFinished = true;
        }

        Thread.sleep (1100);
        assertTrue (isStarted)
        assertFalse (isFinished)
        tr1.commit();
        Thread.sleep (1100);
        assertTrue (isStarted)
        assertFalse (isFinished)
        tr2.commit();
        Thread.sleep (1100);
        assertTrue (isFinished)
        assertSame (tr1.getSession(), tr2.getSession());
        assertNotSame (tr1.getSession(), tr3.getSession());
        assertTrue (tr1.getSession().isClosed());
        assertFalse (tr3.getSession().isClosed());
        tr3.commit();
        Thread.sleep (1100);
        assertTrue (tr3.getSession().isClosed());


    }
    public void testTransactionWillBeCommittedAfterSpecifiedTime()
    {
        int maxNumberOfTransactions =1000000;
        int maxWaitTime = 10000000;
        SingleCompassSessionManager.initialize(compass, maxNumberOfTransactions, maxWaitTime);
        RapidCompassTransaction tr1 = SingleCompassSessionManager.beginTransaction();
        long t = System.nanoTime();
        SingleCompassSessionManager.destroy(3000);
        long interval = (long)((System.nanoTime() - t)/Math.pow(10, 6));
        assertTrue (interval >= 3000);
        assertTrue (SingleCompassSessionManager.isClosedLastSession());
    }

    public void testDestroyWillCloseSessionImmediatelyIfThereIsNoTransactionWaitingToBeCommitted()
    {
        int maxNumberOfTransactions =1000000;
        int maxWaitTime = 10000000;
        SingleCompassSessionManager.initialize(compass, maxNumberOfTransactions, maxWaitTime);
        RapidCompassTransaction tr1 = SingleCompassSessionManager.beginTransaction();
        tr1.commit();
        long t = System.nanoTime();
        SingleCompassSessionManager.destroy(3000);
        long interval = (long)((System.nanoTime() - t)/Math.pow(10, 6));
        assertTrue (interval < 3000);
        assertTrue (SingleCompassSessionManager.isClosedLastSession());
    }

    public void testBeginTransactionThrowsExceptionAfterDestroy()
    {
        int maxNumberOfTransactions = 200;
        int maxWaitTime = 1000;
        SingleCompassSessionManager.initialize(compass, maxNumberOfTransactions, maxWaitTime);
        SingleCompassSessionManager.destroy();
        try
        {
            SingleCompassSessionManager.beginTransaction();
            fail("Should throw exception");
        }
        catch(UnInitializedSessionManagerException exception)
        {
        }
    }

    public void testRollbackTransactionThrowsException()
    {
        int maxNumberOfTransactions = 1;
        int maxWaitTime = 0;
        SingleCompassSessionManager.initialize(compass, maxNumberOfTransactions, maxWaitTime);
        CompassTransaction tr = SingleCompassSessionManager.beginTransaction();
        try
        {
            tr.rollback();
            fail("Should throw exception");
        }
        catch(UnsupportedOperationException exception)
        {
        }
        Thread.sleep (100);
        assertTrue (SingleCompassSessionManager.isClosedLastSession());
    }

    public void testDestroyWillMarkSessionToBeClosed()
    {
        int maxNumberOfTransactions = 200;
        int maxWaitTime = 10;
        SingleCompassSessionManager.initialize(compass, maxNumberOfTransactions, maxWaitTime);
        RapidCompassTransaction tr1 = SingleCompassSessionManager.beginTransaction();
        RapidCompassTransaction tr2 = SingleCompassSessionManager.beginTransaction();
        Thread.sleep (100);
        assertFalse (tr1.getSession().isClosed())
        assertFalse (tr2.getSession().isClosed())
        def isDestroyed = false;
        def thread = new Thread({
            SingleCompassSessionManager.destroy(4000000000);
            isDestroyed = true;

        });
        thread.start();
        Thread.sleep (100);
        assertFalse (tr1.getSession().isClosed())
        assertFalse (tr2.getSession().isClosed())
        tr1.commit();
        assertFalse (tr1.getSession().isClosed())
        assertFalse (tr2.getSession().isClosed())
        tr2.commit();
        assertTrue (tr1.getSession().isClosed())
        assertTrue (tr2.getSession().isClosed())
        Thread.sleep (100);        
        assertTrue (isDestroyed);
    }
}