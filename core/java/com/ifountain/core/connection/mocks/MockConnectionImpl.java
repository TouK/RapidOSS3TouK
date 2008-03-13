/*
 * Created on Jan 21, 2008
 *
 */
package com.ifountain.core.connection.mocks;

import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.IConnection;
public class MockConnectionImpl implements IConnection
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
    public void connect() throws Exception
    {
        if(connectionException != null)
        {
            throw connectionException;
        }
        isConnected = true;
    }
    public void disconnect()
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