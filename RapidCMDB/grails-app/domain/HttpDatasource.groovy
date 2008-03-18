import datasources.HttpAdapter
import org.apache.log4j.Logger
class HttpDatasource extends BaseDatasource{
      HttpConnection connection;
      def adapter;

    def onLoad = {
       this.adapter = new HttpAdapter(connection.name, 0, Logger.getRootLogger());
    }
}
