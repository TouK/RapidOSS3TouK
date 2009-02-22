
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
        RapidDateUtilities.registerDateUtils();
        registerDefaultConverters();

    }

    public void tearDown() {
        super.tearDown();
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

         assertEquals(0,RsRiEvent.list().size());

         def addProps=[name:"ev1",identifier:"ev1",severity:5];
         def addedEvent=RsRiEvent.notify(addProps);
         assertFalse(addedEvent.hasErrors());
         assertEquals(addedEvent.name,addProps.name);
         assertEquals(addedEvent.severity,addProps.severity);
         assertEquals(1,RsRiEvent.list().size());

         def addedEventFromRepo=RsRiEvent.get(name:addProps.name);
         assertEquals(addedEventFromRepo.name,addProps.name);
         assertEquals(addedEventFromRepo.severity,addProps.severity);
         assertEquals(addedEvent,addedEventFromRepo);


         def updateProps=[name:"ev1",severity:1];
         def updatedEvent=RsRiEvent.notify(updateProps);
         assertEquals(updatedEvent.name,updateProps.name);
         assertEquals(updatedEvent.severity,updateProps.severity);
         assertEquals(1,RsRiEvent.list().size());

         def updatedEventFromRepo=RsRiEvent.get(name:updateProps.name);
         assertEquals(updatedEventFromRepo.name,updateProps.name);
         assertEquals(updatedEventFromRepo.severity,updateProps.severity);
         assertEquals(updatedEvent,updatedEventFromRepo);

         assertNotSame(addedEvent,updatedEvent);


     }

}