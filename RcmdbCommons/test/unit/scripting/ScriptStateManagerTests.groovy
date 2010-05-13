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
import com.ifountain.rcmdb.scripting.ScriptStateManager

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Apr 18, 2008
* Time: 9:26:20 AM
* To change this template use File | Settings | File Templates.
*/
class ScriptStateManagerTests extends RapidCmdbTestCase {
    def expectedScriptMessage = "script executed successfully";
    def static base_directory = "../testoutput/";
    ScriptManager scriptManager;
    ScriptStateManager stateManager;
    Logger testLogger;
    static String dsKey = ScriptStateManagerTests.name;
    protected void setUp() {
        super.setUp();
        clearMetaClasses();
        initializeScriptManager();
    }

    protected void tearDown() {
        clearMetaClasses();
        super.tearDown();
        TestDatastore.clear();
        scriptManager.destroyInstance();
        stateManager.destroyInstance();
        FileUtils.deleteDirectory(new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY"));
    }
    protected void initializeScriptManager()
    {
        TestDatastore.put(dsKey, []);
        scriptManager = ScriptManager.getInstance();
        stateManager = ScriptStateManager.getInstance();

        if (new File(base_directory).exists())
        {
            FileUtils.deleteDirectory(new File(base_directory));
        }
        new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();
        scriptManager.initialize(this.class.getClassLoader(), base_directory, [:]);
        testLogger = Logger.getLogger("scriptingtestlogger");
    }
    private void clearMetaClasses()
    {
        ScriptManager.destroyInstance();
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptManager)
        ExpandoMetaClass.enableGlobally();
    }



     public void testCreateDefaultStopState()
    {
        assertEquals(0,stateManager.scriptStopStates.size());

        def scriptName1="script1";
        def scriptName2="script2";


        stateManager.createDefaultStopState(scriptName1);
        assertEquals(1,stateManager.scriptStopStates.size());
        assertEquals(false,stateManager.scriptStopStates[scriptName1][stateManager.IS_STOPPED_PROPERTY])
        def oldStopMap=stateManager.scriptStopStates[scriptName1];

        //create again will create again but with same result
        stateManager.createDefaultStopState(scriptName1);
        assertEquals(1,stateManager.scriptStopStates.size());
        assertEquals(false,stateManager.scriptStopStates[scriptName1][stateManager.IS_STOPPED_PROPERTY])
        assertNotSame(oldStopMap,stateManager.scriptStopStates[scriptName1]);

        //create again will change the true since creates the default
        stateManager.scriptStopStates[scriptName1][stateManager.IS_STOPPED_PROPERTY]=true;

        stateManager.createDefaultStopState(scriptName1);
        assertEquals(1,stateManager.scriptStopStates.size());
        assertEquals(false,stateManager.scriptStopStates[scriptName1][stateManager.IS_STOPPED_PROPERTY])

        //create another type of script
        stateManager.createDefaultStopState(scriptName2);
        assertEquals(2,stateManager.scriptStopStates.size());
        assertEquals(false,stateManager.scriptStopStates[scriptName1][stateManager.IS_STOPPED_PROPERTY])
        assertEquals(false,stateManager.scriptStopStates[scriptName2][stateManager.IS_STOPPED_PROPERTY])

    }
    public void testCreateStopStateIfNotExists()
    {
        assertEquals(0,stateManager.scriptStopStates.size());

        def scriptName1="script1";
        def scriptName2="script2";


        stateManager.createStopStateIfNotExists(scriptName1);
        assertEquals(1,stateManager.scriptStopStates.size());
        assertEquals(false,stateManager.scriptStopStates[scriptName1][stateManager.IS_STOPPED_PROPERTY])
        def oldStopMap=stateManager.scriptStopStates[scriptName1];

        //create again will do nothing
        stateManager.createStopStateIfNotExists(scriptName1);
        assertEquals(1,stateManager.scriptStopStates.size());
        assertEquals(false,stateManager.scriptStopStates[scriptName1][stateManager.IS_STOPPED_PROPERTY])
        assertSame(oldStopMap,stateManager.scriptStopStates[scriptName1])

        //create again will not change the true state will not create
        stateManager.scriptStopStates[scriptName1][stateManager.IS_STOPPED_PROPERTY]=true;

        stateManager.createStopStateIfNotExists(scriptName1);
        assertEquals(1,stateManager.scriptStopStates.size());
        assertEquals(true,stateManager.scriptStopStates[scriptName1][stateManager.IS_STOPPED_PROPERTY])

        //create another type of script
        stateManager.createStopStateIfNotExists(scriptName2);
        assertEquals(2,stateManager.scriptStopStates.size());
        assertEquals(true,stateManager.scriptStopStates[scriptName1][stateManager.IS_STOPPED_PROPERTY])
        assertEquals(false,stateManager.scriptStopStates[scriptName2][stateManager.IS_STOPPED_PROPERTY])
    }
    public void testGetStopStateOfStateMap()
    {
        def scriptName1="script1";

        stateManager.createDefaultStopState(scriptName1);
        assertEquals(false,stateManager.getStopStateOfStateObject(stateManager.scriptStopStates[scriptName1]))

        stateManager.scriptStopStates[scriptName1][stateManager.IS_STOPPED_PROPERTY]=true;
        assertEquals(true,stateManager.getStopStateOfStateObject(stateManager.scriptStopStates[scriptName1]))

        stateManager.scriptStopStates[scriptName1][stateManager.IS_STOPPED_PROPERTY]=false;
        assertEquals(false,stateManager.getStopStateOfStateObject(stateManager.scriptStopStates[scriptName1]))

        stateManager.scriptStopStates[scriptName1][stateManager.IS_STOPPED_PROPERTY]=true;
        assertEquals(true,stateManager.getStopStateOfStateObject(stateManager.scriptStopStates[scriptName1]))

        stateManager.createDefaultStopState(scriptName1);
        assertEquals(false,stateManager.getStopStateOfStateObject(stateManager.scriptStopStates[scriptName1]))
    }
    public void testStopRunningScripts()
    {
        def scriptName1="script1";
        def scriptName2="script2";
        def scriptName3="script3";

        stateManager.createStopStateIfNotExists(scriptName1);
        stateManager.createStopStateIfNotExists(scriptName2);
        

        def oldStateMap1=stateManager.scriptStopStates[scriptName1];
        def oldStateMap2=stateManager.scriptStopStates[scriptName2];

        assertEquals(false,stateManager.getStopStateOfStateObject(oldStateMap1));
        assertEquals(false,stateManager.getStopStateOfStateObject(oldStateMap2));

        stateManager.stopRunningScripts(scriptName1);
        assertEquals(true,stateManager.getStopStateOfStateObject(oldStateMap1));
        assertNotSame(oldStateMap1,stateManager.scriptStopStates[scriptName1]);
        assertEquals(false,stateManager.getStopStateOfStateObject(stateManager.scriptStopStates[scriptName1]));
        assertEquals(false,stateManager.getStopStateOfStateObject(oldStateMap2));

        stateManager.stopRunningScripts(scriptName2);
        assertEquals(true,stateManager.getStopStateOfStateObject(oldStateMap1));
        assertEquals(true,stateManager.getStopStateOfStateObject(oldStateMap2));
        assertNotSame(oldStateMap2,stateManager.scriptStopStates[scriptName2]);
        assertEquals(false,stateManager.getStopStateOfStateObject(stateManager.scriptStopStates[scriptName2]));

        stateManager.stopRunningScripts(scriptName3);
        assertEquals(false,stateManager.getStopStateOfStateObject(stateManager.scriptStopStates[scriptName3]));

    }

