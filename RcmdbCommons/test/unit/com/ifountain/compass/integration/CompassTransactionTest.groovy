package com.ifountain.compass.integration

import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import org.compass.core.Compass
import org.codehaus.groovy.grails.commons.GrailsApplication
import com.ifountain.rcmdb.domain.util.DomainClassDefaultPropertyValueHolder
import com.ifountain.compass.CompassTestObject
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.compass.core.CompassSession
import com.ifountain.rcmdb.test.util.compass.TestCompassUtils
import com.ifountain.compass.DefaultCompassConfiguration
import org.compass.core.CompassQueryBuilder
import org.apache.commons.io.FileUtils
import org.apache.lucene.store.FSDirectory
import org.compass.core.lucene.LuceneEnvironment
import org.apache.lucene.store.LockFactory
import org.apache.lucene.store.NoLockFactory
import org.apache.lucene.store.SingleInstanceLockFactory

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 31, 2009
* Time: 3:54:01 PM
* To change this template use File | Settings | File Templates.
*/
class CompassTransactionTest extends AbstractSearchableCompassTests {
    Compass compass;

    public void setUp() {
        super.setUp()
        FileUtils.deleteDirectory (new File(TestCompassFactory.indexDirectory));
        DomainClassDefaultPropertyValueHolder.destroy();
    }

    public void tearDown() {
        super.tearDown();
        DomainClassDefaultPropertyValueHolder.destroy();
        if (compass)
        {
            compass.close();
        }
    }

    public void testMemoryUsageWithSingleBatch()
    {
        initializeCompass(false);
        Runtime.getRuntime().gc();
        Thread.sleep (200);
        Runtime.getRuntime().gc();
        Thread.sleep (200);
        Runtime.getRuntime().gc();
        Thread.sleep (1000);
        Runtime.getRuntime().gc();
        int id = 0;
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        def numberOfObjectsToBeInserted = 1000;
        TestCompassUtils.withCompassSession(compass){CompassSession session->
            for(int i=0; i < numberOfObjectsToBeInserted; i++)
            {
                session.save (new CompassTestObject(id:id++));
            }
            Runtime.getRuntime().gc();
            Thread.sleep (1000);
            Runtime.getRuntime().gc();
            usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - usedMemory;
        }
        def expectedMemoryUsage = 21;
        def usedMemInMb = usedMemory/Math.pow(2,20);
        assertTrue ("${numberOfObjectsToBeInserted} should use memory less than ${expectedMemoryUsage} but was ${usedMemInMb}", usedMemInMb < expectedMemoryUsage);
        def numberOfInsertedObjects = TestCompassUtils.countHits (compass){CompassQueryBuilder queryBuilder->
            return queryBuilder.queryString ("alias:*").toQuery();
        }
        assertEquals (numberOfObjectsToBeInserted, numberOfInsertedObjects);
    }

    public void test100Objects10PerTurn()
    {
        _testInsertionTimeWithMultipleBatch(1000, 10, 4000, false);
        _testInsertionTimeWithMultipleBatch(1000, 10, 4000, true);
    }


    public void test100Objects100PerTurn()
    {
        _testInsertionTimeWithMultipleBatch(1000, 100, 3000,false);
        _testInsertionTimeWithMultipleBatch(1000, 100, 3000,true);
    }


    public void test1000bjects1000PerTurn()
    {
        _testInsertionTimeWithMultipleBatch(1000, 1000, 3000,false);
        _testInsertionTimeWithMultipleBatch(1000, 1000, 3000,true);
    }

    public void testUpdate100Objects10PerTurn()
    {
        _testUpdateTimeWithMultipleBatch(1000, 10, 4000, false);
        _testUpdateTimeWithMultipleBatch(1000, 10, 4000, true);
    }


    public void testUpdate100Objects100PerTurn()
    {
        _testUpdateTimeWithMultipleBatch(1000, 100, 3000,false);
        _testUpdateTimeWithMultipleBatch(1000, 100, 3000,true);
    }


    public void testUpdate1000bjects1000PerTurn()
    {
        _testUpdateTimeWithMultipleBatch(1000, 1000, 3000,false);
        _testUpdateTimeWithMultipleBatch(1000, 1000, 3000,true);
    }

    public void testChangesAreVisibleToScopeInSameTransaction()
    {

        initializeCompass(false);
        int id = 0;
        TestCompassUtils.withCompassSession(compass){CompassSession session->
            session.save (new CompassTestObject(id:id++));
            assertEquals(1, session.queryBuilder().queryString ("id:0").toQuery().hits().length());
        }
    }

    public void testChangesAreNotVisibleToScopeOfAnotherTransaction()
    {

        initializeCompass(false);
        Object lock = new Object();
        int id = 0;
        Thread t = Thread.start{
            TestCompassUtils.withCompassSession(compass){CompassSession session->
                session.save (new CompassTestObject(id:id++));
                synchronized (lock)
                {
                    lock.wait ();
                }
                assertEquals(1, session.queryBuilder().queryString ("id:0").toQuery().hits().length());
            }
        }
        Thread.sleep (500);
        def foundObjects = TestCompassUtils.countHits(compass){CompassQueryBuilder builder->
            return builder.queryString ("id:0").toQuery();
        }
        assertEquals (0, foundObjects);
        synchronized (lock)
        {
            lock.notifyAll ();
        }
        t.join ();

    }

