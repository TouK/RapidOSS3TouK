
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.scripting.ScriptManager
import script.CmdbScript
import script.CmdbScriptOperations
import org.apache.commons.io.FileUtils
import com.ifountain.rcmdb.test.util.RsUtilityTestUtils


/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 20, 2009
* Time: 5:35:46 PM
* To change this template use File | Settings | File Templates.
*/
class RIManualTestScriptTests extends RapidCmdbWithCompassTestCase {
    def base_directory="";
    def script_manager_directory="../testoutput/";
    def script_directory;
    def classes;

    public void setUp() {
        super.setUp();
        classes=[:];
          //to run in Hudson
        base_directory = "../../../RapidModules/RapidInsight";
        def canonicalPath=new File(".").getCanonicalPath();
        //to run in developer pc
        if(canonicalPath.endsWith("RapidModules"))
        {
            base_directory = "RapidInsight";
        }
        initializeScriptManager();


    }

    public void tearDown() {
        if(classes.StateCalculator!=null)
        {
            classes.StateCalculator.setToDefault();
        }
        RsUtilityTestUtils.setToDefaultProcessors();
        RsUtilityTestUtils.clearUtilityPaths();
        super.tearDown();

    }
    void initializeScriptManager()
    {
        //def script_base_directory= base_directory+"/test/manualTestScripts/${script_directory}";
        //println "script base path is :"+new File(script_base_directory).getCanonicalPath();

        ScriptManager manager = ScriptManager.getInstance();
        if(new File(script_manager_directory).exists())
        {
            FileUtils.deleteDirectory (new File(script_manager_directory));
        }
        manager.initialize(this.class.getClassLoader(), script_manager_directory, [], [:]);
        script_directory="$script_manager_directory/$ScriptManager.SCRIPT_DIRECTORY";
        new File(script_directory).mkdirs();
    }
    public void initializeModels(classMap)
    {
        initialize([CmdbScript,RsTopologyObject,RsCustomer,RsEvent,RsGroup,RsService,RsObjectState,relation.Relation,RsInMaintenance,RsEventJournal,RsHistoricalEvent,RsUtility], []);
        CompassForTests.addOperationSupport (CmdbScript,CmdbScriptOperations);
        CompassForTests.addOperationSupport (RsEvent,RsEventOperations);
        CompassForTests.addOperationSupport (RsTopologyObject,classMap.RsTopologyObjectOperations);
        CompassForTests.addOperationSupport (RsGroup,classMap.RsGroupOperations);
        CompassForTests.addOperationSupport (RsCustomer,classMap.RsCustomerOperations);
        CompassForTests.addOperationSupport (RsService,classMap.RsServiceOperations);
        CompassForTests.addOperationSupport (RsInMaintenance,RsInMaintenanceOperations);
        CompassForTests.addOperationSupport (RsEventJournal,RsEventJournalOperations);
        CompassForTests.addOperationSupport (RsHistoricalEvent,RsHistoricalEventOperations);
        classes.StateCalculator=classMap.StateCalculator;
        RsUtilityTestUtils.initializeRsUtilityOperations(RsUtility);
   }
    void copyManualTestScript(scriptFolder,scriptName)
    {
        def scriptPath= "${base_directory}/test/manualTestScripts/${scriptFolder}/${scriptName}.groovy";

        def ant=new AntBuilder();

        ant.copy(file: scriptPath, toDir: script_directory,overwrite:true);
    }

     void copyScript(scriptName)
    {
        def scriptPath= "${base_directory}/scripts/${scriptName}.groovy";

        def ant=new AntBuilder();

        ant.copy(file: scriptPath, toDir: script_directory,overwrite:true);
    }
    public File getOperationPathAsFile(fromPlugin, opdir, opfile)
    {
        def plugin_base_dir = "${base_directory}";
        return new File("${plugin_base_dir}/${opdir}/${opfile}.groovy");
    }
    
