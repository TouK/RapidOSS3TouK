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
 * Created on Oct 18, 2005
 *
 */
package com.ifountain.comp.test.util;


import junit.framework.Test;
import junit.framework.TestSuite;

public class OracleCreateStatementBuilderTest extends RCompTestCase
{

    OracleCreateStatementBuilder builder;

    protected void setUp() throws Exception
    {
        super.setUp();
        builder = new OracleCreateStatementBuilder();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(OracleCreateStatementBuilderTest.class);
        return suite;
    }
    
    public void testAddStringColumn() throws Exception
    {
        String colName = "stringcol";
        builder.addStringColumn(colName, 50);
        assertEquals(colName + " VARCHAR2(50)", builder.getColumns().get(0).toString());
    }
    
    public void testAddIntColumn() throws Exception
    {
        String colName = "intcol";
        builder.addIntColumn(colName, 10);
        assertEquals(colName + " NUMBER(10)", builder.getColumns().get(0).toString());
        
        builder.addIntColumn(colName, -1);
        assertEquals(colName + " NUMBER", builder.getColumns().get(1).toString());
    }
    
    public void testAddDoubleColumn() throws Exception
    {
        String colName = "doublecol";
        builder.addDoubleColumn(colName);
        assertEquals(colName + " FLOAT", builder.getColumns().get(0).toString());
    }
    
    public void testAddTimestampColumn() throws Exception
    {
        String colName = "timestampcol";
        builder.addTimestampColumn(colName, 50);
        assertEquals(colName + " TIMESTAMP(50)", builder.getColumns().get(0).toString());
        
        builder.addTimestampColumn(colName, -1);
        assertEquals(colName + " TIMESTAMP", builder.getColumns().get(1).toString());
    }
    
    public void testAddTimeColumn() throws Exception
    {
        String colName = "timecol";
        builder.addTimeColumn(colName, 50);
        assertEquals(colName + " TIMESTAMP(50)", builder.getColumns().get(0).toString());
        
        builder.addTimeColumn(colName, -1);
        assertEquals(colName + " TIMESTAMP", builder.getColumns().get(1).toString());
    }
    
    public void testAddDateColumn() throws Exception
    {
        String colName = "datecol";
        builder.addDateColumn(colName, 50);
        assertEquals(colName + " DATE(50)", builder.getColumns().get(0).toString());
        
        builder.addDateColumn(colName, -1);
        assertEquals(colName + " DATE", builder.getColumns().get(1).toString());
    }
    
    public void testAddDateTimeColumn() throws Exception
    {
        String colName = "datetimecol";
        builder.addDateTimeColumn(colName, 50);
        assertEquals(colName + " TIMESTAMP(50)", builder.getColumns().get(0).toString());
        
        builder.addDateTimeColumn(colName, -1);
        assertEquals(colName + " TIMESTAMP", builder.getColumns().get(1).toString());
    }
    
    public void testAddSmallIntColumn() throws Exception
    {
        assertEquals(0, builder.getColumns().size());
        builder.addSmallIntColumn("MySmallIntColumn");
        assertEquals(1, builder.getColumns().size());
        String addedColumn = (String) builder.getColumns().get(0);
        assertEquals("MySmallIntColumn NUMBER", addedColumn);
    }
    public void testAddBigIntColumn() throws Exception
    {
        assertEquals(0, builder.getColumns().size());
        builder.addBigIntColumn("MyBigIntColumn");
        assertEquals(1, builder.getColumns().size());
        String addedColumn = (String) builder.getColumns().get(0);
        assertEquals("MyBigIntColumn NUMBER", addedColumn);
    }

}
