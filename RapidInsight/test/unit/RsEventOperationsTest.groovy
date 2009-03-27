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
class RsEventOperationsTest extends RapidCmdbWithCompassTestCase{
     public void setUp() {
        super.setUp();
        RapidDateUtilities.registerDateUtils();
        registerDefaultConverters();
    }
     def registerDefaultConverters()
    {
        def dateFormat = "yyyy-dd-MM HH:mm:ss";
        RapidConvertUtils.getInstance().register(new DateConverter(dateFormat), Date.class)
        RapidConvertUtils.getInstance().register(new LongConverter(), Long.class)
        RapidConvertUtils.getInstance().register(new DoubleConverter(), Double.class)
        RapidConvertUtils.getInstance().register(new BooleanConverter(), Boolean.class)
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


         assertFalse(addedEvent.asMap() == updatedEvent.asMap());
     }
     public void testHistoricalEventModel()
     {
         initialize([RsEvent,RsHistoricalEvent,RsEventJournal,RsTopologyObject], []);
         CompassForTests.addOperationSupport(RsEvent,RsEventOperations);

         def event=RsEvent.add(name:"testev");
         assertFalse(event.hasErrors());
         assertEquals(1,RsEvent.countHits("alias:*"));
         event.clear();
         assertEquals(0,RsEvent.countHits("alias:*"));
         assertEquals(1,RsHistoricalEvent.countHits("activeId:${event.id}"));
     }

}