/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package scripting

import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptingException
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.test.util.TestDatastore
import org.apache.commons.io.FileUtils
import org.apache.log4j.Level
import org.apache.log4j.Logger
import com.ifountain.rcmdb.execution.ExecutionContextManager
import com.ifountain.rcmdb.execution.ExecutionContext
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.util.DataStore
import com.ifountain.rcmdb.scripting.ScriptObjectWrapper
import com.ifountain.comp.utils.SmartWait
import com.ifountain.rcmdb.util.ClosureWaitAction

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Apr 18, 2008
* Time: 9:26:20 AM
* To change this template use File | Settings | File Templates.
*/
class ScriptingManagerTests extends RapidCmdbTestCase {
    def expectedScriptMessage = "script executed successfully";
    def static base_directory = "../testoutput/";
    ScriptManager manager;
    Logger testLogger;
    static String dsKey = ScriptingManagerTests.name;
    protected void setUp() {
        super.setUp();
        clearMetaClasses();
        initializeScriptManager();
    }

    protected void tearDown() {
        clearMetaClasses();
        super.tearDown();
        TestDatastore.clear();
        manager.destroyInstance();
        FileUtils.deleteDirectory(new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY"));
    }
    protected void initializeScriptManager()
    {
        TestDatastore.put(dsKey, []);
        manager = ScriptManager.getInstance();
        if (new File(base_directory).exists())
        {
            FileUtils.deleteDirectory(new File(base_directory));
        }
        new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();
        manager.initialize(this.class.getClassLoader(), base_directory, [:]);
        testLogger = Logger.getLogger("scriptingtestlogger");
    }
    private void clearMetaClasses()
    {
        ScriptManager.destroyInstance();
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptManager)
        ExpandoMetaClass.enableGlobally();
    }
    public void testAddScript()
    {
        ScriptManager.getInstance().destroy();
        manager.initialize(this.class.getClassLoader(), base_directory, [method1: {param1 -> return param1;}, method2: {param1, param2 -> return param1 + param2}]);
        def scriptName = "script1.groovy";
        createSimpleScript(scriptName)
        manager.addScript(scriptName);
        assertNotNull(manager.getScript(scriptName));
        assertEquals("script1", manager.getScript(scriptName).name);

        manager.clearScripts();
        manager.addScript("script1");
        assertNotNull(manager.getScript(scriptName));
        assertEquals("script1", manager.getScript(scriptName).name);
        def instance = manager.getScript(scriptName).newInstance();
        String param1 = "param1";
        String param2 = "param2";
        assertEquals(param1, instance.method1(param1));
        assertEquals(param1 + param2, instance.method2(param1, param2));

        //TODO:This test case could not passed if a method has single parameter, can it be called with no parameter?
        //        try
        //        {
        //            println instance.method1()
        //            fail("Should throw exception");
        //        }catch(groovy.lang.MissingMethodException e){}
        try
        {
            instance.method2()
            fail("Should throw exception");
        } catch (groovy.lang.MissingMethodException e) {e.printStackTrace()}
    }

