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
        assertEquals((int)(totalTimeout/numberOfConns*2), strategy.calculateNewTimeout(5, connections));
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
        assertEquals((int)(totalTimeout/numberOfConns/2), strategy.calculateNewTimeout(5, connections));
    }

    public void testCalculateTimeoutWithNoConnections()  throws Exception
    {
        DefaultTimeoutStrategy strategy = new DefaultTimeoutStrategy();
        long oldTimeout = 10;
        assertEquals(oldTimeout*2, strategy.calculateNewTimeout(oldTimeout, new ArrayList()));
    }


}
