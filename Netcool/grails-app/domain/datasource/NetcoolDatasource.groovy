package datasource

import connection.NetcoolConnection

class NetcoolDatasource extends BaseDatasource{
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]


    NetcoolConnection connection ;


    static relations = [
            connection:[isMany:false, reverseName:"netcoolDatasources", type:NetcoolConnection]
    ]
    static constraints={
    connection(nullable:true)


    }
    static transients = []
}
