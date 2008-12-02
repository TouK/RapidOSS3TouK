/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be 
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
/*
 * Created on Jan 17, 2008
 *
 */
package com.ifountain.core.connection;

import org.apache.commons.pool.PoolableObjectFactory;

import com.ifountain.core.connection.exception.ConnectionException;
import com.ifountain.core.connection.exception.ConnectionInitializationException;
 
public class PoolableConnectionFactory extends BaseConnectionFactory
{

    private ConnectionParam param;
    private ClassLoader classLoader;
    
    /**
     * @param param
     */
    public PoolableConnectionFactory(ClassLoader classLoader, ConnectionParam param, Class timeoutStrategyClass)
    {
        super(param.getConnectionName(), timeoutStrategyClass);
        this.classLoader = classLoader;
        this.param = param;
    }

    public void activateObject(Object arg0) throws Exception
    {
    }

    protected void _destroyObject(IConnection conn) throws Exception
    {
        conn._disconnect();
    }

    protected IConnection _makeObject(long timeout) throws Exception
    {
        String className = param.getConnectionClass();
        IConnection conn;
        try
        {
            conn = (IConnection) classLoader.loadClass(className).newInstance();
        }
        catch (Exception e)
        {
            throw new ConnectionInitializationException("Could not initialized connection " + param.getConnectionName(),e);
        }
        conn.init(param);
        if(timeout > 0)
        {
            conn.setTimeout(timeout);
        }
        try
        {
            conn._connect();
        }
        catch (Throwable e)
        {
            throw new ConnectionException(e);
        }
        return conn;
    }

    public void passivateObject(Object arg0) throws Exception
    {
    }

    public boolean validateObject(Object arg0)
    {
        try
        {
            IConnection conn = (IConnection)arg0;
            boolean result1 = (conn.getClass() == classLoader.loadClass(conn.getClass().getName())); 
            boolean result2 = conn.getParameters().equals(param);
            return result1 && result2 && conn.isConnected();
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }
    }

    public void setParam(ConnectionParam param) {
        this.param = param;
    }

}
