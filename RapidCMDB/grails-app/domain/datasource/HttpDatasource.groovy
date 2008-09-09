package datasource

import connection.HttpConnection
import datasource.HttpAdapter
import org.apache.log4j.Logger;
class HttpDatasource extends BaseDatasource{
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]

    
    HttpConnection connection ;
    int reconnectInterval = 0;
    def adapter;

    static relations = [
            connection:[isMany:false, reverseName:"httpDatasources", type:HttpConnection]
    ]
    static constraints={
    connection(nullable:true)
        
     
    }

    static transients = ["adapter"];
    
     
   
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
