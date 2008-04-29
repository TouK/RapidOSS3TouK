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
/**
 * Created on Mar 12, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.core.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ifountain.comp.test.util.threads.TestAction;
import com.ifountain.comp.test.util.threads.TestActionExecutorThread;
import com.ifountain.core.connection.ConnectionManager;
import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.exception.ConnectionException;
import com.ifountain.core.connection.mocks.MockConnectionImpl;
import com.ifountain.core.connection.mocks.NotConnectedConnection;
import com.ifountain.core.datasource.mocks.MockBaseListeningAdapter;
import com.ifountain.core.datasource.mocks.MockConnectionParameterSupplierImpl;
import com.ifountain.core.test.util.RapidCoreTestCase;

public class BaseListeningAdapterConnectionTest extends RapidCoreTestCase {

    private MockConnectionParameterSupplierImpl connectionParameterSupplier;
    private ConnectionParam param;
    private String connectionName;
    private MockBaseListeningAdapter listeningAdapter;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        connectionParameterSupplier = new MockConnectionParameterSupplierImpl();
        ConnectionManager.setParamSupplier(connectionParameterSupplier);
        connectionName = "ds1";
    }
    
    public void testSubscribeThrowsExceptionIfConnectionIsNotConnected() throws Throwable
    {
        createConnectionParam(connectionName, NotConnectedConnection.class);
        listeningAdapter = new MockBaseListeningAdapter(connectionName);
        try
        {
            listeningAdapter.subscribe();
            fail("Should throw connection exception");
        }
        catch (ConnectionException e)
        {
            assertTrue(e instanceof ConnectionException);
        }
    }
    
    public void testReconnectionWithConnectionExceptionThrownWhileGettingConnection() throws Exception
    {
        createConnectionParam(connectionName, NotConnectedConnection.class);
        listeningAdapter = new MockBaseListeningAdapter(connectionName, 100);
        
        
        TestAction subscribeWaitAction = new SubscribeWaitAction(listeningAdapter);
        
        TestActionExecutorThread t1 = new TestActionExecutorThread(subscribeWaitAction);
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
    
    public void testReconnectionWithConnectionExceptionThrownWhileSubscription() throws Exception
    {
        fail("Reimplement this test");
        createConnectionParam(connectionName, MockConnectionImpl.class);
        final List<String> executedMethods = new ArrayList<String>();
        listeningAdapter = new MockBaseListeningAdapter(connectionName, 100){
            @Override
            protected void _subscribe() throws Exception {
                executedMethods.add("subscribe");
                if(executedMethods.size() == 1)
                {
                    Exception connectionException = new Exception("Exception due to connection");
                    ((MockConnectionImpl)connection).setConnectionException(connectionException);
                    param.setConnectionClass(NotConnectedConnection.class.getName());
                    throw connectionException;
                }
            }
        };
        
        TestAction subscribeWaitAction = new SubscribeWaitAction(listeningAdapter);
        
        TestActionExecutorThread t1 = new TestActionExecutorThread(subscribeWaitAction);
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
    
    public void testThrowsExceptionIfThrownDuringSubscriptionAndConnectionIsConnected() throws Exception
    {
        createConnectionParam(connectionName, MockConnectionImpl.class);
        final Exception connectionException = new Exception("Exception due to connection");
        listeningAdapter = new MockBaseListeningAdapter(connectionName, 100){
            @Override
            protected void _subscribe() throws Exception {
                throw connectionException;
            }
        };
        
        TestAction subscribeWaitAction = new SubscribeWaitAction(listeningAdapter);
        TestActionExecutorThread t1 = new TestActionExecutorThread(subscribeWaitAction);
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
    
    public void testThrowsExceptionIfThrownDuringSubscriptionAndReconnectIntervalIsSmallerThan0() throws Exception
    {
        createConnectionParam(connectionName, MockConnectionImpl.class);
        final Exception connectionException = new Exception("Exception due to connection");
        listeningAdapter = new MockBaseListeningAdapter(connectionName, 0){
            @Override
            protected void _subscribe() throws Exception {
                ((MockConnectionImpl)connection)._disconnect();
                throw connectionException;
            }
        };
       
        TestAction subscribeWaitAction = new SubscribeWaitAction(listeningAdapter);
        TestActionExecutorThread t1 = new TestActionExecutorThread(subscribeWaitAction);
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
    
    public void testInterruptingCurrentThreadCancelsReconnection() throws Exception
    {
        createConnectionParam(connectionName, NotConnectedConnection.class);
        listeningAdapter = new MockBaseListeningAdapter(connectionName, 100);
        
        
        TestAction subscribeWaitAction = new SubscribeWaitAction(listeningAdapter);
        
        TestActionExecutorThread t1 = new TestActionExecutorThread(subscribeWaitAction);
        t1.start();
        Thread.sleep(300);
        
        t1.interrupt();
        t1.join();
        
        assertTrue(t1.isStarted());
        assertFalse(t1.isExecutedSuccessfully());
        assertTrue(t1.getThrowedException() instanceof InterruptedException);
        
    }
    
    
    class SubscribeWaitAction implements TestAction
    {
        public BaseListeningAdapter listeningAdapter;
        
        public SubscribeWaitAction(BaseListeningAdapter listeningAdapter)
        {
            this.listeningAdapter = listeningAdapter;
        }

        public void execute() throws Throwable
        {
            listeningAdapter.subscribe();
        }
    }
    
    private void createConnectionParam(String connName, Class connClass)
    {
        Map<String, Object> optionalParams = new HashMap<String, Object>();
        optionalParams.put("OptParam1", "optvalue1");
        param = new ConnectionParam("Database", connName, connClass.getName(), optionalParams);
        connectionParameterSupplier.setParam(param);
    }
}
