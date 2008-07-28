package com.ifountain.compass

import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 25, 2008
 * Time: 3:53:16 PM
 * To change this template use File | Settings | File Templates.
 */
class SingleCompassSessionManagerConcurrencyTest extends AbstractSearchableCompassTests
{
    def compass

    void setUp() {
    }

    protected void tearDown() {
        compass.close();
    }

    public void testConcurrency()
    {
        compass = TestCompassFactory.getCompass([CompassTestObject])
        int batchSize = 10;
        long maxWaitTime = 10;
        SingleCompassSessionManager.initialize(compass, batchSize, maxWaitTime) 

        def lockObj = new Object();
        def obj = new CompassTestObject(id:1, prop1:"prop1val");
        saveToCompass(obj);
        def totalNumberOfExecutedTransactions = 0;

        def threads = [];
        for(int i=0; i < 200; i++)
        {
            def thr = new CompassConcurrencyThread(obj:obj, lockObject:lockObj);
            threads += thr;
            thr.start();
        }
        Thread.sleep (1000)
        synchronized (lockObj)
        {
            lockObj.notifyAll();
        }
        threads.each
        {
            it.join();
            assertFalse (it.isExceptionThrown);
            assertEquals (1000, it.totalNumberOfExecutedTransactions);
        }
        println totalNumberOfExecutedTransactions;

    }
}

class CompassConcurrencyThread extends Thread{
    def totalNumberOfExecutedTransactions = 0;
    def obj;
    def closure = {
        def tx = SingleCompassSessionManager.beginTransaction()
        try {
            tx.getSession().load(CompassTestObject, obj.id)
            totalNumberOfExecutedTransactions++;
        }
        finally {
            tx.commit()
        }
    }
    def lockObject;
    def isExceptionThrown = false;
    public void run()
    {
        int i =0;
        try
        {
            synchronized(lockObject)
            {
                lockObject.wait ();
            }
            for(; i < 1000; i++)
            {
                closure();
            }
        }catch(Throwable t)
        {
            t.printStackTrace();
            System.err.println("HATA "+i)
            System.exit (0);

            isExceptionThrown = true;
        }

    }
}