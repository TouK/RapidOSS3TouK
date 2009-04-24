import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 21, 2009
* Time: 8:17:14 PM
* To change this template use File | Settings | File Templates.
*/
class RsTopologyObjectOperationsTest extends RapidCmdbWithCompassTestCase {
    static def classes = [:];
    public void setUp() {
        super.setUp();
        initializeClasses();
        clearMetaClasses();
        initialize([RsTopologyObject, RsObjectState, RsEvent], []);
    }

    public void tearDown() {
        clearMetaClasses();
        RsTopologyObjectOperationsTest.clearClasses();
        super.tearDown();
    }
    public static void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsTopologyObject)
        RsTopologyObjectOperationsTest.classes.each {className, classInstance ->
            GroovySystem.metaClassRegistry.removeMetaClass(classInstance)
        }
        ExpandoMetaClass.enableGlobally();
    }
    public static void clearClasses()
    {
        classes=[:];
    }
    public def initializeClasses()
    {
        def classMap = [:];
        classMap.Constants = Constants;
        classMap.RsTopologyObjectOperations = RsTopologyObjectOperations;
        RsTopologyObjectOperationsTest.initializeClassesFrom(classMap);
    }
    public static def initializeClassesFrom(classesToLoad)
    {
        if(classes!=null)
        {
            classes.clear();
        }
        classes = classesToLoad.clone();
    }
    static def getClasses()
    {
        return RsTopologyObjectOperationsTest.classes;
    }



//    public static void testCurrentStateCallsLoadState()
//    {
//
//        int loadStateReturnValue;
//        getClasses().RsTopologyObjectOperations.metaClass.loadState = {->
//            println "loadState in test"
//            return loadStateReturnValue;
//        }
//
//        CompassForTests.addOperationSupport(RsTopologyObject, getClasses().RsTopologyObjectOperations);
//
//        def object = RsTopologyObject.add(name: "testobject");
//        assertFalse(object.hasErrors());
//
//        5.times {counter ->
//            loadStateReturnValue = counter + 1;
//            assertEquals(counter + 1, object.currentState());
//        }
//    }
//





}