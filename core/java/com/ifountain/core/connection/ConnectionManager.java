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
 * Created on Jan 16, 2008
 *
 */
package com.ifountain.core.connection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

import com.ifountain.core.connection.exception.ConnectionException;
import com.ifountain.core.connection.exception.ConnectionInitializationException;
import com.ifountain.core.connection.exception.ConnectionPoolException;
import com.ifountain.core.connection.exception.UndefinedConnectionException;



public class ConnectionManager
{
    private static boolean isInitialized = false;
    private static ConnectionParameterSupplier paramSupplier;
    private static Map<String, ObjectPool> pools;
    private static Logger logger;
    private static ClassLoader classLoader;
    private static Map<ObjectPool, PoolableConnectionFactory> poolFactoryMap = new HashMap<ObjectPool, PoolableConnectionFactory>();
    private ConnectionManager()
    {
    }

    public static boolean checkConnection(String connectionName)
    {
        boolean connected=false;
        try{
            IConnection conn = getConnection(connectionName);
            if(conn.isConnected()){
                connected = true;
            }
            releaseConnection(conn);
        }
        catch(Exception e){
             logger.warn("Exception in ConnectionManager checkConnection "+e, e);
        }

        return connected;
    }
    public static IConnection getConnection(String connectionName) throws ConnectionInitializationException, ConnectionPoolException, ConnectionException, UndefinedConnectionException
    {
        ConnectionParam param = paramSupplier.getConnectionParam(connectionName);
        if(param == null)
        {
            throw new UndefinedConnectionException(connectionName);
        }
        ObjectPool pool = pools.get(connectionName);
        if(pool == null)
        {
            PoolableConnectionFactory connectionFactory = new PoolableConnectionFactory(classLoader, param);
            GenericObjectPool newPool = new GenericObjectPool(connectionFactory, param.getMaxNumberOfConnectionsInPool());
            poolFactoryMap.put(newPool, connectionFactory);
            newPool.setTestOnBorrow(true);
            newPool.setTestOnReturn(true);
            pool = newPool;
            pools.put(connectionName, pool);
        }
        else{
            PoolableConnectionFactory connectionFactory = poolFactoryMap.get(pool);
            connectionFactory.setParam(param);
            ((GenericObjectPool)pool).setMaxActive(param.getMaxNumberOfConnectionsInPool());
            
        }
        IConnection conn;
        try
        {
            conn = (IConnection) pool.borrowObject();
        }
        catch(ConnectionInitializationException e)
        {
            throw e;
        }
        catch(ConnectionException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ConnectionPoolException("An exception occurred while getting connection "+ connectionName + " from pool", e);
        }
        return conn;
    }
    
    public static void releaseConnection(IConnection connection) throws ConnectionInitializationException, ConnectionPoolException, ConnectionException
    {
        ObjectPool pool = pools.get(connection.getParameters().getConnectionName());
        if(pool != null)
        {
            try
            {
                pool.returnObject(connection);
            }
            catch (Exception e)
            {
                throw new ConnectionPoolException("An exception occurred while releasing connection "+ connection.getParameters().getConnectionName() + " from pool", e);
            }
        }
        else{
            connection._disconnect();
        }
    }

    public static void initialize(Logger logger, ConnectionParameterSupplier paramSupplier, ClassLoader classLoader)
    {
        if(!isInitialized)
        {
            ConnectionManager.classLoader = classLoader;
            ConnectionManager.paramSupplier = paramSupplier;
            ConnectionManager.pools = new HashMap<String, ObjectPool>();
            ConnectionManager.logger = logger;
            logger.info("ConnectionManager is initialized");
            isInitialized = true;
        }
    }

    public static Object getActiveCount(String connectionName)
    {
        ObjectPool pool = pools.get(connectionName);
        if(pool != null)
        {
            return pool.getNumActive();
        }
        return -1;
    }

    public static void destroy()
    {
        logger.info("Destroying ConnectionManager having " + pools.size() + " number of pools.");
        Set<Entry<String, ObjectPool>> values = pools.entrySet();
        for (Iterator<Entry<String, ObjectPool>> iterator = values.iterator(); iterator.hasNext();)
        {
            
            Entry<String, ObjectPool> poolEntry = iterator.next();
            String connectionName = poolEntry.getKey();
            ObjectPool connectionPool = poolEntry.getValue();
            logger.info("Destroying ConnectionPool of " + connectionName + " having "+ connectionPool.getNumActive() + " active connections.");
            try
            {
                connectionPool.close();
            }
            catch (Exception e)
            {
                logger.warn("An exception occurred while destroying ConnectionPool " + connectionName);
            }
        }
        pools.clear();
        isInitialized = false;
    }

    public static void setParamSupplier(ConnectionParameterSupplier paramSupplier)
    {
        ConnectionManager.paramSupplier = paramSupplier;
    }

    public static void removeConnection(String connectionName) throws Exception {
        GenericObjectPool pool = (GenericObjectPool) pools.remove(connectionName);
        if(pool != null){
            pool.close();
        }
    }

    public static boolean isInitialized() {
        return isInitialized;
    }

    public static ConnectionParameterSupplier getParamSupplier() {
        return paramSupplier;
    }
}