    public void testFindMaxTest()
    {
        def classMap = [:];

        GroovyClassLoader loader = new GroovyClassLoader();
        classMap.StateCalculator = loader.parseClass(getOperationPathAsFile("RI", "solutions/statecalculation/operations", "StateCalculator"));
        classMap.RsTopologyObjectOperations = loader.parseClass(getOperationPathAsFile("RI", "solutions/statecalculation/operations", "RsTopologyObjectOperations"));
        classMap.RsGroupOperations=loader.parseClass(getOperationPathAsFile("RI", "operations", "RsGroupOperations"));
        classMap.RsCustomerOperations=loader.parseClass(getOperationPathAsFile("RI", "operations", "RsCustomerOperations"));
        classMap.RsServiceOperations=loader.parseClass(getOperationPathAsFile("RI", "operations", "RsServiceOperations"));

        RsUtilityTestUtils.utilityPaths=["StateCalculator":getOperationPathAsFile("RI", "solutions/statecalculation/operations", "StateCalculator")];
        initializeModels(classMap)

        RsUtility.getUtility("EventProcessor").afterProcessors=["StateCalculator"];
        RsUtility.getUtility("ObjectProcessor").afterProcessors=["StateCalculator"];


        copyManualTestScript("stateCalculation","findMaxTest");

        def script=CmdbScript.addScript([name:"findMaxTest",scriptFile:"findMaxTest.groovy",type: CmdbScript.ONDEMAND],true)
        println script.errors
        assertFalse(script.hasErrors());

        try{
            def result=CmdbScript.runScript(script,[:]);
        }
        catch(e)
        {
            fail("Error in script. Reason ${e}");
        }
    }

    public void testCriticalPercentTest()
    {
        def classMap = [:];

        GroovyClassLoader loader = new GroovyClassLoader();
        classMap.StateCalculator = loader.parseClass(getOperationPathAsFile("RI", "solutions/statecalculation/operations", "StateCalculator"));
        classMap.RsTopologyObjectOperations = loader.parseClass(getOperationPathAsFile("RI", "solutions/statecalculation/operations", "RsTopologyObjectOperations"));
        classMap.RsGroupOperations=loader.parseClass(getOperationPathAsFile("RI", "operations", "RsGroupOperations"));
        classMap.RsCustomerOperations=loader.parseClass(getOperationPathAsFile("RI", "operations", "RsCustomerOperations"));
        classMap.RsServiceOperations=loader.parseClass(getOperationPathAsFile("RI", "operations", "RsServiceOperations"));

        RsUtilityTestUtils.utilityPaths=["StateCalculator":getOperationPathAsFile("RI", "solutions/statecalculation/operations", "StateCalculator")];
        initializeModels(classMap)

        RsUtility.getUtility("EventProcessor").afterProcessors=["StateCalculator"];
        RsUtility.getUtility("ObjectProcessor").afterProcessors=["StateCalculator"];




        copyManualTestScript("stateCalculation","criticalPercentTest");

        def script=CmdbScript.addScript([name:"criticalPercentTest",scriptFile:"criticalPercentTest.groovy",type: CmdbScript.ONDEMAND],true)
        assertFalse(script.hasErrors());

        try{
            def result=CmdbScript.runScript(script,[:]);
        }
        catch(e)
        {
            fail("Error in script. Reason ${e}");
        }
    }

    public void testRsEventOperations()
    {
        initialize([CmdbScript,RsEvent,RsRiEvent,RsHistoricalEvent,RsEventJournal,RsTopologyObject,RsUtility], []);
        CompassForTests.addOperationSupport (CmdbScript,CmdbScriptOperations);
        CompassForTests.addOperationSupport (RsEvent,RsEventOperations);
        CompassForTests.addOperationSupport (RsRiEvent,RsRiEventOperations);
        CompassForTests.addOperationSupport (RsHistoricalEvent,RsHistoricalEventOperations);
        CompassForTests.addOperationSupport (RsEventJournal,RsEventJournalOperations);
        CompassForTests.addOperationSupport (RsTopologyObject,RsTopologyObjectOperations);
        RsUtilityTestUtils.initializeRsUtilityOperations(RsUtility);
        RsUtilityTestUtils.clearProcessors();



        copyManualTestScript("operationTests","rsEventOperationsTestScript");

        def script=CmdbScript.addScript([name:"rsEventOperationsTestScript",scriptFile:"rsEventOperationsTestScript.groovy",type: CmdbScript.ONDEMAND],true)
        println script.errors
        assertFalse(script.hasErrors());

        try{
            def result=CmdbScript.runScript(script,[:]);
        }
        catch(e)
        {               
            fail("Error in script. Reason ${e}");

        }
    }

