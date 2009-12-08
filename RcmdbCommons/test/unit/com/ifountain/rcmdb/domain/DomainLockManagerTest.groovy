package com.ifountain.rcmdb.domain

import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureRunnerThread
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.log4j.Logger

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 17, 2009
* Time: 10:12:47 AM
* To change this template use File | Settings | File Templates.
*/
class DomainLockManagerTest extends RapidCmdbTestCase {

    public void testLockingInstance()
    {
        DomainLockManager.getInstance().initialize(Logger.getRootLogger());
        def thread1State = 0;
        def thread2State = 0;
        String lockName = "lock1";
        Object waitLock = new Object();
        Thread t1 = Thread.start {
            thread1State = 1;
            DomainLockManager.getInstance().lockInstance(lockName);
            DomainLockManager.getInstance().lockInstance(lockName);
            thread1State = 2;
            synchronized (waitLock) {
                waitLock.wait();
            }
            DomainLockManager.getInstance().releaseInstance(lockName);
            synchronized (waitLock) {
                waitLock.wait();
            }
            DomainLockManager.getInstance().releaseInstance(lockName);
            thread1State = 3;
        }
        Thread.sleep(400)
        assertEquals("Writelock is reentrant same owner will be able to get same lock again", 2, thread1State);
        Thread t2 = Thread.start {
            thread2State = 1;
            DomainLockManager.getInstance().lockInstance(lockName);
            DomainLockManager.getInstance().releaseInstance(lockName)
            thread2State = 2;

        }
        Thread.sleep(400);
        assertEquals("Other owner cannot get lock when writelock is belongs to another", 1, thread2State);
        synchronized (waitLock) {
            waitLock.notifyAll();
        }
        Thread.sleep(400);
        assertEquals("Other owner cannot get lock when writelock is belongs to another", 1, thread2State);
        synchronized (waitLock) {
            waitLock.notifyAll();
        }
        t1.join(400)
        t2.join(400)
        assertEquals("After releasing lock new owner can get the lock", 2, thread2State);
    }

