package com.ifountain.core.connection;

import com.ifountain.core.connection.exception.UndefinedConnectionParameterException;

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
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Apr 11, 2008
 * Time: 9:58:10 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseConnection implements IConnection {
    protected boolean isConnected = false;
    private boolean isValid = true;
    protected ConnectionParam params;
    protected long minTimeout;
    protected long maxTimeout;
    protected long timeout;

    public final void _connect() throws Exception {
        connect();
        isConnected = true;
    }

    public boolean isValid() {
        return isValid;
    }

    public void invalidate() {
        this.isValid = false;
    }

    public final void _disconnect() {
        disconnect();
        isConnected = false;
    }

    public void init(ConnectionParam param) throws Exception {
        this.params = param;
        this.minTimeout = params.getMinTimeout();
        this.maxTimeout = params.getMaxTimeout();
        this.timeout = minTimeout;
    }

    public ConnectionParam getParameters() {
        return params;
    }

    public long getMinTimeout() {
        return minTimeout;
    }

    public void setMinTimeout(long minTimeout) {
        this.minTimeout = minTimeout;
    }

    public long getMaxTimeout() {
        return maxTimeout;
    }

    public void setMaxTimeout(long maxTimeout) {
        this.maxTimeout = maxTimeout;
    }

    public void setTimeout(long timeout) {
        if (timeout < minTimeout) {
            this.timeout = this.minTimeout;
        } else if (timeout > maxTimeout) {
            this.timeout = maxTimeout;
        } else {
            this.timeout = timeout;
        }
    }

    public final boolean isConnected() {
        return isConnected;
    }

    public long getTimeout() {
        return timeout;
    }

    protected Object checkParam(String parameterName) throws UndefinedConnectionParameterException {
        if (!params.getOtherParams().containsKey(parameterName)) {
            throw new UndefinedConnectionParameterException(parameterName);
        }
        return params.getOtherParams().get(parameterName);
    }

    protected abstract void connect() throws Exception;

    protected abstract void disconnect();

}
