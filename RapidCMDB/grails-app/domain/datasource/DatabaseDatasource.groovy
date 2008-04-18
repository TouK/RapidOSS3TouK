package datasource;
import datasource.DatabaseAdapter
import org.apache.log4j.Logger
import connection.DatabaseConnection

class DatabaseDatasource extends BaseDatasource{
    DatabaseConnection connection;
    def adapter;
    static transients =  ['adapter']
    
    def onLoad = {
       this.adapter = new DatabaseAdapter(connection.name, 0, Logger.getRootLogger());
    }
}
