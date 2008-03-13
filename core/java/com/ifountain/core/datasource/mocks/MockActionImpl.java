/*
 * Created on Jan 21, 2008
 *
 */
package com.ifountain.core.datasource.mocks;

import com.ifountain.core.connection.IConnection;
import com.ifountain.core.datasource.Action;

public class MockActionImpl implements Action
{
    private IConnection conn;
    private boolean executed = false;
    private Exception exceptionWillBeThrown ; 
    public void execute(IConnection conn) throws Exception
    {
        executed = true;
        this.conn = conn;
        if(exceptionWillBeThrown != null)
        {
            throw exceptionWillBeThrown;
        }
    }
    public IConnection getConnection()
    {
        return conn;
    }
    public boolean isExecuted()
    {
        return executed;
    }
    public void setExceptionWillBeThrown(Exception exceptionWillBeThrown)
    {
        this.exceptionWillBeThrown = exceptionWillBeThrown;
    }
}