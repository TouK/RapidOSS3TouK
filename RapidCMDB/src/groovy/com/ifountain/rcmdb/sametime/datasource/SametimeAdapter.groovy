package com.ifountain.rcmdb.sametime.datasource

import com.ifountain.core.datasource.BaseAdapter
import org.apache.log4j.Logger

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 21, 2009
* Time: 1:21:59 PM
*/
class SametimeAdapter extends BaseAdapter {
    public SametimeAdapter(String connConfigName, long reconnectInterval, Logger logger) {
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