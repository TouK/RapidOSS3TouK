package scripting

import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import script.CmdbScript
import script.ScriptController

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Apr 18, 2008
* Time: 1:29:49 PM
* To change this template use File | Settings | File Templates.
*/
class ScriptControllerIntegrationTests extends RapidCmdbIntegrationTestCase{
    static transactional = false;
    String expectedScriptMessage;
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        expectedScriptMessage = "script successfully executed";
        CmdbScript.list().each{
            it.remove();
        }
        ScriptManager.getInstance().initialize();
    }

    public void tearDown() {
        ScriptManager.getInstance().destroy();
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
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

    public void testSave()
    {
        String scriptName = "script1"
        createSimpleScript(scriptName);
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.save();

            def script = CmdbScript.findByName(scriptName);
            assertNotNull (script);
            assertEquals("/script/show/" + script.id, scriptController.response.redirectedUrl);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }
    public void testSaveReturndErrorIfSyntaxExceptionExistInScript()
    {
        String scriptName = "script1"
        createErrornousScript(scriptName);
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.save();

            def script = CmdbScript.findByName(scriptName);
            assertNull (script);
            assertEquals(scriptName, scriptController.modelAndView.model.cmdbScript.name);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }

    public void testReload()
    {
        String scriptName = "script1"
        createSimpleScript(scriptName);
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.save();

            def script = CmdbScript.findByName(scriptName);

            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = script.name;
            scriptController.run();
            assertEquals(expectedScriptMessage, scriptController.response.contentAsString);

            expectedScriptMessage = "changed message"
            createSimpleScript(scriptName);
            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = script.name;
            scriptController.reload();
            assertEquals("/script/show/" + script.id, scriptController.response.redirectedUrl);

            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = script.name;
            scriptController.run();
            assertEquals(expectedScriptMessage, scriptController.response.contentAsString);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }

    public void testReloadReturnsErrorIfScriptDoesnotExist()
    {
        String scriptName = "script1"
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["id"] = scriptName;
            scriptController.reload();
            assertEquals(ScriptController.SCRIPT_DOESNOT_EXIST, scriptController.flash.message);
            assertEquals("/script/list", scriptController.response.redirectedUrl);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }
    public void testReloadReturnsErrorIfScriptContainsSysntaxErrors()
    {
        String scriptName = "script1"
        createSimpleScript(scriptName);
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.save();

            def script = CmdbScript.findByName(scriptName);

            createErrornousScript(scriptName);
            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = script.name;
            scriptController.reload();
            assertTrue(scriptController.flash.message.indexOf("Exception occurred") >= 0);
            assertEquals("/script/show/" + script.id, scriptController.response.redirectedUrl);

            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = script.name;
            scriptController.run();
            assertEquals(expectedScriptMessage, scriptController.response.contentAsString);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }

    public void testRunReturnsErrorIfScriptDoesnotExist()
    {
        String scriptName = "script1"
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["id"] = scriptName;
            scriptController.run();
            assertEquals(ScriptController.SCRIPT_DOESNOT_EXIST, scriptController.flash.message);
            assertEquals("/script/list", scriptController.response.redirectedUrl);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }

    public void testRunWithReturningNothing()
    {
        String scriptName = "script1"
        def scriptFile = new File("${System.getProperty("base.dir")}/$ScriptManager.SCRIPT_DIRECTORY/${scriptName}.groovy");
        scriptFile.write ("return null");
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.save();

            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = scriptName;
            scriptController.run();
            assertEquals("", scriptController.response.contentAsString);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }

    public void testRunReturnsErrorIfScriptContainsErrors()
    {
        def exceptionMessage = "Error occurred";
        String scriptName = "script1"
        def scriptFile = new File("${System.getProperty("base.dir")}/$ScriptManager.SCRIPT_DIRECTORY/${scriptName}.groovy");
        scriptFile.write ("throw new Exception(\"$exceptionMessage\")");
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.save();

            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = scriptName;
            scriptController.run();
            assertTrue(scriptController.response.contentAsString, scriptController.response.contentAsString.indexOf(exceptionMessage) >= 0);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }
}