package com.ifountain.core.connection;

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

    public long calculateNewTimeout(List<IConnection> connections) {
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
        if(numberOfDisconnectedConnections/connections.size() >= INCREASE_LIMIT)
        {
            return totalTimeout/connections.size()*2;
        }
        else
        {
            return totalTimeout/connections.size()/2;
        }
    }
}
