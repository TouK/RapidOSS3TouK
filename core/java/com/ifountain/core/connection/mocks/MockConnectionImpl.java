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
    private Exception connectionException = null;
    
    public void init(ConnectionParam param)
    {
        this.params = param;
    }
    protected void connect() throws Exception
    {
        if(connectionException != null)
        {
            throw connectionException;
        }
        isConnected = true;
    }
    protected void disconnect()
    {
        isConnected = false;
    }
    public boolean isConnected()
    {
        return isConnected;
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