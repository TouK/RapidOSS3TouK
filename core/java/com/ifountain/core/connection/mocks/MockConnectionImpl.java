/*
 * Created on Jan 21, 2008
 *
 */
package com.ifountain.core.connection.mocks;

import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.IConnection;
import com.ifountain.core.connection.BaseConnection;

public class MockConnectionImpl extends BaseConnection
{
    public static long defaultTimeout = 7777;
    public static Exception globalConnectionException;
    private Exception connectionException = null;
    
    public void init(ConnectionParam param)
    {
        this.params = param;
        this.timeout = defaultTimeout;
    }
    protected void connect() throws Exception
    {
        if(connectionException != null)
        {
            throw connectionException;
        }
        if(globalConnectionException != null) throw globalConnectionException;
    }

    public boolean checkConnection() {
        return isConnected();
    }

    protected void disconnect()
    {
    }
    public ConnectionParam getParam()
    {
        return params;
    }
    public void setConnectionException(Exception connectionException)
    {
        this.connectionException = connectionException;
        disconnect();
    }
}