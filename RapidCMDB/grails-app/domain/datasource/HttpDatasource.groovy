package datasource;
import datasource.HttpAdapter
import org.apache.log4j.Logger
import connection.HttpConnection

class HttpDatasource extends BaseDatasource{
      HttpConnection connection;
      def adapter;
   
    def onLoad = {
       this.adapter = new HttpAdapter(connection.name, 0, Logger.getRootLogger());
    }

    def doRequest(String url, Map params, int type){
        return adapter.doRequest(url, params, type);
    }

    def doRequest(String url, Map params){
        return adapter.doRequest(url, params);
    }

    def doGetRequest(String url, Map params){
        return adapter.doGetRequest(url, params);
    }

    def doPostRequest(String url, Map params){
        return adapter.doPostRequest(url, params);
    }
}
