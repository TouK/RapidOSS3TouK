package com.ifountain.rcmdb.datasource

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import script.CmdbScript
import com.ifountain.rcmdb.scripting.ScriptManager
import org.apache.commons.io.FileUtils
import com.ifountain.rcmdb.util.DataStore
import datasource.BaseListeningDatasource
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 2, 2009
* Time: 3:37:39 PM
* To change this template use File | Settings | File Templates.
*/
class ListeningAdapterManagerSynchronizationTest extends RapidCmdbTestCase {
    public static final String testOutputDir = "../testoutput";
    static def scriptMap;
    protected void setUp() {
        super.setUp();
        scriptMap = [:];
        ScriptManager.destroyInstance();
        ListeningAdapterManager.destroyInstance();
        def outDir = new File(testOutputDir);
        FileUtils.deleteDirectory (outDir);
        outDir.mkdirs();
        DataStore.clear();
    }
    protected void tearDown() {
        super.tearDown();
        ScriptManager.destroyInstance();
        ListeningAdapterManager.destroyInstance();
    }


    public void testStartAdapter()
    {
        DataStore.put ("initLock", new Object());
        DataStore.put ("numberOfRunCalls", 0);
        DataStore.put ("numberOfGetParametersCalls", 0);
        DataStore.put ("numberOfInitCalls", 0);
        DataStore.put ("numberOfUpdateCalls", 0);
        def scriptText = """
        import ${DataStore.class.name};
        def numberOfRunCalls = DataStore.get ("numberOfRunCalls");
        DataStore.put ("numberOfRunCalls", ++numberOfRunCalls);

        def getParameters()
        {
            def numberOfCalls = DataStore.get ("numberOfGetParametersCalls");
            DataStore.put ("numberOfGetParametersCalls", ++numberOfCalls);
            return [:];
        }
        def init()
        {
            println "init"
            def numberOfCalls = DataStore.get ("numberOfInitCalls");
            DataStore.put ("numberOfInitCalls", ++numberOfCalls);
            synchronized(DataStore.get("initLock"))
            {
                DataStore.get("initLock").wait(5000);
            }
        }

        def cleanUp()
        {
        }

        
        """
        def logLevel = Level.DEBUG;
        ScriptManager.getInstance().initialize(this.class.getClassLoader(), testOutputDir, [:]);
        ListeningAdapterManager.getInstance().initialize();
        CompassForTests.initialize([CmdbScript]);
        CompassForTests.addOperationSupport(CmdbScript, script.CmdbScriptOperations);
        CompassForTests.addOperationSupport(BaseListeningDatasource, datasource.BaseListeningDatasourceOperations);

        def scriptName = "SynchronizationTestScript";
        def scriptFile = new File("${testOutputDir}/${ScriptManager.SCRIPT_DIRECTORY}/${scriptName}.groovy");
        scriptFile.setText (scriptText);

        CompassForTests.addOperationData.setObjectsWillBeReturned([new CmdbScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: scriptName, staticParam: "x:5", logLevel: logLevel.toString())]);
        def scriptObject = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: scriptName);

        def ds = new SynchronizationBaseListeningDatasourceMock(id:1);
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ListeningAdapterManager.getInstance().addAdapter (ds);

        ds.listeningScript = scriptObject;
        def threads = new ArrayList();
        def threadRunLock = new Object();
        def numberOfThreads = 50;
        for(int i=0; i < numberOfThreads; i++)
        {
            Thread t = new ListeningAdapterSynchronizationRunnerThread( objectToWait:threadRunLock, closure:{->ListeningAdapterManager.getInstance().startAdapter(ds);});
            t.start();
            threads.add(t);
        }
        Thread.sleep (800);

        threads.each{
            assertEquals ("All threads should be in start position", 1, it.threadState)
        }

        //Start the race
        synchronized (threadRunLock)
        {
            threadRunLock.notifyAll();
        }

        Thread.sleep (500);

        threads.each{
            assertTrue("All threads should be started",  it.threadState >= 3)
        }
        assertEquals (1, DataStore.get ("numberOfRunCalls"));
        assertEquals (1, DataStore.get ("numberOfGetParametersCalls"));
        assertEquals (1, DataStore.get ("numberOfInitCalls"));

        //All threads should be finished because start adapter is threaded
        Thread.sleep (500);
        def threadNotFinished = threads.findAll {it.threadState == 3}
        assertEquals (0, threadNotFinished.size());
        def threadsNotFinishedWithException = threads.findAll {it.threadState == 5}
        //all threads except one throws exception
        assertEquals (numberOfThreads - 1, threadsNotFinishedWithException.size());
        def threadsSuccessfulltFinished = threads.findAll {it.threadState == 4};
        assertEquals (1, threadsSuccessfulltFinished.size());
        threadsNotFinishedWithException.each{ListeningAdapterSynchronizationRunnerThread thread->
            assertEquals (ListeningAdapterException.adapterAlreadyStartedException(ds.id).getMessage(), thread.thrownException.getMessage())
        }

        def initLock = DataStore.get ("initLock");
        synchronized (initLock)
        {
            initLock.notifyAll();
        }

         Thread.sleep (200);
         threadNotFinished = threads.findAll {it.threadState == 3}
         assertEquals (0, threadNotFinished.size());
         threadNotFinished = threads.findAll {it.threadState == 4}
         assertEquals (1, threadNotFinished.size());
    }

    public void testStopAdapter()
    {
        DataStore.put ("cleanUpLock", new Object());
        DataStore.put ("numberOfCleanupCalls", 0);
        def scriptText = """
        import ${DataStore.class.name};
        def getParameters()
        {
            return [:];
        }
        def init()
        {

        }

        def cleanUp()
        {
            def numberOfCalls = DataStore.get ("numberOfCleanupCalls");
            DataStore.put ("numberOfCleanupCalls", ++numberOfCalls);
            synchronized(DataStore.get("cleanUpLock"))
            {
                DataStore.get("cleanUpLock").wait(5000);
            }
        }
        """
        def logLevel = Level.DEBUG;
        ScriptManager.getInstance().initialize(this.class.getClassLoader(), testOutputDir, [:]);
        ListeningAdapterManager.getInstance().initialize();
        CompassForTests.initialize([CmdbScript]);
        CompassForTests.addOperationSupport(CmdbScript, script.CmdbScriptOperations);
        CompassForTests.addOperationSupport(BaseListeningDatasource, datasource.BaseListeningDatasourceOperations);

        def scriptName = "SynchronizationTestScript";
        def scriptFile = new File("${testOutputDir}/${ScriptManager.SCRIPT_DIRECTORY}/${scriptName}.groovy");
        scriptFile.setText (scriptText);

        CompassForTests.addOperationData.setObjectsWillBeReturned([new CmdbScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: scriptName, staticParam: "x:5", logLevel: logLevel.toString())]);
        def scriptObject = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: scriptName);

        def ds = new SynchronizationBaseListeningDatasourceMock(id:1);
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ListeningAdapterManager.getInstance().addAdapter (ds);
        ds.listeningScript = scriptObject;

        ListeningAdapterManager.getInstance().startAdapter(ds);
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals (ListeningAdapterRunner.STARTED, ListeningAdapterManager.getInstance().getState(ds));    
        }))
        
        def threads = new ArrayList();
        def threadRunLock = new Object();
        def numberOfThreads = 50;
        for(int i=0; i < numberOfThreads; i++)
        {
            Thread t = new ListeningAdapterSynchronizationRunnerThread( objectToWait:threadRunLock, closure:{->ListeningAdapterManager.getInstance().stopAdapter(ds);});
            t.start();
            threads.add(t);
        }
        Thread.sleep (800);

        threads.each{
            assertEquals ("All threads should be in start position", 1, it.threadState)
        }

        //Start the race
        synchronized (threadRunLock)
        {
            threadRunLock.notifyAll();
        }

        Thread.sleep (200);

        threads.each{
            assertTrue("All threads should be started",  it.threadState >= 3)
        }
        assertEquals (1, DataStore.get ("numberOfCleanupCalls"));

        //All thread except one which created adapter first should be finished
        Thread.sleep (200);
        def threadNotFinished = threads.findAll {it.threadState == 3}
        assertEquals (1, threadNotFinished.size());
        def threadsNotFinishedWithException = threads.findAll {it.threadState == 5}
        assertEquals (numberOfThreads - 1, threadsNotFinishedWithException.size());
        threadsNotFinishedWithException.each{ListeningAdapterSynchronizationRunnerThread thread->
            assertTrue(thread.thrownException.getMessage() == ListeningAdapterException.adapterAlreadyStoppedException(ds.id).getMessage() || thread.thrownException.getMessage() == ListeningAdapterException.stoppingStateException(ds.id, "stop").getMessage())
        }

        def initLock = DataStore.get ("cleanUpLock");
        synchronized (initLock)
        {
            initLock.notifyAll();
        }

         Thread.sleep (200);
         threadNotFinished = threads.findAll {it.threadState == 3}
         assertEquals (0, threadNotFinished.size());
         threadNotFinished = threads.findAll {it.threadState == 4}
         assertEquals (1, threadNotFinished.size());
    }
}

class ListeningAdapterSynchronizationRunnerThread extends Thread{
    int threadState = 0;
    def thrownException;
    def objectToWait;
    def closure;
    public void run()
    {
        synchronized (objectToWait)
        {
            threadState = 1;
            objectToWait.wait ();
            threadState = 2;
        }
        threadState = 3;
        try
        {
            closure();
            threadState = 4;
        }catch(Throwable e)
        {
            thrownException = e;
            threadState = 5;
        }

    }
}

class SynchronizationBaseListeningDatasourceMock extends BaseListeningDatasource
{
    BaseListeningAdapterMock listeningAdapter;
    int numberOfListeningAdapterCalls;
    def getListeningAdapter(Map params,Logger adapterLogger){
        synchronized (this)
        {
            numberOfListeningAdapterCalls++
        }
        return listeningAdapter;
    }
}