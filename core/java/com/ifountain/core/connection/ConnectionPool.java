/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
    private Exception poolConnectionException = null;
    private List borrowedObjects = new ArrayList();
    private String connectionName;
    private boolean willRunConnectionChecker = false;
    private Object connectionCheckerRunnerLock = new Object();
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
        setMaxIdle(maxNumberOfConnections);
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
    public synchronized void setMaxActive(int i) {
        super.setMaxActive(i);
        super.setMaxIdle(i);
    }
    public Object borrowObject() throws Exception {
        synchronized (connectionCheckerRunnerLock)
        {
            if(willRunConnectionChecker)
            {
                runConnectionChecker();
            }
        }
        if(!isPoolConnected)
        {
            if(!(poolConnectionException instanceof ConnectionException))
            {
                throw ConnectionException.noConnectionException(this.connectionName, poolConnectionException);
            }
            else
            {
                throw poolConnectionException;
            }
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
    public void markConnectionCheckerToRun() throws InterruptedException
    {
        willRunConnectionChecker = true;
    }
    private void runConnectionChecker() throws InterruptedException
    {
        connectionChecker.run();
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
        setPoolConnectionStatus(isConnected, null);
    }
    public void setPoolConnectionStatus(boolean isConnected, Exception e)
    {
        if(this.isPoolConnected != isConnected)
        {
            logger.warn("Changed connection status of pool "+connectionName+". IsConnected:"+isConnected);
        }
        isPoolConnected = isConnected;
        poolConnectionException = e;
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
        int numberOfThreads = getMaxActive();
        ExecutorService checkSingleConnectionService = Executors.newFixedThreadPool(numberOfThreads != 0? numberOfThreads:5);

        private long getConnectionCheckerTimeoutValue(int numberOfActiveConnections)
        {
            if(poolableObjectFactory.getConnectionParameter() == null) return 0;
            long invokeOperationResultWaitTime = 2*poolableObjectFactory.getConnectionParameter().getMaxTimeout()*numberOfActiveConnections;
            if(invokeOperationResultWaitTime == 0)
            {
                return poolableObjectFactory.getConnectionParameter().getConnectionCheckerTimeout();
            }
            return invokeOperationResultWaitTime;
        }
        public void run()
        {
            synchronized (connectionCheckerRunnerLock)
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
                        setPoolConnectionStatus(false, e);
                        if(logger.isDebugEnabled())
                        {
                            logger.debug("An exception occcurred while getting new connection from pool.", e);
                        }
                    }
                }
                if(!validConnections.isEmpty())
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
                        long invokeOperationResultWaitTime = getConnectionCheckerTimeoutValue(validConnections.size());

                        List<Future> features = null;
                        if(invokeOperationResultWaitTime != 0)
                        {
                            features = checkSingleConnectionService.invokeAll(tasks, invokeOperationResultWaitTime, TimeUnit.MILLISECONDS);
                        }
                        else
                        {
                            features = checkSingleConnectionService.invokeAll(tasks);                            
                        }
                        boolean willDisconnectPool = true;
                        for(int i=0; i < features.size(); i++)
                        {
                            try
                            {
                                if(!features.get(i).isCancelled() && ((Boolean)features.get(i).get()).booleanValue())
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
                willRunConnectionChecker = false;
            }
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
