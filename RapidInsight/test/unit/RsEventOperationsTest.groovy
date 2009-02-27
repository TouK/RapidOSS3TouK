import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests

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

    }

    public void tearDown() {
        super.tearDown();
    }

     public void testNotifyAddsRsEvent()
     {
         initialize([RsEvent], []);
         CompassForTests.addOperationSupport(RsEvent,RsEventOperations);

         assertEquals(0,RsEvent.list().size());

         def addProps=[name:"ev1",severity:5];
         def addedEvent=RsEvent.notify(addProps);
         assertFalse(addedEvent.hasErrors());
         assertEquals(addedEvent.name,addProps.name);
         assertEquals(addedEvent.severity,addProps.severity);
         assertEquals(1,RsEvent.list().size());


         def updateProps=[name:"ev1",severity:1];
         def updatedEvent=RsEvent.notify(updateProps);
         assertEquals(updatedEvent.name,updateProps.name);
         assertEquals(updatedEvent.severity,updateProps.severity);
         assertEquals(1,RsEvent.list().size());


         assertNotSame(addedEvent,updatedEvent);
     }

}