package datasource
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

class SendEmailActionTest extends RapidCoreTestCase{

    public void testExecute(){
        Map params = EmailConnectionImplTestUtils.getConnectionParams();           
        
        def conParams=new ConnectionParam("EmailConnection","dummy","dummy",params);

        def con=new EmailConnectionImpl()
        con.init(conParams)

        con._connect()
        assertTrue(con.checkConnection());
        assertTrue(con.isConnected());

        Map sendEmailParams = EmailConnectionImplTestUtils.getSendEmailParams();
        SendEmailAction action=new SendEmailAction(Logger.getRootLogger(),sendEmailParams)
        action.execute(con)
        
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
        Map sendEmailParams = EmailConnectionImplTestUtils.getSendEmailParams();
        def action=new SendEmailAction(Logger.getRootLogger(),sendEmailParams)
        assertEquals(action.getParams().contentType,EmailAdapter.PLAIN)

    }


}