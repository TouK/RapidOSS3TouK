package com.ifountain.core.connection;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.*;

import com.ifountain.core.connection.exception.ConnectionException;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 25, 2008
 * Time: 9:33:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionPool extends GenericObjectPool
{
    Logger logger = Logger.getLogger(ConnectionPool.class);
    private boolean isPoolConnected = true;
    private List borrowedObjects = new ArrayList();
    private String connectionName;
    private long connectionCheckerThreadInterval = 0;
    private Object borrowedConnectionLock = new Object();
    private ScheduledExecutorService connectionCheckerService = Executors.newSingleThreadScheduledExecutor();
    private ConnectionChecker connectionChecker;
    private BaseConnectionFactory poolableObjectFactory;

    public ConnectionPool(String connectionName, BaseConnectionFactory poolableObjectFactory, int maxNumberOfConnections) {
        this(connectionName, poolableObjectFactory, maxNumberOfConnections, 10000);
    }
    public ConnectionPool(String connectionName, BaseConnectionFactory poolableObjectFactory, int maxNumberOfConnections, long connectionCheckerInterval) {
        super(poolableObjectFactory, maxNumberOfConnections);
        logger.info("Initializing connection pool "+connectionName);
        this.poolableObjectFactory = poolableObjectFactory;
        this.connectionName = connectionName;
        this.connectionCheckerThreadInterval = connectionCheckerInterval;
        connectionChecker = new ConnectionChecker();
        logger.info("Will check connections per "+connectionCheckerInterval+" msecs.");
        connectionCheckerService.scheduleWithFixedDelay(connectionChecker, connectionCheckerInterval, connectionCheckerInterval, TimeUnit.MILLISECONDS);
        setTestOnBorrow(true);
        setTestOnReturn(true);

    }

    public Object borrowObject() throws Exception {
        if(!isPoolConnected)
        {
            throw ConnectionException.noConnectionException(this.connectionName);
        }
        return _borrowObject();
    }

    private Object _borrowObject() throws Exception {
        if(logger.isDebugEnabled())
        {
            logger.debug("Borrowing connection from "+connectionName + " pool");
        }
        try
        {
            Object borrowedConnection = super.borrowObject();
            synchronized (borrowedConnectionLock)
            {
                borrowedObjects.add(borrowedConnection);
            }
            if(logger.isDebugEnabled())
            {
                logger.debug("Borrowed connection from "+connectionName + " pool");
            }
            return borrowedConnection;
        }
        catch(Exception e)
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("Exception occcurred while getting connection from "+connectionName + " pool", e);
            }
            throw e;
        }
    }

    public List getBorrowedConnections(){
        synchronized (borrowedConnectionLock)
        {
            return new ArrayList(borrowedObjects);
        }
    }

    public void returnObject(Object connection) throws Exception {
        if(logger.isDebugEnabled())
        {
            logger.debug("Returning connection to "+connectionName + " pool");
        }
        synchronized (borrowedConnectionLock)
        {
            borrowedObjects.remove(connection);
        }
        try
        {
            super.returnObject(connection);
            if(logger.isDebugEnabled())
            {
                logger.debug("Returned connection to "+connectionName + " pool");
            }
        }
        catch(Exception e)
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("Exception occcurred while returning connection to "+connectionName + " pool", e);
            }
            throw e;
        }
    }

    public void setPoolConnectionStatus(boolean isConnected)
    {
        if(this.isPoolConnected != isConnected)
        {
            logger.warn("Changed connection status of pool "+connectionName+". IsConnected:"+isConnected);
        }
        isPoolConnected = isConnected;
    }

    public boolean isPoolConnected() {
        return isPoolConnected;
    }

    public void close() throws Exception {
        logger.info("Closing connection pool "+connectionName);
        connectionCheckerService.shutdown();
        logger.info("Shutting down connection checker service of pool "+connectionName);
        connectionCheckerService.awaitTermination(30000, TimeUnit.MILLISECONDS);
        super.close();
        logger.info("Pool "+connectionName + " is closed");
    }

    private class ConnectionChecker implements Runnable
    {
        ExecutorService checkSingleConnectionService = Executors.newFixedThreadPool(5);
        public void run()
        {
            logger.info("Checking status of connections in pool "+connectionName);
            List allConnections = poolableObjectFactory.getAllConnections();
            List validConnections = new ArrayList();
            for(Iterator it=allConnections.iterator(); it.hasNext();)
            {
                IConnection conn = (IConnection)it.next();
                if(poolableObjectFactory.validateObject(conn))
                {
                    validConnections.add(conn);
                }
            }
            if(validConnections.isEmpty())
            {
                try
                {
                    if(logger.isDebugEnabled())
                    {
                        logger.debug("Currently no connection exists in pool "+connectionName+". Trying to get one");
                    }
                    IConnection connection = (IConnection)_borrowObject();
                    validConnections.add(connection);
                    returnObject(connection);
                }
                catch(Exception e)
                {
                    if(logger.isDebugEnabled())
                    {
                        logger.debug("An exception occcurred while getting new connection from pool.", e);
                    }
                }
            }
            if(validConnections.isEmpty())
            {
                setPoolConnectionStatus(false);
            }
            else
            {
                List tasks = new ArrayList();
                logger.info("Will check "+validConnections.size() +" number of connections in pool "+connectionName);
                for(Iterator it = validConnections.iterator();it.hasNext();)
                {
                    IConnection conn = (IConnection)it.next();
                    tasks.add(new CheckSingleConnection(conn));
                }
                try
                {
                    List<Future> features = checkSingleConnectionService.invokeAll(tasks);
                    boolean willDisconnectPool = true;
                    for(int i=0; i < features.size(); i++)
                    {
                        try
                        {
                            if(((Boolean)features.get(i).get()).booleanValue())
                            {
                                willDisconnectPool = false;
                                break;
                            }
                        }
                        catch(ExecutionException ex)
                        {
                            if(logger.isDebugEnabled())
                            {
                                logger.debug("An exception occcurred while checking connection status of "+connectionName, ex);
                            }
                        }
                    }
                    setPoolConnectionStatus(!willDisconnectPool);
                }catch(InterruptedException ex)
                {
                    logger.warn("Connection checker tasks of "+connectionName+" pool are interrupted.");
                }
            }

            poolableObjectFactory.calculateTimeout();
            logger.info("Checked status of connections in pool "+connectionName);
        }
    }

    private class CheckSingleConnection implements Callable
    {
        IConnection connection;

        public CheckSingleConnection(IConnection connection) {
            this.connection = connection;
        }

        public Object call()  throws Exception{
            if(!connection.checkConnection())
            {
                connection._disconnect();
                return false;
            }
            return true;
        }
    }
}
