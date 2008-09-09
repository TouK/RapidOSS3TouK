package datasource

import com.ifountain.snmp.datasource.SnmpListeningAdapter
import connection.SnmpConnection
import org.apache.log4j.Logger

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

    def getListeningAdapter(Map params){
         return new SnmpListeningAdapter(getProperty("connection").name, 0, Logger.getRootLogger());
    }
}
