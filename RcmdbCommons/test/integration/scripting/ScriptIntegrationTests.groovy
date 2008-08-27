package scripting

import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import script.CmdbScript
import org.codehaus.groovy.grails.commons.ApplicationHolder

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
        ScriptManager.getInstance().initialize(ApplicationHolder.application.classLoader, System.getProperty("base.dir"), []);
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        ScriptManager.getInstance().destroy();
        deleteSimpleScript(scriptName);
    }

    public void testSimpleScriptAdd()
    {
        def scriptFile = scriptName + "File"
        createSimpleScript (scriptFile);
        def script = CmdbScript.add(name:scriptName, scriptFile:scriptFile)
        assertNotNull(script)
        assertFalse(script.hasErrors())
    }
    public void testValidatesScriptBeforeAddAndIfScriptIsInvalidRetunsError()
    {
        def scriptFile = scriptName + "File"
        createErrornousScript(scriptFile);
        def script = CmdbScript.add(name:scriptName, scriptFile:scriptFile)
        assertNotNull(script)
        assertTrue(script.hasErrors())
        assertEquals("script.compilation.error", script.errors.allErrors[0].code);
    }
    public void testNameisUnique()
    {
        def scriptFile = scriptName + "File"
        createSimpleScript(scriptFile);
        def script = CmdbScript.add(name:scriptName, scriptFile:scriptFile)
        assertFalse(script.hasErrors())

        def scriptFile2 = scriptName + "File2"
        createSimpleScript(scriptFile2);
        script = new CmdbScript(name:scriptName, scriptFile:scriptFile2)
        script.validate();
        assertTrue(script.hasErrors())
        println script.errors;
        assertEquals("default.not.unique.message", script.errors.allErrors[0].code);
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