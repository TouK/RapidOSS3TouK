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
        initialize([RsTopologyObject,RapidApplication,RsComputerSystem,RsLink], []);
        CompassForTests.addOperationSupport(RsTopologyObject,RsTopologyObjectOperations);
        RapidApplicationTestUtils.initializeRapidApplicationOperations (RapidApplication);
        RapidApplicationTestUtils.clearProcessors();
    }

    public void tearDown() {
        clearMetaClasses();
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

    public void testRetrieveVisibleProperties()
    {
       def object=RsComputerSystem.add(name:"firefortest",className:"Firewall");
       assertFalse(object.hasErrors());

       def childObject1=RsTopologyObject.add(name:"child1",className:"bclass",parentObjects:[object]);
       def childObject2=RsTopologyObject.add(name:"achild2",className:"bclass",parentObjects:[object]);
       def childObject3=RsTopologyObject.add(name:"child3",className:"aclass",parentObjects:[object]);

       def link1=RsLink.add(name:"linkfortest1",className:"Cable",connectedSystems:[object]);

       def visibleProperties=object.retrieveVisibleProperties();

       println "visibleProperties :"
       visibleProperties.keySet().sort().each{ propName ->
          println "${propName}: ${visibleProperties[propName]}"
       }

       assertEquals(object.name,visibleProperties.name);
       assertEquals(object.className,visibleProperties.className);
       assertEquals("",object.displayName);

       //local relation sorted
       assertEquals(3,visibleProperties.childObjects.size());
       assertEquals("child3",visibleProperties.childObjects[0].name);
       assertEquals("aclass",visibleProperties.childObjects[0].className);
       assertEquals("achild2",visibleProperties.childObjects[1].name);
       assertEquals("bclass",visibleProperties.childObjects[1].className);
       assertEquals("child1",visibleProperties.childObjects[2].name);
       assertEquals("bclass",visibleProperties.childObjects[2].className);

        //unexisting local relation is empty list
        assertEquals(0,visibleProperties.parentObjects.size());
        assertTrue(visibleProperties.parentObjects instanceof List);

       //test global local exclusion
       assertNull(visibleProperties.id);
       assertNull(visibleProperties.rsDatasource);

    }

     public void testGetVisiblePropertiesExcludesLocalProperties()
    {
       def object=RsComputerSystem.add(name:"firefortest",className:"Firewall");
       assertFalse(object.hasErrors());


       def visibleProperties=object.retrieveVisibleProperties();

       println "visibleProperties :"
       visibleProperties.keySet().sort().each{ propName ->
          println "${propName}: ${visibleProperties[propName]}"
       }

        //test global local exclusion
       assertNull(visibleProperties.id);
       assertNull(visibleProperties.rsDatasource);

       assertEquals([],visibleProperties.childObjects);  //empty local relation
       assertEquals([],visibleProperties.parentObjects);  //empty local relation

       //add childObjects for Local RsComputerSystem to exclusion
       RsTopologyObjectOperations.VISIBLE_EXCLUDED_LOCAL_PROPS_PER_CLASS.RsComputerSystem=["childObjects"];
       RsTopologyObjectOperations.VISIBLE_EXCLUDED_LOCAL_PROPS_PER_CLASS.RsLink=["parentObjects"];

       try{
           visibleProperties=object.retrieveVisibleProperties();
           assertNull(visibleProperties.childObjects);
           assertEquals([],visibleProperties.parentObjects);//excluded for Link so still exists
       }
       finally{
           RsTopologyObjectOperations.VISIBLE_EXCLUDED_LOCAL_PROPS_PER_CLASS.RsComputerSystem=[];
           RsTopologyObjectOperations.VISIBLE_EXCLUDED_LOCAL_PROPS_PER_CLASS.RsLink=[];
       }
    }




}