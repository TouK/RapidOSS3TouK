package com.ifountain.compass.transaction.processor

import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.compass.CompassTestObject
import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import org.compass.core.CompassHits
import org.compass.core.CompassQueryBuilder
import org.compass.core.CompassSession
import org.compass.core.lucene.LuceneEnvironment
import org.compass.core.lucene.engine.LuceneSearchEngine
import org.compass.core.lucene.engine.LuceneSearchEngineFactory
import org.compass.core.lucene.engine.transaction.TransactionProcessor
import org.compass.core.lucene.engine.transaction.TransactionProcessorFactory
import org.compass.core.lucene.engine.transaction.readcommitted.ReadCommittedTransactionProcessor
import org.compass.core.spi.InternalCompass

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jun 1, 2009
* Time: 4:54:05 PM
* To change this template use File | Settings | File Templates.
*/
class SingleCompassSessionTransactionProcessorFactoryTest extends AbstractSearchableCompassTests{
    InternalCompass compass;
    SingleCompassSessionTransactionProcessorFactory factory;
    Map additionalCompassSettings;
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        additionalCompassSettings = [:]
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }
    
    public void testTransactionProcessorFactoryConfigurationMethods()
    {
        initialize();
        def compassSettings = compass.getSettings();
        def isConcurrent = new Boolean(compassSettings.getSetting(LuceneEnvironment.Transaction.Processor.ReadCommitted.CONCURRENT_OPERATIONS));
        assertFalse ("concurrent commit should be set to false", isConcurrent);
        assertEquals (1000, factory.getCommitInterval());
        compass.close();


        def newCommitInterval = 10000;
        addCommitIntervalSetting (newCommitInterval)
        initialize();
        assertEquals (newCommitInterval, factory.getCommitInterval());
    }

    public void testCommitsTransactionAtSpecifiedIntervals()
    {
        addCommitIntervalSetting (250);
        initializeWithMockFactory();
        def commitIntervals = [];
        RadCommittedTransactionProcessorWrapper wrapper = TransactionProcessorFactoryMock.wrapperToBeReturned;
        wrapper.closureMap.commit = {boolean onePhase->
            commitIntervals.add(System.nanoTime());
        }
        Thread.sleep (1000);
        assertTrue(commitIntervals.size() >= 3);
        compass.close();
        def commitIntervalCountAfterClose = commitIntervals.size();
        assertTrue ("Warpped factory close method should be called", TransactionProcessorFactoryMock.isCloseCalled);
        for(int i=commitIntervals.size()-2; i >= 0 ; i--)
        {
            assertTrue (commitIntervals[i+1] - commitIntervals[i] < 275*Math.pow(10,6))
        }

        Thread.sleep (1000);
        assertEquals ("after close committer thread should be destroyed", commitIntervalCountAfterClose, commitIntervals.size());

        def newCommitInterval = 800000;
        addCommitIntervalSetting (newCommitInterval)
        commitIntervals.clear();
        initializeWithMockFactory();
        wrapper = TransactionProcessorFactoryMock.wrapperToBeReturned;
        wrapper.closureMap.commit = {boolean onePhase->
            commitIntervals.add(System.nanoTime());
        }
        Thread.sleep (1000);

        assertTrue("Expected 0 but was ${commitIntervals.size()}", commitIntervals.size() == 0);
    }

    public void testSearchOperationAfterCommitWillNotCauseAnyProblem()
    {
        addCommitIntervalSetting (250)
        initialize();
        
        RadCommittedTransactionProcessorWrapper wrapper = TransactionProcessorFactoryMock.wrapperToBeReturned;
        CompassTestObject objToBeSaved1 = new CompassTestObject(id:1);
        CompassTestObject objToBeSaved2 = new CompassTestObject(id:2);
        withCompassQueryBuilder (){CompassQueryBuilder builder->
            withCompassSession (){session->
                session.save (objToBeSaved1);
            }
            CompassHits hits = builder.queryString ("alias:*").toQuery().hits();
            Thread.sleep (1000);
            withCompassSession (){session->
                session.save (objToBeSaved2);
            }
            assertEquals (1, hits.length());
            Object objReturnedFromCompass = hits.data (0);
            assertEquals (objToBeSaved1.id, objReturnedFromCompass.id);
        }
    }

    public void testSearchOperationWillSeeChangesWhichAreNotCommittedYet()
    {
        addCommitIntervalSetting (250000)
        initialize();
        RadCommittedTransactionProcessorWrapper wrapper = TransactionProcessorFactoryMock.wrapperToBeReturned;
        withCompassQueryBuilder (){CompassQueryBuilder builder->
            def savedObjects = []
            withCompassSession (){session->
                for(int i=0; i < 100; i++)
                {
                    savedObjects.add(new CompassTestObject(id:i))
                    session.save (savedObjects[savedObjects.size()-1]);
                }
            }
            CompassHits hits = builder.queryString ("alias:*").toQuery().hits();
            assertEquals (savedObjects.size(), hits.length());
            savedObjects.each{addedObject->
                def objectFromCompass = builder.queryString ("id:${addedObject.id}".toString()).toQuery().hits().data (0)
                assertEquals(addedObject.id, objectFromCompass.id);
            }
        }
    }

    public void testDelete()
    {
        addCommitIntervalSetting (250000)
        initialize();
        RadCommittedTransactionProcessorWrapper wrapper = TransactionProcessorFactoryMock.wrapperToBeReturned;
        withCompassQueryBuilder (){CompassQueryBuilder builder->
            def savedObjects = []
            withCompassSession (){CompassSession session->
                for(int i=0; i < 100; i++)
                {
                    def objectToBeDeleted = new CompassTestObject(id:i);
                    session.save (objectToBeDeleted);
                    session.delete(objectToBeDeleted);
                }
                def object = new CompassTestObject(id:1);
                session.save (object);
                savedObjects.add(object)
            }
            CompassHits hits = builder.queryString ("alias:*").toQuery().hits();
            assertEquals (1, hits.length());
            savedObjects.each{addedObject->
                def objectFromCompass = builder.queryString ("id:${addedObject.id}".toString()).toQuery().hits().data (0)
                assertEquals(addedObject.id, objectFromCompass.id);
            }
        }
    }

    public void testUpdate()
    {
        addCommitIntervalSetting (250000)
        initialize();
        RadCommittedTransactionProcessorWrapper wrapper = TransactionProcessorFactoryMock.wrapperToBeReturned;
        def updatedPropValue = "updatedPropValue"
        withCompassQueryBuilder (){CompassQueryBuilder builder->
            def savedObjects = []
            withCompassSession (){CompassSession session->
                for(int i=0; i < 100; i++)
                {
                    def objectToBeDeleted = new CompassTestObject(id:i);
                    session.save (objectToBeDeleted);
                    objectToBeDeleted.prop1 = updatedPropValue
                    session.save(objectToBeDeleted);
                    savedObjects.add(objectToBeDeleted);
                }
            }
            CompassHits hits = builder.queryString ("alias:*").toQuery().hits();
            assertEquals (savedObjects.size(), hits.length());
            savedObjects.each{addedObject->
                def objectFromCompass = builder.queryString ("id:${addedObject.id}".toString()).toQuery().hits().data (0)
                assertEquals(addedObject.id, objectFromCompass.id);
                assertEquals(updatedPropValue, objectFromCompass.prop1);
            }
        }
    }

    private void addCommitIntervalSetting(long commitInterval)
    {
        additionalCompassSettings.put (SingleCompassSessionTransactionProcessorFactory.COMMIT_INTERVAL_SETTING_KEY, ""+commitInterval);
    }

    private void addSingleSessionFactory()
    {
        additionalCompassSettings.put(LuceneEnvironment.Transaction.Processor.PREFIX+SingleCompassSessionTransactionProcessor.NAME+"."+LuceneEnvironment.Transaction.Processor.CONFIG_TYPE, SingleCompassSessionTransactionProcessorFactory.class.name);
        additionalCompassSettings.put(LuceneEnvironment.Transaction.Processor.TYPE, SingleCompassSessionTransactionProcessor.NAME);
    }

    private initialize()
    {
        addSingleSessionFactory();
        compass = TestCompassFactory.getCompass([CompassTestObject], [], false, additionalCompassSettings);
        factory = ((LuceneSearchEngineFactory)compass.getSearchEngineFactory()).getTransactionProcessorManager().getProcessorFactory (SingleCompassSessionTransactionProcessor.NAME);
    }


    private initializeWithMockFactory()
    {
        addSingleSessionFactory();
        compass = TestCompassFactory.getCompass([CompassTestObject], [], false, additionalCompassSettings);
        factory = ((LuceneSearchEngineFactory)compass.getSearchEngineFactory()).getTransactionProcessorManager().getProcessorFactory (SingleCompassSessionTransactionProcessor.NAME);
        TransactionProcessorFactoryMock mockFactory = new TransactionProcessorFactoryMock();
        factory.setWrappedTransactionProcessorFactory (mockFactory);
        withCompassQueryBuilder(){builder->
            builder.queryString("alias:*").toQuery().hits()            
        }
        CommonTestUtils.waitFor (new ClosureWaitAction(){
            assertNotNull (TransactionProcessorFactoryMock.wrapperToBeReturned);
        })
    }


}

class TransactionProcessorFactoryMock implements TransactionProcessorFactory
{
    public static isCloseCalled = false;
    public static TransactionProcessor wrapperToBeReturned;
    public TransactionProcessor create(LuceneSearchEngine searchEngine) {
        if(wrapperToBeReturned == null)
        {
            wrapperToBeReturned = new  RadCommittedTransactionProcessorWrapper(searchEngine);
        }
        return wrapperToBeReturned;
    }

    public void close() {
        isCloseCalled = true;
        wrapperToBeReturned = null;
    }

    public boolean isThreadSafe() {
        return false; //To change body of implemented methods use File | Settings | File Templates.
    }

}

class RadCommittedTransactionProcessorWrapper extends ReadCommittedTransactionProcessor
{
    def closureMap = [:]
    public RadCommittedTransactionProcessorWrapper(LuceneSearchEngine searchEngine) {
        super(searchEngine); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void commit(boolean onePhase) {
        if(closureMap.commit)
        {
            closureMap.commit(onePhase);
        }
        super.commit(onePhase); //To change body of overridden methods use File | Settings | File Templates.
    }
}
