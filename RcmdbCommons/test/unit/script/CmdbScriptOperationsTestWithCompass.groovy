package script

import auth.Group
import com.ifountain.rcmdb.datasource.ListeningAdapterManager
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import datasource.BaseListeningDatasource
import org.apache.commons.io.FileUtils
import org.apache.log4j.Level
import com.ifountain.rcmdb.scripting.ScriptStateManager
import com.ifountain.rcmdb.test.util.TestDatastore
import datasource.RepositoryDatasource
import connection.RepositoryConnection
import org.apache.log4j.Logger

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 15, 2009
* Time: 1:11:52 PM
* To change this template use File | Settings | File Templates.
*/


class CmdbScriptOperationsTestWithCompass extends RapidCmdbWithCompassTestCase {
    def expectedScriptMessage = "script executed successfully";
    def static base_directory = "../testoutput/";
    def simpleScriptFile = "CmdbScriptOperationsTestScriptFile.groovy"
    public void setUp() {
        super.setUp();
        clearMetaClasses();
    }

    public void tearDown() {
        clearMetaClasses();
        super.tearDown();
    }
    private void clearMetaClasses()
    {

        ListeningAdapterManager.destroyInstance();
        ScriptScheduler.destroyInstance();
        ScriptManager.destroyInstance();
        ScriptStateManager.destroyInstance();
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(BaseListeningDatasource)
        GroovySystem.metaClassRegistry.removeMetaClass(ListeningAdapterManager)
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptScheduler)
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptManager)
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptStateManager)
        GroovySystem.metaClassRegistry.removeMetaClass(CmdbScript)
        GroovySystem.metaClassRegistry.removeMetaClass(CmdbScriptOperations)
        ExpandoMetaClass.enableGlobally();
    }
    void initializeForCmdbScript() {
        initializeScriptManager();

        CompassForTests.addOperationSupport(CmdbScript, CmdbScriptOperations);


    }
    void initializeScriptManager()
    {
        ScriptManager manager = ScriptManager.getInstance();
        if (new File(base_directory).exists())
        {
            FileUtils.deleteDirectory(new File(base_directory));
        }
        manager.initialize(this.class.getClassLoader(), base_directory, [:]);
        new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();
        createSimpleScript(simpleScriptFile, expectedScriptMessage);
    }

    void testAddScriptGeneratesScriptFileParamWhenMissing()
    {
        initialize([CmdbScript], []);
        initializeForCmdbScript();

        def params = [name: simpleScriptFile.replace(".groovy", ""), type: CmdbScript.ONDEMAND]


        def script = CmdbScript.addScript(params)
        assertEquals(script.scriptFile, params.name)
    }
    void testAddScriptGeneratesLogFileParamWhenMissing()
    {
        initialize([CmdbScript], []);
        initializeForCmdbScript();

        def params = [name: "CmdbScriptOperationsTestScript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]

        def script = CmdbScript.addScript(params)
        assertEquals(script.logFile, params.name)

    }

    void testAddScriptDoesNotGenerateExceptionIfFromController()
    {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();


        try {
            def script = CmdbScript.addScript([name: null], true);
            assertTrue(script.hasErrors());
        }
        catch (e)
        {
            println e
            fail("should not throw exception")
        }
    }
    void testAddScriptUpdatesExistingScript()
    {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();

        def logLevel = Level.DEBUG;
        def logParams = [:]
        logParams["logLevel"] = logLevel.toString();


        def params = [name: "myscript", enabledForAllGroups: true, type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile, logLevel: logParams.logLevel, logFileOwn: true]

        assertEquals(0, CmdbScript.count());
        CmdbScript script = CmdbScript.addScript(params)
        assertFalse(script.hasErrors())

        assertEquals(1, CmdbScript.count());

        assertEquals(true,CmdbScript.get(id:script.id).logFileOwn);

        def params2=[name: "myscript",  enabledForAllGroups: false, type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile, logLevel: logParams.logLevel, logFileOwn:false]
        CmdbScript script2 = CmdbScript.addScript(params2)
        assertFalse(script2.hasErrors())

        assertEquals(1, CmdbScript.count());

        assertEquals(false,CmdbScript.get(id:script.id).logFileOwn);
    }
    void testAddUniqueScriptGeneratesErrorWhenScriptExists()
    {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();

        def logLevel = Level.DEBUG;
        def logParams = [:]
        logParams["logLevel"] = logLevel.toString();


        def params = [name: "myscript", enabledForAllGroups: true, type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile, logLevel: logParams.logLevel, logFileOwn: true]

        assertEquals(0, CmdbScript.count());
        CmdbScript script = CmdbScript.addScript(params)
        assertFalse(script.hasErrors())

        assertEquals(1, CmdbScript.count());

        assertEquals(true,CmdbScript.get(id:script.id).logFileOwn);

        def params2=[name: "myscript",  enabledForAllGroups: false, type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile, logLevel: logParams.logLevel, logFileOwn:false]
        CmdbScript script2 = CmdbScript.addUniqueScript(params2,true)
        assertTrue(script2.hasErrors())

        assertEquals(1, CmdbScript.count());
        assertEquals(true,CmdbScript.get(id:script.id).logFileOwn);
    }
    void testAddScript() {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();

        Group gr1 = Group.add(name: "group1");
        Group gr2 = Group.add(name: "group2");
        Group gr3 = Group.add(name: "group3");
        assertFalse(gr1.hasErrors());
        assertFalse(gr2.hasErrors());
        assertFalse(gr3.hasErrors());

        def logLevel = Level.DEBUG;
        def logParams = [:]
        logParams["logLevel"] = logLevel.toString();
        logParams["logFileOwn"] = true;


        def params = [name: "myscript", allowedGroups: [gr1, gr2], enabledForAllGroups: true, type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile, logLevel: logParams.logLevel, logFileOwn: logParams.logFileOwn]

        assertEquals(0, CmdbScript.count());
        CmdbScript script = CmdbScript.addScript(params)
        assertFalse(script.hasErrors())

        assertEquals(1, CmdbScript.count());

        assertTrue(script.enabledForAllGroups);
        def groups = script.allowedGroups;
        assertEquals(2, groups.size());
        groups = groups.sort {it.name}
        assertEquals(gr1.name, groups[0].name);
        assertEquals(gr2.name, groups[1].name);

        //tests that ScriptManager.addScript() is called
        def scriptClass = ScriptManager.getInstance().getScript(script.scriptFile);
        println scriptClass
        assertNotNull(scriptClass);

        //tests the script file is really loaded and can be runned
        def scriptObject = scriptClass.newInstance();
        assertEquals(expectedScriptMessage, scriptObject.run())

        //test the logger is configured
        assertEquals(script.name,script.logFile);
        def logger = CmdbScript.getScriptLogger(script);
        assertEquals(logger.getLevel(), logLevel);
        assertTrue(logger.getAllAppenders().hasMoreElements());
    }


    void testScheduleScriptCallsSchedulerForScheduledScriptsAndDoesNotCallForOthers() {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();


        def schedulerMap = [:];
        def isScheduledMap = [:];
        ScriptScheduler.metaClass.isScheduled = {String scriptName, long startDelay, long period ->
            isScheduledMap.scriptName = scriptName;
            isScheduledMap.startDelay = startDelay;
            isScheduledMap.period = period;
            return false;
        }
        ScriptScheduler.metaClass.scheduleScript = {String scriptName, long startDelay, long period ->
            schedulerMap.scriptName = scriptName
            schedulerMap.startDelay = startDelay
            schedulerMap.period = period
        }
        ScriptScheduler.metaClass.isScheduled = {String scriptName, long startDelay, String cronExp ->
            isScheduledMap.scriptName = scriptName;
            isScheduledMap.startDelay = startDelay;
            isScheduledMap.cronExp = cronExp;
            return false;
        }
        ScriptScheduler.metaClass.scheduleScript = {String scriptName, long startDelay, String cronExp ->
            schedulerMap.scriptName = scriptName
            schedulerMap.startDelay = startDelay
            schedulerMap.cronExp = cronExp
        }


        //testing for periodic script
        def params = [name: "myscript", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.PERIODIC, scriptFile: simpleScriptFile, enabled: true, startDelay: 10, period: 20]


        def script = CmdbScript.add(params)
        assertFalse(script.hasErrors())
        CmdbScript.scheduleScript(script);

        println schedulerMap
        assertEquals(params.name, schedulerMap.scriptName)
        assertEquals(params.startDelay, schedulerMap.startDelay)
        assertEquals(params.period, schedulerMap.period)

        //testing for cron script
        schedulerMap = [:];
        isScheduledMap = [:];
        params = [name: "cmyscript", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.CRON, scriptFile: simpleScriptFile, enabled: true, startDelay: 3, cronExpression: "* * * * * ?"]

        script.update(params)
        assertFalse(script.hasErrors())
        println schedulerMap
        CmdbScript.scheduleScript(script);
        assertEquals(params.name, schedulerMap.scriptName)
        assertEquals(params.startDelay, schedulerMap.startDelay)
        assertEquals(params.cronExpression, schedulerMap.cronExp)
        
        assertEquals(params.name, isScheduledMap.scriptName)
        assertEquals(params.startDelay, isScheduledMap.startDelay)
        assertEquals(params.cronExpression, isScheduledMap.cronExp)

        //testing if script is disabled
        schedulerMap = [:];
        isScheduledMap = [:];
        script.update(enabled: false)
        CmdbScript.scheduleScript(script);
        assertEquals(schedulerMap.size(), 0);

        schedulerMap = [:];
        isScheduledMap = [:];
        script.update(enabled: true)
        CmdbScript.scheduleScript(script);
        assertEquals(schedulerMap.size(), 3);

        schedulerMap = [:];
        isScheduledMap = [:];
        script.update(type: CmdbScript.ONDEMAND)
        CmdbScript.scheduleScript(script);
        assertEquals(schedulerMap.size(), 0);
    }

    void testScheduleScriptWillNotCallSchedulerIfItIsAlreadyScheduledWithPeriodicScript() {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();


        def schedulerMap = [:];
        def isScheduledMap = [:];

        ScriptScheduler.metaClass.isScheduled = {String scriptName, long startDelay, long period ->
            isScheduledMap.scriptName = scriptName;
            isScheduledMap.startDelay = startDelay;
            isScheduledMap.period = period;
            return true;
        }
        ScriptScheduler.metaClass.scheduleScript = {String scriptName, long startDelay, long period ->
            schedulerMap.scriptName = scriptName
            schedulerMap.startDelay = startDelay
            schedulerMap.period = period
        }
        //testing for periodic script
        def params = [name: "myscript", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.PERIODIC, scriptFile: simpleScriptFile, enabled: true, startDelay: 10, period: 20]


        def script = CmdbScript.add(params)
        assertFalse(script.hasErrors())
        CmdbScript.scheduleScript(script);

        assertEquals (params.name, isScheduledMap.scriptName);
        assertEquals (params.startDelay, isScheduledMap.startDelay);
        assertEquals (params.period, isScheduledMap.period);
        assertTrue(schedulerMap.isEmpty());
        schedulerMap.clear();
    }

    void testScheduleScriptWillNotCallSchedulerIfItIsAlreadyScheduledWithCronExpression() {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();


        def schedulerMap = [:];
        def isScheduledMap = [:];

        ScriptScheduler.metaClass.isScheduled = {String scriptName, long startDelay, String cronExp ->
            isScheduledMap.scriptName = scriptName;
            isScheduledMap.startDelay = startDelay;
            isScheduledMap.cronExp = cronExp;
            return true;
        }
        ScriptScheduler.metaClass.scheduleScript = {String scriptName, long startDelay, String cronExp ->
            schedulerMap.scriptName = scriptName
            schedulerMap.startDelay = startDelay
            schedulerMap.cronExp = cronExp
        }
        def params = [name: "cmyscript", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.CRON, scriptFile: simpleScriptFile, enabled: true, startDelay: 3, cronExpression: "* * * * * ?"]


        def script = CmdbScript.add(params)
        assertFalse(script.hasErrors())
        CmdbScript.scheduleScript(script);

        assertEquals (params.name, isScheduledMap.scriptName);
        assertEquals (params.startDelay, isScheduledMap.startDelay);
        assertEquals (params.cronExpression, isScheduledMap.cronExp);
        assertTrue(schedulerMap.isEmpty());
        schedulerMap.clear();
    }
    void testAddScriptCallsScheduleScript()
    {
        initialize([CmdbScript, Group], []);
        initializeScriptManager()

        def callParams = [:]
        CmdbScriptOperations.metaClass.static.scheduleScript = {CmdbScript script ->
            callParams.script = script

        }


        //testing for periodic script
        def params = [name: "myscript", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.PERIODIC, scriptFile: simpleScriptFile, enabled: true, startDelay: 10, period: 20]

        assertNull(callParams.id)
        def script = CmdbScriptOperations.addScript(params)
        assertFalse(script.hasErrors())
        assertEquals(callParams.script.id, script.id)

        //testing for cron script
        callParams = [:];
        params = [name: "myscript2", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.CRON, scriptFile: simpleScriptFile, enabled: true, startDelay: 3, cronExpression: "* * * * * ?"]

        assertNull(callParams.id)
        script = CmdbScriptOperations.addScript(params)
        assertFalse(script.hasErrors())
        assertEquals(callParams.script.id, script.id)

    }
    public void testUpdateScript()
    {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();
        def logLevel = Level.DEBUG;
        def oldLogLevel = Level.WARN;
        def logParams = [:]
        logParams["logLevel"] = logLevel.toString();
        logParams["logFileOwn"] = true;
        logParams["oldLogLevel"] = oldLogLevel.toString();
        logParams["oldLogFileOwn"] = false;

        def updateParams = [name: "myscript333", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile, logLevel: logParams.logLevel, logFileOwn: logParams.logFileOwn]

        def params = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile, logLevel: logParams.oldLogLevel, logFileOwn: logParams.oldLogFileOwn]
        assertEquals(CmdbScript.count(), 0)

        def scriptToUpdate = CmdbScript.addScript(params);

        assertFalse(scriptToUpdate.hasErrors())
        assertEquals(CmdbScript.count(), 1)

        def unscheduleScriptName = null;
        ScriptScheduler.metaClass.unscheduleScript = {String scriptName ->
            unscheduleScriptName = scriptName
        }


        def oldLogger = CmdbScript.getScriptLogger(scriptToUpdate);
        assertEquals(oldLogger.getLevel(), oldLogLevel);
        assertFalse(oldLogger.getAllAppenders().hasMoreElements());

        def script = CmdbScript.updateScript(scriptToUpdate, updateParams, false);
        assertFalse(script.hasErrors())
        assertFalse(scriptToUpdate.hasErrors())
        assertEquals(script.name, updateParams.name)
        assertEquals(unscheduleScriptName, params.name)

        assertEquals(script.name,script.logFile);
        def logger = CmdbScript.getScriptLogger(script);
        assertEquals(logger.getLevel(), logLevel);
        assertTrue(logger.getAllAppenders().hasMoreElements());

    }
    public void testUpdateLogLevel(){
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();
        def logLevel = Level.DEBUG;
        def oldLogLevel = Level.WARN;
        def logParams = [:]
        logParams["logLevel"] = logLevel.toString();
        logParams["logFileOwn"] = true;
        logParams["oldLogLevel"] = oldLogLevel.toString();


        def params = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile, logLevel: logParams.oldLogLevel, logFileOwn: logParams.logFileOwn]
        assertEquals(CmdbScript.count(), 0)

        def scriptToUpdate = CmdbScript.addScript(params);

        assertFalse(scriptToUpdate.hasErrors())
        assertEquals(CmdbScript.count(), 1)

        def oldLogger = CmdbScript.getScriptLogger(scriptToUpdate);
        assertEquals(oldLogger.getLevel(), oldLogLevel);
        assertTrue(oldLogger.getAllAppenders().hasMoreElements());

        def script = CmdbScript.updateLogLevel(scriptToUpdate, logParams["logLevel"], true);
        assertFalse(script.hasErrors())
        assertFalse(scriptToUpdate.hasErrors())

        def logger = CmdbScript.getScriptLogger(script);
        assertEquals(logger.getLevel(), logLevel);
        assertTrue(logger.getAllAppenders().hasMoreElements());
    }
    void testReloadScript()
    {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();
        def params = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]


        def script = CmdbScript.addScript(params);


        //tests the script file is really loaded and can be runned
        def scriptClass = ScriptManager.getInstance().getScript(script.scriptFile);
        def scriptObject = scriptClass.newInstance();
        assertEquals(expectedScriptMessage, scriptObject.run())

        def newScriptMessage = "new_script_message"
        createSimpleScript(simpleScriptFile, newScriptMessage)

        //we overwrited the script file test that the old one is working, because no reload done
        scriptClass = ScriptManager.getInstance().getScript(script.scriptFile);
        scriptObject = scriptClass.newInstance();
        assertEquals(expectedScriptMessage, scriptObject.run())

        //now we reload the script and test that the new one is working
        script.reload()
        scriptClass = ScriptManager.getInstance().getScript(script.scriptFile);
        scriptObject = scriptClass.newInstance();
        assertEquals(newScriptMessage, scriptObject.run())

    }

    void testUpdateScriptLoadsNewScriptFileAndRemovesOldIfNotUsedAndDoesNotRemoveOldIfUsed()
    {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();
        ScriptScheduler.metaClass.unscheduleScript = {String scriptName ->

        }

        ScriptManager.getInstance().addScript(simpleScriptFile);
        assertNotNull(ScriptManager.getInstance().getScript(simpleScriptFile));

        def newScriptFile = "CmdbScriptOperationsTestScriptFile2.groovy";
        def newScriptMessage = "new script message"
        createSimpleScript(newScriptFile, newScriptMessage);

        def params = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]
        def updateParams = [name: "myscript2", type: CmdbScript.ONDEMAND, scriptFile: newScriptFile]

        def script = CmdbScript.addScript(params);
        assertFalse(script.hasErrors())

        assertEquals(CmdbScript.countHits("scriptFile:${params.scriptFile}"), 1)
        assertEquals(CmdbScript.countHits("scriptFile:${updateParams.scriptFile}"), 0)



        //we test that old script is removed from ScriptManager


        def updatedScript = CmdbScript.updateScript(script, updateParams, false);
        assertFalse(updatedScript.hasErrors())
        assertEquals(updatedScript.name, updateParams.name)
        assertEquals(updatedScript.scriptFile, updateParams.scriptFile)

        assertNull(ScriptManager.getInstance().getScript(params.scriptFile));
        def scriptClass = ScriptManager.getInstance().getScript(updateParams.scriptFile)
        assertNotNull(scriptClass);

        //tests the new script file is really loaded and can be runned
        def scriptObject = scriptClass.newInstance();
        assertEquals(newScriptMessage, scriptObject.run())

        assertEquals(CmdbScript.countHits("scriptFile:${params.scriptFile}"), 0)
        assertEquals(CmdbScript.countHits("scriptFile:${updateParams.scriptFile}"), 1)

    }
    void testUpdateScriptLoadsNewScriptFileAndUpdateScriptDoesNotRemoveOldScriptFileIfUsed()
    {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();
        ScriptScheduler.metaClass.unscheduleScript = {String scriptName ->

        }

        ScriptManager.getInstance().addScript(simpleScriptFile);
        assertNotNull(ScriptManager.getInstance().getScript(simpleScriptFile));

        def newScriptFile = "CmdbScriptOperationsTestScriptFile2.groovy";
        def newScriptMessage = "new script message"
        createSimpleScript(newScriptFile, newScriptMessage);

        def params = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]
        def updateParams = [name: "myscript2", type: CmdbScript.ONDEMAND, scriptFile: newScriptFile]

        def params1 = [:]
        params1.putAll(params);
        params1.name = params.name + "oldfile1";

        def params2 = [:]
        params2.putAll(params);
        params2.name = params.name + "oldfile2";

        def scriptUsesOldFile = CmdbScript.addScript(params1);
        assertFalse(scriptUsesOldFile.hasErrors())
        def scriptUsesOldFile2 = CmdbScript.addScript(params2);
        assertFalse(scriptUsesOldFile2.hasErrors())

        assertEquals(CmdbScript.countHits("scriptFile:${params.scriptFile}"), 2)
        assertEquals(CmdbScript.countHits("scriptFile:${updateParams.scriptFile}"), 0)

        def updatedScript = CmdbScript.updateScript(scriptUsesOldFile, updateParams, false);
        assertEquals(updatedScript.name, updateParams.name)
        assertEquals(updatedScript.scriptFile, updateParams.scriptFile)

        assertNotNull(ScriptManager.getInstance().getScript(params.scriptFile));
        def scriptClass = ScriptManager.getInstance().getScript(updateParams.scriptFile)
        assertNotNull(scriptClass);

        //tests the new script file is really loaded and can be runned
        def scriptObject = scriptClass.newInstance();
        assertEquals(newScriptMessage, scriptObject.run())

        assertEquals(CmdbScript.countHits("scriptFile:${params.scriptFile}"), 1)
        assertEquals(CmdbScript.countHits("scriptFile:${updateParams.scriptFile}"), 1)

    }
    void testUpdateScriptDoesNotGenerateExceptionIfFromController()
    {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();

        def params = [name: "CmdbScriptOperationsTestScript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]
        def updateParams = [name: null]

        def scriptToUpdate = CmdbScript.addScript(params)
        assertFalse(scriptToUpdate.hasErrors())

        try {
            def script = CmdbScript.updateScript(scriptToUpdate, updateParams, true);
            assertTrue(script.hasErrors());
        }
        catch (e)
        {
            println e
            fail("should not throw exception")
        }
    }
    void testUpdateScriptWillNotCallSchedulerUnscheduleIfNameIsNotChanged()
    {
        initialize([CmdbScript, Group], []);
        initializeScriptManager()

        def callParams = [:]
        CmdbScriptOperations.metaClass.static.scheduleScript = {CmdbScript script ->
            callParams.script = script
            callParams.time = System.nanoTime()

        }
        def unscheduleCallParams = [:]
        ScriptScheduler.metaClass.unscheduleScript = {String scriptName ->
            unscheduleCallParams.scriptName = scriptName
            unscheduleCallParams.time = System.nanoTime()
        }

        //testing for periodic script
        def updateParams = [name: "myscript", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.PERIODIC, scriptFile: simpleScriptFile, enabled: true, startDelay: 10, period: 20]
        def params = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]
        def scriptToUpdate = CmdbScriptOperations.addScript(params);
        assertFalse(scriptToUpdate.hasErrors())

        callParams = [:];
        unscheduleCallParams = [:];
        assertEquals(callParams.size(), 0)
        assertEquals(unscheduleCallParams.size(), 0)
        def script = CmdbScriptOperations.updateScript(scriptToUpdate, updateParams, false)
        assertFalse(script.hasErrors())
        assertEquals(callParams.script.id, script.id)
        assertEquals(0, unscheduleCallParams.size())

        //testing for cron script
        updateParams = [name: "cmyscript", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.CRON, scriptFile: simpleScriptFile, enabled: true, startDelay: 10, cronExpression: "* * * * * ?"]
        params = [name: "cmyscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]
        scriptToUpdate = CmdbScriptOperations.addScript(params);
        assertFalse(scriptToUpdate.hasErrors())

        callParams = [:];
        unscheduleCallParams = [:];
        assertEquals(callParams.size(), 0)
        assertEquals(unscheduleCallParams.size(), 0)
        script = CmdbScriptOperations.updateScript(scriptToUpdate, updateParams, false)
        assertFalse(script.hasErrors())
        assertEquals(callParams.script.id, script.id)
        assertEquals(0, unscheduleCallParams.size())
    }
    void testUpdateScriptCallsSchedulerUnscheduleAndThenScheduleScriptIfScriptNameIsChanged()
    {
        initialize([CmdbScript, Group], []);
        initializeScriptManager()

        def callParams = [:]
        CmdbScriptOperations.metaClass.static.scheduleScript = {CmdbScript script ->
            callParams.script = script
            callParams.time = System.nanoTime()

        }
        def unscheduleCallParams = [:]
        ScriptScheduler.metaClass.unscheduleScript = {String scriptName ->
            unscheduleCallParams.scriptName = scriptName
            unscheduleCallParams.time = System.nanoTime()
        }

        //testing for periodic script
        def updateParams = [name: "myscript1", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.PERIODIC, scriptFile: simpleScriptFile, enabled: true, startDelay: 10, period: 20]
        def params = [name: "myscript", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.PERIODIC, scriptFile: simpleScriptFile, enabled: true, startDelay: 10, period: 20]
        def scriptToUpdate = CmdbScriptOperations.addScript(params);
        assertFalse(scriptToUpdate.hasErrors())

        callParams = [:];
        unscheduleCallParams = [:];
        assertEquals(callParams.size(), 0)
        assertEquals(unscheduleCallParams.size(), 0)
        def script = CmdbScriptOperations.updateScript(scriptToUpdate, updateParams, false)
        assertFalse(script.hasErrors())
        assertEquals(params.name, unscheduleCallParams.scriptName)
        assertEquals(callParams.script.id, script.id)
        assertTrue(unscheduleCallParams.time < callParams.time)

        //testing for cron script
        updateParams = [name: "cmyscript1", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.CRON, scriptFile: simpleScriptFile, enabled: true, startDelay: 10, cronExpression: "* * * * * ?"]
        params = [name: "cmyscript", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.CRON, scriptFile: simpleScriptFile, enabled: true, startDelay: 10, cronExpression: "* * * * * ?"]
        scriptToUpdate = CmdbScriptOperations.addScript(params);
        assertFalse(scriptToUpdate.hasErrors())

        callParams = [:];
        unscheduleCallParams = [:];
        assertEquals(callParams.size(), 0)
        assertEquals(unscheduleCallParams.size(), 0)
        script = CmdbScriptOperations.updateScript(scriptToUpdate, updateParams, false)
        assertFalse(script.hasErrors())
        assertEquals(params.name, unscheduleCallParams.scriptName)
        assertEquals(callParams.script.id, script.id)
        assertTrue(unscheduleCallParams.time < callParams.time)
    }


    void testUpdateScriptCallsSchedulerUnschedulIfScriptIsDisabled()
    {
        initialize([CmdbScript, Group], []);
        initializeScriptManager()

        def callParams = [:]
        CmdbScriptOperations.metaClass.static.scheduleScript = {CmdbScript script ->
            callParams.script = script
            callParams.time = System.nanoTime()

        }
        def unscheduleCallParams = [:]
        ScriptScheduler.metaClass.unscheduleScript = {String scriptName ->
            unscheduleCallParams.scriptName = scriptName
            unscheduleCallParams.time = System.nanoTime()
        }

        //testing for periodic script
        def updateParams = [name: "myscript", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.PERIODIC, scriptFile: simpleScriptFile, enabled: false, startDelay: 10, period: 20]
        def params = [name: "myscript", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.PERIODIC, scriptFile: simpleScriptFile, enabled: true, startDelay: 10, period: 20]
        def scriptToUpdate = CmdbScriptOperations.addScript(params);
        assertFalse(scriptToUpdate.hasErrors())

        callParams = [:];
        unscheduleCallParams = [:];
        assertEquals(callParams.size(), 0)
        assertEquals(unscheduleCallParams.size(), 0)
        def script = CmdbScriptOperations.updateScript(scriptToUpdate, updateParams, false)
        assertFalse(script.hasErrors())
        assertEquals(callParams.script.id, script.id)
        assertEquals(params.name, unscheduleCallParams.scriptName)
        assertTrue(unscheduleCallParams.time < callParams.time)

        //testing for cron script
        updateParams = [name: "cmyscript", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.CRON, scriptFile: simpleScriptFile, enabled: false, startDelay: 10, cronExpression: "* * * * * ?"]
        params = [name: "cmyscript", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.CRON, scriptFile: simpleScriptFile, enabled: true, startDelay: 10, cronExpression: "* * * * * ?"]
        scriptToUpdate = CmdbScriptOperations.addScript(params);
        assertFalse(scriptToUpdate.hasErrors())

        callParams = [:];
        unscheduleCallParams = [:];
        assertEquals(callParams.size(), 0)
        assertEquals(unscheduleCallParams.size(), 0)
        script = CmdbScriptOperations.updateScript(scriptToUpdate, updateParams, false)
        assertFalse(script.hasErrors())
        assertEquals(params.name, unscheduleCallParams.scriptName)
        assertEquals(callParams.script.id, script.id)
        assertTrue(unscheduleCallParams.time < callParams.time)
    }

    void testUpdateScriptCallsSchedulerUnschedulIfScriptTypeIsChanged()
    {
        initialize([CmdbScript, Group], []);
        initializeScriptManager()

        def callParams = [:]
        CmdbScriptOperations.metaClass.static.scheduleScript = {CmdbScript script ->
            callParams.script = script
            callParams.time = System.nanoTime()

        }
        def unscheduleCallParams = [:]
        ScriptScheduler.metaClass.unscheduleScript = {String scriptName ->
            unscheduleCallParams.scriptName = scriptName
            unscheduleCallParams.time = System.nanoTime()
        }

        //testing for periodic script
        def updateParams = [name: "myscript", type: CmdbScript.ONDEMAND, enabled:true]
        def params = [name: "myscript", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.PERIODIC, scriptFile: simpleScriptFile, enabled: true, startDelay: 10, period: 20]
        def scriptToUpdate = CmdbScriptOperations.addScript(params);
        assertFalse(scriptToUpdate.hasErrors())

        callParams = [:];
        unscheduleCallParams = [:];
        assertEquals(callParams.size(), 0)
        assertEquals(unscheduleCallParams.size(), 0)
        def script = CmdbScriptOperations.updateScript(scriptToUpdate, updateParams, false)
        assertFalse(script.hasErrors())
        assertEquals(callParams.script.id, script.id)
        assertEquals(params.name, unscheduleCallParams.scriptName)
        assertTrue(unscheduleCallParams.time < callParams.time)

        //testing for cron script
        updateParams = [name: "cmyscript", type: CmdbScript.ONDEMAND, enabled:true]
        params = [name: "cmyscript", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.CRON, scriptFile: simpleScriptFile, enabled: true, startDelay: 10, cronExpression: "* * * * * ?"]
        scriptToUpdate = CmdbScriptOperations.addScript(params);
        assertFalse(scriptToUpdate.hasErrors())

        callParams = [:];
        unscheduleCallParams = [:];
        assertEquals(callParams.size(), 0)
        assertEquals(unscheduleCallParams.size(), 0)
        script = CmdbScriptOperations.updateScript(scriptToUpdate, updateParams, false)
        assertFalse(script.hasErrors())
        assertEquals(params.name, unscheduleCallParams.scriptName)
        assertEquals(callParams.script.id, script.id)
        assertTrue(unscheduleCallParams.time < callParams.time)
    }
    void testCreateStaticParamMap() {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();



        CmdbScript script = CmdbScript.add(name: "script1", scriptFile: simpleScriptFile, staticParam: "x:5,y:6");
        assertFalse(script.hasErrors())

        def mapParams = CmdbScript.getStaticParamMap(script);
        assertEquals(mapParams.x, "5");
        assertEquals(mapParams.y, "6");
        assertEquals(mapParams.size(), 2);


        CmdbScript scriptWithNoStaticParam = CmdbScript.add(name: "script2", scriptFile: simpleScriptFile, staticParam: "");
        assertFalse(scriptWithNoStaticParam.hasErrors())

        def mapParams2 = CmdbScript.getStaticParamMap(scriptWithNoStaticParam);
        assertEquals(mapParams2.size(), 0);

        CmdbScript scriptWithCustomParam = CmdbScript.add(name: "script3", scriptFile: simpleScriptFile, staticParam: "xyz-dsfdfdf");
        assertFalse(scriptWithCustomParam.hasErrors())

        def mapParams3 = CmdbScript.getStaticParamMap(scriptWithCustomParam);
        assertEquals(mapParams3.size(), 0);

    }

    void testConfigureScriptLogger()
    {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();

        def logLevel = Level.DEBUG;
        def scriptParams = [:]
        scriptParams["name"] = "testscript";
        scriptParams["logFile"] = "testscript";
        scriptParams["logLevel"] = logLevel.toString();
        scriptParams["logFileOwn"] = false;


        CmdbScript script = CmdbScript.add(name: scriptParams.name, scriptFile: simpleScriptFile, logFile: scriptParams.logFile, logFileOwn: scriptParams.logFileOwn, logLevel: scriptParams.logLevel);
        assertFalse(script.hasErrors())

        def logger = null;

        CmdbScript.configureScriptLogger(script);
        logger = CmdbScript.getScriptLogger(script);
        assertEquals(logger.getLevel(), logLevel);
        assertFalse(logger.getAllAppenders().hasMoreElements());

        script.logFileOwn = true;
        script.logLevel = Level.INFO.toString();
        CmdbScript.configureScriptLogger(script);
        logger = CmdbScript.getScriptLogger(script);
        assertEquals(logger.getLevel(), Level.INFO);
        assertTrue(logger.getAllAppenders().hasMoreElements());
    }
    void testRemovingScriptObjectDestroysLogger()
    {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();

        def logLevel = Level.DEBUG;
        def scriptParams = [:]
        scriptParams["name"] = "testscript";
        scriptParams["logFile"] = "testscript";
        scriptParams["logLevel"] = logLevel.toString();
        scriptParams["logFileOwn"] = true;


        CmdbScript script = CmdbScript.add(name: scriptParams.name, scriptFile: simpleScriptFile, logFile: scriptParams.logFile, logFileOwn: scriptParams.logFileOwn, logLevel: scriptParams.logLevel);
        assertFalse(script.hasErrors())

        def logger = null;

        CmdbScript.configureScriptLogger(script);
        logger = CmdbScript.getScriptLogger(script);
        assertFalse(logger.getAdditivity());
        assertTrue(logger.getAllAppenders().hasMoreElements());

        script.remove();

        assertEquals(0,CmdbScript.count());

        assertTrue(logger.getAdditivity());
        assertFalse(logger.getAllAppenders().hasMoreElements());

        def modifiedLogger=CmdbScript.getScriptLogger(script);
        assertTrue(modifiedLogger.getAdditivity());
        assertFalse(modifiedLogger.getAllAppenders().hasMoreElements());

    }
    void testRenamingScriptObjectDestroysOldLogger()
    {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();

        ScriptScheduler.metaClass.unscheduleScript = {String scriptName ->
            
        }

        def logLevel = Level.DEBUG;
        def scriptParams = [:]
        scriptParams["name"] = "testscript";
        scriptParams["logFile"] = "testscript";
        scriptParams["logLevel"] = logLevel.toString();
        scriptParams["logFileOwn"] = true;


        CmdbScript script = CmdbScript.add(name: scriptParams.name, scriptFile: simpleScriptFile, logFile: scriptParams.logFile, logFileOwn: scriptParams.logFileOwn, logLevel: scriptParams.logLevel);
        assertFalse(script.hasErrors())

        CmdbScript.configureScriptLogger(script);
        def oldLogger = CmdbScript.getScriptLogger(script);
        assertEquals("scripting.OnDemand.testscript",oldLogger.name)
        assertFalse(oldLogger.getAdditivity());
        assertTrue(oldLogger.getAllAppenders().hasMoreElements());

        def updateParams=[:]
        updateParams["name"] = "testscript2";

        def updatedScript = CmdbScript.updateScript(script, updateParams, false);
        assertFalse(updatedScript.hasErrors());

        assertEquals("scripting.OnDemand.testscript",oldLogger.name)
        assertTrue(oldLogger.getAdditivity());
        assertFalse(oldLogger.getAllAppenders().hasMoreElements());
        
        def oldLoggerModified=Logger.getLogger(oldLogger.name);
        assertEquals("scripting.OnDemand.testscript",oldLoggerModified.name)
        assertTrue(oldLoggerModified.getAdditivity());
        assertFalse(oldLoggerModified.getAllAppenders().hasMoreElements());

        def newLogger=CmdbScript.getScriptLogger(updatedScript);
        assertEquals("scripting.OnDemand.testscript2",newLogger.name)
        assertFalse(newLogger.getAdditivity());
        assertTrue(newLogger.getAllAppenders().hasMoreElements());
    }
    void testGetScriptObject()
    {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();

        def managerParams = [:]
        ScriptManager.metaClass.getScriptObject = {scriptPath, bindings, scriptLogger, operationClass ->
            managerParams.scriptPath = scriptPath
            managerParams.bindings = bindings
            managerParams.scriptLogger = scriptLogger
            managerParams.operationClass = operationClass;

        }

        initializeForCmdbScript();

        ScriptManager.getInstance().addScript(simpleScriptFile);

        def script = CmdbScript.add(name: "testscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile, operationClass: "testclass");
        assertFalse(script.hasErrors())

        def params = ["param1": "1", "param2": "a"]
        def oldParams = [:]
        oldParams.putAll(params);


        def scriptObject = CmdbScript.getScriptObject(script, params)

        assertEquals(managerParams.scriptPath, script.scriptFile)
        assertEquals(managerParams.scriptLogger, CmdbScript.getScriptLogger(script))
        assertEquals(managerParams.operationClass, script.operationClass)
        assertEquals(managerParams.bindings.staticParam, script.staticParam)
        assertEquals(managerParams.bindings.staticParamMap, CmdbScript.getStaticParamMap(script))
        assertEquals(managerParams.bindings.size(), oldParams.size() + 4)

        oldParams.each {key, val ->
            assertEquals(val, managerParams.bindings[key])
        }
    }
    void testRunScriptPassesParamsStaticParamAndStaticParamMapToScript()
    {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();

        def scriptFile = "mytestscriptfile.groovy"
        def scriptContent = "return [params:params,staticParam:staticParam,staticParamMap:staticParamMap]"
        createScript(scriptFile, scriptContent);
        ScriptManager.getInstance().addScript(scriptFile)

        def onDemandScript = CmdbScript.add(name: "testscript", type: CmdbScript.ONDEMAND, scriptFile: scriptFile, staticParam: "x:5,y:6");
        assertFalse(onDemandScript.hasErrors())

        def params = [:]

        def result = CmdbScript.runScript(onDemandScript, params)
        assertTrue(result.params instanceof Map);
        assertEquals(0, result.params.size());
        assertEquals(result.staticParam, onDemandScript.staticParam)
        assertEquals(result.staticParamMap.x, "5")
        assertEquals(result.staticParamMap.y, "6")

        def scheduledScript = CmdbScript.add(name: "testscript", type: CmdbScript.SCHEDULED, scriptFile: scriptFile, staticParam: "x:7,y:8");
        assertFalse(scheduledScript.hasErrors())
        result = CmdbScript.runScript(scheduledScript, params)
        assertEquals(result.staticParam, scheduledScript.staticParam)
        assertEquals(result.staticParamMap.x, "7")
        assertEquals(result.staticParamMap.y, "8")

        def listeningScript = CmdbScript.add(name: "testscript", type: CmdbScript.LISTENING, scriptFile: scriptFile, staticParam: "x:10,y:11");
        assertFalse(listeningScript.hasErrors())
        result = CmdbScript.runScript(listeningScript, params)
        assertEquals(result.staticParam, listeningScript.staticParam)
        assertEquals(result.staticParamMap.x, "10")
        assertEquals(result.staticParamMap.y, "11")

    }
    void testRunScriptPassesParametersToScriptManager()
    {
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();

        def managerParams = [:]
        ScriptManager.metaClass.runScript = {scriptPath, bindings, scriptLogger, operationClass ->
            managerParams.scriptPath = scriptPath
            managerParams.bindings = bindings
            managerParams.scriptLogger = scriptLogger
            managerParams.operationClass = operationClass;
            return "myrunscript";
        }

        initializeForCmdbScript();

        ScriptManager.getInstance().addScript(simpleScriptFile);

        def script = CmdbScript.add(name: "testscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile, operationClass: "testclass");
        assertFalse(script.hasErrors())

        def params = ["param1": "1", "param2": "a"]
        def oldParams = [:]
        oldParams.putAll(params);


        def result = CmdbScript.runScript(script, params)
        assertEquals(result, "myrunscript")
        assertEquals(managerParams.scriptPath, script.scriptFile)
        assertEquals(managerParams.scriptLogger, CmdbScript.getScriptLogger(script))
        assertEquals(managerParams.operationClass, script.operationClass)
        assertEquals(managerParams.bindings.staticParam, script.staticParam)
        assertEquals(managerParams.bindings.staticParamMap, CmdbScript.getStaticParamMap(script))
        assertEquals(managerParams.bindings.size(), oldParams.size() + 4)

        oldParams.each {key, val ->
            assertEquals(val, managerParams.bindings[key])
        }

    }
    void testAddScriptCallsBaseWithFromControllerFalse()
    {
        def callParams = [:]
        CmdbScriptOperations.metaClass.static.addScript = {Map params, boolean fromController ->
            callParams.params = params
            callParams.fromController = fromController
        }
        def scriptParams = ["x": "a", "y": "b"]
        CmdbScriptOperations.addScript(scriptParams);

        assertEquals(callParams.fromController, false);
        assertEquals(callParams.params, scriptParams);
    }
    void testAddUniqueScriptCallsBaseWithFromControllerFalse()
    {
        def callParams = [:]
        CmdbScriptOperations.metaClass.static.addUniqueScript = {Map params, boolean fromController ->
            callParams.params = params
            callParams.fromController = fromController
        }
        def scriptParams = ["x": "a", "y": "b"]
        CmdbScriptOperations.addUniqueScript(scriptParams);

        assertEquals(callParams.fromController, false);
        assertEquals(callParams.params, scriptParams);
    }
    void testUpdateScriptWithOnlyParamsCallsBaseWithFromControllerFalse()
    {
        initialize([CmdbScript, Group], [])
        initializeScriptManager()


        def callParams = [:]
        CmdbScriptOperations.metaClass.static.updateScript = {CmdbScript script, Map params, boolean fromController ->
            callParams.params = params
            callParams.fromController = fromController
            callParams.script = script
        }

        def scriptParams = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]
        def script = CmdbScriptOperations.addScript(scriptParams);
        assertFalse(script.hasErrors());

        CmdbScriptOperations.updateScript(scriptParams);

        assertEquals(callParams.fromController, false);
        assertEquals(callParams.params, scriptParams);
        assertEquals(callParams.script.id, script.id);
    }
    void testRunScriptWithOnlyNameCallsBase() {

        def callParams = [:]
        CmdbScriptOperations.metaClass.static.runScript = {String scriptName, Map params ->
            callParams.scriptName = scriptName
            callParams.params = params
        }
        def scriptName = "testscript";
        CmdbScriptOperations.runScript(scriptName);
        assertEquals(callParams.scriptName, scriptName)
        assertEquals(callParams.params.size(), 0)
    }
    void testRunScriptWithNameAndParamsCallsBase() {
        initialize([CmdbScript, Group], [])
        initializeScriptManager()

        def callParams = [:]
        CmdbScriptOperations.metaClass.static.runScript = {CmdbScript script, Map params ->
            callParams.script = script
            callParams.params = params
        }

        def scriptParams = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]
        def script = CmdbScriptOperations.addScript(scriptParams);
        assertFalse(script.hasErrors())

        CmdbScriptOperations.runScript(script.name, scriptParams);

        assertEquals(callParams.script.name, script.name)
        assertEquals(callParams.script.id, script.id)
        assertEquals(callParams.params, scriptParams)

    }
    void testRunScriptWithNameAndParamsGeneratesExceptionWhenScriptIsMissing() {
        initialize([CmdbScript, Group], [])
        assertEquals(CmdbScript.count(), 0)
        try {
            CmdbScriptOperations.runScript("testscript", [:]);
            fail("should throw exception")
        }
        catch (e)
        {
            println e
            assertTrue(e.getMessage().indexOf("does not exist") > 0)
        }

    }
    void testUpdateScriptWithOnlyParamsGeneratesExceptionWhenScriptIsMissing() {
        initialize([CmdbScript, Group], [])
        assertEquals(CmdbScript.count(), 0)
        try {
            CmdbScriptOperations.updateScript([name: "testscript"]);
            fail("should throw exception")
        }
        catch (e)
        {
            println e
            assertTrue(e.getMessage().indexOf("does not exist") > 0)
        }
    }
    void testStartAndStopListeningWithNameGeneratesExceptionWhenScriptIsMissing() {
        initialize([CmdbScript, Group], [])
        assertEquals(CmdbScript.count(), 0)
        try {
            CmdbScriptOperations.startListening([name: "testscript"]);
            fail("should throw exception")
        }
        catch (e)
        {
            println e
            assertTrue(e.getMessage().indexOf("does not exist") > 0)
        }

        try {
            CmdbScriptOperations.stopListening([name: "testscript"]);
            fail("should throw exception")
        }
        catch (e)
        {
            println e
            assertTrue(e.getMessage().indexOf("does not exist") > 0)
        }
    }
    void testStartAndStopListeningWithNameAndParamsCallsBase() {
        initialize([CmdbScript, Group], [])
        initializeScriptManager()

        def callParams = [:]
        CmdbScriptOperations.metaClass.static.startListening = {CmdbScript script ->
            callParams.script = script

        }

        def callParams2 = [:]
        CmdbScriptOperations.metaClass.static.stopListening = {CmdbScript script ->
            callParams2.script = script

        }

        def scriptParams = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]
        def script = CmdbScriptOperations.addScript(scriptParams);
        assertFalse(script.hasErrors())

        CmdbScriptOperations.startListening(script.name);

        assertEquals(callParams.script.name, script.name)
        assertEquals(callParams.script.id, script.id)

        CmdbScriptOperations.stopListening(script.name);

        assertEquals(callParams2.script.name, script.name)
        assertEquals(callParams2.script.id, script.id)
    }

    void testStartAndStopListeningGeneratesExceptionIfListeningDatasourceIsMissing()
    {
        initialize([CmdbScript, Group], [])
        initializeScriptManager()

        def scriptParams = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]
        def script = CmdbScriptOperations.addScript(scriptParams);
        assertFalse(script.hasErrors())
        assertNull(script.listeningDatasource)

        try {
            CmdbScriptOperations.startListening(script);
            fail("should throw exception")
        }
        catch (e)
        {
            println e
            assertEquals(e.getMessage(), "No listening datasource defined")
        }

        try {
            CmdbScriptOperations.stopListening(script);
            fail("should throw exception")
        }
        catch (e)
        {
            println e
            assertEquals(e.getMessage(), "No listening datasource defined")
        }

    }
    void testStartListeningCallsBaseListeningDatasourceAndThrowsException()
    {
        initialize([CmdbScript, BaseListeningDatasource], [])
        initializeForCmdbScript();
        CompassForTests.addOperationSupport (BaseListeningDatasource,datasource.BaseListeningDatasourceOperations);

        def callParams=[:];
        def exceptionToThrow=null;
        BaseListeningDatasource.metaClass.startListening = {  ->
            callParams.listeningDatasource = domainObject;
            if ( exceptionToThrow != null)
            {
                callParams.exceptionToThrow=exceptionToThrow;
                throw exceptionToThrow;
            }
        }


        def ds = BaseListeningDatasource.add(name: "baseds", isSubscribed: false);
        assertFalse(ds.hasErrors())
        assertFalse(ds.isSubscribed)

        def scriptParams = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile, listeningDatasource: ds]
        def script = CmdbScriptOperations.addScript(scriptParams);
        assertFalse(script.hasErrors());
        assertNotNull(script.listeningDatasource);

        assertEquals(0,callParams.size());
        CmdbScriptOperations.startListening(script);
        assertEquals(script.listeningDatasource.id, callParams.listeningDatasource.id)


        callParams=[:];
        exceptionToThrow=new Exception();
        assertEquals(0,callParams.size());

        try{
            CmdbScriptOperations.startListening(script);
            fail("Should throw Exception");
        }
        catch(e)
        {
            assertEquals(callParams.exceptionToThrow,exceptionToThrow);
        }

    }

    void testStopListeningCallsBaseListeningDatasourceAndThrowsException()
    {
        initialize([CmdbScript, BaseListeningDatasource], [])
        initializeForCmdbScript();
        CompassForTests.addOperationSupport (BaseListeningDatasource,datasource.BaseListeningDatasourceOperations);

        def callParams=[:];
        def exceptionToThrow=null;
        BaseListeningDatasource.metaClass.stopListening = { ->
            callParams.listeningDatasource = domainObject;
            if ( exceptionToThrow != null)
            {
                callParams.exceptionToThrow=exceptionToThrow;
                throw exceptionToThrow;
            }
        }

        def ds = BaseListeningDatasource.add(name: "baseds", isSubscribed: true);
        assertFalse(ds.hasErrors())
        assertTrue(ds.isSubscribed)

        def scriptParams = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile, listeningDatasource: ds]
        def script = CmdbScriptOperations.addScript(scriptParams);
        assertFalse(script.hasErrors());
        assertNotNull(script.listeningDatasource);

        CmdbScriptOperations.stopListening(script);
        assertEquals(script.listeningDatasource.id, callParams.listeningDatasource.id) ;


        callParams=[:];
        exceptionToThrow=new Exception();
        assertEquals(0,callParams.size());

        try{
            CmdbScriptOperations.stopListening(script);
            fail("Should throw Exception");
        }
        catch(e)
        {
            assertEquals(callParams.exceptionToThrow,exceptionToThrow);
        }

    }
    void testDeleteScriptWithNameGeneratesExceptionWhenScriptIsMissing() {
        initialize([CmdbScript, Group], [])
        assertEquals(CmdbScript.count(), 0)
        try {
            CmdbScriptOperations.deleteScript("testscript");
            fail("should throw exception")
        }
        catch (e)
        {
            println e
            assertTrue(e.getMessage().indexOf("does not exist") > 0)
        }

    }
    void testDeleteScriptWithNameAndParamsCallsBase() {
        initialize([CmdbScript, Group], [])
        initializeScriptManager()

        def callParams = [:]
        CmdbScriptOperations.metaClass.static.deleteScript = {CmdbScript script ->
            callParams.script = script

        }


        def scriptParams = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]
        def script = CmdbScriptOperations.addScript(scriptParams);
        assertFalse(script.hasErrors())


        CmdbScriptOperations.deleteScript(script.name);

        assertEquals(callParams.script.name, script.name)
        assertEquals(callParams.script.id, script.id)
    }
    void testDeleteScript()
    {
        initialize([CmdbScript, BaseListeningDatasource], [])
        initializeScriptManager()
        def scriptParams = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]
        def scriptInstance = CmdbScriptOperations.addScript(scriptParams);
        assertFalse(scriptInstance.hasErrors())

        def unscheduleScriptName = null;
        ScriptScheduler.metaClass.unscheduleScript = {String scriptName ->
            unscheduleScriptName = scriptName
        }

        def callParams = [:]
        def exceptionToThrow;
        CmdbScriptOperations.metaClass.static.stopListening = {CmdbScript script ->
            callParams.script = script
            if(exceptionToThrow != null)
            {
                callParams.exceptionToThrow=exceptionToThrow;
                throw  exceptionToThrow;
            }

        }

        //we test that the script is deleted and unschedule called
        assertEquals(CmdbScript.count(), 1);
        CmdbScriptOperations.deleteScript(scriptInstance);
        assertEquals(CmdbScript.count(), 0);
        assertEquals(unscheduleScriptName, scriptInstance.name);


        //we test here if there is a listening datasource stopListening is called
        def ds = BaseListeningDatasource.add(name: "baseds", isSubscribed: true);
        assertFalse(ds.hasErrors())
        assertTrue(ds.isSubscribed)

        scriptParams = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile, listeningDatasource: ds]
        scriptInstance = CmdbScriptOperations.addScript(scriptParams);
        assertFalse(scriptInstance.hasErrors());
        assertNotNull(scriptInstance.listeningDatasource);

        unscheduleScriptName = null;

        assertEquals(CmdbScript.countHits("scriptFile:${scriptInstance.scriptFile}"), 1)
        assertEquals(CmdbScript.count(), 1);
        assertNotNull(ScriptManager.getInstance().getScript(scriptInstance.scriptFile))
        CmdbScriptOperations.deleteScript(scriptInstance);
        assertEquals(CmdbScript.count(), 0);
        assertEquals(unscheduleScriptName, scriptInstance.name);
        assertEquals(callParams.script.id, scriptInstance.id);

        //Now we also test that script is removed from script Manager
        assertEquals(CmdbScript.countHits("scriptFile:${scriptInstance.scriptFile}"), 0)
        assertNull(ScriptManager.getInstance().getScript(scriptInstance.scriptFile))


        // we test stoplistening exception is not thrown
        scriptInstance = CmdbScriptOperations.addScript(scriptParams);
        assertFalse(scriptInstance.hasErrors())

        callParams=[:];
        assertEquals(0,callParams.size());
        exceptionToThrow=new Exception();
        try
        {
            CmdbScriptOperations.deleteScript(scriptInstance);
            assertEquals(callParams.script.id, scriptInstance.id);
            assertEquals(callParams.exceptionToThrow,exceptionToThrow);
        }
        catch(e)
        {
            fail("should not throw exception");
        }

    }
    void testDeleteScriptDoesNotRemoveScriptFromScriptManagerIfUsed()
    {
        initialize([CmdbScript, BaseListeningDatasource], [])
        initializeScriptManager()
        def unscheduleScriptName = null;
        ScriptScheduler.metaClass.unscheduleScript = {String scriptName ->
            unscheduleScriptName = scriptName
        }

        def scriptParams = [name: "myscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]
        def scriptParams2 = [name: "myscript2", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]
        def scriptInstance = CmdbScriptOperations.addScript(scriptParams);
        def scriptInstance2 = CmdbScriptOperations.addScript(scriptParams2);
        assertFalse(scriptInstance.hasErrors())
        assertFalse(scriptInstance2.hasErrors())

        assertEquals(CmdbScript.count(), 2);
        assertNotNull(ScriptManager.getInstance().getScript(scriptInstance.scriptFile))
        assertEquals(CmdbScript.countHits("scriptFile:${scriptInstance.scriptFile}"), 2)
        CmdbScriptOperations.deleteScript(scriptInstance);
        assertEquals(unscheduleScriptName, scriptInstance.name);
        assertEquals(CmdbScript.count(), 1);


        //Now we also test that script is not removed from script Manager because its used
        assertEquals(CmdbScript.countHits("scriptFile:${scriptInstance.scriptFile}"), 1)
        assertNotNull(ScriptManager.getInstance().getScript(scriptInstance.scriptFile))

    }

    void testAddScriptGeneratesExceptionWhenErrorOccurs()
    {
        CmdbScriptOperations.metaClass.static.createScriptException = {CmdbScript script ->
            return new Exception("injectedTestMessage");

        }
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();


        def params = [name: "CmdbScriptOperationsTestScript", type: CmdbScript.ONDEMAND, scriptFile: "no_script_file"]

        try {
            CmdbScript.addScript(params);
            fail("should throw exception")
        }
        catch (e)
        {
            assertEquals(e.getMessage(), "injectedTestMessage");
        }

        try {
            CmdbScript.addScript(params, false);
            fail("should throw exception")
        }
        catch (e)
        {
            assertEquals(e.getMessage(), "injectedTestMessage");
        }
    }

    void testUpdateScriptsGeneratesExceptionWhenErrorOccurs()
    {
        CmdbScriptOperations.metaClass.static.createScriptException = {CmdbScript script ->
            return new Exception("injectedTestMessage");

        }
        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();


        def params = [name: "CmdbScriptOperationsTestScript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]
        def script = CmdbScript.addScript(params);
        assertFalse(script.hasErrors());


        try {
            def updateParams = [name: script.name, scriptFile: "no_script_file"];
            CmdbScript.updateScript(updateParams);
            fail("should throw exception")
        }
        catch (e)
        {
            assertEquals(e.getMessage(), "injectedTestMessage");
        }

        try {
            def updateParams = [scriptFile: "no_script_file"];
            CmdbScript.updateScript(script, updateParams, false);
            fail("should throw exception")
        }
        catch (e)
        {
            assertEquals(e.getMessage(), "injectedTestMessage");
        }

    }
   void testStopRunningScriptsCallsScriptStateManagerStopRunningScripts()
    {

        initialize([CmdbScript, Group], []);
        initializeForCmdbScript();

        def managerParams = [:]
        ScriptStateManager.metaClass.stopRunningScripts = {scriptName ->
            managerParams.scriptName = scriptName;

        }



        ScriptManager.getInstance().addScript(simpleScriptFile);

        def script = CmdbScript.add(name: "testscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile);
        assertFalse(script.hasErrors())
        assertFalse(script.scriptFile.compareTo(script.name)==0)

        CmdbScript.stopRunningScripts(script);

        assertEquals(script.name,managerParams.scriptName)

    }

    public void testRunScriptAndGetScriptObjectCallsScriptStateManagerAddStateParamToBindings()
       {
           initialize([CmdbScript, Group], []);
           initializeForCmdbScript();

           def managerParams = [:]
           ScriptStateManager.metaClass.addStateParamToBindings = {String scriptName,bindings ->
               managerParams.scriptName = scriptName;
               managerParams.bindings = bindings;

           }



           ScriptManager.getInstance().addScript(simpleScriptFile);

           def script = CmdbScript.add(name: "testscript", type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile);
           assertFalse(script.hasErrors())
           assertFalse(script.scriptFile.compareTo(script.name)==0)

           def bindings=[:];
           assertEquals(0,managerParams.size());

           CmdbScript.runScript(script,bindings);

           assertEquals(script.name,managerParams.scriptName)
           assertSame(bindings,managerParams.bindings)


           bindings.clear();
           managerParams.clear();
           assertEquals(0,managerParams.size());

           CmdbScript.getScriptObject(script,bindings);

           assertEquals(script.name,managerParams.scriptName)
           assertSame(bindings,managerParams.bindings)
       }
       public void testStopMechanismWorksForScripts()
       {
           initialize([CmdbScript, Group], []);
           initializeForCmdbScript();


           def scriptName = "script2";
           createScript(scriptName+".groovy","""
               import com.ifountain.rcmdb.test.util.TestDatastore
               iterationCount=0;
               while(!IS_STOPPED())
               {
                   println "itr :"+iterationCount
                   Thread.sleep(100);
                   iterationCount++;
               }
               println "stopped in script"
               TestDatastore.put("cmdb_script2_iterationCount",iterationCount);
               TestDatastore.put("cmdb_script2_IS_STOPPED",IS_STOPPED());
           """);
           def script=CmdbScript.addScript(name:scriptName)
           assertFalse(script.hasErrors());

           //script2 should wait until stop is called

           def runnerThread=Thread.start(){
              CmdbScript.runScript(script,[:]);
           }
           assertEquals(null,TestDatastore.get("cmdb_script2_IS_STOPPED"))
           assertEquals(null,TestDatastore.get("cmdb_script2_iterationCount"))

           Thread.sleep(250);
           CmdbScript.stopRunningScripts (script);
           runnerThread.join();
           assertEquals(true,TestDatastore.get("cmdb_script2_IS_STOPPED"))
           assertTrue(TestDatastore.get("cmdb_script2_iterationCount")>0)
       }
        void testManageRepositoryDatasourceAddsRepositoryDatasourceIfListenToParameterIsProvided() {
        initialize([CmdbScript, Group, RepositoryDatasource, RepositoryConnection, BaseListeningDatasource], []);
        initializeForCmdbScript();
        def repoConn = RepositoryConnection.add(name:RepositoryConnection.RCMDB_REPOSITORY)
        def params = [name: "myscript", enabledForAllGroups: true, type: CmdbScript.LISTENING, scriptFile: simpleScriptFile]
        CmdbScript script = CmdbScript.addScript(params)
        assertNull(script.listeningDatasource);

        CmdbScript.manageRepositoryDatasource(script,true);
        assertNotNull(script.listeningDatasource);
        def ds = script.listeningDatasource;
        assertEquals("AutoGeneratedRepositoryDatasource${script.id}", ds.name)
        assertEquals(RepositoryConnection.RCMDB_REPOSITORY, ds.connection.name)
    }

    void testManageRepositoryDatasourceWillNotAddIfTheScriptIsNotListening() {
        initialize([CmdbScript, Group, RepositoryDatasource, RepositoryConnection, BaseListeningDatasource], []);
        initializeForCmdbScript();
        def repoConn = RepositoryConnection.add(name:RepositoryConnection.RCMDB_REPOSITORY)
        def params = [name: "myscript", enabledForAllGroups: true, type: CmdbScript.ONDEMAND, scriptFile: simpleScriptFile]
        CmdbScript script = CmdbScript.addScript(params)
        assertNull(script.listeningDatasource);

        CmdbScript.manageRepositoryDatasource(script,true);
        assertNull(script.listeningDatasource);
    }

    def createSimpleScript(scriptName, scriptMessage)
    {
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write("""return "$scriptMessage" """);
    }
    def createScript(scriptName, scriptContent)
    {
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write(scriptContent);
    }
}