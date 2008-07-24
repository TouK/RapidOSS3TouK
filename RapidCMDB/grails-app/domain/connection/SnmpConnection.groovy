package connection

import datasource.SnmpDatasource;
class SnmpConnection extends Connection{
    static searchable = {
        except = [];
    };
    static cascaded = ["snmpDatasources":true]
    static datasources = [:]

    
    Long port =162;
    
    String host ="localhost";
    

    static hasMany = [snmpDatasources:SnmpDatasource]
    
    static constraints={
    port(blank:true,nullable:true)
        
     host(blank:true,nullable:true)
        
     
    }

    static mappedBy=["snmpDatasources":"connection"]
    static belongsTo = []
    static transients = [];
}