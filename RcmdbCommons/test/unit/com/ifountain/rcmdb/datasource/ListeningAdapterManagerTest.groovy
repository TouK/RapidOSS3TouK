/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package com.ifountain.rcmdb.datasource

import datasource.*;
import script.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.ifountain.rcmdb.test.util.CompassForTests;
import com.ifountain.rcmdb.scripting.ScriptManager;
import com.ifountain.core.datasource.BaseListeningAdapter;
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.commons.io.FileUtils
import com.ifountain.rcmdb.util.DataStore

/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Oct 23, 2008
 * Time: 4:03:10 PM
 * To change this template use File | Settings | File Templates.
 */
class ListeningAdapterManagerTest extends RapidCmdbTestCase {

    static def scriptMap;
    protected void setUp() {
        scriptMap = [:];
    }
    protected void tearDown() {

    }

    def initialize()
    {
        ScriptManager.getInstance().initialize(this.class.getClassLoader(), System.getProperty("base.dir"), [], [:]);
        ListeningAdapterManager.getInstance().initialize();
        CompassForTests.initialize([CmdbScript]);
        CompassForTests.addOperationSupport(CmdbScript, CmdbScriptOperations);
        CompassForTests.addOperationSupport(BaseListeningDatasource, BaseListeningDatasourceOperations);
    }

    void testStartAdapterThrowsExceptionWhenNoListeningScriptIsDefined() {
        def ds = new BaseListeningDatasource();
        try {

            ListeningAdapterManager.getInstance().startAdapter(ds);
            fail("Should throw exception");
        }
        catch (e)
        {
            println e;
        }

        ds.listeningScript = new CmdbScript(name: "dummysc", type: CmdbScript.ONDEMAND);
        try {

            ListeningAdapterManager.getInstance().startAdapter(ds);
            fail("Should throw exception");
        }
        catch (e)
        {
            println e;
        }
    }

    public void testAddAdapter()
    {
        initialize();
        def ds = new BaseListeningDatasourceMock();
        try
        {
            ListeningAdapterManager.getInstance().getState(ds);
            fail("Sohuld throw exception since adapter is not defined");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterDoesNotExist(ds.name).getMessage(), e.getMessage());
        }

        ListeningAdapterManager.getInstance().addAdapter(ds)

