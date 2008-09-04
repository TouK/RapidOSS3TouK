package connection

import datasource.SnmpDatasource;
class SnmpConnection extends Connection{
    static searchable = {
        except = ["snmpDatasources"];
    };
    static cascaded = ["snmpDatasources":true]
    static datasources = [:]

    
    Long port =162;
    String connectionClass = "com.ifountain.snmp.connection.SnmpConnectionImpl";
    String host ="0.0.0.0";
    List snmpDatasources = [];

    static relations = [
            snmpDatasources:[isMany:true, reverseName:"connection", type:SnmpDatasource]
    ]
    static constraints={
    port(nullable:true)
        
     host(blank:true,nullable:true)
        
     
    }

    static transients = [];
}