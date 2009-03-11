package com.ifountain.rcmdb.datasource

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import script.CmdbScript
import script.CmdbScriptOperations
import org.apache.log4j.Logger
import datasource.BaseListeningDatasource
import com.ifountain.rcmdb.util.DataStore
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.scripting.ScriptManager
import org.apache.commons.io.FileUtils

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
        CompassForTests.addOperationSupport(CmdbScript,CmdbScriptOperations);        
        
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
        def code = """
        import ${DataStore.name};
        if(DataStore.get("runException") != null)throw DataStore.get("runException");
        def init(){
            if(DataStore.get("initException") != null)throw DataStore.get("initException");
        }
        def cleanUp(){
            if(DataStore.get("cleanUpException") != null)throw DataStore.get("cleanUpException");
        }
        def getParameters(){
            if(DataStore.get("getParametersException") != null)throw DataStore.get("getParametersException");
            return [:]
        }
            """

        def scriptFile="script1.groovy";
        createScript(scriptFile,code);
        
        def listeningScript=CmdbScript.addScript([name:"script1",scriptFile:scriptFile,type:CmdbScript.LISTENING],true);        
        assertFalse(listeningScript.hasErrors());

        return listeningScript;
    }


    public void testStartAdapterThrowsExceptionIfDatasourceDoesNotReturnAdapter()
    {
        
        
        def runner = new ListeningAdapterRunner("adapter1");
        def ds = new RunnerBaseListeningDatasourceMock();
        ds.listeningScript = createScriptObject();
        try
        {
            runner.start(ds);
            fail("Should throw exception since ds does not have listening adapter");
        }
        catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.noAdapterDefined(ds.name).getMessage(), e.getMessage());
        }
        assertEquals(ListeningAdapterRunner.STOPPED_WITH_EXCEPTION, runner.getState());
    }

    public void testStartAdapterThrowsExceptionIfScriptIsNotListening()
    {
        def runner = new ListeningAdapterRunner("adapter1");
        def ds = new RunnerBaseListeningDatasourceMock();
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
            assertEquals(ListeningAdapterException.noListeningScript(ds.name).getMessage(), e.getMessage());
        }
        assertEquals(ListeningAdapterRunner.STOPPED_WITH_EXCEPTION, runner.getState());
    }


    public void testStartAdapterThrowsExceptionIfScriptIsNotDefined()
    {
        CmdbScript.metaClass.'static'.getScriptObject = {script, params ->
            return null;
        }
        def runner = new ListeningAdapterRunner("adapter1");
        def ds = new RunnerBaseListeningDatasourceMock();
        ds.listeningAdapter = new BaseListeningAdapterMock();
        try
        {
            runner.start(ds);
            fail("Should throw exception since specified script is not listening script");
        }
        catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.noListeningScript(ds.name).getMessage(), e.getMessage());
        }
        assertEquals(ListeningAdapterRunner.STOPPED_WITH_EXCEPTION, runner.getState());
    }

    public void testStartAdapterThrowsExceptionIfRunThrowsException()
    {
        def runner = new ListeningAdapterRunner("adapter1");
        def ds = new RunnerBaseListeningDatasourceMock();
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ds.listeningScript = createScriptObject();
        DataStore.put ("runException", new Exception("run exception"));
        try
        {
            runner.start(ds);
            fail("Should throw exception since specified script run method throws exception");
        }
        catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.listeningScriptExecutionException(runner.adapterName, ds.listeningScript.name, "run", DataStore.get("runException")).getMessage(), e.getMessage());
        }
        assertEquals(ListeningAdapterRunner.STOPPED_WITH_EXCEPTION, runner.getState());
    }

    public void testStartAdapterThrowsExceptionIfGetParametersThrowsException()
    {
        def runner = new ListeningAdapterRunner("adapter1");
        def ds = new RunnerBaseListeningDatasourceMock();
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ds.listeningScript = createScriptObject();
        DataStore.put ("getParametersException", new Exception("getParameters exception"));
        try
        {
            runner.start(ds);
            fail("Should throw exception since specified script getParameters method throws exception");
        }
        catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.listeningScriptExecutionException(runner.adapterName, ds.listeningScript.name, "getParameters", DataStore.get("getParametersException")).getMessage(), e.getMessage());
        }
        assertEquals(ListeningAdapterRunner.STOPPED_WITH_EXCEPTION, runner.getState());
    }

    

    public void testStartAdapterThrowsExceptionIfInitThrowsException()
    {
        def runner = new ListeningAdapterRunner("adapter1");
        def ds = new RunnerBaseListeningDatasourceMock();
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ds.listeningScript = createScriptObject();
        DataStore.put ("initException", new Exception("init exception"));
        try
        {
            runner.start(ds);
            fail("Should throw exception since specified script init method throws exception");
        }
        catch (com.ifountain.rcmdb.datasource.ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.listeningScriptExecutionException(runner.adapterName, ds.listeningScript.name, "init", DataStore.get("initException")).getMessage(), e.getMessage());
        }
        assertEquals(ListeningAdapterRunner.STOPPED_WITH_EXCEPTION, runner.getState());
    }

    public void testStartAdapterThrowsExceptionSubscribeThrowsException()
    {
        def runner = new ListeningAdapterRunner("adapter1");
        def ds = new RunnerBaseListeningDatasourceMock();
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
            assertEquals(ListeningAdapterException.couldNotSubscribed(runner.adapterName, subscribeException).getMessage(), e.getMessage());
        }
        assertEquals(ListeningAdapterRunner.STOPPED_WITH_EXCEPTION, runner.getState());
    }

    public void testStartThrowsExceptionIfAdapterIsNotInOneOfStopStates()
    {
        def runner = new ListeningAdapterRunner("adapter1");
        def ds = new RunnerBaseListeningDatasourceMock();
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ds.listeningScript = createScriptObject();

        runner.setState (ListeningAdapterRunner.INITIALIZING);
        try
        {
            runner.start (ds);
            fail("Should throw exception since it is already started");
        }catch(ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyStartedException(runner.adapterName).getMessage(), e.getMessage());
        }

        runner.setState (ListeningAdapterRunner.INITIALIZED);
        try
        {
            runner.start (ds);
            fail("Should throw exception since it is already started");
        }catch(ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyStartedException(runner.adapterName).getMessage(), e.getMessage());
        }

        runner.setState (ListeningAdapterRunner.STARTED);
        try
        {
            runner.start (ds);
            fail("Should throw exception since it is already started");
        }catch(ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyStartedException(runner.adapterName).getMessage(), e.getMessage());
        }

        runner.setState (ListeningAdapterRunner.STOPPING);
        try
        {
            runner.start (ds);
            fail("Should throw exception ");
        }catch(ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.stoppingStateException(runner.adapterName, "start").getMessage(), e.getMessage());
        }

        runner.setState (ListeningAdapterRunner.NOT_STARTED);
        try
        {
            runner.start (ds);
        }
        catch(e) {
            fail("Should not throw exception");
        }
        runner.setState (ListeningAdapterRunner.STOPPED_WITH_EXCEPTION);
        try
        {
            runner.start (ds);
        }
        catch(e) {
            fail("Should not throw exception");
        }
        runner.setState (ListeningAdapterRunner.STOPPED);
        try
        {
            runner.start (ds);
        }
        catch(e) {
            fail("Should not throw exception");
        }

    }


    public void testStopThrowsExceptionIfAdapterIsNotInOneOfStartStates()
    {
        def runner = new ListeningAdapterRunner("adapter1");
        def ds = new RunnerBaseListeningDatasourceMock();
        ds.listeningAdapter = new BaseListeningAdapterMock();
        ds.listeningScript = createScriptObject();

        runner.setState (ListeningAdapterRunner.NOT_STARTED);
        try
        {
            runner.stop ();
            fail("Should throw exception since it is already stpped");
        }catch(ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyStoppedException(runner.adapterName).getMessage(), e.getMessage());
        }

        runner.setState (ListeningAdapterRunner.STOPPED);
        try
        {
            runner.stop();
            fail("Should throw exception since it is already stpped");
        }catch(ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyStoppedException(runner.adapterName).getMessage(), e.getMessage());
        }
        runner.setState (ListeningAdapterRunner.STOPPING);
        try
        {
            runner.stop();
            fail("Should throw exception");
        }catch(ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.stoppingStateException(runner.adapterName, "stop").getMessage(), e.getMessage());
        }

        runner.setState (ListeningAdapterRunner.STOPPED_WITH_EXCEPTION);
        try
        {
            runner.stop();
            fail("Should throw exception since it is already stpped");
        }catch(ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyStoppedException(runner.adapterName).getMessage(), e.getMessage());
        }
        runner.start (ds);
        //Following states are valid stop states
        runner.setState (ListeningAdapterRunner.INITIALIZING);
        try
        {
            runner.stop();
        }
        catch(e) {
            e.printStackTrace();
            fail("Should not throw exception");
        }
        runner.start (ds);
        runner.setState (ListeningAdapterRunner.INITIALIZED);
        try
        {
            runner.stop();
        }
        catch(e) {
            fail("Should not throw exception");
        }
        runner.start (ds);
        runner.setState (ListeningAdapterRunner.STARTED);
        try
        {
            runner.stop();
        }
        catch(e) {
            fail("Should not throw exception");
        }

    }

    public void testStateMechanism()
    {
        fail("Should be implemented");
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