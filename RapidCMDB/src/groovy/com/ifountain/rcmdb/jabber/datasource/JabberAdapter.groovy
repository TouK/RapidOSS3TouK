package com.ifountain.rcmdb.jabber.datasource

import com.ifountain.core.datasource.BaseAdapter
import org.apache.log4j.Logger

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 3, 2009
* Time: 11:40:22 AM
*/
class JabberAdapter extends BaseAdapter{
    public JabberAdapter(String connConfigName, long reconnectInterval, Logger logger) {
        super(connConfigName, reconnectInterval, logger)
    }
    public void sendMessage(String target, String message) throws Exception {
        SendMessageAction action = new SendMessageAction(target, message);
        executeAction(action);
    }
    public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) {
        return null; 
    }

}