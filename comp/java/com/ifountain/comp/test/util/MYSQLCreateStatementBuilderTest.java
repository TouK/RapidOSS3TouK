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
 * Created on 19.Oct.2005
 */
package com.ifountain.comp.test.util;


import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Burak
 */
public class MYSQLCreateStatementBuilderTest extends RCompTestCase
{

	CreateStatementBuilder mysqlBuilder;
	
	public static Test suite()
    {
        TestSuite suite = new TestSuite(MYSQLCreateStatementBuilderTest.class);
        return suite;
    }
	
	protected void setUp() throws Exception
	{
		super.setUp();
		mysqlBuilder = CreateStatementBuilderFactory.getBuilder(CreateStatementBuilderFactory.MYSQL);
	}
	public void testAddStringColumnMethod() throws Exception
	{
		
		mysqlBuilder.addStringColumn("Name", 50);
		String addedColumn = (String)mysqlBuilder.getColumns().get(0);
		assertEquals("Name varchar(50)", addedColumn);
	}
	
	public void testAddStringWithEmptyColumnName() throws Exception
	{
		assertEquals(0, mysqlBuilder.getColumns().size());
		mysqlBuilder.addStringColumn("", 50);
		assertEquals(0, mysqlBuilder.getColumns().size());
	}
	
	public void testAddStringWithZeroLength() throws Exception
	{
		assertEquals(0, mysqlBuilder.getColumns().size());
		mysqlBuilder.addStringColumn("Players", 0);
		assertEquals(0, mysqlBuilder.getColumns().size());
	}
	
	public void testAddStringWithSubZeroLength() throws Exception
	{
		assertEquals(0, mysqlBuilder.getColumns().size());
		mysqlBuilder.addStringColumn("Players", -1);
		assertEquals(0, mysqlBuilder.getColumns().size());
	}
	
	public void testAddIntColumn() throws Exception
	{
		mysqlBuilder.addIntColumn("Age", 15);
		String addedColumn = (String)mysqlBuilder.getColumns().get(0);
		assertEquals("Age int", addedColumn);
	}
	
	public void testAddIntWithEmptyOrNullColumnName() throws Exception
	{
		assertEquals(0, mysqlBuilder.getColumns().size());
		mysqlBuilder.addIntColumn(null, 5);
		assertEquals(0, mysqlBuilder.getColumns().size());
		mysqlBuilder.addIntColumn("", 5);
		assertEquals(0, mysqlBuilder.getColumns().size());
	}
	
	public void testAddIntIsNotAffectedBySize() throws Exception
	{
		assertEquals(0, mysqlBuilder.getColumns().size());
		mysqlBuilder.addIntColumn("age1", 5);
		assertEquals(1, mysqlBuilder.getColumns().size());
		mysqlBuilder.addIntColumn("age2", -5);
		assertEquals(2, mysqlBuilder.getColumns().size());
		
		String age1 = (String) mysqlBuilder.getColumns().get(0);
		String age2 = (String) mysqlBuilder.getColumns().get(1);
		
		assertEquals("age1 int", age1);
		assertEquals("age2 int", age2);
	}
	
	public void testAddDoubleColumn() throws Exception
	{
		assertEquals(0, mysqlBuilder.getColumns().size());
		mysqlBuilder.addDoubleColumn("MyDouble");
		assertEquals(1, mysqlBuilder.getColumns().size());
		String addedColumn = (String) mysqlBuilder.getColumns().get(0);
		assertEquals("MyDouble double", addedColumn);
	}
	
	public void testAddDoubleColumnWithEmptyAndNullColumnName() throws Exception
	{
		assertEquals(0, mysqlBuilder.getColumns().size());
		mysqlBuilder.addDoubleColumn("");
		assertEquals(0, mysqlBuilder.getColumns().size());
		mysqlBuilder.addDoubleColumn(null);
		assertEquals(0, mysqlBuilder.getColumns().size());
	}
	
	public void testAddTimestampColumnMethod() throws Exception
	{
		assertEquals(0, mysqlBuilder.getColumns().size());
		mysqlBuilder.addTimestampColumn("MyTimestampColumn",-1);
		assertEquals(1, mysqlBuilder.getColumns().size());
		String addedColumn = (String) mysqlBuilder.getColumns().get(0);
		assertEquals("MyTimestampColumn timestamp", addedColumn);
	}
	
	public void testAddTimestampWithNullAndEmptyCoplumnName() throws Exception
	{
		assertEquals(0, mysqlBuilder.getColumns().size());
		mysqlBuilder.addTimestampColumn("", -1);
		assertEquals(0, mysqlBuilder.getColumns().size());
		mysqlBuilder.addTimestampColumn(null, -1);
		assertEquals(0, mysqlBuilder.getColumns().size());
	}
	
	public void testAddTimeColumn() throws Exception
	{
		assertEquals(0, mysqlBuilder.getColumns().size());
		mysqlBuilder.addTimeColumn("MyTimeColumn", -1);
		assertEquals(1, mysqlBuilder.getColumns().size());
		String addedColumn = (String) mysqlBuilder.getColumns().get(0);
		assertEquals("MyTimeColumn time", addedColumn);
	}
	
	public void testAddTimeColumnWithEmptyAndNullColumnName() throws Exception
	{
		assertEquals(0, mysqlBuilder.getColumns().size());
		mysqlBuilder.addTimeColumn("", -1);
		assertEquals(0, mysqlBuilder.getColumns().size());
		mysqlBuilder.addTimeColumn(null, -1);
		assertEquals(0, mysqlBuilder.getColumns().size());
	}
    
    public void testAddDateTimeColumn() throws Exception
    {
        assertEquals(0, mysqlBuilder.getColumns().size());
        mysqlBuilder.addDateTimeColumn("MyDateTimeColumn", -1);
        assertEquals(1, mysqlBuilder.getColumns().size());
        String addedColumn = (String) mysqlBuilder.getColumns().get(0);
        assertEquals("MyDateTimeColumn datetime", addedColumn);
    }
    
    public void testAddSmallIntColumn() throws Exception
    {
        assertEquals(0, mysqlBuilder.getColumns().size());
        mysqlBuilder.addSmallIntColumn("MySmallIntColumn");
        assertEquals(1, mysqlBuilder.getColumns().size());
        String addedColumn = (String) mysqlBuilder.getColumns().get(0);
        assertEquals("MySmallIntColumn smallint", addedColumn);
    }
    public void testAddBigIntColumn() throws Exception
    {
        assertEquals(0, mysqlBuilder.getColumns().size());
        mysqlBuilder.addBigIntColumn("MyBigIntColumn");
        assertEquals(1, mysqlBuilder.getColumns().size());
        String addedColumn = (String) mysqlBuilder.getColumns().get(0);
        assertEquals("MyBigIntColumn bigint", addedColumn);
    }
    
}
