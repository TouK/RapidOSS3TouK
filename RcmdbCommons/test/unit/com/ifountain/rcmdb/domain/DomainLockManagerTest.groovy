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
                DomainLockManager.getLock (t1, lockName+i);
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
}