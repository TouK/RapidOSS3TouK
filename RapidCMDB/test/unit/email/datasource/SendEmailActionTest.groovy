package email.datasource
/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 25, 2008
 * Time: 10:41:28 AM
 * To change this template use File | Settings | File Templates.
 */
import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.rcmdb.test.util.EmailConnectionImplTestUtils
import connection.EmailConnectionImpl
import com.ifountain.comp.exception.RapidMissingParameterException
import org.apache.log4j.Logger
import datasource.SendEmailAction
import datasource.EmailAdapter
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import javax.mail.Message
import javax.mail.internet.InternetAddress


class SendEmailActionTest extends RapidCoreTestCase{
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    public void testExecuteWithLocal()
    {
        _testExecute("Local", "User1", "User2", true);
    }
    public void testExecuteWithYahoo()
    {
        _testExecute("Yahoo", "Yahoo1", "Yahoo2", false);
    }
    public void testExecuteWithGoogle()
    {
        _testExecute("Google", "Google1", "Google2", false);
    }


    public void _testExecute(String serverId, String fromUserId, String toUserId, boolean searchTheMessageWithSubject){

        Map fromUser = EmailConnectionImplTestUtils.getEmailUserAccountInfo(fromUserId)
        Map toUser = EmailConnectionImplTestUtils.getEmailUserAccountInfo(toUserId)
        Map params = EmailConnectionImplTestUtils.getSmtpConnectionParams(fromUserId, serverId);
        def conParams=new ConnectionParam("dummy","dummy",params);
        conParams.setMinTimeout (20000)
        conParams.setMaxTimeout(100000)

        def con=new EmailConnectionImpl()
        con.init(conParams)

        con._connect()
        assertTrue(con.checkConnection());
        assertTrue(con.isConnected());
        println "CONNECTED TO SERVER"
        def numberOfMessagesBeforeSendingMail = EmailConnectionImplTestUtils.getMessageCount(toUserId, serverId);
        println "NUMBER OF MESSAGES BEFORE SENDING MAIL:"+numberOfMessagesBeforeSendingMail

        String subject = "subject1${System.currentTimeMillis()}${Math.random()}";
        String body = "body1${System.currentTimeMillis()}${Math.random()}";
        Map sendEmailParams = EmailConnectionImplTestUtils.getSendEmailParams(fromUser.Username, toUser.Username, subject, body);
        SendEmailAction action=new SendEmailAction(Logger.getRootLogger(),sendEmailParams)
        action.execute(con);
        CommonTestUtils.waitFor (new ClosureWaitAction({
            def messageCountAfterSendingMail = EmailConnectionImplTestUtils.getMessageCount(toUserId, serverId);
            assertEquals ("Message could not be sent successfully", numberOfMessagesBeforeSendingMail+1, messageCountAfterSendingMail);
        }))
        EmailConnectionImplTestUtils.getMessages(toUserId, serverId, {List messages->
            Message lastMessage = messages[messages.size()-1];
            if(searchTheMessageWithSubject)
            {
                lastMessage = messages.find {it.getSubject() == subject}    
            }
            assertEquals (body, lastMessage.getContent().toString().trim());
            assertEquals (new InternetAddress(fromUser.Username, fromUser.Username).toString(), lastMessage.getFrom()[0].toString());
        });

    }


    public void testConstructorGeneratesExceptionWhenParametersMissing()
    {
        def paramList=[]
        paramList.add([:])
        paramList.add(["from":"x"])
        paramList.add(["to":"x"])
        paramList.add(["subject":"x"])
        paramList.add(["body":"x"])

        try{
            for(params in paramList)
            {
                new SendEmailAction(Logger.getRootLogger(),params)
                fail("Should throw RapidMissingParameterException");
            }
        }
        catch(RapidMissingParameterException e)
        {

        }


    }

    public void testConstructorGeneratesContentTypeWhenMissing()
    {
        Map sendEmailParams = EmailConnectionImplTestUtils.getSendEmailParams("User1", "User2", "subject", "body");
        def action=new SendEmailAction(Logger.getRootLogger(),sendEmailParams)
        assertEquals(action.getParams().contentType,EmailAdapter.PLAIN)

    }


}