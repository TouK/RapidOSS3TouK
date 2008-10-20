package datasource

import connection.SmartsConnection

class SmartsNotificationDatasource extends BaseListeningDatasource{
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]

    
    SmartsConnection connection ;
    Long reconnectInterval = 0;
    

    static relations = [
            connection:[isMany:false, reverseName:"smartsNotificationDatasources", type:SmartsConnection]
    ]
    static constraints={
    connection(nullable:true)
        
     
    }
    static transients =  []
}