        try
        {
            ListeningAdapterManager.getInstance().addAdapter(ds)
            fail("Should throw exception since adapter already exists");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyExists(ds.name).getMessage(), e.getMessage());
        }

    }

    void testStartAdapter()
    {
        def logLevel = Level.DEBUG;
        initialize();
        CompassForTests.addOperationData.setObjectsWillBeReturned([new CmdbScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript", staticParam: "x:5", logLevel: logLevel.toString())]);
        def script = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript");

        def ds = new BaseListeningDatasourceMock();
        ds.listeningScript = script;
        try
        {
            ListeningAdapterManager.getInstance().startAdapter(ds);
            fail("Sohuld throw exception since adapter is not defined");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterDoesNotExist(ds.name).getMessage(), e.getMessage());
        }

        try
        {
            ListeningAdapterManager.getInstance().getState(ds);
            fail("Sohuld throw exception since adapter is not defined");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterDoesNotExist(ds.name).getMessage(), e.getMessage());
        }

        ListeningAdapterManager.getInstance().addAdapter(ds);
        assertEquals(ListeningAdapterRunner.NOT_STARTED, ListeningAdapterManager.getInstance().getState(ds));

        ListeningAdapterManager.getInstance().startAdapter(ds);
        assertEquals(ListeningAdapterRunner.STARTED, ListeningAdapterManager.getInstance().getState(ds));


        assertEquals(scriptMap.logger.getLevel(), Level.DEBUG)
        assertEquals(scriptMap.logger, CmdbScript.getScriptLogger(script));

        assertEquals(scriptMap.datasource, ds);
        assertEquals(scriptMap.staticParam, script.staticParam);
        assertEquals(scriptMap.staticParamMap.x, CmdbScript.getStaticParamMap(script).x);


        assertEquals(scriptMap.scriptRunStarted, true);
        assertEquals(scriptMap.scriptRunEnded, true);

        assertEquals(scriptMap.scriptInitInvoked, true);

        assertNotNull(ds.listeningAdapter);
        assertNotNull(ds.listeningAdapter.listeningAdapterObserver);
        assertEquals(ds.listeningAdapter.listeningAdapterObserver.logger, CmdbScript.getScriptLogger(script));
        assertEquals(ds.listeningAdapter.subscribeCalled, true);


        assertNotNull(ds.adapterParams)
        assertEquals(ds.adapterParams.returnparam1, "param1")
        assertEquals(ds.adapterLogger, CmdbScript.getScriptLogger(script))

    }

    void testAddAndStartAdapter() {
        def logLevel = Level.DEBUG;
        initialize();
        CompassForTests.addOperationData.setObjectsWillBeReturned([new CmdbScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript", staticParam: "x:5", logLevel: logLevel.toString())]);
        def script = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript");

        def ds = new BaseListeningDatasourceMock();
        ds.listeningScript = script;
        ListeningAdapterManager.getInstance().addAndStartAdapter(ds);
        assertEquals(ListeningAdapterRunner.STARTED, ListeningAdapterManager.getInstance().getState(ds));
        ListeningAdapterManager.getInstance().stopAdapter (ds);
        assertEquals(ListeningAdapterRunner.STOPPED, ListeningAdapterManager.getInstance().getState(ds));
        ListeningAdapterManager.getInstance().addAndStartAdapter (ds);
        assertEquals(ListeningAdapterRunner.STARTED, ListeningAdapterManager.getInstance().getState(ds));

    }


    void testRemoveAdapter()
    {
        initialize();
        def ds = new BaseListeningDatasourceMock();
        try
        {
            ListeningAdapterManager.getInstance().removeAdapter(ds);
            fail("Should throw exception since adapter is not defined");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterDoesNotExist(ds.name).getMessage(), e.getMessage());
        }
        //test does not have adapter
        assertFalse (ListeningAdapterManager.getInstance().hasAdapter(ds.name));

        //add and remove adapter
        ListeningAdapterManager.getInstance().addAdapter(ds)
        assertTrue (ListeningAdapterManager.getInstance().hasAdapter(ds.name));

        ListeningAdapterManager.getInstance().removeAdapter(ds)
        assertTrue ("Since adapter is not started cleanup should not be called", ListeningAdapterManagerTest.scriptMap.isEmpty());
        assertFalse (ListeningAdapterManager.getInstance().hasAdapter(ds.name));

        //add start and remove adapter will call stop

        CompassForTests.addOperationData.setObjectsWillBeReturned([new CmdbScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript", logFileOwn: true)]);
        def script = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript", logFileOwn: true);
        ds.listeningScript = script;

        ListeningAdapterManager.getInstance().addAdapter(ds)
        ListeningAdapterManager.getInstance().startAdapter(ds)
        ListeningAdapterManager.getInstance().removeAdapter(ds)
        assertTrue ("Since adapter is started cleanup should be called", ListeningAdapterManagerTest.scriptMap.cleanUpInvoked);
        assertFalse (ListeningAdapterManager.getInstance().hasAdapter(ds.name));

        
    }


    void testDestroyInstanceRemovesAllAdapters()
    {
        def testOutputDir = new File("../testoutput");
        FileUtils.deleteDirectory (testOutputDir);

        //we will check number of cleanup calls and we will test if any of connection stop method throws exception destroy will
        //continue to stop other adapters
        DataStore.put ("numberOfCleanupCalls", 0)
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
            if(numberOfCalls == 2)
            {
                throw new Exception("An exception occurred");
            }
        }
        """
        def logLevel = Level.DEBUG;
        ScriptManager.getInstance().initialize(this.class.getClassLoader(), testOutputDir.path, [], [:]);
        ListeningAdapterManager.getInstance().initialize();
        CompassForTests.initialize([CmdbScript]);
        CompassForTests.addOperationSupport(CmdbScript, script.CmdbScriptOperations);
        CompassForTests.addOperationSupport(BaseListeningDatasource, datasource.BaseListeningDatasourceOperations);

        def scriptName = "SynchronizationTestScript";
        def scriptFile = new File("${testOutputDir}/${ScriptManager.SCRIPT_DIRECTORY}/${scriptName}.groovy");
        scriptFile.setText (scriptText);

        def ds1 = new BaseListeningDatasourceMock(name:"ds1");
        def ds2 = new BaseListeningDatasourceMock(name:"ds2");
        def ds3 = new BaseListeningDatasourceMock(name:"ds3");
        ListeningAdapterManager.getInstance().addAdapter (ds1);
        ListeningAdapterManager.getInstance().addAdapter (ds2);
        ListeningAdapterManager.getInstance().addAdapter (ds3);
        assertTrue (ListeningAdapterManager.getInstance().hasAdapter(ds1.name));
        assertTrue (ListeningAdapterManager.getInstance().hasAdapter(ds2.name));
        assertTrue (ListeningAdapterManager.getInstance().hasAdapter(ds3.name));

        CompassForTests.addOperationData.setObjectsWillBeReturned([new CmdbScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: scriptName, logFileOwn: true)]);
        def script = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: scriptName, logFileOwn: true);
        ds1.listeningScript = script;
        ds2.listeningScript = script;
        ds3.listeningScript = script;
        //start adapters
        ListeningAdapterManager.getInstance().startAdapter(ds1);
        ListeningAdapterManager.getInstance().startAdapter(ds2);
        ListeningAdapterManager.getInstance().startAdapter(ds3);

        ListeningAdapterManager managerInstanceBeforeDestroy = ListeningAdapterManager.getInstance();
        ListeningAdapterManager.destroyInstance();

        def numberOfCleanupCalls = DataStore.get ("numberOfCleanupCalls");
        println numberOfCleanupCalls
        assertEquals ("Stop methods should be called 3 times", 3, numberOfCleanupCalls);
        assertFalse ("Adapter should be removed", managerInstanceBeforeDestroy.hasAdapter(ds1.name));
        assertFalse ("Adapter should be removed", managerInstanceBeforeDestroy.hasAdapter(ds2.name));
        assertFalse ("Adapter should be removed", managerInstanceBeforeDestroy.hasAdapter(ds3.name));
        


    }


    void testStopAdapter()
    {
        initialize();

        CompassForTests.addOperationData.setObjectsWillBeReturned([new CmdbScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript", logFileOwn: true)]);
        def script = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript", logFileOwn: true);

        def ds = new BaseListeningDatasourceMock();
        ds.listeningScript = script;

        try
        {//
            ListeningAdapterManager.getInstance().stopAdapter(ds);
            fail("Should throw exception since adapter does not exist");
        }
        catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterDoesNotExist(ds.name).getMessage(), e.getMessage());
        }
        ListeningAdapterManager.getInstance().addAdapter(ds);
        // adapter not started stop will throw exception
        try
        {
            ListeningAdapterManager.getInstance().stopAdapter(ds);
            fail("Should throw exception since adapter is not started");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyStoppedException(ds.name).getMessage(), e.getMessage())
        }
        assertTrue(scriptMap.isEmpty());

        // now we will start and stop
        ListeningAdapterManager.getInstance().startAdapter(ds);
        assertNotSame(ds.listeningAdapter.countObservers(), 0);

        ListeningAdapterManager.getInstance().stopAdapter(ds);

        assertEquals(ds.listeningAdapter.unsubscribeCalled, true);
        assertEquals(ds.listeningAdapter.countObservers(), 0);
        assertEquals(scriptMap.cleanUpInvoked, true);

    }
    void testCallingStartAdapterTwiceWillThrowException()
    {
        initialize();

        CompassForTests.addOperationData.setObjectsWillBeReturned([new CmdbScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript", logFileOwn: true)]);
        def script = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript", logFileOwn: true);

        def ds = new BaseListeningDatasourceMock();
        ds.listeningScript = script;
        ListeningAdapterManager.getInstance().addAdapter(ds);

        //when adapter started for the first time, stopAdapter will do nothing since no previous adapter exists
        ListeningAdapterManager.getInstance().startAdapter(ds);
        def oldAdapter = ds.listeningAdapter;
        assertEquals(false, oldAdapter.unsubscribeCalled);
        assertTrue(oldAdapter.countObservers() != 0);
        assertEquals(null, scriptMap.cleanUpInvoked);
        assertEquals(1, ds.numberOfGetAdapterCalls);

        ListeningAdapterManagerTest.scriptMap.clear();
        oldAdapter.unsubscribeCalled = false;
        oldAdapter.subscribeCalled = false;
        //when adapter is started for the second time, it will throw already started exception
        try
        {
            ListeningAdapterManager.getInstance().startAdapter(ds);
            fail("Should throw exception");
        } catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyStartedException(ds.name).getMessage(), e.getMessage());
        }

        assertTrue("No script methods should be called", ListeningAdapterManagerTest.scriptMap.isEmpty());
        assertFalse(oldAdapter.subscribeCalled);
        assertFalse(oldAdapter.unsubscribeCalled);
        assertEquals(1, ds.numberOfGetAdapterCalls);
    }


    void testIsSubscribed()
    {
        initialize();

        CompassForTests.addOperationData.setObjectsWillBeReturned([new CmdbScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript", logFileOwn: true)]);
        def script = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript", logFileOwn: true);

        def ds = new BaseListeningDatasourceMock();
        ds.listeningScript = script;
        ListeningAdapterManager.getInstance().addAdapter(ds);

        assertFalse(ListeningAdapterManager.getInstance().isSubscribed(ds))

        ListeningAdapterManager.getInstance().startAdapter(ds);
        assertTrue(ListeningAdapterManager.getInstance().isSubscribed(ds))

        ListeningAdapterManager.getInstance().stopAdapter(ds);
        assertFalse(ListeningAdapterManager.getInstance().isSubscribed(ds))
    }
    void testCallingDestroyInstanceWithoutInitializeDoesNotGenerateException() {
        //we should disgard previous initializes
        try {
            ListeningAdapterManager.destroyInstance();
        } catch (e) {;}

        def testManager = ListeningAdapterManager.getInstance();
        assertNotNull(testManager);
        try {
            ListeningAdapterManager.destroyInstance();
        }
        catch (e)
        {
            fail("Should Not Throw Exception")
        }
    }

}


class BaseListeningDatasourceMock extends BaseListeningDatasource
{

    BaseListeningAdapterMock listeningAdapter = null;
    Map adapterParams = null;
    Logger adapterLogger = null;
    int numberOfGetAdapterCalls = 0;
    def getListeningAdapter(Map params, Logger adapterLogger) {
        numberOfGetAdapterCalls++;
        adapterParams = params;
        this.adapterLogger = adapterLogger;
        listeningAdapter = new BaseListeningAdapterMock();
        return listeningAdapter;
    }
}

class BaseListeningAdapterMock extends BaseListeningAdapter
{
    ListeningAdapterObserver listeningAdapterObserver = null;
    def subscribeCalled = false;
    def unsubscribeCalled = false;


    protected boolean isConnectionException(Throwable t) {
        return false; //To change body of implemented methods use File | Settings | File Templates.
    }

    public BaseListeningAdapterMock()
    {
        super(null, 0, null);
    }
    public Object _update(Observable o, Object arg)
    {
        return null;
    }
    protected void _subscribe() throws Exception
    {
    }
    public void subscribe() throws Exception
    {
        subscribeCalled = true;
        isSubscribed = true;
    }
    public void unsubscribe() throws Exception
    {
        unsubscribeCalled = true;
        isSubscribed = false;
    }
    protected void _unsubscribe()
    {

    }

    public synchronized void addObserver(ListeningAdapterObserver observer)
    {
        this.listeningAdapterObserver = observer;
        super.addObserver(observer);
    }

}


