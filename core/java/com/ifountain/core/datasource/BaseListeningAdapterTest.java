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

import com.ifountain.comp.test.util.CommonTestUtils;
import com.ifountain.comp.test.util.WaitAction;
import com.ifountain.comp.test.util.threads.TestAction;
import com.ifountain.comp.test.util.threads.TestActionExecutorThread;
import com.ifountain.core.connection.ConnectionManager;
import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.IConnection;
import com.ifountain.core.connection.exception.UndefinedConnectionException;
import com.ifountain.core.connection.mocks.MockConnectionImpl;
import com.ifountain.core.datasource.mocks.MockBaseListeningAdapter;
import com.ifountain.core.datasource.mocks.MockConnectionParameterSupplierImpl;
import com.ifountain.core.test.util.RapidCoreTestCase;

import java.util.*;


public class BaseListeningAdapterTest extends RapidCoreTestCase {

    private MockConnectionParameterSupplierImpl connectionParameterSupplier;
    private ConnectionParam param;
    private String connectionName;
    private MockBaseListeningAdapter listeningAdapter;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        connectionParameterSupplier = new MockConnectionParameterSupplierImpl();
        ConnectionManager.setParamSupplier(connectionParameterSupplier);
        
        connectionName = "conn1";
        param = createConnectionParam(connectionName);
        connectionParameterSupplier.setParam(param);
        listeningAdapter = new MockBaseListeningAdapter(connectionName, 0);
    }
    
    public void testSubscribe() throws Exception
    {
        listeningAdapter.subscribe();
        assertTrue(listeningAdapter.getConnection().isConnected());
        assertTrue(listeningAdapter.getConnection().checkConnection());
        assertTrue(listeningAdapter.isSubscribed());
    }
    
    public void testSubscribeThrowsExceptionIfSubscriptionIsNotSuccessfully() throws Exception
    {
        
        final Exception expectedException = new Exception("Exception occurred while subscribing.");
        listeningAdapter = new MockBaseListeningAdapter(connectionName, 0){
          @Override
            protected void _subscribe() throws Exception {
                throw expectedException;
            }  
        };
        try
        {
            listeningAdapter.subscribe();
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
    
    public void testSubscribeThrowsExceptionIfConnectionCannotBeReceived() throws Exception
    {
        connectionParameterSupplier.setParam(null);
        try
        {
            listeningAdapter.subscribe();
            fail("Should throw exception");
        }
        catch (UndefinedConnectionException e)
        {
            assertEquals(new UndefinedConnectionException(connectionName), e);
        }
    }
    
    public void testUnsubscribeReleasesConnection() throws Exception {
        param.setMaxNumberOfConnectionsInPool(1);
        listeningAdapter.subscribe();
        assertTrue(listeningAdapter.getConnection().isConnected());
        assertTrue(listeningAdapter.getConnection().checkConnection());
        assertTrue(listeningAdapter.isSubscribed());
        
        listeningAdapter.unsubscribe();
        assertFalse(listeningAdapter.isSubscribed());
        assertNull(listeningAdapter.getConnection());
        IConnection conn = ConnectionManager.getConnection(connectionName);
        assertTrue(conn.isConnected());
    }

    public void testUnsubscribeReleasesConnectionIfExceptionOccurs() throws Exception {
        param.setMaxNumberOfConnectionsInPool(1);
        listeningAdapter.subscribe();
        assertTrue(listeningAdapter.getConnection().isConnected());
        assertTrue(listeningAdapter.getConnection().checkConnection());
        assertTrue(listeningAdapter.isSubscribed());
        listeningAdapter.unSubscribeException = new RuntimeException();
        try
        {
            listeningAdapter.unsubscribe();
            fail("Should throw exception");
        }
        catch(Exception e)
        {
            assertEquals(listeningAdapter.unSubscribeException, e);
        }
        assertTrue(listeningAdapter.isSubscribed());
        assertNull(listeningAdapter.getConnection());
        IConnection conn = ConnectionManager.getConnection(connectionName);
        assertTrue(conn.isConnected());
    }
    
    public void testDelegatingUpdateEventsToSubscribers() throws Exception {
        final ObservableSource source = new ObservableSource();
        final Object changeArg = new Object();
        source.setChangeArg(changeArg);
        listeningAdapter = new MockBaseListeningAdapter(connectionName){
            @Override
            protected void _subscribe() throws Exception {
                source.addObserver(this);
            }
            @Override
            public Object _update(Observable o, Object arg) {
                return changeArg;
            }
        };
        final List<Object> argList = new ArrayList<Object>();
        Observer subscriber = new Observer(){
            @Override
            public void update(Observable o, Object arg) {
                argList.add(arg);
            }
        };
        listeningAdapter.addObserver(subscriber);
        listeningAdapter.subscribe();

        source.sourceChanged();
        assertEquals(1, argList.size());
        assertSame(changeArg, argList.get(0));
    }
    
    public void testNullValuesAreNotDelegated() throws Exception {
        final ObservableSource source = new ObservableSource();
        source.setChangeArg(new Object());
        listeningAdapter = new MockBaseListeningAdapter(connectionName){
            @Override
            protected void _subscribe() throws Exception {
                source.addObserver(this);
            }
            @Override
            public Object _update(Observable o, Object arg) {
                return null;
            }
        };
        final List<Object> argList = new ArrayList<Object>();
        Observer subscriber = new Observer(){
            @Override
            public void update(Observable o, Object arg) {
                argList.add(arg);
            }
        };
        listeningAdapter.addObserver(subscriber);
        listeningAdapter.subscribe();
        
        source.sourceChanged();
        assertEquals(0, argList.size());
    }

    
    private ConnectionParam createConnectionParam(String connectionName)
    {
        Map<String, Object> optionalParams = new HashMap<String, Object>();
        optionalParams.put("OptParam1", "optvalue1");
        ConnectionParam param = new ConnectionParam("Database", connectionName, MockConnectionImpl.class.getName(), optionalParams);
        param.setMaxNumberOfConnectionsInPool(3);
        return param;
    }
    
    class ObservableSource extends Observable{
        private Object changeArg;
        public void sourceChanged(){
            setChanged();
            notifyObservers(changeArg);
        }
        public void setChangeArg(Object changeArg) {
            this.changeArg = changeArg;
        }
    }
    
}