    public void testAddScriptWithNoContent(){
        ScriptManager.getInstance().destroy();
        manager.initialize(this.class.getClassLoader(), base_directory, [method1: {param1 -> return param1;}, method2: {param1, param2 -> return param1 + param2}]);
        def scriptName = "script1";
        createScript("${scriptName}.groovy", "");
        try{
            manager.addScript(scriptName);
            fail("should throw exception")
        }
        catch(ScriptingException e){
           assertEquals("Cannot load script class ${scriptName}.", e.getCause().getMessage())
        }

    }
    public void testManagerGeneratesExceptionIfScriptIsMissingInScriptsFolderEvenIfScriptClassIsInLoader()
    {
        ScriptManager.getInstance().destroy();
        manager.initialize(this.class.getClassLoader(), base_directory, [method1: {param1 -> return param1;}, method2: {param1, param2 -> return param1 + param2}]);
        def scriptInOtherDirectory="scripting.ScriptingManagerTests";

        //first test that script can be loaded with ScriptManager classloader
        def scriptClassLoader = new GroovyClassLoader(ScriptManager.classLoader);
        try{
            scriptClassLoader.loadClass(scriptInOtherDirectory);
        }
        catch(e)
        {
            e.printStackTrace();
            fail("Should not throw exception ${e}");
        }
        assertNull(manager.getScript(scriptInOtherDirectory));
        
        //test that script manager generates exception , because file is not in scripts directory
        try{
            manager.addScript(scriptInOtherDirectory);
            fail("should throw ScriptingException");
        }
        catch(ScriptingException e)
        {
             println e
        }
        assertNull(manager.getScript(scriptInOtherDirectory));


        //test a nonexisting script
        def scriptName = "script555.groovy";
        try{
            manager.addScript(scriptName);
            fail("should throw ScriptingException");
        }
        catch(ScriptingException e)
        {
             println e
        }
        assertNull(manager.getScript(scriptName));

        //add the script and test it is added successfully
        createSimpleScript(scriptName)
        manager.addScript(scriptName);
        assertNotNull(manager.getScript(scriptName));
        assertEquals("script555", manager.getScript(scriptName).name);
        
        //remove the script and test exception is generated
        File scriptFile=new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/${scriptName}");
        scriptFile.delete();
        assertFalse(scriptFile.exists());
        try{
            manager.reloadScript (scriptName);
            fail("should throw ScriptingException");
        }
        catch(ScriptingException e)
        {
             println e
        }
        //kee[s the old version
        assertNotNull(manager.getScript(scriptName));

    }
    public void testRemoveScript()
    {
        def scriptName = "script1.groovy";
        createSimpleScript(scriptName)
        manager.addScript(scriptName);
        assertNotNull(manager.getScript(scriptName));
        assertEquals("script1", manager.getScript(scriptName).name);

        manager.removeScript(scriptName)
        assertNull(manager.getScript(scriptName));

    }
    public void testAddScriptDoesNotAddIfScriptAlreadyExists()
    {
        def scriptName = "script1.groovy";
        createSimpleScript(scriptName)
        manager.addScript(scriptName)
        def cls = manager.getScript(scriptName);
        assertNotNull(cls);
        def scriptObject = cls.newInstance();
        assertEquals(expectedScriptMessage, scriptObject.run())

        def changedMessage = "changed message";
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write("return \"$changedMessage\"");
        manager.addScript(scriptName);

        cls = manager.getScript(scriptName);
        assertNotNull(cls);
        scriptObject = cls.newInstance();
        assertEquals(expectedScriptMessage, scriptObject.run())
    }
    public void testAddScriptThrowsExceptionIfScriptDoesnotExist()
    {
        def scriptName = "script1.groovy";
        try {
            manager.addScript(scriptName)
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
            manager.addScript(scriptName)
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
            manager.checkScript(scriptName)
            fail("Should throw exception. Script doesnot exist");
        }
        catch (ScriptingException e) {
        }
        assertNull(manager.getScript(scriptName));

        createSimpleScript(scriptName)
        manager.checkScript(scriptName)
        assertNull(manager.getScript(scriptName));

        manager.checkScript("script1")
    }

    public void testReloadScript()
    {
        def scriptName = "script1.groovy";
        createSimpleScript(scriptName)
        manager.addScript(scriptName)
        def cls = manager.getScript(scriptName);
        assertNotNull(cls);
        def scriptObject = cls.newInstance();
        assertEquals(expectedScriptMessage, scriptObject.run())

        def changedMessage = "changed message";
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write("return \"$changedMessage\"");
        manager.reloadScript(scriptName);

        cls = manager.getScript(scriptName);
        assertNotNull(cls);
        scriptObject = cls.newInstance();
        assertEquals(changedMessage, scriptObject.run())

        changedMessage = "changed message 2";
        scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write("return \"$changedMessage\"");
        manager.reloadScript(scriptName);

        cls = manager.getScript("script1");
        assertNotNull(cls);
        scriptObject = cls.newInstance();
        assertEquals(changedMessage, scriptObject.run())

    }

    public void testReloadScriptKeepsOldOneIfNewOneContainsErrors()
    {
        def scriptName = "script1.groovy";
        createSimpleScript(scriptName)
        manager.addScript(scriptName)
        def cls = manager.getScript(scriptName);
        assertNotNull(cls);
        def scriptObject = cls.newInstance();
        assertEquals(expectedScriptMessage, scriptObject.run())

        createErrornousScript(scriptName);
        try
        {
            manager.reloadScript(scriptName);
            fail("should throw exception");
        }
        catch (ScriptingException e)
        {
        }

        cls = manager.getScript(scriptName);
        assertNotNull(cls);
        scriptObject = cls.newInstance();
        assertEquals(expectedScriptMessage, scriptObject.run())
    }

