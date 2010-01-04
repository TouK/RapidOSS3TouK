import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidApplicationTestUtils
import application.RapidApplication

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
        initialize([RsEvent,RapidApplication,RsTopologyObject], []);
        CompassForTests.addOperationSupport (RsTopologyObject,RsTopologyObjectOperations);
        RapidApplicationTestUtils.initializeRapidApplicationOperations (RapidApplication);
        RapidApplicationTestUtils.clearProcessors();
    }

    public void tearDown() {
        RapidApplicationTestUtils.clearProcessors();
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
        ObjectProcessor.beforeProcessors=["ObjectBeforeProcessor"];
        ObjectProcessor.afterProcessors=["ObjectAfterProcessor"];

        callParams.clear();

        def object=RsTopologyObject.add(name:"testObj");
        assertFalse(object.hasErrors());


        assertEquals(2,callParams.size());
        assertEquals(object.name,callParams.objectInBeforeInsert.object.name);
        assertEquals(null,callParams.objectInBeforeInsert.objectid);
        assertEquals(object.name,callParams.objectIsAdded.object.name);
        assertEquals(object.id,callParams.objectIsAdded.objectid);
    }

    public void testObjectUpdate()
    {

        ObjectProcessor.beforeProcessors=["ObjectBeforeProcessor"];
        ObjectProcessor.afterProcessors=["ObjectAfterProcessor"];


        def object=RsTopologyObject.add(name:"testObj",className:"testCls",description:"desc");
        assertFalse(object.hasErrors());

        callParams.clear();

        object.update(className:"testCl2",description:"ddd");
        assertFalse(object.hasErrors());

        assertEquals(2,callParams.size());

        assertEquals(object.name,callParams.objectInBeforeUpdate.object.name);
        assertEquals("testCls",callParams.objectInBeforeUpdate.changedProps.className);
        assertEquals("desc",callParams.objectInBeforeUpdate.changedProps.description);

        assertEquals(object.name,callParams.objectIsUpdated.object.name);
        assertEquals("testCls",callParams.objectIsUpdated.changedProps.className);
        assertEquals("desc",callParams.objectIsUpdated.changedProps.description);

    }

    public void testObjectDelete()
    {

        ObjectProcessor.beforeProcessors=["ObjectBeforeProcessor"];
        ObjectProcessor.afterProcessors=["ObjectAfterProcessor"];

        def object=RsTopologyObject.add(name:"testObj");
        assertFalse(object.hasErrors());

        callParams.clear();

        object.remove();
        assertEquals(0,RsTopologyObject.count());

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


