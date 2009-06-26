package scriptTests
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.scripting.ScriptManager
import script.CmdbScript
import script.CmdbScriptOperations
import com.ifountain.rcmdb.test.util.CompassForTests
import org.apache.commons.io.FileUtils
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 5, 2009
* Time: 6:07:23 PM
* To change this template use File | Settings | File Templates.
*/
class InstrumentationScriptsTest extends RapidCmdbWithCompassTestCase{
     def base_directory ;
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
    }

    public void tearDown() {

        super.tearDown();
    }
    void initializeScriptManager()
    {
        base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/scripts"
        println "base path is :"+new File(base_directory).getCanonicalPath();


        ScriptManagerForTest.initialize (gcl,base_directory);

    }

    public void testEnableDisableInstrumentationScripts()
    {
        ScriptManagerForTest.addScript('enableInstrumentation');
        ScriptManagerForTest.addScript('disableInstrumentation');

        System.clearProperty (StatisticsOperations.GLOBAL_ENABLE_KEY);

        assertFalse(Statistics.isEnabledGlobally());

        def result=ScriptManagerForTest.runScript("enableInstrumentation",[:]);
        assertTrue(Statistics.isEnabledGlobally());


        result=ScriptManagerForTest.runScript("disableInstrumentation",[:]);
        assertFalse(Statistics.isEnabledGlobally());

        result=ScriptManagerForTest.runScript("enableInstrumentation",[:]);
        assertTrue(Statistics.isEnabledGlobally());
    }

    public void testCreateInstrumentationParametersScript()
    {
        ScriptManagerForTest.addScript('createInstrumentationParameters');

        def paramList=["system.totalMemory","system.usedMemory","user.login","ui.objectDetails","ui.eventDetails"];

        assertEquals(0,InstrumentationParameters.countHits("alias:*"));

        ScriptManagerForTest.runScript("createInstrumentationParameters",[:]);

        assertEquals(paramList.size(),InstrumentationParameters.countHits("alias:*"));

        paramList.each{ paramName ->
            assertEquals(1,InstrumentationParameters.countHits("name:${paramName.exactQuery()} AND enabled:true"));
        }
    }

}