package com.ifountain.rcmdb.aol.connection

import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.rcmdb.test.util.ConnectionTestUtils
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException
import com.ifountain.comp.test.util.logging.TestLogUtils
import org.apache.log4j.Logger
import net.kano.joscar.net.ClientConn
import com.ifountain.core.connection.exception.ConnectionException
import com.ifountain.rcmdb.test.util.AolUser
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import junit.framework.TestSuite

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 11, 2009
* Time: 3:28:17 PM
*/
class AolConnectionImplTests extends RapidCoreTestCase {

    AolConnectionImpl connection;
    protected void setUp() {
        super.setUp();
        TestLogUtils.enableLogger(Logger.getRootLogger())
        connection = new AolConnectionImpl();
    }

    protected void tearDown() {
        if (connection.isConnected()) {
            connection._disconnect();
        }
        super.tearDown();
    }

    public void testInit() throws Exception {
        ConnectionParam connParam = ConnectionTestUtils.getAolConnectionParam();
        try {
            connection.init(connParam);
        }
        catch (e) {
            fail("should not throw exception")
        }
        assertSame(connParam, connection.getParameters());

        connParam.getOtherParams().remove(AolConnectionImpl.HOST);
        try {
            connection.init(connParam);
            fail("should throw exception")
        }
        catch (UndefinedConnectionParameterException e) {}

        connParam = ConnectionTestUtils.getAolConnectionParam();
        connParam.getOtherParams().remove(AolConnectionImpl.PORT);
        try {
            connection.init(connParam);
            fail("should not throw exception")
        }
        catch (e) {
        }

        connParam = ConnectionTestUtils.getAolConnectionParam();
        connParam.getOtherParams().remove(AolConnectionImpl.USERNAME);
        try {
            connection.init(connParam);
            fail("should throw exception")
        }
        catch (UndefinedConnectionParameterException e) {}

        connParam = ConnectionTestUtils.getAolConnectionParam();
        connParam.getOtherParams().remove(AolConnectionImpl.PASSWORD);
        try {
            connection.init(connParam);
            fail("should throw exception")
        }
        catch (UndefinedConnectionParameterException e) {}
    }

    public void testConnect() throws Exception {
        ConnectionParam param = ConnectionTestUtils.getAolConnectionParam();
        connection.init(param);
        connection._connect();
        assertTrue(connection.checkConnection());
        assertEquals(ClientConn.STATE_NOT_CONNECTED, connection.getLoginConn().getState())
        assertEquals(ClientConn.STATE_CONNECTED, connection.getBosConn().getState())
        assertEquals(connection.getTimeout(), connection.getBosConn().getSocket().getSoTimeout())
    }

    public void testIfHostIsUnreachableConnectThrowsException() {
        String host = "UNKNOWN_HOST"
        ConnectionParam param = ConnectionTestUtils.getAolConnectionParam();
        param.getOtherParams().put(AolConnectionImpl.HOST, host)
        connection.init(param);
        try {
            connection._connect();
            fail("should throw exception")
        }
        catch (ConnectionException e) {
            assertEquals("java.net.UnknownHostException: ${host}", e.getMessage())
        }
    }

    public void testIfCredentialsAreIncorrectConnectThrowsException() {
        ConnectionParam param = ConnectionTestUtils.getAolConnectionParam();
        param.getOtherParams().put(AolConnectionImpl.USERNAME, "invalidUser")
        connection.init(param);
        try {
            connection._connect();
            fail("should throw exception")
        }
        catch (ConnectionException e) {
            assertTrue(e.getMessage().indexOf("Login error.") > -1)
        }
    }

    public void testDisconnect() throws Exception {
        ConnectionParam param = ConnectionTestUtils.getAolConnectionParam();
        connection.init(param);
        connection._connect();
        assertTrue(connection.checkConnection());
        assertEquals(ClientConn.STATE_NOT_CONNECTED, connection.getLoginConn().getState())
        assertEquals(ClientConn.STATE_CONNECTED, connection.getBosConn().getState())

        connection._disconnect();
        assertFalse(connection.checkConnection())
        assertEquals(ClientConn.STATE_NOT_CONNECTED, connection.getLoginConn().getState())
        assertEquals(ClientConn.STATE_NOT_CONNECTED, connection.getBosConn().getState())
    }

    public void testSendAndReceiveMessage() throws Exception
    {
        final List receivedPacketList = new ArrayList();
        ConnectionParam param = ConnectionTestUtils.getAolConnectionParam();
        connection.init(param);
        connection._connect();

        AolConnectionImpl receiverConnection = new AolConnectionImpl();
        receiverConnection.setTextReceivedCallback {target, message ->
            receivedPacketList.add(message)
        }
        AolUser receiverUser = ConnectionTestUtils.getAolUser();
        param.getOtherParams().put(AolConnectionImpl.USERNAME, receiverUser.getUsername())
        param.getOtherParams().put(AolConnectionImpl.PASSWORD, receiverUser.getPassword())
        receiverConnection.init(param);
        try {
            receiverConnection._connect();
            Thread.sleep(1000);
            String messageText = "aim_message";
            connection.sendMessage(receiverUser.getUsername(), messageText);
            CommonTestUtils.waitFor(new ClosureWaitAction({
                assertEquals(1, receivedPacketList.size())
            }))
            assertEquals(messageText, receivedPacketList[0]);

            connection.sendMessage(receiverUser.getUsername(), messageText + 1);
            CommonTestUtils.waitFor(new ClosureWaitAction({
                assertEquals(2, receivedPacketList.size())
            }))
            assertEquals(messageText + 1, receivedPacketList[1]);

            connection.sendMessage(receiverUser.getUsername(), messageText + 2);
            CommonTestUtils.waitFor(new ClosureWaitAction({
                assertEquals(3, receivedPacketList.size())
            }))
            assertEquals(messageText + 2, receivedPacketList[2]);
        }
        finally {
            receiverConnection._disconnect();
        }
    }

    public void testSendMessageThrowsExceptionIfNotConnected() throws Exception {
        ConnectionParam param = ConnectionTestUtils.getAolConnectionParam();
        connection.init(param);
        String target = "someUser"
        try {
            connection.sendMessage(target, "someMessage");
            fail("should throw exception")
        }
        catch (ConnectionException e) {
            assertEquals("Sending message to ${target} is failed, because there is no established connection.", e.getMessage());
        }

    }

}