package datasource


import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import connection.SametimeConnection



/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 25, 2009
* Time: 9:52:05 AM
* To change this template use File | Settings | File Templates.
*/
class SametimeDatasourceOperationsTest extends RapidCmdbWithCompassTestCase{

     public void setUp() {
        super.setUp();

    }

    public void tearDown() {
        super.tearDown();
    }


    public void testOnLoadDoesNotThrowExceptionWhenDatasourceDoesNotHaveConnection()
    {
         initialize([SametimeDatasource,SametimeConnection],[]);
         CompassForTests.addOperationSupport (SametimeDatasource,SametimeDatasourceOperations);


         def con=SametimeConnection.add(name:"testcon",host:"u",username:"u");
         assertFalse(con.errors.toString(),con.hasErrors());

         def newDs=SametimeDatasource.add(name:"testds",connection:con);
         assertFalse(newDs.hasErrors());
         assertNotNull(newDs.adapter);

         newDs.removeRelation(connection:con);
         assertFalse(newDs.hasErrors());
         assertNull(newDs.connection);

         try{
            def dsFromRepo=SametimeDatasource.get(name:newDs.name);
            assertNull(dsFromRepo.adapter);

         }
         catch(e)
         {
             e.printStackTrace();
             fail("Should not throw exception. Exception thrown is ${e}");
         }


    }


}

