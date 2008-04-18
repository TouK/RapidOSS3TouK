package datasource;
import datasource.SingleTableDatabaseAdapter
import org.apache.log4j.Logger
import connection.DatabaseConnection

class SingleTableDatabaseDatasource extends BaseDatasource{
    DatabaseConnection connection;
    String tableName;
    String tableKeys;
    def adapter;
    static transients =  ['adapter']
     static constraints = {
        tableName(blank:false);
        tableKeys(blank:false)
    };

    def onLoad = {
       this.adapter = new SingleTableDatabaseAdapter(connection.name, tableName, tableKeys, 0, Logger.getRootLogger());
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
        def props = adapter.getRecordMultiKey(keys, properties);
        return props;
     }


    public retrieveRecords(){
		return adapter.getRecords();
    }

    public retrieveRecords(List columnList){
          return adapter.getRecords(columnList);
    }

    public retrieveRecords(String whereclause){
        return adapter.getRecords(whereclause);
    }

    public retrieveRecords(whereClause, List columnList){
        return adapter.getRecords(whereClause, columnList);     
    }

}
