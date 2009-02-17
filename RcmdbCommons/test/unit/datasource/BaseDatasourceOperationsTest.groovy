package datasource

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 13, 2009
* Time: 11:28:17 AM
* To change this template use File | Settings | File Templates.
*/
class BaseDatasourceOperationsTest extends RapidCmdbWithCompassTestCase{

     public void setUp() {
        super.setUp();
        //clearMetaClasses();
        initialize();
    }

    public void tearDown() {
        super.tearDown();
    }
    private void clearMetaClasses()
    {
        ListeningAdapterManager.destroyInstance();
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptScheduler)
        ExpandoMetaClass.enableGlobally();
    }
    void initialize(){
       
        initialize([BaseDatasource], []);        
        CompassForTests.addOperationSupport (BaseDatasource, BaseDatasourceOperations);
     }
     public void testGetOnDemandReturnsNullWhenNoDatasourceIsFound(){
         assertEquals(BaseDatasource.list().size(),0);
         def onDemandDs=BaseDatasource.getOndemand(name:"testds");
         assertNull(onDemandDs);
     }
     public void testGetOnDemand()
     {
         def ds=BaseDatasource.add(name:"testds");
         assertFalse(ds.hasErrors())
         assertNull(ds.adapter);

         def onDemandDs=BaseDatasource.getOndemand(name:"testds");
         assertEquals(onDemandDs.name,ds.name);
         assertNull(onDemandDs.adapter);
     }

     public void testGetPropertyReturnsNull()
     {
        def ds=BaseDatasource.add(name:"testds");
        assertFalse(ds.hasErrors())

        assertNull(ds.getProperty(null,null));
        assertNull(ds.getProperty([:],""));
        assertNull(ds.getProperty(["name":"name"],"name"));
     }

      public void testGetPropertiesReturnsNull()
     {
        def ds=BaseDatasource.add(name:"testds");
        assertFalse(ds.hasErrors())

        assertNull(ds.getProperties(null,null));
        assertNull(ds.getProperties([:],[]));
        assertNull(ds.getProperties(["name":"name"],["name"]));
     }

}