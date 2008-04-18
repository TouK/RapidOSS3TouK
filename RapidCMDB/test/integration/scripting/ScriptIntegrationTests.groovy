package scripting

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import script.CmdbScript

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Apr 18, 2008
* Time: 5:13:37 PM
* To change this template use File | Settings | File Templates.
*/
class ScriptIntegrationTests extends RapidCmdbIntegrationTestCase{
    String expectedScriptMessage;
    String scriptName = "script1";
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        expectedScriptMessage = "script successfully executed";
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        deleteSimpleScript(scriptName);
    }
    public void testValidatesScriptBeforeAdd()
    {
        createSimpleScript (scriptName);
        def script = new CmdbScript(name:scriptName)
        assertNotNull(script.save())
    }
    public void testValidatesScriptBeforeAddAndIfScriptIsInvalidRetunsError()
    {
        createErrornousScript(scriptName);
        def script = new CmdbScript(name:scriptName)
        assertNull(script.save())
    }
    public void testNameisUnique()
    {
        createSimpleScript(scriptName);
        def script = new CmdbScript(name:scriptName)
        assertNotNull(script.save())

        script = new CmdbScript(name:scriptName)
        assertNull(script.save())
    }

    def createSimpleScript(scriptName)
    {
        def scriptFile = new File("${System.getProperty("base.dir")}/$ScriptingService.SCRIPT_DIRECTORY/${scriptName}.groovy");
        scriptFile.write ("""return "$expectedScriptMessage" """);
    }

    def createErrornousScript(scriptName)
    {
        def scriptFile = new File("${System.getProperty("base.dir")}/$ScriptingService.SCRIPT_DIRECTORY/${scriptName}.groovy");
        scriptFile.write ("return \"$expectedScriptMessage");
    }

    def deleteSimpleScript(scriptName)
    {
        new File("${System.getProperty("base.dir")}/$ScriptingService.SCRIPT_DIRECTORY/${scriptName}.groovy").delete();

    }

}