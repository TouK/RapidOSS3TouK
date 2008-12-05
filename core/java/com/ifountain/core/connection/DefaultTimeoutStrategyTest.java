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

import com.ifountain.core.test.util.RapidCoreTestCase;
import com.ifountain.core.connection.mocks.MockConnectionImpl;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 1, 2008
 * Time: 3:10:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultTimeoutStrategyTest extends RapidCoreTestCase
{
    public void testShouldRecalculate()
    {
        DefaultTimeoutStrategy strategy = new DefaultTimeoutStrategy();
        List connections = new ArrayList();
        MockConnectionImpl conn = new MockConnectionImpl();
        conn._disconnect();
        connections.add(conn);

        conn = new MockConnectionImpl();
        conn._disconnect();
        connections.add(conn);

        assertTrue(strategy.shouldRecalculate(connections));
    }

    public void testShouldRecalculateReturnsFalseIfNumberOfDisconnectedObjectsIsBelowTheLimit() throws Exception
    {
        DefaultTimeoutStrategy strategy = new DefaultTimeoutStrategy();
        List connections = new ArrayList();
        int numberOfConns = 20;
        for(int i=0; i < numberOfConns; i++)
        {
            MockConnectionImpl conn = new MockConnectionImpl();
            connections.add(conn);
            if(i >= numberOfConns* DefaultTimeoutStrategy.INCREASE_LIMIT -1)
            {
                conn._connect();
            }
        }
        assertFalse(strategy.shouldRecalculate(connections));
    }

    public void testShouldRecalculateReturnsTrueIfNoConnectionsDefined() throws Exception
    {
        DefaultTimeoutStrategy strategy = new DefaultTimeoutStrategy();
        List connections = new ArrayList();
        assertTrue(strategy.shouldRecalculate(connections));
    }

    public void testShouldRecalculateReturnsTrueIfNumberOfDisconnectedConnectionsIsLessThanDecreaseLimit() throws Exception
    {
        DefaultTimeoutStrategy strategy = new DefaultTimeoutStrategy();
        List connections = new ArrayList();
        int numberOfConns = 20;
        for(int i=0; i < numberOfConns; i++)
        {
            MockConnectionImpl conn = new MockConnectionImpl();
            connections.add(conn);
            if(i >= numberOfConns* DefaultTimeoutStrategy.DECREASE_LIMIT -1)
            {
                conn._connect();
            }
        }
        assertTrue(strategy.shouldRecalculate(connections));
    }

    public void testCalculateTimeoutWithNumberOfDisconnectedExceedsIncreaseLimit()  throws Exception
    {
        DefaultTimeoutStrategy strategy = new DefaultTimeoutStrategy();
        List connections = new ArrayList();
        int numberOfConns = 20;
        int totalTimeout = 0;
        for(int i=0; i < numberOfConns; i++)
        {
            totalTimeout+=i;
            MockConnectionImpl conn = new MockConnectionImpl();
            conn.setMaxTimeout(10000);
            conn.setMinTimeout(0);
            conn.setTimeout(i);
            connections.add(conn);
        }
        long currentTimeout = 500;
        assertEquals((int)((totalTimeout+currentTimeout)/(numberOfConns+1)*2), strategy.calculateNewTimeout(currentTimeout, connections));
    }

    public void testCalculateTimeoutWithNumberOfDisconnectedIsLessThanDecreaseLimit()  throws Exception
    {
        DefaultTimeoutStrategy strategy = new DefaultTimeoutStrategy();
        List connections = new ArrayList();
        int numberOfConns = 20;
        int totalTimeout = 0;
        for(int i=0; i < numberOfConns; i++)
        {
            totalTimeout+=i;
            MockConnectionImpl conn = new MockConnectionImpl();
            conn.setMaxTimeout(10000);
            conn.setMinTimeout(0);
            conn.setTimeout(i);
            if(i >= numberOfConns* DefaultTimeoutStrategy.DECREASE_LIMIT -1)
            {
                conn._connect();
            }
            connections.add(conn);
        }
        long currentTimeout = 500;
        assertEquals((int)((totalTimeout+currentTimeout)/(numberOfConns+1)/2), strategy.calculateNewTimeout(currentTimeout, connections));
    }

    public void testCalculateTimeoutWithNoConnections()  throws Exception
    {
        DefaultTimeoutStrategy strategy = new DefaultTimeoutStrategy();
        long oldTimeout = 10;
        assertEquals(oldTimeout*2, strategy.calculateNewTimeout(oldTimeout, new ArrayList()));
    }


}