    public void testGetScriptObjectCreatesLogger()
    {
        ExecutionContextManager.destroy();
        def scriptName = "script1.groovy";
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write("return ${RapidCMDBConstants.LOGGER}");
        manager.addScript(scriptName)

        def bindings = [:];

        def logLevel = Level.DEBUG;
        def logger = Logger.getLogger("testlogger");
        logger.setLevel(logLevel);

        def scriptObject = manager.getScriptObject(scriptName, bindings, logger);
        assertTrue ("Script obejcts should be wrapped to add execution context", scriptObject instanceof ScriptObjectWrapper);
        assertEquals(scriptObject[RapidCMDBConstants.LOGGER], logger);
        assertEquals(scriptObject[RapidCMDBConstants.LOGGER].getLevel(), logLevel);
        assertEquals(scriptObject[RapidCMDBConstants.LOGGER].getName(), "testlogger");

        logger.setLevel(Level.INFO);
        scriptObject = manager.getScriptObject(scriptName, bindings, logger);
        assertEquals(scriptObject[RapidCMDBConstants.LOGGER].getLevel(), Level.INFO);

    }
    public void testGetScriptObjectAllowsParamsLoggerToOverrideScriptLogger()
    {
        ExecutionContextManager.destroy();
        def scriptName = "script1.groovy";
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write("return ${RapidCMDBConstants.LOGGER}");
        manager.addScript(scriptName)



        def logLevel = Level.DEBUG;
        def logger = Logger.getLogger("testlogger");
        logger.setLevel(logLevel);

        def paramsLoggerLevel=Level.INFO;
        def paramsLogger=Logger.getLogger("paramsTestLogger");
        paramsLogger.setLevel(paramsLoggerLevel);

        def bindings = [:];
        bindings[RapidCMDBConstants.LOGGER]=paramsLogger;

        def scriptObject = manager.getScriptObject(scriptName, bindings, logger);
        assertTrue ("Script obejcts should be wrapped to add execution context", scriptObject instanceof ScriptObjectWrapper);
        assertEquals(scriptObject[RapidCMDBConstants.LOGGER], paramsLogger);
        assertEquals(scriptObject[RapidCMDBConstants.LOGGER].getLevel(), paramsLoggerLevel);
        assertEquals(scriptObject[RapidCMDBConstants.LOGGER].getName(), "paramsTestLogger");

        paramsLogger.setLevel(Level.WARN);
        scriptObject = manager.getScriptObject(scriptName, bindings, logger);
        assertEquals(scriptObject[RapidCMDBConstants.LOGGER].getLevel(), Level.WARN);
    }

    public void testGetScriptObjectThrowsScriptDoesnotExistsExceptionIfScriptNotAdded()
    {
        def scriptName = "script1.groovy";
        def bindings = [:]
        try
        {
            manager.getScriptObject(scriptName, bindings, testLogger);
            fail("Should throw exception");
        }
        catch (ScriptingException e)
        {
            assertEquals(ScriptingException.scriptDoesnotExist("script1.groovy").getMessage(), e.getMessage());
        }
    }


    public void testRunScript()
    {
        DataStore.clear();
        def scriptName = "script1.groovy";
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write("""
        ${DataStore.class.name}.put("contextLogger", ${ExecutionContextManager.class.name}.getInstance().getExecutionContext()[\"${RapidCMDBConstants.LOGGER}\"])
return name""");
        manager.addScript(scriptName)

        def bindings = ["name": "user1"]
        assertEquals("user1", manager.runScript(scriptName, bindings, testLogger));
        assertEquals("user1", manager.runScript("script1", bindings, testLogger));
        assertSame (testLogger, DataStore.get("contextLogger"));
    }
    public void testRunScriptCallsBase()
    {
        def scriptName = "script1.groovy";

        clearMetaClasses();
        def managerParams = [:]
        ScriptManager.metaClass.runScript = {scriptPath, bindings, scriptLogger ->
            managerParams.scriptPath = scriptPath
            managerParams.bindings = bindings
            managerParams.scriptLogger = scriptLogger
            return "myrunscript";
        }
        initializeScriptManager();

        def bindings = ["x": 5];
        assertEquals(managerParams.size(), 0);
        def result = manager.runScript(scriptName, bindings, testLogger);
        assertEquals(result, "myrunscript");
        assertEquals(managerParams.scriptPath, scriptName);
        assertEquals(managerParams.bindings, bindings);
        assertEquals(managerParams.scriptLogger, testLogger);

    }

