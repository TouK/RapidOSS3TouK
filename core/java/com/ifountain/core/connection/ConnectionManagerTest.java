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
import com.ifountain.comp.test.util.threads.TestAction;
import com.ifountain.comp.test.util.threads.TestActionExecutorThread;
import com.ifountain.core.connection.exception.ConnectionException;
import com.ifountain.core.connection.exception.UndefinedConnectionException;
import com.ifountain.core.connection.exception.ConnectionPoolException;
import com.ifountain.core.connection.mocks.MockConnectionImpl;
import com.ifountain.core.connection.mocks.NotConnectedConnection;
import com.ifountain.core.datasource.mocks.MockConnectionParameterSupplierImpl;
import com.ifountain.core.test.util.RapidCoreTestCase;
import org.apache.log4j.Logger;

import java.util.*;

public class ConnectionManagerTest extends RapidCoreTestCase
{
    long poolConnectionCheckingInterval;
    MockConnectionParameterSupplierImpl parameterSupplier;
    ClassLoader classLoader = ConnectionManagerTest.class.getClassLoader(); 
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        MockTimeoutStrategy.connectionParameterList.clear();
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
        assertEquals(1, ConnectionManager.getActiveCount(connectionName));
        assertEquals(1, ConnectionManager.getConnectionCount(connectionName));
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

        assertNull(ConnectionManager.getBorrowedConnections(connectionName));

