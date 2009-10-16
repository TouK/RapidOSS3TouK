package com.ifountain.rcmdb.connection

import com.ifountain.comp.utils.CaseInsensitiveMap
import com.ifountain.core.connection.ConnectionManager
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.ConnectionParameterSupplier
import connection.Connection
import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 23, 2008
 * Time: 2:41:47 PM
 * To change this template use File | Settings | File Templates.
 */
class RcmdbConnectionManagerAdapter implements ConnectionParameterSupplier{
    private Map connectionMap = [:]
    private static RcmdbConnectionManagerAdapter adapter;
    public static com.ifountain.rcmdb.connection.RcmdbConnectionManagerAdapter getInstance()
    {
        if(adapter == null)
        {
            adapter = new RcmdbConnectionManagerAdapter();
        }
        return adapter
    }
    public static com.ifountain.rcmdb.connection.RcmdbConnectionManagerAdapter destroyInstance()
    {
        if(adapter != null)
        {
            adapter.destroy();
            adapter = null;
        }
    }


    public ConnectionParam getConnectionParam(String connConfigName) {
        ConnectionParam connParam =  connectionMap[connConfigName]
        if(connParam == null)
        {
            Connection conn = Connection.get(name:connConfigName);
            if(conn != null)
            {
                connParam =  createConnectionParamFromObject(conn)
                connectionMap[connConfigName] = connParam;
            }
        }
        return connParam;
    }

    public synchronized void initialize(Logger logger, ClassLoader classLoader, long poolCheckInterval, Class timeoutStrategy)
    {
        ConnectionManager.initialize (logger, this, classLoader, poolCheckInterval);
        if(timeoutStrategy != null)
        {
            ConnectionManager.setTimeoutStrategyClass(timeoutStrategy);
        }
    }

    
    public synchronized void destroy()
    {
        ConnectionManager.destroy();
        connectionMap.clear();
    }


    public synchronized void removeConnection(String connectionName) throws Exception
    {
        ConnectionManager.removeConnection (connectionName);
        connectionMap.remove(connectionName);
    }

    public synchronized void addConnection(Connection connection) throws Exception{
        connectionMap[connection.name] = createConnectionParamFromObject(connection);
        ConnectionManager.markConnectionCheckerToRun(connection.name);
    }

    private ConnectionParam createConnectionParamFromObject(Connection connection)
    {
        def excludedProps = ['id', 'maxNumberOfConnections', "name", "connectionClass", "minTimeout", "maxTimeout"]
        def optProps = new CaseInsensitiveMap();
        connection.getPropertiesList().each{prop->
            if(!excludedProps.contains(prop.name) && !prop.isRelation && !prop.isOperationProperty)
            {
                optProps[prop.name] = connection.getProperty(prop.name);
            }
        }
        if(optProps.userPassword)
        {
            optProps.put ("password",optProps.userPassword);
        }
        else
        {
            optProps.put ("password","");
        }
        return new ConnectionParam(connection.getClass().getName(), connection.name, connection.connectionClass, optProps, connection.maxNumberOfConnections.intValue(), connection.minTimeout.intValue()*1000, connection.maxTimeout.intValue()*1000)
    }
}