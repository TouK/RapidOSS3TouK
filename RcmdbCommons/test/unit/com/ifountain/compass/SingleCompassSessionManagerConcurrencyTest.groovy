package com.ifountain.compass

import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import org.apache.commons.io.FileUtils
import org.compass.core.Compass
import org.compass.core.CompassQuery
import org.compass.core.CompassQueryBuilder
import org.apache.lucene.store.FSDirectory

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 25, 2008
 * Time: 3:53:16 PM
 * To change this template use File | Settings | File Templates.
 */
class SingleCompassSessionManagerConcurrencyTest extends AbstractSearchableCompassTests
{
    Compass compass
    public static Th1 th1= new Th1();
    void setUp() {
        FileUtils.deleteDirectory(new File(TestCompassFactory.indexDirectory));
    }

    protected void tearDown() {
        compass.close();
        SingleCompassSessionManager.destroy();
    }

    public void testConcurrency()
    {
//        FSDirectory.setDisableLocks(true);
        fail("THIS TEST SHOULD BE RE IMPLEMENTED AFTER COMPASS UPGRADE");
        compass = TestCompassFactory.getCompass([CompassTestObject], null, true)
        int batchSize = 40;
        long maxWaitTime = 10;
        SingleCompassSessionManager.initialize(compass, batchSize, maxWaitTime)

        def lockObj = new Object();
        def obj = new CompassTestObject(id:1, prop1:"prop1val");
        saveToCompass(obj);
        def totalNumberOfExecutedTransactions = 0;

        def threads = [];
        for(int i=0; i < 50; i++)
        {
            def thr = new CompassConcurrencyThread(obj:obj, lockObject:lockObj, type:i%5==0?1:0);
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
//            assertEquals (1000, it.totalNumberOfExecutedTransactions);
        }
        println totalNumberOfExecutedTransactions;

    }
}

class CompassConcurrencyThread extends Thread{
    static int objId= 0;
    static int numberOfFinisheds= 0;
    static Object writeLock= new Object();
    def type = 0;
    def totalNumberOfExecutedTransactions = 0;
    def obj;
    def closure = {
        def tx = SingleCompassSessionManager.beginTransaction()
        try {
            if(type == 0)
            {

                def hits = tx.getSession().queryBuilder().queryString("alias:*").toQuery().hits();
                hits.each {hit->
                    hit.getData();
                }
            }
            else if(type == 1)
            {


                synchronized (writeLock)
                {
                    obj.id = objId++;
                    tx.getSession().save(obj)
                }
            }
            else
            {
                synchronized (writeLock)
                {
                    def hits = tx.getSession().queryBuilder().queryString("alias:*").toQuery().hits();
                    def objects = [];
                    hits?.each {hit->
                        objects+=hit.getData();

                        return;
                    }
                    objects.each{
                    tx.getSession().delete(it);
                    }
                }
            }
            totalNumberOfExecutedTransactions++;
        }
        finally {
            if(tx.wasCommitted())
            {
                println "SESSION CLOSED"
            }
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
            def maxnumberOfOperations = 100000;
            if(type == 0) maxnumberOfOperations = 100000;
            for(; i < maxnumberOfOperations; i++)
            {
                closure();
            }

        }catch(Throwable t)
        {
            t.printStackTrace();
            isExceptionThrown = true;
        }
        finally
        {

                numberOfFinisheds++;
//                println "${System.nanoTime()}\t$numberOfFinisheds";
        }

    }
}

class Th1 extends java.lang.ThreadLocal
{

    protected Object initialValue() {
        return new HashMap();    //To change body of overridden methods use File | Settings | File Templates.
    }

}