package datasource;
import datasources.RapidInsightAdapter
import org.apache.log4j.Logger
import connection.RapidInsightConnection

class RapidInsightDatasource {
    RapidInsightConnection connection;
    def adapter;

    def onLoad = {
       this.adapter = new RapidInsightAdapter(connection.name, 0, Logger.getRootLogger());
    }
}
