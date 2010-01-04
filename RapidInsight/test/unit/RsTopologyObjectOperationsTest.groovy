import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidApplicationTestUtils
import application.RapidApplication

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
        clearMetaClasses();
        initialize([RsTopologyObject,RapidApplication], []);
        CompassForTests.addOperationSupport(RsTopologyObject,RsTopologyObjectOperations);
        RapidApplicationTestUtils.initializeRapidApplicationOperations (RapidApplication);
        RapidApplicationTestUtils.clearProcessors();
    }

    public void tearDown() {
       super.tearDown();       
    }
    public void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsTopologyObject);
        GroovySystem.metaClassRegistry.removeMetaClass(RsTopologyObjectOperations);
        ExpandoMetaClass.enableGlobally();
    }
    public void testGetState()
    {
        def object=RsTopologyObject.add(name:"testobj");
        assertFalse(object.hasErrors());

        assertEquals(Constants.NORMAL,object.getState())
    }






}