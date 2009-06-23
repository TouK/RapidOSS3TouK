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

    public void setUp() {
        super.setUp();


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
        params.destinationType=RsMessage.EMAIL
        params.action=RsMessage.ACTION_CREATE
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
        
        def undelayedMessage=RsMessage.addEventCreateMessage([id:1],RsMessage.EMAIL, "xxx",0)

        assertFalse(undelayedMessage.hasErrors())
        assertEquals(RsMessage.count(),1)
        assertEquals(undelayedMessage.state,RsMessage.STATE_READY)
        assertEquals(undelayedMessage.insertedAt,undelayedMessage.sendAfter)
        assertEquals(undelayedMessage.eventId,1)
        assertEquals(undelayedMessage.action,RsMessage.ACTION_CREATE)

        Long delay2=2
        def delayedMessage=RsMessage.addEventCreateMessage([id:2],RsMessage.EMAIL, "xxx",delay2)
        assertFalse(delayedMessage.hasErrors())
        assertEquals(RsMessage.count(),2)
        assertEquals(delayedMessage.state,RsMessage.STATE_IN_DELAY)
        assertEquals(delayedMessage.sendAfter,delayedMessage.insertedAt+(delay2*1000))
        assertEquals(delayedMessage.eventId,2)
        assertEquals(delayedMessage.action,RsMessage.ACTION_CREATE)
    }

    public void  testAddEventClearMessageDoesNotCreateClearMessageWhenCreateMessageIsMissing()
    {
        initialize([RsMessage], []);
        CompassForTests.addOperationSupport(RsMessage,RsMessageOperations)
        
        def message=RsMessage.addEventClearMessage([activeId:5],RsMessage.EMAIL, "xxx")
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

        def delayingMessage=RsMessage.addEventCreateMessage([id:params.id],RsMessage.EMAIL,params.destination,1000)

        def clearMessage=RsMessage.addEventClearMessage([activeId:params.id],RsMessage.EMAIL,params.destination)
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

        def createMessage=RsMessage.addEventCreateMessage([id:params.id],RsMessage.EMAIL,params.destination,0)
        assertFalse(createMessage.hasErrors())
        assertEquals(RsMessage.count(),1)
        assertEquals(createMessage.state,RsMessage.STATE_READY)
        assertEquals(createMessage.eventId,params.id)
        assertEquals(createMessage.action,RsMessage.ACTION_CREATE)


        def clearMessage=RsMessage.addEventClearMessage([activeId:params.id],RsMessage.EMAIL,params.destination)
        assertFalse(clearMessage.hasErrors())
        assertEquals(RsMessage.count(),2)
        assertEquals(clearMessage.state,RsMessage.STATE_READY)
        assertEquals(clearMessage.eventId,params.id)
        assertEquals(clearMessage.action,RsMessage.ACTION_CLEAR)
        
    }
}