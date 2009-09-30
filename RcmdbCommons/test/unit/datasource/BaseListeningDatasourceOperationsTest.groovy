package datasource
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 11, 2009
 * Time: 9:15:48 AM
 * To change this template use File | Settings | File Templates.
 */
import script.CmdbScript
import script.CmdbScriptOperations

import datasource.BaseListeningDatasource
import datasource.BaseListeningDatasourceOperations

import com.ifountain.rcmdb.datasource.ListeningAdapterManager
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

import org.apache.commons.io.FileUtils
import org.apache.log4j.Level
import com.ifountain.core.datasource.BaseAdapter
import org.apache.log4j.Logger


class BaseListeningDatasourceOperationsTest extends RapidCmdbWithCompassTestCase{
     def static base_directory = "../testoutput/";
     
     public void setUp() {
        super.setUp();
        clearMetaClasses();
        initialize();
    }

    public void tearDown() {
        super.tearDown();
        clearMetaClasses();
    }
    private void clearMetaClasses()
    {
        ListeningAdapterManager.destroyInstance();
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptScheduler);
        GroovySystem.metaClassRegistry.removeMetaClass(ListeningAdapterManager);
        ExpandoMetaClass.enableGlobally();
    }
    void initialize(){
        ScriptManager manager = ScriptManager.getInstance();
        if(new File(base_directory).exists())
        {
            FileUtils.deleteDirectory (new File(base_directory));
        }
        manager.initialize(this.class.getClassLoader(), base_directory, [:]);
        new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();
        
        initialize([CmdbScript,BaseListeningDatasource], []);
        CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);
        CompassForTests.addOperationSupport (BaseListeningDatasource, BaseListeningDatasourceOperations);
     }
     void testDeleteRemovesAdapterAndDoesNotThrowException(){
        def scriptFile="baselistening_test.groovy";
        createScript(scriptFile,"return 1");
        
        CmdbScript script=CmdbScript.add(name:"testscript",type:CmdbScript.LISTENING,scriptFile:scriptFile)
        assertFalse(script.hasErrors())
        
        BaseListeningDatasource ds=BaseListeningDatasource.add(name:"myds",listeningScript:script)
        assertFalse(ds.hasErrors())
        assertEquals(ds.listeningScript.name,script.name);


        assertEquals(script.listeningDatasource.id,ds.id)


        def callParams=[:];
        def exceptionToThrow=null;
        ListeningAdapterManager.metaClass.removeAdapter= { BaseListeningDatasource listeningDatasource ->
            println "removeAdapter in test";
            callParams.listeningDatasource = listeningDatasource;
            if(exceptionToThrow != null)
            {
                callParams.exceptionToThrow=exceptionToThrow;
                throw exceptionToThrow;
            }
        }
        assertEquals(0,callParams.size());        

        exceptionToThrow = new Exception();
        try
        {
            ds.remove();
            assertFalse(ds.hasErrors());
            assertEquals(callParams.listeningDatasource.id,ds.id);
            assertEquals(callParams.listeningDatasource.name,ds.name);
            assertEquals(callParams.exceptionToThrow,exceptionToThrow);
        }
        catch(Exception e)
        {

            fail("Should not throw exception");
        }

     }
     void testInsertCallsAddAdapterIfNotExistsAndDoesNotThrowException(){
        def callParams=[:];
        def exceptionToThrow=null;
        ListeningAdapterManager.metaClass.addAdapterIfNotExists= { BaseListeningDatasource listeningDatasource ->
            println "addAdapterIfNotExists in testInsert";
            callParams.listeningDatasource = listeningDatasource;
            if(exceptionToThrow != null)
            {
                callParams.exceptionToThrow=exceptionToThrow;
                throw exceptionToThrow;
            }
        }
        assertEquals(0,callParams.size());

        
        exceptionToThrow = new Exception();
        try
        {
            def ds=BaseListeningDatasource.add(name:"myds2")
            assertFalse(ds.hasErrors())
            assertEquals(callParams.listeningDatasource.id,ds.id);
            assertEquals(callParams.listeningDatasource.name,ds.name);
            assertEquals(callParams.exceptionToThrow,exceptionToThrow);
        }
        catch(Exception e)
        {               
            fail("Should not throw exception");
        }
     }

     public void testStartListeningChangesTheLoggerOfTheAdaptersToScriptLogger()
     {
         initialize();
         CompassForTests.addOperationSupport (BaseListeningDatasource, BaseListeningDatasourceOperationsLoggerMock);
         
         ListeningAdapterManager.metaClass.startAdapter={ BaseListeningDatasource listeningDatasource ->
            println "in test startAdapter";
         }
         
         def scriptFile="baselistening_test.groovy";
         createScript(scriptFile,"return 1");

         CmdbScript script=CmdbScript.add(name:"testscript",type:CmdbScript.LISTENING,scriptFile:scriptFile)
         assertFalse(script.hasErrors())

         def ds=BaseListeningDatasource.add(name:"testds",listeningScript:script);
         assertFalse(ds.hasErrors())

         def testDs=BaseListeningDatasource.get(name:"testds");
         assertSame(testDs.getLogger(),testDs.adapter.logger);

         def scriptLogger=CmdbScript.getScriptLogger(script);
         assertNotSame(scriptLogger,testDs.adapter.logger);
         
         testDs.startListening();

         assertSame(scriptLogger,testDs.adapter.logger);


     }

    void testStartListeningCallsListeningAdapterManagerAndThrowsException()
    {
        def callParams=[:];

        def exceptionToThrow=null;
        ListeningAdapterManager.metaClass.startAdapter = {BaseListeningDatasource listeningDatasource ->
            callParams.listeningDatasource = listeningDatasource;
            if(exceptionToThrow != null)
            {
                callParams.exceptionToThrow=exceptionToThrow;
                throw exceptionToThrow;
            }
        }

        def ds = BaseListeningDatasource.add(name: "baseds", isSubscribed: false);
        assertFalse(ds.hasErrors())
        assertFalse(ds.isSubscribed)

        assertEquals(0,callParams.size());
        ds.startListening();
        assertEquals(ds.id, callParams.listeningDatasource.id);

        def updatedDs = BaseListeningDatasource.get(name: ds.name)
        assertTrue(updatedDs.isSubscribed)


        callParams=[:];
        exceptionToThrow=new Exception();
        assertEquals(0,callParams.size());
        def ds2 = BaseListeningDatasource.add(name: "baseds2", isSubscribed: false);
        try{
            ds2.startListening();
            fail("Should throw Exception");
        }
        catch(e)
        {
            assertEquals(callParams.exceptionToThrow,exceptionToThrow);

            def updatedDs2 = BaseListeningDatasource.get(name: ds2.name)
            assertFalse(updatedDs2.isSubscribed)
        }
        

    }

    void testStopListeningCallsListeningAdapterManagerAndThrowsException()
    {

        def callParams=[:];
        def exceptionToThrow=null;

        ListeningAdapterManager.metaClass.stopAdapter = {BaseListeningDatasource listeningDatasource ->
            callParams.listeningDatasource = listeningDatasource;
            if(exceptionToThrow != null)
            {
                callParams.exceptionToThrow=exceptionToThrow;
                throw exceptionToThrow;
            }
        }

        assertEquals(0,callParams.size());
        def ds = BaseListeningDatasource.add(name: "baseds", isSubscribed: true);
        assertFalse(ds.hasErrors())
        assertTrue(ds.isSubscribed);

        ds.stopListening();
        assertEquals(ds.id, callParams.listeningDatasource.id)

        def updatedDs = BaseListeningDatasource.get(name: ds.name)
        assertFalse(updatedDs.isSubscribed)

        callParams=[:];
        exceptionToThrow=new Exception();
        assertEquals(0,callParams.size());
        def ds2 = BaseListeningDatasource.add(name: "baseds2", isSubscribed: true);
        try{
            ds2.stopListening();
            fail("Should throw Exception");
        }
        catch(e)
        {
            assertEquals(callParams.exceptionToThrow,exceptionToThrow);

             def updatedDs2 = BaseListeningDatasource.get(name: ds2.name)
             assertTrue(updatedDs2.isSubscribed)

        }

    }

     void testGetListeningAdapter()
     {
        BaseListeningDatasource ds=BaseListeningDatasource.add(name:"myds")
        assertFalse(ds.hasErrors())
        assertNull(ds.getListeningAdapter(null,null));
     }
     void testisFree(){


        def scriptFile="baselistening_test.groovy";
        createScript(scriptFile,"return 1");

        CmdbScript script=CmdbScript.add(name:"testscript",type:CmdbScript.LISTENING,scriptFile:scriptFile)
        assertFalse(script.hasErrors())

        BaseListeningDatasource ds=BaseListeningDatasource.add(name:"myds",listeningScript:script)
        assertFalse(ds.hasErrors())
        assertEquals(ds.listeningScript.name,script.name);


        assertEquals(script.listeningDatasource.id,ds.id)


        def calledDatasource=null;
        def callResult=true;
        ListeningAdapterManager.metaClass.isFree= { BaseListeningDatasource listeningDatasource ->
            calledDatasource = listeningDatasource;
            return callResult;
        }
        assertNull(calledDatasource);
        assertEquals(true,ds.isFree());
        assertEquals(calledDatasource.id,ds.id);
        assertEquals(calledDatasource.name,ds.name);
        callResult=false;
        assertEquals(false,ds.isFree());


     }

    def createScript(scriptName,scriptContent)
    {
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write (scriptContent);
    }
}

class BaseListeningDatasourceOperationsLoggerMock extends BaseListeningDatasourceOperations{
    BaseAdapterMock adapter;
    def onLoad(){
        this.adapter = new BaseAdapterMock("testds", 0, getLogger());
    }

    def getAdapters()
    {
        return [adapter];
    }
}

class BaseAdapterMock extends BaseAdapter
{
    public BaseAdapterMock(){
		super();
	}

	public BaseAdapterMock(String datasourceName, long reconnectInterval, Logger logger) {
        super(datasourceName, reconnectInterval, logger);
    }

    public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) throws Exception
    {
        return null;
    }

}