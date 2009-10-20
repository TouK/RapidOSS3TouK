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
 * Created on Jan 17, 2008
 *
 */
package com.ifountain.core.connection;

import java.util.HashMap;
import java.util.Map;

public class ConnectionParam
{
    private String connectionType;
    private String connectionName;
    private String connectionClass;
    private int maxNumberOfConnectionsInPool;
    private int minTimeout;
    private int maxTimeout;
    private long connectionCheckerTimeout = 3600000l;
    private Map<String, Object> otherParams;
    
    public ConnectionParam(String type, String name, String connClass,
            Map<String, Object> otherParams)
    {
        this(type, name, connClass, otherParams, 1, 1000, 6000);
    }
    public ConnectionParam(String datasourceType, String datasourceName, String datasourceClass,
            Map<String, Object> otherParams, int maxNumberOfDatasourceInPool, int minTimeout, int maxTimeout)
    {
        this.minTimeout = minTimeout;
        this.maxTimeout = maxTimeout;
        this.maxNumberOfConnectionsInPool = maxNumberOfDatasourceInPool;
        this.connectionName = datasourceName;
        this.connectionType = datasourceType;
        this.connectionClass = datasourceClass;
        this.otherParams = otherParams;
    }

    public int getMinTimeout() {
        return minTimeout;
    }

    public void setMinTimeout(int minTimeout) {
        this.minTimeout = minTimeout;
    }

    public int getMaxTimeout() {
        return maxTimeout;
    }

    public long getConnectionCheckerTimeout() {
        return connectionCheckerTimeout;
    }

    public void setConnectionCheckerTimeout(long connectionCheckerTimeout) {
        this.connectionCheckerTimeout = connectionCheckerTimeout;
    }

    public void setMaxTimeout(int maxTimeout) {
        this.maxTimeout = maxTimeout;
    }

    public String getConnectionName()
    {
        return connectionName;
    }
    public void setConnectionName(String name)
    {
        this.connectionName = name;
    }    
    
    public String getConnectionType()
    {
        return connectionType;
    }
    public void setConnectionType(String type)
    {
        this.connectionType = type;
    }
    public String getConnectionClass()
    {
        return connectionClass;
    }
    public void setConnectionClass(String connClass)
    {
        this.connectionClass = connClass;
    }
    public Map<String, Object> getOtherParams()
    {
        return otherParams;
    }
    public void setOtherParams(Map<String, Object> otherParams)
    {
        this.otherParams = otherParams;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        try{
            Map newOtherParams = otherParams.getClass().newInstance();
            newOtherParams.putAll(otherParams);
            return new ConnectionParam(connectionType, connectionName, connectionClass, newOtherParams, maxNumberOfConnectionsInPool, minTimeout, maxTimeout);
        }
        catch(IllegalAccessException ex)
        {
            throw new RuntimeException(ex);
        }
        catch(InstantiationException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        return "ConnectionParam{" +
                "connectionType='" + connectionType + '\'' +
                ", connectionName='" + connectionName + '\'' +
                ", connectionClass='" + connectionClass + '\'' +
                ", maxNumberOfConnectionsInPool=" + maxNumberOfConnectionsInPool +
                ", minTimeout=" + minTimeout +
                ", maxTimeout=" + maxTimeout +
                ", otherParams=" + otherParams +
                '}';
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof ConnectionParam)
        {
            ConnectionParam other = (ConnectionParam) obj;
            return other.getConnectionName().equals(getConnectionName()) && other.getConnectionClass().equals(getConnectionClass())
            && other.getConnectionType().equals(getConnectionType()) && other.getOtherParams().equals(getOtherParams());
        }
        return super.equals(obj);
    }
    public int getMaxNumberOfConnectionsInPool()
    {
        return maxNumberOfConnectionsInPool;
    }
    public void setMaxNumberOfConnectionsInPool(int maxNumberOfConnectionsInPool)
    {
        this.maxNumberOfConnectionsInPool = maxNumberOfConnectionsInPool;
    }
    
}
