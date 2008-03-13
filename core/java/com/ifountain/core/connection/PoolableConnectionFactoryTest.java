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

import com.ifountain.core.connection.exception.ConnectionException;
import com.ifountain.core.connection.exception.ConnectionInitializationException;
import com.ifountain.core.connection.mocks.MockConnectionImpl;
import com.ifountain.core.test.util.RapidCoreTestCase;

public class PoolableConnectionFactoryTest extends RapidCoreTestCase
{
     
    public void testFactory() throws Exception
    {
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        PoolableConnectionFactory factory = new PoolableConnectionFactory(PoolableConnectionFactoryTest.class.getClassLoader(), param);
        
        MockConnectionImpl conn = (MockConnectionImpl) factory.makeObject();
        assertTrue(conn.isConnected());
        assertEquals(param, conn.getParam());
        conn.disconnect();
        factory.activateObject(conn);
        assertTrue(conn.isConnected());
        assertTrue(factory.validateObject(conn));
        
        Exception expectedConnectionException = new Exception("Exception Occurred While Connecting");
        conn.setConnectionException(expectedConnectionException);
        conn.disconnect();
        
        try
        {
            factory.activateObject(conn);
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
        PoolableConnectionFactory factory = new PoolableConnectionFactory(PoolableConnectionFactoryTest.class.getClassLoader(), param);
        
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
        PoolableConnectionFactory factory = new PoolableConnectionFactory(PoolableConnectionFactoryTest.class.getClassLoader(), param);
        
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
        PoolableConnectionFactory factory = new PoolableConnectionFactory(classLoader, param);
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
    }
    
    private ConnectionParam createConnectionParam(String connectionName)
    {
        Map<String, Object> optionalParams = new HashMap<String, Object>();
        optionalParams.put("OptParam1", "optvalue1");
        ConnectionParam param = new ConnectionParam("Database", connectionName, MockConnectionImpl.class.getName(), optionalParams);
        return param;
    }
}
