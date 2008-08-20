package connection

import datasource.SmartsNotificationDatasource
import datasource.SmartsTopologyDatasource;

class SmartsConnection extends Connection{
    
    

     static searchable = {
        except = [];
    };
    static cascaded = ["smartsTopologyDatasources":true, "smartsNotificationDatasources":true]
    static datasources = [:]

    String connectionClass = "com.ifountain.smarts.connection.SmartsConnectionImpl";
    String username ="";
    
    String domain ="";
    
    String userPassword ="";
    
    String broker ="";
    List smartsTopologyDatasources = [];
    List smartsNotificationDatasources = [];
    

    static hasMany = [smartsTopologyDatasources:SmartsTopologyDatasource, smartsNotificationDatasources:SmartsNotificationDatasource]
    
    static constraints={
    username(blank:true,nullable:true)
        
     domain(blank:true,nullable:true)
        
     userPassword(blank:true,nullable:true)
        
     broker(blank:true,nullable:true)
        
     
    }

    static mappedBy=["smartsTopologyDatasources":"connection", "smartsNotificationDatasources":"connection"]
    static belongsTo = []
    static transients = [];
}
