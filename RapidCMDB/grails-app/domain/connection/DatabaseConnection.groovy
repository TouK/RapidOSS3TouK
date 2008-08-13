package connection

import datasource.DatabaseDatasource
import datasource.SingleTableDatabaseDatasource;
class DatabaseConnection extends Connection{

    static searchable = {
        except = [];
    };
    static cascaded = ["singleTableDatabaseDatasources":true, "databaseDatasources":true]
    static datasources = [:]

    String connectionClass = "connection.DatabaseConnectionImpl";
    String url ="";
    
    String userPassword ="";
    
    String username ="";
    
    String driver ="";
    List singleTableDatabaseDatasources = [];
    List databaseDatasources = [];

   static hasMany = [singleTableDatabaseDatasources:SingleTableDatabaseDatasource, databaseDatasources:DatabaseDatasource]
    
    static constraints={
        url(blank:false)
        userPassword(blank:true,  nullable:true)
        username(blank:false)
        driver(blank:false)
    }

    static mappedBy=["singleTableDatabaseDatasources":"connection", "databaseDatasources":"connection"]
    static belongsTo = []
    static transients = [];
}
