package datasource

import com.ifountain.snmp.datasource.SnmpListeningAdapter
import connection.SnmpConnection
import org.apache.log4j.Logger

class SnmpDatasource extends BaseListeningDatasource {
     static searchable = {
        except = [];
    };
    static datasources = [:]

    
    SnmpConnection connection ;
      

    static hasMany = [:]
    
    static constraints={
    connection(nullable:true)
     
    }

    static mappedBy=["connection":"snmpDatasources"]
    static belongsTo = []

    def getListeningAdapter(Map params){
         return new SnmpListeningAdapter(connection.name, 0, Logger.getRootLogger());
    }
}
