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
/**
 * Created on Feb 8, 2008
 *
 * Author Sezgin
 */
package database.datasource;

import java.sql.ResultSet;
import groovy.util.*;
import java.sql.SQLException;

import com.ifountain.comp.test.util.logging.TestLogUtils;
import connection.DatabaseConnectionImpl;
import com.ifountain.rcmdb.test.util.DatabaseConnectionImplTestUtils;
import com.ifountain.core.test.util.RapidCoreTestCase
import datasource.ExecuteQueryAction;

public class ExecuteQueryActionTests extends RapidCoreTestCase {

    DatabaseConnectionImpl connection;
    public ExecuteQueryActionTests() {
        try {
            DatabaseConnectionImplTestUtils.createTableConnectionTrials();
        } catch (ClassNotFoundException e) {
        }
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        connection = new DatabaseConnectionImpl();
        connection.init(DatabaseConnectionImplTestUtils.getDatasourceParam());
        connection.connect();
    }
    
    protected void tearDown() throws Exception {
        if(connection != null){
            connection.disconnect();
        }
        super.tearDown();
    }
    
    public void testExecute() throws Exception {
    	DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        String sqlQuery = "select * from connectiontrials";
        Object [] queryParams = new Object[0];
        ExecuteQueryAction action = new ExecuteQueryAction(TestLogUtils.log, sqlQuery, queryParams);
        action.execute(connection);
        ResultSet resultSet = action.getResultSet();
        assertFalse(resultSet.next());
        
        DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(1,"Switch","eraaswiad");

        action = new ExecuteQueryAction(TestLogUtils.log, sqlQuery, queryParams);
        action.execute(connection);
        resultSet = action.getResultSet();
        assertTrue(resultSet.next());
        assertEquals(1, resultSet.getInt(1));
        assertEquals("Switch", resultSet.getString(2));
        assertEquals("eraaswiad",resultSet.getString(3));
        assertFalse(resultSet.next());
        
        DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(2,"Router","ernertbos");
        sqlQuery = "select classname,instancename from connectiontrials";
        action = new ExecuteQueryAction(TestLogUtils.log, sqlQuery, queryParams);
        action.execute(connection);
        resultSet = action.getResultSet();
        assertTrue(resultSet.next());
        assertEquals("Switch", resultSet.getString(1));
        assertEquals("eraaswiad",resultSet.getString(2));
        assertTrue(resultSet.next());
        assertEquals("Router",resultSet.getString(1));
        assertEquals("ernertbos",resultSet.getString(2));
        assertFalse(resultSet.next());
        
        
        sqlQuery = "select classname,instancename from connectiontrials where id=?";
        queryParams = new Object[1];
        queryParams[0] = new Integer(2);
        action = new ExecuteQueryAction(TestLogUtils.log, sqlQuery, queryParams);
        action.execute(connection);
        resultSet = action.getResultSet();
        assertTrue(resultSet.next());
        assertEquals("Router",resultSet.getString(1));
        assertEquals("ernertbos",resultSet.getString(2));
        assertFalse(resultSet.next());
        
        queryParams = new Object[3];
        queryParams[0] = new Integer(2);
        queryParams[1] = new Integer(2);
        queryParams[2] = new Integer(2);
        action = new ExecuteQueryAction(TestLogUtils.log, sqlQuery, queryParams);
        try {
            action.execute(connection);
            fail("should throw exception.");
        } catch (SQLException e) {
        }
        
        queryParams = null;
        action = new ExecuteQueryAction(TestLogUtils.log, sqlQuery, queryParams);
        try {
            action.execute(connection);
            fail("should throw exception.");
        } catch (Exception e) {
            assertEquals("QueryParameters cannot be null.",e.getMessage());
        }
        
        queryParams = new Object[0];
        action = new ExecuteQueryAction(TestLogUtils.log, sqlQuery, queryParams);
        try {
            action.execute(connection);
            fail("should throw exception.");
        } catch (SQLException e) {
        }
        
        queryParams = new Object[1];
        queryParams[0] = new Integer(2);
        connection.disconnect();
        action = new ExecuteQueryAction(TestLogUtils.log, sqlQuery, queryParams);
        try {
            action.execute(connection);
            fail("should throw exception.");
        } catch (SQLException e) {
        }
    }
}
