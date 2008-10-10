package datasource

import connection.DatabaseConnection
class SingleTableDatabaseDatasource extends BaseDatasource{
    static searchable = {
        except = ["connection", 'records'];
    };
    static datasources = [:]

    
    String tableKeys ="";
    
    String tableName ="";
    
    DatabaseConnection connection ;
    int reconnectInterval = 0;
    

    static relations = [
            connection:[isMany:false, reverseName:"singleTableDatabaseDatasources", type:DatabaseConnection]
    ]
    
    static constraints={
    tableKeys(blank:false,nullable:false)
        
     tableName(blank:false,nullable:false)
        
     connection(nullable:true)
        
     
    }
    static transients =  ['records']
}
