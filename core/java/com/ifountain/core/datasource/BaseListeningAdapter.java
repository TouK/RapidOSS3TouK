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
/**
 * Created on Mar 11, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.core.datasource;

import java.util.Observable;
import java.util.Observer;
import java.util.Map;
import java.util.List;

import org.apache.log4j.Logger;

import com.ifountain.core.connection.ConnectionManager;
import com.ifountain.core.connection.IConnection;
import com.ifountain.core.connection.exception.ConnectionException;
import com.ifountain.core.connection.exception.ConnectionInitializationException;
import com.ifountain.core.connection.exception.ConnectionPoolException;
import com.ifountain.core.connection.exception.UndefinedConnectionException;

public abstract class BaseListeningAdapter extends Observable implements Observer {

    protected String connectionName;
    protected long reconnectInterval = 0;
    protected Logger logger;
    protected boolean isSubscribed = false;
    protected boolean stoppedByUser = false;
    private Object subscriptionLock = new Object();
    private Object isUpdateProcessingLock = new Object();
    private Object updateWaitLock = new Object();
    protected ActionExecutor executorAdapter;
    private boolean isUpdateProcessing = false;

    public BaseListeningAdapter(String connectionName, long reconnectInterval, Logger logger) {
        this.connectionName = connectionName;
        this.reconnectInterval = reconnectInterval;
        this.logger = logger;
    }

    private void setIsUpdateProcessing(boolean isUpdateProcessing) {
        synchronized (isUpdateProcessingLock) {
            this.isUpdateProcessing = isUpdateProcessing;
        }
    }

    private boolean isUpdateProcessing() {
        synchronized (isUpdateProcessingLock) {
            return this.isUpdateProcessing;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        setIsUpdateProcessing(true);
        Object objectToDelegate = _update(o, arg);
        sendDataToObservers(objectToDelegate);
        synchronized (updateWaitLock) {
            setIsUpdateProcessing(false);
            updateWaitLock.notifyAll();
        }
        //updateLock.notifyAll();
    }

    public void sendDataToObservers(Object data) {
        if (data != null) {
            setChanged();
            notifyObservers(data);

        }
    }

    public boolean isConversionEnabledForUpdate()
    {
        return true;
    }

    public abstract Object _update(Observable o, Object arg);

    protected abstract void _subscribe() throws Exception;

    protected abstract void _unsubscribe();

    private void subscribeInternally() throws Exception {
        if (!isSubscribed()) {
            logger.info("Subscribing to connection with name " + connectionName);
            executorAdapter = new ActionExecutor(this, connectionName, reconnectInterval, logger);
            if (!stoppedByUser) {
                executorAdapter.executeAction(new SubscribeAction());
            } else {
                logger.info("Stopped by user cannot subscribe to connection " + connectionName);
                throw new Exception("Stopped by user cannot subscribe");
            }

        }
    }

    public void subscribe() throws Exception {
        synchronized (subscriptionLock) {
            this.stoppedByUser = false;
        }
        subscribeInternally();
    }

    public void unsubscribe() throws Exception {
        synchronized (subscriptionLock) {
            this.stoppedByUser = true;
            unsubscribeInternally();
        }
    }

    private synchronized void releaseConnection() throws ConnectionInitializationException, ConnectionPoolException, ConnectionException {
        executorAdapter.releaseConnection(getConnection());
    }

    public void kill() {
        synchronized (updateWaitLock) {
            updateWaitLock.notifyAll();
        }
    }

    private void unsubscribeInternally() throws Exception {
        try {
            if (isSubscribed()) {
                try {
                    _unsubscribe();
                }
                finally {
                    isSubscribed = false;
                    synchronized (updateWaitLock) {
                        if (isUpdateProcessing()) {
                            updateWaitLock.wait();
                        }
                    }
                    releaseConnection();
                }
            }
        }
        finally {
            if (executorAdapter != null) {
                executorAdapter.destroy();
            }
        }

    }

    protected void disconnectDetected() throws Exception {
        unsubscribeInternally();
        subscribeInternally();
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public long getReconnectInterval() {
        return reconnectInterval;
    }

    public void setReconnectInterval(long reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    protected IConnection getConnection() {
        try {
            return executorAdapter.getConnection();
        } catch (Exception e) {
            return null;
        }
    }

    protected class ActionExecutor extends BaseAdapter {
        IConnection connection;
        BaseListeningAdapter listeningAdapter;

        public ActionExecutor(BaseListeningAdapter listeningAdapter, String connConfigName, long reconnectInterval, Logger logger) {
            super(connConfigName, reconnectInterval, logger);
            this.listeningAdapter = listeningAdapter;
        }

        public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) throws Exception {
            return null;
        }

        protected IConnection getConnection() throws ConnectionInitializationException, UndefinedConnectionException, ConnectionPoolException, ConnectionException {
            if (connection == null) {
                connection = super.getConnection();
            }
            return connection;
        }

        protected void releaseConnection(IConnection connection) throws ConnectionInitializationException, ConnectionPoolException, ConnectionException {
            if (!isSubscribed()) {
                super.releaseConnection(connection);
                connection = null;
            }
        }
    }

    class SubscribeAction implements Action {

        public void execute(IConnection conn) throws Exception {
            synchronized (subscriptionLock) {
                _subscribe();
                isSubscribed = true;
            }
        }
    }


}
