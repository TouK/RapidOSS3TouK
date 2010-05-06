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
import com.ifountain.core.connection.exception.ConnectionException;
import com.ifountain.core.datasource.mocks.MockConnectionParameterSupplierImpl;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 25, 2008
 * Time: 9:36:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConncectionPoolTest extends RapidCoreTestCase
{
    ConnectionPool pool;
    public void setUp() throws Exception {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        MockConnection.isConnected = true;
        MockConnection.isValid = true;
        MockConnection.checkConnectionCallCount = 0;
        MockConnection.disconnectCalledFor.clear();
        MockTimeoutStrategy.connectionParameterList.clear();
    }

    public void tearDown() throws Exception {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
        pool.close();
        MockConnection.isConnected = true;
        MockConnection.isValid = true;
        MockConnection.checkConnectionCallCount = 0;
        MockConnection.disconnectCalledFor.clear();
        MockTimeoutStrategy.connectionParameterList.clear();

    }
    //TODO: Try to implement a case to test synchronization , when synchronization is off it should fail.
    public void testGetBarrowedObjects() throws Exception
    {
        MockPoolableObjectFactory fact = new MockPoolableObjectFactory(null);
        pool = new ConnectionPool("con1", fact, 10);
        assertEquals(0, pool.getBorrowedConnections().size());

        Object borrowedObject1 = pool.borrowObject();
        assertEquals(1, pool.getBorrowedConnections().size());
        assertTrue(pool.getBorrowedConnections().contains(borrowedObject1));

        Object borrowedObject2 = pool.borrowObject();
        assertEquals(2, pool.getBorrowedConnections().size());
        assertTrue(pool.getBorrowedConnections().contains(borrowedObject1));
        assertTrue(pool.getBorrowedConnections().contains(borrowedObject2));

        pool.returnObject(borrowedObject1);
        assertEquals(1, pool.getBorrowedConnections().size());
        assertFalse(pool.getBorrowedConnections().contains(borrowedObject1));
        assertTrue(pool.getBorrowedConnections().contains(borrowedObject2));

        pool.returnObject(borrowedObject2);
        assertEquals(0, pool.getBorrowedConnections().size());
        assertFalse(pool.getBorrowedConnections().contains(borrowedObject1));
        assertFalse(pool.getBorrowedConnections().contains(borrowedObject2));
    }

    public void testReturningObjectMultipleTimesDoesNotGenerateException() throws Exception
    {
        MockPoolableObjectFactory fact = new MockPoolableObjectFactory(null);
        pool = new ConnectionPool("con1", fact, 10);
        assertEquals(0, pool.getBorrowedConnections().size());

        Object borrowedObject1 = pool.borrowObject();
        assertEquals(1, pool.getBorrowedConnections().size());
        assertTrue(pool.getBorrowedConnections().contains(borrowedObject1));

        try{
            pool.returnObject(borrowedObject1);
        }
        catch(Exception e){
            fail("Shoult not throw Exception");
        }
        assertEquals(0, pool.getBorrowedConnections().size());
        assertFalse(pool.getBorrowedConnections().contains(borrowedObject1));

        try{
            pool.returnObject(borrowedObject1);
        }
        catch(Exception e){
            fail("Shoult not throw Exception");
        }
        assertEquals(0, pool.getBorrowedConnections().size());
        assertFalse(pool.getBorrowedConnections().contains(borrowedObject1));

        try{
            pool.returnObject(borrowedObject1);
        }
        catch(Exception e){
            fail("Shoult not throw Exception");
        }
        assertEquals(0, pool.getBorrowedConnections().size());
        assertFalse(pool.getBorrowedConnections().contains(borrowedObject1));
        
    }

    public void testReturningAnUnexistingObjectToPoolDoesNotGenerateException() throws Exception
    {
        MockPoolableObjectFactory fact = new MockPoolableObjectFactory(null);
        pool = new ConnectionPool("con1", fact, 10);
        assertEquals(0, pool.getBorrowedConnections().size());

        try{
            pool.returnObject(new Object());
        }
        catch(Exception e){
            fail("Shoult not throw Exception");
        }
        assertEquals(0, pool.getBorrowedConnections().size());

        Object borrowedObject1 = pool.borrowObject();
        assertEquals(1, pool.getBorrowedConnections().size());
        assertTrue(pool.getBorrowedConnections().contains(borrowedObject1));

        try{
            pool.returnObject(new Object());
        }
        catch(Exception e){
            fail("Shoult not throw Exception");
        }
        assertEquals(1, pool.getBorrowedConnections().size());
        
    }
    public void testThrowsExceptionIfPoolIsUnavailableDueToConnectionLost() throws Exception
    {
        MockPoolableObjectFactory fact = new MockPoolableObjectFactory(null);
        String connectionName = "con1";
        pool = new ConnectionPool(connectionName, fact, 10);
        pool.setPoolConnectionStatus(false);
        try
        {
            pool.borrowObject();
            fail("Should throw connection exception since pool is disconnected");
        }
        catch(ConnectionException e)
        {
            assertEquals(ConnectionException.noConnectionException(connectionName, null).getMessage(), e.getMessage());
        }

        Exception rootException = new Exception("Root exception");
        pool.setPoolConnectionStatus(false, rootException);
        try
        {
            pool.borrowObject();
            fail("Should throw connection exception since pool is disconnected");
        }
        catch(ConnectionException e)
        {
            assertSame(rootException, e.getCause());
        }

        rootException = new ConnectionException("Root exception");
        pool.setPoolConnectionStatus(false, rootException);
        try
        {
            pool.borrowObject();
            fail("Should throw connection exception since pool is disconnected");
        }
        catch(ConnectionException e)
        {
            assertSame(e, e);
        }

        pool.setPoolConnectionStatus(true);
        assertNotNull(pool.borrowObject());
    }
    public void testMaxIdleIsEqualToMaxActiveAfterConstructionAndAfterSetMaxIdleCall()
    {
        MockPoolableObjectFactory fact = new MockPoolableObjectFactory(null);
        String connectionName = "con1";
        MockConnection.checkConnectionResult = false;
        MockConnection.isConnected = false;
        
        int maxActive=10;
        
        pool = new ConnectionPool(connectionName, fact, maxActive);

        assertEquals(pool.getMaxActive(),maxActive);
        assertEquals(pool.getMaxIdle(),maxActive);
        assertEquals(pool.getMinIdle(),0);

        pool = new ConnectionPool(connectionName, fact, maxActive,1000);
        assertEquals(pool.getMaxActive(),maxActive);
        assertEquals(pool.getMaxIdle(),maxActive);
        assertEquals(pool.getMinIdle(),0);

        int newMaxActive=5000;
        pool.setMaxActive(newMaxActive);
        assertEquals(pool.getMaxActive(),newMaxActive);
        assertEquals(pool.getMaxIdle(),newMaxActive);
        assertEquals(pool.getMinIdle(),0);


    }
    public void testIfNoConnectionExistsPollWillBeMarkedAsDisconnected() throws Exception
    {
        MockPoolableObjectFactory fact = new MockPoolableObjectFactory(null);
        String connectionName = "con1";
        MockConnection.checkConnectionResult = false;
        MockConnection.isConnected = false;
        pool = new ConnectionPool(connectionName, fact, 10, 100);
        Thread.sleep(500);
        assertFalse(pool.isPoolConnected());
        MockConnection.checkConnectionResult = true;
        MockConnection.isConnected = true;
        Thread.sleep(500);
        assertTrue(pool.isPoolConnected());
        MockConnection conn = (MockConnection)pool.borrowObject();
        assertEquals(1, conn.disconnectCalledFor.size());
        assertTrue(conn.disconnectCalledFor.get(0) > 0);

    }




    public void testWillRunConnectionCheckerInNextBorrowObjectIfRunConnectionCheckerFlagIsSet() throws Exception
    {
        MockPoolableObjectFactory fact = new MockPoolableObjectFactory(null);
        String connectionName = "con1";
        MockConnection.checkConnectionResult = true;
        MockConnection.isConnected = true;
        pool = new ConnectionPool(connectionName, fact, 10, 1000000);
        Thread.sleep(500);
        assertTrue(pool.isPoolConnected());

        MockConnection.checkConnectionResult = false;
        MockConnection.isConnected = false;

        pool.markConnectionCheckerToRun();
        try{
            pool.borrowObject();
            fail("Should throw connection exception");
        }catch(ConnectionException e)
        {

        }
        assertFalse(pool.isPoolConnected());

        MockConnection.checkConnectionResult = true;
        MockConnection.isConnected = true;
        try{
            pool.borrowObject();
            fail("Should throw connection exception");
        }catch(ConnectionException e)
        {
        }
        assertFalse(pool.isPoolConnected());
    }

    public void testAllOfTheConnectionsAreDisconnectedPoolWillBeMarkedAsDisconnected() throws Exception
    {
        MockPoolableObjectFactory fact = new MockPoolableObjectFactory(null);
        String connectionName = "con1";
        pool = new ConnectionPool(connectionName, fact, 10, 100);
        MockConnection conn1 = (MockConnection)pool.borrowObject();
        MockConnection conn2 = (MockConnection)pool.borrowObject();
        MockConnection conn3 = (MockConnection)pool.borrowObject();
        pool.returnObject(conn1);
        pool.returnObject(conn2);
        MockConnection.isConnected = true;
        MockConnection.checkConnectionResult = false;
        Thread.sleep(500);
        assertFalse(pool.isPoolConnected());
        assertTrue(MockConnection.disconnectCalledFor.get(conn1.id) > 0);
        assertTrue(MockConnection.disconnectCalledFor.get(conn2.id) > 0);
        assertTrue(MockConnection.disconnectCalledFor.get(conn3.id) > 0);
        MockConnection.checkConnectionResult = true;
        Thread.sleep(500);
        MockConnection.disconnectCalledFor.clear();
        Thread.sleep(500);

        assertTrue(pool.isPoolConnected());
        assertTrue(MockConnection.disconnectCalledFor.isEmpty());
    }

    public void testIfAtLeastOneConnectionIsConnectedPoolWillNotBeDisconnected() throws Exception
    {
        MockPoolableObjectFactory fact = new MockPoolableObjectFactory(null);
        String connectionName = "con1";
        pool = new ConnectionPool(connectionName, fact, 10, 100);
        MockConnection conn1 = (MockConnection)pool.borrowObject();
        MockConnection conn2 = (MockConnection)pool.borrowObject();
        MockConnection conn3 = (MockConnection)pool.borrowObject();
        conn1.privateIsConnected = true;
        conn2.privateIsConnected = true;
        conn3.privateIsConnected = true;
        conn1.privateCheckConnectionResult = false;
        conn2.privateCheckConnectionResult = false;
        conn3.privateCheckConnectionResult = true;
        pool.returnObject(conn1);
        Thread.sleep(500);
        assertTrue(pool.isPoolConnected());
        assertTrue(MockConnection.disconnectCalledFor.get(conn1.id) > 0);
        assertTrue(MockConnection.disconnectCalledFor.get(conn2.id) > 0);
        assertTrue(MockConnection.disconnectCalledFor.get(conn3.id) == 0);
    }

    public void testIfCheckConnectionIsBlockedConnectionCheckerWillNotBeBlocked() throws Exception
    {
        final Object checkConnWaitLock = new Object();
        try{
            MockConnectionParameterSupplierImpl supp = new MockConnectionParameterSupplierImpl();
            ConnectionParam connParam = new ConnectionParam("con1" , MockConnection.class.getName(), new HashMap());
            connParam.setMinTimeout(100);
            connParam.setMaxTimeout(250);
            supp.setParam(connParam);

            MockPoolableObjectFactory fact = new MockPoolableObjectFactory(supp, MockTimeoutStrategy.class)
            {
                public IConnection _makeObject(long timeout) throws Exception {
                    MockConnection conn = new MockConnection(connectionId++)
                    {
                        public boolean checkConnection() {

                            synchronized (checkConnWaitLock)
                            {
                                try {
                                    checkConnWaitLock.wait();
                                } catch (InterruptedException e) {
                                }
                            }
                            return super.checkConnection();    //To change body of overridden methods use File | Settings | File Templates.
                        }
                    };
                    conn.setTimeout(timeout);
                    return conn;
                }
            };
            String connectionName = "con1";
            pool = new ConnectionPool(connectionName, fact, 10, 100);
            MockConnection conn1 = (MockConnection)pool.borrowObject();
            MockConnection conn2 = (MockConnection)pool.borrowObject();
            MockConnection conn3 = (MockConnection)pool.borrowObject();
            Thread.sleep(500);
            assertTrue(pool.isPoolConnected());
            Thread.sleep(2500);
            assertFalse(pool.isPoolConnected());
        }finally {
            synchronized (checkConnWaitLock)
            {
                checkConnWaitLock.notifyAll();
            }
        }
    }

    public void testIfCheckConnectionIsBlockedConnectionCheckerWillNotBeBlockedEvenTheMaxTimeoutIsZero() throws Exception
    {
        final Object checkConnWaitLock = new Object();
        try{
            MockConnectionParameterSupplierImpl supp = new MockConnectionParameterSupplierImpl();
            ConnectionParam connParam = new ConnectionParam("con1" , MockConnection.class.getName(), new HashMap());
            connParam.setMinTimeout(0);
            connParam.setMaxTimeout(0);
            connParam.setConnectionCheckerTimeout(2000);
            supp.setParam(connParam);

            MockPoolableObjectFactory fact = new MockPoolableObjectFactory(supp, MockTimeoutStrategy.class)
            {
                public IConnection _makeObject(long timeout) throws Exception {
                    MockConnection conn = new MockConnection(connectionId++)
                    {
                        public boolean checkConnection() {

                            synchronized (checkConnWaitLock)
                            {
                                try {
                                    checkConnWaitLock.wait();
                                } catch (InterruptedException e) {
                                }
                            }
                            return super.checkConnection();    //To change body of overridden methods use File | Settings | File Templates.
                        }
                    };
                    conn.setTimeout(timeout);
                    return conn;
                }
            };
            String connectionName = "con1";
            pool = new ConnectionPool(connectionName, fact, 10, 100);
            MockConnection conn1 = (MockConnection)pool.borrowObject();
            MockConnection conn2 = (MockConnection)pool.borrowObject();
            MockConnection conn3 = (MockConnection)pool.borrowObject();
            Thread.sleep(1000);
            assertTrue(pool.isPoolConnected());
            Thread.sleep(3000);
            assertFalse(pool.isPoolConnected());
        }finally {
            synchronized (checkConnWaitLock)
            {
                checkConnWaitLock.notifyAll();
            }
        }
    }

    public void testTimeoutMechanism() throws Exception
    {
        MockConnectionParameterSupplierImpl supp = new MockConnectionParameterSupplierImpl();
        ConnectionParam connParam = new ConnectionParam("con1" , MockConnection.class.getName(), new HashMap());
        connParam.setMinTimeout(0);
        connParam.setMaxTimeout(1000000000);
        supp.setParam(connParam);
        MockPoolableObjectFactory fact = new MockPoolableObjectFactory(supp, MockTimeoutStrategy.class);
        String connectionName = "con1";
        pool = new ConnectionPool(connectionName, fact, 10, 100);
        MockTimeoutStrategy.shouldRecalculate = true;
        MockTimeoutStrategy.newTimeoutInterval = 100000;
        MockConnection.isConnected = true;
        MockConnection.checkConnectionResult = true;
        Thread.sleep(500);   //Wait for connection cheker to process at least once
        MockConnection conn =  (MockConnection)pool.borrowObject();

        //get a fresh copy
        conn =  (MockConnection)pool.borrowObject();
        conn =  (MockConnection)pool.borrowObject();
        assertEquals(MockTimeoutStrategy.newTimeoutInterval, conn.getTimeout());
        assertFalse(MockTimeoutStrategy.connectionParameterList.isEmpty());
    }

    public void testIfShouldRecalculateIsFalseTimeoutWillNotChange() throws Exception
    {
        MockConnectionParameterSupplierImpl supp = new MockConnectionParameterSupplierImpl();
        ConnectionParam connParam = new ConnectionParam("con1" , MockConnection.class.getName(), new HashMap());
        connParam.setMinTimeout(0);
        connParam.setMaxTimeout(1000000000);
        supp.setParam(connParam);
        MockPoolableObjectFactory fact = new MockPoolableObjectFactory(supp, MockTimeoutStrategy.class);
        String connectionName = "con1";
        MockTimeoutStrategy.shouldRecalculate = false;
        MockTimeoutStrategy.newTimeoutInterval = 100000;
        pool = new ConnectionPool(connectionName, fact, 10, 100);
        MockConnection.isConnected = true;
        MockConnection.checkConnectionResult = true;
        Thread.sleep(500);   //Wait for connection cheker to process at least once
        MockConnection conn =  (MockConnection)pool.borrowObject();

        //get a fresh copy
        conn =  (MockConnection)pool.borrowObject();
        conn =  (MockConnection)pool.borrowObject();
        assertEquals(0, conn.getTimeout());
    }
    public void testIfObjectIsNotValidPoolWillNotUseThatObjectToCheckConnectionStatus() throws Exception
    {
        MockConnectionParameterSupplierImpl supp = new MockConnectionParameterSupplierImpl();
        ConnectionParam connParam = new ConnectionParam("con1" , MockConnection.class.getName(), new HashMap());
        connParam.setMinTimeout(0);
        connParam.setMaxTimeout(1000000000);
        supp.setParam(connParam);
        MockPoolableObjectFactory fact = new MockPoolableObjectFactory(supp, MockTimeoutStrategy.class);
        String connectionName = "con1";
        MockConnection.isConnected = true;
        MockConnection.checkConnectionResult = true;
        pool = new ConnectionPool(connectionName, fact, 10, 100);
        MockConnection conn = (MockConnection)pool.borrowObject();
        pool.returnObject(conn);
        List availableConns = fact.getAllConnections();
        for(Iterator it=availableConns.iterator();it.hasNext();)
        {
            ((MockConnection)it.next()).privateCheckConnectionResult = false;
        }
        fact.invalidConnections.addAll(fact.getAllConnections());
        Thread.sleep(500);
        assertTrue(pool.isPoolConnected());
    }

}

