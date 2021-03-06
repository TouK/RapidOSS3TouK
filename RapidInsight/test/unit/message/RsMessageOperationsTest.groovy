package message


import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import org.apache.log4j.Logger
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 9, 2009
* Time: 9:03:57 AM
* To change this template use File | Settings | File Templates.
*/
class RsMessageOperationsTest extends RapidCmdbWithCompassTestCase{

    def RsEvent;
    def RsHistoricalEvent;
    def EMAIL_TYPE="email";

    public void setUp() {
        super.setUp();

        ["RsEvent","RsHistoricalEvent"].each{ className ->
            setProperty(className,gcl.loadClass(className));
        }

    }

    public void tearDown() {
        super.tearDown();    
    }

    public void testProcessDelayedEmails()
    {
        initialize([RsMessage], []);
        CompassForTests.addOperationSupport(RsMessage,RsMessageOperations)

        assertEquals(RsMessage.count(),0)
        def date=new Date();
        def delay=2000
        def params=[:]
        params.eventId=1
        params.state=RsMessage.STATE_IN_DELAY
        params.destination="xxx"
        params.destinationType=EMAIL_TYPE
        params.eventType=RsMessage.EVENT_TYPE_CREATE
        params.sendAfter=date.getTime()+delay


        def message=RsMessage.add(params)
        
        if(message.hasErrors())
        {
            fail("Message should be added successfully")
        }

        RsMessage.processDelayedMessages()

        def mes=RsMessage.get(id:message.id)
        assertEquals(mes.state,RsMessage.STATE_IN_DELAY)
        Thread.sleep(delay+1000)
        RsMessage.processDelayedMessages()
        mes=RsMessage.get(id:message.id)
        assertEquals(mes.state,RsMessage.STATE_READY)


        
    }

    public void testAddCreateMessage()
    {
        initialize([RsMessage], []);
        CompassForTests.addOperationSupport(RsMessage,RsMessageOperations)

        assertEquals(RsMessage.count(),0)
        
        def undelayedMessage=RsMessage.addEventCreateMessage([id:1],EMAIL_TYPE, "xxx",0)

        assertFalse(undelayedMessage.hasErrors())
        assertEquals(RsMessage.count(),1)
        assertEquals(undelayedMessage.state,RsMessage.STATE_READY)
        assertEquals(undelayedMessage.insertedAt,undelayedMessage.sendAfter)
        assertEquals(undelayedMessage.eventId,1)
        assertEquals(undelayedMessage.eventType,RsMessage.EVENT_TYPE_CREATE)

        Long delay2=2
        def delayedMessage=RsMessage.addEventCreateMessage([id:2],EMAIL_TYPE, "xxx",delay2)
        assertFalse(delayedMessage.hasErrors())
        assertEquals(RsMessage.count(),2)
        assertEquals(delayedMessage.state,RsMessage.STATE_IN_DELAY)
        assertEquals(delayedMessage.sendAfter,delayedMessage.insertedAt+(delay2*1000))
        assertEquals(delayedMessage.eventId,2)
        assertEquals(delayedMessage.eventType,RsMessage.EVENT_TYPE_CREATE)
    }

    public void  testAddEventClearMessageDoesNotCreateClearMessageWhenCreateMessageIsMissing()
    {
        initialize([RsMessage], []);
        CompassForTests.addOperationSupport(RsMessage,RsMessageOperations)
        
        def message=RsMessage.addEventClearMessage([activeId:5],EMAIL_TYPE, "xxx")
        assertNull(message)
        assertEquals(RsMessage.count(),0)

    }
    public void testAddEventClearMessageHandlesDelayingCreateMessage()
    {
        initialize([RsMessage], []);
        CompassForTests.addOperationSupport(RsMessage,RsMessageOperations)
        def params=[:]
        params.id=4
        params.destination="xxx"

        def delayingMessage=RsMessage.addEventCreateMessage([id:params.id],EMAIL_TYPE,params.destination,1000)

        def clearMessage=RsMessage.addEventClearMessage([activeId:params.id],EMAIL_TYPE,params.destination)
        assertNull(clearMessage)
        assertEquals(RsMessage.count(),1)

        def crateMessage=RsMessage.get(id:delayingMessage.id)
        assertEquals(crateMessage.state,RsMessage.STATE_ABORT)
    }
    public void testAddEventClearEmail()
    {
        initialize([RsMessage], []);
        CompassForTests.addOperationSupport(RsMessage,RsMessageOperations)

        def params=[:]
        params.id=4
        params.destination="xxx"

        def createMessage=RsMessage.addEventCreateMessage([id:params.id],EMAIL_TYPE,params.destination,0)
        assertFalse(createMessage.hasErrors())
        assertEquals(RsMessage.count(),1)
        assertEquals(createMessage.state,RsMessage.STATE_READY)
        assertEquals(createMessage.eventId,params.id)
        assertEquals(createMessage.eventType,RsMessage.EVENT_TYPE_CREATE)


        def clearMessage=RsMessage.addEventClearMessage([activeId:params.id],EMAIL_TYPE,params.destination)
        assertFalse(clearMessage.hasErrors())
        assertEquals(RsMessage.count(),2)
        assertEquals(clearMessage.state,RsMessage.STATE_READY)
        assertEquals(clearMessage.eventId,params.id)
        assertEquals(clearMessage.eventType,RsMessage.EVENT_TYPE_CLEAR)
        
    }