    public void testScriptsHaveIS_STOPPEDMethodAndAddStateParamToBindingsCreatesStateParamAndBinds()
    {
        def scriptName1 = "script1";
        createScript(scriptName1+".groovy","return IS_STOPPED()");
        scriptManager.addScript(scriptName1)

        def scriptName2 = "script2";
        createScript(scriptName2+".groovy","""
            import com.ifountain.rcmdb.test.util.TestDatastore
            iterationCount=0;
            while(!IS_STOPPED())
            {
                println "itr :"+iterationCount
                Thread.sleep(100);
                iterationCount++;
            }
            println "stopped in script"
            TestDatastore.put("script2_iterationCount",iterationCount);
            TestDatastore.put("script2_IS_STOPPED",IS_STOPPED());
        """);
        scriptManager.addScript(scriptName2)

        stateManager.createStopStateIfNotExists(scriptName1);
        stateManager.createStopStateIfNotExists(scriptName2);



        def bindings=[:];
        stateManager.addStateParamToBindings(scriptName1,bindings);
        assertEquals(false,scriptManager.runScript(scriptName1,bindings,testLogger));

        //doesnt effect the next run
        stateManager.stopRunningScripts (scriptName1);
        stateManager.addStateParamToBindings(scriptName1,bindings);
        assertEquals(false,scriptManager.runScript(scriptName1,bindings,testLogger));

        //script2 should wait until stop is called
        stateManager.addStateParamToBindings(scriptName2,bindings);
        def runnerThread=Thread.start(){
           scriptManager.runScript(scriptName2,bindings,testLogger);
        }
        assertEquals(null,TestDatastore.get("script2_IS_STOPPED"))
        assertEquals(null,TestDatastore.get("script2_iterationCount"))

        Thread.sleep(250);
        stateManager.stopRunningScripts (scriptName2);
        runnerThread.join();
        assertEquals(true,TestDatastore.get("script2_IS_STOPPED"))
        assertTrue(TestDatastore.get("script2_iterationCount")>0)



    }
    public void testStopMechanismIsSynchronized()
    {
        def stateHistory=[];
        def stopHistory=[];

        10.times{ subCounter ->
            def scriptName="script${subCounter}";
            stateManager.createStopStateIfNotExists(scriptName);
        }

        Thread adderThread=Thread.start(){
            50.times{
                10.times{ subCounter ->
                    def scriptName="script${subCounter}";
                    synchronized (stateManager.stopStateLock)
                    {
                        stateHistory.add([stopped:stateManager.getStopStateOfStateObject(stateManager.scriptStopStates[scriptName]),time:System.currentTimeMillis()]);
                    }
                }
            }
        }

        while(adderThread.isAlive())
        {
            10.times{ subCounter ->
                def scriptName="script${subCounter}";
                stateManager.stopRunningScripts(scriptName);
            }
        }

        def stopCount=0
        stateHistory.each{ map ->
            if(map.stopped)
            {
                stopCount++;
            }
        }

        if(stopCount>0)
            fail("There are ${stopCount} stop states found should be 0");




    }



   

    def createScript(scriptName,scriptContent)
    {
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.delete()
        scriptFile.setText(scriptContent);
    }



}



