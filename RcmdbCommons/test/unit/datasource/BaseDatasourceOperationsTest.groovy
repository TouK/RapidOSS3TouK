package datasource

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.core.datasource.BaseAdapter
import org.apache.log4j.Logger;

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
        clearMetaClasses();
        initialize();

    }

    public void tearDown() {
        super.tearDown();
        clearMetaClasses();
    }
    private void clearMetaClasses()
    {           
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(BaseDatasource);
        GroovySystem.metaClassRegistry.removeMetaClass(BaseDatasourceOperations);
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
     public void testGetOnDemandDoesNotGenerateExceptionWhenAdapterIsNull(){
         def ds=BaseDatasource.add(name:"testds");
         assertFalse(ds.hasErrors())
         assertNull(ds.adapter);

         def onDemandDs=BaseDatasource.getOndemand(name:"testds");
         assertEquals(onDemandDs.name,ds.name);
         assertNull(onDemandDs.adapter);
     }
     public void testGetOnDemandSetsReconnectIntervalToZero()
     {
         BaseDatasource.metaClass.onLoad={ ->
            adapter = new BaseDatasourceTestAdapter("",5,null);
         }
         def ds=BaseDatasource.add(name:"testds");
         assertFalse(ds.hasErrors())
         assertNotNull (ds.adapter);
         assertEquals(ds.adapter.getReconnectInterval(),5);

         def onDemandDs=BaseDatasource.getOndemand(name:"testds");
         assertEquals(onDemandDs.name,ds.name);
         assertEquals(onDemandDs.adapter.getReconnectInterval(),0);

         
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

class BaseDatasourceTestAdapter extends BaseAdapter{
     public BaseDatasourceTestAdapter(String connectionName, long reconnectInterval, Logger logger) {
        super(connectionName, reconnectInterval, logger);
    }
    public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) {
        return null; 
    }
}

