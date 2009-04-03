package com.ifountain.rcmdb.domain

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.log4j.Logger

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
        Thread t1 = null;
        for(int i=0; i < 10000; i++)
        {
            t1 = Thread.start{
                DomainLockManager.getLock(DomainLockManager.WRITE_LOCK,t1, lockName+i);
                DomainLockManager.releaseLock(t1, lockName+i);
            }
            t1.join ();
            t1 = null;
        }

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

    public void testWriteLock()
    {
        DomainLockManager.initialize (10000, Logger.getRootLogger());
        def thread1State = 0;
        String lockName = "lock1";
        Object lockowner1 = new Object();
        Object lockowner2 = new Object();
        DomainLockManager.getLock(DomainLockManager.WRITE_LOCK,lockowner1, lockName);
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

    public void testBulkIndexCheckLock()
    {
        DomainLockManager.initialize (10000, Logger.getRootLogger());
        def thread1State = 0;
        String lockName = "lock1";
        Object lockowner1 = new Object();
        Object lockowner2 = new Object();
        Object lockowner3 = new Object();
        DomainLockManager.getLock(DomainLockManager.BULK_INDEX_CHECK_LOCK, lockowner1, lockName);
        Thread t1 = Thread.start{
            thread1State = 1;
            DomainLockManager.getLock(DomainLockManager.BULK_INDEX_CHECK_LOCK, lockowner1, lockName);
            thread1State = 2;
        }
        t1.join (1000);
        assertEquals ("BuulkIndexChecking is reentrant same owner will be able to get same lock again", 2, thread1State);
        thread1State = 0;
        t1 = Thread.start{
            thread1State = 1;
            DomainLockManager.getLock(DomainLockManager.BULK_INDEX_CHECK_LOCK, lockowner2, lockName);
            thread1State = 2;
        }
        t1.join (1000);
        assertEquals ("BuulkIndexChecking will allow request from same level", 2, thread1State);
    }
    public void testBulkIndexLock()
    {
        DomainLockManager.initialize (10000, Logger.getRootLogger());
        def thread1State = 0;
        String lockName = "lock1";
        Object lockowner1 = new Object();
        Object lockowner2 = new Object();
        Object lockowner3 = new Object();
        DomainLockManager.getLock(DomainLockManager.BULK_INDEX_LOCK, lockowner1, lockName);
        Thread t1 = Thread.start{
            thread1State = 1;
            DomainLockManager.getLock(DomainLockManager.BULK_INDEX_LOCK, lockowner1, lockName);
            thread1State = 2;
        }
        t1.join (1000);
        assertEquals ("BuulkIndex is reentrant same owner will be able to get same lock again", 2, thread1State);
        thread1State = 0;
        t1 = Thread.start{
            thread1State = 1;
            DomainLockManager.getLock(DomainLockManager.BULK_INDEX_LOCK, lockowner2, lockName);
            thread1State = 2;
        }
        Thread.sleep (400);
        assertEquals ("BuulkIndex will not allow request from another owner", 1, thread1State);
        
        DomainLockManager.releaseLock(lockowner1, lockName);
        t1.join (1000);
        assertEquals ("BuulkIndex will allow new owner if previous owner releases lock", 2, thread1State);
    }

    public void testFirstBulkIndexThenBulkIndexCheckingLock()
    {
        DomainLockManager.initialize (10000, Logger.getRootLogger());
        def thread1State = 0;
        String lockName = "lock1";
        Object lockowner1 = new Object();
        Object lockowner2 = new Object();
        Object lockowner3 = new Object();
        DomainLockManager.getLock(DomainLockManager.BULK_INDEX_LOCK, lockowner1, lockName);
        Thread t1 = Thread.start{
            thread1State = 1;
            DomainLockManager.getLock(DomainLockManager.BULK_INDEX_LOCK, lockowner1, lockName);
            thread1State = 2;
        }
        t1.join (1000);
        assertEquals ("BuulkIndex is reentrant same owner will be able to get same lock again", 2, thread1State);
        
        thread1State = 0;
        t1 = Thread.start{
            thread1State = 1;
            DomainLockManager.getLock(DomainLockManager.BULK_INDEX_CHECK_LOCK, lockowner2, lockName);
            thread1State = 2;
        }
        Thread.sleep (400);
        assertEquals ("BuulkIndexCheck will not allow request from another level", 1, thread1State);

        DomainLockManager.releaseLock(lockowner1, lockName);
        t1.join (1000);
        assertEquals ("BuulkIndex will allow new owner if previous owner releases lock", 2, thread1State);
    }

    public void testFirstBulkIndexCheckingThenBulkIndexLock()
    {
        DomainLockManager.initialize (10000, Logger.getRootLogger());
        def thread1State = 0;
        String lockName = "lock1";
        Object lockowner1 = new Object();
        Object lockowner2 = new Object();
        Object lockowner3 = new Object();
        DomainLockManager.getLock(DomainLockManager.BULK_INDEX_CHECK_LOCK, lockowner1, lockName);
        Thread t1 = Thread.start{
            thread1State = 1;
            DomainLockManager.getLock(DomainLockManager.BULK_INDEX_LOCK, lockowner2, lockName);
            thread1State = 2;
        }
        Thread.sleep (400);
        assertEquals ("BuulkIndexCheck will not allow locks from t1 thread with owner loclOwner3", 1, thread1State);

        def thread2State = 0;
        Thread t2 = Thread.start{
            thread2State = 1;
            DomainLockManager.getLock(DomainLockManager.BULK_INDEX_CHECK_LOCK, lockowner3, lockName);
            thread2State = 2;
        }
        Thread.sleep (400);
        assertEquals ("t2 will be blocked since BulkIndex is requested by t1 previously", 1, thread2State);

        DomainLockManager.releaseLock(lockowner1, lockName);
        Thread.sleep (400);
        assertEquals ("BuulkIndex will get lock since previous owner released lock", 2, thread1State);

        def thread3State = 0;
        Thread t3 = Thread.start{
            thread3State = 1;
            DomainLockManager.getLock(DomainLockManager.BULK_INDEX_CHECK_LOCK, lockowner2, lockName);
            thread3State = 2;
        }
        t3.join (1000);
        assertEquals ("Since lockOwner2 already have BulkIndex lock t3 will not be blocked", 2, thread3State);

        DomainLockManager.releaseLock(lockowner2, lockName);

        Thread.sleep (400);
        assertEquals ("t2 will get lock since previous owner released lock", 2, thread2State);
    }
}