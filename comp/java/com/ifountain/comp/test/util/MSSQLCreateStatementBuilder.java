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
 * Created on Oct 24, 2005
 *
 */
package com.ifountain.comp.test.util;

import java.sql.Types;


public class MSSQLCreateStatementBuilder extends CreateStatementBuilder
{

    public MSSQLCreateStatementBuilder()
    {
        super();
    }

    public void addStringColumn(String columnName, int length)
    {
        if (columnName == null || columnName.length() < 1 || length < 1)
            return;
        
        String strToAdd = columnName + " varchar(" + length + ")";
        getColumns().add(strToAdd);
    }

    public void addIntColumn(String columnName, int size)
    {
        if(columnName == null || columnName.length() < 1)
            return;
        
        String strToAdd = columnName + " int";
        getColumns().add(strToAdd);
    }

    public void addDoubleColumn(String columnName)
    {
        if(columnName == null || columnName.length() < 1)
            return;
        
        String strToAdd = columnName + " float";
        getColumns().add(strToAdd);
    }

    public void addTimestampColumn(String columnName, int size)
    {
        if(columnName == null || columnName.length() < 1)
            return;
        
        String strToAdd = columnName + " datetime";
        getColumns().add(strToAdd);
    }

    public void addTimeColumn(String columnName, int size)
    {
        addDateTimeColumn(columnName,size);
    }

    public void addDateColumn(String columnName, int size)
    {
        addDateTimeColumn(columnName,size);
    }

    public void addSmallIntColumn(String columnName)
    {
        if(columnName == null || columnName.length() < 1)
            return;
        
        String strToAdd = columnName + " smallint";
        getColumns().add(strToAdd);
    }

    public void addBigIntColumn(String columnName)
    {
        if(columnName == null || columnName.length() < 1)
            return;
        
        String strToAdd = columnName + " bigint";
        getColumns().add(strToAdd);
    }

    public void addDateTimeColumn(String columnName, int size)
    {
        if(columnName == null || columnName.length() < 1)
            return;
        
        String strToAdd = columnName + " datetime";
        getColumns().add(strToAdd);
    }

    public int getType(int value_type)
    {
        switch (value_type)
        {
            case CreateStatementBuilder.INTEGER_TYPE:
                return Types.INTEGER;
            case CreateStatementBuilder.STRING_TYPE:
                return Types.VARCHAR;
            case CreateStatementBuilder.BIGINT_TYPE:
                return Types.BIGINT;
            case CreateStatementBuilder.DATE_TYPE:
                return Types.DATE;
            case CreateStatementBuilder.DATETIME_TYPE:
                return Types.TIMESTAMP;
            case CreateStatementBuilder.DOUBLE_TYPE:
                return Types.FLOAT;
            case CreateStatementBuilder.SMALLINT_TYPE:
                return Types.SMALLINT;
            case CreateStatementBuilder.TIME_TYPE:
                return Types.TIMESTAMP;
            case CreateStatementBuilder.TIMESTAMP_TYPE:
                return Types.TIMESTAMP;
            default:
                return -1;
        }
    }

}
