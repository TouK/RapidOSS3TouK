package com.ifountain.rcmdb.test.util

import com.ifountain.core.connection.ConnectionParam
import com.ifountain.rcmdb.jabber.connection.JabberConnectionImpl
import com.ifountain.comp.test.util.CommonTestUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 1, 2009
* Time: 4:17:16 PM
*/
class JabberTestUtils {
    public static ConnectionParam getConnectionParam() {
        Map otherParams = new HashMap();
        otherParams.put(JabberConnectionImpl.HOST, CommonTestUtils.getTestProperty(JabberTestConstants.JABBER_HOST))
        otherParams.put(JabberConnectionImpl.PORT, Long.parseLong(CommonTestUtils.getTestProperty(JabberTestConstants.JABBER_PORT)))
        otherParams.put(JabberConnectionImpl.USERNAME, CommonTestUtils.getTestProperty(JabberTestConstants.JABBER_USERNAME))
        otherParams.put(JabberConnectionImpl.PASSWORD, CommonTestUtils.getTestProperty(JabberTestConstants.JABBER_PASSWORD))
        otherParams.put(JabberConnectionImpl.SERVICENAME, CommonTestUtils.getTestProperty(JabberTestConstants.JABBER_SERVICENAME))

        ConnectionParam param = new ConnectionParam("JabberConnection", JabberTestConstants.JABBER_TEST_CONNECTION, JabberConnectionImpl.class.getName(), otherParams);
        param.setMinTimeout(30000);
        param.setMaxTimeout(30000);
        return param;
    }
}