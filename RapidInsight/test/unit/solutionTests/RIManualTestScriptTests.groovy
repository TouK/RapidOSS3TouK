package solutionTests

import application.RapidApplication
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.core.connection.ConnectionManager
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.datasource.AdapterStateProvider
import com.ifountain.core.test.util.DatasourceTestUtils
import com.ifountain.rcmdb.datasource.ListeningAdapterManager
import com.ifountain.rcmdb.domain.connection.RepositoryConnectionImpl
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidApplicationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import connection.RepositoryConnection
import datasource.BaseListeningDatasource
import datasource.BaseListeningDatasourceOperations
import datasource.RepositoryDatasource
import datasource.RepositoryDatasourceOperations
import org.apache.commons.io.FileUtils
import script.CmdbScript
import script.CmdbScriptOperations

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 20, 2009
* Time: 5:35:46 PM
* To change this template use File | Settings | File Templates.
*/
class RIManualTestScriptTests extends RapidCmdbWithCompassTestCase {
    def base_directory = "";
    def script_manager_directory = "../testoutput/";
    def script_directory;
    def classes;

    def RsTopologyObject;
    def RsCustomer;
    def RsEvent;
    def RsRiEvent;
    def RsGroup;
    def RsService;
    def RsObjectState;
    def RsEventJournal;
    def RsHistoricalEvent;
    def RsHeartBeat;

    def RsEventOperations;
    def RsRiEventOperations;
    def RsHistoricalEventOperations;
    def RsEventJournalOperations;
    def RsTopologyObjectOperations;

    public void setUp() {
        super.setUp();
        clearMetaClasses()

        ["RsTopologyObject", "RsCustomer", "RsEvent","RsRiEvent", "RsGroup", "RsService", "RsObjectState","RsEventJournal", "RsHistoricalEvent","RsHeartBeat"].each{ className ->
              setProperty(className,gcl.loadClass(className));
        }

        ["RsEventOperations", "RsRiEventOperations","RsHistoricalEventOperations","RsEventJournalOperations", "RsTopologyObjectOperations"].each{ className ->
              setProperty(className,gcl.loadClass(className));
        }


        classes = [:];
        base_directory = getWorkspacePath()+"/RapidModules/RapidInsight";
        initializeScriptManager();

    }

