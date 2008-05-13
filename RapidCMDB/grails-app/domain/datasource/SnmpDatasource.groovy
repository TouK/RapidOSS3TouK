package datasource;
import connection.SnmpConnection
import com.ifountain.snmp.datasource.SnmpListeningAdapter
import org.apache.log4j.Logger
import script.CmdbScript
import com.ifountain.rcmdb.snmp.ScriptTrapProcessor

class SnmpDatasource extends BaseDatasource {
    public static def snmpListeningAdapters = [:];
    SnmpConnection connection;
    CmdbScript script;
    def scriptingService;
    static constraints = {
        script(blank: false);
    };
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
            listeningAdapter = new SnmpListeningAdapter(this.connection.host, this.connection.port, Logger.getRootLogger());
            listeningAdapter.addTrapProcessor(new ScriptTrapProcessor(this.script.name, this.scriptingService, Logger.getRootLogger()));
            listeningAdapter.open();
            snmpListeningAdapters.put(this.name, listeningAdapter);
        }
        else if(!listeningAdapter.isOpen()){
            listeningAdapter.removeAllTrapProcessors();
            listeningAdapter.addTrapProcessor(new ScriptTrapProcessor(this.script.name, this.scriptingService, Logger.getRootLogger()));
            listeningAdapter.open();
        }
    }

    def close() {
        snmpListeningAdapters[this.name]?.close();
    }
}
