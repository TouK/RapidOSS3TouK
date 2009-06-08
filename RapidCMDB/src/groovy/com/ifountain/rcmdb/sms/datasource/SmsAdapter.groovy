package com.ifountain.rcmdb.sms.datasource

import com.ifountain.core.datasource.BaseAdapter
import org.apache.log4j.Logger

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 5, 2009
* Time: 2:24:25 PM
*/
class SmsAdapter extends BaseAdapter {
    public SmsAdapter(String connConfigName, long reconnectInterval, Logger logger) {
        super(connConfigName, reconnectInterval, logger)
    }
    public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) {
        return null;
    }

    public void sendMessage(String smsNumber, String message) {
        SendMessageAction action = new SendMessageAction(smsNumber, message)
        executeAction(action);
    }

}