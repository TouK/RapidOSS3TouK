package connection

import datasource.NetcoolDatasource
import com.ifountain.rcmdb.netcool.NetcoolConnectionImpl;
class NetcoolConnection extends DatabaseConnection{
    String driver = "com.sybase.jdbc2.jdbc.SybDriver";
    String connectionClass = NetcoolConnectionImpl.class.name;
    static searchable = {
        except = [];
    };
    static cascaded = ["netcoolDatasources":true]
    static datasources = [:]


     static hasMany = [netcoolDatasources:NetcoolDatasource]

    static constraints={
    driver(blank:true,nullable:true)


    }

    static mappedBy=["netcoolDatasources":"connection"]
    static belongsTo = []
    static transients = [];
}
