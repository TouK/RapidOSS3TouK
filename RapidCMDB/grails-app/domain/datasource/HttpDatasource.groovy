package datasource

import connection.HttpConnection
class HttpDatasource extends BaseDatasource{
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]

    
    HttpConnection connection ;
    int reconnectInterval = 0;
    static relations = [
            connection:[isMany:false, reverseName:"httpDatasources", type:HttpConnection]
    ]
    static constraints={
    connection(nullable:false)
        
     
    }

    static transients = [];
}
