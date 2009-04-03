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

import org.apache.log4j.Logger;

public class SingleTableDatabaseAdapter extends DatabaseAdapter {
    private String table;
    private List keys;

    public SingleTableDatabaseAdapter(){
    	super();
    }

    public SingleTableDatabaseAdapter(String datasourceName, String tableName, String keys, long reconnectInterval, Logger logger) {
        super(datasourceName, reconnectInterval, logger);
        this.table = tableName;
        setKeys(keys);
    }

    public void setTable(String tableName){
    	this.table = tableName;
    }

    public void setKeys(String keys){
    	this.keys = Arrays.asList(keys.split(","));
    }

    public addRecord(Map fields){
		def primaryValues = [:];
		for (key in keys){
			def keyValue = fields[key]; // .get(key);
			if(keyValue == null) throw new Exception("No value supplied for one of the primary key fields: " + key);
			primaryValues.put(key, keyValue);
		}

		def row = getMultiKeyRecord(primaryValues);
		if (row.size() == 0) {
            StringBuffer query1 = new StringBuffer("insert into ").append(table).append(" (");
			StringBuffer query2 = new StringBuffer(" VALUES(");
			def params = [];
			fields.each(){key,value ->
				query1.append(key).append(", ");
				query2.append("?, ");
				params.add(value);
			}
			query1.deleteCharAt(query1.length()-2);
			query2.deleteCharAt(query2.length()-2);
			query1.append(")");
			query2.append(")");
			def res = executeUpdate(query1.append(query2).toString(), params);
			return getMultiKeyRecord(primaryValues);
		}
		else{
            return updateRecord(fields);
		}
	}

	public updateRecord(Map fields){
		def primaryValues = [:];
		def updatedProps = [:];
		updatedProps.putAll(fields);
		for (key in keys){
			def keyValue = fields[key]; // .get(key);
			if(keyValue == null) throw new Exception("No value supplied for one of the primary key fields: " + key);
			primaryValues.put(key, keyValue);
			updatedProps.remove(key);
		}
		if(updatedProps.size() == 0) return null;
		def row = getMultiKeyRecord(primaryValues);
		if (row.size() > 0) {
			def params = getUpdateParams(fields);
			def updateQuery = getUpdateQuery(updatedProps);
			def res = executeUpdate(updateQuery, params);
			return getMultiKeyRecord(primaryValues);
		}
		return null;
	}

	boolean removeRecord(keyValue){
		def keyMap = [:];
		keyMap.put(keys[0], keyValue);   // only one key
		removeMultiKeyRecord(keyMap);
	}

	boolean removeMultiKeyRecord(Map keyMap){
		def query = new StringBuffer("delete from ").append(table).append(" where ");
		def params = [];
		keyMap.each(){key,value->
				query.append(key).append(" = ? AND ");
				params.add(value);
		}
		def queryStr = query.substring(0,query.length()-4);
		return executeUpdate(queryStr, params) > 0;
	}

    public getRecord(keyValue){
	    def keyMap = [:];
	  	keyMap.put(keys[0], keyValue);   // only one key
	    return getMultiKeyRecord(keyMap,[]);
    }

    public getRecord(keyValue, columnList){
	    def keyMap = [:];
	  	keyMap.put(keys[0], keyValue);   // only one key
	    return getMultiKeyRecord(keyMap, columnList);
    }

    public getMultiKeyRecord(keyMap){
	    return getMultiKeyRecord(keyMap,[]);
    }

    public getMultiKeyRecord(Map keyMap, columnList){
	    StringBuffer query = new StringBuffer();
	    query = formSql(columnList);
	    def params = [];
	    query.append(formWhereClause(keyMap, params));
	    def result = executeQuery(query.toString(), params);
	    if(result.size() > 0)
        {
            return result[0];
        }
        else
        {
            return [:];
        }
    }

    public getRecords(){
		return getRecords("", []);
    }

    public getRecords(List columnList){
	    return getRecords("", columnList);
    }

    public getRecords(String whereclause){
		return getRecords(whereclause, []);
    }

    public getRecords(whereClause, List columnList){
		StringBuffer query = new StringBuffer();
	    query = formSql(columnList);
	    if(whereClause.trim().length() > 0){
			query.append(" where ").append(whereClause);
		}
		return executeQuery(query.toString(), []);
    }

    public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) throws Exception
	{
		return getMultiKeyRecord(ids, fieldsToBeRetrieved);
	}

    private String getUpdateQuery(fields){
		StringBuffer updateQuery = new StringBuffer();
		updateQuery.append("update ").append(table);
		updateQuery.append(" set ");
		fields.each(){key,value->
			updateQuery.append(key).append(" = ?, ");
		}
		updateQuery.deleteCharAt(updateQuery.length()-2);
		updateQuery.append(" where ");
		for (key in keys){
			updateQuery.append(key).append(" = ? AND ");
		}
		def queryStr = updateQuery.substring(0,updateQuery.length()-4);
		return queryStr;
	}

	private List getUpdateParams(fields){
		def params = [];
		fields.each(){key,value ->
			if (!keys.contains(key)){
				params.add(value);
			}
		}
		for (key in keys){
			params.add(fields[key]);
		}
		return params;
	}

    private formSql(columnList){
	 StringBuffer query = new StringBuffer("select ");
		if (columnList.size() == 0){
			query.append("*");
		}
		else{
			for (key in keys){
				if (!columnList.contains(key)){
					columnList.add(key);
				}
			}
			for(column in columnList){
				query.append(column).append(", ");
			}
			query.deleteCharAt(query.length()-2);
		}
		query.append(" from ").append(table);
		return query;
    }

    private formWhereClause(Map keyMap, params){
	    StringBuffer query = new StringBuffer(" where ");
		keyMap.each(){key,value->
				query.append(key).append(" = ? AND ");
				params.add(value);
		}
		def queryStr = query.substring(0,query.length()-4);
		return queryStr;
	}
}
