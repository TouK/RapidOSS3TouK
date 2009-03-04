import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 21, 2009
* Time: 8:17:14 PM
* To change this template use File | Settings | File Templates.
*/
class RsTopologyObjectOperationsTest extends RapidCmdbWithCompassTestCase{
     public void setUp() {
        super.setUp();

    }

    public void tearDown() {
        super.tearDown();
    }

     public void testRemoveDeletesRsObjectStateInstance()
     {
         initialize([RsTopologyObject,RsObjectState,RsEvent], []);
         CompassForTests.addOperationSupport(RsTopologyObject,RsTopologyObjectOperations);

         def object=RsTopologyObject.add(name:"testobject");
         assertFalse(object.hasErrors());

         assertEquals(0,RsObjectState.list().size());

         object.getState();
         assertEquals(1,RsObjectState.list().size());

         object.remove();

         assertEquals(0,RsObjectState.list().size());
         
     }

}