package com.ifountain.rcmdb.sms.datasource

import com.ifountain.core.datasource.Action
import com.ifountain.core.connection.IConnection
import com.ifountain.rcmdb.sms.connection.SmsConnectionImpl

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 8, 2009
* Time: 1:34:44 PM
*/
class SendMessageAction implements Action {
    private String target;
    private String message;
    public SendMessageAction(target, message) {
        this.target = target;
        this.message = message;
    }
    public void execute(IConnection conn) throws Exception {
        SmsConnectionImpl smsConnection = (SmsConnectionImpl) conn;
        smsConnection.sendMessage(target, message);
    }

}