    public void testRunScriptThrowsRuntimeExceptions()
    {
        def scriptName = "script1.groovy";
        def exceptionMessage = "error occurred"
        createScript(scriptName,"throw new Exception(\"$exceptionMessage\");");
        manager.addScript(scriptName)

        def bindings = [:]
        try
        {
            manager.runScript(scriptName, bindings, testLogger);
            fail("Should throw exception");
        }
        catch (ScriptingException e)
        {
            assertTrue(e.getMessage().indexOf(exceptionMessage) >= 0);
        }
    }

    public void testInitialize()
    {
        def script1 = "script1.groovy";
        def script2 = "script2.groovy";
        def script3 = "script3.groovy";
        def notScript = "notScript.xml";
        createSimpleScript(script1)
        createSimpleScript(script2)
        createErrornousScript(script3);
        createSimpleScript(notScript)
        String expectedStringFromDefaultMethod = "expected";
        manager.initialize(this.class.classLoader, base_directory, [method1: {param1 -> return param1 + expectedStringFromDefaultMethod;}]);
        assertNotNull(manager.getScript(script1));
        assertEquals("script1", manager.getScript(script1).name);

        def scriptInstance = manager.getScript(script1).newInstance();
        String parameter = "param1";
        assertEquals(parameter + expectedStringFromDefaultMethod, scriptInstance.method1(parameter));

        assertNotNull(manager.getScript(script2));
        assertEquals("script2", manager.getScript(script2).name);
        scriptInstance = manager.getScript(script2).newInstance();
        parameter = "param1";
        assertEquals(parameter + expectedStringFromDefaultMethod, scriptInstance.method1(parameter));

        assertNull("Should not load scripts containing syntax errors", manager.getScript(script3));
        assertNull("Should not load files other than groovy", manager.getScript("notScript"));

    }

    public void testRunStartupScripts()
    {
        def script1 = "script1.groovy";
        def script2 = "script2.groovy";
        def script3 = "script3.groovy";
        createStartupScriptScript(script1)
        createErrornousScript(script2)
        createStartupScriptScript(script3);
        manager.initialize(ScriptingManagerTests.classLoader, base_directory, [:]);
        manager.runStartupScripts (["script1", "script2", "script3.groovy"]);
        assertEquals(2, TestDatastore.get(dsKey).size());
    }

    void testCallingDestroyInstanceWithoutInitializeDoesNotGenerateException() {
        //we should disgard previous initializes
        try {
            ScriptManager.destroyInstance();
        } catch (e) {;}

        def testManager = ScriptManager.getInstance();
        assertNotNull(testManager);
        try {
            ScriptManager.destroyInstance();
        }
        catch (e)
        {
            fail("Should Not Throw Exception")
        }
    }

    void testScriptManagerIsSynchronized() {
        clearMetaClasses();
        def testScripts = new ArrayList();
        ScriptManager.metaClass._addScript = {String scriptPath ->
            Thread.sleep(300)
            testScripts.add(scriptPath)
        }
        initializeScriptManager();

        def threads = [];
        10.times {
            def scriptName = "myscript${it}"
            def t = Thread.start {
                manager.addScript(scriptName);
            }
            threads.add(t);
        }
        Thread.sleep(400);
        try {
            assertTrue(testScripts.size() < 10)
        }
        finally {
            threads.each {
                it.join();
            }
        }
    }



      
    public static void addScriptMessage(String message)
    {
        TestDatastore.get(dsKey).add(message);
    }
    def createStartupScriptScript(scriptName)
    {
        createScript(scriptName,""" ${ScriptingManagerTests.class.name}.addScriptMessage("$expectedScriptMessage");
        """);
    }

    def createSimpleScript(scriptName)
    {
        createScript(scriptName,"""return "$expectedScriptMessage" """);
    }

    def createErrornousScript(scriptName)
    {
        createScript(scriptName,"return \"$expectedScriptMessage");
    }

    def createScript(scriptName,scriptContent)
    {
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.delete()
        scriptFile.setText(scriptContent);
    }



}



