import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RsApplicationTestUtils
import application.RsApplication




/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 21, 2009
* Time: 8:17:14 PM
* To change this template use File | Settings | File Templates.
*/
class RsEventOperationsTest extends RapidCmdbWithCompassTestCase{
     public void setUp() {
        super.setUp();

        initialize([RsEvent,RsHistoricalEvent,RsEventJournal,RsTopologyObject,RsApplication], []);
         CompassForTests.addOperationSupport(RsEvent,RsEventOperations);
         RsApplicationTestUtils.initializeRsApplicationOperations(RsApplication);
         RsApplicationTestUtils.clearProcessors();

    }

    public void tearDown() {

        super.tearDown();
    }

     public void testNotifyAddsRsEvent()
     {
         assertEquals(0,RsEvent.count());

         def addProps=[name:"ev1",severity:5];
         def addedEvent=RsEvent.notify(addProps);
         assertFalse(addedEvent.hasErrors());
         assertEquals(addedEvent.name,addProps.name);
         assertEquals(addedEvent.severity,addProps.severity);
         assertEquals(1,RsEvent.count());


         def updateProps=[name:"ev1",severity:1];
         def updatedEvent=RsEvent.notify(updateProps);
         assertEquals(updatedEvent.name,updateProps.name);
         assertEquals(updatedEvent.severity,updateProps.severity);
         assertEquals(1,RsEvent.count());


         assertFalse(addedEvent.asMap() == updatedEvent.asMap());
     }
     public void testClear()
     {
         assertEquals(0,RsEvent.count());

         def addProps=[name:"ev1",severity:5,elementName:"el1",owner:"owl1",count:3];
         def addedEvent=RsEvent.add(addProps);
         assertFalse(addedEvent.hasErrors());
         def eventId=addedEvent.id;

         assertEquals(1,RsEvent.count());
         assertEquals(0,RsEventJournal.count());
         assertEquals(0,RsHistoricalEvent.count());
         
         addedEvent.clear();

         assertEquals(0,RsEvent.count());
         assertEquals(1,RsEventJournal.count());
         assertEquals(1,RsHistoricalEvent.count());

         def journal=RsEventJournal.list()[0];
         assertEquals("cleared",journal.eventName);
         assertEquals(eventId,journal.eventId);
         checkTimeDiff (journal.rsTime.getTime());
         assertEquals(1,RsEventJournal.count());

         def historicalEvent=RsHistoricalEvent.list()[0];
         assertEquals(eventId,historicalEvent.activeId);
         assertEquals(journal.rsTime.getTime(),historicalEvent.clearedAt);

         addProps.each{ propName , propVal ->
            assertEquals(propVal,historicalEvent.getProperty(propName));
         }
         

     }
     public void testClearEventDoesNotCreateJournalEntryWhenCreateJournalIsFalse()
     {
         def addedEvent=RsEvent.add(name:"testev");
         assertEquals(1,RsEvent.count());
         
         addedEvent.clear(false,[:]);
         assertEquals(0,RsEvent.count());
         assertEquals(0,RsEventJournal.count());
         assertEquals(1,RsHistoricalEvent.count());

     }
     public void testClearWithExtraProperties()
     {
         def addedEvent=RsEvent.add(name:"testev",elementName:"testel",severity:1);
         def eventId=addedEvent.id;

         assertEquals(1,RsEvent.count());

         def extraProperties=[elementName:"testel2",severity:2,owner:"testowner",count:3];

         addedEvent.clear(true,extraProperties);
         assertEquals(0,RsEvent.count());
         assertEquals(1,RsEventJournal.count());
         assertEquals(1,RsHistoricalEvent.count());


         def historicalEvent=RsHistoricalEvent.list()[0];
         assertEquals(eventId,historicalEvent.activeId);
         assertEquals(addedEvent.name,historicalEvent.name);

         extraProperties.each{ propName , propVal ->
            assertEquals(propVal,historicalEvent.getProperty(propName));
         }

     }
     
     public void testAddToJournalWithName()
     {
         def event=RsEvent.add(name:"testev");

         assertEquals(1,RsEvent.count());
         assertEquals(0,RsEventJournal.count());
         def journalName="test_journal";
         event.addToJournal(journalName)
         assertEquals(1,RsEventJournal.count());

         def journal=RsEventJournal.list()[0];
         assertEquals(event.id,journal.eventId);
         assertEquals(journalName,journal.eventName);
         checkTimeDiff (journal.rsTime.getTime());

     }