class MockTimeoutStrategy implements TimeoutStrategy
{
    public static List connectionParameterList = new ArrayList();
    public static boolean shouldRecalculate = false;
    public static long newTimeoutInterval = 0;
    public boolean shouldRecalculate(List<IConnection> connections) {
        return shouldRecalculate;
    }

    public long calculateNewTimeout(long oldTimeout, List<IConnection> connections)
    {
        connectionParameterList.add(connections);
        return newTimeoutInterval;   
    }
}
class MockPoolableObjectFactory extends BaseConnectionFactory
{
    int connectionId = 0;
    List invalidConnections = new ArrayList();
    MockPoolableObjectFactory(Class timeoutStrategyClass) {
        super("factory1", new MockConnectionParameterSupplierImpl(), timeoutStrategyClass);
    }

    MockPoolableObjectFactory(ConnectionParameterSupplier paramSuplier, Class timeoutStrategyClass) {
        super("factory1", paramSuplier, timeoutStrategyClass);
    }

    public IConnection _makeObject(long timeout) throws Exception {
        MockConnection conn = new MockConnection(connectionId++);
        conn.setTimeout(timeout);
        return conn;
    }

    public void _destroyObject(IConnection o) throws Exception {
    }

    public boolean validateObject(Object o) {
        return !invalidConnections.contains(o);
    }

