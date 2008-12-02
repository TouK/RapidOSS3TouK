package com.ifountain.core.connection;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 1, 2008
 * Time: 3:09:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultTimeoutStrategy implements TimeoutStrategy{
    Logger logger = Logger.getLogger(DefaultTimeoutStrategy.class);
    public static final double INCREASE_LIMIT = 0.4;
    public static final double DECREASE_LIMIT = 0.1;
    public boolean shouldRecalculate(List<IConnection> connections) {
        double numberOfDisconnectedConnections = 0;
        for(Iterator it=connections.iterator(); it.hasNext();)
        {
            IConnection conn = (IConnection)it.next();
            if(!conn.isConnected())
            {
                numberOfDisconnectedConnections++;
            }
        }
        double numberOfDiconnectedRatio = numberOfDisconnectedConnections/connections.size();
        return numberOfDiconnectedRatio >= INCREASE_LIMIT || numberOfDiconnectedRatio <= DECREASE_LIMIT;
    }

    public long calculateNewTimeout(long oldTimeout, List<IConnection> connections) {
        double numberOfDisconnectedConnections = 0;
        int totalTimeout = 0;
        for(Iterator it=connections.iterator(); it.hasNext();)
        {
            IConnection conn = (IConnection)it.next();
            totalTimeout += conn.getTimeout();
            if(!conn.isConnected())
            {
                numberOfDisconnectedConnections++;
            }
        }
        if(connections.size() == 0)
        {
            long newTimeoutInterval = oldTimeout*2;
            logger.debug("No connections defined increasing timeout interval from " + oldTimeout + " to "+newTimeoutInterval);
            return newTimeoutInterval;    
        }
        else if(numberOfDisconnectedConnections/connections.size() >= INCREASE_LIMIT)
        {
            long newTimeoutInterval = totalTimeout/connections.size()*2;
            if(logger.isDebugEnabled())
            logger.debug("Increasing timeout interval from " + oldTimeout + " to "+newTimeoutInterval);
            return newTimeoutInterval;
        }
        else
        {
            long newTimeoutInterval = totalTimeout/connections.size()/2;
            if(logger.isDebugEnabled())
            logger.debug("Decreasing timeout interval from " + oldTimeout + " to "+newTimeoutInterval);
            return newTimeoutInterval;
        }
    }
}
