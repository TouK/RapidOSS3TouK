import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 21, 2009
* Time: 8:17:14 PM
* To change this template use File | Settings | File Templates.
*/
class RsGroupOperationsForCriticalTest extends RapidCmdbWithCompassTestCase {
    def base_directory = "";
    static def classes = [:];
    public void setUp() {
        super.setUp();
        initializeClasses();
        clearMetaClasses();
        initialize([RsTopologyObject, RsObjectState, RsEvent,RsGroup,relation.Relation], []);
    }

    public void tearDown() {
        clearMetaClasses();
        RsGroupOperationsTest.clearClasses();
        super.tearDown();
    }
    public static void clearMetaClasses()
    {
       RsGroupOperationsTest.clearMetaClasses();
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
        classMap.RsGroupOperations = loader.parseClass(getOperationPathAsFile("overridenOperations/criticalPercent", "RsGroupOperations"));
        RsGroupOperationsTest.initializeClassesFrom(classMap);
    }
    static def getClasses()
    {
        return RsGroupOperationsTest.classes;
    }    
    public File getOperationPathAsFile(opdir, opfile)
    {
        return new File("${base_directory}/${opdir}/${opfile}.groovy");
    }


    public static void testFindMaxSeverity()
    {
        RsGroupOperationsTest.testFindMaxSeverity();
    }
    public static void testCriticalPercent()
    {
        RsGroupOperationsTest.testCriticalPercent();
    }
    public static void testCalculateStateCallsCriticalPercent()
    {

        def operationsClass = getClasses().RsGroupOperations;

        int criticalPercentReturnValue;
        def callParams = [:]
        operationsClass.metaClass.criticalPercent = {currentState, oldPropagatedState, newPropagatedState ->
            println "criticalPercent in test"
            callParams = [currentState: currentState, oldPropagatedState: oldPropagatedState, newPropagatedState: newPropagatedState]
            return criticalPercentReturnValue;
        }

        CompassForTests.addOperationSupport(RsGroup, operationsClass);

        def object = RsGroup.add(name: "testobject");
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