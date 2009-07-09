package database.datasource

import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.core.test.util.DatasourceTestUtils
import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.rcmdb.test.util.DatabaseConnectionImplTestUtils
import datasource.DatabaseAdapter

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 3, 2009
* Time: 9:40:20 AM
* To change this template use File | Settings | File Templates.
*/
class DatabaseAdapterTests extends RapidCoreTestCase {
    protected void setUp() throws Exception {
        super.setUp();
        DatasourceTestUtils.getParamSupplier().setParam(DatabaseConnectionImplTestUtils.getConnectionParam());
        try {
            DatabaseConnectionImplTestUtils.createTableConnectionTrials();
        } catch (ClassNotFoundException e) {
        }
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testExecuteQuery() throws Exception {
        DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        String sqlQuery = "select * from connectiontrials";
        Object[] queryParams = new Object[0];
        DatabaseAdapter adapter = new DatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, 0, TestLogUtils.log)
        List results = adapter.executeQuery(sqlQuery, queryParams);
        assertEquals(0, results.size());
      
        DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(1,"Switch","eraaswiad");
        results = adapter.executeQuery(sqlQuery, queryParams);
        assertEquals(1, results.size());
        assertEquals(new BigDecimal(1), results[0]["id"])
        assertEquals(new BigDecimal(1), results[0]["ID"])
        assertEquals("Switch", results[0]["CLASSNAME"])
        assertEquals("Switch", results[0]["classname"])
        assertEquals("eraaswiad", results[0]["instancename"])
        assertEquals("eraaswiad", results[0]["INSTANCENAME"])
    }

    public void testExecuteQueryWithFetchSize() throws Exception{
       DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
       DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(1,"Switch","eraaswiad1");
       DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(2,"Switch","eraaswiad2");
       DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(3,"Switch","eraaswiad3");
       DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(4,"Switch","eraaswiad4");
       DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(5,"Switch","eraaswiad5");
       def iterCount = 0;
       DatabaseAdapter adapter = new DatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, 0, TestLogUtils.log)
       adapter.executeQuery("select * from connectiontrials order by id", new Object[0], 2){Map record ->
           assertEquals(5, record.size())
           iterCount++;
           assertEquals(new BigDecimal(iterCount), record["ID"])           
           assertEquals("eraaswiad${iterCount}", record["INSTANCENAME"])
           assertEquals("Switch", record["CLASSNAME"])
       }
       assertEquals(5, iterCount)
    }
}