package connection

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.core.connection.ConnectionManager
import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.core.datasource.mocks.MockConnectionParameterSupplierImpl
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.mocks.MockConnectionImpl
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Dec 30, 2008
* Time: 1:14:03 PM
* To change this template use File | Settings | File Templates.
*/
class ConnectionOperationsTest extends RapidCmdbTestCase{
    public void testCheckConnection()
    {
        def supplier = new MockConnectionParameterSupplierImpl();
        ConnectionManager.initialize(TestLogUtils.log, supplier, this.class.classLoader, 1000);
        ConnectionParam param = new ConnectionParam("Mock", "con1", MockConnectionImpl.class.name, [:]);
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
}