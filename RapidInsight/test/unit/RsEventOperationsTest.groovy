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
     public void testHistoricalEventModel()
     {
         def event=RsEvent.add(name:"testev");
         assertFalse(event.hasErrors());
         assertEquals(1,RsEvent.countHits("alias:*"));
         event.clear();
         assertEquals(0,RsEvent.countHits("alias:*"));
         assertEquals(1,RsHistoricalEvent.countHits("activeId:${event.id}"));
     }

}