        MockConnectionImpl conn1 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn1.isConnected());
        assertEquals(param, conn1.getParam());
        assertEquals(1, ConnectionManager.getActiveCount(connectionName));
        assertEquals(1, ConnectionManager.getConnectionCount(connectionName));
        assertEquals(1,ConnectionManager.getBorrowedConnections(connectionName).size());
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn1));

        MockConnectionImpl conn2 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn2.isConnected());

        assertNotSame(conn1, conn2);
        assertEquals(2, ConnectionManager.getActiveCount(connectionName));
        assertEquals(2, ConnectionManager.getConnectionCount(connectionName));
        assertEquals(2,ConnectionManager.getBorrowedConnections(connectionName).size());
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn1));
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn2));


        ConnectionManager.releaseConnection(conn1);
        assertEquals(1, ConnectionManager.getActiveCount(connectionName));
        assertEquals(2, ConnectionManager.getConnectionCount(connectionName));

        IConnection conn3 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertSame(conn1, conn3);

        assertEquals(2,ConnectionManager.getBorrowedConnections(connectionName).size());
        assertEquals(2, ConnectionManager.getActiveCount(connectionName));
        assertEquals(2, ConnectionManager.getConnectionCount(connectionName));
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn2));
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn3));

        ConnectionManager.releaseConnection(conn3);
        
        ConnectionParam param2 = createConnectionParam(connectionName);
        param2.setMaxNumberOfConnectionsInPool(3);
        param2.getOtherParams().put("newparam", "newValue");
        parameterSupplier.setParam(param2);
        
        MockConnectionImpl conn4 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn4.isConnected());
        assertEquals(param2, conn4.getParam());

        assertEquals(2,ConnectionManager.getBorrowedConnections(connectionName).size());
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn2));
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn4));
        
        MockConnectionImpl conn5 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn5.isConnected());

        assertEquals(3,ConnectionManager.getBorrowedConnections(connectionName).size());
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn2));
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn4));
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn5));
    }
    public void testGetConnectionThrowsExceptionIfParametersAreNull() throws Exception
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
    
    class GetConnectionThread extends Thread{
        String connectionName;
        int getConnectionLimit=3;
        public List connections;
        int sleeptime;
        public GetConnectionThread(String connectionName,int getConnectionLimit,int sleeptime){
            this.connectionName=connectionName;
            this.getConnectionLimit=getConnectionLimit;
            this.sleeptime=sleeptime;
            connections =  new ArrayList();
        }
         public void run() {

            //System.out.println("* "+getName()+" starts");
            try {
                for(int x=0;x<getConnectionLimit;x++){
                    //System.out.println( "* "+getName()+" will do getConnection");
                    MockConnectionImpl conn1 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
                    connections.add(conn1);
                    Thread.sleep(sleeptime);
                }
            }
            catch(InterruptedException e)
            {
                System.out.println(e);
            }
            catch(Exception e)
            {
                System.out.println(e);
                e.printStackTrace();
            }
            //System.out.println( "* "+getName()+" stops");
        }
    }

     public void testGetConnectionSynchronizesPoolAccessAndCreation() throws Exception
    {
        int threadLimit=20;
        int getConnectionLimit=3;
        int threadSleepTime=1000;
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(threadLimit*getConnectionLimit);
        parameterSupplier.setParam(param);

        List threadList=new ArrayList();
        List connectionList=new ArrayList();
        try{
            for(int x=1;x<=threadLimit;x++)
            {
                GetConnectionThread t1=new GetConnectionThread(connectionName,getConnectionLimit,threadSleepTime);
                threadList.add(t1);
                t1.start();

            }

            Thread.sleep(threadSleepTime*(getConnectionLimit+2));
            ListIterator listItr = threadList.listIterator();
            while(listItr.hasNext()) {
                GetConnectionThread t1 = (GetConnectionThread)listItr.next();
                connectionList.addAll(t1.connections);
            }

            List borrowedConnectionList=ConnectionManager.getBorrowedConnections(connectionName);
            System.out.println (borrowedConnectionList);
            assertEquals(borrowedConnectionList.size(),threadLimit*getConnectionLimit);

            ListIterator listItr2 = connectionList.listIterator();
            while(listItr2.hasNext()) {
                MockConnectionImpl conn1 = (MockConnectionImpl)listItr2.next();
                assertTrue(conn1.isConnected());
                assertEquals(param, conn1.getParam());
                assertTrue(borrowedConnectionList.contains(conn1));
            }
        }
        catch(Throwable t)
        {
            for(int i=0; i < threadList.size(); i++)
            {
                ((Thread)threadList.get(i)).join();
            }
        }

    }
     class ReleaseConnectionThread extends Thread{
        
        public List connections;
        int threadSleepTime;
        public ReleaseConnectionThread(List connections,int threadSleepTime){
            this.connections=connections;
            this.threadSleepTime=threadSleepTime;
        }
         public void run() {

            //System.out.println("* "+getName()+" starts");
            try {
                for(int x=0;x<connections.size();x++){
                    //System.out.println( "* "+getName()+" will do releaseConnection");
                    ConnectionManager.releaseConnection((MockConnectionImpl)connections.get(x));                    
                    Thread.sleep(threadSleepTime);
                }
            }
            catch(InterruptedException e)
            {
                System.out.println(e);
            }
            catch(Exception e)
            {
                System.out.println(e);
                e.printStackTrace();
            }
            //System.out.println( "* "+getName()+" stops");
        }
    }
    //This test is similar to testGetConnectionSynchronizesPoolAccessAndCreation
    //Currently there is no syncronization in releaseConnection ( actually there is syncronization in Pool )
    //This test currenty only tests that release connection is safely executed by multiple threads
    public void testReleaseConnectionWithMultipleThreads() throws Exception
    {
        int threadLimit=20;
        int getConnectionLimit=3;
        int threadSleepTime=1000;

        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(threadLimit*getConnectionLimit);
        parameterSupplier.setParam(param);

        assertNull(ConnectionManager.getBorrowedConnections(connectionName));
        
        List connectionList=new ArrayList();
        for(int x=0;x<threadLimit * getConnectionLimit ;x++)
        {
            MockConnectionImpl conn1 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
            connectionList.add(conn1);
            assertTrue(conn1.isConnected());
        }


        assertEquals(ConnectionManager.getBorrowedConnections(connectionName).size(),threadLimit*getConnectionLimit);

        //Now we seperate the connections to threadLimit count of groups
        List threadConnections=new ArrayList();
        for(int x=0;x<threadLimit;x++)
        {
            List connections=new ArrayList();
            threadConnections.add(connections);
            for(int y=0;y<getConnectionLimit;y++)
            {
                connections.add(connectionList.get((x*getConnectionLimit)+y));
            }
            assertEquals(connections.size(),getConnectionLimit);
        }
        assertEquals(threadConnections.size(),threadLimit);

        ListIterator listItr = threadConnections.listIterator();
        while(listItr.hasNext()) {
            ReleaseConnectionThread t1 = new ReleaseConnectionThread((ArrayList)listItr.next(),threadSleepTime);
            t1.start();
        }


        Thread.sleep(getConnectionLimit*(threadSleepTime+2));

        
        assertEquals(ConnectionManager.getBorrowedConnections(connectionName).size(),0);
        
        ListIterator listItr2 = connectionList.listIterator();
        while(listItr2.hasNext()) {
            MockConnectionImpl conn1 = (MockConnectionImpl)listItr2.next();
            assertTrue(conn1.isConnected());
            assertEquals(param, conn1.getParam());
        }
        
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
            fail("Should throw exception since connection does not exist");
        }
        catch(ConnectionException e)
        {
            assertSame(NotConnectedConnection.notConnectException, e.getCause());
        }

        Thread.sleep(500);
        assertFalse(ConnectionManager.isPoolConnected(connectionName));

        try
        {
            ConnectionManager.getConnection(connectionName);
            fail("Should throw exception since connection does not exist");
        }
        catch(ConnectionException e)
        {
            assertSame(NotConnectedConnection.notConnectException, e.getCause());
        }

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

        assertNull(ConnectionManager.getBorrowedConnections(connectionName));


        MockConnectionImpl conn1 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn1.isConnected());
        assertEquals(param, conn1.getParam());
        MockConnectionImpl conn2 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn2.isConnected());

        assertEquals(2,ConnectionManager.getBorrowedConnections(connectionName).size());

        ConnectionManager.releaseConnection(conn1);

        assertEquals(1,ConnectionManager.getBorrowedConnections(connectionName).size());

        ConnectionManager.removeConnection(connectionName);

        assertNull(ConnectionManager.getBorrowedConnections(connectionName));


        assertFalse(conn1.isConnected());
        assertTrue(conn2.isConnected());

    }
    


     public void testReleaseConnection() throws Exception
     {
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(5);
        parameterSupplier.setParam(param);

        assertNull(ConnectionManager.getBorrowedConnections(connectionName));

        MockConnectionImpl conn1 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn1.isConnected());

        MockConnectionImpl conn2 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn2.isConnected());

        assertEquals(2,ConnectionManager.getBorrowedConnections(connectionName).size());
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn1));
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn2));

        ConnectionManager.releaseConnection(conn1);
        assertTrue(conn1.isConnected());
        assertEquals(1,ConnectionManager.getBorrowedConnections(connectionName).size());
        assertFalse(ConnectionManager.getBorrowedConnections(connectionName).contains(conn1));
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn2));

        ConnectionManager.releaseConnection(conn2);
        assertTrue(conn2.isConnected());
        assertEquals(0,ConnectionManager.getBorrowedConnections(connectionName).size());

        conn2 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn2.isConnected());

        assertEquals(1,ConnectionManager.getBorrowedConnections(connectionName).size());
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn2));

        ConnectionManager.releaseConnection(conn2);
        assertTrue(conn2.isConnected());
        assertEquals(0,ConnectionManager.getBorrowedConnections(connectionName).size());

        //we now release already relased connection , should not change connection state and no exception
        ConnectionManager.releaseConnection(conn1);
        ConnectionManager.releaseConnection(conn2);
        assertTrue(conn1.isConnected());
        assertTrue(conn2.isConnected());
        assertEquals(0,ConnectionManager.getBorrowedConnections(connectionName).size());

    }
    public void testReleaseConnectionDisconnectsConnectionIfPoolDoesNotExists()  throws Exception
    {

        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(5);
        parameterSupplier.setParam(param);

        assertNull(ConnectionManager.getBorrowedConnections(connectionName));

        MockConnectionImpl conn1 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn1.isConnected());
        MockConnectionImpl conn2 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn2.isConnected());

        assertEquals(2,ConnectionManager.getBorrowedConnections(connectionName).size());
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn1));
        assertTrue(ConnectionManager.getBorrowedConnections(connectionName).contains(conn2));

        ConnectionManager.removeConnection(connectionName);
        assertNull(ConnectionManager.getBorrowedConnections(connectionName));
        assertTrue(conn1.isConnected());
        assertTrue(conn2.isConnected());

        ConnectionManager.releaseConnection(conn1);
        assertNull(ConnectionManager.getBorrowedConnections(connectionName));
        assertFalse(conn1.isConnected());
        assertTrue(conn2.isConnected());

        ConnectionManager.releaseConnection(conn2);
        assertNull(ConnectionManager.getBorrowedConnections(connectionName));
        assertFalse(conn1.isConnected());
        assertFalse(conn2.isConnected());

    }
    public void testRelaseConnectionThrowsConnectionPoolExceptionIfPoolGeneratesError() throws Exception
    {
        String connectionName = "conn1";
        ConnectionParam param = createConnectionParam(connectionName);
        param.setMaxNumberOfConnectionsInPool(5);
        parameterSupplier.setParam(param);

        assertNull(ConnectionManager.getBorrowedConnections(connectionName));

        MockConnectionImpl conn1 = (MockConnectionImpl) ConnectionManager.getConnection(connectionName);
        assertTrue(conn1.isConnected());

        assertEquals(1,ConnectionManager.getBorrowedConnections(connectionName).size());
        
        Map pools=ConnectionManager.getPoolsMap();

        ConnectionPoolExceptionMock poolMock=new ConnectionPoolExceptionMock(null,null,0);
        poolMock.returnObjectException=new Exception("returnObjectException");
        
        pools.put(connectionName,poolMock);

        try{
            ConnectionManager.releaseConnection(conn1);
            fail("should throw ConnectionPoolException ");
        }
        catch(ConnectionPoolException e)
        {
            System.out.println(e.getCause().getLocalizedMessage());
            System.out.println(poolMock.returnObjectException.getMessage());

            assertTrue(e.getCause().getMessage().indexOf(poolMock.returnObjectException.getMessage())>0);
        }
        
        assertTrue(conn1.isConnected());

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

    public class ConnectionPoolExceptionMock extends ConnectionPool
    {
        public Exception returnObjectException;
        public ConnectionPoolExceptionMock(String connectionName, BaseConnectionFactory poolableObjectFactory, int maxNumberOfConnections) {
                super(connectionName, poolableObjectFactory, maxNumberOfConnections, 10000);
        }
        public void returnObject(Object connection) throws Exception {
            if(returnObjectException!=null)
            {
                throw new Exception(returnObjectException);
            }
        }
    }
    
}

