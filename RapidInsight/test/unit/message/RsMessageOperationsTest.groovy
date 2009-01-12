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
        params.state=0
        params.destination="xxx"
        params.destinationType=RsMessage.EMAIL
        params.action="create"
        params.sendAfter=date.getTime()+delay


        def message=RsMessage.add(params)
        
        if(message.hasErrors())
        {
            fail("Message should be added successfully")
        }

        RsMessage.processDelayedEmails(Logger.getLogger("yyy"))

        def mes=RsMessage.get(id:message.id)
        assertEquals(mes.state,0)
        Thread.sleep(delay+1000)
        RsMessage.processDelayedEmails(Logger.getLogger("yyy"))
        mes=RsMessage.get(id:message.id)
        assertEquals(mes.state,1)


        
    }

}