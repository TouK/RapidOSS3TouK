package datasource
import datasource.DatabaseAdapter
import org.apache.log4j.Logger;
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 5:46:48 PM
 * To change this template use File | Settings | File Templates.
 */
class DatabaseDatasourceOperations extends BaseDatasourceOperations{
    def adapter;
    def onLoad(){
       this.adapter = new DatabaseAdapter(getProperty("connection").name, reconnectInterval*1000, Logger.getRootLogger());
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