package datasource

import connection.SmartsConnection

class SmartsTopologyDatasource extends BaseListeningDatasource{
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]

    
    SmartsConnection connection ;
    Long reconnectInterval = 0;
    

    static relations = [
            connection:[isMany:false, reverseName:"smartsTopologyDatasources", type:SmartsConnection]
    ]
    static constraints={
    connection(nullable:true)
        
     
    }


    static transients =  []
}
