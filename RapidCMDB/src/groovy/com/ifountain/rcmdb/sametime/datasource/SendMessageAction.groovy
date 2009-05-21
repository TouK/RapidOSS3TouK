package com.ifountain.rcmdb.sametime.datasource

import com.ifountain.core.datasource.Action
import com.ifountain.core.connection.IConnection
import com.ifountain.rcmdb.sametime.connection.SametimeConnectionImpl

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 21, 2009
* Time: 1:23:16 PM
*/
class SendMessageAction implements Action{
    private String target;
    private String message;
    public SendMessageAction(target, message){
        this.target = target;
        this.message = message;
    }
    public void execute(IConnection conn) throws Exception{
        SametimeConnectionImpl sametimeConn = (SametimeConnectionImpl) conn;
        sametimeConn.sendImMessage(target, message);
    }

}