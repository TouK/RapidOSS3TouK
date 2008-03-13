/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be 
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
/*
 * Created on 19.October.2005
 */
package com.ifountain.comp.test.util;

import java.util.ArrayList;

/**
 * @author Burak
 */
public abstract class CreateStatementBuilder
{
	public static final int INTEGER_TYPE =      0;
    public static final int STRING_TYPE =       1;
    public static final int DOUBLE_TYPE =       2;
    public static final int TIMESTAMP_TYPE =    3;
    public static final int TIME_TYPE =         4;
    public static final int DATE_TYPE =         5;
    public static final int SMALLINT_TYPE =     6;
    public static final int BIGINT_TYPE =       7;
    public static final int DATETIME_TYPE =     8;
    
    
    protected ArrayList columns;
	protected String tableName;
	
	protected CreateStatementBuilder()
	{
		columns = new ArrayList();
	}
	
	public String getSqlString()
	{
		if(tableName == null || tableName.length() < 1)
			return "error : no tablename defined";
		if(columns == null || columns.size() < 1)
			return "error : no columns defined";
		
		StringBuffer sqlBuffer = new StringBuffer("create table " + tableName + "(");
		for (int i = 0 ; i < columns.size() ; i++)
		{
			if( i != 0)
				sqlBuffer.append(", ");
			
			sqlBuffer.append(columns.get(i).toString());
		}
		
		sqlBuffer.append(")");
		
		return sqlBuffer.toString();
	}
	public abstract void addStringColumn(String columnName, int length);
	public abstract void addIntColumn(String columnName, int size);
	public abstract void addDoubleColumn(String columnName);
	public abstract void addTimestampColumn(String columnName, int size);
	public abstract void addTimeColumn(String columnName, int size);
	public abstract void addDateColumn(String columnName, int size);
    public abstract void addSmallIntColumn(String string);
    public abstract void addBigIntColumn(String string);
    public abstract void addDateTimeColumn(String string, int size);
	
	
	public ArrayList getColumns()
	{
		return columns;
	}

	public void setTableName( String tableName )
	{
		this.tableName = tableName;
	}

    public abstract int getType(int value_type);

    
}