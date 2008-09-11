package datasource

import connection.RapidInsightConnection

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
    static transients = [];
}
