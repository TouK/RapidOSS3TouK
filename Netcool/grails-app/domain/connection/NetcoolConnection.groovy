package connection

import datasource.NetcoolDatasource
import com.ifountain.rcmdb.netcool.NetcoolConnectionImpl;
class NetcoolConnection extends DatabaseConnection{
    String driver = "com.sybase.jdbc2.jdbc.SybDriver";
    String host = "";
    Long port;
    String connectionClass = NetcoolConnectionImpl.class.name;
    static searchable = {
        except = [];
    };
    static cascaded = ["netcoolDatasources":true]
    static datasources = [:]


     static hasMany = [netcoolDatasources:NetcoolDatasource]

    static constraints={
        driver(blank:true,nullable:true)
        host(blank:false,nullable:false)
        port(blank:false,nullable:false)
    }
    public String getUrl()
    {
        return "jdbc:sybase:Tds:${host}:${port}/?LITERAL_PARAMS=true".toString()
    }
    static mappedBy=["netcoolDatasources":"connection"]
    static belongsTo = []
    static transients = [];
}
