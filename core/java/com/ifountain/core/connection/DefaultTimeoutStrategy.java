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
        if(connections.isEmpty()) return true;
        double numberOfDiconnectedRatio = numberOfDisconnectedConnections/connections.size();
        return numberOfDiconnectedRatio >= INCREASE_LIMIT || numberOfDiconnectedRatio <= DECREASE_LIMIT;
    }

    public long calculateNewTimeout(long oldTimeout, List<IConnection> connections) {
        double numberOfDisconnectedConnections = 0;
        long totalTimeout = oldTimeout;
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
            long newTimeoutInterval = totalTimeout/(connections.size()+1)*2;
            if(logger.isDebugEnabled())
            logger.debug("Connection status is :(. Changing timeout interval from " + oldTimeout + " to "+newTimeoutInterval);
            return newTimeoutInterval;
        }
        else
        {
            long newTimeoutInterval = (totalTimeout/(connections.size()+1))/2;
            if(logger.isDebugEnabled())
            logger.debug("Connection status is :).Changing timeout interval from " + oldTimeout + " to "+newTimeoutInterval);
            return newTimeoutInterval;
        }
    }
}
