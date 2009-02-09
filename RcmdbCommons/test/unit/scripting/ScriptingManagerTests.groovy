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

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Apr 18, 2008
* Time: 9:26:20 AM
* To change this template use File | Settings | File Templates.
*/
class ScriptingManagerTests extends RapidCmdbTestCase{
    def expectedScriptMessage = "script executed successfully";
    def static base_directory = "../testoutput/";
    ScriptManager manager;
    Logger testLogger;
    static String dsKey = ScriptingManagerTests.name;
    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        TestDatastore.put(dsKey, []);
        manager = ScriptManager.getInstance();
        if(new File(base_directory).exists())
        {
            FileUtils.deleteDirectory (new File(base_directory));
        }
        manager.initialize(this.class.getClassLoader(), base_directory, [], [:]);
        new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();
        testLogger=Logger.getLogger("scriptingtestlogger");
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        TestDatastore.clear();
        manager.destroyInstance();
        FileUtils.deleteDirectory(new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY"));
    }


    public void testAddScript()
    {
        ScriptManager.getInstance().destroy();
        manager.initialize(this.class.getClassLoader(), base_directory, [], [method1:{param1-> return param1;}, method2:{param1, param2-> return param1+param2}]);
        def scriptName = "script1.groovy";
        createSimpleScript (scriptName)
        manager.addScript(scriptName);
        assertNotNull (manager.getScript(scriptName));
        assertEquals("script1", manager.getScript(scriptName).name);

        manager.clearScripts();
        manager.addScript("script1");
        assertNotNull (manager.getScript(scriptName));
        assertEquals("script1", manager.getScript(scriptName).name);
        def instance = manager.getScript(scriptName).newInstance();
        String param1 = "param1";
        String param2 = "param2";
        assertEquals (param1, instance.method1(param1));
        assertEquals (param1+param2, instance.method2(param1, param2));

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
        }catch(groovy.lang.MissingMethodException e){e.printStackTrace()}
    }
    public void testRemoveScript()
    {
        def scriptName = "script1.groovy";
        createSimpleScript (scriptName)
        manager.addScript(scriptName);
        assertNotNull (manager.getScript(scriptName));
        assertEquals("script1", manager.getScript(scriptName).name);

        manager.removeScript(scriptName)
        assertNull (manager.getScript(scriptName));

    }
    public void testAddScriptDoesNotAddIfScriptAlreadyExists()
    {
        def scriptName = "script1.groovy";
        createSimpleScript(scriptName)
        manager.addScript(scriptName)
        def cls = manager.getScript(scriptName);
        assertNotNull (cls);
        def scriptObject = cls.newInstance();
        assertEquals (expectedScriptMessage, scriptObject.run())

        def changedMessage = "changed message";
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("return \"$changedMessage\"");
        manager.addScript (scriptName);
        
        cls = manager.getScript(scriptName);
        assertNotNull (cls);
        scriptObject = cls.newInstance();
        assertEquals (expectedScriptMessage, scriptObject.run())
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
        assertNull (manager.getScript(scriptName));

        createSimpleScript(scriptName)
        manager.checkScript(scriptName)
        assertNull (manager.getScript(scriptName));

        manager.checkScript("script1")
    }

    public void testReloadScript()
    {
        def scriptName = "script1.groovy";
        createSimpleScript(scriptName)
        manager.addScript(scriptName)
        def cls = manager.getScript(scriptName);
        assertNotNull (cls);
        def scriptObject = cls.newInstance();
        assertEquals (expectedScriptMessage, scriptObject.run())

        def changedMessage = "changed message";
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("return \"$changedMessage\"");
        manager.reloadScript (scriptName);
        
        cls = manager.getScript(scriptName);
        assertNotNull (cls);
        scriptObject = cls.newInstance();
        assertEquals (changedMessage, scriptObject.run())

        changedMessage = "changed message 2";
        scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("return \"$changedMessage\"");
        manager.reloadScript (scriptName);

        cls = manager.getScript("script1");
        assertNotNull (cls);
        scriptObject = cls.newInstance();
        assertEquals (changedMessage, scriptObject.run())
        
    }

     public void testReloadScriptKeepsOldOneIfNewOneContainsErrors()
    {
        def scriptName = "script1.groovy";
        createSimpleScript(scriptName)
        manager.addScript(scriptName)
        def cls = manager.getScript(scriptName);
        assertNotNull (cls);
        def scriptObject = cls.newInstance();
        assertEquals (expectedScriptMessage, scriptObject.run())

        createErrornousScript (scriptName);
        try
        {
            manager.reloadScript (scriptName);
            fail("should throw exception");
        }
        catch(ScriptingException e)
        {
        }

        cls = manager.getScript(scriptName);
        assertNotNull (cls);
        scriptObject = cls.newInstance();
        assertEquals (expectedScriptMessage, scriptObject.run())
    }
    
    public void testRunScriptCreatesLogger()
    {
        def scriptName = "script1.groovy";
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("return logger");
        manager.addScript(scriptName)

        def bindings=[:];

        def logLevel=Level.DEBUG;
        def logger=Logger.getLogger("testlogger");
        logger.setLevel(logLevel);


        def res=manager.runScript(scriptName, bindings,logger);
        assertEquals(res.getLevel(),logLevel);
        assertEquals(res.getName(),"testlogger");

        logger.setLevel(Level.INFO);
        res=manager.runScript(scriptName, bindings,logger);
        assertEquals(res.getLevel(),Level.INFO);
        
    }
    
    public void testRunScript()
    {
        def scriptName = "script1.groovy";
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("return name");
        manager.addScript(scriptName)

        def bindings = ["name":"user1"]
        assertEquals ("user1", manager.runScript(scriptName, bindings,testLogger));
        assertEquals ("user1", manager.runScript("script1", bindings,testLogger));
    }
    public void testOperationClassInjectedToScript(){
        def scriptName = "script1.groovy";
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("""
           setProperty("input",[:])
           input.fromScript="scriptHello";
           injectedFunction();
           injectedFunction2("injectedParamHello");
           return input;     
        """);
        manager.addScript(scriptName);
        def result=manager.runScript(scriptName, [:],testLogger,TestScriptOperationClass);
        
        assertEquals(result.fromScript,"scriptHello");
        assertEquals(result.fromInjectedFunction,"injectedHello");
        assertEquals(result.fromInjectedFunctionParam,"injectedParamHello");
    }
    public void testRunScriptThrowsRuntimeExceptions()
    {
        def scriptName = "script1.groovy";
        def exceptionMessage = "error occurred"
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("throw new Exception(\"$exceptionMessage\");");
        manager.addScript(scriptName)

        def bindings = [:]
        try
        {
            manager.runScript(scriptName, bindings,testLogger);
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
            manager.runScript(scriptName, bindings,testLogger);
            fail("Should throw exception");
        }
        catch(ScriptingException e)
        {
            assertEquals (ScriptingException.scriptDoesnotExist("script1").getMessage(), e.getMessage());
        }
    }

    public void testInitialize()
    {
        def script1 = "script1.groovy";
        def script2 = "script2.groovy";
        def script3 = "script3.groovy";
        def notScript = "notScript.xml";
        createSimpleScript (script1)
        createSimpleScript (script2)
        createErrornousScript(script3);
        createSimpleScript (notScript)
        String expectedStringFromDefaultMethod = "expected";
        manager.initialize(this.class.classLoader, base_directory, [], [method1:{param1-> return param1+expectedStringFromDefaultMethod;}]);
        assertNotNull (manager.getScript(script1));
        assertEquals ("script1", manager.getScript(script1).name);
        
        def scriptInstance = manager.getScript(script1).newInstance();
        String parameter = "param1";
        assertEquals (parameter+expectedStringFromDefaultMethod, scriptInstance.method1(parameter));

        assertNotNull (manager.getScript(script2));
        assertEquals ("script2", manager.getScript(script2).name);
        scriptInstance = manager.getScript(script2).newInstance();
        parameter = "param1";
        assertEquals (parameter+expectedStringFromDefaultMethod, scriptInstance.method1(parameter));

        assertNull ("Should not load scripts containing syntax errors", manager.getScript(script3));
        assertNull ("Should not load files other than groovy", manager.getScript("notScript"));

    }

    public void testStartupScripts()
    {

        def script1 = "script1.groovy";
        def script2 = "script2.groovy";
        def script3 = "script3.groovy";
        createStartupScriptScript (script1)
        createErrornousScript (script2)
        createStartupScriptScript(script3);
        manager.initialize(ScriptingManagerTests.classLoader, base_directory, ["script1", "script2", "script3.groovy"], [:]);
        assertEquals (2, TestDatastore.get(dsKey).size());


    }
    void testCallingDestroyInstanceWithoutInitializeDoesNotGenerateException(){
        //we should disgard previous initializes
        try{
            ScriptManager.destroyInstance();
        }catch(e){;}

        def testManager=ScriptManager.getInstance();
        assertNotNull(testManager);
        try{
            ScriptManager.destroyInstance();
        }
        catch(e)
        {
            fail("Should Not Throw Exception")
        }
    }

    public static void addScriptMessage(String message)
    {
        TestDatastore.get(dsKey).add(message);
    }
    def createStartupScriptScript(scriptName)
    {
        
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.delete()
        scriptFile.write (""" ${ScriptingManagerTests.class.name}.addScriptMessage("$expectedScriptMessage");
        """);
    }

    def createSimpleScript(scriptName)
    {
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("""return "$expectedScriptMessage" """);
    }

    def createErrornousScript(scriptName)
    {

        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("return \"$expectedScriptMessage");
    }

}
