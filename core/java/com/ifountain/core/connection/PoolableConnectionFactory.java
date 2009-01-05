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
import org.apache.log4j.Logger;

import com.ifountain.core.connection.exception.ConnectionException;
import com.ifountain.core.connection.exception.ConnectionInitializationException;
 
public class PoolableConnectionFactory extends BaseConnectionFactory
{
    Logger logger = Logger.getLogger(PoolableConnectionFactory.class); 
    private ClassLoader classLoader;
    
    public PoolableConnectionFactory(ClassLoader classLoader, String connectionName, ConnectionParameterSupplier paramSupplier, Class timeoutStrategyClass)
    {
        super(connectionName, paramSupplier, timeoutStrategyClass);
        this.classLoader = classLoader;
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
        ConnectionParam param = getConnectionParameter();
        String className = param.getConnectionClass();
        IConnection conn;
        try
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("Creating a new instance for connection " + param.getConnectionName()+ " from Class:" + className + " with Timeout:" + timeout + " with Parameters:"+ param);
            }
            conn = (IConnection) classLoader.loadClass(className).newInstance();
            if(logger.isDebugEnabled())
            {
                logger.debug("Created a new instance for connection " + param.getConnectionName());
            }
        }
        catch (Exception e)
        {
            throw new ConnectionInitializationException("Could not initialize connection " + param.getConnectionName(),e);
        }
        conn.init((ConnectionParam)param.clone());
        if(timeout > 0)
        {
            conn.setTimeout(timeout);
        }
        try
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("Connecting instance of " + param.getConnectionName());
            }
            conn._connect();
            if(logger.isDebugEnabled())
            {
                logger.debug("Connected instance of " + param.getConnectionName());
            }
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
        ConnectionParam param = getConnectionParameter();
        try
        {
            IConnection conn = (IConnection)arg0;

            if(logger.isDebugEnabled())
            {
                logger.debug("Validating connection instance of " + param.getConnectionName());
            }
            boolean result1 = (conn.getClass() == classLoader.loadClass(conn.getClass().getName()));
            if(logger.isDebugEnabled() && !result1)
            {
                logger.debug("Invalid connection instance of " + param.getConnectionName()+". Connection classes are not same");
            }
            boolean result2 = conn.getParameters().equals(param);
            if(logger.isDebugEnabled() && !result2)
            {
                logger.debug("Invalid connection instance of " + param.getConnectionName()+". Connection parameters are not equals. Pool params:"+param+" Connection Params:"+conn.getParameters());
            }
            boolean finalResult = result1 && result2 && conn.isConnected();
            if(logger.isDebugEnabled() && !finalResult)
            {
                logger.debug("Connection instance " + param.getConnectionName()+" is not valid");
            }
            return finalResult;
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }
    }
}
