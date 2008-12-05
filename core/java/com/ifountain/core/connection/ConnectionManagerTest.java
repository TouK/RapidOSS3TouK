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

import com.ifountain.comp.test.util.logging.TestLogUtils;
import com.ifountain.comp.test.util.threads.TestActionExecutorThread;
import com.ifountain.comp.test.util.threads.TestAction;
import com.ifountain.core.connection.exception.UndefinedConnectionException;
import com.ifountain.core.connection.exception.ConnectionException;
import com.ifountain.core.connection.mocks.MockConnectionImpl;
import com.ifountain.core.connection.mocks.NotConnectedConnection;
import com.ifountain.core.datasource.mocks.MockConnectionParameterSupplierImpl;
import com.ifountain.core.test.util.RapidCoreTestCase;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class ConnectionManagerTest extends RapidCoreTestCase
{
    long poolConnectionCheckingInterval;
    MockConnectionParameterSupplierImpl parameterSupplier;
    ClassLoader classLoader = ConnectionManagerTest.class.getClassLoader(); 
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        MockTimeoutStrategy.connectionParameterList.clear();;
        parameterSupplier = new MockConnectionParameterSupplierImpl();
        ConnectionManager.setParamSupplier(parameterSupplier);
        poolConnectionCheckingInterval = 1000;
        
    }
    public void testInitializingTimeoutMechanism() throws Exception
    {
        poolConnectionCheckingInterval = 100;
        MockTimeoutStrategy.newTimeoutInterval = 100999;
        MockTimeoutStrategy.shouldRecalculate= true;

        ConnectionManager.destroy();
        ConnectionManager.initialize(TestLogUtils.log, parameterSupplier, classLoader, poolConnectionCheckingInterval);
        ConnectionManager.setTimeoutStrategyClass(MockTimeoutStrategy.class);
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(10);
        parameterSupplier.setParam(param);

        //we get first connection to initialize pool
        IConnection conn = ConnectionManager.getConnection(connectionName);
        assertNotNull(conn);
        Thread.sleep(500);
        assertFalse(MockTimeoutStrategy.connectionParameterList.isEmpty());
        //all subsequent connections will get new timeout value
        conn = ConnectionManager.getConnection(connectionName);
        conn = ConnectionManager.getConnection(connectionName);
        conn = ConnectionManager.getConnection(connectionName);
        assertEquals(MockTimeoutStrategy.newTimeoutInterval, conn.getTimeout());

    }
    public void testcheckConnection() throws Exception
    {
        assertFalse(ConnectionManager.checkConnection("dx4545"));

        final String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(1);
        parameterSupplier.setParam(param);

        //releases connection
        TestAction action = new TestAction(){
            public void execute() throws Throwable {

                ConnectionManager.checkConnection(connectionName);
            }
        };
        TestActionExecutorThread t1 = new TestActionExecutorThread(action);
        TestActionExecutorThread t2 = new TestActionExecutorThread(action);
        TestActionExecutorThread t3 = new TestActionExecutorThread(action);
        t1.start();
        t2.start();
        t3.start();
        t1.join(1000);
        t2.join(1000);
        t3.join(1000);
        assertTrue(t1.isExecutedSuccessfully());
        assertTrue(t2.isExecutedSuccessfully());
        assertTrue(t3.isExecutedSuccessfully());

        param = createConnectionParam(connectionName, NotConnectedConnection.class.getName());
        param.setMaxNumberOfConnectionsInPool(2);
        parameterSupplier.setParam(param);

        assertFalse(ConnectionManager.checkConnection(connectionName));
        
    }
    
    public void testGetConnection() throws Exception
    {
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(2);
        parameterSupplier.setParam(param);
        
        MockConnectionImpl conn1 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn1.isConnected());
        assertTrue(conn1.checkConnection());
        assertEquals(param, conn1.getParam());
        MockConnectionImpl conn2 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn2.isConnected());
        assertTrue(conn2.checkConnection());

        assertNotSame(conn1, conn2);
        
        ConnectionManager.releaseConnection(conn1);
        
        IConnection conn3 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertSame(conn1, conn3);
        
        ConnectionManager.releaseConnection(conn3);
        
        ConnectionParam param2 = createConnectionParam(connectionName);
        param2.setMaxNumberOfConnectionsInPool(3);
        param2.getOtherParams().put("newparam", "newValue");
        parameterSupplier.setParam(param2);
        
        MockConnectionImpl conn4 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn4.isConnected());
        assertTrue(conn4.checkConnection());
        assertEquals(param2, conn4.getParam());
        
        MockConnectionImpl conn5 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn5.isConnected());
        assertTrue(conn5.checkConnection());
    }

    public void testConnectionCheckerMechnism() throws Exception
    {
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(2);
        param.setConnectionClass(NotConnectedConnection.class.getName());
        parameterSupplier.setParam(param);
        ConnectionManager.destroy();
        ConnectionManager.initialize(TestLogUtils.log, parameterSupplier, classLoader, 100);
        try
        {
            ConnectionManager.getConnection(connectionName);
            fail("Should throw exception cince connection does not exist");
        }
        catch(ConnectionException e)
        {
        }

        Thread.sleep(500);
        assertFalse(ConnectionManager.isPoolConnected(connectionName));

        param = (ConnectionParam)param.clone();
        param.setConnectionClass(MockConnectionImpl.class.getName());
        parameterSupplier.setParam(param);

        Thread.sleep(500);
        assertTrue(ConnectionManager.isPoolConnected(connectionName));

    }

    public void testUpdatingMaxNumberOfConnections() throws Exception{
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(1);
        parameterSupplier.setParam(param);

        MockConnectionImpl conn1 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn1.isConnected());
        assertTrue(conn1.checkConnection());

        param.setMaxNumberOfConnectionsInPool(2);
        parameterSupplier.setParam(param);

        MockConnectionImpl conn2 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn2.isConnected());
        assertTrue(conn2.checkConnection());

        ConnectionManager.releaseConnection(conn1);
        ConnectionManager.releaseConnection(conn2);
    }
    
    public void testGetDatasourceValidatesObject() throws Exception
    {
        ConnectionManager.destroy();
        final StringBuffer wrongClassName = new StringBuffer();
        ClassLoader classLoader = new ClassLoader(PoolableConnectionFactoryTest.class.getClassLoader())
        {
            @Override
            public Class< ? > loadClass(String name)
                    throws ClassNotFoundException
            {
                if(MockConnectionImpl.class.getName().equals(name))
                {
                    if(wrongClassName.length() != 0)
                    {
                        return super.loadClass(wrongClassName.toString());
                    }
                    return MockConnectionImpl.class;
                }
                return super.loadClass(name);
            }
        };
        ConnectionManager.initialize(TestLogUtils.log, parameterSupplier, classLoader, poolConnectionCheckingInterval);
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(2);
        parameterSupplier.setParam(param);
        
        MockConnectionImpl conn1 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        MockConnectionImpl conn2 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        
        
        wrongClassName.append("UnknownClassName");
        ConnectionManager.releaseConnection(conn1);
        assertFalse(conn1.isConnected());
        assertFalse(conn1.checkConnection());
        assertTrue(conn2.isConnected());
        assertTrue(conn2.checkConnection());

        wrongClassName.delete(0, wrongClassName.length());
        
        ConnectionManager.releaseConnection(conn2);
        assertTrue(conn2.isConnected());
        assertTrue(conn2.checkConnection());
        wrongClassName.append("UnknownClassName");
        try
        {
            ConnectionManager.getConnection(connectionName);
        }
        catch (Exception e)
        {
        }
        assertFalse(conn2.isConnected());
        assertFalse(conn2.checkConnection());
    }
    
    public void testRemoveConnection() throws Exception {
        String connectionName = "conn1";
        try {
            ConnectionManager.removeConnection(connectionName);
        } catch (Exception e) {
            fail("should not throw exception");
        }
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(2);
        parameterSupplier.setParam(param);
        
        MockConnectionImpl conn1 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn1.checkConnection());
        assertEquals(param, conn1.getParam());
        MockConnectionImpl conn2 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn2.checkConnection());
        
        ConnectionManager.releaseConnection(conn1);
        ConnectionManager.removeConnection(connectionName);
        
        assertFalse(conn1.checkConnection());
        assertTrue(conn2.checkConnection());
        
        ConnectionManager.releaseConnection(conn2);
        assertFalse(conn2.checkConnection());
    }
    
    public void testThrowsExceptionIfParametersAreNull() throws Exception
    {
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(2);
        parameterSupplier.setParam(param);
    }
    
    public void testReleaseConnection() throws Exception
    {
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(2);
        parameterSupplier.setParam(null);
        
        try
        {
            ConnectionManager.getConnection(connectionName);
            fail("Should throw exception");
        }
        catch (UndefinedConnectionException e)
        {
            assertEquals(new UndefinedConnectionException(connectionName), e);
        }
        
    }
    
    public void testInitializeDoesnotInitializesTwice() throws Exception
    {
        String connectionName1 = "conn1";
        ConnectionParam param1 = createConnectionParam(connectionName1);
        parameterSupplier.setParam(param1);
        
        
        MockConnectionImpl conn1 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName1);
        ConnectionManager.releaseConnection(conn1);
        
        ConnectionManager.initialize(Logger.getRootLogger(), parameterSupplier, classLoader, poolConnectionCheckingInterval);
        
        IConnection conn2 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName1);
        ConnectionManager.releaseConnection(conn2);
        assertSame(conn1, conn2);
    }
    
    public void testDestroy() throws Exception
    {
        String connectionName1 = "conn1";
        String connectionName2 = "conn2";
        ConnectionParam param1 = createConnectionParam(connectionName1);
        param1.setMaxNumberOfConnectionsInPool(2);
        parameterSupplier.setParam(param1);
        
        MockConnectionImpl conn1 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName1);
        MockConnectionImpl conn2 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName1);
        
        ConnectionParam param2 = createConnectionParam(connectionName1);
        param2.setMaxNumberOfConnectionsInPool(2);
        parameterSupplier.setParam(param2);
        
        MockConnectionImpl conn3 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName2);
        MockConnectionImpl conn4 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName2);
        
        assertNotSame(conn1, conn3);
        assertNotSame(conn2, conn3);
        
        assertTrue(conn1.checkConnection());
        assertTrue(conn2.checkConnection());
        assertTrue(conn3.checkConnection());
        assertTrue(conn4.checkConnection());
        
        ConnectionManager.destroy();
        ConnectionManager.releaseConnection(conn1);
        ConnectionManager.releaseConnection(conn2);
        ConnectionManager.releaseConnection(conn3);
        ConnectionManager.releaseConnection(conn4);
        assertFalse(conn1.checkConnection());
        assertFalse(conn2.checkConnection());
        assertFalse(conn3.checkConnection());
        assertFalse(conn4.checkConnection());
        
        ConnectionManager.initialize(Logger.getRootLogger(), parameterSupplier, classLoader, poolConnectionCheckingInterval);
        
        IConnection ds1AfterReinitialize = (MockConnectionImpl) ConnectionManager.getConnection(connectionName1);
        
        assertNotSame(conn1, ds1AfterReinitialize);
    }
    
    private ConnectionParam createConnectionParam(String connectionName)
    {
        return createConnectionParam(connectionName, MockConnectionImpl.class.getName());
    }

    private ConnectionParam createConnectionParam(String connectionName, String className)
    {
        Map<String, Object> optionalParams = new HashMap<String, Object>();
        optionalParams.put("OptParam1", "optvalue1");
        ConnectionParam param = new ConnectionParam("Database", connectionName, className, optionalParams, 1, 1000, 100000000);
        return param;
    }


    
}