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

import com.ifountain.comp.utils.CaseInsensitiveMap;
import com.ifountain.core.connection.exception.ConnectionException;
import com.ifountain.core.connection.exception.ConnectionInitializationException;
import com.ifountain.core.connection.mocks.MockConnectionImpl;
import com.ifountain.core.datasource.mocks.MockConnectionParameterSupplierImpl;
import com.ifountain.core.test.util.RapidCoreTestCase;

import java.util.Map;

public class PoolableConnectionFactoryTest extends RapidCoreTestCase
{
    MockConnectionParameterSupplierImpl paramSupplier;
    public void setUp() throws Exception
    {
        super.setUp();
        paramSupplier = new MockConnectionParameterSupplierImpl();
    }
    public void testMakeObject() throws Exception
    {
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        paramSupplier.setParam(param);
        PoolableConnectionFactory factory = new PoolableConnectionFactory(PoolableConnectionFactoryTest.class.getClassLoader(), connectionName, paramSupplier, null);
        
        MockConnectionImpl conn = (MockConnectionImpl) factory.makeObject();
        assertTrue(conn.isConnected());
        assertTrue(conn.checkConnection());
        assertEquals(param, conn.getParam());
        assertTrue(factory.validateObject(conn));
    }

    public void testWithTimeoutValue() throws Exception
    {
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxTimeout(30000);
        paramSupplier.setParam(param);
        PoolableConnectionFactory factory = new PoolableConnectionFactory(PoolableConnectionFactoryTest.class.getClassLoader(), connectionName, paramSupplier, null);

        long timeoutValue = 9999;
        MockConnectionImpl conn = (MockConnectionImpl) factory._makeObject(timeoutValue);
        assertEquals(timeoutValue, conn.getTimeout());
        timeoutValue = 0;
        conn = (MockConnectionImpl) factory._makeObject(timeoutValue);
        assertEquals(MockConnectionImpl.defaultTimeout, conn.getTimeout());

        timeoutValue = -1;
        conn = (MockConnectionImpl) factory._makeObject(timeoutValue);
        assertEquals(MockConnectionImpl.defaultTimeout, conn.getTimeout());

    }


    public void tesMakeObjectThrowsExceptionIfCanNotConnect() throws Exception
    {
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        paramSupplier.setParam(param);
        PoolableConnectionFactory factory = new PoolableConnectionFactory(PoolableConnectionFactoryTest.class.getClassLoader(), connectionName, paramSupplier,null);
        Exception expectedConnectionException = new Exception("Exception Occurred While Connecting");
        MockConnectionImpl.globalConnectionException = expectedConnectionException;

        try
        {
            factory.makeObject();
            fail("Should throw exception");
        }
        catch (ConnectionException e)
        {
            assertEquals(expectedConnectionException, e.getCause());
        }
    }
    
    public void testMakeObjectThrowsExceptionIfClassNotFound() throws Exception
    {
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setConnectionClass("unknownclass.unknownclass");
        paramSupplier.setParam(param);
        PoolableConnectionFactory factory = new PoolableConnectionFactory(PoolableConnectionFactoryTest.class.getClassLoader(), connectionName, paramSupplier, null);
        
        try
        {
            factory.makeObject();
            fail("Should throw exception");
        }
        catch (ConnectionInitializationException e)
        {
            assertEquals(ClassNotFoundException.class, e.getCause().getClass());
        }
    }
    
    public void testMakeObjectThrowsExceptionIfLoadedClassIsNotConnection() throws Exception
    {
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setConnectionClass("java.lang.Object");
        paramSupplier.setParam(param);
        PoolableConnectionFactory factory = new PoolableConnectionFactory(PoolableConnectionFactoryTest.class.getClassLoader(), connectionName, paramSupplier, null);
        
        try
        {
            factory.makeObject();
            fail("Should throw exception");
        }
        catch (ConnectionInitializationException e)
        {
            assertEquals(ClassCastException.class, e.getCause().getClass());
        }
    }
    
    public void testValidate() throws Exception
    {
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
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        paramSupplier.setParam(param);
        PoolableConnectionFactory factory = new PoolableConnectionFactory(classLoader, connectionName, paramSupplier, null);
        MockConnectionImpl conn = (MockConnectionImpl) factory.makeObject();
        
        wrongClassName.append(this.getClass().getName());
        assertFalse(factory.validateObject(conn));
        
        wrongClassName.delete(0, wrongClassName.length());
        wrongClassName.append("UnknownClassName");
        assertFalse(factory.validateObject(conn));

        try
        {
            factory.makeObject();
            fail("Should throw exception");
        }
        catch (ConnectionInitializationException e)
        {
            assertEquals(ClassNotFoundException.class, e.getCause().getClass());
        }

        wrongClassName.delete(0, wrongClassName.length());
        conn.getParameters().getOtherParams().put("OptParam1", "OptParam1changed");
        assertFalse(factory.validateObject(conn));
    }

    public void testValidateReturnFalseIfConnectionIsNotConnected() throws Exception
    {
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        paramSupplier.setParam(param);
        PoolableConnectionFactory factory = new PoolableConnectionFactory(PoolableConnectionFactoryTest.class.getClassLoader(), connectionName, paramSupplier, null);

        MockConnectionImpl conn = (MockConnectionImpl) factory.makeObject();
        assertTrue(conn.isConnected());
        conn._disconnect();
        assertFalse(factory.validateObject(conn));
    }

    public void testValidateReturnFalseIfConnectionIsNotValid() throws Exception
    {
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        paramSupplier.setParam(param);
        PoolableConnectionFactory factory = new PoolableConnectionFactory(PoolableConnectionFactoryTest.class.getClassLoader(), connectionName, paramSupplier, null);

        MockConnectionImpl conn = (MockConnectionImpl) factory.makeObject();
        conn.invalidate();
        assertFalse(factory.validateObject(conn));
    }
    
    private ConnectionParam createConnectionParam(String connectionName)
    {
        Map optionalParams = new CaseInsensitiveMap();
        optionalParams.put("OptParam1", "optvalue1");
        ConnectionParam param = new ConnectionParam(connectionName, MockConnectionImpl.class.getName(), optionalParams);
        return param;
    }
}
