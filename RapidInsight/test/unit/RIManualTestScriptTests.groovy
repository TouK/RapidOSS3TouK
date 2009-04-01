
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.scripting.ScriptManager
import script.CmdbScript
import script.CmdbScriptOperations
import com.ifountain.comp.test.util.logging.TestLogUtils
import org.codehaus.groovy.grails.compiler.GrailsClassLoader
import com.ifountain.rcmdb.util.RapidDateUtilities
import com.ifountain.rcmdb.converter.*

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 20, 2009
* Time: 5:35:46 PM
* To change this template use File | Settings | File Templates.
*/
class RIManualTestScriptTests extends RapidCmdbWithCompassTestCase {
    def base_directory="";
    public void setUp() {
        super.setUp();
          //to run in Hudson
        base_directory = "../../../RapidModules/RapidInsight";
        def canonicalPath=new File(".").getCanonicalPath();
        //to run in developer pc
        if(canonicalPath.endsWith("RapidModules"))
        {
            base_directory = "RapidInsight";
        }
        RapidDateUtilities.registerDateUtils();
        registerDefaultConverters();

    }
     def registerDefaultConverters()
    {
        def dateFormat = "yyyy-dd-MM HH:mm:ss";
        RapidConvertUtils.getInstance().register(new DateConverter(dateFormat), Date.class)
        RapidConvertUtils.getInstance().register(new LongConverter(), Long.class)
        RapidConvertUtils.getInstance().register(new DoubleConverter(), Double.class)
        RapidConvertUtils.getInstance().register(new BooleanConverter(), Boolean.class)
    }

    public void tearDown() {
        super.tearDown();

    }
    void initializeScriptManager(script_directory)
    {
        def script_base_directory= base_directory+"/test/manualTestScripts/${script_directory}";
        println "script base path is :"+new File(script_base_directory).getCanonicalPath();

        ScriptManager manager = ScriptManager.getInstance();
        manager.initialize(this.class.getClassLoader(), script_base_directory, [], [:]);
    }
    public void initializeModels(classes)
    {
        initialize([CmdbScript,RsTopologyObject,RsCustomer,RsEvent,RsGroup,RsService,RsObjectState,relation.Relation], []);
        CompassForTests.addOperationSupport (CmdbScript,CmdbScriptOperations);
        CompassForTests.addOperationSupport (RsEvent,RsEventOperations);
        CompassForTests.addOperationSupport (RsTopologyObject,classes.RsTopologyObjectOperations);
        CompassForTests.addOperationSupport (RsGroup,classes.RsGroupOperations);
        CompassForTests.addOperationSupport (RsCustomer,classes.RsCustomerOperations);
        CompassForTests.addOperationSupport (RsService,classes.RsServiceOperations);



    }
    public File getOperationPathAsFile(opdir,opfile)
    {
        return new File("${base_directory}/${opdir}/${opfile}.groovy");
    }
    public void testFindMaxTest()
    {
        def classes=[:];
        classes.RsTopologyObjectOperations=RsTopologyObjectOperations;
        classes.RsGroupOperations=RsGroupOperations;
        classes.RsCustomerOperations=RsCustomerOperations;
        classes.RsServiceOperations=RsServiceOperations;
        initializeModels(classes)
        initializeScriptManager("stateCalculation");

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
        def classes=[:];

        //note that here we first parce RsTopologyObject operations
        //and then we load other extending Operations , by this way all will extend from the first loaded ones
        //if we do not load the parents first the childs will the the ones in original operations folder
        GroovyClassLoader loader=new GroovyClassLoader();
        classes.RsTopologyObjectOperations=loader.parseClass(getOperationPathAsFile("overridenOperations/criticalPercent","RsTopologyObjectOperations"));
        classes.RsGroupOperations=loader.parseClass(getOperationPathAsFile("overridenOperations/criticalPercent","RsGroupOperations"));
        classes.RsCustomerOperations=loader.parseClass(getOperationPathAsFile("operations","RsCustomerOperations"));
        classes.RsServiceOperations=loader.parseClass(getOperationPathAsFile("operations","RsServiceOperations"));
        initializeModels(classes)
        initializeScriptManager("stateCalculation");

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
        initialize([CmdbScript,RsEvent,RsRiEvent,RsHistoricalEvent,RsEventJournal,RsTopologyObject], []);
        CompassForTests.addOperationSupport (CmdbScript,CmdbScriptOperations);
        CompassForTests.addOperationSupport (RsEvent,RsEventOperations);
        CompassForTests.addOperationSupport (RsRiEvent,RsRiEventOperations);
        CompassForTests.addOperationSupport (RsHistoricalEvent,RsHistoricalEventOperations);
        CompassForTests.addOperationSupport (RsEventJournal,RsEventJournalOperations);
        CompassForTests.addOperationSupport (RsTopologyObject,RsTopologyObjectOperations);

        initializeScriptManager("operationTests");

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
}
