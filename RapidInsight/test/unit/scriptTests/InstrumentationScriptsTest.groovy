package scriptTests
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.scripting.ScriptManager
import script.CmdbScript
import script.CmdbScriptOperations
import com.ifountain.rcmdb.test.util.CompassForTests
import org.apache.commons.io.FileUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 5, 2009
* Time: 6:07:23 PM
* To change this template use File | Settings | File Templates.
*/
class InstrumentationScriptsTest extends RapidCmdbWithCompassTestCase{
     def static script_base_directory = "../testoutput/";
     def managerInitialized=false;
     def classes=[:];

     def Statistics;
     def InstrumentationParameters;
     def StatisticsOperations;

     public void setUp() {
        super.setUp();
        ["Statistics","InstrumentationParameters","StatisticsOperations"].each{ className ->
             setProperty(className,gcl.loadClass(className));
        }
        initialize([CmdbScript,Statistics,InstrumentationParameters], []);
        CompassForTests.addOperationSupport (CmdbScript,CmdbScriptOperations);
        CompassForTests.addOperationSupport (Statistics,StatisticsOperations);

        initializeScriptManager();
        def script=CmdbScript.addScript([name:"enableInstrumentation",type: CmdbScript.ONDEMAND])
        assertFalse(script.hasErrors());
        def script2=CmdbScript.addScript([name:"disableInstrumentation",type: CmdbScript.ONDEMAND])
        assertFalse(script2.hasErrors());

        def script3=CmdbScript.addScript([name:"createInstrumentationParameters",type: CmdbScript.ONDEMAND])
        assertFalse(script3.hasErrors());
    }

    public void tearDown() {

        super.tearDown();
    }
    void initializeScriptManager()
    {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight"
        println "base path is :"+new File(base_directory).getCanonicalPath();

        if (new File(script_base_directory).exists())
        {
            FileUtils.deleteDirectory(new File(script_base_directory));
        }
        new File("$script_base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();

        ScriptManager manager = ScriptManager.getInstance();
        manager.initialize(this.class.getClassLoader(), script_base_directory, [], [:]);


        FileUtils.copyFileToDirectory (new File("${base_directory}/scripts/enableInstrumentation.groovy"),new File("$script_base_directory/$ScriptManager.SCRIPT_DIRECTORY"));
        FileUtils.copyFileToDirectory (new File("${base_directory}/scripts/disableInstrumentation.groovy"),new File("$script_base_directory/$ScriptManager.SCRIPT_DIRECTORY"));
        FileUtils.copyFileToDirectory (new File("${base_directory}/scripts/createInstrumentationParameters.groovy"),new File("$script_base_directory/$ScriptManager.SCRIPT_DIRECTORY"));

    }

    public void testEnableDisableInstrumentationScripts()
    {
        System.clearProperty (StatisticsOperations.GLOBAL_ENABLE_KEY);

        assertFalse(Statistics.isEnabledGlobally());

        def result=CmdbScript.runScript("enableInstrumentation",[:]);
        assertTrue(Statistics.isEnabledGlobally());


        result=CmdbScript.runScript("disableInstrumentation",[:]);
        assertFalse(Statistics.isEnabledGlobally());

        result=CmdbScript.runScript("enableInstrumentation",[:]);
        assertTrue(Statistics.isEnabledGlobally());
    }

    public void testCreateInstrumentationParametersScript()
    {
        def paramList=["system.totalMemory","system.usedMemory","user.login","ui.objectDetails","ui.eventDetails"];

        assertEquals(0,InstrumentationParameters.countHits("alias:*"));

        CmdbScript.runScript("createInstrumentationParameters",[:]);

        assertEquals(paramList.size(),InstrumentationParameters.countHits("alias:*"));

        paramList.each{ paramName ->
            assertEquals(1,InstrumentationParameters.countHits("name:${paramName.exactQuery()} AND enabled:true"));
        }
    }

}