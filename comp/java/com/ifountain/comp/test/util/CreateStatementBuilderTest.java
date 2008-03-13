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

import java.sql.Types;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Burak
 */
public class CreateStatementBuilderTest extends RCompTestCase
{

	public static Test suite()
  {
      TestSuite suite = new TestSuite(CreateStatementBuilderTest.class);
      return suite;
  }
	
	public void testConstructor() throws Exception
	{
		CreateStatementBuilder builder = CreateStatementBuilderFactory.getBuilder(CreateStatementBuilderFactory.MYSQL);
		assertNotNull(builder.getColumns());
		assertEquals(0, builder.getColumns().size());
	}
	
	public void testGetSqlStringMethod() throws Exception
	{
		CreateStatementBuilder builder = CreateStatementBuilderFactory.getBuilder(CreateStatementBuilderFactory.MYSQL);
		assertEquals("error : no tablename defined", builder.getSqlString());
		
		builder.setTableName("myTable");
		assertEquals("error : no columns defined", builder.getSqlString());
		
		builder.getColumns().add("1");
		builder.getColumns().add("1");
		builder.getColumns().add("2");
		builder.getColumns().add("3");
		
		assertEquals("create table myTable(1, 1, 2, 3)", builder.getSqlString());
	}
    
    public void testGetTypeMethod() throws Exception
    {
        CreateStatementBuilder builder = CreateStatementBuilderFactory.getBuilder(CreateStatementBuilderFactory.MYSQL);
        int intType = builder.getType(CreateStatementBuilder.INTEGER_TYPE);
        assertEquals(Types.INTEGER, intType);
    }

}
