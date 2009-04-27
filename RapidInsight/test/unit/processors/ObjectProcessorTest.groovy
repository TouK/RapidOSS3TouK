import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.RsUtilityTestUtils
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 24, 2009
* Time: 3:03:46 PM
* To change this template use File | Settings | File Templates.
*/
class ObjectProcessorTest extends RapidCmdbWithCompassTestCase{
    static def callParams=[:] ;
    
     public void setUp() {
        super.setUp();
        clearMetaClasses();
        initialize([RsEvent,RsUtility,RsInMaintenance,RsTopologyObject], []);
        CompassForTests.addOperationSupport (RsTopologyObject,RsTopologyObjectOperations);
        CompassForTests.addOperationSupport (RsInMaintenance,RsInMaintenanceOperations);
        RsUtilityTestUtils.initializeRsUtilityOperations (RsUtility);
        RsUtilityTestUtils.setToDefaultProcessors();
    }

    public void tearDown() {
        clearMetaClasses();
        super.tearDown();
    }
    public void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsTopologyObject)
        ExpandoMetaClass.enableGlobally();
    }


    public void testObjectInsert()
    {
        EventProcessor.beforeProcessors=["ObjectBeforeProcessor"];
        EventProcessor.afterProcessors=["ObjectAfterProcessor"];

        callParams.clear();

        def object=RsTopologyObject.add(name:"testObj");
        assertFalse(object.hasErrors());

        callParams.clear();

        assertEquals(2,callParams.size());
        assertEquals(object.name,callParams.eventInBeforeInsert.object.name);
        assertEquals(null,callParams.eventInBeforeInsert.objectid);
        assertEquals(object.name,callParams.eventIsAdded.object.name);
        assertEquals(object.id,callParams.eventIsAdded.objectid);
    }

    public void testObjectUpdate()
    {

        EventProcessor.beforeProcessors=["ObjectBeforeProcessor"];
        EventProcessor.afterProcessors=["ObjectAfterProcessor"];


        def event=RsEvent.add(name:"testEvent",severity:3,rsDatasource:"testds");
        assertFalse(event.hasErrors());

        callParams.clear();

        event.update(severity:5,rsDatasource:"dsfortest");
        assertFalse(event.hasErrors());

        assertEquals(2,callParams.size());

        assertEquals(event.name,callParams.eventInBeforeUpdate.event.name);
        assertEquals(3,callParams.eventInBeforeUpdate.changedProps.severity);
        assertEquals("testds",callParams.eventInBeforeUpdate.changedProps.rsDatasource);

        assertEquals(event.name,callParams.eventIsUpdated.event.name);
        assertEquals(3,callParams.eventIsUpdated.changedProps.severity);
        assertEquals("testds",callParams.eventIsUpdated.changedProps.rsDatasource);

    }

    public void testObjectDelete()
    {

        ObjectProcessor.beforeProcessors=["ObjectBeforeProcessor"];
        ObjectProcessor.afterProcessors=["ObjectAfterProcessor"];

        def object=RsTopologyObject.add(name:"testObj");
        assertFalse(object.hasErrors());

        callParams.clear();

        object.remove();
        assertEquals(0,RsTopologyObject.list().size());

        assertEquals(1,callParams.size());
        assertEquals(object.id,callParams.objectIsDeleted.object.id);

    }


}


public class ObjectBeforeProcessor
{
    static def objectInBeforeInsert(object){
         ObjectProcessorTest.callParams.objectInBeforeInsert=[object:object,objectid:object.id];
    }
    static def objectInBeforeUpdate(object,changedProps){
         ObjectProcessorTest.callParams.objectInBeforeUpdate=[object:object,changedProps:changedProps];
    }

}
public class ObjectAfterProcessor
{
    static def objectIsAdded(object){
        ObjectProcessorTest.callParams.objectIsAdded=[object:object,objectid:object.id];
    }
    static def objectIsUpdated(object,changedProps){
        ObjectProcessorTest.callParams.objectIsUpdated=[object:object,changedProps:changedProps];
    }
    static def objectIsDeleted(object){
        ObjectProcessorTest.callParams.objectIsDeleted=[object:object];
    }

}


