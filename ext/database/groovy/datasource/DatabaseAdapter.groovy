package datasource

import com.ifountain.comp.utils.CaseInsensitiveMap
import com.ifountain.core.datasource.BaseAdapter
import java.sql.ResultSet
import org.apache.log4j.Logger;

public class DatabaseAdapter extends BaseAdapter {
    
    public DatabaseAdapter(){
    	super();
    }
    
    public DatabaseAdapter(String datasourceName, long reconnectInterval, Logger logger) {
        super(datasourceName, reconnectInterval, logger);
    }
    
    public static getInstance(datasourceName, tableName, keys){
    	return new SingleTableDatabaseAdapter(datasourceName, tableName, keys, 0, Logger.getRootLogger());
    }

    public static getInstance(datasourceName){
    	return new DatabaseAdapter(datasourceName, 0, Logger.getRootLogger());
    }    
    
    public int executeUpdate(sql) throws Exception{
        return executeUpdate(sql, []);
    }
    
    public int executeUpdate(sql, queryParams) throws Exception{
        ExecuteUpdateAction action = new ExecuteUpdateAction(logger, sql, (Object[]) queryParams);
        executeAction(action);
        return action.getAffectedRowCount();
    }
 
    public List executeQuery(sql,  queryParams) throws Exception{
        List results = [];
        def rset = executeQuery(sql,  queryParams, 0);
        def metaData = rset.getMetaData();
        def colCount = metaData.getColumnCount();
        while(rset.next())
        {
            def record = new CaseInsensitiveMap();
	        for(int i=1; i <= colCount; i++)
	        {
                String fieldvalue=null;
                if(rset.getObject(i) instanceof Date)
                {
                    fieldvalue=String.valueOf(rset.getObject(i).getTime()) 
                }
                else
                {
                   fieldvalue=String.valueOf(rset.getObject(i)).trim();
                }
                
                record.put(metaData.getColumnName(i).toUpperCase(), fieldvalue);
	        }
	        results += record;
	    }
	    
	    return results;
    }

    public ResultSet executeQuery(sql,  queryParams, fetchSize) throws Exception{
        ExecuteQueryAction action = new ExecuteQueryAction(logger, sql, (Object[])queryParams, fetchSize);
        executeAction(action);
        return action.getResultSet();
    }

    public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) throws Exception
	{
    	throw new UnsupportedOperationException();
	}
}
