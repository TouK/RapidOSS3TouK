package com.ifountain.rcmdb.sms.datasource

import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.rcmdb.sms.connection.SmsConnectionImpl
import com.ifountain.rcmdb.test.util.ConnectionTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.comp.test.util.CommonTestUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 8, 2009
* Time: 1:36:10 PM
*/
class SendMessageActionTests extends RapidCoreTestCase {
    SmsConnectionImpl connection;
    protected void setUp() {
        super.setUp();
        connection = new SmsConnectionImpl();
        connection.init(ConnectionTestUtils.getSmsConnectionParam());
        connection._connect();
    }

    protected void tearDown() {
        if (connection.isConnected()) {
            connection._disconnect();
        }
        super.tearDown();
    }

    public void testExecute() {
        def receivedMessages = [];
        def messageText = "Hello World";
        def smsNumber = "212333"
        SendMessageAction action = new SendMessageAction(smsNumber, messageText);
        connection.setTextReceivedCallback {target, message ->
            receivedMessages.add(message);
        }
        action.execute(connection);
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, receivedMessages.size())
            assertEquals(messageText, receivedMessages[0])
        }))
    }
}