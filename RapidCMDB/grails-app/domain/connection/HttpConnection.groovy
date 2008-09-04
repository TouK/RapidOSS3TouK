package connection

import datasource.HttpDatasource;
class HttpConnection extends Connection{
     

    static searchable = {
        except = ["httpDatasources"];
    };
    static cascaded = ["httpDatasources":true]
    static datasources = [:]

    
    String baseUrl ="";
    String connectionClass = "connection.HttpConnectionImpl";
    List httpDatasources = [];

    static relations = [
            httpDatasources:[isMany:true, reverseName:"connection", type:HttpDatasource]
    ]
    static constraints={
    baseUrl(blank:true,nullable:true)
        
     
    }
    static transients = [];

}
