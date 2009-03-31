import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 21, 2009
* Time: 8:17:14 PM
* To change this template use File | Settings | File Templates.
*/
class RsGroupOperationsTest extends RapidCmdbWithCompassTestCase {
    static def classes = [:];
    public void setUp() {
        super.setUp();
        initializeClasses();
        clearMetaClasses();
        initialize([RsTopologyObject, RsObjectState, RsEvent,RsGroup,relation.Relation], []);
    }

    public void tearDown() {
        clearMetaClasses();
        super.tearDown();
    }
    public static void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsTopologyObject)
        GroovySystem.metaClassRegistry.removeMetaClass(RsGroup)
        RsGroupOperationsTest.classes.each {className, classInstance ->
            GroovySystem.metaClassRegistry.removeMetaClass(classInstance)
        }
        ExpandoMetaClass.enableGlobally();
    }
    public def initializeClasses()
    {
        def classMap = [:];
        classMap.Constants = Constants;
        classMap.RsTopologyObjectOperations = RsTopologyObjectOperations;
        classMap.RsGroupOperations = RsGroupOperations;
        RsGroupOperationsTest.initializeClassesFrom(classMap);
    }
    public static def initializeClassesFrom(classesToLoad)
    {
        RsGroupOperationsTest.classes = classesToLoad;
    }
    static def getClasses()
    {
        return RsGroupOperationsTest.classes;
    }


    public static void testFindMaxSeverity()
    {

        CompassForTests.addOperationSupport(RsTopologyObject, getClasses().RsTopologyObjectOperations);
        CompassForTests.addOperationSupport(RsGroup, getClasses().RsGroupOperations);

        def object = RsGroup.add(name: "testobject");
        assertFalse(object.hasErrors());
        5.times {counter ->
            //need to calculate is true
            assertEquals(getClasses().Constants.NORMAL, object.findMaxSeverity(counter + 1, getClasses().Constants.NOTSET, getClasses().Constants.NOTSET));
        }
        def childObjects=[];

        
        childObjects.add(RsTopologyObject.add(name: "ev1", parentObjects:[object]))
        childObjects.add(RsTopologyObject.add(name: "ev2", parentObjects:[object]))
        childObjects.add(RsTopologyObject.add(name: "ev3", parentObjects:[object]))

        childObjects[0].saveState(1);
        childObjects[1].saveState(2);
        childObjects[2].saveState(3);

        
        //these 2 child object here must not effect calculation
        def nonChildObjects=[];
        nonChildObjects.add(RsTopologyObject.add(name: "ev4"))
        nonChildObjects.add(RsTopologyObject.add(name: "ev5"))

        nonChildObjects[0].saveState(4);
        nonChildObjects[1].saveState(5);


        assertEquals(3,object.childObjects.size());

        //assertEquals(5, RsTopologyObject.countHits("alias:RsTopologyObject"));
        assertEquals(5, RsTopologyObject.countHits("name:ev*"));
        assertEquals(1, RsGroup.countHits("alias:*"));

        5.times {counter ->
            //need to calculate is true
            assertEquals(3, object.findMaxSeverity(counter + 1, getClasses().Constants.NOTSET, getClasses().Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, object.findMaxSeverity(counter + 1, counter + 1, counter + 1));
        }
    }
    public static void testCriticalPercent()
    {

        CompassForTests.addOperationSupport(RsTopologyObject, getClasses().RsTopologyObjectOperations);
        CompassForTests.addOperationSupport(RsGroup, getClasses().RsGroupOperations);

        def object = RsGroup.add(name: "testobject");
        assertFalse(object.hasErrors());

        def childObjectCount = 10;
        def childObjects=[];

        childObjectCount.times {counter ->
            def childObject=RsTopologyObject.add(name: "ev${counter}", parentObjects:[object]);
            childObject.saveState(getClasses().Constants.NORMAL);
            childObjects.add(childObject)

        }

        //these 2 child object here must not effect calculation
        def nonChildObjects=[];
        nonChildObjects.add(RsTopologyObject.add(name: "evchild1"))
        nonChildObjects.add(RsTopologyObject.add(name: "evchild2"))

        nonChildObjects[0].saveState(getClasses().Constants.CRITICAL);
        nonChildObjects[1].saveState(getClasses().Constants.CRITICAL);

        assertEquals(childObjectCount,object.childObjects.size());
        assertEquals(childObjectCount + 2, RsTopologyObject.countHits("name:ev*"));
        //testing with 0 critical count
        int criticalCount = 0;
        assertEquals(criticalCount + 2, RsObjectState.countHits("state:${getClasses().Constants.CRITICAL}"));
        5.times {counter ->
            //need to calculate is true
            assertEquals(getClasses().Constants.NORMAL, object.criticalPercent(counter + 1, getClasses().Constants.NOTSET, getClasses().Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, object.criticalPercent(counter + 1, counter + 1, counter + 1));
        }


        //testing with critical count 1 less than MAJOR PERCENTAGE

        //we update some of the events severity : critical until the limit of major percentage
        criticalCount = Math.floor((childObjectCount * getClasses().Constants.MAJOR_PERCENTAGE / 100)) - 1;
        criticalCount.times {index ->
            childObjects[index].saveState(getClasses().Constants.CRITICAL);
        }
        assertEquals(criticalCount + 2, RsObjectState.countHits("state:${getClasses().Constants.CRITICAL}"));

        5.times {counter ->
            //need to calculate is true
            assertEquals(getClasses().Constants.NORMAL, object.criticalPercent(counter + 1, getClasses().Constants.NOTSET, getClasses().Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, object.criticalPercent(counter + 1, counter + 1, counter + 1));
        }

        //testing with critical count 1 less than CRITICAL PERCENTAGE
        criticalCount = Math.floor((childObjectCount * getClasses().Constants.CRITICAL_PERCENTAGE / 100)) - 1;
        criticalCount.times {index ->
            childObjects[index].saveState(getClasses().Constants.CRITICAL);
        }
        assertEquals(criticalCount + 2, RsObjectState.countHits("state:${getClasses().Constants.CRITICAL}"));

        5.times {counter ->
            //need to calculate is true
            assertEquals(getClasses().Constants.MAJOR, object.criticalPercent(counter + 1, getClasses().Constants.NOTSET, getClasses().Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, object.criticalPercent(counter + 1, counter + 1, counter + 1));
        }

        //testing with critical count 1 more than CRITICAL PERCENTAGE
        criticalCount = Math.floor((childObjectCount * getClasses().Constants.CRITICAL_PERCENTAGE / 100)) + 1;
        criticalCount.times {index ->
            childObjects[index].saveState(getClasses().Constants.CRITICAL);
        }
        assertEquals(criticalCount + 2, RsObjectState.countHits("state:${getClasses().Constants.CRITICAL}"));

        5.times {counter ->
            //need to calculate is true
            assertEquals(getClasses().Constants.CRITICAL, object.criticalPercent(counter + 1, getClasses().Constants.NOTSET, getClasses().Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, object.criticalPercent(counter + 1, counter + 1, counter + 1));
        }

        //testing with all critical
        criticalCount = childObjectCount;
        criticalCount.times {index ->
            childObjects[index].saveState(getClasses().Constants.CRITICAL);
        }
        assertEquals(criticalCount + 2, RsObjectState.countHits("state:${getClasses().Constants.CRITICAL}"));

        5.times {counter ->
            //need to calculate is true
            assertEquals(getClasses().Constants.CRITICAL, object.criticalPercent(counter + 1, getClasses().Constants.NOTSET, getClasses().Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, object.criticalPercent(counter + 1, counter + 1, counter + 1));
        }
    }

     public static void testCalculateStateCallsFindMaxSeverity()
    {

        int findMaxSeverityReturnValue;
        def callParams = [:]
        getClasses().RsGroupOperations.metaClass.findMaxSeverity = {currentState, oldPropagatedState, newPropagatedState ->
            println "findmax in test"
            callParams = [currentState: currentState, oldPropagatedState: oldPropagatedState, newPropagatedState: newPropagatedState]
            return findMaxSeverityReturnValue;
        }

        CompassForTests.addOperationSupport(RsGroup, getClasses().RsGroupOperations);

        def object = RsGroup.add(name: "testobject");
        assertFalse(object.hasErrors());

        5.times {counter ->
            callParams = [:]
            findMaxSeverityReturnValue = counter + 1;
            assertEquals(counter + 1, object.calculateState(1, 2, 3));
            assertEquals(1, callParams.currentState);
            assertEquals(2, callParams.oldPropagatedState);
            assertEquals(3, callParams.newPropagatedState);
        }
    }

}