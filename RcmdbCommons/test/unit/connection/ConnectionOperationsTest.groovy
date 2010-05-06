package connection

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.core.connection.ConnectionManager
import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.core.datasource.mocks.MockConnectionParameterSupplierImpl
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.mocks.MockConnectionImpl
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.core.connection.exception.ConnectionException
import com.ifountain.rcmdb.connection.RcmdbConnectionManagerAdapter

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Dec 30, 2008
* Time: 1:14:03 PM
* To change this template use File | Settings | File Templates.
*/
class ConnectionOperationsTest extends RapidCmdbTestCase{
     public void setUp() {
        super.setUp();
        clearMetaClasses();
        
    }

    public void tearDown() {
        super.tearDown();
    }
    private void clearMetaClasses()
    {
                      
        RcmdbConnectionManagerAdapter.destroyInstance();
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(ConnectionManager);
        GroovySystem.metaClassRegistry.removeMetaClass(RcmdbConnectionManagerAdapter);
        ExpandoMetaClass.enableGlobally();
    }
    
    public void testCheckConnection()
    {
        def supplier = new MockConnectionParameterSupplierImpl();
        ConnectionManager.initialize(TestLogUtils.log, supplier, this.class.classLoader, 1000);
        ConnectionParam param = new ConnectionParam("con1", MockConnectionImpl.class.name, [:]);
        supplier.setParam (param);
        CompassForTests.addOperationSupport (Connection, ConnectionOperations);
        Connection conn = new Connection(name:"con1", connectionClass:MockConnectionImpl.class.name);
        MockConnectionImpl.globalConnectionException = new Exception("Connection lost");
        
        try
        {
            conn.checkConnection()
            fail("Should throw exception");
        }
        catch(Exception e)
        {
            assertSame (MockConnectionImpl.globalConnectionException , e);
        }

        try
        {
            conn.checkConnection()
            fail("Should throw exception");
        }
        catch(Exception e)
        {
            assertSame (MockConnectionImpl.globalConnectionException , e);
        }

        ConnectionManager.destroy();
        param.maxNumberOfConnectionsInPool = 1;
        MockConnectionImpl.globalConnectionException = null
        ConnectionManager.initialize(TestLogUtils.log, supplier, this.class.classLoader, 1000);


        assertTrue(conn.checkConnection());

        int threadState = 0;
        Thread t = Thread.start{
            threadState = 1;
            conn.checkConnection()
            threadState = 2;
        }

        Thread.sleep (300);
        assertEquals (2, threadState);


    }
    public void testCheckConnectionDoesNotGenerateNullPointerExceptionWhenErrorOccurs(){
        CompassForTests.addOperationSupport (Connection, ConnectionOperations);
        Connection conn = new Connection(name:"con1");

        def exception=new ConnectionException("Exception with no cause");
        assertNull(exception.getCause());
        ConnectionManager.metaClass.static.getConnection = { String connectionName ->
            throw exception;    
        }
        try{
            conn.checkConnection();
        }
        catch(e)
        {
            if(e.class==NullPointerException)
            {
                fail("Shoul not throw NullPointerException")
            }

            assertEquals(e.class,ConnectionException);
        }
        
    }

    public void testAfterDelete(){
        CompassForTests.addOperationSupport (Connection, ConnectionOperations);
        Connection conn = new Connection(name:"con1");

        def callParams=[:];
        RcmdbConnectionManagerAdapter.metaClass.removeConnection= { String connectionName ->
            callParams.connectionName=connectionName;
        }

        conn.afterDelete();
        assertEquals(callParams.connectionName,conn.name)
        
    }
     public void testAfterUpdate(){
        CompassForTests.addOperationSupport (Connection, ConnectionOperations);
        Connection conn = new Connection(name:"con1");
      
        def callParams=[:];
        RcmdbConnectionManagerAdapter.metaClass.addConnection= { Connection connection ->
            callParams.connection=connection;
        }

        conn.afterUpdate([:]);
        assertEquals(callParams.connection,conn)

    }
     public void testAfterInsert(){
        CompassForTests.addOperationSupport (Connection, ConnectionOperations);
        Connection conn = new Connection(name:"con1");

        def callParams=[:];
        RcmdbConnectionManagerAdapter.metaClass.addConnection= { Connection connection ->
            callParams.connection=connection;
        }

        conn.afterInsert();
        assertEquals(callParams.connection,conn)

    }
}