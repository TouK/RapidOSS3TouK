package scripting

import com.ifountain.rcmdb.scripting.QuartzScriptJob
import com.ifountain.rcmdb.scripting.ScriptManager;

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import script.CmdbScript
import script.CmdbScriptOperations
import com.ifountain.rcmdb.test.util.CompassForTests;
import org.apache.commons.io.FileUtils
import com.ifountain.rcmdb.test.util.LoggerForTest

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 10, 2009
* Time: 9:34:50 AM
* To change this template use File | Settings | File Templates.
*/
class QuartzScriptJobTests extends RapidCmdbWithCompassTestCase{
    def static base_directory = "../testoutput/";
    public void setUp() {
        super.setUp();
        clearMetaClasses();
        initialize([CmdbScript], []);
        initializeScriptManager();
        CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);
    }

    public void tearDown() {
        super.tearDown();
    }
    
     private void clearMetaClasses()
     {
        ScriptManager.destroyInstance();
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptManager)
        GroovySystem.metaClassRegistry.removeMetaClass(CmdbScript)        
        ExpandoMetaClass.enableGlobally();
     }
     
    void initializeScriptManager()
     {            
        ScriptManager manager = ScriptManager.getInstance();
        if(new File(base_directory).exists())
        {
            FileUtils.deleteDirectory (new File(base_directory));
        }
        manager.initialize(this.class.getClassLoader(), base_directory, [], [:]);
        
     }
     
    public void testRunScript()
    {

        def callParams=[:]
        CmdbScript.metaClass.static.runScript={CmdbScript script, Map params ->
            callParams.script=script
            callParams.params=params
        }
        
        QuartzScriptJob job=new QuartzScriptJob();        
      
        def content="""  println "done";  """;
        def scriptName="myTestScript";
        def scriptFile=scriptName+".groovy"
        createScript(scriptFile,content);
        CmdbScript.addScript(name:scriptName,scriptFile:scriptFile);

        assertEquals(callParams.size(),0);
        job.runScript(scriptName);
        assertEquals(callParams.script.name,scriptName);
        assertEquals(callParams.params.size(),0);
    }
    public void testRunScriptDoesNotGenerateExceptionWhenScriptIsNotFound()
    {
         assertEquals(CmdbScript.count(),0);
         QuartzScriptJob job=new QuartzScriptJob();
         try{
            job.runScript("noscript");
         }
         catch (e){
            fail("should not throw exception");   
         }

    }
    public void testRunScriptDoesNotGenerateExceptionWhenScriptErrorOccured()
    {
        def callParams=[:]
        CmdbScript.metaClass.static.runScript={CmdbScript script, Map params ->
            callParams.script=script
            throw new Exception("Test Exception");
        }
        def testLogger=new LoggerForTest();
        def scriptInGetScriptLogger;
        CmdbScript.metaClass.static.getScriptLogger={CmdbScript script ->
            scriptInGetScriptLogger=script;
            return testLogger;
        }


        QuartzScriptJob job=new QuartzScriptJob();

        def content="""  println "done";  """;
        def scriptName="myTestScript";
        def scriptFile=scriptName+".groovy"
        createScript(scriptFile,content);
        def addedScript=CmdbScript.addScript(name:scriptName,scriptFile:scriptFile);
        assertFalse(addedScript.hasErrors());

        assertEquals(callParams.size(),0);
        try{
            job.runScript(scriptName);
        }
        catch (e){
            fail("should not throw exception");
        }
        assertEquals(callParams.script.name,scriptName);

        assertEquals(addedScript.id,scriptInGetScriptLogger.id);

        assertEquals(1,testLogger.logHistory.WARN.size());
        assertTrue(testLogger.logHistory.WARN[0].message.indexOf("Exception in periodic script ${scriptName}")>=0);
        
        
    }

    def createScript(scriptName,scriptContent)
    {
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write (scriptContent);
    }
}
