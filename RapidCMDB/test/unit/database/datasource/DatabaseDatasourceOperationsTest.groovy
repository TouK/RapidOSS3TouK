package database.datasource


import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import datasource.DatabaseDatasource
import connection.DatabaseConnection
import datasource.DatabaseDatasourceOperations




/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 25, 2009
* Time: 9:52:05 AM
* To change this template use File | Settings | File Templates.
*/
class DatabaseDatasourceOperationsTest extends RapidCmdbWithCompassTestCase{

     public void setUp() {
        super.setUp();

    }

    public void tearDown() {
        super.tearDown();
    }


    public void testOnLoadDoesNotThrowExceptionWhenDatasourceDoesNotHaveConnection()
    {
         initialize([DatabaseDatasource,DatabaseConnection],[]);
         CompassForTests.addOperationSupport (DatabaseDatasource,DatabaseDatasourceOperations);


         def con=DatabaseConnection.add(name:"testcon",url:"u",username:"u",driver:"com.mysql.jdbc.Driver");
         assertFalse(con.errors.toString(),con.hasErrors());

         def newDs=DatabaseDatasource.add(name:"testds",connection:con);
         assertFalse(newDs.hasErrors());
         assertNotNull(newDs.adapter);

         newDs.removeRelation(connection:con);
         assertFalse(newDs.hasErrors());
         assertNull(newDs.connection);

         try{
            def dsFromRepo=DatabaseDatasource.get(name:newDs.name);
            assertNull(dsFromRepo.adapter);

         }
         catch(e)
         {
             e.printStackTrace();
             fail("Should not throw exception. Exception thrown is ${e}");
         }


    }


}

