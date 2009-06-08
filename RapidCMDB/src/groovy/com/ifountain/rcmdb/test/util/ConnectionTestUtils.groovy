package com.ifountain.rcmdb.test.util

import com.ifountain.core.connection.ConnectionParam
import com.ifountain.rcmdb.jabber.connection.JabberConnectionImpl
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.sametime.connection.SametimeConnectionImpl
import com.ifountain.rcmdb.sms.connection.SmsConnectionImpl

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 5, 2009
* Time: 4:00:15 PM
*/
class ConnectionTestUtils {
    public static ConnectionParam getJabberConnectionParam() {
        Map otherParams = new HashMap();
        otherParams.put(JabberConnectionImpl.HOST, CommonTestUtils.getTestProperty(ConnectionTestConstants.JABBER_HOST))
        otherParams.put(JabberConnectionImpl.PORT, Long.parseLong(CommonTestUtils.getTestProperty(ConnectionTestConstants.JABBER_PORT)))
        otherParams.put(JabberConnectionImpl.USERNAME, CommonTestUtils.getTestProperty(ConnectionTestConstants.JABBER_USERNAME))
        otherParams.put(JabberConnectionImpl.PASSWORD, CommonTestUtils.getTestProperty(ConnectionTestConstants.JABBER_PASSWORD))
        otherParams.put(JabberConnectionImpl.SERVICENAME, CommonTestUtils.getTestProperty(ConnectionTestConstants.JABBER_SERVICENAME))

        ConnectionParam param = new ConnectionParam("JabberConnection", ConnectionTestConstants.JABBER_TEST_CONNECTION, JabberConnectionImpl.class.getName(), otherParams);
        param.setMinTimeout(30000);
        param.setMaxTimeout(30000);
        return param;
    }

    public static ConnectionParam getSametimeConnectionParam() {
        Map otherParams = new HashMap();
        otherParams.put(SametimeConnectionImpl.HOST, CommonTestUtils.getTestProperty(ConnectionTestConstants.SAMETIME_HOST))
        otherParams.put(SametimeConnectionImpl.USERNAME, CommonTestUtils.getTestProperty(ConnectionTestConstants.SAMETIME_USERNAME))
        otherParams.put(SametimeConnectionImpl.PASSWORD, CommonTestUtils.getTestProperty(ConnectionTestConstants.SAMETIME_PASSWORD))
        otherParams.put(SametimeConnectionImpl.COMMUNITY, CommonTestUtils.getTestProperty(ConnectionTestConstants.SAMETIME_COMMUNITY))
        ConnectionParam param = new ConnectionParam("SametimeConnection", ConnectionTestConstants.SAMETIME_TEST_CONNECTION, SametimeConnectionImpl.class.getName(), otherParams);
        param.setMinTimeout(30000);
        param.setMaxTimeout(30000);
        return param;
    }

    public static ConnectionParam getSmsConnectionParam() {
        Map otherParams = new HashMap();
        otherParams.put(SmsConnectionImpl.HOST, CommonTestUtils.getTestProperty(ConnectionTestConstants.SMS_HOST))
        otherParams.put(SmsConnectionImpl.PORT, Long.parseLong(CommonTestUtils.getTestProperty(ConnectionTestConstants.SMS_PORT)))
        otherParams.put(SmsConnectionImpl.USERNAME, CommonTestUtils.getTestProperty(ConnectionTestConstants.SMS_USERNAME))
        otherParams.put(SmsConnectionImpl.PASSWORD, CommonTestUtils.getTestProperty(ConnectionTestConstants.SMS_PASSWORD))

        ConnectionParam param = new ConnectionParam("SmsConnection", ConnectionTestConstants.SAMETIME_TEST_CONNECTION, SmsConnectionImpl.class.getName(), otherParams);
        param.setMinTimeout(30000);
        param.setMaxTimeout(30000);
        return param;
    }
}