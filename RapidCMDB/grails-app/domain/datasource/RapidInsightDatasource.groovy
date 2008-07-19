package datasource

import connection.RapidInsightConnection
import datasource.RapidInsightAdapter
import org.apache.log4j.Logger;
class RapidInsightDatasource extends BaseDatasource{
    static searchable = {
        except = [];
    };
    static datasources = [:]

    
    RapidInsightConnection connection ;
    int reconnectInterval = 0;
    

    static hasMany = [:]
    
    static constraints={
    connection(nullable:true)
        
     
    }

    static mappedBy=["connection":"rapidInsightDatasources"]
    static belongsTo = []
    def adapter;
    static transients = ["adapter"];
    def onLoad = {
       this.adapter = new RapidInsightAdapter(connection.name, reconnectInterval, Logger.getRootLogger());
    }
}
