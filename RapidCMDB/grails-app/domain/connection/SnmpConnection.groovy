package connection;
import datasource.*
class SnmpConnection extends Connection{
    static searchable = {
        except = [];
    };
    static cascaded = ["snmpDatasources":true]
    static datasources = [:]

    
    Long port =0;
    
    String host ="";
    

    static hasMany = [snmpDatasources:SnmpDatasource]
    
    static constraints={
    port(blank:true,nullable:true)
        
     host(blank:true,nullable:true)
        
     
    }

    static mappedBy=["snmpDatasources":"connection"]
    static belongsTo = []
    static transients = [];
}