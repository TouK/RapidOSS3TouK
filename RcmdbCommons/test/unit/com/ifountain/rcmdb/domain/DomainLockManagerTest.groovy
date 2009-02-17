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
        for(int i=0; i < 10000; i++)
        {
            Thread t1 = Thread.start{
                DomainLockManager.getLock (this, lockName+i);
                DomainLockManager.releaseLock(this, lockName+i);
            }
            t1.join ();
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