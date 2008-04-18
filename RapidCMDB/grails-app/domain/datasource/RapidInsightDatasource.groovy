package datasource;
import datasource.RapidInsightAdapter
import org.apache.log4j.Logger
import connection.RapidInsightConnection

class RapidInsightDatasource extends BaseDatasource{
    RapidInsightConnection connection;
    def adapter;
    static mapping = {
        tablePerHierarchy false
     }
    def onLoad = {
       this.adapter = new RapidInsightAdapter(connection.name, 0, Logger.getRootLogger());
    }
}