    //
//    public void testTwoTransactionsCanCommitToSameIndex()
//    {
//        initializeCompass(true);
//        Object lock1 = new Object();
//        Object lock2 = new Object();
//        int id = 0;
//        Thread t1 = Thread.start{
//            TestCompassUtils.withCompassSession(compass){CompassSession session->
//                println session;
//                session.save (new CompassTestObject(id:id++));
//                synchronized (lock1)
//                {
//                    lock1.wait ();
//                }
//            }
//        }
//        Thread.sleep (500);
//        def thread2State = 0;
//        Thread t2 = Thread.start{
//            TestCompassUtils.withCompassSession(compass){CompassSession session->
//                println session;
//                thread2State = 1;
//                session.save (new CompassTestObject(id:id++));
//                thread2State = 2;
//            }
//        }
//        try
//        {
//            t2.join ();
//        }
//        catch(Exception e)
//        {
//            fail("Should not throw exception");
//        }
//        try{
//            assertEquals (2, thread2State);
//        }finally{
//            synchronized (lock1)
//            {
//                lock1.notifyAll ();
//            }
//            t2.join ();
//        }
//    }

    public void testCommittedChangedWillBeVisibleToTransactionStartedBeforeChange()
    {

        initializeCompass(false);
        Object lock = new Object();
        int id = 0;
        def numberOfFoundObjects = 0;
        Thread t = Thread.start{
            TestCompassUtils.withCompassSession(compass){CompassSession session->
                synchronized (lock)
                {
                    lock.wait ();
                }
                numberOfFoundObjects = session.queryBuilder().queryString ("id:0").toQuery().hits().length();
            }
        }
        Thread.sleep (500);
        TestCompassUtils.withCompassSession(compass){CompassSession session->
            session.save (new CompassTestObject(id:id++));
            assertEquals(1, session.queryBuilder().queryString ("id:0").toQuery().hits().length());
        }
        synchronized (lock)
        {
            lock.notifyAll ();
        }
        t.join ();
        assertEquals (1, numberOfFoundObjects);

    }


    public void _testInsertionTimeWithMultipleBatch(numberOfObjectsToBeInserted, numberOfObjectsToBeInsertedPerTurn, expectedInsertionTime, willPersist)
    {
        initializeCompass(willPersist);
        int id = 0;
        long time = 0;
        for(int k=0; k < numberOfObjectsToBeInserted; k+=numberOfObjectsToBeInsertedPerTurn)
        {
            def tmpTime = System.currentTimeMillis();
            TestCompassUtils.withCompassSession(compass){CompassSession session->
                for(int i=0; i < numberOfObjectsToBeInsertedPerTurn; i++)
                {
                    session.save (new CompassTestObject(id:id++));
                }
                time += System.currentTimeMillis() - tmpTime;
            }
        }
        def numberOfInsertedObjects = TestCompassUtils.countHits (compass){CompassQueryBuilder queryBuilder->
            return queryBuilder.queryString ("alias:*").toQuery();
        }
        assertEquals (numberOfObjectsToBeInserted, numberOfInsertedObjects);
        println "Total insertion time ${time} for total ${numberOfObjectsToBeInserted} and per Turn insertion ${numberOfObjectsToBeInsertedPerTurn}. with ${willPersist?"persisted":"non-persisted"} compass"
        assertTrue("${numberOfObjectsToBeInserted} should be inserted in ${expectedInsertionTime} but was ${time}", time < expectedInsertionTime);
        compass.close();
    }

    public void _testUpdateTimeWithMultipleBatch(numberOfObjectsToBeInserted, numberOfObjectsToBeInsertedPerTurn, expectedInsertionTime, willPersist)
    {
        initializeCompass(willPersist);
        int id = 0;
        for(int k=0; k < numberOfObjectsToBeInserted; k+=numberOfObjectsToBeInsertedPerTurn)
        {
            TestCompassUtils.withCompassSession(compass){CompassSession session->
                for(int i=0; i < numberOfObjectsToBeInsertedPerTurn; i++)
                {
                    session.save (new CompassTestObject(id:id++));
                }
            }
        }
        id = 0;
        long time = 0;
        for(int k=0; k < numberOfObjectsToBeInserted; k+=numberOfObjectsToBeInsertedPerTurn)
        {
            def tmpTime = System.currentTimeMillis();
            TestCompassUtils.withCompassSession(compass){CompassSession session->
                for(int i=0; i < numberOfObjectsToBeInsertedPerTurn; i++)
                {
                    session.save (new CompassTestObject(id:id++));
                }
                time += System.currentTimeMillis() - tmpTime;
            }
        }
        def numberOfInsertedObjects = TestCompassUtils.countHits (compass){CompassQueryBuilder queryBuilder->
            return queryBuilder.queryString ("alias:*").toQuery();
        }
        assertEquals (numberOfObjectsToBeInserted, numberOfInsertedObjects);
        println "Total update time ${time} for total ${numberOfObjectsToBeInserted} and per Turn update ${numberOfObjectsToBeInsertedPerTurn}. with ${willPersist?"persisted":"non-persisted"} compass"
        assertTrue("${numberOfObjectsToBeInserted} should be updated in ${expectedInsertionTime} but was ${time}", time < expectedInsertionTime);
        compass.close();
    }

    def initializeCompass(persisted)
    {
        DomainClassDefaultPropertyValueHolder.initialize ([CompassTestObject]);
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;
        def defaultSettings = DefaultCompassConfiguration.getDefaultSettings(null);
        defaultSettings.remove("compass.engine.store.wrapper.wrapper1.type");
        defaultSettings.remove("compass.engine.store.indexDeletionPolicy.type");
        defaultSettings.put("compass.engine.store.lockFactory.type", SingleInstanceLockFactory.name)
        compass = TestCompassFactory.getCompass(application, null, persisted, defaultSettings);
    }
}