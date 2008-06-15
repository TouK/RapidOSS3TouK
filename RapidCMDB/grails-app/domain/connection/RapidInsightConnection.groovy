package connection;

import datasource.*
class RapidInsightConnection extends Connection{
   
    
    
    static searchable = {
        except = [];
        
    };
    static cascaded = ["rapidInsightDatasources":true]
    static datasources = [:]

    
    String baseUrl ="";
    
    String userPassword ="";
    
    String username ="";
    String connectionClass = "connection.RapidInsightConnectionImpl";
    

   static hasMany = [rapidInsightDatasources:RapidInsightDatasource]
    
    static constraints={
    baseUrl(blank:true,nullable:true)
        
     userPassword(blank:true,nullable:true)
        
     username(blank:true,nullable:true)
        
     
    }

    static mappedBy=["rapidInsightDatasources":"connection"]
    static belongsTo = []
    static transients = [];
}
