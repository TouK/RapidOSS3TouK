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
    

   static hasMany = [singleTableDatabaseDatasources:SingleTableDatabaseDatasource, databaseDatasources:DatabaseDatasource]
    
    static constraints={
    url(blank:true,nullable:true)
        
     userPassword(blank:true,nullable:true)
        
     username(blank:true,nullable:true)
        
     driver(blank:true,nullable:true)
        
     
    }

    static mappedBy=["singleTableDatabaseDatasources":"connection", "databaseDatasources":"connection"]
    static belongsTo = []
    static transients = [];
}
