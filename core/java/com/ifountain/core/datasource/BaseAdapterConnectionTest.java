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
 * Created on Jan 21, 2008
 *
 */
package com.ifountain.core.datasource;

import com.ifountain.comp.test.util.threads.TestAction;
import com.ifountain.comp.test.util.threads.TestActionExecutorThread;
import com.ifountain.core.connection.ConnectionManager;
import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.IConnection;
import com.ifountain.core.connection.exception.ConnectionException;
import com.ifountain.core.connection.mocks.MockConnectionImpl;
import com.ifountain.core.connection.mocks.NotConnectedConnection;
import com.ifountain.core.datasource.mocks.MockActionImpl;
import com.ifountain.core.datasource.mocks.MockBaseAdapterImpl;
import com.ifountain.core.datasource.mocks.MockConnectionParameterSupplierImpl;
import com.ifountain.core.test.util.RapidCoreTestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseAdapterConnectionTest extends RapidCoreTestCase
{
    private MockConnectionParameterSupplierImpl connectionParameterSupplier;
    private ConnectionParam param;
    private String connectionConfigName;
    private MockBaseAdapterImpl impl;
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        MockConnectionImpl.isConnectionException = false;
        MockConnectionImpl.isConnectionExceptionParams.clear();;
        MockConnectionImpl.globalConnectionException = null;
        connectionParameterSupplier = new MockConnectionParameterSupplierImpl();
        ConnectionManager.setParamSupplier(connectionParameterSupplier);
        connectionConfigName = "BaseAdapterConnectionTestds1";
        
    }
    public void testExecuteActionThrowsExceptionIfConnectionIsNotConnected() throws Throwable
    {
        
        createConnectionParam(connectionConfigName, NotConnectedConnection.class);
        impl = new MockBaseAdapterImpl(connectionConfigName);
        
        MockActionImpl action = new MockActionImpl();
        
        try
        {
            impl.executeAction(action);
            fail("Should throw connection exception");
        }
        catch (ConnectionException e)
        {
        }
    }
    
    public void testReconnectionWithConnectionExceptionThrownWhileGettingConnection() throws Exception
    {
        createConnectionParam(connectionConfigName, NotConnectedConnection.class);
        impl = new MockBaseAdapterImpl(connectionConfigName, 100);
        
        final MockActionImpl action = new MockActionImpl();
        
        TestAction datasourceExecuteWaitAction = new ExecuteWaitAction(impl, action);
        
        TestActionExecutorThread t1 = new TestActionExecutorThread(datasourceExecuteWaitAction);
        t1.start();
        
        Thread.sleep(300);
        
        try
        {
            assertTrue(t1.isStarted());
            assertFalse(t1.isExecutedSuccessfully());
            assertNull(t1.getThrowedException());
            param.setConnectionClass(MockConnectionImpl.class.getName());
            
            Thread.sleep(1500);
            assertTrue(t1.isStarted());
            assertTrue(t1.isExecutedSuccessfully());
            assertNull(t1.getThrowedException());
        }
        finally
        {
            t1.interrupt();
        }
    }
    public void testReconnectionWithConnectionExceptionThrownWhileExecutingAction() throws Exception
    {
        createConnectionParam(connectionConfigName, MockConnectionImpl.class);
        impl = new MockBaseAdapterImpl(connectionConfigName, 100);
        final List<String> executedMethods = new ArrayList<String>();
        final MockActionImpl dsAction = new MockActionImpl()
        {
            @Override
            public void execute(IConnection conn) throws Exception
            {

                executedMethods.add("execute");
                if(executedMethods.size() == 1)
                {
                    Exception connectionException = new Exception("Exception due to connection");
                    param.setConnectionClass(NotConnectedConnection.class.getName());
                    ((MockConnectionImpl)conn).setConnectionException(connectionException);
                    throw connectionException;
                }
            }
        };
        
        TestAction datasourceExecuteWaitAction = new ExecuteWaitAction(impl, dsAction);
        
        TestActionExecutorThread t1 = new TestActionExecutorThread(datasourceExecuteWaitAction);
        t1.start();
        
        Thread.sleep(300);
        
        try
        {
            assertTrue(t1.isStarted());
            assertFalse(t1.isExecutedSuccessfully());
            assertNull(t1.getThrowedException());
            param.setConnectionClass(MockConnectionImpl.class.getName());
            
            Thread.sleep(1500);
            assertTrue(t1.isStarted());
            assertTrue(t1.isExecutedSuccessfully());
            assertNull(t1.getThrowedException());
        }
        finally
        {
            t1.interrupt();
        }
    }
    public void testThrowsExceptionIfThrowedWhileExecutingActionAndDatasourceIsConnected() throws Exception
    {
        createConnectionParam(connectionConfigName, MockConnectionImpl.class);
        impl = new MockBaseAdapterImpl(connectionConfigName, 100);
        final Exception connectionException = new Exception("Exception due to connection");
        final MockActionImpl dsAction = new MockActionImpl()
        {
            @Override
            public void execute(IConnection conn) throws Exception
            {
                throw connectionException;
            }
        };
        
        TestAction datasourceExecuteWaitAction = new ExecuteWaitAction(impl, dsAction);
        
        TestActionExecutorThread t1 = new TestActionExecutorThread(datasourceExecuteWaitAction);
        t1.start();
        
        Thread.sleep(1100);
        
        try
        {
            assertTrue(t1.isStarted());
            assertFalse(t1.isExecutedSuccessfully());
            assertSame(connectionException, t1.getThrowedException());
        }
        finally {
            t1.interrupt();
        }
    }
    public void testThrowsExceptionIfThrowedWhileExecutingActionAndReconnectIntervalIsSmallerThan0() throws Exception
    {
        createConnectionParam(connectionConfigName, MockConnectionImpl.class);
        impl = new MockBaseAdapterImpl(connectionConfigName, 0);
        final Exception connectionException = new Exception("Exception due to connection");
        final MockActionImpl dsAction = new MockActionImpl()
        {
            @Override
            public void execute(IConnection conn) throws Exception
            {
                ((MockConnectionImpl)conn).setConnectionException(connectionException);
                throw connectionException;
            }
        };
        
        
        TestAction datasourceExecuteWaitAction = new ExecuteWaitAction(impl, dsAction);
        TestActionExecutorThread t1 = new TestActionExecutorThread(datasourceExecuteWaitAction);
        t1.start();
        
        Thread.sleep(1100);
        
        try
        {
            assertTrue(t1.isStarted());
            assertFalse(t1.isExecutedSuccessfully());
            assertTrue(t1.getThrowedException() instanceof ConnectionException);
            assertSame(connectionException, t1.getThrowedException().getCause());
        }
        finally {
            t1.interrupt();
        }
    }

    public void testThrowsConnectionExceptionAndInavlidateConnectionIfCheckConnectionReturnsTrueAndIsConnectionExceptionReturnTrue() throws Exception
    {
        createConnectionParam(connectionConfigName, MockConnectionImpl.class);
        impl = new MockBaseAdapterImpl(connectionConfigName, 0);
        MockConnectionImpl.isConnectionException = true;
        final Exception connectionException = new Exception("Exception due to connection");
        final MockActionImpl dsAction = new MockActionImpl()
        {
            @Override
            public void execute(IConnection conn) throws Exception
            {
                throw connectionException;
            }
        };


        TestAction datasourceExecuteWaitAction = new ExecuteWaitAction(impl, dsAction);
        TestActionExecutorThread t1 = new TestActionExecutorThread(datasourceExecuteWaitAction);
        t1.start();

        Thread.sleep(1100);

        try
        {
            assertTrue(t1.isStarted());
            assertFalse(t1.isExecutedSuccessfully());
            assertTrue(t1.getThrowedException() instanceof ConnectionException);
            assertSame(connectionException, t1.getThrowedException().getCause());
            assertEquals(1, MockConnectionImpl.isConnectionExceptionParams.size());
            assertTrue(MockConnectionImpl.isConnectionExceptionParams.contains(connectionException));
        }
        finally {
            t1.interrupt();
        }
    }
    
    
    public void testInterruptingCurrentThreadCancelsReconnection() throws Exception
    {
        createConnectionParam(connectionConfigName, NotConnectedConnection.class);
        impl = new MockBaseAdapterImpl(connectionConfigName, 100);
        
        final MockActionImpl dsAction = new MockActionImpl();
        
        TestAction datasourceExecuteWaitAction = new ExecuteWaitAction(impl, dsAction);
        
        TestActionExecutorThread t1 = new TestActionExecutorThread(datasourceExecuteWaitAction);
        t1.start();
        Thread.sleep(300);
        
        t1.interrupt();
        t1.join();
        
        assertTrue(t1.isStarted());
        assertFalse(t1.isExecutedSuccessfully());
        assertTrue(t1.getThrowedException() instanceof InterruptedException);
        
    }
    
    private void createConnectionParam(String connName, Class connClass)
    {
        Map<String, Object> optionalParams = new HashMap<String, Object>();
        optionalParams.put("OptParam1", "optvalue1");
        param = new ConnectionParam(connName, connClass.getName(), optionalParams);
        connectionParameterSupplier.setParam(param);
    }
}

class ExecuteWaitAction implements TestAction
{
    public Adapter impl;
    public Action action;
    
    /**
     * @param impl
     * @param action
     */
    public ExecuteWaitAction(Adapter impl,
            Action action)
    {
        this.impl = impl;
        this.action = action;
    }

    public void execute() throws Throwable
    {
        impl.executeAction(action);
    }
}
