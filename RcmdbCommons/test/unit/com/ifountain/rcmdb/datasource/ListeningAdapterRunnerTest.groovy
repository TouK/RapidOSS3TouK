package com.ifountain.rcmdb.datasource

import com.ifountain.rcmdb.execution.ExecutionContextManager
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.util.DataStore
import com.ifountain.rcmdb.util.RapidCMDBConstants
import datasource.BaseListeningDatasource
import org.apache.commons.io.FileUtils
import org.apache.log4j.Logger
import script.CmdbScript
import script.CmdbScriptOperations

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 3, 2009
* Time: 11:04:35 AM
* To change this template use File | Settings | File Templates.
*/
class ListeningAdapterRunnerTest extends RapidCmdbWithCompassTestCase {
    GroovyClassLoader gcl = null;
    def static base_directory = "../testoutput/";

    public void setUp() {
        super.setUp();
        gcl = new GroovyClassLoader();
        reloadClasses();
        ExpandoMetaClass.enableGlobally();
        DataStore.clear();

        initializeScriptManager();
        initialize([CmdbScript], []);
        CompassForTests.addOperationSupport(CmdbScript, CmdbScriptOperations);

    }
    public void tearDown() {
        super.tearDown();
        reloadClasses();
        DataStore.clear();
    }

    private void reloadClasses()
    {
        ScriptManager.destroyInstance();
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(CmdbScript);
        GroovySystem.metaClassRegistry.removeMetaClass(BaseListeningAdapterMock);
    }
    void initializeScriptManager()
    {
        ScriptManager manager = ScriptManager.getInstance();
        if (new File(base_directory).exists())
        {
            FileUtils.deleteDirectory(new File(base_directory));
        }
        manager.initialize(this.class.getClassLoader(), base_directory, [], [:]);
        new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();

    }
    def createScript(scriptName, scriptContent)
    {
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write(scriptContent);
    }
    private CmdbScript createScriptObject()
    {
        Closure executionContextLoggerStatementClosure = {String datastoreEntryName->
            return """ ${DataStore.class.name}.put("${datastoreEntryName}", ${ExecutionContextManager.class.name}.getInstance().getExecutionContext()) """
        };
        def code = """
        import ${DataStore.name};
        if(DataStore.get("runException") != null)throw DataStore.get("runException");
        ${executionContextLoggerStatementClosure("runContext")}
        def init(){
            ${executionContextLoggerStatementClosure("initContext")}
            if(DataStore.get("initException") != null)throw DataStore.get("initException");
        }
        def cleanUp(){
            ${executionContextLoggerStatementClosure("cleanUpContext")}
            if(DataStore.get("cleanUpException") != null)throw DataStore.get("cleanUpException");
        }
        def getParameters(){
            ${executionContextLoggerStatementClosure("getParametersContext")}
            if(DataStore.get("getParametersException") != null)throw DataStore.get("getParametersException");
            return [:]
        }
        def update(data){
            ${executionContextLoggerStatementClosure("updateContext")}
            def receivedObjects = DataStore.get("receivedObjects");
            if(receivedObjects == null){
                receivedObjects = [];
                DataStore.put("receivedObjects", receivedObjects)
            }
            receivedObjects.add(data);
        }
            """
        return _createScriptObject(code);
    }

    private CmdbScript createScriptForStateMechanism() {
        def code = """
        import ${DataStore.name};
        stateWaitLock = DataStore.get("stateWaitLock")
        def init(){
            println "scriptForStateMechanism: starting init ${new Date()}"
            synchronized(stateWaitLock){
                 stateWaitLock.wait(1000);
            }
            println "scriptForStateMechanism: init done ${new Date()}"
        }
        def cleanUp(){
            println "scriptForStateMechanism: starting cleanUp ${new Date()}"
             synchronized(stateWaitLock){
                 stateWaitLock.wait(1000);
            }
            println "scriptForStateMechanism: cleanUp done ${new Date()}"
        }
        def getParameters(){
            println "scriptForStateMechanism: starting getParameters ${new Date()}"
            synchronized(stateWaitLock){
                 stateWaitLock.wait(1000);
            }
            println "scriptForStateMechanism: getParameters done ${new Date()}"
            return [:]
        }
        def update(data){
        }
            """
        return _createScriptObject(code);
    }

