package com.ifountain.rcmdb.jabber.connection

import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.ConnectionException
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException
import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.rcmdb.test.util.ConnectionTestConstants
import com.ifountain.rcmdb.test.util.ConnectionTestUtils
import org.jivesoftware.smack.XMPPException

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 1, 2009
* Time: 4:16:19 PM
*/
class JabberConnectionImplTests extends RapidCoreTestCase {
    JabberConnectionImpl connection;
    String receiverUsername;
    protected void setUp() {
        super.setUp();
        connection = new JabberConnectionImpl();
        receiverUsername = CommonTestUtils.getTestProperty(ConnectionTestConstants.JABBER_SECONDARY_USERNAME);
    }

    protected void tearDown() {
        if (connection.isConnected()) {
            connection._disconnect();
        }
        super.tearDown();
    }

    public void testInit() throws Exception {
        ConnectionParam connParam = ConnectionTestUtils.getJabberConnectionParam();
        try {
            connection.init(connParam);
        }
        catch (e) {
            fail("should not throw exception")
        }
        assertSame(connParam, connection.getParameters());

        connParam.getOtherParams().remove(JabberConnectionImpl.HOST);
        try {
            connection.init(connParam);
            fail("should throw exception")
        }
        catch (UndefinedConnectionParameterException e) {}

        connParam = ConnectionTestUtils.getJabberConnectionParam();
        connParam.getOtherParams().remove(JabberConnectionImpl.PORT);
        try {
            connection.init(connParam);
            fail("should not throw exception")
        }
        catch (e) {
        }

        connParam = ConnectionTestUtils.getJabberConnectionParam();
        connParam.getOtherParams().remove(JabberConnectionImpl.USERNAME);
        try {
            connection.init(connParam);
            fail("should throw exception")
        }
        catch (UndefinedConnectionParameterException e) {}

        connParam = ConnectionTestUtils.getJabberConnectionParam();
        connParam.getOtherParams().remove(JabberConnectionImpl.PASSWORD);
        try {
            connection.init(connParam);
            fail("should throw exception")
        }
        catch (UndefinedConnectionParameterException e) {}

        connParam = ConnectionTestUtils.getJabberConnectionParam();
        connParam.getOtherParams().remove(JabberConnectionImpl.SERVICENAME);
        try {
            connection.init(connParam);
            fail("should not throw exception")
        }
        catch (e) {
        }
    }

    public void testConnect() throws Exception {
        ConnectionParam param = ConnectionTestUtils.getJabberConnectionParam();
        connection.init(param);
        connection._connect();
        assertTrue(connection.checkConnection());
        assertTrue(connection.getXmppConnection().isConnected());
        assertTrue(connection.getXmppConnection().isAuthenticated());
    }

    public void testThrowsExceptionfTheServerIsNotAvailable() throws Exception {
        String nonExistingServer = "NON_EXISTING_JABBER_SERVER";
        ConnectionParam param = ConnectionTestUtils.getJabberConnectionParam();
        param.getOtherParams().put(JabberConnectionImpl.HOST, nonExistingServer)
        connection.init(param);
        try
        {
            connection._connect();
            fail("Should throw exception");
        }
        catch (XMPPException e) {
        }
    }

    public void testThrowsExceptionIfTheGivenUsernamePasswordIsNotAuthenticated() throws Exception {
        ConnectionParam param = ConnectionTestUtils.getJabberConnectionParam();
        param.getOtherParams().put(JabberConnectionImpl.PASSWORD, "invalid password")
        connection.init(param);
        try
        {
            connection._connect();
            fail("Should throw exception");
        }
        catch (XMPPException e) {
        }
        //clears already opened session
        assertFalse(connection.getXmppConnection().isConnected())
    }

    public void testDisconnect() throws Exception {
        ConnectionParam param = ConnectionTestUtils.getJabberConnectionParam();
        connection.init(param);
        connection._connect();
        assertTrue(connection.checkConnection());
        connection._disconnect();
        assertFalse(connection.checkConnection());
        assertFalse(connection.getXmppConnection().isConnected())
        assertFalse(connection.getXmppConnection().isAuthenticated())
    }


    public void testSendMessage() throws Exception {
        ConnectionParam param = ConnectionTestUtils.getJabberConnectionParam();
        connection.init(param);
        connection._connect();
        JabberConnectionImpl receiverConnection = new JabberConnectionImpl();
        param.getOtherParams().put(JabberConnectionImpl.USERNAME, receiverUsername);
        param.getOtherParams().put(JabberConnectionImpl.PASSWORD, CommonTestUtils.getTestProperty(ConnectionTestConstants.JABBER_SECONDARY_PASSWORD));
        receiverConnection.init(param);
        receiverConnection._connect();
        try {
            def receivedMessages = [];
            def textReceived = {from, message ->
                receivedMessages.add(message);
            }
            receiverConnection.setTextReceivedCallback(textReceived)
            def messageText = "Hello World";
            def userAddress = "${receiverUsername}@${CommonTestUtils.getTestProperty(ConnectionTestConstants.JABBER_SERVICENAME)}"
            connection.sendImMessage(userAddress, messageText);

            CommonTestUtils.waitFor(new ClosureWaitAction({
                assertEquals(1, receivedMessages.size())
                assertEquals(messageText, receivedMessages[0])
            }))
        }
        finally {
            receiverConnection._disconnect();
        }
    }

    public void testSendMessageThrowsExceptionIfNotConnected() {
        ConnectionParam param = ConnectionTestUtils.getJabberConnectionParam();
        connection.init(param);
        def messageText = "Hello World";

        try {
            connection.sendImMessage(receiverUsername, messageText);
            fail("should throw exception")
        }
        catch (ConnectionException e) {
            assertEquals("Sending im to ${receiverUsername} is failed, because there is no established connection.", e.getMessage())
        }
    }

    public void testOfflineMessaging() throws Exception {
        def serviceName = CommonTestUtils.getTestProperty(ConnectionTestConstants.JABBER_SERVICENAME)
        ConnectionParam param = ConnectionTestUtils.getJabberConnectionParam();
        connection.init(param);
        connection._connect();

        String messageText = "message";
        connection.sendImMessage(receiverUsername + "@" + serviceName, messageText);
        
        JabberConnectionImpl receiverConnection = new JabberConnectionImpl();
        param.getOtherParams().put(JabberConnectionImpl.USERNAME, receiverUsername);
        param.getOtherParams().put(JabberConnectionImpl.PASSWORD, CommonTestUtils.getTestProperty(ConnectionTestConstants.JABBER_SECONDARY_PASSWORD));
        receiverConnection.init(param);
        def receivedMessages = [];
        def textReceived = {from, message ->
            receivedMessages.add(message);
        }
        receiverConnection.setTextReceivedCallback(textReceived)
        receiverConnection._connect();

        try {
            CommonTestUtils.waitFor(new ClosureWaitAction({
                assertEquals(1, receivedMessages.size())
                assertEquals(messageText, receivedMessages[0])
            }))
        }
        finally {
            receiverConnection._disconnect();
        }
    }

    public void testGetUserIdFromSource() throws Exception {
        assertEquals("a@b", JabberConnectionImpl.getUserIdFromSource("a@b"));
        assertEquals("a", JabberConnectionImpl.getUserIdFromSource("a"));
        assertEquals("a@gmail.com", JabberConnectionImpl.getUserIdFromSource("a@gmail.com"));
        assertEquals("sezo104@gmail.com", JabberConnectionImpl.getUserIdFromSource("sezo104@gmail.com/Talk.v1048F4F851B"));
    }

}
