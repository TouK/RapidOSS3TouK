package datasource;
import datasource.HttpAdapter
import org.apache.log4j.Logger
import connection.HttpConnection

class HttpDatasource extends BaseDatasource{
      HttpConnection connection;
      def adapter;
    static mapping = {
        tablePerHierarchy false
     }
    def onLoad = {
       this.adapter = new HttpAdapter(connection.name, 0, Logger.getRootLogger());
    }
}
