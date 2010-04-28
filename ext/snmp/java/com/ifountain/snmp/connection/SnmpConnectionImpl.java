package com.ifountain.snmp.connection;

import com.ifountain.core.connection.BaseConnection;
import com.ifountain.core.connection.ConnectionParam;
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
 * Date: Aug 11, 2008
 * Time: 6:05:47 PM
 */
public class SnmpConnectionImpl extends BaseConnection{

    public static final String HOST = "host";
    public static final String PORT = "port";
    private String host;
    private Long port;
    protected void connect() throws Exception {
    }

    public boolean isConnectionException(Throwable t)
    {
        return false;
    }

    protected void disconnect() {
    }

    public void init(ConnectionParam param) throws Exception {
         this.params = param;
         host = (String)checkParam(HOST);       
         port = (Long)checkParam(PORT);       
    }

    public boolean checkConnection() {
        return true;
    }

    public String getHost() {
        return host;
    }

    public Long getPort() {
        return port;
    }
}
