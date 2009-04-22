import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 21, 2009
* Time: 8:17:14 PM
* To change this template use File | Settings | File Templates.
*/
class RsGroupOperationsTest extends RapidCmdbWithCompassTestCase {
    static def classes = [:];
    public void setUp() {
        super.setUp();
        initializeClasses();
        clearMetaClasses();
        initialize([RsTopologyObject, RsObjectState, RsEvent,RsGroup,relation.Relation], []);
    }

    public void tearDown() {
        clearMetaClasses();
        RsGroupOperationsTest.clearClasses();
        super.tearDown();
    }
    public static void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsTopologyObject)
        GroovySystem.metaClassRegistry.removeMetaClass(RsGroup)
        RsGroupOperationsTest.classes.each {className, classInstance ->
            GroovySystem.metaClassRegistry.removeMetaClass(classInstance)
        }
        ExpandoMetaClass.enableGlobally();
    }
    public static void clearClasses()
    {
        classes=[:];
    }
    public def initializeClasses()
    {
        def classMap = [:];
        classMap.Constants = Constants;
        classMap.RsTopologyObjectOperations = RsTopologyObjectOperations;
        classMap.RsGroupOperations = RsGroupOperations;
        RsGroupOperationsTest.initializeClassesFrom(classMap);
    }
    public static def initializeClassesFrom(classesToLoad)
    {
        if(classes!=null)
        {
            classes.clear();
        }   
        classes = classesToLoad.clone();
    }
    static def getClasses()
    {
        return RsGroupOperationsTest.classes;
    }
   public void testDummy()
    {

    }



}