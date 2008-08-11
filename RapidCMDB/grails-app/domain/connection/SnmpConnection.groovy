package connection

import datasource.SnmpDatasource;
class SnmpConnection extends Connection{
    static searchable = {
        except = [];
    };
    static cascaded = ["snmpDatasources":true]
    static datasources = [:]

    
    Long port =162;
    
    String host ="0.0.0.0";
    

    static hasMany = [snmpDatasources:SnmpDatasource]
    
    static constraints={
    port(nullable:true)
        
     host(blank:true,nullable:true)
        
     
    }

    static mappedBy=["snmpDatasources":"connection"]
    static belongsTo = []
    static transients = [];
}