    public void testRetrieveEvent()
    {
        initialize([RsMessage,RsEvent,RsHistoricalEvent], []);
        CompassForTests.addOperationSupport(RsMessage,RsMessageOperations);

        def nonExistingEventId=4444444444;

        def noEventMessage=RsMessage.add(eventType:RsMessage.EVENT_TYPE_CREATE,eventId:nonExistingEventId,destination:"dest",destinationType:"desttype");
        assertFalse(noEventMessage.hasErrors());
        assertNull(noEventMessage.retrieveEvent());


        def noHistoricalEventMessage=RsMessage.add(eventType:RsMessage.EVENT_TYPE_CLEAR,eventId:nonExistingEventId,destination:"dest",destinationType:"desttype");
        assertFalse(noHistoricalEventMessage.hasErrors());
        assertNull(noHistoricalEventMessage.retrieveEvent());

        def unknownActionMessage=RsMessage.add(eventType:"someaction",eventId:nonExistingEventId,destination:"dest",destinationType:"desttype");
        assertFalse(unknownActionMessage.hasErrors());

        assertNull(unknownActionMessage.retrieveEvent());

        def activeEvent=RsEvent.add(name:"testev");
        assertFalse(activeEvent.hasErrors());

        def activeEventMessage=RsMessage.add(eventType:RsMessage.EVENT_TYPE_CREATE,eventId:activeEvent.id,destination:"dest",destinationType:"desttype");
        assertFalse(activeEventMessage.hasErrors());

        assertEquals(activeEvent.id,activeEventMessage.retrieveEvent().id);
        
        def historicalEvent=RsHistoricalEvent.add(name:"testev3",activeId:55);
        assertFalse(historicalEvent.hasErrors());

        def historicalEventMessage=RsMessage.add(eventType:RsMessage.EVENT_TYPE_CLEAR,eventId:historicalEvent.activeId,destination:"dest",destinationType:"desttype");
        assertFalse(historicalEventMessage.hasErrors());

        assertEquals(historicalEvent.id,historicalEventMessage.retrieveEvent().id);
    }

    public void testRecordSuccess()
    {
        initialize([RsMessage], []);
        CompassForTests.addOperationSupport(RsMessage,RsMessageOperations);

        def message=RsMessage.add(eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY,eventId:1,destination:"dest",destinationType:"desttype",tryCount:1);
        assertFalse(message.hasErrors());

        assertEquals (RsMessage.STATE_READY,message.state);
        assertEquals (1,message.tryCount);
        assertEquals (message.sentAt,message.firstSentAt);
        assertEquals (0,message.sentAt);
        assertEquals (0,message.firstSentAt);

        def now=Date.now();
        Thread.sleep(50);
        message.recordSuccess();
        
        assertEquals (RsMessage.STATE_SENT,message.state);
        assertEquals (2,message.tryCount);
        assertEquals (message.sentAt,message.firstSentAt);
        assertTrue (message.sentAt>now);
        assertTrue (message.firstSentAt>now);
    }

    public void testRecordFailure()
    {
        initialize([RsMessage], []);
        CompassForTests.addOperationSupport(RsMessage,RsMessageOperations);

        def message=RsMessage.add(eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY,eventId:1,destination:"dest",destinationType:"desttype");
        assertFalse(message.hasErrors());

        assertEquals (RsMessage.STATE_READY,message.state);
        assertEquals (0,message.tryCount);
        assertEquals (message.sentAt,message.firstSentAt);
        assertEquals (0,message.sentAt);
        assertEquals (0,message.firstSentAt);

        def now=Date.now();
        Thread.sleep(50);
        message.recordFailure();

        assertEquals (RsMessage.STATE_ERROR,message.state);
        assertEquals (1,message.tryCount);
        assertEquals (message.sentAt,message.firstSentAt);
        assertTrue (message.sentAt>now);
        assertTrue (message.firstSentAt>now);

        Thread.sleep(50);
        message.recordFailure();

        assertEquals (RsMessage.STATE_ERROR,message.state);
        assertEquals (2,message.tryCount);
        assertTrue (message.sentAt>message.firstSentAt);  

        2.times{ counter ->
          message.recordFailure();
          assertEquals (RsMessage.STATE_ERROR,message.state);
          assertEquals (3+counter,message.tryCount);
        }

        message.recordFailure();
        assertEquals (RsMessage.STATE_ERROR_LIMIT,message.state);
        assertEquals (5,message.tryCount);

    }

    public void testRecordNotExists()
    {
        initialize([RsMessage], []);
        CompassForTests.addOperationSupport(RsMessage,RsMessageOperations);

        def message=RsMessage.add(eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY,eventId:1,destination:"dest",destinationType:"desttype");
        assertFalse(message.hasErrors());

        assertEquals (RsMessage.STATE_READY,message.state);
        assertEquals (0,message.tryCount);
        assertEquals (0,message.sentAt);
        assertEquals (0,message.firstSentAt);

        def now=Date.now();
        Thread.sleep(50);
        message.recordNotExists();

        assertEquals (RsMessage.STATE_NOT_EXISTS,message.state);
        assertEquals (0,message.tryCount);
        assertEquals (0,message.sentAt);
        assertEquals (0,message.firstSentAt);
    }

    public void testMarkForResend()
    {
        initialize([RsMessage], []);
        CompassForTests.addOperationSupport(RsMessage,RsMessageOperations);

        def message=RsMessage.add(eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_ERROR,tryCount:3,eventId:1,destination:"dest",destinationType:"desttype");
        assertFalse(message.hasErrors());

        assertEquals (RsMessage.STATE_ERROR,message.state);
        assertEquals (3,message.tryCount);
      
        message.markForResend();

        assertEquals (RsMessage.STATE_READY,message.state);
        assertEquals (0,message.tryCount);
    }
}