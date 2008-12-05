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

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 28, 2008
 * Time: 2:53:19 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseConnectionFactory implements PoolableObjectFactory
{
    Logger logger = Logger.getLogger(BaseConnectionFactory.class);
    private List connections = new ArrayList();
    private TimeoutManager timeoutManager;
    protected String name;

    protected BaseConnectionFactory(String name, Class timeoutStrategyClass)
    {
        this.name = name;
        this.timeoutManager = new TimeoutManager(timeoutStrategyClass);
    }

    public Object makeObject() throws Exception
    {
        IConnection conn = _makeObject(timeoutManager.timeout);
        connections.add(conn);
        return conn;
    }

    public void destroyObject(Object o) throws Exception {
        connections.remove(o);
        _destroyObject((IConnection)o);
    }

    public List getAllConnections()
    {
        return new ArrayList(connections);
    }

    public void calculateTimeout()
    {
        timeoutManager.calculateTimeout(getAllConnections());    
    }

    private class TimeoutManager
    {
        long timeout;
        TimeoutStrategy timeoutStrategy;
        public TimeoutManager(Class timeoutStrategyClass)
        {
            if(timeoutStrategyClass != null)
            {
                try
                {
                    this.timeoutStrategy = (TimeoutStrategy)timeoutStrategyClass.newInstance();
                    logger.info("Initialized timeout manager with timeout strategy class "+ timeoutStrategyClass.getName() + " for connection "+ name);
                }
                catch(Exception e)
                {
                    logger.warn("Could not initialized timeout manager with timeout strategy class "+ timeoutStrategyClass.getName() + " for connection "+ name, e);
                    throw new RuntimeException(e);
                }
            }
            else
            {
                logger.info("No timeout strategy defined  for connection "+ name);
            }
        }

        public void calculateTimeout(List<IConnection> connections)
        {
            if(timeoutStrategy != null)
            {
                if(timeoutStrategy.shouldRecalculate(connections))
                {
                    timeout = timeoutStrategy.calculateNewTimeout(timeout, connections);
                    logger.debug("New timeout interval for connection"+ name + " is "+ timeout);
                }
                else
                {
                    if(logger.isDebugEnabled())
                    {
                        logger.debug("No need to recalculate timeout interval for connection "+ name);
                    }
                }
            }
        }
    }

    protected abstract IConnection _makeObject(long timeout) throws Exception;
    protected abstract void _destroyObject(IConnection o) throws Exception;
}
