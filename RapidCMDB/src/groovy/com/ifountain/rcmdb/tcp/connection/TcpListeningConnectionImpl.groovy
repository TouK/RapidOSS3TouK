package com.ifountain.rcmdb.tcp.connection

import com.ifountain.core.connection.BaseConnection
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException

/**
* Created by Sezgin Kucukkaraaslan
* Date: Oct 27, 2010
* Time: 6:04:05 PM
*/
class TcpListeningConnectionImpl extends BaseConnection {
    public static final String HOST = "host";
    public static final String PORT = "port";
    private String host;
    private Long port;
    protected void connect() {
    }

    protected void disconnect() {
    }

    public void init(ConnectionParam param) {
        super.init(param);
        host = (String) checkParam(HOST);
        port = (Long) checkParam(PORT);
    }



    public boolean checkConnection() {
        return true;
    }

    public boolean isConnectionException(Throwable t) {
        return false;
    }

    protected Object checkParam(String parameterName) throws UndefinedConnectionParameterException {
        if (!params.getOtherParams().containsKey(parameterName)) {
            throw new UndefinedConnectionParameterException(parameterName);
        }
        return params.getOtherParams().get(parameterName);
    }

    public String getHost() {
        return host
    }
    public Long getPort() {
        return port;
    }

}