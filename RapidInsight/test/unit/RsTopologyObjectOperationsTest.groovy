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


//    public static void testCalculateWeight()
//    {
//
//        CompassForTests.addOperationSupport(RsTopologyObject, getClasses().RsTopologyObjectOperations);
//
//        //add 1 object and  test calculate weight
//        //Tree : object
//        def object = RsTopologyObject.add(name: "testobject");
//        assertFalse(object.hasErrors());
//
//        assertEquals(1, RsTopologyObject.countHits("alias:*"));
//        assertEquals(1, object.calculateWeight());
//        assertEquals(0, object.parentObjects.size());
//
//        //add 3 parentobjects and test calculate weight
//        //Tree  object < parentLevel1
//        3.times {counter ->
//            RsTopologyObject.add(name: "parentlevel1_${counter}", childObjects: [object]);
//        }
//
//        object = RsTopologyObject.get(name: "testobject");
//        assertEquals(3, object.parentObjects.size());
//        assertEquals(1 + 3, object.calculateWeight());
//
//        object.parentObjects.each {parentObject ->
//            assertEquals(1, parentObject.calculateWeight());
//        }
//
//        //add 6 parent object for each of the parent objects of the object
//        //Tree  object < parentLevel1 <  parentLevel2
//        object.parentObjects.each {parentObject ->
//            3.times {counter ->
//                RsTopologyObject.add(name: "parentlevel2a_${parentObject.name}_${counter}", childObjects: [parentObject]);
//                RsTopologyObject.add(name: "parentlevel2b_${parentObject.name}_${counter}", childObjects: [parentObject]);
//            }
//        }
//
//        object = RsTopologyObject.get(name: "testobject");
//        assertEquals(3, object.parentObjects.size());
//        assertEquals(1 + 3 + 18, object.calculateWeight());
//
//        object.parentObjects.each {parentObject ->
//            assertEquals(6, parentObject.parentObjects.size());
//            assertEquals(1 + 6, parentObject.calculateWeight());
//            parentObject.parentObjects.each {parentObject2 ->
//                assertEquals(0, parentObject2.parentObjects.size());
//                assertEquals(1, parentObject2.calculateWeight());
//            }
//
//        }
//
//    }


}