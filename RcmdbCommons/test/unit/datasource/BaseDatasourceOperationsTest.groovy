package datasource

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.core.datasource.BaseAdapter
import org.apache.log4j.Logger;

import com.ifountain.rcmdb.converter.datasource.StringConverter
import com.ifountain.comp.converter.ConverterRegistry
import com.ifountain.rcmdb.converter.datasource.DatasourceConversionUtils

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
        DatasourceConversionUtils.registerDefaultConverters();
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
         assertEquals(BaseDatasource.count(),0);
         def onDemandDs=BaseDatasource.getOnDemand(name:"testds");
         assertNull(onDemandDs);
     }
     public void testGetOnDemandDoesNotGenerateExceptionWhenGetAdaptersIsNull(){
         def ds=BaseDatasource.add(name:"testds");
         assertFalse(ds.hasErrors())
         assertNull(ds.getAdapters());

         def onDemandDs=BaseDatasource.getOnDemand(name:"testds");
         assertEquals(onDemandDs.name,ds.name);
     }

     public void testGetOnDemandSetsReconnectIntervalToZero()
     {
         def adapters = [new BaseDatasourceTestAdapter("",5,null), new BaseDatasourceTestAdapter("",5,null)];
         BaseDatasource.metaClass.getAdapters = {
             return adapters;
         }
         def ds=BaseDatasource.add(name:"testds");
         assertFalse(ds.hasErrors())
         assertSame(adapters, ds.getAdapters());
         ds.getAdapters().each{adapter->
            assertEquals(5, adapter.getReconnectInterval());    
         }


         def onDemandDs=BaseDatasource.getOnDemand(name:"testds");
         assertEquals(onDemandDs.name,ds.name);
         ds.getAdapters().each{adapter->
            assertEquals(0, adapter.getReconnectInterval());
         }
         
     }

     public void testGetOnDemandDoesNotGenerateExceptionWhenOneOfTheAdapterIsNullOrIsNotAnInstanceOfBaseAdapter(){
        def adapters = [null, new BaseDatasourceTestAdapter("",5,null), null, new Object(), new BaseDatasourceTestAdapter("",5,null)];
        BaseDatasource.metaClass.getAdapters = {
         return adapters;
        }
        def ds=BaseDatasource.add(name:"testds");
        assertFalse(ds.hasErrors())
        def onDemandDs=BaseDatasource.getOnDemand(name:"testds");
        assertEquals(onDemandDs.name,ds.name);
        assertEquals(0, adapters[1].getReconnectInterval());
        assertEquals(0, adapters[4].getReconnectInterval());
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
     public void testConvert()
     {
         def ds=BaseDatasource.add(name:"testds");
         assertFalse(ds.hasErrors())

         assertEquals(ds.convert(null),"") ;
         assertEquals(ds.convert("abc"),"abc") ;
         assertEquals(ds.convert(5),5) ;
     }
     public void testConvertWithDefault()
     {
         def ds=BaseDatasource.add(name:"testds");
         assertFalse(ds.hasErrors())

         assertEquals(ds.convertWithDefault(null,""),"") ;
         assertEquals(ds.convertWithDefault(null,"x"),"x") ;
         assertEquals(ds.convertWithDefault(null,[:]),[:]) ;
         assertEquals(ds.convertWithDefault(null,[]),[]) ;
         assertEquals(ds.convertWithDefault(null,["x":5]),["x":5]) ;
         
         assertEquals(ds.convertWithDefault("abc",""),"abc") ;
         assertEquals(ds.convertWithDefault(5,""),5) ;
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

