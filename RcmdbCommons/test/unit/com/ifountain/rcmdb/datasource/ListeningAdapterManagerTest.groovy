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
import org.apache.commons.io.FileUtils
import com.ifountain.rcmdb.util.DataStore
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction


/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Oct 23, 2008
 * Time: 4:03:10 PM
 * To change this template use File | Settings | File Templates.
 */
class ListeningAdapterManagerTest extends RapidCmdbWithCompassTestCase {
    def static base_directory = "../testoutput/";
    

    public void setUp() {
        super.setUp();
        clearMetaClasses();
        DataStore.put("scriptMap",[:])
    }
    public void tearDown() {
        clearMetaClasses();
        super.tearDown();
        FileUtils.deleteDirectory(new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY"));
    }
    private void clearMetaClasses()
    {
        ListeningAdapterManager.destroyInstance();
        ScriptManager.destroyInstance();
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(ListeningAdapterManager);
        GroovySystem.metaClassRegistry.removeMetaClass(CmdbScript);        
        ExpandoMetaClass.enableGlobally();
    }
    def initializeManagers()
    {
        if (new File(base_directory).exists())
        {
            FileUtils.deleteDirectory(new File(base_directory));
        }
        new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();
        ScriptManager.getInstance().initialize(this.class.getClassLoader(), base_directory, [], [:]);
        ListeningAdapterManager.getInstance().initialize();


        def scriptName="ListeningAdapterManagerTestScript.groovy";
        def scriptContent="""
        import com.ifountain.rcmdb.datasource.*;
        import com.ifountain.rcmdb.util.DataStore;

        scriptMap=DataStore.get("scriptMap");

        scriptMap.datasource=datasource
        scriptMap.staticParam=staticParam
        scriptMap.staticParamMap=staticParamMap
        scriptMap.logger=logger


        println "script started"
        scriptMap.scriptRunStarted=true

        println "script ended"
        scriptMap.scriptRunEnded=true


        def init(){
            scriptMap.scriptInitInvoked=true
        }

        def cleanUp(){
            scriptMap.cleanUpInvoked=true
        }

        def getParameters(){
        return [
               "returnparam1":"param1"
        ]
        }
        """;

        createScript(scriptName,scriptContent);
    }
    def createScript(scriptName,scriptContent)
    {
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write(scriptContent);
    }
    def initializeOperations()
    {
        CompassForTests.addOperationSupport(CmdbScript, CmdbScriptOperations);        
    }
    def initialize()
    {
        initializeManagers();
        initialize([CmdbScript], []);
        initializeOperations();
    }
    def initializeWithBaseListeningDatasourceCompassMock()
    {
        initializeManagers();        
        initialize([CmdbScript,BaseListeningDatasource,BaseListeningDatasourceCompassMock], []);
        initializeOperations();
        CompassForTests.addOperationSupport(BaseListeningDatasourceCompassMock, BaseListeningDatasourceOperations);
    }


    void testStartAdapterWhenNoListeningScriptIsDefined() {
        initializeWithBaseListeningDatasourceCompassMock()
        def ds = new BaseListeningDatasource(id:1);
        ListeningAdapterManager.getInstance().addAdapter(ds);
        ListeningAdapterManager.getInstance().startAdapter(ds);
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(AdapterStateProvider.STOPPED_WITH_EXCEPTION, ListeningAdapterManager.getInstance().getState(ds))
        }))

        ds.listeningScript = new CmdbScript(name: "dummysc", type: CmdbScript.ONDEMAND);
        ListeningAdapterManager.getInstance().startAdapter(ds);
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(AdapterStateProvider.STOPPED_WITH_EXCEPTION, ListeningAdapterManager.getInstance().getState(ds))
        }))
    }
    public void testGetLastStateChangeTime()
    {
        def logLevel = Level.DEBUG;
        initialize();
        def script = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript");

        def ds = new BaseListeningDatasourceMock();
        ds.listeningScript = script;
        
        try
        {
            ListeningAdapterManager.getInstance().getLastStateChangeTime(ds);
            fail("Sohuld throw exception since adapter runner is not defined");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.runnerDoesNotExist(ds.id).getMessage(), e.getMessage());
        }
        def firstTime=new Date();
        Thread.sleep(20);

        ListeningAdapterManager.getInstance().addAdapter(ds);
        assertEquals(AdapterStateProvider.NOT_STARTED, ListeningAdapterManager.getInstance().getState(ds));
        def managerTime=ListeningAdapterManager.getInstance().getLastStateChangeTime(ds);
        def runnerTime=ListeningAdapterManager.getInstance().getRunner(ds.id).getLastStateChangeTime();
        assertEquals(0,managerTime.compareTo(runnerTime));        
        assertEquals(1,managerTime.compareTo(firstTime));

        def secondTime=new Date();
        Thread.sleep(20);

        ListeningAdapterManager.getInstance().startAdapter(ds);
        CommonTestUtils.waitFor(new ClosureWaitAction({
             assertEquals(AdapterStateProvider.STARTED, ListeningAdapterManager.getInstance().getState(ds));
        }))
        def secondManagerTime=ListeningAdapterManager.getInstance().getLastStateChangeTime(ds);
        def secondRunnerTime=ListeningAdapterManager.getInstance().getRunner(ds.id).getLastStateChangeTime();
        assertEquals(0,secondManagerTime.compareTo(secondRunnerTime));
        assertEquals(1,secondManagerTime.compareTo(secondTime));
        assertEquals(1,secondManagerTime.compareTo(managerTime));

    }
    public void testAddAdapter()
    {
        initialize();
        def ds = new BaseListeningDatasourceMock(id:1);
        try
        {
            ListeningAdapterManager.getInstance().getState(ds);
            fail("Sohuld throw exception since adapter runner is not defined");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.runnerDoesNotExist(ds.id).getMessage(), e.getMessage());
        }

        ListeningAdapterManager.getInstance().addAdapter(ds)

        try
        {
            ListeningAdapterManager.getInstance().addAdapter(ds)
            fail("Should throw exception since adapter already exists");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyExists(ds.id).getMessage(), e.getMessage());
        }

    }
    public void testAddAdapterIfNotExists()
    {
        initialize();
        def ds = new BaseListeningDatasourceMock(id:1);
        assertFalse(ListeningAdapterManager.getInstance().hasAdapter(ds.id))

        ListeningAdapterManager.getInstance().addAdapterIfNotExists(ds)
        assertTrue(ListeningAdapterManager.getInstance().hasAdapter(ds.id))
        try
        {
            ListeningAdapterManager.getInstance().addAdapterIfNotExists(ds)

        } catch (e)
        {
            fail("Should not throw exception");

        }
        assertTrue(ListeningAdapterManager.getInstance().hasAdapter(ds.id))
    }
    void testStartAdapter()
    {
        def logLevel = Level.DEBUG;
        initialize();
        def script = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript",logLevel: logLevel.toString());

        def ds = new BaseListeningDatasourceMock(id:1);
        ds.listeningScript = script;
        try
        {
            ListeningAdapterManager.getInstance().startAdapter(ds);
            fail("Sohuld throw exception since adapter is not defined");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.runnerDoesNotExist(ds.id).getMessage(), e.getMessage());
        }

        try
        {
            ListeningAdapterManager.getInstance().getState(ds);
            fail("Sohuld throw exception since adapter is not defined");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.runnerDoesNotExist(ds.id).getMessage(), e.getMessage());
        }

        ListeningAdapterManager.getInstance().addAdapter(ds);
        assertEquals(AdapterStateProvider.NOT_STARTED, ListeningAdapterManager.getInstance().getState(ds));

        ListeningAdapterManager.getInstance().startAdapter(ds);
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(AdapterStateProvider.STARTED, ListeningAdapterManager.getInstance().getState(ds));    
        }))
        def scriptMap=DataStore.get("scriptMap");
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
   
    void testRemoveAdapter()
    {
        initialize();
        def ds = new BaseListeningDatasourceMock(id:1);
        try
        {
            ListeningAdapterManager.getInstance().removeAdapter(ds);
            fail("Should throw exception since adapter is not defined");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.runnerDoesNotExist(ds.id).getMessage(), e.getMessage());
        }
        //test does not have adapter
        assertFalse (ListeningAdapterManager.getInstance().hasAdapter(ds.id));

        //add and remove adapter
        ListeningAdapterManager.getInstance().addAdapter(ds)
        assertTrue (ListeningAdapterManager.getInstance().hasAdapter(ds.id));

        ListeningAdapterManager.getInstance().removeAdapter(ds)
        assertTrue ("Since adapter is not started cleanup should not be called", DataStore.get("scriptMap").isEmpty());
        assertFalse (ListeningAdapterManager.getInstance().hasAdapter(ds.id));

        //add start and remove adapter will call stop

        def script = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript", logFileOwn: true);
        ds.listeningScript = script;

        ListeningAdapterManager.getInstance().addAdapter(ds)
        ListeningAdapterManager.getInstance().startAdapter(ds)
        ListeningAdapterManager.getInstance().removeAdapter(ds)
        assertTrue ("Since adapter is started cleanup should be called", DataStore.get("scriptMap").cleanUpInvoked);
        assertFalse (ListeningAdapterManager.getInstance().hasAdapter(ds.id));
    }


    void testDestroyInstanceRemovesAllAdapters()
    {

        initialize();

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


        def scriptName = "SynchronizationTestScript";
        def scriptFile = "${scriptName}.groovy";
        createScript(scriptFile,scriptText);

        def ds1 = new BaseListeningDatasourceMock(name:"ds1", id:1);
        def ds2 = new BaseListeningDatasourceMock(name:"ds2", id:2);
        def ds3 = new BaseListeningDatasourceMock(name:"ds3", id:3);
        ListeningAdapterManager.getInstance().addAdapter (ds1);
        ListeningAdapterManager.getInstance().addAdapter (ds2);
        ListeningAdapterManager.getInstance().addAdapter (ds3);
        assertTrue (ListeningAdapterManager.getInstance().hasAdapter(ds1.id));
        assertTrue (ListeningAdapterManager.getInstance().hasAdapter(ds2.id));
        assertTrue (ListeningAdapterManager.getInstance().hasAdapter(ds3.id));


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
        assertEquals ("Stop methods should be called 3 times", 3, numberOfCleanupCalls);
        assertFalse ("Adapter should be removed", managerInstanceBeforeDestroy.hasAdapter(ds1.id));
        assertFalse ("Adapter should be removed", managerInstanceBeforeDestroy.hasAdapter(ds2.id));
        assertFalse ("Adapter should be removed", managerInstanceBeforeDestroy.hasAdapter(ds3.id));
        


    }


    void testStopAdapter()
    {
        initialize();

        def script = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript", logFileOwn: true);

        def ds = new BaseListeningDatasourceMock(id:1);
        ds.listeningScript = script;

        try
        {//
            ListeningAdapterManager.getInstance().stopAdapter(ds);
            fail("Should throw exception since adapter does not exist");
        }
        catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.runnerDoesNotExist(ds.id).getMessage(), e.getMessage());
        }
        ListeningAdapterManager.getInstance().addAdapter(ds);
        // adapter not started stop will throw exception
        try
        {
            ListeningAdapterManager.getInstance().stopAdapter(ds);
            fail("Should throw exception since adapter is not started");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyStoppedException(ds.id).getMessage(), e.getMessage())
        }
        assertTrue(DataStore.get("scriptMap").isEmpty());

        // now we will start and stop
        ListeningAdapterManager.getInstance().startAdapter(ds);
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(AdapterStateProvider.STARTED, ListeningAdapterManager.getInstance().getState(ds))
        }))
        assertNotSame(ds.listeningAdapter.countObservers(), 0);

        ListeningAdapterManager.getInstance().stopAdapter(ds);

        assertEquals(ds.listeningAdapter.unsubscribeCalled, true);
        assertEquals(ds.listeningAdapter.countObservers(), 0);
        assertEquals(DataStore.get("scriptMap").cleanUpInvoked, true);

    }
    void testCallingStartAdapterTwiceWillThrowException()
    {
        initialize();
        def scriptMap=DataStore.get("scriptMap");
        def script = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript", logFileOwn: true);

        def ds = new BaseListeningDatasourceMock(id:1);
        ds.listeningScript = script;
        ListeningAdapterManager.getInstance().addAdapter(ds);

        ListeningAdapterManager.getInstance().startAdapter(ds);
        CommonTestUtils.waitFor(new ClosureWaitAction({
           assertEquals(AdapterStateProvider.STARTED, ListeningAdapterManager.getInstance().getState(ds)) 
        }))
        def oldAdapter = ds.listeningAdapter;
        assertEquals(false, oldAdapter.unsubscribeCalled);
        assertTrue(oldAdapter.countObservers() != 0);
        assertEquals(null, DataStore.get("scriptMap").cleanUpInvoked);
        assertEquals(1, ds.numberOfGetAdapterCalls);

        DataStore.get("scriptMap").clear();
        oldAdapter.unsubscribeCalled = false;
        oldAdapter.subscribeCalled = false;
        //when adapter is started for the second time, it will throw already started exception
        try
        {
            ListeningAdapterManager.getInstance().startAdapter(ds);
            fail("Should throw exception");
        } catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyStartedException(ds.id).getMessage(), e.getMessage());
        }

        assertTrue("No script methods should be called", DataStore.get("scriptMap").isEmpty());
        assertFalse(oldAdapter.subscribeCalled);
        assertFalse(oldAdapter.unsubscribeCalled);
        assertEquals(1, ds.numberOfGetAdapterCalls);
    }


    void testIsSubscribed()
    {
        initialize();

        def script = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript", logFileOwn: true);

        def ds = new BaseListeningDatasourceMock();
        ds.listeningScript = script;
        ListeningAdapterManager.getInstance().addAdapter(ds);

        assertFalse(ListeningAdapterManager.getInstance().isSubscribed(ds))

        ListeningAdapterManager.getInstance().startAdapter(ds);
        CommonTestUtils.waitFor(new ClosureWaitAction({
           assertEquals(AdapterStateProvider.STARTED, ListeningAdapterManager.getInstance().getState(ds))
        }))
        assertTrue(ListeningAdapterManager.getInstance().isSubscribed(ds))

        ListeningAdapterManager.getInstance().stopAdapter(ds);
        assertFalse(ListeningAdapterManager.getInstance().isSubscribed(ds))
    }
    void testisFree()
    {
        initialize();

        def script = CmdbScript.addScript(name: "dummysc", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript", logFileOwn: true);

        def ds = new BaseListeningDatasourceMock(id:1);
        ds.name= "testds";
        ds.listeningScript = script;

        assertTrue(ListeningAdapterManager.getInstance().isFree(ds))

        ListeningAdapterManager.getInstance().addAdapter(ds);
        assertTrue(ListeningAdapterManager.getInstance().isFree(ds))

        ListeningAdapterManager.getInstance().startAdapter(ds);
        CommonTestUtils.waitFor(new ClosureWaitAction({
           assertEquals(AdapterStateProvider.STARTED, ListeningAdapterManager.getInstance().getState(ds))
        }))
        assertFalse(ListeningAdapterManager.getInstance().isFree(ds))

        ListeningAdapterManager.getInstance().stopAdapter(ds);
        assertTrue(ListeningAdapterManager.getInstance().isFree(ds))


        ListeningAdapterManager.getInstance().removeAdapter(ds);
        assertTrue(ListeningAdapterManager.getInstance().isFree(ds))

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
   
    void testInitializeListeningDatasources()
    {
        initializeWithBaseListeningDatasourceCompassMock();
       
        def datasources=[:];
        def scripts=[:];
        def inactiveDatasources=[:];
        def inactiveScripts=[:];
        def datasourceId = 1;
        5.times{  counter ->
            def script = CmdbScript.addScript(name: "testscript${counter}", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript");
            assertFalse(script.hasErrors());
            scripts[script.name]=script;
            def ds = BaseListeningDatasourceCompassMock.add(name:"testdds${counter}",listeningScript:script,isSubscribed:true, id:datasourceId);
            assertFalse(ds.hasErrors());
            datasources[ds.name]=ds;
            datasourceId ++;
        }
        3.times{  counter ->
            def script = CmdbScript.addScript(name: "testinactivescript${counter}", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript");
            assertFalse(script.hasErrors());
            inactiveScripts[script.name]=script;
            def ds = BaseListeningDatasourceCompassMock.add(name:"testinactiveds${counter}",listeningScript:script,isSubscribed:false, id:datasourceId);
            assertFalse(ds.hasErrors());
            inactiveDatasources[ds.name]=ds;
             datasourceId ++;
        }

        assertEquals(5,BaseListeningDatasource.countHits("isSubscribed:true"));
        assertEquals(3,BaseListeningDatasource.countHits("isSubscribed:false"));
        assertEquals(8,BaseListeningDatasource.countHits("alias:*"));
        
        ListeningAdapterManager.destroyInstance();
        ListeningAdapterManager.getInstance().initialize();

        datasources.each { dsName , ds ->
            assertFalse(ListeningAdapterManager.getInstance().hasAdapter(ds.id));
            assertTrue(ListeningAdapterManager.getInstance().isFree(ds));
            assertFalse(ListeningAdapterManager.getInstance().isSubscribed(ds));                      
        }
        inactiveDatasources.each { dsName , ds ->
            assertFalse(ListeningAdapterManager.getInstance().hasAdapter(ds.id));
            assertTrue(ListeningAdapterManager.getInstance().isFree(ds));
            assertFalse(ListeningAdapterManager.getInstance().isSubscribed(ds));
        }
        assertNull(ListeningAdapterManager.getInstance().listeningScriptInitializerThread);
        ListeningAdapterManager.getInstance().initializeListeningDatasources();
        def initializerThread=ListeningAdapterManager.getInstance().listeningScriptInitializerThread;
        assertNotNull(initializerThread);
        assertTrue(initializerThread.isAlive());
        assertFalse(initializerThread.isInterrupted());
        Thread.sleep(2000);
        
        datasources.each { dsName , ds ->
            assertTrue(ListeningAdapterManager.getInstance().hasAdapter(ds.id));
            assertFalse(ListeningAdapterManager.getInstance().isFree(ds));
            assertTrue(ListeningAdapterManager.getInstance().isSubscribed(ds));
        }
        inactiveDatasources.each { dsName , ds ->
            assertTrue(ListeningAdapterManager.getInstance().hasAdapter(ds.id));
            assertTrue(ListeningAdapterManager.getInstance().isFree(ds));
            assertFalse(ListeningAdapterManager.getInstance().isSubscribed(ds));                       
        }



    }
    void testInitializeListeningDatasourcesDoesNotThrowExceptionAndProcessesAllDatources_OnError()
    {           
        initializeWithBaseListeningDatasourceCompassMock();
        
        def startListeningCallParams = [:];
        def startListeningExceptionCallParams=[:];
        CmdbScriptOperations.metaClass.static.startListening = {CmdbScript script ->
            startListeningCallParams[script.name]=script;
            if(script.name=="testscript2")
            {
              startListeningExceptionCallParams[script.name]=script;
              throw new Exception(script.name);
            }
        }

        def addAdapterCallParams = [:];
        def addAdapterExceptionCallParams=[:];
        ListeningAdapterManager.metaClass.addAdapter= { BaseListeningDatasource ds ->
            addAdapterCallParams[ds.name]=ds;
            if(ds.name=="testds3")
            {
                addAdapterExceptionCallParams[ds.name]=ds;
                throw new Exception(ds.name);
            }
        }


        def datasources=[:];
        def scripts=[:];
        5.times{  counter ->
            def script = CmdbScript.addScript(name: "testscript${counter}", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript");
            assertFalse(script.hasErrors());
            scripts[script.name]=script;
            def ds = BaseListeningDatasourceCompassMock.add(name:"testds${counter}",listeningScript:script,isSubscribed:true);
            assertFalse(ds.hasErrors());
            datasources[ds.name]=ds;
        }

        try{
            ListeningAdapterManager.getInstance().initializeListeningDatasources();
        }
        catch(e)
        {
            fail("should not throw exception");
        }
        Thread.sleep(2000);

        assertEquals(5,startListeningCallParams.size());
        assertEquals(5,addAdapterCallParams.size());
        assertEquals(1,startListeningExceptionCallParams.size());
        assertEquals(1,addAdapterExceptionCallParams.size());
        assertTrue(addAdapterExceptionCallParams.containsKey("testds3"));
        assertTrue(startListeningExceptionCallParams.containsKey("testscript2"));
        assertFalse(addAdapterExceptionCallParams["testds3"].listeningScript.id==startListeningExceptionCallParams["testscript2"].id);


    }
    void testInitializeListeningDatasourcesDoesNotWaitDatasourceSubscriptions()
    {
        initializeWithBaseListeningDatasourceCompassMock();
        
        def startListeningCallParams = [:];
       
        CmdbScriptOperations.metaClass.static.startListening = {CmdbScript script ->
            startListeningCallParams["startTime"]=new Date();
            Thread.sleep(1000);
            startListeningCallParams["endTime"]=new Date();
            startListeningCallParams[script.name]=script;
        }

        def script = CmdbScript.addScript(name: "testscript", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript");
        assertFalse(script.hasErrors());
        
        def ds = BaseListeningDatasourceCompassMock.add(name:"testds",listeningScript:script,isSubscribed:true);
        assertFalse(ds.hasErrors());

        def startTime=new Date();        
        ListeningAdapterManager.getInstance().initializeListeningDatasources();
        def endTime=new Date();
        

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertTrue(startListeningCallParams.containsKey(script.name));
            assertEquals(1,startListeningCallParams["endTime"].compareTo(startTime));
            assertEquals(1,startListeningCallParams["endTime"].compareTo(startListeningCallParams["startTime"]));
            assertEquals(1,startListeningCallParams["endTime"].compareTo(endTime));
        }))


        
    }
     void testCallingDestroyInstanceDestroysListeningDatasourceInitializerThread() {
        initializeWithBaseListeningDatasourceCompassMock();

        def startListeningCallParams = [:];

        CmdbScriptOperations.metaClass.static.startListening = {CmdbScript script ->
            startListeningCallParams["startTime"]=new Date();
            startListeningCallParams[script.name]=script;
            Thread.sleep(10000);
            startListeningCallParams["endTime"]=new Date();
        }
        
        assertNull(ListeningAdapterManager.getInstance().listeningScriptInitializerThread);

        def script = CmdbScript.addScript(name: "testscript", type: CmdbScript.LISTENING, scriptFile: "ListeningAdapterManagerTestScript");
        assertFalse(script.hasErrors());

        def ds = BaseListeningDatasourceCompassMock.add(name:"testds",listeningScript:script,isSubscribed:true);
        assertFalse(ds.hasErrors());

        ListeningAdapterManager.getInstance().initializeListeningDatasources();
        def initializerThread=ListeningAdapterManager.getInstance().listeningScriptInitializerThread;
        assertNotNull(initializerThread);
        assertTrue(initializerThread.isAlive());

        try{
            ListeningAdapterManager.getInstance().destroyInstance();
        }
        catch(e)
        {
            fail("should throw exception");
        }

        assertFalse(initializerThread.isAlive());
        

    }

}

class BaseListeningDatasourceCompassMock extends BaseListeningDatasource
{
     def getListeningAdapter(Map params, Logger adapterLogger) {        
        def listeningAdapter = new BaseListeningAdapterMock();
        return listeningAdapter;
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
    def unsubscribeException = null;
    def waitLock = null;
    def enableUpdateConversion=true;



    public BaseListeningAdapterMock()
    {
        super(null, 0, null);
    }
    public Object _update(Observable o, Object arg)
    {
        return arg;
    }
    public boolean isConversionEnabledForUpdate()
    {
        return enableUpdateConversion;
    }
    protected void _subscribe() throws Exception
    {
    }
    public void subscribe() throws Exception
    {
        println "BaseListeningAdapterMock: starting subscribe ${new Date()}"

        if(waitLock != null){
            synchronized(waitLock){
                waitLock.wait(1000);    
            }
        }
        subscribeCalled = true;
        isSubscribed = true;

        println "BaseListeningAdapterMock: subscribe done ${new Date()}"
    }
    public void unsubscribe() throws Exception
    {
        println "BaseListeningAdapterMock: starting unsubscribe ${new Date()}"

        if(unsubscribeException != null){
            throw unsubscribeException;
        }
        if(waitLock != null){
            synchronized(waitLock){
                waitLock.wait(1000);
            }
        }
        unsubscribeCalled = true;
        isSubscribed = false;

        println "BaseListeningAdapterMock: unsubscribe done ${new Date()}"
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


