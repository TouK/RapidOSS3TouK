package datasource


import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import connection.AolConnection



/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 25, 2009
* Time: 9:52:05 AM
* To change this template use File | Settings | File Templates.
*/
class AolDatasourceOperationsTest extends RapidCmdbWithCompassTestCase{

     public void setUp() {
        super.setUp();

    }

    public void tearDown() {
        super.tearDown();
    }


    public void testOnLoadDoesNotThrowExceptionWhenDatasourceDoesNotHaveConnection()
    {
         initialize([AolDatasource,AolConnection],[]);
         CompassForTests.addOperationSupport (AolDatasource,AolDatasourceOperations);


         def con=AolConnection.add(name:"testcon",host:"u",username:"u",port:50);
         assertFalse(con.errors.toString(),con.hasErrors());

         def newDs=AolDatasource.add(name:"testds",connection:con);
         assertFalse(newDs.hasErrors());
         assertNotNull(newDs.adapter);

         newDs.removeRelation(connection:con);
         assertFalse(newDs.hasErrors());
         assertNull(newDs.connection);

         try{
            def dsFromRepo=AolDatasource.get(name:newDs.name);
            assertNull(dsFromRepo.adapter);

         }
         catch(e)
         {
             e.printStackTrace();
             fail("Should not throw exception. Exception thrown is ${e}");
         }


    }


}

