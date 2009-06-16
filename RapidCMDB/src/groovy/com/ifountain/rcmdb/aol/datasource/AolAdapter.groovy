package com.ifountain.rcmdb.aol.datasource

import com.ifountain.core.datasource.BaseAdapter
import org.apache.log4j.Logger

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 9, 2009
* Time: 4:37:48 PM
*/
class AolAdapter extends BaseAdapter {
    public AolAdapter(String connConfigName, long reconnectInterval, Logger logger) {
        super(connConfigName, reconnectInterval, logger)
    }
    public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) {
        return null;
    }
    public void sendMessage(String target, String message) {
        SendMessageAction action = new SendMessageAction(target, message)
        executeAction(action);
    }
}