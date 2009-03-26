import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 21, 2009
* Time: 8:17:14 PM
* To change this template use File | Settings | File Templates.
*/
class RsTopologyObjectOperationsTest extends RapidCmdbWithCompassTestCase{
     static def classes=[:];
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
     public static void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsTopologyObject)
        RsTopologyObjectOperationsTest.classes.each { className, classInstance ->
            GroovySystem.metaClassRegistry.removeMetaClass(classInstance)
        }
        ExpandoMetaClass.enableGlobally();
    }
    public def initializeClasses()
    {
        def classMap=[:];
        classMap.RsTopologyObjectOperations=RsTopologyObjectOperations;
        RsTopologyObjectOperationsTest.initializeClassesFrom(classMap);
    }
    public static def initializeClassesFrom(classesToLoad)
    {
        RsTopologyObjectOperationsTest.classes=classesToLoad;        
    }
    public static void testRemoveDeletesRsObjectStateInstance()
     {
         CompassForTests.addOperationSupport(RsTopologyObject,classes.RsTopologyObjectOperations);

         def object=RsTopologyObject.add(name:"testobject");
         assertFalse(object.hasErrors());

         assertEquals(0,RsObjectState.list().size());

         object.getState();
         assertEquals(1,RsObjectState.list().size());

         object.remove();

         assertEquals(0,RsObjectState.list().size());
     }
    
     public static void testSaveStateAndLoadState()
     {

        CompassForTests.addOperationSupport(RsTopologyObject,classes.RsTopologyObjectOperations);

        def object=RsTopologyObject.add(name:"testobject");
        assertFalse(object.hasErrors());

        assertEquals(0,RsObjectState.list().size());
        assertEquals(Constants.NOTSET,object.loadState())

        def newState=5
        object.saveState(newState);
        assertEquals(1,RsObjectState.list().size());
        assertEquals(newState,RsObjectState.get(objectId:object.id).state);
        assertEquals(newState,object.loadState())

        newState=3
        object.saveState(newState);
        assertEquals(1,RsObjectState.list().size());
        assertEquals(newState,RsObjectState.get(objectId:object.id).state);
        assertEquals(newState,object.loadState())

     }

     public static void testNeedToCalculate()
     {

        CompassForTests.addOperationSupport(RsTopologyObject,classes.RsTopologyObjectOperations);

        def object=RsTopologyObject.add(name:"testobject");
        assertFalse(object.hasErrors());

        //when old and new are notset
        5.times{ counter ->
            def currentState=counter+1
            assertTrue(object.needToCalculate(currentState,Constants.NOTSET,Constants.NOTSET))
        }

        5.times{ counter ->
            def currentState=counter+1
            //when new is higher than current   , and current is not same as old
            assertTrue(object.needToCalculate(currentState,Constants.NOTSET,currentState+1))
            assertTrue(object.needToCalculate(currentState,currentState*3,currentState+1))
            assertTrue(object.needToCalculate(currentState,currentState/3,currentState+1))
            //when new is less than current   , and current is not same as old
            assertFalse(object.needToCalculate(currentState,Constants.NOTSET,currentState-1))
            assertFalse(object.needToCalculate(currentState,currentState*3,currentState-1))
            assertFalse(object.needToCalculate(currentState,currentState/3,currentState-1))

        }

        5.times{ counter ->
            def currentState=counter+1
            //when old is same as current and new is less than current
            assertTrue(object.needToCalculate(currentState,currentState,currentState-1))
            //false cases when new is less than current but old is not same as current
            assertFalse(object.needToCalculate(currentState,currentState+1,currentState-1))
        }
        //other false cases
        5.times{ counter ->
            def currentState=counter+1
            //when all are same
            assertFalse(object.needToCalculate(currentState,currentState,currentState))
            //when current higher than old , old higher than new
            assertFalse(object.needToCalculate(currentState+2,currentState+1,currentState))
            //when old and new are same and current is higher
            assertFalse(object.needToCalculate(currentState+1,currentState,currentState))
            
            assertFalse(object.needToCalculate(currentState+1,currentState,currentState-1))
        }
     }

     public static void testFindMaxSeverity()
     {

        CompassForTests.addOperationSupport(RsTopologyObject,classes.RsTopologyObjectOperations);

        def object=RsTopologyObject.add(name:"testobject");
        assertFalse(object.hasErrors());
        5.times{ counter ->
            //need to calculate is true
            assertEquals(Constants.INDETERMINATE,object.findMaxSeverity(counter+1,Constants.NOTSET,Constants.NOTSET));
        }
        RsEvent.add(name:"ev1",elementName:object.name,severity:1)
        RsEvent.add(name:"ev2",elementName:object.name,severity:2)
        RsEvent.add(name:"ev3",elementName:object.name,severity:3)
        //these 2 events here must not effect calculation
        RsEvent.add(name:"ev4",severity:4)
        RsEvent.add(name:"ev5",severity:5)

        assertEquals(5,RsEvent.countHits("alias:*"));

        5.times{ counter ->
            //need to calculate is true
            assertEquals(3,object.findMaxSeverity(counter+1,Constants.NOTSET,Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter+1,object.findMaxSeverity(counter+1,counter+1,counter+1));
        }
     }

     public static void testCriticalPercent()
     {

        CompassForTests.addOperationSupport(RsTopologyObject,classes.RsTopologyObjectOperations);

        def object=RsTopologyObject.add(name:"testobject");
        assertFalse(object.hasErrors());

        def eventCount=10;
        def events=[];
        eventCount.times{ counter ->
            events.add(RsEvent.add(name:"ev${counter}",elementName:object.name,severity:Constants.INDETERMINATE))
        }
        //these 2 events here must not effect calculation
        RsEvent.add(name:"otherev1",severity:1)
        RsEvent.add(name:"otherev2",severity:1)

        assertEquals(eventCount+2,RsEvent.countHits("alias:*"));
        //testing with 0 critical count
        int criticalCount=0;
        assertEquals(criticalCount,RsEvent.countHits("elementName:${object.name} AND severity:${Constants.CRITICAL}"));
        5.times{ counter ->
            //need to calculate is true
            assertEquals(Constants.INDETERMINATE,object.criticalPercent(counter+1,Constants.NOTSET,Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter+1,object.criticalPercent(counter+1,counter+1,counter+1));
        }

        //testing with critical count 1 less than MAJOR PERCENTAGE

        //we update some of the events severity : critical until the limit of major percentage
        criticalCount=Math.floor((eventCount*Constants.MAJOR_PERCENTAGE/100))-1;
        criticalCount.times{ index ->
            events[index].update(severity:Constants.CRITICAL);
        }
        assertEquals(criticalCount,RsEvent.countHits("elementName:${object.name} AND severity:${Constants.CRITICAL}"));
        
        5.times{ counter ->    
            //need to calculate is true
            assertEquals(Constants.INDETERMINATE,object.criticalPercent(counter+1,Constants.NOTSET,Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter+1,object.criticalPercent(counter+1,counter+1,counter+1));
        }

        //testing with critical count 1 less than CRITICAL PERCENTAGE
        criticalCount=Math.floor((eventCount*Constants.CRITICAL_PERCENTAGE/100))-1;
        criticalCount.times{ index ->
            events[index].update(severity:Constants.CRITICAL);
        }
        assertEquals(criticalCount,RsEvent.countHits("elementName:${object.name} AND severity:${Constants.CRITICAL}"));

        5.times{ counter ->    
            //need to calculate is true
            assertEquals(Constants.MAJOR,object.criticalPercent(counter+1,Constants.NOTSET,Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter+1,object.criticalPercent(counter+1,counter+1,counter+1));
        }

        //testing with critical count 1 more than CRITICAL PERCENTAGE
        criticalCount=Math.floor((eventCount*Constants.CRITICAL_PERCENTAGE/100))+1;
        criticalCount.times{ index ->
            events[index].update(severity:Constants.CRITICAL);
        }
        assertEquals(criticalCount,RsEvent.countHits("elementName:${object.name} AND severity:${Constants.CRITICAL}"));

        5.times{ counter ->
            //need to calculate is true
            assertEquals(Constants.CRITICAL,object.criticalPercent(counter+1,Constants.NOTSET,Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter+1,object.criticalPercent(counter+1,counter+1,counter+1));
        }

        //testing with all critical
        criticalCount=eventCount;
        criticalCount.times{ index ->
            events[index].update(severity:Constants.CRITICAL);
        }
        assertEquals(criticalCount,RsEvent.countHits("elementName:${object.name} AND severity:${Constants.CRITICAL}"));

        5.times{ counter ->
            //need to calculate is true
            assertEquals(Constants.CRITICAL,object.criticalPercent(counter+1,Constants.NOTSET,Constants.NOTSET));
            //need to calculate is false
            assertEquals(counter+1,object.criticalPercent(counter+1,counter+1,counter+1));
        }
     }

     public static void testCalculateStateCallsFindMaxSeverity()
     {

        int findMaxSeverityReturnValue;
        def callParams=[:]
        classes.RsTopologyObjectOperations.metaClass.findMaxSeverity = {currentState,  oldPropagatedState, newPropagatedState  ->
            println "findmax in test"
            callParams=[currentState:currentState,oldPropagatedState:oldPropagatedState,newPropagatedState:newPropagatedState]
            return findMaxSeverityReturnValue;
        }

        CompassForTests.addOperationSupport(RsTopologyObject,classes.RsTopologyObjectOperations);

        def object=RsTopologyObject.add(name:"testobject");
        assertFalse(object.hasErrors());

        5.times{ counter ->
            callParams=[:]
            findMaxSeverityReturnValue=counter+1;
            assertEquals(counter+1,object.calculateState(1,2,3));
            assertEquals(1,callParams.currentState);
            assertEquals(2,callParams.oldPropagatedState);
            assertEquals(3,callParams.newPropagatedState);
        }
     }

     public static void testCurrentStateCallsLoadState()
     {

        int loadStateReturnValue;
        classes.RsTopologyObjectOperations.metaClass.loadState = {  ->
            println "loadState in test"
            return loadStateReturnValue;
        }

        CompassForTests.addOperationSupport(RsTopologyObject,classes.RsTopologyObjectOperations);

        def object=RsTopologyObject.add(name:"testobject");
        assertFalse(object.hasErrors());

        5.times{ counter ->
            loadStateReturnValue=counter+1;
            assertEquals(counter+1,object.currentState());
        }
     }

    public static void testGetState()
    {

        int calculateStateReturnValue;
        def callParams=[:];
         classes.RsTopologyObjectOperations.metaClass.calculateState = {currentState,  oldPropagatedState, newPropagatedState  ->
            println "calculateState in test"
            callParams=[currentState:currentState,oldPropagatedState:oldPropagatedState,newPropagatedState:newPropagatedState]
            return calculateStateReturnValue;

        }

        CompassForTests.addOperationSupport(RsTopologyObject,classes.RsTopologyObjectOperations);

        def object=RsTopologyObject.add(name:"testobject");
        assertFalse(object.hasErrors());


        //test that if no state is calculated before , state calculation is done  and result saved
        5.times{ counter ->
            callParams=[:];
            RsObjectState.removeAll();
            assertEquals(Constants.NOTSET,object.currentState());

            calculateStateReturnValue=counter+1;
            def calculatedState=counter+1;
            assertEquals(calculatedState,object.getState());

            assertEquals(calculatedState,object.loadState());
            assertEquals(calculatedState,RsObjectState.get(objectId:object.id).state);
                      
            assertEquals(Constants.NOTSET,callParams.currentState);
            assertEquals(Constants.NOTSET,callParams.oldPropagatedState);
            assertEquals(Constants.NOTSET,callParams.newPropagatedState);
        }
        //test that if state is calculated before not state calculation is not done and result is returned
        //save the state of the object
        RsObjectState.removeAll();
        assertEquals(Constants.NOTSET,object.currentState());
        def newState=3;
        object.saveState(newState);
        assertEquals(newState,object.loadState());
        //cal getState
        callParams=[:];
        assertEquals(newState,object.getState());
        assertEquals(0,callParams.size());



    }
    public static void testSetState()
    {

        int calculateStateReturnValue;
        def calculateStateCallParams=[:];
        def propagateStateStateCallParams=[:];
        
        classes.RsTopologyObjectOperations.metaClass.calculateState = {currentState,  oldPropagatedState, newPropagatedState  ->
            println "calculateState in test"
            calculateStateCallParams=[currentState:currentState,oldPropagatedState:oldPropagatedState,newPropagatedState:newPropagatedState]
            return calculateStateReturnValue;

        }
        classes.RsTopologyObjectOperations.metaClass.propagateState = {oldState, newState  ->
            println "calculateState in test"
            propagateStateStateCallParams=[oldState:oldState,newState:newState]

        }

        CompassForTests.addOperationSupport(RsTopologyObject,classes.RsTopologyObjectOperations);
        def object=RsTopologyObject.add(name:"testobject");
        assertFalse(object.hasErrors());


        //current state and calculated state is different , save state is called and propagation done
        5.times{ counter ->
            calculateStateCallParams=[:];
            propagateStateStateCallParams=[:];

            def savedState=counter+2
            object.saveState(savedState)
            assertEquals(savedState,object.currentState());

            calculateStateReturnValue=counter+1;
            def calculatedState=counter+1;
            assertEquals(calculatedState,object.setState(counter+1));

            assertEquals(calculatedState,object.loadState());
            assertEquals(calculatedState,RsObjectState.get(objectId:object.id).state);

            assertEquals(savedState,calculateStateCallParams.currentState);
            assertEquals(Constants.NOTSET,calculateStateCallParams.oldPropagatedState);
            assertEquals(calculatedState,calculateStateCallParams.newPropagatedState);

            assertEquals(savedState,propagateStateStateCallParams.oldState);
            assertEquals(calculatedState,propagateStateStateCallParams.newState);

        }

        //current state and calculated state is different old propagated state is given, save state is called and propagation done
        5.times{ counter ->
            calculateStateCallParams=[:];
            propagateStateStateCallParams=[:];

            def savedState=counter+2
            object.saveState(savedState)
            assertEquals(savedState,object.currentState());

            calculateStateReturnValue=counter+1;
            def calculatedState=counter+1;
            def oldPropagatedState=10;
            assertEquals(calculatedState,object.setState(counter+1,oldPropagatedState));

            assertEquals(calculatedState,object.loadState());
            assertEquals(calculatedState,RsObjectState.get(objectId:object.id).state);

            assertEquals(savedState,calculateStateCallParams.currentState);
            assertEquals(oldPropagatedState,calculateStateCallParams.oldPropagatedState);
            assertEquals(calculatedState,calculateStateCallParams.newPropagatedState);

            assertEquals(savedState,propagateStateStateCallParams.oldState);
            assertEquals(calculatedState,propagateStateStateCallParams.newState);

        }

        //current state and calculated state are same, set propagation is not called
        5.times{ counter ->
            calculateStateCallParams=[:];
            propagateStateStateCallParams=[:];

            def savedState=counter+1
            object.saveState(savedState)
            assertEquals(savedState,object.currentState());

            calculateStateReturnValue=counter+1;
            def calculatedState=counter+1;
            assertEquals(calculatedState,object.setState(counter+1));
            assertEquals(calculatedState,savedState);

            assertEquals(calculatedState,object.loadState());
            assertEquals(calculatedState,RsObjectState.get(objectId:object.id).state);

            assertEquals(savedState,calculateStateCallParams.currentState);
            assertEquals(Constants.NOTSET,calculateStateCallParams.oldPropagatedState);
            assertEquals(calculatedState,calculateStateCallParams.newPropagatedState);

            assertEquals(0,propagateStateStateCallParams.size());
        }
    }
     public static void testCalculateWeight()
     {

        CompassForTests.addOperationSupport(RsTopologyObject,classes.RsTopologyObjectOperations);

        //add 1 object and  test calculate weight
        //Tree : object
        def object=RsTopologyObject.add(name:"testobject");
        assertFalse(object.hasErrors());

        assertEquals(1,RsTopologyObject.countHits("alias:*"));
        assertEquals(1,object.calculateWeight());
        assertEquals(0,object.parentObjects.size());

        //add 3 parentobjects and test calculate weight
        //Tree  object < parentLevel1
        3.times{ counter->
            RsTopologyObject.add(name:"parentlevel1_${counter}",childObjects:[object]);
        }

        object=RsTopologyObject.get(name:"testobject");
        assertEquals(3,object.parentObjects.size());
        assertEquals(1+3,object.calculateWeight());

        object.parentObjects.each{ parentObject->
            assertEquals(1,parentObject.calculateWeight());    
        }

        //add 6 parent object for each of the parent objects of the object
        //Tree  object < parentLevel1 <  parentLevel2
        object.parentObjects.each{ parentObject->
            3.times{ counter->
                RsTopologyObject.add(name:"parentlevel2a_${parentObject.name}_${counter}",childObjects:[parentObject]);
                RsTopologyObject.add(name:"parentlevel2b_${parentObject.name}_${counter}",childObjects:[parentObject]);
            }
        }

        object=RsTopologyObject.get(name:"testobject");
        assertEquals(3,object.parentObjects.size());
        assertEquals(1+3+18,object.calculateWeight());

        object.parentObjects.each{ parentObject ->
            assertEquals(6,parentObject.parentObjects.size());
            assertEquals(1+6,parentObject.calculateWeight());
            parentObject.parentObjects.each { parentObject2  ->
                assertEquals(0,parentObject2.parentObjects.size());
                assertEquals(1,parentObject2.calculateWeight());
            }

        }


     }
     public static void testPropagateStateCallsSetStateOfParentObjects()
     {

        def setStateStateCallParams=[:];
        classes.RsTopologyObjectOperations.metaClass.setState = {newState, oldState   ->
            println "calculateState in test"
            setStateStateCallParams[id]=[oldState:oldState,newState:newState]

        }
        CompassForTests.addOperationSupport(RsTopologyObject,classes.RsTopologyObjectOperations);

        //add 1 object and  test calculate weight
        //Tree : object
        def object=RsTopologyObject.add(name:"testobject");
        assertFalse(object.hasErrors());

        assertEquals(1,RsTopologyObject.countHits("alias:*"));


        //add 3 parentobjects and test calculate weight
        //Tree  object < parentLevel1
        3.times{ counter->
            RsTopologyObject.add(name:"parentlevel1_${counter}",childObjects:[object]);
        }

        object=RsTopologyObject.get(name:"testobject");
        assertEquals(3,object.parentObjects.size());

        def oldState=1;
        def newState=2;
        object.propagateState(oldState,newState);

        assertEquals(3,setStateStateCallParams.size());
        
        object.parentObjects.each{ parentObject ->
            assertEquals(oldState,setStateStateCallParams[parentObject.id].oldState)
            assertEquals(newState,setStateStateCallParams[parentObject.id].newState)
        }
     }
     


}