package com.ifountain.rcmdb.aol.datasource

import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.rcmdb.aol.connection.AolConnectionImpl
import com.ifountain.rcmdb.test.util.ConnectionTestUtils
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.comp.test.util.CommonTestUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 16, 2009
* Time: 10:36:59 AM
*/
class SendMessageActionTests extends RapidCoreTestCase{
    AolConnectionImpl connection;
    protected void setUp() {
        super.setUp();
        connection = new AolConnectionImpl();
        connection.init(ConnectionTestUtils.getAolConnectionParam());
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
        ConnectionParam param = ConnectionTestUtils.getAolConnectionParam();
        String receiverUsername = param.getOtherParams().get(AolConnectionImpl.USERNAME);
        SendMessageAction action = new SendMessageAction(receiverUsername, messageText);

        AolConnectionImpl receiverConnection = new AolConnectionImpl();
        receiverConnection.init(param);
        receiverConnection._connect();
        Thread.sleep(1000);
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