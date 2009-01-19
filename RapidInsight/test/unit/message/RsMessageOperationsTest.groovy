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

        assertEquals(RsMessage.list().size(),0)
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

        RsMessage.processDelayedEmails(Logger.getRootLogger())

        def mes=RsMessage.get(id:message.id)
        assertEquals(mes.state,RsMessage.STATE_IN_DELAY)
        Thread.sleep(delay+1000)
        RsMessage.processDelayedEmails(Logger.getRootLogger())
        mes=RsMessage.get(id:message.id)
        assertEquals(mes.state,RsMessage.STATE_READY)


        
    }

    public void testAddCreateEmail()
    {
        initialize([RsMessage], []);
        CompassForTests.addOperationSupport(RsMessage,RsMessageOperations)

        assertEquals(RsMessage.list().size(),0)


        
        def undelayedMessage=RsMessage.addEventCreateEmail(Logger.getRootLogger(),[id:1],"xxx",0)

        assertFalse(undelayedMessage.hasErrors())
        assertEquals(RsMessage.list().size(),1)
        assertEquals(undelayedMessage.state,RsMessage.STATE_READY)
        assertEquals(undelayedMessage.insertedAt,undelayedMessage.sendAfter)
        assertEquals(undelayedMessage.eventId,1)
        assertEquals(undelayedMessage.action,RsMessage.ACTION_CREATE)

        Long delay2=2
        def delayedMessage=RsMessage.addEventCreateEmail(Logger.getRootLogger(),[id:2],"xxx",delay2)
        assertFalse(delayedMessage.hasErrors())
        assertEquals(RsMessage.list().size(),2)
        assertEquals(delayedMessage.state,RsMessage.STATE_IN_DELAY)
        assertEquals(delayedMessage.sendAfter,delayedMessage.insertedAt+(delay2*1000))
        assertEquals(delayedMessage.eventId,2)
        assertEquals(delayedMessage.action,RsMessage.ACTION_CREATE)


    }

    public void  testAddEventClearEmailDoesNotCreateClearMessageWhenCreateMessageIsMissing()
    {
        initialize([RsMessage], []);
        CompassForTests.addOperationSupport(RsMessage,RsMessageOperations)
        
        def message=RsMessage.addEventClearEmail(Logger.getRootLogger(),[activeId:5],"xxx")
        assertNull(message)
        assertEquals(RsMessage.list().size(),0)

    }
    public void testAddEventClearEmailHandlesDelayingCreateMessage()
    {
        initialize([RsMessage], []);
        CompassForTests.addOperationSupport(RsMessage,RsMessageOperations)
        def params=[:]
        params.id=4
        params.destination="xxx"

        def delayingMessage=RsMessage.addEventCreateEmail(Logger.getRootLogger(),[id:params.id],params.destination,1000)

        def clearMessage=RsMessage.addEventClearEmail(Logger.getRootLogger(),[activeId:params.id],params.destination)
        assertNull(clearMessage)
        assertEquals(RsMessage.list().size(),1)

        def crateMessage=RsMessage.get(id:delayingMessage.id)
        assertEquals(crateMessage.state,RsMessage.STATE_DELAY_EXPIRED)
    }
    public void testAddEventClearEmail()
    {
        initialize([RsMessage], []);
        CompassForTests.addOperationSupport(RsMessage,RsMessageOperations)

        def params=[:]
        params.id=4
        params.destination="xxx"

        def createMessage=RsMessage.addEventCreateEmail(Logger.getRootLogger(),[id:params.id],params.destination,0)
        assertFalse(createMessage.hasErrors())
        assertEquals(RsMessage.list().size(),1)
        assertEquals(createMessage.state,RsMessage.STATE_READY)
        assertEquals(createMessage.eventId,params.id)
        assertEquals(createMessage.action,RsMessage.ACTION_CREATE)


        def clearMessage=RsMessage.addEventClearEmail(Logger.getRootLogger(),[activeId:params.id],params.destination)
        assertFalse(clearMessage.hasErrors())
        assertEquals(RsMessage.list().size(),2)
        assertEquals(clearMessage.state,RsMessage.STATE_READY)
        assertEquals(clearMessage.eventId,params.id)
        assertEquals(clearMessage.action,RsMessage.ACTION_CLEAR)
        
        
    }
}