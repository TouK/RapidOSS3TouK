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
package database.datasource

import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.rcmdb.test.util.DatabaseConnectionImplTestUtils
import connection.DatabaseConnectionImpl
import datasource.ExecuteQueryAction
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.PreparedStatement
import com.ifountain.rcmdb.test.util.DatabaseTestConstants

public class ExecuteQueryActionTests extends RapidCoreTestCase {

    DatabaseConnectionImpl connection;
    public ExecuteQueryActionTests() {
    }

    protected void setUp() throws Exception {
        super.setUp();
        connection = new DatabaseConnectionImpl();
    }

    protected void tearDown() throws Exception {
        if (connection != null) {
            connection.disconnect();
        }
        DatabaseConnectionImplTestUtils.DEFAULT_DB_TYPE = DatabaseTestConstants.ORACLE
        super.tearDown();
    }

    public void testExecute() throws Exception {
        try {
            DatabaseConnectionImplTestUtils.createTableConnectionTrials();
        } catch (ClassNotFoundException e) {
        }
        connection.init(DatabaseConnectionImplTestUtils.getConnectionParam());
        connection.connect();
        DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        String sqlQuery = "select * from connectiontrials";
        Object[] queryParams = new Object[0];
        ExecuteQueryAction action = new ExecuteQueryAction(TestLogUtils.log, sqlQuery, queryParams);
        action.execute(connection);
        ResultSet resultSet = action.getResultSet();
        assertFalse(resultSet.next());

        DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(1, "Switch", "eraaswiad");

        action = new ExecuteQueryAction(TestLogUtils.log, sqlQuery, queryParams);
        action.execute(connection);
        resultSet = action.getResultSet();
        assertTrue(resultSet.next());
        assertEquals(1, resultSet.getInt(1));
        assertEquals("Switch", resultSet.getString(2));
        assertEquals("eraaswiad", resultSet.getString(3));
        assertFalse(resultSet.next());

        DatabaseConnectionImplTestUtils.addRecordIntoConnectionTrialsTable(2, "Router", "ernertbos");
        sqlQuery = "select classname,instancename from connectiontrials";
        action = new ExecuteQueryAction(TestLogUtils.log, sqlQuery, queryParams);
        action.execute(connection);
        resultSet = action.getResultSet();
        assertTrue(resultSet.next());
        assertEquals("Switch", resultSet.getString(1));
        assertEquals("eraaswiad", resultSet.getString(2));
        assertTrue(resultSet.next());
        assertEquals("Router", resultSet.getString(1));
        assertEquals("ernertbos", resultSet.getString(2));
        assertFalse(resultSet.next());


        sqlQuery = "select classname,instancename from connectiontrials where id=?";
        queryParams = new Object[1];
        queryParams[0] = new Integer(2);
        action = new ExecuteQueryAction(TestLogUtils.log, sqlQuery, queryParams);
        action.execute(connection);
        resultSet = action.getResultSet();
        assertTrue(resultSet.next());
        assertEquals("Router", resultSet.getString(1));
        assertEquals("ernertbos", resultSet.getString(2));
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
            assertEquals("QueryParameters cannot be null.", e.getMessage());
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
    public void testSearchingWithNullToMySql() {
        checkSearchingWithNull(DatabaseTestConstants.MYSQL)
    }
    public void testSearchingWithNullToMSSql() {
        checkSearchingWithNull(DatabaseTestConstants.MSSQL)
    }
    public void testSearchingWithNullToOracle() {
        checkSearchingWithNull(DatabaseTestConstants.ORACLE)
    }

    public void testSearchWithEmptyStringInMySql() {
        checkSearchWithEmptyString(DatabaseTestConstants.MYSQL, true)
    }

    public void testSearchWithEmptyStringInMsSql() {
        checkSearchWithEmptyString(DatabaseTestConstants.MSSQL, true)
    }

    public void testSearchWithEmptyStringInOracleDoesNotReturnResults() {
        checkSearchWithEmptyString(DatabaseTestConstants.ORACLE, false)
    }

    private void checkSearchWithEmptyString(dbType, boolean returnResult) {
        DatabaseConnectionImplTestUtils.DEFAULT_DB_TYPE = dbType
        connection.init(DatabaseConnectionImplTestUtils.getConnectionParam());
        connection.connect();
        try {
            DatabaseConnectionImplTestUtils.createTableConnectionTrials();
        } catch (ClassNotFoundException e) {
        }
        DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        String sqlQuery = "insert into connectiontrials (id,classname,instancename,severity,eventtext) values (?,?,?,?,?)";
        Object[] queryParams = new Object[5];
        queryParams[0] = new Integer(1);
        queryParams[1] = "a2";
        queryParams[2] = "b2";
        queryParams[3] = null;
        queryParams[4] = "";
        PreparedStatement stmt = connection.getConnection().prepareStatement(sqlQuery);
        DatabaseConnectionImpl.setStatementParameters(queryParams, stmt);
        stmt.executeUpdate();

        String selectQuery = "select * from connectiontrials";
        Object[] selectQueryParams = new Object[0];
        ExecuteQueryAction action = new ExecuteQueryAction(TestLogUtils.log, selectQuery, selectQueryParams)
        action.execute(connection)

        ResultSet resultSet = action.getResultSet();
        assertTrue(resultSet.next());
        assertEquals(1, resultSet.getInt(1));
        if(dbType == DatabaseTestConstants.ORACLE){
            assertNull(resultSet.getString(5))
        }
        else{
            assertEquals("", resultSet.getString(5))
        }

        selectQuery = "select * from connectiontrials where eventtext=?";
        selectQueryParams = new Object[1];
        selectQueryParams[0] = "";
        action = new ExecuteQueryAction(TestLogUtils.log, selectQuery, selectQueryParams)
        action.execute(connection)

        resultSet = action.getResultSet();
        if(returnResult){
            assertTrue(resultSet.next());    
        }
        else{
            assertFalse(resultSet.next());
        }

    }
    private void checkSearchingWithNull(dbType) {
        DatabaseConnectionImplTestUtils.DEFAULT_DB_TYPE = dbType
        connection.init(DatabaseConnectionImplTestUtils.getConnectionParam());
        connection.connect();
        try {
            DatabaseConnectionImplTestUtils.createTableConnectionTrials();
        } catch (ClassNotFoundException e) {
        }
        DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        String sqlQuery = "insert into connectiontrials (id,classname,instancename,severity,eventtext) values (?,?,?,?,?)";
        Object[] queryParams = new Object[5];
        queryParams[0] = new Integer(1);
        queryParams[1] = "a2";
        queryParams[2] = "b2";
        queryParams[3] = null;
        queryParams[4] = null;
        PreparedStatement stmt = connection.getConnection().prepareStatement(sqlQuery);
        DatabaseConnectionImpl.setStatementParameters(queryParams, stmt);
        stmt.executeUpdate();

        String selectQuery = "select * from connectiontrials";
        Object[] selectQueryParams = new Object[0];
        ExecuteQueryAction action = new ExecuteQueryAction(TestLogUtils.log, selectQuery, selectQueryParams)
        action.execute(connection)

        ResultSet resultSet = action.getResultSet();
        assertTrue(resultSet.next());
        assertEquals(1, resultSet.getInt(1));
        assertNull(resultSet.getObject(4))
        assertNull(resultSet.getObject(5))

        selectQuery = "select * from connectiontrials where severity=?";
        selectQueryParams = new Object[1];
        selectQueryParams[0] = null;
        action = new ExecuteQueryAction(TestLogUtils.log, selectQuery, selectQueryParams)
        action.execute(connection)

        resultSet = action.getResultSet();
        assertFalse(resultSet.next());

        selectQuery = "select * from connectiontrials where severity is null";
        selectQueryParams = new Object[0];
        action = new ExecuteQueryAction(TestLogUtils.log, selectQuery, selectQueryParams)
        action.execute(connection)

        resultSet = action.getResultSet();
        assertTrue(resultSet.next());

        selectQuery = "select * from connectiontrials where severity is null";
        selectQueryParams = new Object[0];
        action = new ExecuteQueryAction(TestLogUtils.log, selectQuery, selectQueryParams)
        action.execute(connection)

        resultSet = action.getResultSet();
        assertTrue(resultSet.next());
    }
}
