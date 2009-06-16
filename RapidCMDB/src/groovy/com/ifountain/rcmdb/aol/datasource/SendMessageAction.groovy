package com.ifountain.rcmdb.aol.datasource

import com.ifountain.core.datasource.Action
import com.ifountain.core.connection.IConnection
import com.ifountain.rcmdb.aol.connection.AolConnectionImpl

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 16, 2009
* Time: 10:34:34 AM
*/
class SendMessageAction implements Action {

    private String target;
    private String message;
    public SendMessageAction(target, message) {
        this.target = target;
        this.message = message;
    }
    public void execute(IConnection conn) throws Exception {
        AolConnectionImpl aolConnection = (AolConnectionImpl) conn;
        aolConnection.sendMessage(target, message);
    }

}