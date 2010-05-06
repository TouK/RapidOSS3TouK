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

import com.ifountain.comp.test.util.CommonTestUtils;
import com.ifountain.comp.test.util.WaitAction;
import com.ifountain.comp.test.util.threads.TestAction;
import com.ifountain.comp.test.util.threads.TestActionExecutorThread;
import com.ifountain.core.connection.ConnectionManager;
import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.IConnection;
import com.ifountain.core.connection.exception.UndefinedConnectionException;
import com.ifountain.core.connection.exception.ConnectionException;
import com.ifountain.core.connection.mocks.MockConnectionImpl;
import com.ifountain.core.datasource.mocks.MockActionImpl;
import com.ifountain.core.datasource.mocks.MockBaseAdapterImpl;
import com.ifountain.core.datasource.mocks.MockConnectionParameterSupplierImpl;
import com.ifountain.core.test.util.RapidCoreTestCase;

import java.util.HashMap;
import java.util.Map;

public class BaseAdapterTest extends RapidCoreTestCase
{
    private MockConnectionParameterSupplierImpl connectionParameterSupplier;
    private ConnectionParam param;
    private String connectionName;
    private MockBaseAdapterImpl impl;
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        connectionParameterSupplier = new MockConnectionParameterSupplierImpl();
        ConnectionManager.setParamSupplier(connectionParameterSupplier);
        
        connectionName = "conn1";
        param = createConnectionParam(connectionName);
        connectionParameterSupplier.setParam(param);
        impl = new MockBaseAdapterImpl(connectionName);
    }
    
    public void testExecuteAction() throws Exception
    {
        MockActionImpl action = new MockActionImpl();
        impl.executeAction(action);
        
        assertSame(ConnectionManager.getConnection(connectionName), action.getConnection());
        assertTrue(action.isExecuted());
    }
     public void testDestroyedAdapterDoesNotExecuteAction() throws Exception
    {
        MockActionImpl action = new MockActionImpl();
        impl.destroy();
        impl.executeAction(action);
        assertFalse(action.isExecuted());
    }
    
    public void testExecuteActionThrowsExceptionIfActionCouldNotBeExecutedSuccessfully() throws Exception
    {
        MockActionImpl dsAction = new MockActionImpl();
        Exception expectedException = new Exception("Exception occurred while executing action.");
        dsAction.setExceptionWillBeThrown(expectedException);
        try
        {
            impl.executeAction(dsAction);
            fail("Should throw exception");
        }
        catch (Exception e)
        {
            assertEquals(expectedException, e);
        }
        TestAction action = new TestAction()
        {
            public void execute() throws Throwable
            {
                ConnectionManager.getConnection(connectionName);
            }
        };
        
        final TestActionExecutorThread th = new TestActionExecutorThread(action);
        th.start();
        try
        {
            CommonTestUtils.waitFor(new WaitAction(){
                public void check()
                {
                    assertTrue(th.isExecutedSuccessfully());
                }
            });
        }
        finally
        {
            th.interrupt();
            th.join();
        }
        
    }
    
    public void testExecuteActionThrowsExceptionIfConnectionIsNotDefined() throws Exception
    {
        connectionParameterSupplier.setParam(null);
        MockActionImpl dsAction = new MockActionImpl();
        
        try
        {
            impl.executeAction(dsAction);
            fail("Should throw exception");
        }
        catch (UndefinedConnectionException e)
        {
            assertEquals(new UndefinedConnectionException(connectionName), e);
        }
    }

    public void testSubscribeSetConnectionAsInvalidIfThrowedExceptionIsConnectionException() throws Exception
    {
        createConnectionParam(connectionName);
        IConnection conn = ConnectionManager.getConnection(connectionName);
        ConnectionManager.releaseConnection(conn);
        MockConnectionImpl.isConnectionException = true;
        MockActionImpl dsAction = new MockActionImpl();
        Exception expectedException = new Exception("Exception occurred while executing action.");
        dsAction.setExceptionWillBeThrown(expectedException);

        try
        {
            impl.executeAction(dsAction);
            fail("Should throw exception");
        }
        catch(Exception e)
        {
            assertTrue(e instanceof ConnectionException);
        }

        IConnection connectionAfterSubscribeException = ConnectionManager.getConnection(connectionName);
        assertNotSame("Connection object should not be same since previous connection should be marked as invalid by adapter", conn, connectionAfterSubscribeException);

    }
    
    private ConnectionParam createConnectionParam(String connectionName)
    {
        MockConnectionImpl.globalConnectionException = null;
        MockConnectionImpl.isConnectionExceptionParams.clear();;
        MockConnectionImpl.isConnectionException = false;
        Map<String, Object> optionalParams = new HashMap<String, Object>();
        optionalParams.put("OptParam1", "optvalue1");
        ConnectionParam param = new ConnectionParam(connectionName, MockConnectionImpl.class.getName(), optionalParams);
        return param;
    }
}
