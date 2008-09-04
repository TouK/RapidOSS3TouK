package connection

import datasource.DatabaseDatasource
import datasource.SingleTableDatabaseDatasource;
class DatabaseConnection extends Connection{

    static searchable = {
        except = ["singleTableDatabaseDatasources", "databaseDatasources"];
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
    static relations = [
            singleTableDatabaseDatasources:[isMany:true, reverseName:"connection", type:SingleTableDatabaseDatasource],
            databaseDatasources:[isMany:true, reverseName:"connection", type:DatabaseDatasource]
    ]
    static constraints={
        url(blank:false)
        userPassword(blank:true,  nullable:true)
        username(blank:false)
        driver(blank:false)
    }

    static transients = [];
}
