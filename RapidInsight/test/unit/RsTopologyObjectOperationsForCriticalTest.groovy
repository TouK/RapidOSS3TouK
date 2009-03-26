import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 21, 2009
* Time: 8:17:14 PM
* To change this template use File | Settings | File Templates.
*/
class RsTopologyObjectOperationsForCriticalTest extends RapidCmdbWithCompassTestCase{
     static def base_directory="";
     public void setUp() {
        super.setUp();
        initializeClasses();
        clearMetaClasses();
        initialize([RsTopologyObject,RsObjectState,RsEvent], []);


    }

    public void tearDown() {
        clearMetaClasses();
        super.tearDown();
    }
     private void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsTopologyObject)
        GroovySystem.metaClassRegistry.removeMetaClass(RsTopologyObjectOperationsTest.classes.RsTopologyObjectOperations)
        ExpandoMetaClass.enableGlobally();
    }
    public static def initializeClasses()
    {

          //to run in Hudson
        base_directory = "../../../RapidModules/RapidInsight";
        def canonicalPath=new File(".").getCanonicalPath();
        //to run in developer pc
        if(canonicalPath.endsWith("RapidModules"))
        {
            base_directory = "RapidInsight";
        }
        GroovyClassLoader loader=new GroovyClassLoader();
        def classMap=[];
        classMap=[:];
        classMap.RsTopologyObjectOperations=loader.parseClass(getOperationPathAsFile("overridenOperations/criticalPercent","RsTopologyObjectOperations"));
        RsTopologyObjectOperationsTest.initializeClassesFrom(classMap);
    }

    public static File getOperationPathAsFile(opdir,opfile)
    {
        return new File("${base_directory}/${opdir}/${opfile}.groovy");
    }

     public void testRemoveDeletesRsObjectStateInstance()
     {
         RsTopologyObjectOperationsTest.testRemoveDeletesRsObjectStateInstance();
     }
     public void testSaveStateAndLoadState()
     {
         RsTopologyObjectOperationsTest.testSaveStateAndLoadState();
     }
     public void testNeedToCalculate()
     {
         RsTopologyObjectOperationsTest._testNeedToCalculate();
     }
     public void testFindMaxSeverity()
     {
         RsTopologyObjectOperationsTest._testFindMaxSeverity();
     }
     public void testCriticalPercent()
     {
         RsTopologyObjectOperationsTest._testCriticalPercent();
     }
     public void testCurrentStateCallsLoadState()
     {
         RsTopologyObjectOperationsTest._testCurrentStateCallsLoadState();
     }



}