package datasource

import connection.DatabaseConnection
import datasource.DatabaseAdapter
import org.apache.log4j.Logger;
class DatabaseDatasource extends BaseDatasource{
    static searchable = {
        except = [];
    };
    static datasources = [:]

    
    DatabaseConnection connection ;
    int reconnectInterval = 0;
    

    static hasMany = [:]
    
    static constraints={
    connection(nullable:true)
        
     
    }

    static mappedBy=["connection":"databaseDatasources"]
    static belongsTo = []
    def adapter;
    static transients =  ['adapter']

    def onLoad = {
       this.adapter = new DatabaseAdapter(connection.name, reconnectInterval*1000, Logger.getRootLogger());
    }
    def runUpdate(sql){
        return this.adapter.executeUpdate(sql, []);
    }
    def runUpdate(sql, queryParams){
        return this.adapter.executeUpdate(sql, queryParams);
    }
    def runQuery(sql){
        return this.adapter.executeQuery(sql, []);
    }
    def runQuery(sql,  queryParams){
        return this.adapter.executeQuery(sql, queryParams);
    }
    def runQuery(sql,  queryParams, fetchSize){
        return this.adapter.executeQuery(sql, queryParams, fetchSize);
    }
}