    public void testSimpleWriteOperationsCanShareDirectoryLocks() {
        DomainLockManager.getInstance().initialize(Logger.getRootLogger());
        Object waitLock = new Object();
        String lockName = "directory1";
        def thread1State = 0;
        def thread2State = 0;
        Thread t1 = Thread.start {
            DomainLockManager.getInstance().lockDirectory(lockName);
            thread1State = 1;
            synchronized (waitLock) {
                waitLock.wait()
            }
            DomainLockManager.getInstance().releaseDirectory(lockName);
            thread1State = 2;
        }
        Thread.sleep(300);
        assertEquals(1, thread1State);

        Thread t2 = Thread.start {
            DomainLockManager.getInstance().lockDirectory(lockName);
            thread2State = 1;
            synchronized (waitLock) {
                waitLock.wait()
            }
            DomainLockManager.getInstance().releaseDirectory(lockName);
            thread2State = 2;
        }
        Thread.sleep(300);
        assertEquals(1, thread2State);

        synchronized (waitLock) {
            waitLock.notifyAll();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, thread1State)
            assertEquals(2, thread2State)
        }))
    }

    public void testBatchActionsWaitsOnDirectoryLocksForSimpleWriteActions() {
        DomainLockManager.getInstance().initialize(Logger.getRootLogger());
        Object waitLock = new Object();
        String lockName = "directory1";
        def thread1State = 0;
        def thread2State = 0;

        Thread t1 = Thread.start {
            DomainLockManager.getInstance().lockDirectory(lockName);
            thread1State = 1;
            synchronized (waitLock) {
                waitLock.wait()
            }
            DomainLockManager.getInstance().releaseDirectory(lockName);
            thread1State = 2;
        }

        Thread.sleep(300);
        assertEquals(1, thread1State);

        Thread t2 = Thread.start {
            DomainLockManager.getInstance().batchStarted();
            DomainLockManager.getInstance().lockDirectory(lockName);
            thread2State = 1;
            synchronized (waitLock) {
                waitLock.wait();
            }
            DomainLockManager.getInstance().releaseDirectory(lockName);
            DomainLockManager.getInstance().batchFinished();
            thread2State = 2;
        }
        Thread.sleep(300);
        assertEquals(0, thread2State);

        synchronized (waitLock) {
            waitLock.notify();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, thread1State);
            assertEquals(1, thread2State);
        }))
        synchronized (waitLock) {
            waitLock.notify();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, thread2State);
        }))
    }

    public void testBatchActionsWaitsOnDirectoryLocksForBatchActions() {
        DomainLockManager.getInstance().initialize(Logger.getRootLogger());
        Object waitLock = new Object();
        String lockName = "directory1";
        def thread1State = 0;
        def thread2State = 0;

        Thread t1 = Thread.start {
            DomainLockManager.getInstance().batchStarted();
            DomainLockManager.getInstance().lockDirectory(lockName);
            thread1State = 1;
            synchronized (waitLock) {
                waitLock.wait()
            }
            DomainLockManager.getInstance().releaseDirectory(lockName);
            DomainLockManager.getInstance().batchFinished();
            thread1State = 2;
        }

        Thread.sleep(300);
        assertEquals(1, thread1State);

        Thread t2 = Thread.start {
            DomainLockManager.getInstance().batchStarted();
            DomainLockManager.getInstance().lockDirectory(lockName);
            thread2State = 1;
            synchronized (waitLock) {
                waitLock.wait();
            }
            DomainLockManager.getInstance().releaseDirectory(lockName);
            DomainLockManager.getInstance().batchFinished();
            thread2State = 2;
        }
        Thread.sleep(300);
        assertEquals(0, thread2State);

        synchronized (waitLock) {
            waitLock.notify();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, thread1State);
            assertEquals(1, thread2State);
        }))
        synchronized (waitLock) {
            waitLock.notify();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, thread2State);
        }))
    }

    public void testSimpleWriteActionsWaitsOnDirectoryLocksForBatchActions() {
        DomainLockManager.getInstance().initialize(Logger.getRootLogger());
        Object waitLock = new Object();
        String lockName = "directory1";
        def thread1State = 0;
        def thread2State = 0;

        Thread t1 = Thread.start {
            DomainLockManager.getInstance().batchStarted();
            DomainLockManager.getInstance().lockDirectory(lockName);
            thread1State = 1;
            synchronized (waitLock) {
                waitLock.wait()
            }
            DomainLockManager.getInstance().releaseDirectory(lockName);
            DomainLockManager.getInstance().batchFinished();
            thread1State = 2;
        }

        Thread.sleep(300);
        assertEquals(1, thread1State);

        Thread t2 = Thread.start {
            DomainLockManager.getInstance().lockDirectory(lockName);
            thread2State = 1;
            synchronized (waitLock) {
                waitLock.wait();
            }
            DomainLockManager.getInstance().releaseDirectory(lockName);
            thread2State = 2;
        }
        Thread.sleep(300);
        assertEquals(0, thread2State);

        synchronized (waitLock) {
            waitLock.notify();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, thread1State);
            assertEquals(1, thread2State);
        }))
        synchronized (waitLock) {
            waitLock.notify();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, thread2State);
        }))
    }

    public void testSimpleActionsCannotGetDirectoryLocksUntilBatchFinishesItsExecution() {
        DomainLockManager.getInstance().initialize(Logger.getRootLogger());
        Object waitLock = new Object();
        String lockName = "directory1";
        def thread1State = 0;
        def thread2State = 0;

        Thread t1 = Thread.start {
            DomainLockManager.getInstance().batchStarted();
            DomainLockManager.getInstance().lockDirectory(lockName);
            DomainLockManager.getInstance().releaseDirectory(lockName);
            thread1State = 1;
            synchronized (waitLock) {
                waitLock.wait()
            }
            DomainLockManager.getInstance().batchFinished();
            thread1State = 2;
        }

        Thread.sleep(300);
        assertEquals(1, thread1State);

        Thread t2 = Thread.start {
            DomainLockManager.getInstance().lockDirectory(lockName);
            thread2State = 1;
            synchronized (waitLock) {
                waitLock.wait();
            }
            DomainLockManager.getInstance().releaseDirectory(lockName);
            thread2State = 2;
        }
        Thread.sleep(300);
        assertEquals(0, thread2State);

        synchronized (waitLock) {
            waitLock.notify();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, thread1State);
            assertEquals(1, thread2State);
        }))
        synchronized (waitLock) {
            waitLock.notify();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, thread2State);
        }))

    }
    public void testMemoryLeak()
    {
        DomainLockManager.getInstance().initialize(Logger.getRootLogger());
        Runtime.getRuntime().gc();
        Thread.sleep(1000);
        Runtime.getRuntime().gc();
        Thread.sleep(1000);
        Runtime.getRuntime().gc();
        Thread.sleep(1000);
        Runtime.getRuntime().gc();
        Thread.sleep(1000);
        long usedMemoryBeforeLockOperations = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        String lockName = "lock1";
        for (int i = 0; i < 10000; i++)
        {
            def t1 = new ClosureRunnerThread()
            t1.closure = {
                //multiple calls should be performed to see multiple cals does not affect memory conssumption
                DomainLockManager.getInstance().lockInstance(lockName + i);
                DomainLockManager.getInstance().lockInstance(lockName + i);
                DomainLockManager.getInstance().lockInstance(lockName + i);
                DomainLockManager.getInstance().releaseInstance(lockName + i);
                DomainLockManager.getInstance().releaseInstance(lockName + i);
                DomainLockManager.getInstance().releaseInstance(lockName + i);
                DomainLockManager.getInstance().releaseInstance(lockName + i);
            }
            t1.start();
            t1.join();
        }
        Runtime.getRuntime().gc();
        Thread.sleep(1000);
        Runtime.getRuntime().gc();
        Thread.sleep(1000);
        Runtime.getRuntime().gc();
        Thread.sleep(1000);
        Runtime.getRuntime().gc();
        Thread.sleep(1000);

        long usedMemoryAfterLockOperations = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        def difference = usedMemoryAfterLockOperations - usedMemoryBeforeLockOperations;
        println difference;
        assertTrue("$difference should be less than ${Math.pow(2, 12)}", difference < Math.pow(2, 12));
    }

    public void testMemoryLeakWithLockOwnersThrowingLockTimeoutException()
    {
        String lockName = "lock1";
        Object threadStartFlag = new Object();
        boolean willWaitToStart = true;
        Object waitLock = new Object();
        boolean willWait = true;
        def threads = [];
        def threadStates = [];
        def lockCount = 200;
        DomainLockManager.getInstance().initialize(Logger.getRootLogger());

        for (int i = 0; i < lockCount; i++)
        {
            def locali = i;
            threadStates[locali] = 0;
            threads << Thread.start {
                threadStates[locali] = 1;
                synchronized (threadStartFlag)
                {
                    if (willWaitToStart)
                    {
                        threadStartFlag.wait();
                        willWaitToStart = false;
                    }
                }
                DomainLockManager.getInstance().lockInstance(lockName + locali);
                threadStates[locali] = 2;
                synchronized (waitLock)
                {
                    if (willWait)
                    {
                        waitLock.wait();
                        willWait = false;
                    }
                }
                DomainLockManager.getInstance().releaseInstance(lockName + locali);
                threadStates[locali] = 3;
            }
        }

        Runtime.getRuntime().gc();
        Thread.sleep(1000);
        Runtime.getRuntime().gc();
        Thread.sleep(1000);
        Runtime.getRuntime().gc();
        Thread.sleep(1000);
        Runtime.getRuntime().gc();
        Thread.sleep(1000);
        long usedMemoryBeforeLockOperations = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();



        CommonTestUtils.waitFor(new ClosureWaitAction() {
            threadStates.each {
                assertEquals("expected 1 but was ${it}".toString(), 1, it);
            }
        }, 300)
        synchronized (threadStartFlag)
        {
            usedMemoryBeforeLockOperations = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            threadStartFlag.notifyAll();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction() {
            threadStates.each {
                assertEquals("expected 2 but was ${it}".toString(), 2, it);
            }
        }, 300)
        DomainLockManager.getInstance().setInstanceBasedLockTimeout(1);
        for (int i = 0; i < lockCount; i++)
        {
            try {
                DomainLockManager.getInstance().lockInstance(lockName + i);
                fail("Should throw timeout exception since lock is not available");
            }
            catch (org.apache.commons.transaction.locking.LockException exception)
            {

            }
        }
        synchronized (waitLock)
        {
            waitLock.notifyAll();
        }
        threads.each {
            it.join();
        }
        threadStates.each {
            assertEquals(3, it);
        }
        threads = null;
        threadStates = null;
        waitLock = null;
        Runtime.getRuntime().gc();
        Thread.sleep(1000);
        Runtime.getRuntime().gc();
        Thread.sleep(1000);
        Runtime.getRuntime().gc();
        Thread.sleep(1000);
        Runtime.getRuntime().gc();
        Thread.sleep(1000);

        long usedMemoryAfterLockOperations = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        def difference = usedMemoryAfterLockOperations - usedMemoryBeforeLockOperations;
        assertTrue("$difference should be less than ${Math.pow(2, 12)}", difference < Math.pow(2, 12));
    }
}