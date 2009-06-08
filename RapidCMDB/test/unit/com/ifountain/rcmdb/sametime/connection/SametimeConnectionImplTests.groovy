package com.ifountain.rcmdb.sametime.connection

import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.rcmdb.test.util.ConnectionTestConstants
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException
import com.ifountain.comp.test.util.CommonTestUtils
import com.lotus.sametime.core.constants.STError
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.core.connection.exception.ConnectionException
import junit.framework.TestSuite
import com.ifountain.rcmdb.test.util.ConnectionTestUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 20, 2009
* Time: 3:15:15 PM
*/
class SametimeConnectionImplTests extends RapidCoreTestCase {
    SametimeConnectionImpl connection;
    String receiverUsername;
    protected void setUp() {
        super.setUp();
        connection = new SametimeConnectionImpl();
        receiverUsername = CommonTestUtils.getTestProperty(ConnectionTestConstants.SAMETIME_SECONDARY_USERNAME);
    }

    protected void tearDown() {
        if (connection.isConnected()) {
            connection._disconnect();
        }
        super.tearDown();
    }

    public void testInit() throws Exception {
        ConnectionParam connParam = ConnectionTestUtils.getSametimeConnectionParam();
        try {
            connection.init(connParam);
        }
        catch (e) {
            fail("should not throw exception")
        }
        assertSame(connParam, connection.getParameters());

        connParam.getOtherParams().remove(SametimeConnectionImpl.HOST);
        try {
            connection.init(connParam);
            fail("should throw exception")
        }
        catch (UndefinedConnectionParameterException e) {}

        connParam = ConnectionTestUtils.getSametimeConnectionParam();
        connParam.getOtherParams().remove(SametimeConnectionImpl.USERNAME);
        try {
            connection.init(connParam);
            fail("should throw exception")
        }
        catch (UndefinedConnectionParameterException e) {}

        connParam = ConnectionTestUtils.getSametimeConnectionParam();
        connParam.getOtherParams().remove(SametimeConnectionImpl.PASSWORD);
        try {
            connection.init(connParam);
            fail("should throw exception")
        }
        catch (UndefinedConnectionParameterException e) {}

        //community is optional
        connParam = ConnectionTestUtils.getSametimeConnectionParam();
        connParam.getOtherParams().remove(SametimeConnectionImpl.COMMUNITY);
        try {
            connection.init(connParam);
        }
        catch (e) {
            fail("should not throw exception")
        }
    }

    public void testConnect() throws Exception {
        ConnectionParam param = ConnectionTestUtils.getSametimeConnectionParam();
        connection.init(param);
        connection._connect();
        assertTrue(connection.checkConnection());
        assertTrue(connection.getStsession().isActive())
        assertTrue(connection.getCommservice().isLoggedIn())
    }

    public void testServerIsInvalid() throws Exception {
        ConnectionParam param = ConnectionTestUtils.getSametimeConnectionParam();
        param.getOtherParams().put(SametimeConnectionImpl.HOST, "HostDoesNotExist")
        connection.init(param);
        try {
            connection._connect();
            fail("should throw exception")
        }
        catch (e) {
            if(!e.getMessage().equals(connection.getErrorMessage(STError.ST_CONNECT_HOST_UNREACHABLE))){
                assertEquals("Login request to the host HostDoesNotExist timed out.", e.getMessage())
            }

        }
    }

    public void testUsernameIsInvalid() throws Exception {
        ConnectionParam param = ConnectionTestUtils.getSametimeConnectionParam();
        param.getOtherParams().put(SametimeConnectionImpl.USERNAME, "Invalid user")
        connection.init(param);
        try {
            connection._connect();
            fail("should throw exception")
        }
        catch (e) {
            assertEquals(connection.getErrorMessage(STError.ST_CONNECT_BAD_LOGIN), e.getMessage())
        }
    }
    public void testPasswordIsInvalid() throws Exception {
        ConnectionParam param = ConnectionTestUtils.getSametimeConnectionParam();
        param.getOtherParams().put(SametimeConnectionImpl.PASSWORD, "InvalidPass")
        connection.init(param);
        try {
            connection._connect();
            fail("should throw exception")
        }
        catch (e) {
            assertEquals(connection.getErrorMessage(STError.ST_CONNECT_BAD_LOGIN), e.getMessage())
        }
    }

    public void testResolveUser() {
        ConnectionParam param = ConnectionTestUtils.getSametimeConnectionParam();
        connection.init(param);
        connection._connect();
        connection.resolveUser(receiverUsername);
        assertEquals(1, connection.getTargets().size());
        assertEquals(1, connection.getUserIds().size());
        String userId = connection.getUserIds().get(receiverUsername);
        assertNotNull(userId);
        assertEquals(receiverUsername, connection.getTargets().get(userId));
        assertEquals(1, connection.getResolvedUsers().size());
        assertTrue(connection.getResolvedUsers().containsKey(userId));
    }

    public void testDisconnect() throws Exception {
        ConnectionParam param = ConnectionTestUtils.getSametimeConnectionParam();
        connection.init(param);
        connection._connect();
        assertTrue(connection.checkConnection());
        connection._disconnect();
        assertFalse(connection.checkConnection());
        assertFalse(connection.getStsession().isActive())
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertFalse(connection.getCommservice().isLoggedIn())    
        }))
    }

    public void testSendMessage() throws Exception {
        ConnectionParam param = ConnectionTestUtils.getSametimeConnectionParam();
        connection.init(param);
        connection._connect();

        SametimeConnectionImpl receiverConnection = new SametimeConnectionImpl();
        param.getOtherParams().put(SametimeConnectionImpl.USERNAME, receiverUsername);
        param.getOtherParams().put(SametimeConnectionImpl.PASSWORD, CommonTestUtils.getTestProperty(ConnectionTestConstants.SAMETIME_SECONDARY_PASSWORD));
        receiverConnection.init(param);
        receiverConnection._connect();
        try {
            def receivedMessages = [];
            def textReceived = {from, message ->
                receivedMessages.add(message);
            }
            receiverConnection.setTextReceivedCallback(textReceived)
            def messageText = "Hello World";
            connection.sendImMessage(receiverUsername, messageText);

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
        ConnectionParam param = ConnectionTestUtils.getSametimeConnectionParam();
        connection.init(param);
        def messageText = "Hello World";

        try {
            connection.sendImMessage(receiverUsername, messageText);
            fail("should throw exception")
        }
        catch (ConnectionException e) {
            assertEquals("Sametime user <" + receiverUsername + "> resolve request timed out.", e.getMessage())
        }
    }

    public void testCannotSendOfflineMessages() {
        ConnectionParam param = ConnectionTestUtils.getSametimeConnectionParam();
        connection.init(param);
        connection._connect();

        def messageText = "Hello World";
        try {
            connection.sendImMessage(receiverUsername, messageText);
            fail("should throw exception")
        }
        catch (Exception e) {
            assertEquals("Sametime instant message session with user <" + receiverUsername + "> cannot be created.", e.getMessage());
        }
    }

    public void testSendingMessageToANonexistingUser() {
        ConnectionParam param = ConnectionTestUtils.getSametimeConnectionParam();
        connection.init(param);
        connection._connect();

        def messageText = "Hello World";
        def invalidUser = "invalidUser"
        try {
            connection.sendImMessage(invalidUser, messageText);
            fail("should throw exception")
        }
        catch (Exception e) {
            assertEquals("Sametime user <" + invalidUser + "> could not be resolved.", e.getMessage());
        }
    }

}