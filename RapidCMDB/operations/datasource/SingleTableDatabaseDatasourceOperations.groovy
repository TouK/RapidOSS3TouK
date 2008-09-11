package datasource
import datasource.SingleTableDatabaseAdapter
import org.apache.log4j.Logger;
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 5:49:29 PM
 * To change this template use File | Settings | File Templates.
 */
class SingleTableDatabaseDatasourceOperations extends BaseDatasourceOperations{

    def adapter;
    def onLoad = {
       this.adapter = new SingleTableDatabaseAdapter(getProperty("connection").name, tableName, tableKeys, reconnectInterval*1000, Logger.getRootLogger());
    }

    def getProperty(Map keys, String propName){
        def props = adapter.getMultiKeyRecord(keys, [propName]);
        if(props)
        {
            return props[propName];
        }
        return "";
    }

    def getProperties(Map keys, List properties){
       def props = adapter.getMultiKeyRecord(keys, properties);
       return props;
    }

    def getRecord(keyValue){
        return adapter.getRecord(keyValue);
    }

    def getRecord(keyValue, columnList){
	    return adapter.getRecord(keyValue, columnList);
    }

    def getMultiKeyRecord(keyMap){
        return adapter.getMultiKeyRecord(keymap);
    }

    def getMultiKeyRecord(Map keyMap, columnList){
        return adapter.getMultiKeyRecord(keymap, columnList);
    }

    def getRecords(){
		return adapter.getRecords();
    }

    def getRecords(List columnList){
          return adapter.getRecords(columnList);
    }

    def getRecords(String whereclause){
        return adapter.getRecords(whereclause);
    }

    def getRecords(whereClause, List columnList){
        return adapter.getRecords(whereClause, columnList);
    }

    def addRecord(Map fields){
        return adapter.addRecord(fields);
    }

	def updateRecord(Map fields){
        return adapter.updateRecord(fields);
    }

	boolean removeRecord(keyValue){
		return adapter.removeRecord(keyValue);
	}

	boolean removeMultiKeyRecord(Map keyMap){
		return adapter.removeMultiKeyRecord(keyMap);
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