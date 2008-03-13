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
package datasources;

import java.sql.PreparedStatement;
import groovy.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;

import test.util.DatabaseConnectionImplTestUtils;

import com.ifountain.comp.test.util.logging.TestLogUtils;
import connections.DatabaseConnectionImpl;
import test.util.DatabaseConnectionImplTestUtils;
import com.ifountain.core.test.util.RapidCoreTestCase;

public class ExecuteUpdateActionTest extends RapidCoreTestCase {

    DatabaseConnectionImpl datasource;
    public ExecuteUpdateActionTest() {
        try {
        	DatabaseConnectionImplTestUtils.createTableConnectionTrials();
        } catch (ClassNotFoundException e) {
        }
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        datasource = new DatabaseConnectionImpl();
        datasource.init(DatabaseConnectionImplTestUtils.getDatasourceParam());
        datasource.connect();
    }
    
    @Override
    protected void tearDown() throws Exception {
        if(datasource != null){
            datasource.disconnect();
        }
        super.tearDown();
    }

    public void testExecute() throws Exception {
    	DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        String sqlQuery = "insert into datasourcetrials (id,classname,instancename) values (?,?,?)";
        Object [] queryParams = new Object[3];
        queryParams[0] = new Integer(1);
        queryParams[1] = "IPNetwork";
        queryParams[2] = "IPNET-10.10.0.0";
        ExecuteUpdateAction action = new ExecuteUpdateAction(TestLogUtils.log, sqlQuery, queryParams);
        action.execute(datasource);
        int affectedRowCount = action.getAffectedRowCount();
        assertEquals(1,affectedRowCount);
        
        String selectQuery = "select * from datasourcetrials";
        Object [] selectQueryParams = new Object[0];
        PreparedStatement stmt = datasource.getConnection().prepareStatement( selectQuery );
        DatabaseConnectionImpl.setStatementParameters(selectQueryParams, stmt);
        ResultSet resultSet = stmt.executeQuery();
        assertTrue(resultSet.next());
        assertEquals(1, resultSet.getInt(1));
        assertEquals("IPNetwork", resultSet.getString(2));
        assertEquals("IPNET-10.10.0.0", resultSet.getString(3));
        assertFalse(resultSet.next());
        
        queryParams = null;
        action = new ExecuteUpdateAction(TestLogUtils.log, sqlQuery, queryParams);
        try {
            action.execute(datasource);
            fail("Must throw exception");
        } catch (Exception e) {
            assertEquals("QueryParameters cannot be null.",e.getMessage());
        }
        
        DatabaseConnectionImplTestUtils.clearConnectionTrialsTable();
        
        queryParams = new Object[3];
        queryParams[0] = "1";           //We set param0 as string not integer
        queryParams[1] = "IPNetwork";
        queryParams[2] = "IPNET-10.10.0.0";
        
        action = new ExecuteUpdateAction(TestLogUtils.log, sqlQuery, queryParams);
        action.execute(datasource);
        affectedRowCount = action.getAffectedRowCount();
        assertEquals(1,affectedRowCount);
        stmt = datasource.getConnection().prepareStatement( selectQuery );
        DatabaseConnectionImpl.setStatementParameters(selectQueryParams, stmt);
        resultSet = stmt.executeQuery();
        assertTrue(resultSet.next());
        assertEquals(1, resultSet.getInt(1));
        assertEquals("IPNetwork", resultSet.getString(2));
        assertEquals("IPNET-10.10.0.0", resultSet.getString(3));
        assertFalse(resultSet.next());
        
        queryParams = new Object[2];
        queryParams[0] = new Integer(1);
        queryParams[1] = "IPNetwork";
        action = new ExecuteUpdateAction(TestLogUtils.log, sqlQuery, queryParams);
        try {
            action.execute(datasource);
            fail("Must throw exception");
        } catch (SQLException e) {
        }
        
        queryParams = new Object[4];
        queryParams[0] = new Integer(1);
        queryParams[1] = "IPNetwork";
        queryParams[2] = "IPNET-10.10.0.0";
        queryParams[3] = "Extra";
        
        action = new ExecuteUpdateAction(TestLogUtils.log, sqlQuery, queryParams);
        try {
            action.execute(datasource);
            fail("Must throw exception");
        } catch (SQLException e) {
        }
        
        sqlQuery = "update datasourcetrials set classname=?,instancename=? where id=?";
        queryParams = new Object[3];
        queryParams[0] = "Router";
        queryParams[1] = "ernertbos";
        queryParams[2] = new Integer(1);
        
        action = new ExecuteUpdateAction(TestLogUtils.log, sqlQuery, queryParams);
        action.execute(datasource);
        affectedRowCount = action.getAffectedRowCount();
        assertEquals(1,affectedRowCount);
        stmt = datasource.getConnection().prepareStatement( selectQuery );
        DatabaseConnectionImpl.setStatementParameters(selectQueryParams, stmt);
        resultSet = stmt.executeQuery();
        assertTrue(resultSet.next());
        assertEquals(1, resultSet.getInt(1));
        assertEquals("Router", resultSet.getString(2));
        assertEquals("ernertbos", resultSet.getString(3));
        assertFalse(resultSet.next());
        
        sqlQuery = "delete from datasourcetrials where id=?";
        queryParams = new Object[1];
        queryParams[0] = new Integer(1);
        action = new ExecuteUpdateAction(TestLogUtils.log, sqlQuery, queryParams);
        action.execute(datasource);
        affectedRowCount = action.getAffectedRowCount();
        assertEquals(1,affectedRowCount);
        stmt = datasource.getConnection().prepareStatement( selectQuery );
        DatabaseConnectionImpl.setStatementParameters(selectQueryParams, stmt);
        resultSet = stmt.executeQuery();
        assertFalse(resultSet.next());
    }
}
