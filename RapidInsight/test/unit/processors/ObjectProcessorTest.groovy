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
        GroovySystem.metaClassRegistry.removeMetaClass(StateCalculator)
        GroovySystem.metaClassRegistry.removeMetaClass(InMaintenanceCalculator)
        ExpandoMetaClass.enableGlobally();
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
        ObjectProcessorTest.object.objectIsAdded=[object:object,objectid:object.id];
    }
    static def objectIsUpdated(object,changedProps){
        ObjectProcessorTest.callParams.objectIsUpdated=[object:object,changedProps:changedProps];
    }
    static def objectIsDeleted(object){
        ObjectProcessorTest.callParams.objectIsDeleted=[object:object];
    }

}


