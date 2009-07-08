package database.datasource


import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import datasource.DatabaseDatasource
import connection.DatabaseConnection
import datasource.DatabaseDatasourceOperations
import com.ifountain.core.connection.ConnectionManager
import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.core.test.util.DatasourceTestUtils
import com.ifountain.rcmdb.test.util.DatabaseConnectionImplTestUtils
import com.ifountain.rcmdb.converter.datasource.DatasourceConversionUtils
import com.ifountain.comp.converter.ConverterRegistry




/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 25, 2009
* Time: 9:52:05 AM
* To change this template use File | Settings | File Templates.
*/
class DatabaseDatasourceOperationsTest extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp();
        ConnectionManager.initialize(TestLogUtils.log, DatasourceTestUtils.getParamSupplier(), Thread.currentThread().getContextClassLoader(), 1000);
        DatasourceTestUtils.getParamSupplier().setParam(DatabaseConnectionImplTestUtils.getConnectionParam());
    }

    public void tearDown() {
        ConnectionManager.destroy();
        super.tearDown();
    }


    public void testOnLoadDoesNotThrowExceptionWhenDatasourceDoesNotHaveConnection()
    {
        initialize([DatabaseDatasource, DatabaseConnection], []);
        CompassForTests.addOperationSupport(DatabaseDatasource, DatabaseDatasourceOperations);


        def con = DatabaseConnection.add(name: "testcon", url: "u", username: "u", driver: "com.mysql.jdbc.Driver");
        assertFalse(con.errors.toString(), con.hasErrors());

        def newDs = DatabaseDatasource.add(name: "testds", connection: con);
        assertFalse(newDs.hasErrors());
        assertNotNull(newDs.adapter);

        newDs.removeRelation(connection: con);
        assertFalse(newDs.hasErrors());
        assertNull(newDs.connection);

        try {
            def dsFromRepo = DatabaseDatasource.get(name: newDs.name);
            assertNull(dsFromRepo.adapter);

        }
        catch (e)
        {
            e.printStackTrace();
            fail("Should not throw exception. Exception thrown is ${e}");
        }
    }

    public void testRunQueryWithFetchSizeApplyConversions() {
        DatasourceConversionUtils.registerDefaultConverters();
        try {
            DatabaseConnectionImplTestUtils.createTableConnectionTrials();
        } catch (ClassNotFoundException e) {
        }
        initialize([DatabaseDatasource, DatabaseConnection], []);
        CompassForTests.addOperationSupport(DatabaseDatasource, DatabaseDatasourceOperations);


        def con = DatabaseConnection.add(name: DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, url: "u", username: "u", driver: "com.mysql.jdbc.Driver");
        assertFalse(con.errors.toString(), con.hasErrors());

        def newDs = DatabaseDatasource.add(name: "testds", connection: con);
        assertFalse(newDs.hasErrors());

        DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(1, "Switch", "eraaswiad1");

        newDs.adapter.executeQuery("select * from connectiontrials order by id", new Object[0], 2) {Map record ->
            assertEquals(BigDecimal.class, record.ID.class);
        }
        newDs.runQuery("select * from connectiontrials order by id", new Object[0], 2) {Map record ->
            assertEquals(Double.class, record.ID.class);
        }
    }

}

