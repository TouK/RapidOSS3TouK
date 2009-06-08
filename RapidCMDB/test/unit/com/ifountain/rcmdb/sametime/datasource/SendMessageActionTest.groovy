package com.ifountain.rcmdb.sametime.datasource

import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.rcmdb.sametime.connection.SametimeConnectionImpl
import com.ifountain.rcmdb.test.util.ConnectionTestUtils
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ConnectionTestConstants
import com.ifountain.rcmdb.test.util.ClosureWaitAction

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 21, 2009
* Time: 1:25:35 PM
*/
class SendMessageActionTest extends RapidCoreTestCase {
    SametimeConnectionImpl connection;
    String receiverUsername;
    protected void setUp() {
        super.setUp();
        receiverUsername = CommonTestUtils.getTestProperty(ConnectionTestConstants.SAMETIME_SECONDARY_USERNAME)
        connection = new SametimeConnectionImpl();
        connection.init(ConnectionTestUtils.getSametimeConnectionParam());
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
        SendMessageAction action = new SendMessageAction(receiverUsername, messageText);

        SametimeConnectionImpl receiverConnection = new SametimeConnectionImpl();
        ConnectionParam param = ConnectionTestUtils.getSametimeConnectionParam();
        param.getOtherParams().put(SametimeConnectionImpl.USERNAME, receiverUsername)
        param.getOtherParams().put(SametimeConnectionImpl.PASSWORD, CommonTestUtils.getTestProperty(ConnectionTestConstants.SAMETIME_SECONDARY_PASSWORD))
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