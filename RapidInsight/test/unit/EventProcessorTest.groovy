import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.RsApplicationTestUtils
import com.ifountain.rcmdb.test.util.CompassForTests
import application.RsApplication

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 24, 2009
* Time: 3:03:46 PM
* To change this template use File | Settings | File Templates.
*/
class EventProcessorTest extends RapidCmdbWithCompassTestCase{
    static def callParams=[:];

     public void setUp() {
        super.setUp();
        clearMetaClasses();
        initialize([RsEvent,RsApplication,RsTopologyObject], []);
        CompassForTests.addOperationSupport (RsEvent,RsEventOperations);
        RsApplicationTestUtils.initializeRsApplicationOperations (RsApplication);
        RsApplicationTestUtils.clearProcessors();
    }

    public void tearDown() {
        RsApplicationTestUtils.clearProcessors();
        clearMetaClasses();
        super.tearDown();
    }
    public void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsTopologyObject)
        ExpandoMetaClass.enableGlobally();
    }



    public void testEventInsert()
    {
        EventProcessor.beforeProcessors=["EventBeforeProcessor"];
        EventProcessor.afterProcessors=["EventAfterProcessor"];

        callParams.clear();

        def event=RsEvent.add(name:"testEvent");
        assertFalse(event.hasErrors());

        assertEquals(2,callParams.size());
        assertEquals(event.name,callParams.eventInBeforeInsert.event.name);
        assertEquals(null,callParams.eventInBeforeInsert.eventid);
        assertEquals(event.name,callParams.eventIsAdded.event.name);
        assertEquals(event.id,callParams.eventIsAdded.eventid);
    }

    public void testEventUpdate()
    {

        EventProcessor.beforeProcessors=["EventBeforeProcessor"];
        EventProcessor.afterProcessors=["EventAfterProcessor"];


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
    public void testEventDelete()
    {
        EventProcessor.beforeProcessors=["EventBeforeProcessor"];
        EventProcessor.afterProcessors=["EventAfterProcessor"];

        def event=RsEvent.add(name:"testEvent",severity:3,rsDatasource:"testds");
        assertFalse(event.hasErrors());

        callParams.clear();

        event.remove();
        assertEquals(0,RsEvent.count());

        assertEquals(1,callParams.size());
        assertEquals(event.id,callParams.eventIsDeleted.event.id);

    }
}

public class EventBeforeProcessor
{
    static def eventInBeforeInsert(event){
         EventProcessorTest.callParams.eventInBeforeInsert=[event:event,eventid:event.id];
    }
    static def eventInBeforeUpdate(event,changedProps){
         EventProcessorTest.callParams.eventInBeforeUpdate=[event:event,changedProps:changedProps];
    }

}
public class EventAfterProcessor
{
    static def eventIsAdded(event){
        EventProcessorTest.callParams.eventIsAdded=[event:event,eventid:event.id];
    }
    static def eventIsUpdated(event,changedProps){
        EventProcessorTest.callParams.eventIsUpdated=[event:event,changedProps:changedProps];
    }
    static def eventIsDeleted(event){
        EventProcessorTest.callParams.eventIsDeleted=[event:event];
    }

}



