package performance.writeOperations

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.compass.CompositeDirectoryWrapperProvider
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.rcmdb.domain.DomainLockManager
import com.ifountain.comp.test.util.logging.TestLogUtils
import junit.framework.TestSuite
import org.apache.commons.transaction.locking.GenericLock
import com.ifountain.rcmdb.domain.cache.IdCache

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 26, 2009
* Time: 3:32:22 PM
* To change this template use File | Settings | File Templates.
*/
class NewPerfTest extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        IdCache.initialize (100000)
    }

    public void testMultiThreadsWithAllAddingNewObjects()
    {
        def numberOfAddThreads = 20;
        def numberOfObjsPerThread = 100;
        def numberOfInitialObjects = 0;
        def expectedNumberOfAdds = 100;
        def modelName = "model1";
        def willAddSameObjects = false;
        _testAddPerformance (numberOfAddThreads, numberOfObjsPerThread, numberOfInitialObjects, expectedNumberOfAdds, modelName, willAddSameObjects);
    }

    public void testUpdate()
    {
        def classesMap = intializeCompassWithSimpleObjects(CompositeDirectoryWrapperProvider.FILE_DIR_TYPE);
    }

    public void testMultiThreadsWithAllAddingSameObjects()
    {
        def numberOfAddThreads = 20;
        def numberOfObjsPerThread = 100;
        def numberOfInitialObjects = 0;
        def expectedNumberOfAdds = 100;
        def modelName = "model1";
        def willAddSameObjects = true;
        _testAddPerformance (numberOfAddThreads, numberOfObjsPerThread, numberOfInitialObjects, expectedNumberOfAdds, modelName, willAddSameObjects);
    }



    public void testSearchOperationPerformanceWithSingleModelsWhileAddOperationContinueWithMax1()
    {
        def numberOfSearchoperations = 200;
        def numberOfAddThreads = 10;
        def numberOfSearchThreads = 40;
        def numberOfInitialObjects = 80;
         def maxCount = 1;
         def expectedNumberOfSearchOperation = 500;
         def addOperationSleepTime = 0;
        _testSearchPerformanceWithDifferentModelsWhileAddContinue(numberOfAddThreads, addOperationSleepTime, numberOfSearchThreads, numberOfSearchoperations, numberOfInitialObjects, maxCount, expectedNumberOfSearchOperation, "model1");

    }

    public void testSearchOperationPerformanceWithSingleModelsWhileSlowlyAddingWithMax1()
    {
        def numberOfSearchoperations = 200;
        def numberOfAddThreads = 1;
        def numberOfSearchThreads = 40;
        def numberOfInitialObjects = 80;
        def maxCount = 1;
        def expectedNumberOfSearchOperation = 700;
        def addOperationSleepTime = 15;
        _testSearchPerformanceWithDifferentModelsWhileAddContinue(numberOfAddThreads, addOperationSleepTime, numberOfSearchThreads, numberOfSearchoperations, numberOfInitialObjects, maxCount, expectedNumberOfSearchOperation, "model1");
    }

    public void testSearchOperationPerformanceWithSingleModelsWhileFastAddingWithMax10()
    {
        def numberOfSearchoperations = 200;
        def numberOfAddThreads = 5;
        def numberOfSearchThreads = 40;
        def numberOfInitialObjects = 80;
        def maxCount = 10;
        def expectedNumberOfSearchOperation = 150;
        def addOperationSleepTime = 0;
        _testSearchPerformanceWithDifferentModelsWhileAddContinue(numberOfAddThreads, addOperationSleepTime, numberOfSearchThreads, numberOfSearchoperations, numberOfInitialObjects, maxCount, expectedNumberOfSearchOperation, "model1");
    }

    public void testSearchOperationPerformanceWithSingleModelsWhileSlowlyAddingWithMax10()
    {
        def numberOfSearchoperations = 200;
        def numberOfAddThreads = 1;
        def numberOfSearchThreads = 40;
        def numberOfInitialObjects = 80;
        def maxCount = 10;
        def expectedNumberOfSearchOperation = 150;
        def addOperationSleepTime = 15;
        _testSearchPerformanceWithDifferentModelsWhileAddContinue(numberOfAddThreads, addOperationSleepTime, numberOfSearchThreads, numberOfSearchoperations, numberOfInitialObjects, maxCount, expectedNumberOfSearchOperation, "model1");
    }

    public void testSearchOperationPerformanceWitSingleModelsWithMax1AndNoAdd()
    {
        def numberOfSearchoperations = 200;
        def numberOfAddThreads = 0;
        def numberOfSearchThreads = 40;
        def numberOfInitialObjects = 100;
         def maxCount = 1;
         def expectedNumberOfSearchOperation = 900;
         def addOperationSleepTime = 0;
        _testSearchPerformanceWithDifferentModelsWhileAddContinue(numberOfAddThreads, addOperationSleepTime, numberOfSearchThreads, numberOfSearchoperations, numberOfInitialObjects, maxCount, expectedNumberOfSearchOperation, "model1");

    }

    def _testAddPerformance(numberOfThreads, numberOfObjsPerThread, numberOfInitialObjects, expectedNumberOfAdds, modelName, willAddSameObjects)
    {
        def classesMap = intializeCompassWithSimpleObjects(CompositeDirectoryWrapperProvider.FILE_DIR_TYPE);
        Object waitLock = new Object();
        Object idIncreaseLock = new Object();
        def classes = new ArrayList(classesMap.values())
        def id = 0;
        def threadInfo = executeInThreads (waitLock, numberOfThreads){threadId->
            def model = modelName!=null?classesMap[modelName]:classes[(int)(Math.random()*classes.size())];
            long t = System.nanoTime();
            for (int i = 0; i < numberOfObjsPerThread; i++)
            {
                def localid  = -1;
                synchronized (idIncreaseLock)
                {
                    if(willAddSameObjects)
                    {
                        localid = i;
                    }else{
                        localid = id;
                        id++;
                    }
                }
                model.add(keyProp: "keyvalue" + localid);
                if (i == 0)
                {
                    println "thread ${threadId} started"
                }
            }
            def totDur = (System.nanoTime() - t) / Math.pow(10, 6);
            println "thread ${threadId} finished"
        }
        def time = System.nanoTime();
        synchronized (waitLock)
        {
            waitLock.notifyAll();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction() {
            threadInfo.threads.each {
                it.join();
            }
        }, 300);
        def totalTime = (System.nanoTime() - time) / Math.pow(10, 9)
        threadInfo.threadStates.each {
            assertTrue("expected 3 but was ${it}".toString(), it == 3);
        }
        if(willAddSameObjects)
        {
            assertEquals(numberOfObjsPerThread, classesMap[modelName].count());
            assertEquals(numberOfObjsPerThread, classesMap.model1.count() + classesMap.model3.count() + classesMap.model2.count());
        }
        else
        {
//            assertEquals(numberOfObjsPerThread*numberOfThreads, classesMap.model1.count() + classesMap.model3.count() + classesMap.model2.count());
        }
        def totalTimePerAdd = totalTime/(numberOfThreads * numberOfObjsPerThread)
        def numberOfInsertedObjectPerSec = 1/totalTimePerAdd;

        println "whole time for ${numberOfThreads * numberOfObjsPerThread} is ${totalTime}"
        println " number of objects inserted per secs ${numberOfInsertedObjectPerSec}"
        assertTrue ("Expected to add ${expectedNumberOfAdds} but was ${numberOfInsertedObjectPerSec}", numberOfInsertedObjectPerSec >= expectedNumberOfAdds);

    }
    def _testSearchPerformanceWithDifferentModelsWhileAddContinue(numberOfAddThreads, addOperationSleepTime, numberOfSearchThreads, numberOfSearchoperations, numberOfInitialObjects, maxCount, expectedNumberOfSearch, modelName)
    {
        def classesMap = intializeCompassWithSimpleObjects(CompositeDirectoryWrapperProvider.FILE_DIR_TYPE);
        Object addOperationWaitLock = new Object();
        Object searchOperationWaitLock = new Object();
        def classes = new ArrayList(classesMap.values());
        def id = 0;
        classesMap.each{String name, Class model->
            if(modelName == null || modelName == name)
            {
                for (int i = 0; i < numberOfInitialObjects; i++)
                {
                    model.add(keyProp: "keyvalue" + id);
                    id++;
                }
            }
        }
        classesMap.each{String name, Class model->
            println "Number of initial objects for ${model.name} is ${model.count()}";
        }

        def addThreadsStopped = false;
        def addThreadInfo = executeInThreads(addOperationWaitLock, numberOfAddThreads){threadId->
            def model = modelName!=null?classesMap[modelName]:classes[(int)(Math.random()*classes.size())]
            while(!addThreadsStopped)
            {
                synchronized (this)
                {
                    id++;
                }
               model.add(keyProp: "keyvalue" + id);
               if(addOperationSleepTime > 0)
               {
                Thread.sleep (addOperationSleepTime);
               }
            }
        }
        def searchThreadInfo = executeInThreads(searchOperationWaitLock, numberOfSearchThreads){threadId->

            def model = modelName!=null?classesMap[modelName]:classes[(int)(Math.random()*classes.size())]
            def time = System.nanoTime();
            for (int i = 0; i < numberOfSearchoperations; i++)
            {
                model.search("alias:*", [max:maxCount]);
            }
        }
        synchronized (addOperationWaitLock)
        {
            addOperationWaitLock.notifyAll();
        }

        Thread.sleep (3000)
        def time = System.nanoTime();
        synchronized (searchOperationWaitLock)
        {
            searchOperationWaitLock.notifyAll();
        }
        searchThreadInfo.threads.each{
            it.join();
        }
        def totalSearchTime = System.nanoTime()-time
        totalSearchTime = totalSearchTime / Math.pow(10, 9)
        def searchTimeForPerOperation = totalSearchTime / (numberOfSearchThreads*numberOfSearchoperations);
        def searchOprCountPerSec = 1 / searchTimeForPerOperation;
        addThreadsStopped = true;
        addThreadInfo.threads.each{
            it.join();
        }
        println "After all threads finished";
        println "Total search time ${totalSearchTime} secs"
        println "Search time for per operation ${searchTimeForPerOperation} secs"
        println "Number of search operations ${searchOprCountPerSec} per sec"
        assertTrue("Average expected number of search operations ${expectedNumberOfSearch} but was ${searchOprCountPerSec} secs", searchOprCountPerSec >= expectedNumberOfSearch );
        addThreadInfo.threads.each{
            it.join();
        }
    }

    private Map executeInThreads(Object addWaitLock, int numberOfAddThreads, Closure colosureToBeInvoked)
    {
        def addThreads = [];
        def addThreadStates = [];
        boolean willWait = true;
        for (int j = 0; j < numberOfAddThreads; j++)
        {
            def locali = j;
            addThreadStates[locali] = 0;
            def thread = Thread.start {
                addThreadStates[locali] = 1;
                synchronized (addWaitLock)
                {
                    if (willWait)
                    {
                        addWaitLock.wait();
                        willWait = false;
                    }
                }
                addThreadStates[locali] = 2;
                try {
                    colosureToBeInvoked(locali)
                    addThreadStates[locali] = 3;
                } catch (Throwable e)
                {
                    e.printStackTrace();
                    addThreadStates[locali] = 4;
                }

            };
            addThreads.add(thread);
        }
        CommonTestUtils.waitFor(new ClosureWaitAction() {
            addThreadStates.each {
                assertTrue("Expected 1 but was ${0}", it == 1);
            }
        }, 800);
        return [threads:addThreads, threadStates:addThreadStates];
    }
    def intializeCompassWithSimpleObjects(String storageType)
    {
        DomainLockManager.getInstance().initialize( TestLogUtils.log, 100000);
        def keyProp = [name: "keyProp", type: ModelGenerator.STRING_TYPE, blank: false];
        String propValue = "ThisIsALongPropValueThisIsALongPropValueThisIsALongPropValueThisIsALongPropValueThisIsALongPropValueThisIsALongPropValue"
        def model1MetaProps = [name: "Model1", storageType: storageType]
        def model2MetaProps = [name: "Model2", storageType: storageType]
        def model3MetaProps = [name: "Model3", storageType: storageType]
        def modelProps = [keyProp];
        for (int i = 0; i < 50; i++)
        {
            def prop = [name: "prop" + i, type: ModelGenerator.STRING_TYPE, blank: false, defaultValue: propValue]
            modelProps.add(prop);

        }
        def keyPropList = [keyProp];
        String model1String = ModelGenerationTestUtils.getModelText(model1MetaProps, modelProps, keyPropList, [])
        String model2String = ModelGenerationTestUtils.getModelText(model2MetaProps, modelProps, keyPropList, [])
        String model3String = ModelGenerationTestUtils.getModelText(model3MetaProps, modelProps, keyPropList, [])
        this.gcl.parseClass(model1String + model2String + model3String);
        Class model1Class = this.gcl.loadClass("Model1")
        Class model2Class = this.gcl.loadClass("Model2")
        Class model3Class = this.gcl.loadClass("Model3")
        initialize([model1Class, model2Class, model3Class], [], true);
        return [model1: model1Class, model2: model2Class, model3: model3Class];
    }

}