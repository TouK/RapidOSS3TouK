import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RsUtilityTestUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 21, 2009
* Time: 8:17:14 PM
* To change this template use File | Settings | File Templates.
*/
class RsTopologyObjectOperationsTest extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp();
        initialize([RsTopologyObject,RsUtility], []);
        CompassForTests.addOperationSupport(RsTopologyObject,RsTopologyObjectOperations);
        RsUtilityTestUtils.initializeRsUtilityOperations (RsUtility);
    }

    public void tearDown() {
       super.tearDown();
    }

    public void testGetState()
    {
        def object=RsTopologyObject.add(name:"testobj");
        assertFalse(object.hasErrors());

        assertEquals(Constants.NORMAL,object.getState())
    }






}