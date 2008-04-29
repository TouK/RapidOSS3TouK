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
    
    private boolean isConnected = false;
    private ConnectionParam param;
    private Exception connectionException = null;
    
    public void init(ConnectionParam param)
    {
        this.param = param;
    }
    public ConnectionParam getParameters()
    {
        return param;
    }
    public void _connect() throws Exception
    {
        if(connectionException != null)
        {
            throw connectionException;
        }
        isConnected = true;
    }
    public void _disconnect()
    {
        isConnected = false;
    }
    public boolean isConnected()
    {
        return isConnected;
    }
    public ConnectionParam getParam()
    {
        return param;
    }
    public void setConnectionException(Exception connectionException)
    {
        this.connectionException = connectionException;
        disconnect();
    }
}