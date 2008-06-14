package datasource;
import connection.SnmpConnection
import com.ifountain.snmp.datasource.SnmpListeningAdapter
import org.apache.log4j.Logger
import script.CmdbScript
import com.ifountain.rcmdb.snmp.ScriptTrapProcessor

class SnmpDatasource extends BaseDatasource {
    public static def snmpListeningAdapters = [:];
     static searchable = {
        except = [];
    };
    static datasources = [:]

    
    SnmpConnection connection ;
    
    CmdbScript script ;
    

    static hasMany = [:]
    
    static constraints={
    connection(nullable:true)
        
     script(nullable:true)
        
     
    }

    static mappedBy=["connection":"snmpDatasources", "script":"snmpDatasources"]
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
            listeningAdapter.addTrapProcessor(new ScriptTrapProcessor(this.script.name, Logger.getRootLogger()));
            listeningAdapter.open();
            snmpListeningAdapters.put(this.name, listeningAdapter);
        }
        else if(!listeningAdapter.isOpen()){
            listeningAdapter.removeAllTrapProcessors();
            listeningAdapter.addTrapProcessor(new ScriptTrapProcessor(this.script.name, Logger.getRootLogger()));
            listeningAdapter.open();
        }
    }

    def close() {
        snmpListeningAdapters[this.name]?.close();
    }
}
