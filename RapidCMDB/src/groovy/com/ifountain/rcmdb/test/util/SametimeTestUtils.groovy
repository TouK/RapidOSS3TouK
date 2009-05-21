package com.ifountain.rcmdb.test.util

import com.ifountain.core.connection.ConnectionParam
import com.ifountain.rcmdb.sametime.connection.SametimeConnectionImpl
import com.ifountain.comp.test.util.CommonTestUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 21, 2009
* Time: 1:27:40 PM
*/
class SametimeTestUtils {
    public static ConnectionParam getConnectionParam() {
        Map otherParams = new HashMap();
        otherParams.put(SametimeConnectionImpl.HOST, CommonTestUtils.getTestProperty(SametimeTestConstants.SAMETIME_HOST))
        otherParams.put(SametimeConnectionImpl.USERNAME, CommonTestUtils.getTestProperty(SametimeTestConstants.SAMETIME_USERNAME))
        otherParams.put(SametimeConnectionImpl.PASSWORD, CommonTestUtils.getTestProperty(SametimeTestConstants.SAMETIME_PASSWORD))
        otherParams.put(SametimeConnectionImpl.COMMUNITY, CommonTestUtils.getTestProperty(SametimeTestConstants.SAMETIME_COMMUNITY))
        ConnectionParam param = new ConnectionParam("SametimeConnection", SametimeTestConstants.SAMETIME_TEST_CONNECTION, SametimeConnectionImpl.class.getName(), otherParams);
        param.setMinTimeout(30000);
        param.setMaxTimeout(30000);
        return param;
    }
}