package connection

import datasource.HttpDatasource;
class HttpConnection extends Connection{
     

    static searchable = {
        except = [];
    };
    static cascaded = ["httpDatasources":true]
    static datasources = [:]

    
    String baseUrl ="";
    String connectionClass = "connection.HttpConnectionImpl";
    List httpDatasources = [];

    static hasMany = [httpDatasources:HttpDatasource]
    
    static constraints={
    baseUrl(blank:true,nullable:true)
        
     
    }

    static mappedBy=["httpDatasources":"connection"]
    static belongsTo = []
    static transients = [];

}
