package connection

import datasource.RapidInsightDatasource;

class RapidInsightConnection extends Connection{
   
    
    
    static searchable = {
        except = ["rapidInsightDatasources"];
        
    };
    static cascaded = ["rapidInsightDatasources":true]
    static datasources = [:]

    
    String baseUrl ="";
    
    String userPassword ="";
    
    String username ="";
    String connectionClass = "connection.RapidInsightConnectionImpl";
    List rapidInsightDatasources = [];

    static relations = [
            rapidInsightDatasources:[isMany:true, reverseName:"connection", type:RapidInsightDatasource]
    ]
    static constraints={
    baseUrl(blank:true,nullable:true)
        
     userPassword(blank:true,nullable:true)
        
     username(blank:true,nullable:true)
        
     
    }

    static transients = [];
}