     public void testAddToJournalWithNameAndDetails()
     {
         def event=RsEvent.add(name:"testev");

         assertEquals(1,RsEvent.count());
         assertEquals(0,RsEventJournal.count());

         def journalName="test_journal";
         def journalDetails="test_details";

         event.addToJournal(journalName,journalDetails)
         assertEquals(1,RsEventJournal.count());

         def journal=RsEventJournal.list()[0];
         assertEquals(event.id,journal.eventId);
         assertEquals(journalName,journal.eventName);
         assertEquals(journalDetails,journal.details);
         checkTimeDiff (journal.rsTime.getTime());

     }
     public void testAddToJournalWithPropsMap()
     {
         def event=RsEvent.add(name:"testev",);

         assertEquals(1,RsEvent.count());
         assertEquals(0,RsEventJournal.count());

         def props=[eventName:"Anything", details:"did anything",rsTime:new Date()];

         event.addToJournal(props);
         assertEquals(1,RsEventJournal.count());

         def journal=RsEventJournal.list()[0];
         assertEquals(event.id,journal.eventId);
         assertEquals(props.eventName,journal.eventName);
         assertEquals(props.details,journal.details);
         assertEquals(props.rsTime,journal.rsTime);

     }

     public void testAcknowledge()
     {

        def event = RsEvent.add(name:"Rsevent",owner:"Pinar");

        assertFalse(event.hasErrors());
        assertEquals(0,RsEventJournal.count());

        assert (event.owner == "Pinar" && event.acknowledged == false)
        event.acknowledge(true,"Berkay");
        event = RsEvent.get(name:"Rsevent");
        assert (event.owner == "Pinar" && event.acknowledged == true)
        checkTimeDiff(event.changedAt);

        //checking journal
        assertEquals(1,RsEventJournal.count());
        def journal = RsEventJournal.searchEvery("alias:*",[sort:"id",order:"desc",max:1])[0];
        assertEquals(event.id,journal.eventId);
        assertEquals("acknowledged",journal.eventName);
        checkTimeDiff(journal.rsTime.getTime());
        assertEquals("Acknowledged by Berkay",journal.details)
        assertEquals(event.changedAt,journal.rsTime.getTime())

        event = RsEvent.get(name:"Rsevent");
        event.acknowledge(false,"Pinar");
        event = RsEvent.get(name:"Rsevent");
        assert (event.owner == "Pinar" && event.acknowledged == false)
        checkTimeDiff(event.changedAt);

        //checking journal
        assertEquals(2,RsEventJournal.count());
        journal = RsEventJournal.searchEvery("alias:*",[sort:"id",order:"desc",max:1])[0];
        assertEquals(event.id,journal.eventId);
        assertEquals("unacknowledged",journal.eventName);
        checkTimeDiff(journal.rsTime.getTime());
        assertEquals("UnAcknowledged by Pinar",journal.details)
        assertEquals(event.changedAt,journal.rsTime.getTime())

        event = RsEvent.get(name:"Rsevent");
        event.acknowledge(true,"Pinar");
        event = RsEvent.get(name:"Rsevent");
        assert (event.owner == "Pinar" && event.acknowledged == true)
        checkTimeDiff(event.changedAt);
        

        //checking journal
        assertEquals(3,RsEventJournal.count());
        journal = RsEventJournal.searchEvery("alias:*",[sort:"id",order:"desc",max:1])[0];
        assertEquals(event.id,journal.eventId);
        assertEquals("acknowledged",journal.eventName);
        checkTimeDiff(journal.rsTime.getTime());
        assertEquals("Acknowledged by Pinar",journal.details)
        assertEquals(event.changedAt,journal.rsTime.getTime())
     }

     public void testSetOwnerShip()
     {
        def event = RsEvent.add(name:"RsEvent2",owner:"Berkay");
        assertFalse(event.hasErrors());

        assertEquals(0,RsEventJournal.count());

        event = RsEvent.get(name:"RsEvent2");
        event.setOwnership(true,"Pinar");
        event = RsEvent.get(name:"RsEvent2");
        assert (event.owner == "Pinar")
        checkTimeDiff(event.changedAt);

        //checking journal
        assertEquals(1,RsEventJournal.count());
        def journal = RsEventJournal.searchEvery("alias:*",[sort:"id",order:"desc",max:1])[0];
        assertEquals(event.id,journal.eventId);
        assertEquals("TakeOwnership",journal.eventName);
        checkTimeDiff(journal.rsTime.getTime());
        assertEquals("TakeOwnership by Pinar",journal.details)
        assertEquals(event.changedAt,journal.rsTime.getTime())


        event.setOwnership(false,"Admin");
        event = RsEvent.get(name:"RsEvent2");
        assert (event.owner == "")
        checkTimeDiff(event.changedAt);
        
        //checking journal
        assertEquals(2,RsEventJournal.count());
        journal = RsEventJournal.searchEvery("alias:*",[sort:"id",order:"desc",max:1])[0];
        assertEquals(event.id,journal.eventId);
        assertEquals("ReleaseOwnership",journal.eventName);
        checkTimeDiff(journal.rsTime.getTime());
        assertEquals("ReleaseOwnership by Admin",journal.details)
        assertEquals(event.changedAt,journal.rsTime.getTime())
     }
     protected void checkTimeDiff(time)
     {
         def timeDiff=Date.now()-time;
         assertTrue(timeDiff>=0);
         assertTrue(timeDiff<2000);
     }


}