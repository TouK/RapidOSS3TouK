package email.connection


import com.ifountain.core.connection.ConnectionParam
import com.ifountain.rcmdb.test.util.EmailConnectionImplTestUtils
import com.ifountain.core.test.util.RapidCoreTestCase
import connection.EmailConnectionImpl

/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 24, 2008
 * Time: 3:42:22 PM
 * To change this template use File | Settings | File Templates.
 */
class EmailConnectionImplTest extends RapidCoreTestCase{
    protected void setUp() throws Exception {
        super.setUp();

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsConnectionException()
    {
        EmailConnectionImpl connection = new EmailConnectionImpl();
        ConnectException  exception = new ConnectException("exception");
        assertTrue (connection.isConnectionException(exception));

        SocketException socketException = new SocketException();
        assertTrue (connection.isConnectionException(socketException));

        NoRouteToHostException noRouteToHostException = new NoRouteToHostException();
        assertTrue (connection.isConnectionException(noRouteToHostException));

        IOException ioException = new IOException()
        assertFalse (connection.isConnectionException(ioException));

        Exception nestedException = new Exception(new SocketException());
        assertTrue (connection.isConnectionException(nestedException));

        def socketExceptionString = new SocketException().toString()
        Exception exceptionContainingSocketException = new Exception(socketExceptionString);
        assertTrue ("${socketExceptionString} contains socketexception class name so it should be a connection exception", connection.isConnectionException(exceptionContainingSocketException));

        Exception otherException = new Exception();
        assertFalse(connection.isConnectionException(otherException));
    }

     public void testConnectAndDisconnect()
     {
        Map params = EmailConnectionImplTestUtils.getSmtpConnectionParams("User1");
         def conParams=new ConnectionParam("dummy","dummy",params);
         conParams.setMinTimeout (44000);

         EmailConnectionImpl con=new EmailConnectionImpl()
         con.init(conParams)

         assertFalse(con.checkConnection());
         assertFalse(con.isConnected());

         con._connect()
         assertTrue(con.checkConnection());
         assertTrue(con.isConnected());
         assertEquals("timeout value should be assigned", ""+conParams.getMinTimeout(), con.getEmailSession().getProperty ("mail." + con.getProtocol() + ".timeout"));
         assertEquals("connectiontimeout value should be assigned", ""+conParams.getMinTimeout(), con.getEmailSession().getProperty ("mail." + con.getProtocol() + ".connectiontimeout"));

         con._disconnect()
         assertFalse(con.checkConnection());
         assertFalse(con.isConnected());

         conParams=new ConnectionParam("dummy","dummy",params);
         conParams.setMinTimeout (44000);

         con=new EmailConnectionImpl()
         con.init(conParams)
         con._connect()
         assertEquals("timeout value should be assigned", ""+conParams.getMinTimeout(), con.getEmailSession().getProperty ("mail." + con.getProtocol() + ".timeout"));
         assertEquals("connectiontimeout value should be assigned", ""+conParams.getMinTimeout(), con.getEmailSession().getProperty ("mail." + con.getProtocol() + ".connectiontimeout"));
         
         con._disconnect()
     }
     public void testConnectThrowsExceptionWithWrongAccount(){
        Map params = EmailConnectionImplTestUtils.getSmtpConnectionParams("User1");
        params.Password="xxxxxxxxxxx55555555yyyyyyyyyyyyyyyy"

        def conParams=new ConnectionParam("dummy","dummy",params);
        conParams.setMinTimeout (30000);
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
        Map params = EmailConnectionImplTestUtils.getSmtpConnectionParams("User1");
        params.SmtpPort="66"

        def conParams=new ConnectionParam("dummy","dummy",params);

        def con=new EmailConnectionImpl()
        con.init(conParams)

        assertEquals(con.getSmtpPort(),66);

     }

}