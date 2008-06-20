package scripting

import com.ifountain.rcmdb.scripting.ScriptManager
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
    static transactional = false;
    def messageService;
    String expectedScriptMessage;
    String scriptName = "script1";
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        expectedScriptMessage = "script successfully executed";
        ScriptManager.getInstance().initialize();
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        ScriptManager.getInstance().destroy();
        deleteSimpleScript(scriptName);
    }
    public void testSimpleScriptAdd()
    {
        createSimpleScript (scriptName);
        def script = CmdbScript.add(name:scriptName)
        assertNotNull(script)
        assertFalse(script.hasErrors())
    }
    public void testValidatesScriptBeforeAddAndIfScriptIsInvalidRetunsError()
    {
        createErrornousScript(scriptName);
        def script = CmdbScript.add(name:scriptName)
        assertNotNull(script)
        assertTrue(script.hasErrors())
        assertEquals("script.compilation.error", script.errors.allErrors[0].code);
    }
    public void testNameisUnique()
    {
        createSimpleScript(scriptName);
        def script = CmdbScript.add(name:scriptName)
        assertFalse(script.hasErrors())

        script = CmdbScript.add(name:scriptName)
        assertTrue(script.hasErrors())
        assertEquals("default.not.unique.message", script.errors.getFieldError("name").code);
    }

    def createSimpleScript(scriptName)
    {
        def scriptFile = new File("${System.getProperty("base.dir")}/$ScriptManager.SCRIPT_DIRECTORY/${scriptName}.groovy");
        scriptFile.write ("""return "$expectedScriptMessage" """);
    }

    def createErrornousScript(scriptName)
    {
        def scriptFile = new File("${System.getProperty("base.dir")}/$ScriptManager.SCRIPT_DIRECTORY/${scriptName}.groovy");
        scriptFile.write ("return \"$expectedScriptMessage");
    }

    def deleteSimpleScript(scriptName)
    {
        new File("${System.getProperty("base.dir")}/$ScriptManager.SCRIPT_DIRECTORY/${scriptName}.groovy").delete();

    }

}