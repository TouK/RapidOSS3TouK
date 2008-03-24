package datasource;
import datasources.TopologyAdapter
import org.apache.log4j.Logger
import connection.SmartsConnection

class SmartsTopologyDatasource extends BaseDatasource{
    SmartsConnection connection;
    def adapter;
    static transients =  ['adapter']


    def onLoad = {
       this.adapter = new TopologyAdapter(connection.name, 0, Logger.getRootLogger());
    }

    def getProperty(Map keys, String propName)
     {
         println keys;
         def prop = this.adapter.getObject(keys.CreationClassName, keys.Name, [propName]);
         if(prop)
         {
             return prop[propName];
         }
         return "";
     }

     def getProperties(Map keys, List properties)
     {
         return this.adapter.getObject(keys.CreationClassName, keys.Name, properties);
     }
}
