/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
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

    protected boolean isConnectionException(Throwable t) {
        return false; //To change body of implemented methods use File | Settings | File Templates.
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
                Object fieldValue=rset.getObject(i);
                record.put(metaData.getColumnName(i).toUpperCase(), fieldValue);
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
