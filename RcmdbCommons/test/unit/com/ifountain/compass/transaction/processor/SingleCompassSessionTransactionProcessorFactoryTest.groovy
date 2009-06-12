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
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.compass.CompositeDirectoryWrapperProvider
import org.apache.commons.io.FileUtils
import org.compass.core.CompassQueryBuilder.CompassQueryStringBuilder
import org.compass.core.CompassQuery
import org.compass.core.lucene.engine.transaction.mt.MTTransactionProcessorFactory

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jun 1, 2009
* Time: 4:54:05 PM
* To change this template use File | Settings | File Templates.
*/
class SingleCompassSessionTransactionProcessorFactoryTest extends AbstractSearchableCompassTests {
    InternalCompass compass;
    SingleCompassSessionTransactionProcessorFactory factory;
    Class parentObject;
    Class level1ChildObject;
    Class childObject1;
    Class childObject2;
    Class singleObject;
    Map additionalCompassSettings;
    GroovyClassLoader gcl;
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        gcl = new GroovyClassLoader(this.class.classLoader);
        additionalCompassSettings = [:]
        FileUtils.deleteDirectory(new File(TestCompassFactory.indexDirectory));
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        compass.close();
    }

    public void testTransactionProcessorFactoryConfigurationMethods()
    {
        initialize();
        def compassSettings = compass.getSettings();
        def isConcurrent = new Boolean(compassSettings.getSetting(LuceneEnvironment.Transaction.Processor.ReadCommitted.CONCURRENT_OPERATIONS));
        assertFalse("concurrent commit should be set to false", isConcurrent);
        assertEquals(1000, factory.getCommitInterval());
        compass.close();


        def newCommitInterval = 10000;
        addCommitIntervalSetting(newCommitInterval)
        initialize();
        assertEquals(newCommitInterval, factory.getCommitInterval());
    }

    public void testCommitsTransactionAtSpecifiedIntervals()
    {
        addCommitIntervalSetting(250);
        initializeWithMockFactory();
        def commitIntervals = [];
        RadCommittedTransactionProcessorWrapper wrapper = TransactionProcessorFactoryMock.wrapperToBeReturned;
        wrapper.closureMap.commit = {boolean onePhase ->
            commitIntervals.add(System.nanoTime());
        }
        Thread.sleep(1000);
        assertTrue(commitIntervals.size() >= 3);
        compass.close();
        def commitIntervalCountAfterClose = commitIntervals.size();
        assertTrue("Warpped factory close method should be called", TransactionProcessorFactoryMock.isCloseCalled);
        for (int i = commitIntervals.size() - 2; i >= 0; i--)
        {
            assertTrue(commitIntervals[i + 1] - commitIntervals[i] < 275 * Math.pow(10, 6))
        }

        Thread.sleep(1000);
        assertEquals("after close committer thread should be destroyed", commitIntervalCountAfterClose, commitIntervals.size());

        def newCommitInterval = 800000;
        addCommitIntervalSetting(newCommitInterval)
        commitIntervals.clear();
        initializeWithMockFactory();
        wrapper = TransactionProcessorFactoryMock.wrapperToBeReturned;
        wrapper.closureMap.commit = {boolean onePhase ->
            commitIntervals.add(System.nanoTime());
        }
        Thread.sleep(1000);

        assertTrue("Expected 0 but was ${commitIntervals.size()}", commitIntervals.size() == 0);
    }


    public void testClosingFactoryWillCallCommit()
    {
        addCommitIntervalSetting(2500000);
        initializeWithMockFactory();
        def commitIntervals = [];
        RadCommittedTransactionProcessorWrapper wrapper = TransactionProcessorFactoryMock.wrapperToBeReturned;
        wrapper.closureMap.commit = {boolean onePhase ->
            commitIntervals.add(System.nanoTime());
        }
        Thread.sleep(1000);
        assertEquals(0, commitIntervals.size());
        compass.close();
        assertEquals("Should call commit in close method", 1, commitIntervals.size());

    }

    public void testSearchOperationAfterCommitWillNotCauseAnyProblem()
    {
        addCommitIntervalSetting(250)
        initialize();

        RadCommittedTransactionProcessorWrapper wrapper = TransactionProcessorFactoryMock.wrapperToBeReturned;
        def objToBeSaved1 = singleObject.newInstance(id: 1);
        def objToBeSaved2 = singleObject.newInstance(id: 2);
        withCompassQueryBuilder() {CompassQueryBuilder builder ->
            withCompassSession() {session ->
                session.save(objToBeSaved1);
            }
            CompassHits hits = builder.queryString("alias:*").toQuery().hits();
            Thread.sleep(1000);
            withCompassSession() {session ->
                session.save(objToBeSaved2);
            }
            assertEquals(1, hits.length());
            Object objReturnedFromCompass = hits.data(0);
            assertEquals(objToBeSaved1.id, objReturnedFromCompass.id);
        }
    }

    public void testSearchOperationWillSeeChangesWhichAreNotCommittedYet()
    {
        addCommitIntervalSetting(250000)
        initialize();
        RadCommittedTransactionProcessorWrapper wrapper = TransactionProcessorFactoryMock.wrapperToBeReturned;
        withCompassQueryBuilder() {CompassQueryBuilder builder ->
            def savedObjects = []
            withCompassSession() {session ->
                for (int i = 0; i < 100; i++)
                {
                    savedObjects.add(singleObject.newInstance(id: i))
                    session.save(savedObjects[savedObjects.size() - 1]);
                }
            }
            CompassHits hits = builder.queryString("alias:*").toQuery().hits();
            assertEquals(savedObjects.size(), hits.length());
            savedObjects.each {addedObject ->
                def objectFromCompass = builder.queryString("id:${addedObject.id}".toString()).toQuery().hits().data(0)
                assertEquals(addedObject.id, objectFromCompass.id);
            }
        }
    }

    public void testDelete()
    {
        addCommitIntervalSetting(250000)
        initialize();
        RadCommittedTransactionProcessorWrapper wrapper = TransactionProcessorFactoryMock.wrapperToBeReturned;
        withCompassQueryBuilder() {CompassQueryBuilder builder ->
            def savedObjects = []
            withCompassSession() {CompassSession session ->
                for (int i = 0; i < 100; i++)
                {
                    def objectToBeDeleted = singleObject.newInstance(id: i);
                    session.save(objectToBeDeleted);
                    session.delete(objectToBeDeleted);
                }
                def object = singleObject.newInstance(id: 1);
                session.save(object);
                savedObjects.add(object)
            }
            CompassHits hits = builder.queryString("alias:*").toQuery().hits();
            assertEquals(1, hits.length());
            savedObjects.each {addedObject ->
                def objectFromCompass = builder.queryString("id:${addedObject.id}".toString()).toQuery().hits().data(0)
                assertEquals(addedObject.id, objectFromCompass.id);
            }
        }
    }

    public void testUpdate()
    {
        addCommitIntervalSetting(250000)
        initialize();
        RadCommittedTransactionProcessorWrapper wrapper = TransactionProcessorFactoryMock.wrapperToBeReturned;
        def updatedPropValue = "updatedPropValue"
        withCompassQueryBuilder() {CompassQueryBuilder builder ->
            def savedObjects = []
            withCompassSession() {CompassSession session ->
                for (int i = 0; i < 100; i++)
                {
                    def objectToBeDeleted = singleObject.newInstance(id: i);
                    session.save(objectToBeDeleted);
                    objectToBeDeleted.prop1 = updatedPropValue
                    session.save(objectToBeDeleted);
                    savedObjects.add(objectToBeDeleted);
                }
            }
            CompassHits hits = builder.queryString("alias:*").toQuery().hits();
            assertEquals(savedObjects.size(), hits.length());
            savedObjects.each {addedObject ->
                def objectFromCompass = builder.queryString("id:${addedObject.id}".toString()).toQuery().hits().data(0)
                assertEquals(addedObject.id, objectFromCompass.id);
                assertEquals(updatedPropValue, objectFromCompass.prop1);
            }
        }
    }


    public void testSearchWhileUpdating()
    {
        MTTransactionProcessorFactory
        addCommitIntervalSetting(300)
        initialize();
        def updatedPropValue = "updatedPropValue"
        def numberOfObjects = 200;
        withCompassSession() {CompassSession session ->
            for (int i = 0; i < numberOfObjects; i++)
            {
                def objectToBeDeleted = singleObject.newInstance(id: i);
                session.save(objectToBeDeleted);
            }
        }
        withCompassQueryBuilder() {CompassQueryBuilder builder ->
            CompassHits hits = builder.queryString("alias:*").toQuery().hits();
            assertEquals(numberOfObjects, hits.length());
        }
        println "inserted"
        def willStopThread = false;
        Thread t = Thread.start {
            while (!willStopThread)
            {
                withCompassSession() {CompassSession session ->
                    for (int i = 0; i < numberOfObjects; i++)
                    {
                        def objectToBeUpdated = singleObject.newInstance(id: i);
                        objectToBeUpdated.prop1 = updatedPropValue;
                        session.save(objectToBeUpdated);
                    }
                }
            }
        }
        Thread.sleep(3000);
        try {
            for (int i = 0; i < 1000; i++)
            {
                withCompassQueryBuilder() {CompassQueryBuilder builder ->
                    CompassHits hits = builder.queryString("alias:*").toQuery().hits();
                    assertEquals(numberOfObjects, hits.length());
                }
            }
            println "FINISHED"
        }
        finally {
            willStopThread = true;
            t.join();
        }

    }


    public void testSearchWhileUpdatingWithObjectHierarchy()
    {
        addCommitIntervalSetting(300)
        initialize();
        def updatedPropValue = "updatedPropValue"
        def numberOfObjectsForParent = 20;
        def numberOfObjectsForChild = 200;
        withCompassSession() {CompassSession session ->
            def id = 0;
            numberOfObjectsForParent.times{
                def objectToBeAdded = parentObject.newInstance(id: id++);
                session.save(objectToBeAdded);
            }
            numberOfObjectsForChild.times{
                def childToBeAdded = childObject1.newInstance(id: id++);
                session.save(childToBeAdded);
                childToBeAdded = childObject2.newInstance(id: id++);
                session.save(childToBeAdded);
            }
        }                                         
        withCompassQueryBuilder() {CompassQueryBuilder builder ->
            CompassHits hits = builder.queryString("alias:*").toQuery().hits();
            assertEquals(numberOfObjectsForParent+2*numberOfObjectsForChild, hits.length());
        }
        println "inserted"
        def isStopped = false;
        Thread t = Thread.start {
            withCompassSession() {CompassSession session ->

                def id = 0;
                numberOfObjectsForParent.times{
                    def objectToBeAdded = parentObject.newInstance(id: id++);
                    session.save(objectToBeAdded);
                }
                numberOfObjectsForChild.times{
                    def childToBeAdded = childObject1.newInstance(id: id++);
                    session.save(childToBeAdded);
                    childToBeAdded = childObject2.newInstance(id: id++);
                    session.save(childToBeAdded);
                }
            }
            isStopped = true;
        }
        try {
            while(!isStopped)
            {
                withCompassQueryBuilder() {CompassQueryBuilder builder ->
                    CompassQueryStringBuilder strBuilder = builder.queryString("alias:*")
                    CompassQuery query = strBuilder.toQuery();
                    CompassHits hits = query.hits();
                    assertEquals(numberOfObjectsForParent+2*numberOfObjectsForChild, hits.length());
                }
            }
        } finally {
            t.join();
        }

    }

    private void addCommitIntervalSetting(long commitInterval)
    {
        additionalCompassSettings.put(SingleCompassSessionTransactionProcessorFactory.COMMIT_INTERVAL_SETTING_KEY, "" + commitInterval);
        additionalCompassSettings.put("compass.engine.maxBufferedDocs", "1000");
        //        additionalCompassSettings.put("compass.engine.maxBufferedDeletedTerms", "100");
        additionalCompassSettings.put("compass.engine.ramBufferSize", "60");
        additionalCompassSettings.put("compass.engine.cacheIntervalInvalidation", "-1");
    }

    private void addSingleSessionFactory()
    {
        additionalCompassSettings.put(LuceneEnvironment.Transaction.Processor.PREFIX + SingleCompassSessionTransactionProcessor.NAME + "." + LuceneEnvironment.Transaction.Processor.CONFIG_TYPE, SingleCompassSessionTransactionProcessorFactory.class.name);
        additionalCompassSettings.put(LuceneEnvironment.Transaction.Processor.TYPE, SingleCompassSessionTransactionProcessor.NAME);
    }

    private initialize()
    {
        createModels(CompositeDirectoryWrapperProvider.FILE_DIR_TYPE);
        addSingleSessionFactory();
        def app = TestCompassFactory.getGrailsApplication([this.parentObject, this.childObject1, this.childObject2, this.level1ChildObject, this.singleObject], gcl);
        compass = TestCompassFactory.getCompass(app, [], true, additionalCompassSettings);
        factory = ((LuceneSearchEngineFactory) compass.getSearchEngineFactory()).getTransactionProcessorManager().getProcessorFactory(SingleCompassSessionTransactionProcessor.NAME);
    }


    private initializeWithMockFactory()
    {
        createModels(CompositeDirectoryWrapperProvider.FILE_DIR_TYPE);
        addSingleSessionFactory();
        def app = TestCompassFactory.getGrailsApplication([this.parentObject, this.childObject1, this.childObject2, this.level1ChildObject, this.singleObject], gcl);
        compass = TestCompassFactory.getCompass(app, [], false, additionalCompassSettings);
        factory = ((LuceneSearchEngineFactory) compass.getSearchEngineFactory()).getTransactionProcessorManager().getProcessorFactory(SingleCompassSessionTransactionProcessor.NAME);
        TransactionProcessorFactoryMock mockFactory = new TransactionProcessorFactoryMock();
        factory.setWrappedTransactionProcessorFactory(mockFactory);
        withCompassQueryBuilder() {builder ->
            builder.queryString("alias:*").toQuery().hits()
        }
        CommonTestUtils.waitFor(new ClosureWaitAction() {
            assertNotNull(TransactionProcessorFactoryMock.wrapperToBeReturned);
        })
    }

    private createModels(storageType)
    {
        def keyProp = [name: "keyProp", type: ModelGenerator.STRING_TYPE, blank: false];
        String propValue = "ThisIsALongPropValueThisIsALongPropValueThisIsALongPropValueThisIsALongPropValueThisIsALongPropValueThisIsALongPropValue"
        def model1MetaProps = [name: "FactoryParentObject", storageType: storageType]
        def level1ChildMetaProps = [name: "Level1FactoryChildObject1", storageType: storageType, parentModel: model1MetaProps.name]
        def child1MetaProps = [name: "FactoryChildObject1", storageType: storageType, parentModel: level1ChildMetaProps.name]
        def child2MetaProps = [name: "FactoryChildObject2", storageType: storageType, parentModel: level1ChildMetaProps.name]
        def model3MetaProps = [name: "FactorySingleObject", storageType: storageType]
        def modelProps = [keyProp];
        for (int i = 0; i < 50; i++)
        {
            def prop = [name: "prop" + i, type: ModelGenerator.STRING_TYPE, blank: false, defaultValue: propValue]
            modelProps.add(prop);

        }
        def keyPropList = [keyProp];
        String model1String = ModelGenerationTestUtils.getModelText(model1MetaProps, modelProps, keyPropList, [])
        String child1ModelString = ModelGenerationTestUtils.getModelText(child1MetaProps, modelProps, keyPropList, [])
        String child2ModelString = ModelGenerationTestUtils.getModelText(child2MetaProps, modelProps, keyPropList, [])
        String level1ChildModelString = ModelGenerationTestUtils.getModelText(level1ChildMetaProps, modelProps, keyPropList, [])
        String model3String = ModelGenerationTestUtils.getModelText(model3MetaProps, modelProps, keyPropList, [])
        gcl.parseClass(model1String + level1ChildModelString+child1ModelString+child2ModelString + model3String);
        this.parentObject = gcl.loadClass(model1MetaProps.name)
        this.level1ChildObject = gcl.loadClass(level1ChildMetaProps.name)
        this.childObject1 = gcl.loadClass(child1MetaProps.name)
        this.childObject2 = gcl.loadClass(child1MetaProps.name)
        this.singleObject = gcl.loadClass(model3MetaProps.name)
    }

}

class TransactionProcessorFactoryMock implements TransactionProcessorFactory
{
    public static isCloseCalled = false;
    public static TransactionProcessor wrapperToBeReturned;
    public TransactionProcessor create(LuceneSearchEngine searchEngine) {
        if (wrapperToBeReturned == null)
        {
            wrapperToBeReturned = new RadCommittedTransactionProcessorWrapper(searchEngine);
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
        if (closureMap.commit)
        {
            closureMap.commit(onePhase);
        }
        super.commit(onePhase); //To change body of overridden methods use File | Settings | File Templates.
    }
}
