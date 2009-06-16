package com.ifountain.rcmdb.test.util

import com.ifountain.core.connection.ConnectionParam
import com.ifountain.rcmdb.jabber.connection.JabberConnectionImpl
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.sametime.connection.SametimeConnectionImpl
import com.ifountain.rcmdb.sms.connection.SmsConnectionImpl
import com.ifountain.rcmdb.aol.connection.AolConnectionImpl

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 5, 2009
* Time: 4:00:15 PM
*/
class ConnectionTestUtils {
    private static int aolUserCount = 5;
	private static int lastAolUser = 1;
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

    public static ConnectionParam getAolConnectionParam() {
        Map otherParams = new HashMap();
        AolUser aolUser = getAolUser();
        otherParams.put(AolConnectionImpl.HOST, CommonTestUtils.getTestProperty(ConnectionTestConstants.AOL_HOST))
        otherParams.put(AolConnectionImpl.PORT, Long.parseLong(CommonTestUtils.getTestProperty(ConnectionTestConstants.AOL_PORT)))
        otherParams.put(AolConnectionImpl.USERNAME, aolUser.getUsername())
        otherParams.put(AolConnectionImpl.PASSWORD, aolUser.getPassword())

        ConnectionParam param = new ConnectionParam("SmsConnection", ConnectionTestConstants.AOL_TEST_CONNECTION, AolConnectionImpl.class.getName(), otherParams);
        param.setMinTimeout(30000);
        param.setMaxTimeout(30000);
        return param;
    }

    public static AolUser getAolUser()
    {
    	lastAolUser++;
    	if(lastAolUser > aolUserCount)
    	{
    		lastAolUser = 1;
    	}
    	return new AolUser(CommonTestUtils.getTestProperty(ConnectionTestConstants.AOL_USERNAME + lastAolUser), CommonTestUtils.getTestProperty(ConnectionTestConstants.AOL_PASSWORD + lastAolUser));
    }
}

public class AolUser
{
    private String username;
    private String password;
    public AolUser(String username, String password)
    {
        this.username = username;
        this.password = password;
    }
    public String getPassword()
    {
        return password;
    }
    public String getUsername()
    {
        return username;
    }
    public String toString()
    {
        return "Username: " + username + ", Password: " + password;
    }
}