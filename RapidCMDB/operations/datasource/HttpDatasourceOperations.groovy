package datasource
import datasource.HttpAdapter
import org.apache.log4j.Logger;
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 5:47:41 PM
 * To change this template use File | Settings | File Templates.
 */
class HttpDatasourceOperations extends BaseDatasourceOperations{


    def adapter;
    def onLoad = {
       this.adapter = new HttpAdapter(getProperty("connection").name, reconnectInterval*1000, Logger.getRootLogger());
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