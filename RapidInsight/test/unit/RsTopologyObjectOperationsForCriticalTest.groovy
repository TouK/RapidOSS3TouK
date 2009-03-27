import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 21, 2009
* Time: 8:17:14 PM
* To change this template use File | Settings | File Templates.
*/
class RsTopologyObjectOperationsForCriticalTest extends RapidCmdbWithCompassTestCase {
    def base_directory = "";
    public void setUp() {
        super.setUp();
        initializeClasses();
        clearMetaClasses();
        initialize([RsTopologyObject, RsObjectState, RsEvent], []);

    }

    public void tearDown() {
        clearMetaClasses();
        super.tearDown();
    }
    private void clearMetaClasses()
    {
        RsTopologyObjectOperationsTest.clearMetaClasses();
    }
    public def initializeClasses()
    {

        //to run in Hudson
        base_directory = "../../../RapidModules/RapidInsight";
        def canonicalPath = new File(".").getCanonicalPath();
        //to run in developer pc
        if (canonicalPath.endsWith("RapidModules"))
        {
            base_directory = "RapidInsight";
        }
        GroovyClassLoader loader = new GroovyClassLoader();
        def classMap = [];
        classMap = [:];
        classMap.Constants = Constants;
        classMap.RsTopologyObjectOperations = loader.parseClass(getOperationPathAsFile("overridenOperations/criticalPercent", "RsTopologyObjectOperations"));
        RsTopologyObjectOperationsTest.initializeClassesFrom(classMap);
    }
    static def getClasses()
    {
        return RsTopologyObjectOperationsTest.getClasses();
    }
    public File getOperationPathAsFile(opdir, opfile)
    {
        return new File("${base_directory}/${opdir}/${opfile}.groovy");
    }
    public static void testConstants()
    {
        RsTopologyObjectOperationsTest.testConstants();
    }
    public static void testRemoveDeletesRsObjectStateInstance()
    {
        RsTopologyObjectOperationsTest.testRemoveDeletesRsObjectStateInstance();
    }
    public static void testSaveStateAndLoadState()
    {
        RsTopologyObjectOperationsTest.testSaveStateAndLoadState();
    }
    public static void testNeedToCalculate()
    {
        RsTopologyObjectOperationsTest.testNeedToCalculate();
    }
    public static void testFindMaxSeverity()
    {
        RsTopologyObjectOperationsTest.testFindMaxSeverity();
    }
    public static void testCriticalPercent()
    {
        RsTopologyObjectOperationsTest.testCriticalPercent();
    }
    public static void testCurrentStateCallsLoadState()
    {
        RsTopologyObjectOperationsTest.testCurrentStateCallsLoadState();
    }
    public static void testGetState()
    {
        RsTopologyObjectOperationsTest.testGetState();
    }
    public static void testSetState()
    {
        RsTopologyObjectOperationsTest.testSetState();
    }
    public static void testCalculateWeight()
    {
        RsTopologyObjectOperationsTest.testCalculateWeight();
    }
    public static void testPropagateStateCallsSetStateOfParentObjects()
    {
        RsTopologyObjectOperationsTest.testPropagateStateCallsSetStateOfParentObjects();
    }
    public static void testCalculateStateCallsCriticalPercent()
    {
        def operationsClass = getClasses().RsTopologyObjectOperations;

        int criticalPercentReturnValue;
        def callParams = [:]
        operationsClass.metaClass.criticalPercent = {currentState, oldPropagatedState, newPropagatedState ->
            println "criticalPercent in test"
            callParams = [currentState: currentState, oldPropagatedState: oldPropagatedState, newPropagatedState: newPropagatedState]
            return criticalPercentReturnValue;
        }

        CompassForTests.addOperationSupport(RsTopologyObject, operationsClass);

        def object = RsTopologyObject.add(name: "testobject");
        assertFalse(object.hasErrors());

        5.times {counter ->
            callParams = [:]
            criticalPercentReturnValue = counter + 1;
            assertEquals(counter + 1, object.calculateState(1, 2, 3));
            assertEquals(1, callParams.currentState);
            assertEquals(2, callParams.oldPropagatedState);
            assertEquals(3, callParams.newPropagatedState);
        }
    }
}