    public void testMaintenanceTest()
    {
        initialize([CmdbScript,RsEvent,RsTopologyObject,RsInMaintenance,RsInMaintenanceSchedule,RsUtility], []);
        CompassForTests.addOperationSupport (CmdbScript,CmdbScriptOperations);
        CompassForTests.addOperationSupport (RsEvent,RsEventOperations);
        CompassForTests.addOperationSupport (RsTopologyObject,RsTopologyObjectOperations);
        CompassForTests.addOperationSupport (RsInMaintenance,RsInMaintenanceOperations);
        CompassForTests.addOperationSupport (RsInMaintenanceSchedule,RsInMaintenanceScheduleOperations);
        RsUtilityTestUtils.initializeRsUtilityOperations(RsUtility);

        RsUtility.getUtility("EventProcessor").beforeProcessors=["InMaintenanceCalculator"];

        copyScript("MaintenanceScheduler");
        copyManualTestScript("maintenance","MaintenanceTest");

        def script=CmdbScript.addScript([name:"MaintenanceTest",scriptFile:"MaintenanceTest.groovy",type: CmdbScript.ONDEMAND],true)
        println script.errors
        assertFalse(script.hasErrors());

        def maintScript=CmdbScript.addScript([name:"MaintenanceScheduler",type: CmdbScript.ONDEMAND],true)
        println maintScript.errors
        assertFalse(maintScript.hasErrors());

        try{
            def result=CmdbScript.runScript(script,[:]);
        }
        catch(e)
        {
            e.printStackTrace();
            fail("Error in script. Reason ${e}");

        }
    }
    public void testMaintenanceScheduleTest()
    {
        RsUtilityTestUtils.utilityPaths=["StateCalculator":getOperationPathAsFile("RI", "solutions/statecalculation/operations", "StateCalculator")];

        initialize([CmdbScript,RsEvent,RsTopologyObject,RsInMaintenance,RsInMaintenanceSchedule,RsUtility], []);
        CompassForTests.addOperationSupport (CmdbScript,CmdbScriptOperations);
        CompassForTests.addOperationSupport (RsEvent,RsEventOperations);
        CompassForTests.addOperationSupport (RsTopologyObject,RsTopologyObjectOperations);
        CompassForTests.addOperationSupport (RsInMaintenance,RsInMaintenanceOperations);
        CompassForTests.addOperationSupport (RsInMaintenanceSchedule,RsInMaintenanceScheduleOperations);
        RsUtilityTestUtils.initializeRsUtilityOperations(RsUtility);

        RsUtility.getUtility("EventProcessor").beforeProcessors=["InMaintenanceCalculator"];

        copyScript("MaintenanceScheduler");
        copyManualTestScript("maintenance","MaintenanceScheduleTest");

        def script=CmdbScript.addScript([name:"MaintenanceScheduleTest",type: CmdbScript.ONDEMAND],true)
        println script.errors
        assertFalse(script.hasErrors());

        def maintScript=CmdbScript.addScript([name:"MaintenanceScheduler",type: CmdbScript.ONDEMAND],true)
        println maintScript.errors
        assertFalse(maintScript.hasErrors());

        try{
            def result=CmdbScript.runScript(script,[:]);
        }
        catch(e)
        {
            e.printStackTrace();
            fail("Error in script. Reason ${e}");

        }
    }
    
    public void testHeartBeatTest()
    {
        initialize([CmdbScript,RsEvent,RsHeartBeat,RsEventJournal,RsHistoricalEvent,RsUtility,RsTopologyObject], []);
        CompassForTests.addOperationSupport (CmdbScript,CmdbScriptOperations);
        CompassForTests.addOperationSupport (RsEvent,RsEventOperations);
        CompassForTests.addOperationSupport (RsHeartBeat,RsHeartBeatOperations);
        CompassForTests.addOperationSupport (RsEventJournal,RsEventJournalOperations);
        CompassForTests.addOperationSupport (RsHistoricalEvent,RsHistoricalEventOperations);
        RsUtilityTestUtils.initializeRsUtilityOperations(RsUtility);
        CompassForTests.addOperationSupport (RsTopologyObject,RsTopologyObjectOperations);
        RsUtilityTestUtils.clearProcessors();

        copyManualTestScript("Heartbeat","HeartbeatTest");

        def script=CmdbScript.addScript([name:"HeartbeatTest",scriptFile:"HeartbeatTest.groovy",type: CmdbScript.ONDEMAND],true)
        println script.errors
        assertFalse(script.hasErrors());

        try{
            def result=CmdbScript.runScript(script,[:]);
        }
        catch(e)
        {
            e.printStackTrace();
            fail("Error in script. Reason ${e}");

        }
    }
}