    public void tearDown() {
        if (classes.StateCalculator != null)
        {
            classes.StateCalculator.setToDefault();
        }
        RapidApplicationTestUtils.clearProcessors();
        RapidApplicationTestUtils.clearUtilityPaths();
        ListeningAdapterManager.getInstance().destroyInstance();
        if(ConnectionManager.isInitialized()){
            ConnectionManager.destroy();
        }

        clearMetaClasses()
        super.tearDown();

    }
     public static void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(CmdbScript);
        ExpandoMetaClass.enableGlobally();
    }

    void initializeScriptManager()
    {
        //def script_base_directory= base_directory+"/test/manualTestScripts/${script_directory}";
        //println "script base path is :"+new File(script_base_directory).getCanonicalPath();

        ScriptManager manager = ScriptManager.getInstance();
        if (new File(script_manager_directory).exists())
        {
            FileUtils.deleteDirectory(new File(script_manager_directory));
        }
        manager.initialize(this.class.getClassLoader(), script_manager_directory, [:]);
        script_directory = "$script_manager_directory/$ScriptManager.SCRIPT_DIRECTORY";
        new File(script_directory).mkdirs();
    }
    public void initializeModels(classMap)
    {
        initialize([CmdbScript, RepositoryConnection, RepositoryDatasource, BaseListeningDatasource, RsTopologyObject, RsCustomer, RsEvent, RsGroup, RsService, RsObjectState, relation.Relation, RsEventJournal, RsHistoricalEvent, RapidApplication], []);
        CompassForTests.addOperationSupport(CmdbScript, CmdbScriptOperations);
        CompassForTests.addOperationSupport(RsEvent, RsEventOperations);
        CompassForTests.addOperationSupport(RsTopologyObject, classMap.RsTopologyObjectOperations);
        CompassForTests.addOperationSupport(RsGroup, classMap.RsGroupOperations);
        CompassForTests.addOperationSupport(RsCustomer, classMap.RsCustomerOperations);
        CompassForTests.addOperationSupport(RsService, classMap.RsServiceOperations);
        CompassForTests.addOperationSupport(RsEventJournal, RsEventJournalOperations);
        CompassForTests.addOperationSupport(RsHistoricalEvent, RsHistoricalEventOperations);
        CompassForTests.addOperationSupport(RepositoryDatasource, RepositoryDatasourceOperations);
        CompassForTests.addOperationSupport(BaseListeningDatasource, BaseListeningDatasourceOperations);
        classes.StateCalculator = classMap.StateCalculator;
        RapidApplicationTestUtils.initializeRapidApplicationOperations(RapidApplication);
    }
    void copyManualTestScript(scriptFolder, scriptName)
    {
        def scriptPath = "${base_directory}/test/manualTestScripts/${scriptFolder}/${scriptName}.groovy";

        def ant = new AntBuilder();

        ant.copy(file: scriptPath, toDir: script_directory, overwrite: true);
    }

    void copyScript(folderName, scriptName)
    {
        def scriptPath = "${base_directory}/solutions/${folderName}/scripts/${scriptName}.groovy";

        def ant = new AntBuilder();

        ant.copy(file: scriptPath, toDir: script_directory, overwrite: true);
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
        classMap.StateCalculator = loader.parseClass(getOperationPathAsFile("RI", "solutions/stateCalculation/operations", "StateCalculator"));
        classMap.RsTopologyObjectOperations = loader.parseClass(getOperationPathAsFile("RI", "solutions/stateCalculation/operations", "RsTopologyObjectOperations"));
        classMap.RsGroupOperations = loader.parseClass(getOperationPathAsFile("RI", "operations", "RsGroupOperations"));
        classMap.RsCustomerOperations = loader.parseClass(getOperationPathAsFile("RI", "operations", "RsCustomerOperations"));
        classMap.RsServiceOperations = loader.parseClass(getOperationPathAsFile("RI", "operations", "RsServiceOperations"));

        RapidApplicationTestUtils.utilityPaths = ["StateCalculator": getOperationPathAsFile("RI", "solutions/stateCalculation/operations", "StateCalculator")];
        initializeModels(classMap)

        RapidApplication.getUtility("EventProcessor").afterProcessors = ["StateCalculator"];
        RapidApplication.getUtility("ObjectProcessor").afterProcessors = ["StateCalculator"];


        copyManualTestScript("stateCalculation", "findMaxTest");

        def script = CmdbScript.addScript([name: "findMaxTest", scriptFile: "findMaxTest.groovy", type: CmdbScript.ONDEMAND], true)
        println script.errors
        assertFalse(script.hasErrors());

        try {
            def result = CmdbScript.runScript(script, [:]);
        }
        catch (e)
        {
            fail("Error in script. Reason ${e}");
        }
    }

    public void testFindMaxTestWithAsynchroniousListeningMechanism()
    {
        def classMap = [:];

        GroovyClassLoader loader = new GroovyClassLoader();
        classMap.StateCalculator = loader.parseClass(getOperationPathAsFile("RI", "solutions/stateCalculation/operations", "StateCalculator"));
        classMap.RsTopologyObjectOperations = loader.parseClass(getOperationPathAsFile("RI", "solutions/stateCalculation/operations", "RsTopologyObjectOperations"));
        classMap.RsGroupOperations = loader.parseClass(getOperationPathAsFile("RI", "operations", "RsGroupOperations"));
        classMap.RsCustomerOperations = loader.parseClass(getOperationPathAsFile("RI", "operations", "RsCustomerOperations"));
        classMap.RsServiceOperations = loader.parseClass(getOperationPathAsFile("RI", "operations", "RsServiceOperations"));

        RapidApplicationTestUtils.utilityPaths = ["StateCalculator": getOperationPathAsFile("RI", "solutions/stateCalculation/operations", "StateCalculator")];
        initializeModels(classMap)
        copyManualTestScript("stateCalculation", "findMaxTest");
        copyScript("stateCalculation", "stateCalculationListeningScript");

        def connectionName = "repoConn"
        ConnectionManager.initialize(TestLogUtils.log, DatasourceTestUtils.getParamSupplier(), Thread.currentThread().getContextClassLoader(), 1000);
        DatasourceTestUtils.getParamSupplier().setParam(getConnectionParam(connectionName));
        ListeningAdapterManager.getInstance().initialize();



        def conn = RepositoryConnection.add(name: RepositoryConnection.RCMDB_REPOSITORY);
        def listeningscript = CmdbScript.addScript([name: "stateCalculationListeningScript", type: CmdbScript.LISTENING, listenToRepository: true,logFileOwn:true], true)
        def script = CmdbScript.addScript([name: "findMaxTest", type: CmdbScript.ONDEMAND], true)



        CmdbScript.startListening(listeningscript);
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(AdapterStateProvider.STARTED, ListeningAdapterManager.getInstance().getState(listeningscript.listeningDatasource));
        }))

        try {
            def result = CmdbScript.runScript(script, [:]);
        }
        catch (e)
        {
            fail("Error in script. Reason ${e}");
        }
        finally {
            CmdbScript.stopListening(listeningscript);
        }

    }

    private ConnectionParam getConnectionParam(connectionName) {
        return new ConnectionParam(connectionName, RepositoryConnectionImpl.class.getName(), [:], 10, 1000, 0);
    }

    public void testCriticalPercentTest()
    {
        def classMap = [:];

        GroovyClassLoader loader = new GroovyClassLoader();
        classMap.StateCalculator = loader.parseClass(getOperationPathAsFile("RI", "solutions/stateCalculation/operations", "StateCalculator"));
        classMap.RsTopologyObjectOperations = loader.parseClass(getOperationPathAsFile("RI", "solutions/stateCalculation/operations", "RsTopologyObjectOperations"));
        classMap.RsGroupOperations = loader.parseClass(getOperationPathAsFile("RI", "operations", "RsGroupOperations"));
        classMap.RsCustomerOperations = loader.parseClass(getOperationPathAsFile("RI", "operations", "RsCustomerOperations"));
        classMap.RsServiceOperations = loader.parseClass(getOperationPathAsFile("RI", "operations", "RsServiceOperations"));

        RapidApplicationTestUtils.utilityPaths = ["StateCalculator": getOperationPathAsFile("RI", "solutions/stateCalculation/operations", "StateCalculator")];
        initializeModels(classMap)

        RapidApplication.getUtility("EventProcessor").afterProcessors = ["StateCalculator"];
        RapidApplication.getUtility("ObjectProcessor").afterProcessors = ["StateCalculator"];




        copyManualTestScript("stateCalculation", "criticalPercentTest");

        def script = CmdbScript.addScript([name: "criticalPercentTest", scriptFile: "criticalPercentTest.groovy", type: CmdbScript.ONDEMAND], true)
        assertFalse(script.hasErrors());

        try {
            def result = CmdbScript.runScript(script, [:]);
        }
        catch (e)
        {
            fail("Error in script. Reason ${e}");
        }
    }

    public void testCriticalPercentWithAsynchroniousListeningMechanism()
    {
        def classMap = [:];

        GroovyClassLoader loader = new GroovyClassLoader();
        classMap.StateCalculator = loader.parseClass(getOperationPathAsFile("RI", "solutions/stateCalculation/operations", "StateCalculator"));
        classMap.RsTopologyObjectOperations = loader.parseClass(getOperationPathAsFile("RI", "solutions/stateCalculation/operations", "RsTopologyObjectOperations"));
        classMap.RsGroupOperations = loader.parseClass(getOperationPathAsFile("RI", "operations", "RsGroupOperations"));
        classMap.RsCustomerOperations = loader.parseClass(getOperationPathAsFile("RI", "operations", "RsCustomerOperations"));
        classMap.RsServiceOperations = loader.parseClass(getOperationPathAsFile("RI", "operations", "RsServiceOperations"));

        RapidApplicationTestUtils.utilityPaths = ["StateCalculator": getOperationPathAsFile("RI", "solutions/stateCalculation/operations", "StateCalculator")];
        initializeModels(classMap)
        copyManualTestScript("stateCalculation", "criticalPercentTest");
        copyScript("stateCalculation", "stateCalculationListeningScript");

        def connectionName = "repoConn"
        ConnectionManager.initialize(TestLogUtils.log, DatasourceTestUtils.getParamSupplier(), Thread.currentThread().getContextClassLoader(), 1000);
        DatasourceTestUtils.getParamSupplier().setParam(getConnectionParam(connectionName));
        ListeningAdapterManager.getInstance().initialize();



        def conn = RepositoryConnection.add(name: RepositoryConnection.RCMDB_REPOSITORY);
        def listeningscript = CmdbScript.addScript([name: "stateCalculationListeningScript", type: CmdbScript.LISTENING, listenToRepository: true], true)
        def script = CmdbScript.addScript([name: "criticalPercentTest", type: CmdbScript.ONDEMAND], true)



        CmdbScript.startListening(listeningscript);
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(AdapterStateProvider.STARTED, ListeningAdapterManager.getInstance().getState(listeningscript.listeningDatasource));
        }))

        try {
            def result = CmdbScript.runScript(script, [:]);
        }
        catch (e)
        {
            fail("Error in script. Reason ${e}");
        }
        finally {
            CmdbScript.stopListening(listeningscript);
        }

    }


    public void testHeartBeatTest()
    {
        def classMap = [:];
        GroovyClassLoader loader = new GroovyClassLoader();

        classMap.RsHeartBeatOperations = loader.parseClass(getOperationPathAsFile("RI", "solutions/heartbeat/operations", "RsHeartBeatOperations"));


        initialize([CmdbScript, RsEvent, RsHeartBeat, RsEventJournal, RsHistoricalEvent, RapidApplication, RsTopologyObject], []);
        CompassForTests.addOperationSupport(CmdbScript, CmdbScriptOperations);
        CompassForTests.addOperationSupport(RsEvent, RsEventOperations);
        CompassForTests.addOperationSupport(RsHeartBeat, classMap.RsHeartBeatOperations);
        CompassForTests.addOperationSupport(RsEventJournal, RsEventJournalOperations);
        CompassForTests.addOperationSupport(RsHistoricalEvent, RsHistoricalEventOperations);
        RapidApplicationTestUtils.initializeRapidApplicationOperations(RapidApplication);
        CompassForTests.addOperationSupport(RsTopologyObject, RsTopologyObjectOperations);
        RapidApplicationTestUtils.clearProcessors();

        copyManualTestScript("Heartbeat", "HeartbeatTest");

        def script = CmdbScript.addScript([name: "HeartbeatTest", scriptFile: "HeartbeatTest.groovy", type: CmdbScript.ONDEMAND], true)
        println script.errors
        assertFalse(script.hasErrors());

        try {
            def result = CmdbScript.runScript(script, [:]);
        }
        catch (e)
        {
            e.printStackTrace();
            fail("Error in script. Reason ${e}");

        }
    }
}

