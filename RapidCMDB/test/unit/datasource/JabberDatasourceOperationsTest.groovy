package datasource


import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import connection.JabberConnection



/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 25, 2009
* Time: 9:52:05 AM
* To change this template use File | Settings | File Templates.
*/
class JabberDatasourceOperationsTest extends RapidCmdbWithCompassTestCase{

     public void setUp() {
        super.setUp();

    }

    public void tearDown() {
        super.tearDown();
    }


    public void testOnLoadDoesNotThrowExceptionWhenDatasourceDoesNotHaveConnection()
    {
         initialize([JabberDatasource,JabberConnection],[]);
         CompassForTests.addOperationSupport (JabberDatasource,JabberDatasourceOperations);


         def con=JabberConnection.add(name:"testcon",userPassword:"u",username:"u",serviceName:"s",host:"h");
         assertFalse(con.errors.toString(),con.hasErrors());

         def newDs=JabberDatasource.add(name:"testds",connection:con);
         assertFalse(newDs.hasErrors());
         assertNotNull(newDs.adapter);

         newDs.removeRelation(connection:con);
         assertFalse(newDs.hasErrors());
         assertNull(newDs.connection);

         try{
            def dsFromRepo=JabberDatasource.get(name:newDs.name);
            assertNull(dsFromRepo.adapter);

         }
         catch(e)
         {
             e.printStackTrace();
             fail("Should not throw exception. Exception thrown is ${e}");
         }


    }


}

