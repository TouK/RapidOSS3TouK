import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.RsUtilityTestUtils
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 22, 2009
* Time: 1:49:38 PM
* To change this template use File | Settings | File Templates.
*/
class StateCalculatorTest extends RapidCmdbWithCompassTestCase{
    static def classes = [:];
    public void setUp() {
        super.setUp();
        initializeClasses();
        clearMetaClasses();
        initialize([RsTopologyObject, RsObjectState, RsEvent,RsUtility,RsGroup], []);
        RsUtilityTestUtils.initializeRsUtilityOperations (RsUtility);
        RsUtilityTestUtils.setToDefaultProcessors();
    }

    public void tearDown() {
        clearMetaClasses();
        StateCalculatorTest.clearClasses();
        super.tearDown();
    }
    public static void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsTopologyObject)
        StateCalculatorTest.classes.each {className, classInstance ->
            GroovySystem.metaClassRegistry.removeMetaClass(classInstance)
        }
        ExpandoMetaClass.enableGlobally();
    }
    public static void clearClasses()
    {
        classes=[:];
        RsUtility.getUtility("StateCalculator").setToDefault();

    }
    public def initializeClasses()
    {
        def classMap = [:];
        classMap.Constants = Constants;
        classMap.StateCalculator=StateCalculator;
        classMap.RsTopologyObjectOperations = RsTopologyObjectOperations;
        StateCalculatorTest.initializeClassesFrom(classMap);
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
        return StateCalculatorTest.classes;
    }
    public static void testConstants()
    {
        assertEquals(5, getClasses().Constants.CRITICAL);
        assertEquals(4, getClasses().Constants.MAJOR);
        assertEquals(0, getClasses().Constants.NORMAL);
        assertEquals(-1, getClasses().Constants.NOTSET);
        assertTrue(getClasses().Constants.CRITICAL_PERCENTAGE > getClasses().Constants.MAJOR_PERCENTAGE);
        assertTrue(getClasses().Constants.MAJOR_PERCENTAGE > 0);
    }
    public static void testRemoveDeletesRsObjectStateInstance()
    {
        CompassForTests.addOperationSupport(RsTopologyObject, getClasses().RsTopologyObjectOperations);

        def object = RsTopologyObject.add(name: "testobject");
        assertFalse(object.hasErrors());

        assertEquals(0, RsObjectState.list().size());

        object.getState();
        assertEquals(1, RsObjectState.list().size());

        object.remove();

        assertEquals(0, RsObjectState.list().size());
    }

    public static void testSaveStateAndLoadState()
    {
        def _Contants=getClasses().Constants;
        def _StateCalculator=getClasses().StateCalculator;

        def object = RsTopologyObject.add(name: "testobject");
        assertFalse(object.hasErrors());

        assertEquals(0, RsObjectState.list().size());
        assertEquals(_Contants.NOTSET, _StateCalculator.loadObjectState(object))

        def newState = 5
        getClasses().StateCalculator.saveObjectState(object,newState);
        assertEquals(1, RsObjectState.list().size());
        assertEquals(newState, RsObjectState.get(objectId: object.id).state);
        assertEquals(newState, _StateCalculator.loadObjectState(object))

        newState = 3
        getClasses().StateCalculator.saveObjectState(object,newState);
        assertEquals(1, RsObjectState.list().size());
        assertEquals(newState, RsObjectState.get(objectId: object.id).state);
        assertEquals(newState, _StateCalculator.loadObjectState(object))

    }

    public static void testNeedToCalculate()
    {
        def _Contants=getClasses().Constants;
        def _StateCalculator=getClasses().StateCalculator;

        def object = RsTopologyObject.add(name: "testobject");
        assertFalse(object.hasErrors());

        //when old and new are notset
        5.times {counter ->
            def currentState = counter + 1
            assertTrue(_StateCalculator.needToCalculate(currentState, getClasses().Constants.NOTSET, getClasses().Constants.NOTSET))
        }

        5.times {counter ->
            def currentState = counter + 1
            //when new is higher than current   , and current is not same as old
            assertTrue(_StateCalculator.needToCalculate(currentState, getClasses().Constants.NOTSET, currentState + 1))
            assertTrue(_StateCalculator.needToCalculate(currentState, currentState * 3, currentState + 1))
            assertTrue(_StateCalculator.needToCalculate(currentState, currentState / 3, currentState + 1))
            //when new is less than current   , and current is not same as old
            assertFalse(_StateCalculator.needToCalculate(currentState, getClasses().Constants.NOTSET, currentState - 1))
            assertFalse(_StateCalculator.needToCalculate(currentState, currentState * 3, currentState - 1))
            assertFalse(_StateCalculator.needToCalculate(currentState, currentState / 3, currentState - 1))

        }

        5.times {counter ->
            def currentState = counter + 1
            //when old is same as current and new is less than current
            assertTrue(_StateCalculator.needToCalculate(currentState, currentState, currentState - 1))
            //false cases when new is less than current but old is not same as current
            assertFalse(_StateCalculator.needToCalculate(currentState, currentState + 1, currentState - 1))
        }
        //other false cases
        5.times {counter ->
            def currentState = counter + 1
            //when all are same
            assertFalse(_StateCalculator.needToCalculate(currentState, currentState, currentState))
            //when current higher than old , old higher than new
            assertFalse(_StateCalculator.needToCalculate(currentState + 2, currentState + 1, currentState))
            //when old and new are same and current is higher
            assertFalse(_StateCalculator.needToCalculate(currentState + 1, currentState, currentState))

            assertFalse(_StateCalculator.needToCalculate(currentState + 1, currentState, currentState - 1))
        }
    }


    public static void testFindMaxSeverityForRsTopologyObject()
    {
        def _Constants=getClasses().Constants;
        def _StateCalculator=getClasses().StateCalculator;

        def object = RsTopologyObject.add(name: "testobject");
        assertFalse(object.hasErrors());
        5.times {counter ->
            //need to calculate is true
            assertEquals(_Constants.NORMAL, _StateCalculator.findMaxSeverity(object,counter + 1, _Constants.NOTSET, _Constants.NOTSET));
        }
        RsEvent.add(name: "ev1", elementName: object.name, severity: 1)
        RsEvent.add(name: "ev2", elementName: object.name, severity: 2)
        RsEvent.add(name: "ev3", elementName: object.name, severity: 3)
        //these 2 events here must not effect calculation
        RsEvent.add(name: "ev4", severity: 4)
        RsEvent.add(name: "ev5", severity: 5)

        assertEquals(5, RsEvent.countHits("alias:*"));

        5.times {counter ->
            //need to calculate is true
            assertEquals(3, _StateCalculator.findMaxSeverity(object,counter + 1, _Constants.NOTSET, _Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, _StateCalculator.findMaxSeverity(object,counter + 1, counter + 1, counter + 1));
        }
    }
    public static void testFindMaxSeverityForRsGroup()
    {
        def _Constants=getClasses().Constants;
        def _StateCalculator=getClasses().StateCalculator;

        def object = RsGroup.add(name: "testobject");
        assertFalse(object.hasErrors());
        5.times {counter ->
            //need to calculate is true
            assertEquals(_Constants.NORMAL, _StateCalculator.findMaxSeverity(object,counter + 1, _Constants.NOTSET, _Constants.NOTSET));
        }
        def childObjects=[];


        childObjects.add(RsTopologyObject.add(name: "ev1", parentObjects:[object]))
        childObjects.add(RsTopologyObject.add(name: "ev2", parentObjects:[object]))
        childObjects.add(RsTopologyObject.add(name: "ev3", parentObjects:[object]))

        _StateCalculator.saveObjectState(childObjects[0],1);
        _StateCalculator.saveObjectState(childObjects[1],2);
        _StateCalculator.saveObjectState(childObjects[2],3);



        //these 2 child object here must not effect calculation
        def nonChildObjects=[];
        nonChildObjects.add(RsTopologyObject.add(name: "ev4"))
        nonChildObjects.add(RsTopologyObject.add(name: "ev5"))

        _StateCalculator.saveObjectState(nonChildObjects[0],4);
        _StateCalculator.saveObjectState(nonChildObjects[1],5);


        assertEquals(3,object.childObjects.size());

        //assertEquals(5, RsTopologyObject.countHits("alias:RsTopologyObject"));
        assertEquals(5, RsTopologyObject.countHits("name:ev*"));
        assertEquals(1, RsGroup.countHits("alias:*"));

        5.times {counter ->
            //need to calculate is true
            assertEquals(3, _StateCalculator.findMaxSeverity(object,counter + 1, _Constants.NOTSET, _Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, _StateCalculator.findMaxSeverity(object,counter + 1, counter + 1, counter + 1));
        }
    }

    public static void testCriticalPercentForRsTopologyObject()
    {

        def _Constants=getClasses().Constants;
        def _StateCalculator=getClasses().StateCalculator;

        def object = RsTopologyObject.add(name: "testobject");
        assertFalse(object.hasErrors());

        def eventCount = 10;
        def events = [];
        eventCount.times {counter ->
            events.add(RsEvent.add(name: "ev${counter}", elementName: object.name, severity: _Constants.NORMAL))
        }
        //these 2 events here must not effect calculation
        RsEvent.add(name: "otherev1", severity: 1)
        RsEvent.add(name: "otherev2", severity: 1)

        assertEquals(eventCount + 2, RsEvent.countHits("alias:*"));
        //testing with 0 critical count
        int criticalCount = 0;
        assertEquals(criticalCount, RsEvent.countHits("elementName:${object.name} AND severity:${_Constants.CRITICAL}"));
        5.times {counter ->
            //need to calculate is true
            assertEquals(_Constants.NORMAL, _StateCalculator.criticalPercent(object,counter + 1, _Constants.NOTSET, _Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, _StateCalculator.criticalPercent(object,counter + 1, counter + 1, counter + 1));
        }

        //testing with critical count 1 less than MAJOR PERCENTAGE

        //we update some of the events severity : critical until the limit of major percentage
        criticalCount = Math.floor((eventCount * _Constants.MAJOR_PERCENTAGE / 100)) - 1;
        criticalCount.times {index ->
            events[index].update(severity: _Constants.CRITICAL);
        }
        assertEquals(criticalCount, RsEvent.countHits("elementName:${object.name} AND severity:${_Constants.CRITICAL}"));

        5.times {counter ->
            //need to calculate is true
            assertEquals(_Constants.NORMAL, _StateCalculator.criticalPercent(object,counter + 1, _Constants.NOTSET, _Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, _StateCalculator.criticalPercent(object,counter + 1, counter + 1, counter + 1));
        }

        //testing with critical count 1 less than CRITICAL PERCENTAGE
        criticalCount = Math.floor((eventCount * _Constants.CRITICAL_PERCENTAGE / 100)) - 1;
        criticalCount.times {index ->
            events[index].update(severity: _Constants.CRITICAL);
        }
        assertEquals(criticalCount, RsEvent.countHits("elementName:${object.name} AND severity:${_Constants.CRITICAL}"));

        5.times {counter ->
            //need to calculate is true
            assertEquals(_Constants.MAJOR, _StateCalculator.criticalPercent(object,counter + 1, _Constants.NOTSET, _Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, _StateCalculator.criticalPercent(object,counter + 1, counter + 1, counter + 1));
        }

        //testing with critical count 1 more than CRITICAL PERCENTAGE
        criticalCount = Math.floor((eventCount * _Constants.CRITICAL_PERCENTAGE / 100)) + 1;
        criticalCount.times {index ->
            events[index].update(severity: _Constants.CRITICAL);
        }
        assertEquals(criticalCount, RsEvent.countHits("elementName:${object.name} AND severity:${_Constants.CRITICAL}"));

        5.times {counter ->
            //need to calculate is true
            assertEquals(_Constants.CRITICAL, _StateCalculator.criticalPercent(object,counter + 1, _Constants.NOTSET, _Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, _StateCalculator.criticalPercent(object,counter + 1, counter + 1, counter + 1));
        }

        //testing with all critical
        criticalCount = eventCount;
        criticalCount.times {index ->
            events[index].update(severity: _Constants.CRITICAL);
        }
        assertEquals(criticalCount, RsEvent.countHits("elementName:${object.name} AND severity:${_Constants.CRITICAL}"));

        5.times {counter ->
            //need to calculate is true
            assertEquals(_Constants.CRITICAL, _StateCalculator.criticalPercent(object,counter + 1, _Constants.NOTSET, _Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, _StateCalculator.criticalPercent(object,counter + 1, counter + 1, counter + 1));
        }
    }

    public static void testCriticalPercentForRsGroup()
    {
        def _Constants=getClasses().Constants;
        def _StateCalculator=getClasses().StateCalculator;

        def object = RsGroup.add(name: "testobject");
        assertFalse(object.hasErrors());

        def childObjectCount = 10;
        def childObjects=[];

        childObjectCount.times {counter ->
            def childObject=RsTopologyObject.add(name: "ev${counter}", parentObjects:[object]);
            _StateCalculator.saveObjectState(childObject,_Constants.NORMAL);
            childObjects.add(childObject)

        }

        //these 2 child object here must not effect calculation
        def nonChildObjects=[];
        nonChildObjects.add(RsTopologyObject.add(name: "evchild1"))
        nonChildObjects.add(RsTopologyObject.add(name: "evchild2"))

        _StateCalculator.saveObjectState(nonChildObjects[0],_Constants.CRITICAL);
        _StateCalculator.saveObjectState(nonChildObjects[1],_Constants.CRITICAL);


        assertEquals(childObjectCount,object.childObjects.size());
        assertEquals(childObjectCount + 2, RsTopologyObject.countHits("name:ev*"));
        //testing with 0 critical count
        int criticalCount = 0;
        assertEquals(criticalCount + 2, RsObjectState.countHits("state:${_Constants.CRITICAL}"));
        5.times {counter ->
            //need to calculate is true
            assertEquals(_Constants.NORMAL, _StateCalculator.criticalPercent(object,counter + 1, _Constants.NOTSET, _Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, _StateCalculator.criticalPercent(object,counter + 1, counter + 1, counter + 1));
        }


        //testing with critical count 1 less than MAJOR PERCENTAGE

        //we update some of the events severity : critical until the limit of major percentage
        criticalCount = Math.floor((childObjectCount * _Constants.MAJOR_PERCENTAGE / 100)) - 1;
        criticalCount.times {index ->
            _StateCalculator.saveObjectState(childObjects[index],_Constants.CRITICAL);
        }
        assertEquals(criticalCount + 2, RsObjectState.countHits("state:${_Constants.CRITICAL}"));

        5.times {counter ->
            //need to calculate is true
            assertEquals(_Constants.NORMAL, _StateCalculator.criticalPercent(object,counter + 1, _Constants.NOTSET, _Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, _StateCalculator.criticalPercent(object,counter + 1, counter + 1, counter + 1));
        }

        //testing with critical count 1 less than CRITICAL PERCENTAGE
        criticalCount = Math.floor((childObjectCount * _Constants.CRITICAL_PERCENTAGE / 100)) - 1;
        criticalCount.times {index ->
            _StateCalculator.saveObjectState(childObjects[index],_Constants.CRITICAL);
        }
        assertEquals(criticalCount + 2, RsObjectState.countHits("state:${_Constants.CRITICAL}"));

        5.times {counter ->
            //need to calculate is true
            assertEquals(_Constants.MAJOR, _StateCalculator.criticalPercent(object,counter + 1, _Constants.NOTSET, _Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, _StateCalculator.criticalPercent(object,counter + 1, counter + 1, counter + 1));
        }

        //testing with critical count 1 more than CRITICAL PERCENTAGE
        criticalCount = Math.floor((childObjectCount * _Constants.CRITICAL_PERCENTAGE / 100)) + 1;
        criticalCount.times {index ->
            _StateCalculator.saveObjectState(childObjects[index],_Constants.CRITICAL);
        }
        assertEquals(criticalCount + 2, RsObjectState.countHits("state:${_Constants.CRITICAL}"));

        5.times {counter ->
            //need to calculate is true
            assertEquals(_Constants.CRITICAL, _StateCalculator.criticalPercent(object,counter + 1, _Constants.NOTSET, _Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, _StateCalculator.criticalPercent(object,counter + 1, counter + 1, counter + 1));
        }

        //testing with all critical
        criticalCount = childObjectCount;
        criticalCount.times {index ->
            _StateCalculator.saveObjectState(childObjects[index],_Constants.CRITICAL);
        }
        assertEquals(criticalCount + 2, RsObjectState.countHits("state:${_Constants.CRITICAL}"));

        5.times {counter ->
            //need to calculate is true
            assertEquals(_Constants.CRITICAL, _StateCalculator.criticalPercent(object,counter + 1, _Constants.NOTSET, _Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter + 1, _StateCalculator.criticalPercent(object,counter + 1, counter + 1, counter + 1));
        }
    }

    public static void testGetState()
    {
        def _Constants=getClasses().Constants;
        def _StateCalculator=getClasses().StateCalculator;

        int calculateStateReturnValue;
        def callParams = [:];
        _StateCalculator.metaClass.'static'.calculateObjectState = {topoObject,currentState, oldPropagatedState, newPropagatedState ->
            println "calculateState in testGetState"
            callParams = [currentState: currentState, oldPropagatedState: oldPropagatedState, newPropagatedState: newPropagatedState]
            return calculateStateReturnValue;

        }


        def object = RsTopologyObject.add(name: "testobject");
        assertFalse(object.hasErrors());


        //test that if no state is calculated before , state calculation is done  and result saved
        5.times {counter ->
            callParams = [:];
            RsObjectState.removeAll();
            assertEquals(_Constants.NOTSET, _StateCalculator.loadObjectState(object));

            calculateStateReturnValue = counter + 1;
            def calculatedState = counter + 1;
            assertEquals(calculatedState, _StateCalculator.getObjectState(object));

            assertEquals(calculatedState, _StateCalculator.loadObjectState(object));
            assertEquals(calculatedState, RsObjectState.get(objectId: object.id).state);

            assertEquals(_Constants.NOTSET, callParams.currentState);
            assertEquals(_Constants.NOTSET, callParams.oldPropagatedState);
            assertEquals(_Constants.NOTSET, callParams.newPropagatedState);
        }

        //test that if state is calculated before not state calculation is not done and result is returned
        //save the state of the object
        RsObjectState.removeAll();
        assertEquals(_Constants.NOTSET, _StateCalculator.loadObjectState(object));
        def newState = 3;
        _StateCalculator.saveObjectState(object,newState)
        assertEquals(newState, _StateCalculator.loadObjectState(object));
        //cal getState
        callParams = [:];
        assertEquals(newState, _StateCalculator.getObjectState(object));
        assertEquals(0, callParams.size());

    }
    
    public static void testSetState()
    {
        def _Constants=getClasses().Constants;
        def _StateCalculator=getClasses().StateCalculator;

        int calculateStateReturnValue;
        def calculateStateCallParams = [:];
        def propagateStateStateCallParams = [:];

        _StateCalculator.metaClass.'static'.calculateObjectState = {topoObject,currentState, oldPropagatedState, newPropagatedState ->
            println "calculateState in testSetState"
            calculateStateCallParams = [currentState: currentState, oldPropagatedState: oldPropagatedState, newPropagatedState: newPropagatedState]
            return calculateStateReturnValue;

        }
        _StateCalculator.metaClass.'static'.propagateObjectState = {topoObject,oldState, newState ->
            println "propagateObjectState in testSetState"
            propagateStateStateCallParams = [oldState: oldState, newState: newState]

        }

        def object = RsTopologyObject.add(name: "testobject");
        assertFalse(object.hasErrors());


        //current state and calculated state is different , save state is called and propagation done
        5.times {counter ->
            calculateStateCallParams = [:];
            propagateStateStateCallParams = [:];

            def savedState = counter + 2
            _StateCalculator.saveObjectState(object,savedState)
            assertEquals(savedState, _StateCalculator.loadObjectState(object));

            calculateStateReturnValue = counter + 1;
            def calculatedState = counter + 1;
            assertEquals(calculatedState, _StateCalculator.setObjectState(object,counter + 1));

            assertEquals(calculatedState, _StateCalculator.loadObjectState(object));
            assertEquals(calculatedState, RsObjectState.get(objectId: object.id).state);

            assertEquals(savedState, calculateStateCallParams.currentState);
            assertEquals(_Constants.NOTSET, calculateStateCallParams.oldPropagatedState);
            assertEquals(calculatedState, calculateStateCallParams.newPropagatedState);

            assertEquals(savedState, propagateStateStateCallParams.oldState);
            assertEquals(calculatedState, propagateStateStateCallParams.newState);

        }

        //current state and calculated state is different old propagated state is given, save state is called and propagation done
        5.times {counter ->
            calculateStateCallParams = [:];
            propagateStateStateCallParams = [:];

            def savedState = counter + 2
            _StateCalculator.saveObjectState(object,savedState)
            assertEquals(savedState, _StateCalculator.loadObjectState(object));

            calculateStateReturnValue = counter + 1;
            def calculatedState = counter + 1;
            def oldPropagatedState = 10;
            assertEquals(calculatedState, _StateCalculator.setObjectState(object,counter + 1, oldPropagatedState));


            assertEquals(calculatedState, _StateCalculator.loadObjectState(object));
            assertEquals(calculatedState, RsObjectState.get(objectId: object.id).state);

            assertEquals(savedState, calculateStateCallParams.currentState);
            assertEquals(oldPropagatedState, calculateStateCallParams.oldPropagatedState);
            assertEquals(calculatedState, calculateStateCallParams.newPropagatedState);

            assertEquals(savedState, propagateStateStateCallParams.oldState);
            assertEquals(calculatedState, propagateStateStateCallParams.newState);

        }

        //current state and calculated state are same, set propagation is not called
        5.times {counter ->
            calculateStateCallParams = [:];
            propagateStateStateCallParams = [:];

            def savedState = counter + 1
            _StateCalculator.saveObjectState(object,savedState)
            assertEquals(savedState, _StateCalculator.loadObjectState(object));

            calculateStateReturnValue = counter + 1;
            def calculatedState = counter + 1;
            assertEquals(calculatedState, _StateCalculator.setObjectState(object,counter + 1));
            assertEquals(calculatedState, savedState);

            assertEquals(calculatedState,  _StateCalculator.loadObjectState(object));
            assertEquals(calculatedState, RsObjectState.get(objectId: object.id).state);

            assertEquals(savedState, calculateStateCallParams.currentState);
            assertEquals(_Constants.NOTSET, calculateStateCallParams.oldPropagatedState);
            assertEquals(calculatedState, calculateStateCallParams.newPropagatedState);

            assertEquals(0, propagateStateStateCallParams.size());
        }
    }
    public static void testPropagateStateCallsSetStateOfParentObjects()
    {
        def _Constants=getClasses().Constants;
        def _StateCalculator=getClasses().StateCalculator;

        def setStateStateCallParams = [:];
        _StateCalculator.metaClass.'static'.setObjectState = {object,newState, oldState ->
            println "setObjectState in testPropagateStateCallsSetStateOfParentObjects"
            setStateStateCallParams[object.id] = [oldState: oldState, newState: newState]

        }


        //add 1 object and  test calculate weight
        //Tree : object
        def object = RsTopologyObject.add(name: "testobject");
        assertFalse(object.hasErrors());

        assertEquals(1, RsTopologyObject.countHits("alias:*"));


        //add 3 parentobjects and test calculate weight
        //Tree  object < parentLevel1
        3.times {counter ->
            RsTopologyObject.add(name: "parentlevel1_${counter}", childObjects: [object]);
        }

        object = RsTopologyObject.get(name: "testobject");
        assertEquals(3, object.parentObjects.size());

        def oldState = 1;
        def newState = 2;
        _StateCalculator.propagateObjectState(object,oldState, newState);

        assertEquals(3, setStateStateCallParams.size());

        object.parentObjects.each {parentObject ->
            assertEquals(oldState, setStateStateCallParams[parentObject.id].oldState)
            assertEquals(newState, setStateStateCallParams[parentObject.id].newState)
        }
    }

    public static void testCalculateStateCallsMethodInCalculateMethodProperty()
    {
        def _Constants=getClasses().Constants;
        def _StateCalculator=getClasses().StateCalculator;

        int findMaxSeverityReturnValue;
        int criticalPercentReturnValue;
        
        def findMaxSeverityCallParams = [:]
        def criticalPercentCallParams = [:]

        _StateCalculator.metaClass.'static'.findMaxSeverity = {object,currentState, oldPropagatedState, newPropagatedState ->
            println "findmax in test"
            findMaxSeverityCallParams = [currentState: currentState, oldPropagatedState: oldPropagatedState, newPropagatedState: newPropagatedState]
            return findMaxSeverityReturnValue;
        }

        _StateCalculator.metaClass.'static'.criticalPercent = {object,currentState, oldPropagatedState, newPropagatedState ->
            println "criticalPercent in test"
            criticalPercentCallParams = [currentState: currentState, oldPropagatedState: oldPropagatedState, newPropagatedState: newPropagatedState]
            return criticalPercentReturnValue;
        }



        def object = RsTopologyObject.add(name: "testobject");
        assertFalse(object.hasErrors());

        3.times {counter ->
            findMaxSeverityCallParams = [:]
            _StateCalculator.calculateMethod="findMaxSeverity";
            findMaxSeverityReturnValue = counter + 1;
            assertEquals(counter + 1, _StateCalculator.calculateObjectState(object,1, 2, 3));
            assertEquals(1, findMaxSeverityCallParams.currentState);
            assertEquals(2, findMaxSeverityCallParams.oldPropagatedState);
            assertEquals(3, findMaxSeverityCallParams.newPropagatedState);

            criticalPercentCallParams = [:]
            _StateCalculator.calculateMethod="criticalPercent";
            criticalPercentReturnValue = counter + 1;
            assertEquals(counter + 1, _StateCalculator.calculateObjectState(object,1, 2, 3));
            assertEquals(1, criticalPercentCallParams.currentState);
            assertEquals(2, criticalPercentCallParams.oldPropagatedState);
            assertEquals(3, criticalPercentCallParams.newPropagatedState);
        }
    }
}