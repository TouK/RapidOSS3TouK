package com.ifountain.rcmdb.jabber.datasource

import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.rcmdb.jabber.connection.JabberConnectionImpl
import com.ifountain.rcmdb.test.util.ConnectionTestConstants
import com.ifountain.rcmdb.test.util.ConnectionTestUtils
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.comp.test.util.CommonTestUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 3, 2009
* Time: 11:35:09 AM
*/
class SendMessageActionTests extends RapidCoreTestCase {
    JabberConnectionImpl connection;
    String receiverUsername;
    protected void setUp() {
        super.setUp();
        receiverUsername = CommonTestUtils.getTestProperty(ConnectionTestConstants.JABBER_SECONDARY_USERNAME)
        connection = new JabberConnectionImpl();
        connection.init(ConnectionTestUtils.getJabberConnectionParam());
        connection._connect();
    }

    protected void tearDown() {
        if (connection.isConnected()) {
            connection._disconnect();
        }
        super.tearDown();
    }

    public void testExecute() {
        def messageText = "Hello World";
        def serviceName = CommonTestUtils.getTestProperty(ConnectionTestConstants.JABBER_SERVICENAME);
        def userTarget = "${receiverUsername}@${serviceName}"
        SendMessageAction action = new SendMessageAction(userTarget, messageText);
        JabberConnectionImpl receiverConnection = new JabberConnectionImpl();
        ConnectionParam param = ConnectionTestUtils.getJabberConnectionParam();
        param.getOtherParams().put(JabberConnectionImpl.USERNAME, receiverUsername)
        param.getOtherParams().put(JabberConnectionImpl.PASSWORD, CommonTestUtils.getTestProperty(ConnectionTestConstants.JABBER_SECONDARY_PASSWORD))
        receiverConnection.init(param);
        receiverConnection._connect();
        try {
            def receivedMessages = [];
            def textReceived = {from, message ->
                receivedMessages.add(message);
            }
            receiverConnection.setTextReceivedCallback(textReceived)
            action.execute(connection);
            CommonTestUtils.waitFor(new ClosureWaitAction({
                assertEquals(1, receivedMessages.size())
                assertEquals(messageText, receivedMessages[0])
            }))
        }
        finally {
            receiverConnection._disconnect();
        }

    }
}