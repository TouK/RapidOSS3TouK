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
public class CreateStatementBuilderFactoryTest extends RCompTestCase
{

	public static Test suite()
  {
      TestSuite suite = new TestSuite(CreateStatementBuilderFactoryTest.class);
      return suite;
  }
	
	public void testGettingMysqlFactory() throws Exception
	{
		CreateStatementBuilder builder = CreateStatementBuilderFactory.getBuilder(CreateStatementBuilderFactory.MYSQL);
		assertNotNull(builder);
		assertTrue(builder instanceof MYSQLCreateStatementBuilder);
		
		try
		{
			builder = CreateStatementBuilderFactory.getBuilder(123);
			fail("should have thrown exception : unknown database type for CreateStatementBuilderFactory");
		}
		catch (Exception e)
		{
		}
        
        builder = CreateStatementBuilderFactory.getBuilder(CreateStatementBuilderFactory.MSSQL);
        assertTrue(builder instanceof MSSQLCreateStatementBuilder);
		
        builder = CreateStatementBuilderFactory.getBuilder(CreateStatementBuilderFactory.CLOUDSCAPE);
        assertTrue(builder instanceof CloudscapeCreateStatementBuilder);
	}

}
