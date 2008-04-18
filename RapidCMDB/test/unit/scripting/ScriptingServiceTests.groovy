package scripting
import org.apache.commons.io.*
import com.ifountain.exceptions.scripting.ScriptingException
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication;
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 18, 2008
 * Time: 9:26:20 AM
 * To change this template use File | Settings | File Templates.
 */
class ScriptingServiceTests extends GroovyTestCase{
    def expectedScriptMessage = "script executed successfully";
    def static base_directory = "../testoutput/";
    ScriptingService service;
    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        service = new ScriptingService(classLoader:this.class.classLoader, scripts:[:], baseDirectory:base_directory);
        new File("$base_directory/$ScriptingService.SCRIPT_DIRECTORY").mkdirs();
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        FileUtils.deleteDirectory(new File("$base_directory/$ScriptingService.SCRIPT_DIRECTORY"));
    }

    public void testAddScript()
    {
        def scriptName = "script1.groovy";
        createSimpleScript (scriptName)
        service.addScript(scriptName);
        assertNotNull (service.getScript(scriptName));
        assertEquals("script1", service.getScript(scriptName).name);

        service.scripts.clear();
        service.addScript("script1");
        assertNotNull (service.getScript(scriptName));
        assertEquals("script1", service.getScript(scriptName).name);
    }

    public void testAddScriptThrowsExceptionIfScriptDoesnotExist()
    {
        def scriptName = "script1.groovy";
        try {
            service.addScript(scriptName)
            fail("Should throw exception. Script doesnot exist");
        }
        catch (ScriptingException e) {
        }
    }

    public void testAddScriptThrowsExceptionIfScriptContainsSyntaxErrors()
    {
        def scriptName = "script1.groovy";
        createErrornousScript(scriptName)
        try {
            service.addScript(scriptName)
            fail("Should throw exception. Script doesnot exist");
        }
        catch (ScriptingException e) {
        }
    }


    public void testCheckScript()
    {
        def scriptName = "script1.groovy";
        createErrornousScript(scriptName)
        try {
            service.checkScript(scriptName)
            fail("Should throw exception. Script doesnot exist");
        }
        catch (ScriptingException e) {
        }
        assertNull (service.getScript(scriptName));

        createSimpleScript(scriptName)
        service.checkScript(scriptName)
        assertNull (service.getScript(scriptName));

        service.checkScript("script1")
    }

    public void testReloadScript()
    {
        def scriptName = "script1.groovy";
        createSimpleScript(scriptName)
        service.addScript(scriptName)
        def cls = service.getScript(scriptName);
        assertNotNull (cls);
        def scriptObject = cls.newInstance();
        assertEquals (expectedScriptMessage, scriptObject.run())

        def changedMessage = "changed message";
        def scriptFile = new File("$base_directory/$ScriptingService.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("return \"$changedMessage\"");
        service.reloadScript (scriptName);
        
        cls = service.getScript(scriptName);
        assertNotNull (cls);
        scriptObject = cls.newInstance();
        assertEquals (changedMessage, scriptObject.run())

        changedMessage = "changed message 2";
        scriptFile = new File("$base_directory/$ScriptingService.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("return \"$changedMessage\"");
        service.reloadScript (scriptName);

        cls = service.getScript("script1");
        assertNotNull (cls);
        scriptObject = cls.newInstance();
        assertEquals (changedMessage, scriptObject.run())
        
    }

     public void testReloadScriptKeepsOldOneIfNewOneContainsErrors()
    {
        def scriptName = "script1.groovy";
        createSimpleScript(scriptName)
        service.addScript(scriptName)
        def cls = service.getScript(scriptName);
        assertNotNull (cls);
        def scriptObject = cls.newInstance();
        assertEquals (expectedScriptMessage, scriptObject.run())

        createErrornousScript (scriptName);
        try
        {
            service.reloadScript (scriptName);
            fail("should throw exception");
        }
        catch(ScriptingException e)
        {
        }

        cls = service.getScript(scriptName);
        assertNotNull (cls);
        scriptObject = cls.newInstance();
        assertEquals (expectedScriptMessage, scriptObject.run())
    }

    public void testRunScript()
    {
        def scriptName = "script1.groovy";
        def scriptFile = new File("$base_directory/$ScriptingService.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("return name");
        service.addScript(scriptName)

        def bindings = ["name":"user1"]
        assertEquals ("user1", service.runScript(scriptName, bindings));
        assertEquals ("user1", service.runScript("script1", bindings));
    }
    public void testRunScriptThrowsRuntimeExceptions()
    {
        def scriptName = "script1.groovy";
        def exceptionMessage = "error occurred"
        def scriptFile = new File("$base_directory/$ScriptingService.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("throw new Exception(\"$exceptionMessage\");");
        service.addScript(scriptName)

        def bindings = [:]
        try
        {
            service.runScript(scriptName, bindings);
            fail("Should throw exception");
        }
        catch(ScriptingException e)
        {
            assertTrue (e.getMessage().indexOf(exceptionMessage) >= 0);
        }
    }

    public void testRunScriptThrowsScriptDoesnotExistsExceptionIfScriptNotAdded()
    {
        def scriptName = "script1.groovy";
        def bindings = [:]
        try
        {
            service.runScript(scriptName, bindings);
            fail("Should throw exception");
        }
        catch(ScriptingException e)
        {
            assertEquals (ScriptingException.scriptDoesnotExist("script1").getMessage(), e.getMessage());
        }
    }

    public void testAfterPropertiesSet()
    {
        String defaultBaseDir = System.getProperty("base.dir")
        def defaultApplication = ApplicationHolder.application;

        System.setProperty ("base.dir", base_directory);
        ApplicationHolder.application = new DefaultGrailsApplication();

        try
        {
            def script1 = "script1.groovy";
            def script2 = "script2.groovy";
            def script3 = "script3.groovy";
            def notScript = "notScript.xml";
            createSimpleScript (script1)
            createSimpleScript (script2)
            createErrornousScript(script3);
            createSimpleScript (notScript)
            service.afterPropertiesSet();
            assertNotNull (service.getScript(script1));
            assertEquals ("script1", service.getScript(script1).name);
            assertNotNull (service.getScript(script2));
            assertEquals ("script2", service.getScript(script2).name);
            assertNull ("Should not load scripts containing syntax errors", service.getScript(script3));
            assertNull ("Should not load files other than groovy", service.getScript("notScript"));

        }
        finally
        {
            if(defaultBaseDir)
            System.setProperty ("base.dir", defaultBaseDir);
            ApplicationHolder.application = defaultApplication;
        }

    }


    def createSimpleScript(scriptName)
    {
        def scriptFile = new File("$base_directory/$ScriptingService.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("""return "$expectedScriptMessage" """);
    }

    def createErrornousScript(scriptName)
    {

        def scriptFile = new File("$base_directory/$ScriptingService.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("return \"$expectedScriptMessage");
    }
}
