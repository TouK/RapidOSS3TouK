package datasource;
import datasource.DatabaseAdapter
import org.apache.log4j.Logger
import connection.DatabaseConnection

class DatabaseDatasource extends BaseDatasource{
    DatabaseConnection connection;
    def adapter;
    static transients =  ['adapter']

    def onLoad = {
       this.adapter = new DatabaseAdapter(connection.name, 0, Logger.getRootLogger());
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
