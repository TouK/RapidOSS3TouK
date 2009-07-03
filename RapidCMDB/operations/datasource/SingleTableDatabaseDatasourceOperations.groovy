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

    SingleTableDatabaseAdapter adapter;
    def onLoad(){
       def ownConnection=getProperty("connection")
       if(ownConnection != null)
       {
            this.adapter = new SingleTableDatabaseAdapter(ownConnection.name, tableName, tableKeys, reconnectInterval*1000, getLogger());
       }
    }
    def getAdapters()
    {
        return [adapter];
    }

    def getProperty(Map keys, String propName){
        def props = adapter.getMultiKeyRecord(keys, [propName]);
        return convert(props[propName]);
    }

    def getProperties(Map keys, List properties){
       def props = adapter.getMultiKeyRecord(keys, properties);
       return convert(props);
    }

    def getRecord(keyValue){
        return convert(adapter.getRecord(keyValue));
    }

    def getRecord(keyValue, columnList){
	    return convert(adapter.getRecord(keyValue, columnList));
    }

    def getMultiKeyRecord(keyMap){
        return convert(adapter.getMultiKeyRecord(keymap));
    }

    def getMultiKeyRecord(Map keyMap, columnList){
        return convert(adapter.getMultiKeyRecord(keymap, columnList));
    }

    def getRecords(){
		return convert(adapter.getRecords());
    }

    def getRecords(List columnList){
          return convert(adapter.getRecords(columnList));
    }

    def getRecords(String whereclause){
        return convert(adapter.getRecords(whereclause));
    }

    def getRecords(whereClause, List columnList){
        return convert(adapter.getRecords(whereClause, columnList));
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
        return convert(this.adapter.executeUpdate(sql, []));
    }

    def runUpdate(sql, queryParams){
        return convert(this.adapter.executeUpdate(sql, queryParams));
    }

    def runQuery(sql){
        return convert(this.adapter.executeQuery(sql, []));
    }

    def runQuery(sql,  queryParams){
        return convert(this.adapter.executeQuery(sql, queryParams));
    }

    def runQuery(sql,  queryParams, fetchSize, closure){
        this.adapter.executeQuery(sql, queryParams, fetchSize, closure);
    }
}