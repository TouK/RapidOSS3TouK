package database.connection

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import connection.DatabaseConnection

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 7, 2009
* Time: 4:20:46 PM
* To change this template use File | Settings | File Templates.
*/
class DatabaseConnectionTests extends RapidCmdbWithCompassTestCase{

     public void setUp() {
        super.setUp();
       initialize([DatabaseConnection],[]);

    }

    public void tearDown() {
        super.tearDown();
    }

    public void testAddDatabaseConnectionGeneratesErrorWhenDriverIsMissing()
    {
        //with non empty non existing driver
         def connection=DatabaseConnection.add(name:"testcon",driver:"missingDriver",url:"testurl",username:"testuser");

         assertTrue(connection.hasErrors());
         assertTrue("wrong error ${connection.errors}",connection.errors.toString().indexOf('database.driver.does.not.exist')>=0);
         assertEquals(0,DatabaseConnection.count());

         //with existing driver
         def connection2=DatabaseConnection.add(name:"testcon",driver:"com.mysql.jdbc.Driver",url:"testurl",username:"testuser");

         assertFalse(connection2.errors.toString(),connection2.hasErrors());
         assertEquals(1,DatabaseConnection.count());
         connection2.remove();
         assertEquals(0,DatabaseConnection.count());

         //with empty driver
         def connection3=DatabaseConnection.add(name:"testcon",driver:"",url:"testurl",username:"abc");

         assertTrue(connection3.hasErrors());
         assertTrue("wrong error ${connection3.errors}",connection3.errors.toString().indexOf('driver.blank.error')>=0);
         assertEquals(0,DatabaseConnection.count());
         
         //with null driver
         def connection4=DatabaseConnection.add(name:"testcon",driver:null,url:"testurl",username:"testuser");

         assertTrue(connection4.hasErrors());
         assertTrue("wrong error ${connection4.errors}",connection4.errors.toString().indexOf('driver.nullable.error')>=0);
         assertEquals(0,DatabaseConnection.count());

         //with driver parameter missing
         def connection5=DatabaseConnection.add(name:"testcon",url:"testurl",username:"testuser");

         assertTrue(connection5.hasErrors());
         assertTrue("wrong error ${connection5.errors}",connection5.errors.toString().indexOf('driver.blank.error')>=0);
         assertEquals(0,DatabaseConnection.count());
         



    }
}