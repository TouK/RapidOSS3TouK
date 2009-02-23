
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.util.RapidDateUtilities

import com.ifountain.rcmdb.converter.*

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
        RapidDateUtilities.registerDateUtils();
        registerDefaultConverters();


    }

    public void tearDown() {
        clearMetaClasses();
        super.tearDown();
    }
    private void clearMetaClasses()
     {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsComputerSystem)        
        ExpandoMetaClass.enableGlobally();
     }
     def registerDefaultConverters()
    {
        def dateFormat = "yyyy-dd-MM HH:mm:ss";
        RapidConvertUtils.getInstance().register(new DateConverter(dateFormat), Date.class)
        RapidConvertUtils.getInstance().register(new LongConverter(), Long.class)
        RapidConvertUtils.getInstance().register(new DoubleConverter(), Double.class)
        RapidConvertUtils.getInstance().register(new BooleanConverter(), Boolean.class)
    }

     public void testNotifyAddsRsRiEvent()
     {
        initialize([RsEvent,RsRiEvent,RsEventJournal,RsComputerSystem], []);
        CompassForTests.addOperationSupport(RsRiEvent,RsRiEventOperations);
        
        assertEquals(RsRiEvent.eventModel(),RsRiEvent);
        
        assertEquals(0,RsRiEvent.list().size());
        assertEquals(0,RsEventJournal.list().size());

        def addProps=[name:"ev1",identifier:"ev1",severity:5];
        def timeBeforeCall=Date.now();
        Thread.sleep(100);
        
        def addedEvent=RsRiEvent.notify(addProps);
        assertFalse(addedEvent.hasErrors());

        assertEquals(addedEvent.name,addProps.name);
        assertEquals(addedEvent.severity,addProps.severity);
        assertEquals(addedEvent.identifier,addProps.identifier);

        assertEquals(1,RsRiEvent.list().size());

        assertEquals(addedEvent.createdAt,addedEvent.changedAt);
        assertTrue(addedEvent.changedAt>timeBeforeCall);
        assertEquals(1,addedEvent.count);


        assertEquals(1,RsEventJournal.list().size());
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
        assertEquals(1,RsRiEvent.list().size());



        assertNotSame(addedEvent,updatedEvent);

        assertEquals(2,RsEventJournal.list().size());
        def addedJournal2=RsEventJournal.search("eventId:${addedEvent.id}", ["sort":"id","order":"asc"]).results[1]

        assertEquals(addedJournal2.eventId,updatedEvent.id);
        assertEquals(addedJournal2.eventName,updatedEvent.identifier);
        assertEquals(addedJournal2.rsTime,Date.toDate(updatedEvent.changedAt));
        assertEquals(addedJournal2.details,RsEventJournal.MESSAGE_UPDATE);
        
     }
     public void testNotifyDoesNotSetCreatedAtAndChangedAtIfGiven()
     {
         initialize([RsEvent,RsRiEvent,RsEventJournal,RsComputerSystem], []);
         CompassForTests.addOperationSupport(RsRiEvent,RsRiEventOperations);
        
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
         
         initialize([RsEvent,RsRiEvent,RsEventJournal,RsComputerSystem], []);
         CompassForTests.addOperationSupport(RsRiEvent,RsRiEventOperations);

         def addProps=[name:"ev1",identifier:"ev1",severity:5];
         def addedEvent=RsRiEvent.notify(addProps)
         assertEquals(addedEvent.name,addProps.name);
         assertEquals(1,addedEvent.count);


         def udpateProps=[name:"ev1",identifier:"ev1",severity:5,count:55];
         def updatedEvent=RsRiEvent.notify(udpateProps)
         assertEquals(updatedEvent.name,udpateProps.name);
         assertEquals(updatedEvent.count,udpateProps.count);

     }
     public void testPropagateElementStateAndNotifyCallsPropagateElementState(){
         initialize([RsObjectState,RsEvent,RsRiEvent,RsEventJournal,RsComputerSystem], []);
         CompassForTests.addOperationSupport(RsRiEvent,RsRiEventOperations);
         
         def callParams=[:]
         RsComputerSystem.metaClass.setState = { newPropagatedState ->            
            callParams.state=newPropagatedState;
            
         }

         def element=RsComputerSystem.add(name:"testsys");
         assertFalse(element.hasErrors());


         def event=RsRiEvent.add(name:"testev",elementId:element.id,severity:5);
         assertFalse(event.hasErrors());

         event.propagateElementState();
         assertEquals(callParams.state,event.severity)

         callParams=[:];

        def event2=RsRiEvent.notify(name:"testev",elementId:element.id,severity:3);
        assertFalse(event.hasErrors());

        assertEquals(callParams.state,event2.severity)

     }



}