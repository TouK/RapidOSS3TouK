package com.ifountain.rcmdb.sms.connection

import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.ConnectionException
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException
import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.rcmdb.test.util.ConnectionTestUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 5, 2009
* Time: 2:51:14 PM
*/
class SmsConnectionImplTests extends RapidCoreTestCase {
    SmsConnectionImpl connection;
    protected void setUp() {
        super.setUp();
        connection = new SmsConnectionImpl();
    }

    protected void tearDown() {
        if (connection.isConnected()) {
            connection._disconnect();
        }
        super.tearDown();
    }

    public void testInit() throws Exception {
        ConnectionParam connParam = ConnectionTestUtils.getSmsConnectionParam();
        try {
            connection.init(connParam);
        }
        catch (e) {
            e.printStackTrace()
            fail("should not throw exception")
        }
        assertSame(connParam, connection.getParameters());

        connParam.getOtherParams().remove(SmsConnectionImpl.HOST);
        try {
            connection.init(connParam);
            fail("should throw exception")
        }
        catch (UndefinedConnectionParameterException e) {}

        connParam = ConnectionTestUtils.getSmsConnectionParam();
        connParam.getOtherParams().remove(SmsConnectionImpl.PORT);
        try {
            connection.init(connParam);
            fail("should not throw exception")
        }
        catch (e) {
        }

        connParam = ConnectionTestUtils.getSmsConnectionParam();
        connParam.getOtherParams().remove(SmsConnectionImpl.USERNAME);
        try {
            connection.init(connParam);
            fail("should throw exception")
        }
        catch (UndefinedConnectionParameterException e) {}

        connParam = ConnectionTestUtils.getSmsConnectionParam();
        connParam.getOtherParams().remove(SmsConnectionImpl.PASSWORD);
        try {
            connection.init(connParam);
            fail("should throw exception")
        }
        catch (UndefinedConnectionParameterException e) {}

    }

    public void testConnect() throws Exception {
        ConnectionParam param = ConnectionTestUtils.getSmsConnectionParam();
        connection.init(param);
        connection._connect();
        assertTrue(connection.checkConnection());
        assertTrue(connection.getSmppConnection().isBound());
        assertTrue(connection.getSmscLink().isConnected());
        assertEquals(connection.getTimeout(), connection.getSmscLink().getTimeout());
    }

    public void testConnectThrowsExceptionIfServerIsUnavailable() {
        ConnectionParam param = ConnectionTestUtils.getSmsConnectionParam();
        param.getOtherParams().put(SmsConnectionImpl.HOST, "NONEXISTANT_SERVER");
        connection.init(param);
        try {
            connection._connect();
            fail("should throw exception")
        }
        catch (UnknownHostException e) {
        }
    }

    public void testConnectThrowsExceptionIfLoginCredentialsAreInvalid() {
        ConnectionParam param = ConnectionTestUtils.getSmsConnectionParam();
        param.getOtherParams().put(SmsConnectionImpl.USERNAME, "INVALID_USER");
        connection.init(param);
        try {
            connection._connect();
            fail("should throw exception")
        }
        catch (ConnectionException e) {
            assertEquals("Could not bind to the host ${param.getOtherParams().get(SmsConnectionImpl.HOST)}.", e.getMessage())
        }
    }

    public void testDisconnect() {
        ConnectionParam param = ConnectionTestUtils.getSmsConnectionParam();
        connection.init(param);
        connection._connect();
        assertTrue(connection.checkConnection());
        assertTrue(connection.getSmppConnection().isBound());
        assertTrue(connection.getSmscLink().isConnected());

        connection._disconnect();
        assertFalse(connection.checkConnection());
        assertFalse(connection.getSmppConnection().isBound());
        assertFalse(connection.getSmscLink().isConnected());
    }

    public void testSendMessage() {
        def receivedMessages = [];

        ConnectionParam param = ConnectionTestUtils.getSmsConnectionParam();
        connection.init(param);
        connection.setTextReceivedCallback {sender, message ->
            receivedMessages.add(message);
        }
        connection._connect();
        def smsNumber = "212333"
        def messageText = "messageText"
        connection.sendMessage(smsNumber, messageText + 1);
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, receivedMessages.size())    
        }))
        assertEquals(messageText + 1, receivedMessages[0])

        connection.sendMessage(smsNumber, messageText + 2);
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, receivedMessages.size())
        }))
        assertEquals(messageText + 2, receivedMessages[1])
    }

}