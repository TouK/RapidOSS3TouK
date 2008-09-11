package datasource

import connection.DatabaseConnection
class DatabaseDatasource extends BaseDatasource{
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]

    
    DatabaseConnection connection ;
    int reconnectInterval = 0;
    

    static relations = [
            connection:[isMany:false, reverseName:"databaseDatasources", type:DatabaseConnection]
    ]
    static constraints={
    connection(nullable:true)
        
     
    }


    static transients =  []
}
