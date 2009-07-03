package database.datasource


import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import datasource.SingleTableDatabaseDatasource
import connection.DatabaseConnection
import datasource.SingleTableDatabaseDatasourceOperations




/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 25, 2009
* Time: 9:52:05 AM
* To change this template use File | Settings | File Templates.
*/
class SingleTableDatabaseDatasourceOperationsTest extends RapidCmdbWithCompassTestCase{

     public void setUp() {
        super.setUp();

    }

    public void tearDown() {
        super.tearDown();
    }


    public void testOnLoadDoesNotThrowExceptionWhenDatasourceDoesNotHaveConnection()
    {
         initialize([SingleTableDatabaseDatasource,DatabaseConnection],[]);
         CompassForTests.addOperationSupport (SingleTableDatabaseDatasource,SingleTableDatabaseDatasourceOperations);


         def con=DatabaseConnection.add(name:"testcon",url:"u",username:"u",driver:"d");
         assertFalse(con.errors.toString(),con.hasErrors());

         def newDs=SingleTableDatabaseDatasource.add(name:"testds",connection:con,tableName:"t",tableKeys:"k");
         assertFalse(newDs.hasErrors());
         assertNotNull(newDs.adapter);

         newDs.removeRelation(connection:con);
         assertFalse(newDs.hasErrors());
         assertNull(newDs.connection);

         try{
            def dsFromRepo=SingleTableDatabaseDatasource.get(name:newDs.name);
            assertNull(dsFromRepo.adapter);

         }
         catch(e)
         {
             e.printStackTrace();
             fail("Should not throw exception. Exception thrown is ${e}");
         }


    }


}

