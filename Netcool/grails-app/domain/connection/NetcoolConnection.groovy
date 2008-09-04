package connection

import datasource.NetcoolDatasource
import com.ifountain.rcmdb.netcool.NetcoolConnectionImpl;
class NetcoolConnection extends DatabaseConnection{
    String driver = "com.sybase.jdbc2.jdbc.SybDriver";
    String host = "";
    Long port;
    String connectionClass = NetcoolConnectionImpl.class.name;
    static searchable = {
        except = ["netcoolDatasources"];
    };
    static cascaded = ["netcoolDatasources":true]
    static datasources = [:]

    List netcoolDatasources = [];
    static relations = [
            netcoolDatasources:[isMany:true, reverseName:"connection", type:NetcoolDatasource]
    ]
    static constraints={
        driver(blank:true,nullable:true, validator:{val, obj ->
            try
            {
                Class.forName(val)
            }
            catch(ClassNotFoundException e)
            {
                return 'database.driver.does.not.exist';
            }

        })
        host(blank:false,nullable:false)
        port(nullable:false)
    }
    public String getUrl()
    {
        return "jdbc:sybase:Tds:${host}:${port}/?LITERAL_PARAMS=true".toString()
    }
    static transients = [];
}