    public void activateObject(Object o) throws Exception {
    }

    public void passivateObject(Object o) throws Exception {
    }
}

class MockConnection implements IConnection
{
    public static boolean isValid = true;
    public static int checkConnectionCallCount = 0;
    public static boolean isConnected = true;
    public static boolean checkConnectionResult = true;
    public static Map<Integer, Integer> disconnectCalledFor = new HashMap<Integer, Integer>();
    int id;
    boolean privateIsConnected = true;
    boolean privateIsValid = true;
    boolean privateCheckConnectionResult = true;
    long timeout;

    public boolean isConnectionException(Throwable t)
    {
        return false;
    }
    public MockConnection(int id) {
        this.id = id;
        disconnectCalledFor.put(id, 0);
    }

    public void init(ConnectionParam param) throws Exception {
    }

    public boolean isValid() {
        return isValid && privateIsValid;
    }

    public void invalidate() {
        privateIsValid = false;
    }

    public ConnectionParam getParameters() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void _disconnect() {
        disconnectCalledFor.put(id, disconnectCalledFor.get(id)+1);
        privateIsConnected = false;
    }

    public boolean isConnected() {
        return isConnected && privateIsConnected;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean checkConnection() {
        checkConnectionCallCount++;
        return checkConnectionResult && privateCheckConnectionResult;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void _connect() throws Exception {
        if(!isConnected())
        {
            throw new Exception("Connection lost");
        }
    }
}
