package database.datasource

import com.ifountain.core.test.util.RapidCoreTestCase
import datasource.SingleTableDatabaseAdapter
import com.ifountain.rcmdb.test.util.DatabaseConnectionImplTestUtils
import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.core.test.util.DatasourceTestUtils
import datasource.DatabaseAdapter

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Apr 3, 2009
* Time: 11:19:04 AM
*/
class SingleTableDatabaseAdapterTests extends RapidCoreTestCase {


    protected void setUp() throws Exception {
        super.setUp();
        DatasourceTestUtils.getParamSupplier().setParam(DatabaseConnectionImplTestUtils.getConnectionParam());


        
        try {
            DatabaseConnectionImplTestUtils.createTableConnectionTrials();
        } catch (ClassNotFoundException e) {
        }
        try {
            DatabaseConnectionImplTestUtils.createTable("create table singletabletrials (name varchar(250) not null, surname varchar(250) not null, age int, city varchar(250), PRIMARY KEY (name, surname))")
        } catch (ClassNotFoundException e) {
        }
    }

    void testGetRecords() {
        DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        SingleTableDatabaseAdapter adapter = new SingleTableDatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, "connectiontrials", "id", 0, TestLogUtils.log)
        List results = adapter.getRecords();
        assertEquals(0, results.size());

        DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(1, "Switch", "eraaswiad");
        results = adapter.getRecords();
        assertEquals(1, results.size());
        assertEquals(5, results[0].size())
        assertEquals(new BigDecimal(1), results[0]["id"])
        assertEquals(new BigDecimal(1), results[0]["ID"])
        assertEquals("Switch", results[0]["CLASSNAME"])
        assertEquals("Switch", results[0]["classname"])
        assertEquals("eraaswiad", results[0]["instancename"])
        assertEquals("eraaswiad", results[0]["INSTANCENAME"])
    }

    void testGetRecordsWithColumnList() {
        DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        SingleTableDatabaseAdapter adapter = new SingleTableDatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, "connectiontrials", "id", 0, TestLogUtils.log)

        //invalid column throws exception
        try {
            adapter.getRecords(["invalidColumn"]);
            fail("should throw exception")
        }
        catch (java.sql.SQLException e) {
        }
        List results = adapter.getRecords(["classname"]);
        assertEquals(0, results.size());

        DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(1, "Switch", "eraaswiad");
        results = adapter.getRecords(["classname"]);
        assertEquals(1, results.size());
        def result = results[0];
        //key values are brought
        assertEquals(2, result.size())
        assertEquals("Switch", result["classname"]);
        assertEquals(new BigDecimal(1), result["id"]);
    }

    void testGetRecordsWithWhereClause() {
        DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        SingleTableDatabaseAdapter adapter = new SingleTableDatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, "connectiontrials", "id", 0, TestLogUtils.log)

        //invalid where clause throws exception
        try {
            adapter.getRecords("invalid where clause");
            fail("should throw exception")
        }
        catch (java.sql.SQLException e) {
        }
        List results = adapter.getRecords("classname = 'Switch'");
        assertEquals(0, results.size());

        DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(1, "Switch", "eraaswiad");
        DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(2, "Router", "errouter");
        results = adapter.getRecords("classname = 'Switch'");
        assertEquals(1, results.size());
        def result = results[0];
        assertEquals(5, result.size())
        assertEquals("Switch", result["classname"]);
        assertEquals(new BigDecimal(1), result["id"]);
        assertEquals("eraaswiad", result["instancename"]);
    }

    void testGetRecordsWithWhereClauseAndColumnList() {
        DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        SingleTableDatabaseAdapter adapter = new SingleTableDatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, "connectiontrials", "id", 0, TestLogUtils.log)

        //invalid column throws exception
        try {
            adapter.getRecords("classname = 'Switch'", ["invalidColumn"]);
            fail("should throw exception")
        }
        catch (java.sql.SQLException e) {
        }
        //invalid where clause throws exception
        try {
            adapter.getRecords("invalid where clause", []);
            fail("should throw exception")
        }
        catch (java.sql.SQLException e) {
        }
        List results = adapter.getRecords("classname = 'Switch'", []);
        assertEquals(0, results.size());

        DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(1, "Switch", "eraaswiad");
        DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(2, "Router", "errouter");
        results = adapter.getRecords("classname = 'Switch'", ["classname"]);
        assertEquals(1, results.size());
        def result = results[0];
        assertEquals(2, result.size())
        assertEquals("Switch", result["classname"]);
        assertEquals(new BigDecimal(1), result["id"]);
    }

    void testGetMultiKeyRecordWithOneKey() {
        DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        SingleTableDatabaseAdapter adapter = new SingleTableDatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, "connectiontrials", "id", 0, TestLogUtils.log)

        def result = adapter.getMultiKeyRecord(["id": 1])
        assertEquals(0, result.size())

        DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(1, "Switch", "eraaswiad");
        DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(2, "Router", "errouter");

        result = adapter.getMultiKeyRecord(["id": 1])
        assertEquals(5, result.size())
        assertEquals("Switch", result["classname"]);
        assertEquals(new BigDecimal(1), result["id"]);
        assertEquals("eraaswiad", result["instancename"]);

        result = adapter.getMultiKeyRecord(["id": 1], ["classname"])
        assertEquals(2, result.size())
        assertEquals("Switch", result["classname"]);
        assertEquals(new BigDecimal(1), result["id"]);

        result = adapter.getMultiKeyRecord(["id": "1"])
        assertEquals(5, result.size())
    }

    void testGetMultiKeyRecordWithMultipleKey() {
        def tableName = "singletabletrials";
        DatabaseConnectionImplTestUtils.clearTable(tableName)
        DatabaseAdapter dbAdapter = new DatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, 0, TestLogUtils.log);
        SingleTableDatabaseAdapter adapter = new SingleTableDatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, tableName, "name,surname", 0, TestLogUtils.log);

        def result = adapter.getMultiKeyRecord(["name": "sezgin", "surname": "kara"])
        assertEquals(0, result.size())

        dbAdapter.executeUpdate("insert into ${tableName} values (?, ?, ?, ?)", ["sezgin", "kara", 27, "ankara"] as Object[]);
        dbAdapter.executeUpdate("insert into ${tableName} values (?, ?, ?, ?)", ["abdurrahim", "eke", 26, "konya"] as Object[]);
        result = adapter.getMultiKeyRecord(["name": "sezgin", "surname": "kara"])
        assertEquals(4, result.size())
        assertEquals("sezgin", result["name"])
        assertEquals("kara", result["surname"])
        assertEquals("ankara", result["city"])
        assertEquals(new BigDecimal(27), result["age"])

        result = adapter.getMultiKeyRecord(["name": "sezgin", "surname": "kara"], ["city"])
        assertEquals(3, result.size())
        assertEquals("sezgin", result["name"])
        assertEquals("kara", result["surname"])
        assertEquals("ankara", result["city"])
    }

    void testRemoveMultiKeyRecordWithOneKey() {
        DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        SingleTableDatabaseAdapter adapter = new SingleTableDatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, "connectiontrials", "id", 0, TestLogUtils.log)
        try {
            assertFalse(adapter.removeMultiKeyRecord([id: 1]));
        }
        catch (java.sql.SQLException e) {
            fail("should not throw exception");
        }

        DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(1, "Switch", "eraaswiad");
        assertEquals(5, adapter.getMultiKeyRecord([id: 1]).size())

        assertTrue(adapter.removeMultiKeyRecord([id: 1]));
        assertEquals(0, adapter.getMultiKeyRecord([id: 1]).size())

    }

    void testRemoveMultiKeyRecordWithMultipleKey() {
        def tableName = "singletabletrials";
        DatabaseConnectionImplTestUtils.clearTable(tableName)
        DatabaseAdapter dbAdapter = new DatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, 0, TestLogUtils.log);
        SingleTableDatabaseAdapter adapter = new SingleTableDatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, tableName, "name,surname", 0, TestLogUtils.log);

        assertFalse(adapter.removeMultiKeyRecord([name: "sezgin", surname: "kara"]));
        dbAdapter.executeUpdate("insert into ${tableName} values (?, ?, ?, ?)", ["sezgin", "kara", 27, "ankara"] as Object[]);
        assertEquals(4, adapter.getMultiKeyRecord(["name": "sezgin", "surname": "kara"]).size());

        assertTrue(adapter.removeMultiKeyRecord([name: "sezgin", surname: "kara"]))
        assertEquals(0, adapter.getMultiKeyRecord(["name": "sezgin", "surname": "kara"]).size());
    }

    void testAddRecordWithOneKey(){
        DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        SingleTableDatabaseAdapter adapter = new SingleTableDatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, "connectiontrials", "id", 0, TestLogUtils.log)
        //when one of key is not specified it throws exception
        try{
            adapter.addRecord([:])
            fail("should throw exception");
        }
        catch(Exception e){
            assertEquals("No value supplied for one of the primary key fields: id",  e.getMessage());
        }

        //throw exception if could not add to table, (classname not null)
        try{
            adapter.addRecord([id:1])
            fail("should throw exception")
        }
        catch(java.sql.SQLException e){
        }
        //throws exception when an invalid parameter is given
        try{
            adapter.addRecord([id:"invalidid", classname:"Switch", instancename:"eraaswiad"]);
            fail("should throw exception")
        }
        catch(java.sql.SQLException e){
        }

        adapter.addRecord([id:1, classname:"Switch", instancename:"eraaswiad"]);
        assertEquals(5, adapter.getMultiKeyRecord([id:1]).size());

        //updates if record exists
        adapter.addRecord([id:1, classname:"Router", instancename:"eraaswiad"]);
        assertEquals(1, adapter.getRecords().size())
        def record = adapter.getMultiKeyRecord([id:1]);
        assertEquals(5, record.size());
        assertEquals("Router", record["classname"]);
    }

     void testAddRecordWithMultipleKey(){
        def tableName = "singletabletrials";
        DatabaseConnectionImplTestUtils.clearTable(tableName)
        SingleTableDatabaseAdapter adapter = new SingleTableDatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, tableName, "name,surname", 0, TestLogUtils.log);

        adapter.addRecord([name:"sezgin", surname:"kara", age:27, city:"ankara"]);
        assertEquals(4, adapter.getMultiKeyRecord([name:"sezgin", surname:"kara"]).size());

        adapter.addRecord([name:"sezgin", surname:"kara", age:28, city:"istanbul"]);
        assertEquals(1, adapter.getRecords().size())
        def record = adapter.getMultiKeyRecord([name:"sezgin", surname:"kara"])
        assertEquals(4, record.size());
        assertEquals(new BigDecimal(28), record["age"])
        assertEquals("istanbul", record["city"])
     }

     void testUpdateRecordWithOneKey(){
        DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        SingleTableDatabaseAdapter adapter = new SingleTableDatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, "connectiontrials", "id", 0, TestLogUtils.log)
        //when one of key is not specified it throws exception
        try{
            adapter.updateRecord([:])
            fail("should throw exception");
        }
        catch(Exception e){
            assertEquals("No value supplied for one of the primary key fields: id",  e.getMessage());
        }

        //returns null when no property other than keys is specified
        assertNull(adapter.updateRecord([id:1]));

        //returns null if record does not exist
         assertNull(adapter.updateRecord([id:1, classname:"Router"]));

        adapter.addRecord([id:1, classname:"Switch", instancename:"eraaswiad"]);
        def record = adapter.updateRecord([id:1, classname:"Router"]);
        assertNotNull(record)
        assertEquals(new BigDecimal(1), record["id"]);
        assertEquals("Router", record["classname"]);
     }

     void testUpdateRecordWithMultipleKeys(){
         def tableName = "singletabletrials";
        DatabaseConnectionImplTestUtils.clearTable(tableName)
        SingleTableDatabaseAdapter adapter = new SingleTableDatabaseAdapter(DatabaseConnectionImplTestUtils.DATABASE_CONN_NAME, tableName, "name,surname", 0, TestLogUtils.log);

        //when one of key is not specified it throws exception
        try{
            adapter.updateRecord([:])
            fail("should throw exception");
        }
        catch(Exception e){
            assertEquals("No value supplied for one of the primary key fields: name",  e.getMessage());
        }

        //returns null when no property other than keys is specified
        assertNull(adapter.updateRecord([name:"sezgin", surname:"kara"]));

        //returns null if record does not exist
         assertNull(adapter.updateRecord([name:"sezgin", surname:"kara", age:22]));

        adapter.addRecord([name:"sezgin", surname:"kara", age:28]);
        def record = adapter.updateRecord([name:"sezgin", surname:"kara", age:22, city:"ankara"]);
        assertNotNull(record)
        assertEquals("sezgin", record["name"]);
        assertEquals("kara", record["surname"]);
        assertEquals(new BigDecimal(22), record["age"]);
        assertEquals("ankara", record["city"]);

     }
}