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


public abstract class BaseAdapter implements Adapter
{
    protected String connectionName;
    protected long reconnectInterval = 0;
    protected Logger logger;

    public BaseAdapter() {
    }
    
    public BaseAdapter(String connConfigName, long reconnectInterval, Logger logger)
    {
        this.connectionName = connConfigName;
        this.reconnectInterval = reconnectInterval;
        this.logger = logger;
    }
    
    public abstract Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) throws Exception;
    
    public void executeAction(Action action) throws Exception
    {
        
        while(true)
        {
                IConnection conn;
                try
                {
                    conn = ConnectionManager.getConnection(connectionName);
                }
                catch (ConnectionException e)
                {
                    if(reconnectInterval > 0)
                    {
                        Thread.sleep(reconnectInterval);
                        continue;
                    }
                    else
                    {
                        throw e;
                    }
                }
                try
                {
                    action.execute(conn);
                    break;
                    
                }
                catch (Exception e) {
                    
                    if(conn.isConnected())
                    {
                        throw e;
                    }
                    else
                    {
                        if(reconnectInterval > 0)
                        {
                            Thread.sleep(reconnectInterval);
                        }
                        else
                        {
                            throw new ConnectionException(e);
                        }
                    }
                }
                finally
                {
                    ConnectionManager.releaseConnection(conn);
                }
            
            
        }
        
    }
    
    @Override
    public void setConnectionName(String connectionName)
    {
    	this.connectionName = connectionName;
    }
    
    @Override
    public void setReconnectInterval(int reconnectInterval)
    {
    	this.reconnectInterval = reconnectInterval;
    }
    
    @Override
    public void setLogger(Logger logger)
    {
    	this.logger = logger;
    }

}
