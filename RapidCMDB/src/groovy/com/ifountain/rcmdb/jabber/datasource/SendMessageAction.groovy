package com.ifountain.rcmdb.jabber.datasource

import com.ifountain.core.datasource.Action
import com.ifountain.core.connection.IConnection
import com.ifountain.rcmdb.jabber.connection.JabberConnectionImpl

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 3, 2009
* Time: 11:34:04 AM
*/
class SendMessageAction implements Action {
    private String target;
    private String message;
    public SendMessageAction(target, message) {
        this.target = target;
        this.message = message;
    }
    public void execute(IConnection conn) {
        JabberConnectionImpl connection = (JabberConnectionImpl) conn;
        connection.sendImMessage(target, message);
    }

}