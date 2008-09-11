package datasource

import connection.SnmpConnection

class SnmpDatasource extends BaseListeningDatasource {
     static searchable = {
        except = ["connection"];
    };
    static datasources = [:]

    
    SnmpConnection connection ;
      
    static relations = [
            connection:[isMany:false, reverseName:"snmpDatasources", type:SnmpConnection]
    ]
    static constraints={
    connection(nullable:true)
     
    }
}
