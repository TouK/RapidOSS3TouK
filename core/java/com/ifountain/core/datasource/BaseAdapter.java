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
 * Created on Jan 21, 2008
 *
 */
package com.ifountain.core.datasource;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ifountain.core.connection.ConnectionManager;
import com.ifountain.core.connection.IConnection;
import com.ifountain.core.connection.exception.ConnectionException;
import com.ifountain.core.connection.exception.ConnectionInitializationException;
import com.ifountain.core.connection.exception.UndefinedConnectionException;
import com.ifountain.core.connection.exception.ConnectionPoolException;


public abstract class BaseAdapter implements Adapter {
    protected String connectionName;
    protected long reconnectInterval = 0;
    protected Logger logger;
    private boolean isDestroyed = false;

    public BaseAdapter() {
    }

    public BaseAdapter(String connConfigName, long reconnectInterval, Logger logger) {
        this.connectionName = connConfigName;
        this.reconnectInterval = reconnectInterval;
        this.logger = logger;
    }

    public abstract Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) throws Exception;

    public void executeAction(Action action) throws Exception {

        boolean isPrintedConnectionExceptionOnce = false;
        while (!isDestroyed) {
            IConnection conn;
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Getting connection " + connectionName + " from pool");
                }
                conn = getConnection();
            }
            catch (ConnectionException e) {
                if (reconnectInterval > 0) {
                    if (!isPrintedConnectionExceptionOnce) {
                        isPrintedConnectionExceptionOnce = true;
                        logger.warn("Exception occurred while getting connection " + connectionName + " from pool. Trying to reconnect.", e);
                    }
                    Thread.sleep(reconnectInterval);
                    continue;
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Exception occurred while getting connection " + connectionName + " from pool.", e);
                    }
                    throw e;
                }
            }
            boolean shouldWait = false;
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Executing action with " + connectionName);
                }
                action.execute(conn);
                if (logger.isDebugEnabled()) {
                    logger.debug("Executed action with " + connectionName);
                }
                break;

            }
            catch (Exception e) {
                boolean isConnectionException = conn.isConnectionException(e);
                if (!isConnectionException && conn.checkConnection()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Exception occurred while executing action with connection " + connectionName, e);
                    }
                    throw e;
                } else {
                    if (isConnectionException) {
                        conn.invalidate();
                    }
                    if (reconnectInterval > 0) {
                        if (!isPrintedConnectionExceptionOnce) {
                            isPrintedConnectionExceptionOnce = true;
                            logger.warn("Exception occurred while executing action " + connectionName + ". Trying to reconnect.", e);
                        }
                        shouldWait = true;
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Exception occurred while executing action with connection " + connectionName, e);
                        }
                        throw new ConnectionException(e);
                    }
                }
            }
            finally {
                releaseConnection(conn);
                if (logger.isDebugEnabled()) {
                    logger.debug("Released connection " + connectionName);
                }
                if(shouldWait){
                    Thread.sleep(reconnectInterval);
                }
            }
        }
    }


    protected IConnection getConnection() throws ConnectionInitializationException, UndefinedConnectionException, ConnectionPoolException, ConnectionException {
        return ConnectionManager.getConnection(connectionName);
    }

    protected void releaseConnection(IConnection connection) throws ConnectionInitializationException, ConnectionPoolException, ConnectionException {
        ConnectionManager.releaseConnection(connection);
    }

    @Override
    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    @Override
    public void setReconnectInterval(int reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    public long getReconnectInterval() {
        return reconnectInterval;
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void destroy() {
        isDestroyed = true;
    }

}