    private CmdbScript _createScriptObject(code) {
        def scriptFile = "script1.groovy";
        createScript(scriptFile, code);

        def listeningScript = CmdbScript.addScript([name: "script1", scriptFile: scriptFile, type: CmdbScript.LISTENING], true);
        assertFalse(listeningScript.hasErrors());

        return listeningScript;
    }

    public void testStartAdapter() {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        def runner = new ListeningAdapterRunner(ds.id);
        ds.listeningScript = createScriptObject();
        ds.listeningAdapter = new BaseListeningAdapterMock();
        runner.start(ds);
        assertEquals(AdapterStateProvider.STARTED, runner.getState());
        def data = ["param1": "value1"]
        ds.listeningAdapter.update(ds.listeningAdapter, data);
        def receivedObjects = DataStore.get("receivedObjects");
        assertNotNull(receivedObjects);
        assertEquals(1, receivedObjects.size());
        def receivedData = receivedObjects.get(0);
        assertEquals("value1", receivedData["param1"]);
        runner.stop();
        runner.cleanUp();
        def logger  = CmdbScript.getScriptLogger(ds.listeningScript);
        assertEquals(logger, DataStore.get ("runContext")[RapidCMDBConstants.LOGGER]);
        assertEquals(logger, DataStore.get ("initContext")[RapidCMDBConstants.LOGGER]);
        assertEquals(logger, DataStore.get ("getParametersContext")[RapidCMDBConstants.LOGGER]);
        assertEquals(logger, DataStore.get ("cleanUpContext")[RapidCMDBConstants.LOGGER]);
    }

    public void testGetLastStateChangeTime()
    {
        def firstTime = new Date();
        Thread.sleep(50);
        def rObj = new ListeningAdapterRunner(1);
        def firstStateTime = rObj.getLastStateChangeTime();
        assertEquals(1, firstStateTime.compareTo(firstTime));

        //test that returned dates are cloned
        assertNotSame(rObj.getLastStateChangeTime(), rObj.getLastStateChangeTime());

        //test that if no state change no time change occurs
        Thread.sleep(50);
        assertEquals(0, firstStateTime.compareTo(rObj.getLastStateChangeTime()));
        assertEquals(0, rObj.getLastStateChangeTime().compareTo(rObj.getLastStateChangeTime()));

        def secondTime = new Date();
        Thread.sleep(50);
        //test changing state changes the time
        rObj.setState(AdapterStateProvider.STARTED);
        def secondStateTime = rObj.getLastStateChangeTime()
        assertEquals("${secondStateTime.getTime()} should be greater than ${secondTime.getTime()}", 1, secondStateTime.compareTo(secondTime));
        assertEquals(1, secondStateTime.compareTo(firstStateTime));

        //test changing state changes the time
        def thirdTime = new Date();
        Thread.sleep(50);
        rObj.setState(AdapterStateProvider.STOPPED_WITH_EXCEPTION);
        def thirdStateTime = rObj.getLastStateChangeTime()
        assertEquals("${thirdStateTime.getTime()} should be greater than ${thirdTime.getTime()}", 1, thirdStateTime.compareTo(thirdTime));
        assertEquals(1, thirdStateTime.compareTo(secondStateTime));
    }


