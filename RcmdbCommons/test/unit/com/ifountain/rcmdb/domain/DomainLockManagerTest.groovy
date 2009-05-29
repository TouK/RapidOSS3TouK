package com.ifountain.rcmdb.domain

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.log4j.Logger
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.rcmdb.test.util.ClosureRunnerThread

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 17, 2009
* Time: 10:12:47 AM
* To change this template use File | Settings | File Templates.
*/
class DomainLockManagerTest extends RapidCmdbTestCase{
    public void testMemoryLeak()
    {
        DomainLockManager.initialize (10000, Logger.getRootLogger());
        Runtime.getRuntime().gc();
        Thread.sleep (1000);
        Runtime.getRuntime().gc();
        Thread.sleep (1000);
        Runtime.getRuntime().gc();
        Thread.sleep (1000);
        Runtime.getRuntime().gc();
        Thread.sleep (1000);
        long usedMemoryBeforeLockOperations = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        String lockName = "lock1";
        for(int i=0; i < 10000; i++)
        {
            def t1 = new ClosureRunnerThread()
            t1.closure = {
                //multiple calls should be performed to see multiple cals does not affect memory conssumption 
                DomainLockManager.getLock(DomainLockManager.WRITE_LOCK,t1, lockName+i);
                DomainLockManager.getLock(DomainLockManager.WRITE_LOCK,t1, lockName+i);
                DomainLockManager.getLock(DomainLockManager.WRITE_LOCK,t1, lockName+i);
                DomainLockManager.releaseLock(t1, lockName+i);
                DomainLockManager.releaseLock(t1, lockName+i);
                DomainLockManager.releaseLock(t1, lockName+i);
                DomainLockManager.releaseLock(t1, lockName+i);
            }
            t1.start();
            t1.join ();
        }
        println DomainLockManager.lockAccessObjects.size()
        Runtime.getRuntime().gc();
        Thread.sleep (1000);
        Runtime.getRuntime().gc();
        Thread.sleep (1000);
        Runtime.getRuntime().gc();
        Thread.sleep (1000);
        Runtime.getRuntime().gc();
        Thread.sleep (1000);

        long usedMemoryAfterLockOperations = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        def difference = usedMemoryAfterLockOperations-usedMemoryBeforeLockOperations;
        println difference;
        assertTrue ("$difference should be less than ${Math.pow(2, 12)}", difference < Math.pow(2, 12));
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
        DomainLockManager.initialize (10000, Logger.getRootLogger());

        for(int i=0; i < lockCount; i++)
        {
            def locali = i;
            threadStates[locali] = 0;
            threads << Thread.start{
                threadStates[locali] = 1;
                synchronized (threadStartFlag)
                {
                    if(willWaitToStart)
                    {
                        threadStartFlag.wait ();
                        willWaitToStart = false;
                    }
                }
                DomainLockManager.getLock(DomainLockManager.WRITE_LOCK,Thread.currentThread(), lockName+locali);
                threadStates[locali] = 2;
                synchronized (waitLock)
                {
                    if(willWait)
                    {
                        waitLock.wait ();
                        willWait = false;
                    }
                }
                DomainLockManager.releaseLock(Thread.currentThread(), lockName+locali);
                threadStates[locali] = 3;
            }
        }

        Runtime.getRuntime().gc();
        Thread.sleep (1000);
        Runtime.getRuntime().gc();
        Thread.sleep (1000);
        Runtime.getRuntime().gc();
        Thread.sleep (1000);
        Runtime.getRuntime().gc();
        Thread.sleep (1000);
        long usedMemoryBeforeLockOperations = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();



        CommonTestUtils.waitFor (new ClosureWaitAction(){
            threadStates.each{
                assertEquals ("expected 1 but was ${it}".toString(), 1, it);
            }
        }, 300)
        synchronized (threadStartFlag)
        {
            usedMemoryBeforeLockOperations = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            threadStartFlag.notifyAll();
        }
        CommonTestUtils.waitFor (new ClosureWaitAction(){
            threadStates.each{
                assertEquals ("expected 2 but was ${it}".toString(), 2, it);
            }
        }, 300)
        DomainLockManager.setLockTimeout (1);
        for(int i=0; i < lockCount; i++)
        {
            try{
                DomainLockManager.getLock(DomainLockManager.WRITE_LOCK,this, lockName+i);
                fail("Should throw timeout exception since lock is not available");
            }
            catch(org.apache.commons.transaction.locking.LockException exception)
            {

            }
        }
        synchronized (waitLock)
        {
            waitLock.notifyAll();
        }
        threads.each{
            it.join();
        }
        threadStates.each{
            assertEquals(3, it);
        }
        threads = null;
        threadStates = null;
        waitLock = null;
        println DomainLockManager.lockAccessObjects.size()
        println DomainLockManager.lockAccessObjects.values().accessCount
        Runtime.getRuntime().gc();
        Thread.sleep (1000);
        Runtime.getRuntime().gc();
        Thread.sleep (1000);
        Runtime.getRuntime().gc();
        Thread.sleep (1000);
        Runtime.getRuntime().gc();
        Thread.sleep (1000);

        long usedMemoryAfterLockOperations = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        def difference = usedMemoryAfterLockOperations-usedMemoryBeforeLockOperations;
        assertTrue ("$difference should be less than ${Math.pow(2, 12)}", difference < Math.pow(2, 12));
    }

    public void testWriteLock()
    {
        DomainLockManager.initialize (10000, Logger.getRootLogger());
        def thread1State = 0;
        String lockName = "lock1";
        Object lockowner1 = new Object();
        Object lockowner2 = new Object();
        assertFalse (DomainLockManager.hasLock(lockowner1, lockName));
        assertFalse(DomainLockManager.hasLock(lockowner1))
        DomainLockManager.getLock(DomainLockManager.WRITE_LOCK,lockowner1, lockName);
        assertTrue (DomainLockManager.hasLock(lockowner1, lockName));
        assertTrue(DomainLockManager.hasLock(lockowner1))
        Thread t1 = Thread.start{
            thread1State = 1;
            DomainLockManager.getLock(DomainLockManager.WRITE_LOCK,lockowner1, lockName);
            thread1State = 2;
        }
        t1.join (1000);
        assertEquals ("Writelock is reentrant same owner will be able to get same lock again", 2, thread1State);
        thread1State = 0;
        t1 = Thread.start{
            thread1State = 1;
            DomainLockManager.getLock(DomainLockManager.WRITE_LOCK,lockowner2, lockName);
            thread1State = 2;
        }
        Thread.sleep (400);
        assertEquals ("Other owner cannot get lock when writelock is belongs to another", 1, thread1State);
        DomainLockManager.releaseLock(lockowner1, lockName);
        t1.join (400);
        assertEquals ("After releasing lock new owner can get the lock", 2, thread1State);
    }
}