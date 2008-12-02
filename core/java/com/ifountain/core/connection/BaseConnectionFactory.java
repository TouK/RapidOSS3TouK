package com.ifountain.core.connection;

import org.apache.commons.pool.PoolableObjectFactory;

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
    private List connections = new ArrayList();
    private TimeoutManager timeoutManager;

    protected BaseConnectionFactory(Class timeoutStrategyClass)
    {
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
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

        public void calculateTimeout(List<IConnection> connections)
        {
            if(timeoutStrategy != null)
            {
                if(timeoutStrategy.shouldRecalculate(connections))
                {
                    timeout = timeoutStrategy.calculateNewTimeout(connections);
                }
            }
        }
    }

    protected abstract IConnection _makeObject(long timeout) throws Exception;
    protected abstract void _destroyObject(IConnection o) throws Exception;
}
