
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.util.RapidDateUtilities
import com.ifountain.rcmdb.test.util.RsApplicationTestUtils
import application.RsApplication

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 21, 2009
* Time: 8:17:14 PM
* To change this template use File | Settings | File Templates.
*/
class RsRiEventOperationsTest extends RapidCmdbWithCompassTestCase{
     public void setUp() {
        super.setUp();
        clearMetaClasses();

         initialize([RsEvent,RsHistoricalEvent,RsRiEvent,RsRiHistoricalEvent,RsEventJournal,RsTopologyObject,RsApplication,RsComputerSystem], []);
         CompassForTests.addOperationSupport(RsRiEvent,RsRiEventOperations);
         RsApplicationTestUtils.initializeRsApplicationOperations(RsApplication);
         RsApplicationTestUtils.clearProcessors();

    }

    public void tearDown() {

        clearMetaClasses();
        super.tearDown();
    }
    private void clearMetaClasses()
     {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsRiEvent)
        GroovySystem.metaClassRegistry.removeMetaClass(RsRiEventOperations)
        GroovySystem.metaClassRegistry.removeMetaClass(RsComputerSystem)
        ExpandoMetaClass.enableGlobally();
     }


     public void testNotifyAddsRsRiEvent()
     {



        assertEquals(0,RsRiEvent.count());
        assertEquals(0,RsEventJournal.count());

        def addProps=[name:"ev1",identifier:"ev1",severity:5];
        def timeBeforeCall=Date.now();
        Thread.sleep(100);

        def addedEvent=RsRiEvent.notify(addProps);
        assertFalse(addedEvent.hasErrors());

        assertEquals(addedEvent.name,addProps.name);
        assertEquals(addedEvent.severity,addProps.severity);
        assertEquals(addedEvent.identifier,addProps.identifier);

        assertEquals(1,RsRiEvent.count());

        assertEquals(addedEvent.createdAt,addedEvent.changedAt);
        assertTrue(addedEvent.changedAt>timeBeforeCall);
        assertEquals(1,addedEvent.count);


        assertEquals(1,RsEventJournal.count());
        def addedJournal=RsEventJournal.search("eventId:${addedEvent.id}", ["sort":"id","order":"asc"]).results[0]

        assertEquals(addedJournal.eventId,addedEvent.id);
        assertEquals(addedJournal.eventName,addedEvent.identifier);
        assertEquals(addedJournal.rsTime,Date.toDate(addedEvent.changedAt));
        assertEquals(addedJournal.details,RsEventJournal.MESSAGE_CREATE);

        //Testing update
        def timeBeforeCall2=Date.now();
        Thread.sleep(100);
        def updateProps=[name:"ev1",identifier:"ev1",severity:1];
        def updatedEvent=RsRiEvent.notify(updateProps);
        assertEquals(updatedEvent.name,updateProps.name);
        assertEquals(updatedEvent.severity,updateProps.severity);
        assertEquals(updatedEvent.identifier,updateProps.identifier);

        assertTrue(updatedEvent.changedAt>updatedEvent.createdAt);
        assertTrue(updatedEvent.changedAt>timeBeforeCall2);
        assertEquals(2,updatedEvent.count);
        assertEquals(1,RsRiEvent.count());



        assertFalse(addedEvent.asMap() == updatedEvent.asMap());

        assertEquals(2,RsEventJournal.count());
        def addedJournal2=RsEventJournal.search("eventId:${addedEvent.id}", ["sort":"id","order":"asc"]).results[1]

        assertEquals(addedJournal2.eventId,updatedEvent.id);
        assertEquals(addedJournal2.eventName,updatedEvent.identifier);
        assertEquals(addedJournal2.rsTime,Date.toDate(updatedEvent.changedAt));
        assertEquals(addedJournal2.details,RsEventJournal.MESSAGE_UPDATE);

     }
    void testIfEventHasErrorsEventIsNotProcessed()
    {
        initialize([RsEvent,RsRiEvent,RsEventJournal,RsComputerSystem], []);



        CompassForTests.addOperationSupport(RsRiEvent,RsRiEventOperations);
        //we first test successfull case and propageElementstate is called
        assertEquals(0,RsRiEvent.count());
        assertEquals(0,RsEventJournal.count());

        def addProps=[name:"testev",identifier:"ev1",severity:5];
        def addedEvent=RsRiEvent.notify(addProps);
        assertFalse(addedEvent.hasErrors());
        assertEquals(1,RsRiEvent.count());
        assertEquals(1,RsEventJournal.count());


        //now we test the fail case
        RsRiEvent.removeAll();
        RsEventJournal.removeAll();


        assertEquals(0,RsRiEvent.count());
        assertEquals(0,RsEventJournal.count());

        addProps=[name:null,identifier:"ev1",severity:5];

        addedEvent=RsRiEvent.notify(addProps);
        assertTrue(addedEvent.hasErrors());
        assertEquals(0,RsRiEvent.count());
        assertEquals(0,RsEventJournal.count());

    }
     public void testNotifyDoesNotSetCreatedAtAndChangedAtIfGiven()
     {


         def addProps=[name:"ev1",identifier:"ev1",severity:5,createdAt:Date.now()-60000];
         def addedEvent=RsRiEvent.notify(addProps)
         assertEquals(addedEvent.name,addProps.name);
         assertEquals(addedEvent.createdAt,addProps.createdAt);

         RsRiEvent.removeAll();

         def addProps2=[name:"ev1",identifier:"ev1",severity:5,changedAt:Date.now()-60000];
         def addedEvent2=RsRiEvent.notify(addProps2)
         assertEquals(addedEvent2.name,addProps2.name);
         assertEquals(addedEvent2.changedAt,addProps2.changedAt);

         RsRiEvent.removeAll();

         def addProps3=[name:"ev1",identifier:"ev1",severity:5,createdAt:Date.now()-120000,changedAt:Date.now()-60000];
         def addedEvent3=RsRiEvent.notify(addProps3)
         assertEquals(addedEvent3.name,addProps3.name);
         assertEquals(addedEvent3.createdAt,addProps3.createdAt);
         assertEquals(addedEvent3.changedAt,addProps3.changedAt);
     }
     public void testNotifyDoesNotSetCountIfGiven(){

         def addProps=[name:"ev1",identifier:"ev1",severity:5];
         def addedEvent=RsRiEvent.notify(addProps)
         assertEquals(addedEvent.name,addProps.name);
         assertEquals(1,addedEvent.count);


         def udpateProps=[name:"ev1",identifier:"ev1",severity:5,count:55];
         def updatedEvent=RsRiEvent.notify(udpateProps)
         assertEquals(updatedEvent.name,udpateProps.name);
         assertEquals(updatedEvent.count,udpateProps.count);

     }


     public void testHistoricalEventModel()
     {


         def event=RsRiEvent.add(name:"testev");
         assertFalse(event.hasErrors());
         assertEquals(1,RsRiEvent.count());
         event.clear();
         assertEquals(0,RsRiEvent.count());
         assertEquals(1,RsRiHistoricalEvent.countHits("activeId:${event.id}"));
     }




}