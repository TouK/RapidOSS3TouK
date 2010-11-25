/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package http.connection

import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException
import com.ifountain.core.test.util.RapidCoreTestCase
import connection.HttpConnectionImpl
import com.ifountain.comp.utils.HttpUtils;

public class HttpConnectionImplTests extends RapidCoreTestCase {

    public void testInit() throws Exception {
    	HttpConnectionImpl conn = new HttpConnectionImpl();
        Map otherParams = [:];
        otherParams.put(HttpConnectionImpl.BASE_URL, "http://localhost:9999/");
        ConnectionParam param = new ConnectionParam("ds", HttpConnectionImpl.class.getName(), otherParams);
        try {
            conn.init(param);
        } catch (Throwable e) {
            fail("should not throw exception");
        }
        assertSame(param, conn.getParameters());
        assertEquals (param.getMinTimeout(), conn.getTimeout());
        assertEquals (param.getMinTimeout(), conn.getMinTimeout());
        assertEquals (param.getMaxTimeout(), conn.getMaxTimeout());

        param.getOtherParams().remove(HttpConnectionImpl.BASE_URL);
        try {
            conn.init(param);
            fail("should throw exception");
        } catch (UndefinedConnectionParameterException e) {
        }
    }

    public void testGetConnection() throws Exception {
        HttpConnectionImpl conn = new HttpConnectionImpl();
        Map otherParams = [:];
        otherParams.put(HttpConnectionImpl.BASE_URL, "http://www.google.com");
        ConnectionParam param = new ConnectionParam("ds", HttpConnectionImpl.class.getName(), otherParams);
        conn.init (param);


        HttpUtils httpUtils = conn.getHttpConnection();
        def expectedTimeoutValue = conn.getTimeout();
        assertEquals (expectedTimeoutValue, httpUtils.getTimeout());
        assertNotNull(httpUtils.doGetRequest ("http://www.google.com", [:]));
    }

    public void testIsConnectionException()
    {
        HttpConnectionImpl connection = new HttpConnectionImpl();
        ConnectException  exception = new ConnectException("exception");
        assertTrue (connection.isConnectionException(exception));

        SocketException socketException = new SocketException();
        assertTrue (connection.isConnectionException(socketException));

        IOException connectTimeoutException = new IOException();
        assertTrue (connection.isConnectionException(connectTimeoutException));

        NoRouteToHostException noRouteToHostException = new NoRouteToHostException();
        assertTrue (connection.isConnectionException(noRouteToHostException));

        IOException ioException = new IOException()
        assertFalse (connection.isConnectionException(ioException));

        Exception nestedException = new Exception(new SocketException());
        assertTrue (connection.isConnectionException(nestedException));

        Exception otherException = new Exception();
        assertFalse(connection.isConnectionException(otherException));
    }
}
