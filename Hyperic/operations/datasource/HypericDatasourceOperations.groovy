package datasource

import org.apache.log4j.Logger
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Nov 21, 2008
 * Time: 10:41:06 AM
 */
class HypericDatasourceOperations  extends BaseDatasourceOperations{
    def adapter;
    def onLoad(){
       this.adapter = new HypericAdapter(getProperty("connection").name, reconnectInterval*1000, Logger.getRootLogger());
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