    public void testStartAdapterThrowsExceptionIfDatasourceDoesNotReturnAdapter()
    {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        def runner = new ListeningAdapterRunner(ds.id);
        ds.listeningScript = createScriptObject();
        try
        {
            runner.start(ds);
            fail("Should throw exception since ds does not have listening adapter");
        }
        catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.noAdapterDefined(runner.datasourceId).getMessage(), e.getMessage());
        }
        assertEquals(AdapterStateProvider.STOPPED_WITH_EXCEPTION, runner.getState());
    }

    public void testStartAdapterThrowsExceptionIfScriptIsNotListening()
    {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        def runner = new ListeningAdapterRunner(ds.id);
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ds.listeningScript = createScriptObject();
        ds.listeningScript.type = CmdbScript.PERIODIC;
        try
        {
            runner.start(ds);
            fail("Should throw exception since specified script is not listening script");
        }
        catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.noListeningScript(runner.datasourceId).getMessage(), e.getMessage());
        }
        assertEquals(AdapterStateProvider.STOPPED_WITH_EXCEPTION, runner.getState());
    }


    public void testStartAdapterThrowsExceptionIfScriptIsNotDefined()
    {
        CmdbScript.metaClass.'static'.getScriptObject = {script, params ->
            return null;
        }
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        def runner = new ListeningAdapterRunner(ds.id);
        ds.listeningAdapter = new BaseListeningAdapterMock();
        try
        {
            runner.start(ds);
            fail("Should throw exception since specified script is not listening script");
        }
        catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.noListeningScript(runner.datasourceId).getMessage(), e.getMessage());
        }
        assertEquals(AdapterStateProvider.STOPPED_WITH_EXCEPTION, runner.getState());
    }

    public void testStartAdapterThrowsExceptionIfRunThrowsException()
    {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        def runner = new ListeningAdapterRunner(ds.id);
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ds.listeningScript = createScriptObject();
        DataStore.put("runException", new Throwable("run exception"));

        try
        {
            runner.start(ds);
            fail("Should throw exception since specified script run method throws exception");
        }
        catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.listeningScriptExecutionException(runner.datasourceId, ds.listeningScript.name, "run", DataStore.get("runException")).getMessage(), e.getMessage());
        }
        assertEquals(AdapterStateProvider.STOPPED_WITH_EXCEPTION, runner.getState());
    }

    public void testStartAdapterThrowsExceptionIfGetParametersThrowsException()
    {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        def runner = new ListeningAdapterRunner(ds.id);
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ds.listeningScript = createScriptObject();
        DataStore.put("getParametersException", new Throwable("getParameters exception"));
        try
        {
            runner.start(ds);
            fail("Should throw exception since specified script getParameters method throws exception");
        }
        catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.listeningScriptExecutionException(runner.datasourceId, ds.listeningScript.name, "getParameters", DataStore.get("getParametersException")).getMessage(), e.getMessage());
        }
        assertEquals(AdapterStateProvider.STOPPED_WITH_EXCEPTION, runner.getState());
    }



    public void testStartAdapterThrowsExceptionIfInitThrowsException()
    {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        def runner = new ListeningAdapterRunner(ds.id);
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ds.listeningScript = createScriptObject();
        DataStore.put("initException", new Exception("init exception"));
        try
        {
            runner.start(ds);
            fail("Should throw exception since specified script init method throws exception");
        }
        catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.listeningScriptExecutionException(runner.datasourceId, ds.listeningScript.name, "init", DataStore.get("initException")).getMessage(), e.getMessage());
        }
        assertEquals(AdapterStateProvider.STOPPED_WITH_EXCEPTION, runner.getState());
    }

    public void testStartAdapterThrowsExceptionSubscribeThrowsException()
    {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        def runner = new ListeningAdapterRunner(ds.id);
        def subscribeException = new Exception("Subscribe exception");
        BaseListeningAdapterMock.metaClass.subscribe = {->
            throw subscribeException;
        }
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ds.listeningScript = createScriptObject();

        try
        {
            runner.start(ds);
            fail("Should throw exception since subscribe throws exception");
        }
        catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.couldNotSubscribed(runner.datasourceId, subscribeException).getMessage(), e.getMessage());
        }
        assertEquals(AdapterStateProvider.STOPPED_WITH_EXCEPTION, runner.getState());
    }

    public void testStateMechanism()
    {
        def stateWaitLock = new Object();
        DataStore.put("stateWaitLock", stateWaitLock)
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        def runner = new ListeningAdapterRunner(ds.id);
        ds.listeningScript = createScriptForStateMechanism();
        ds.listeningAdapter = new BaseListeningAdapterMock(waitLock: stateWaitLock);
        Thread.start{
            runner.start(ds);
        }
        Thread.sleep(500);
        assertEquals(AdapterStateProvider.INITIALIZING, runner.getState());
        
        //getParameters should pass
        synchronized(stateWaitLock){
            stateWaitLock.notify();    
        }
        Thread.sleep(300)
        assertEquals(AdapterStateProvider.INITIALIZING, runner.getState());

        //init should pass
        synchronized(stateWaitLock){
            stateWaitLock.notify();
        }
        Thread.sleep(300)
        assertEquals(AdapterStateProvider.INITIALIZED, runner.getState());

        //subscribe should pass
        synchronized(stateWaitLock){
            stateWaitLock.notify();
        }
        Thread.sleep(300)
        assertEquals(AdapterStateProvider.STARTED, runner.getState());

        Thread.start{
            runner.stop();
        }
        Thread.sleep(500);
        assertEquals(AdapterStateProvider.STOPPING, runner.getState());
        
        //unsubscribe should pass but the state is still stopping
        synchronized(stateWaitLock){
            stateWaitLock.notify();
        }
        assertEquals(AdapterStateProvider.STOPPING, runner.getState());

        Thread.start{
            runner.cleanUp();
        }
        Thread.sleep(500);
        assertEquals(AdapterStateProvider.STOPPING, runner.getState());
         //cleanup should pass 
        synchronized(stateWaitLock){
            stateWaitLock.notify();
        }
        Thread.sleep(300);
        assertEquals(AdapterStateProvider.STOPPED, runner.getState());
        
    }
    public void testStopAdapter() {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        def runner = new ListeningAdapterRunner(ds.id);
        ds.listeningScript = createScriptObject();
        ds.listeningAdapter = new BaseListeningAdapterMock();
        runner.start(ds);
        assertEquals(AdapterStateProvider.STARTED, runner.getState());
        runner.stop();

        //adapter is not stopped until cleanUp is called successfully
        assertEquals(AdapterStateProvider.STOPPING, runner.getState());
        assertEquals(true, runner.isStopCalled());
        assertEquals(true, ds.listeningAdapter.unsubscribeCalled);
        runner.cleanUp();
        assertEquals(AdapterStateProvider.STOPPED, runner.getState());
    }

    public void testAdapterWillBeAssignedToNullAfterStopThrowsException()
    {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ds.listeningAdapter.unsubscribeException = new Exception("");
        ds.listeningScript = createScriptObject();
        def runner = new ListeningAdapterRunner(ds.id);
        runner.start(ds);

        assertNotNull(runner.adapter);

        runner.stop();
        assertNull("Adapter should be set to null after runner stopped", runner.adapter);
    }
    public void testAdapterWillBeAssignedToNullAfterStop()
    {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ds.listeningScript = createScriptObject();
        def runner = new ListeningAdapterRunner(ds.id);
        runner.start(ds);

        assertNotNull(runner.adapter);

        runner.stop();
        assertNull(runner.adapter);
    }

    public void testAdapterDoesNotSubscribeIfStopIsCalledBefore() {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ds.listeningScript = createScriptObject();
        def runner = new ListeningAdapterRunner(ds.id);
        runner.setStopCalled(true);
        runner.start(ds);
        assertEquals(AdapterStateProvider.INITIALIZED, runner.getState());
        assertFalse(runner.isSubscribed());
    }
    public void testCleanUpStoresStoppedWithExceptionStateEvenIfItIsSuccessfullyExecuted(){
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ds.listeningAdapter.unsubscribeException = new Exception("");
        ds.listeningScript = createScriptObject();
        def runner = new ListeningAdapterRunner(ds.id);
        runner.start(ds);
        assertEquals(AdapterStateProvider.STARTED, runner.getState());
        runner.stop();
        assertEquals(AdapterStateProvider.STOPPED_WITH_EXCEPTION, runner.getState());
        runner.cleanUp();
        assertEquals(AdapterStateProvider.STOPPED_WITH_EXCEPTION, runner.getState());
    }

}



class RunnerBaseListeningDatasourceMock extends BaseListeningDatasource
{

    BaseListeningAdapterMock listeningAdapter = null;
    Map adapterParams = null;
    Logger adapterLogger = null;
    int numberOfGetAdapterCalls = 0;
    def getListeningAdapter(Map params, Logger adapterLogger) {
        numberOfGetAdapterCalls++;
        return listeningAdapter;
    }
}