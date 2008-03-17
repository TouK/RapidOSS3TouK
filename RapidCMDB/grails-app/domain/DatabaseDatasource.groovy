import datasources.SingleTableDatabaseAdapter
import org.apache.log4j.Logger

class DatabaseDatasource extends BaseDatasource{
    DatabaseConnection connection;
    String tableName;
    String keys;
    def adapter;
    static transients =  ['adapter']
    

    def onLoad = {
       this.adapter = new SingleTableDatabaseAdapter(connection.name, tableName, keys, 0, Logger.getRootLogger());
    }

    def getProperty(Map keys, String propName)
    {
        def props = adapter.getRecordMultiKey(keys, [propName]);
        if(props)
        {
            return props[propName];
        }
        return "";
    }

     def getProperties(Map keys, List properties)
     {
        def props = adapter.getRecordMultiKey(keys, [propName]);
        return props;
     }

}
