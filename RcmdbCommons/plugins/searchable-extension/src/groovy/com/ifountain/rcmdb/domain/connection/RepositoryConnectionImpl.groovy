package com.ifountain.rcmdb.domain.connection

import com.ifountain.core.connection.BaseConnection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 12, 2009
* Time: 4:44:24 PM
*/
class RepositoryConnectionImpl extends BaseConnection {

    protected void connect() {
    }

    protected void disconnect() {
    }

    public boolean isConnectionException(Throwable t) {
        return false;
    }

    public boolean checkConnection() {
        return true;
    }

}