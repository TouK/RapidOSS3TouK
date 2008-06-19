package datasource

import com.ifountain.rcmdb.snmp.ScriptTrapProcessor
import com.ifountain.snmp.datasource.SnmpListeningAdapter
import connection.SnmpConnection
import org.apache.log4j.Logger
class SnmpDatasource extends BaseDatasource {
    public static def snmpListeningAdapters = [:];
     static searchable = {
        except = [];
    };
    static datasources = [:]

    
    SnmpConnection connection ;
    
    String scriptName = "";
    

    static hasMany = [:]
    
    static constraints={
    connection(nullable:true)
        
     scriptName(nullable:true, blank:true)
        
     
    }

    static mappedBy=["connection":"snmpDatasources"]
    static belongsTo = []
    def beforeDelete = {
        def listeningAdapter = snmpListeningAdapters.remove(this.name);
        if (listeningAdapter != null) {
            listeningAdapter.close();
        }
    }
    def isOpen() {
        def listeningAdapter = snmpListeningAdapters[this.name];
        if (listeningAdapter != null) {
            return listeningAdapter.isOpen();
        }
        return false;
    }

    def open() {
        def listeningAdapter = snmpListeningAdapters[this.name];
        if (listeningAdapter == null) {
            listeningAdapter = new SnmpListeningAdapter(this.connection.host, this.connection.port.intValue(), Logger.getRootLogger());
            listeningAdapter.addTrapProcessor(new ScriptTrapProcessor(this.scriptName, Logger.getRootLogger()));
            listeningAdapter.open();
            snmpListeningAdapters.put(this.name, listeningAdapter);
        }
        else if(!listeningAdapter.isOpen()){
            listeningAdapter.removeAllTrapProcessors();
            listeningAdapter.addTrapProcessor(new ScriptTrapProcessor(this.scriptName, Logger.getRootLogger()));
            listeningAdapter.open();
        }
    }

    def close() {
        snmpListeningAdapters[this.name]?.close();
    }
}
