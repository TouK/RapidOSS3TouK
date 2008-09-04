package datasource

import connection.RapidInsightConnection
import datasource.RapidInsightAdapter
import org.apache.log4j.Logger;
class RapidInsightDatasource extends BaseDatasource{
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]

    
    RapidInsightConnection connection ;
    int reconnectInterval = 0;
    
    static relations = [
            connection:[isMany:false, reverseName:"rapidInsightDatasources", type:RapidInsightConnection]
    ]
    static constraints={
    connection(nullable:true)
        
     
    }

    def adapter;
    static transients = ["adapter"];
    def onLoad = {
       this.adapter = new RapidInsightAdapter(connection.name, reconnectInterval*1000, Logger.getRootLogger());
    }
}
