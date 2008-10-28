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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ifountain.comp.test.util.logging.TestLogUtils;
import com.ifountain.core.connection.exception.UndefinedConnectionException;
import com.ifountain.core.connection.mocks.MockConnectionImpl;
import com.ifountain.core.connection.mocks.NotConnectedConnection;
import com.ifountain.core.datasource.mocks.MockConnectionParameterSupplierImpl;
import com.ifountain.core.test.util.RapidCoreTestCase;

public class ConnectionManagerTest extends RapidCoreTestCase
{
    MockConnectionParameterSupplierImpl parameterSupplier;
    ClassLoader classLoader = ConnectionManagerTest.class.getClassLoader(); 
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        parameterSupplier = new MockConnectionParameterSupplierImpl();
        ConnectionManager.setParamSupplier(parameterSupplier);
        
    }

    public void testcheckConnection() throws Exception
    {
        assertFalse(ConnectionManager.checkConnection("dx4545"));

        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(1);
        parameterSupplier.setParam(param);

        //releases connection
        // TODO it can stock here, check timout    
        assertTrue(ConnectionManager.checkConnection(connectionName));
        assertTrue(ConnectionManager.checkConnection(connectionName));
        assertTrue(ConnectionManager.checkConnection(connectionName));

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
        assertEquals(param, conn1.getParam());
        MockConnectionImpl conn2 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn2.isConnected());
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
        assertEquals(param2, conn4.getParam());
        
        MockConnectionImpl conn5 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn5.isConnected());
    }

    public void testUpdatingMaxNumberOfConnections() throws Exception{
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(1);
        parameterSupplier.setParam(param);

        MockConnectionImpl conn1 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn1.isConnected());

        param.setMaxNumberOfConnectionsInPool(2);
        parameterSupplier.setParam(param);

        MockConnectionImpl conn2 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn2.isConnected());

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
        ConnectionManager.initialize(TestLogUtils.log, parameterSupplier, classLoader);
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(2);
        parameterSupplier.setParam(param);
        
        MockConnectionImpl conn1 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        MockConnectionImpl conn2 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        
        
        wrongClassName.append("UnknownClassName");
        ConnectionManager.releaseConnection(conn1);
        assertFalse(conn1.isConnected());
        assertTrue(conn2.isConnected());
        
        wrongClassName.delete(0, wrongClassName.length());
        
        ConnectionManager.releaseConnection(conn2);
        assertTrue(conn2.isConnected());
        wrongClassName.append("UnknownClassName");
        try
        {
            ConnectionManager.getConnection(connectionName);
        }
        catch (Exception e)
        {
        }
        assertFalse(conn2.isConnected());
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
        assertTrue(conn1.isConnected());
        assertEquals(param, conn1.getParam());
        MockConnectionImpl conn2 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn2.isConnected());
        
        ConnectionManager.releaseConnection(conn1);
        ConnectionManager.removeConnection(connectionName);
        
        assertFalse(conn1.isConnected());
        assertTrue(conn2.isConnected());
        
        ConnectionManager.releaseConnection(conn2);
        assertFalse(conn2.isConnected());
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
        
        ConnectionManager.initialize(Logger.getRootLogger(), parameterSupplier, classLoader);
        
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
        
        assertTrue(conn1.isConnected());
        assertTrue(conn2.isConnected());
        assertTrue(conn3.isConnected());
        assertTrue(conn4.isConnected());
        
        ConnectionManager.destroy();
        ConnectionManager.releaseConnection(conn1);
        ConnectionManager.releaseConnection(conn2);
        ConnectionManager.releaseConnection(conn3);
        ConnectionManager.releaseConnection(conn4);
        assertFalse(conn1.isConnected());
        assertFalse(conn2.isConnected());
        assertFalse(conn3.isConnected());
        assertFalse(conn4.isConnected());
        
        ConnectionManager.initialize(Logger.getRootLogger(), parameterSupplier, classLoader);
        
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
        ConnectionParam param = new ConnectionParam("Database", connectionName, className, optionalParams);
        return param;
    }
    
}




