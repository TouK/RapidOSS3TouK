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

import org.apache.log4j.Logger;

import com.ifountain.core.connection.ConnectionManager;
import com.ifountain.core.connection.IConnection;
import com.ifountain.core.connection.exception.ConnectionException;

public abstract class BaseListeningAdapter extends Observable implements Observer {

    protected String connectionName;
    protected long reconnectInterval = 0;
    protected Logger logger;
    protected IConnection connection;
    protected boolean isSubscribed = false;

    public BaseListeningAdapter(String connectionName, long reconnectInterval, Logger logger) {
        this.connectionName = connectionName;
        this.reconnectInterval = reconnectInterval;
        this.logger = logger;
    }

    @Override
    public void update(Observable o, Object arg) {
        Object objectToDelegate = _update(o, arg);
        sendDataToObservers(objectToDelegate);
    }

    public void sendDataToObservers(Object data) {
        if (data != null) {
            setChanged();
            notifyObservers(data);
        }
    }

    public abstract Object _update(Observable o, Object arg);

    protected abstract void _subscribe() throws Exception;

    protected abstract void _unsubscribe();

    public void subscribe() throws Exception {
        if (!isSubscribed()) {
            while (true) {
                try {
                    connection = ConnectionManager.getConnection(connectionName);
                }
                catch (ConnectionException e) {
                    if (reconnectInterval > 0) {
                        Thread.sleep(reconnectInterval);
                        continue;
                    } else {
                        throw e;
                    }
                }
                try {
                    _subscribe();
                    isSubscribed = true;
                    break;

                }
                catch (Exception e) {
                    if (connection.isConnected()) {
                        throw e;
                    } else {
                        connection.setConnectedOnce(false);
                        if (reconnectInterval > 0) {
                            Thread.sleep(reconnectInterval);
                        } else {
                            throw new ConnectionException(e);
                        }
                    }
                }
                finally {
                    if (!isSubscribed()) {
                        ConnectionManager.releaseConnection(connection);
                        connection = null;
                    }
                }
            }
        }
    }

    public void unsubscribe() throws Exception {
        if (isSubscribed()) {
            _unsubscribe();
            isSubscribed = false;
            if (connection != null) {
                ConnectionManager.releaseConnection(connection);
                connection = null;
            }
        }
    }

    protected void disconnectDetected() throws Exception {
        unsubscribe();
        subscribe();
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
        return connection;
    }


}
