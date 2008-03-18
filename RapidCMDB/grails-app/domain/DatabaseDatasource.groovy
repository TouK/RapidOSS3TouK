import datasources.DatabaseAdapter
import org.apache.log4j.Logger

class DatabaseDatasource extends BaseDatasource{
    DatabaseConnection connection;
    def adapter;
    static transients =  ['adapter']

    
    def onLoad = {
       this.adapter = new DatabaseAdapter(connection.name, 0, Logger.getRootLogger());
    }
}
