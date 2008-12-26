package connection


import com.ifountain.core.connection.ConnectionParam
import com.ifountain.rcmdb.test.util.EmailConnectionImplTestUtils
import com.ifountain.core.test.util.RapidCoreTestCase

/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 24, 2008
 * Time: 3:42:22 PM
 * To change this template use File | Settings | File Templates.
 */
class EmailConnectionImplTest extends RapidCoreTestCase{

     public void testConnectAndDisconnect()
     {
        Map params = EmailConnectionImplTestUtils.getConnectionParams();
         def conParams=new ConnectionParam("EmailConnection","dummy","dummy",params);

         def con=new EmailConnectionImpl()
         con.init(conParams)
         
         assertFalse(con.checkConnection());
         assertFalse(con.isConnected());

         con._connect()
         assertTrue(con.checkConnection());
         assertTrue(con.isConnected());

         con._disconnect()
         assertFalse(con.checkConnection());
         assertFalse(con.isConnected());
     }
     public void testConnectThrowsExceptionWithWrongAccount(){
        Map params = EmailConnectionImplTestUtils.getConnectionParams();
        params.Password="xxxxxxxxxxx55555555yyyyyyyyyyyyyyyy"

        def conParams=new ConnectionParam("EmailConnection","dummy","dummy",params);

        def con=new EmailConnectionImpl()
        con.init(conParams)

        assertFalse(con.checkConnection());
        assertFalse(con.isConnected());
        try {
            con._connect();
            fail("Should throw Exception");
        }
        catch(javax.mail.AuthenticationFailedException e)
        {

        }
        assertFalse(con.checkConnection());
        assertFalse(con.isConnected());


     }
     public void testInitConvertsSmtpPortToInt()
     {
        Map params = EmailConnectionImplTestUtils.getConnectionParams();
        params.SmtpPort="66"

        def conParams=new ConnectionParam("EmailConnection","dummy","dummy",params);

        def con=new EmailConnectionImpl()
        con.init(conParams)

        assertEquals(con.getSmtpPort(),66);

     }

}