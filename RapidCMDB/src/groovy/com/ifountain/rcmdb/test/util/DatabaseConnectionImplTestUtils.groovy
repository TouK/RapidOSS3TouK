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
package com.ifountain.rcmdb.test.util

import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.core.connection.ConnectionParam
import connection.DatabaseConnectionImpl
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException;

public class DatabaseConnectionImplTestUtils {
    public static final String DATABASE_CONN_NAME = "dbConn";
    public static String DEFAULT_DB_TYPE = DatabaseTestConstants.ORACLE
    public static DatabaseConnectionParams getConnectionParams(String type) {
        if (type.equals(DatabaseTestConstants.MYSQL) || type.equals(DatabaseTestConstants.ORACLE) || type.equals(DatabaseTestConstants.SYBASE)|| type.equals(DatabaseTestConstants.MSSQL)) {
            return new DatabaseConnectionParams(CommonTestUtils.getTestProperty(type + "." + DatabaseTestConstants.DATABASE_DRIVER),
                    CommonTestUtils.getTestProperty(type + "." + DatabaseTestConstants.DATABASE_URL),
                    CommonTestUtils.getTestProperty(type + "." + DatabaseTestConstants.DATABASE_USER),
                    CommonTestUtils.getTestProperty(type + "." + DatabaseTestConstants.DATABASE_PASSWORD)
            );
        }
        return null;
    }
    public static DatabaseConnectionParams getConnectionParams() {
        return getConnectionParams(DEFAULT_DB_TYPE);
    }

    public static ConnectionParam getConnectionParam(String type) {
        DatabaseConnectionParams connectionParams = getConnectionParams(type);
        Map<String, Object> otherParams = new HashMap<String, Object>();
        otherParams.put(DatabaseConnectionImpl.DRIVER, connectionParams.getDriver());
        otherParams.put(DatabaseConnectionImpl.URL, connectionParams.getUrl());
        otherParams.put(DatabaseConnectionImpl.USERNAME, connectionParams.getUsername());
        otherParams.put(DatabaseConnectionImpl.PASSWORD, connectionParams.getPassword());

        ConnectionParam connectionParam=new ConnectionParam("Database", DATABASE_CONN_NAME, DatabaseConnectionImpl.class.getName(), otherParams);
        connectionParam.setMinTimeout(10000);
        return connectionParam;
    }
    public static ConnectionParam getConnectionParam() {
        return getConnectionParam(DEFAULT_DB_TYPE);
    }

    public static void createTableConnectionTrials() throws ClassNotFoundException {
        createTable("create table connectiontrials (id int NOT NULL,classname varchar(50) NOT NULL,instancename varchar(50) NOT NULL,severity int, eventtext varchar(50))");
    }

    public static void createTable(String sql) throws ClassNotFoundException {
        DatabaseConnectionParams params = getConnectionParams();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            Class.forName(params.getDriver());
            conn = DriverManager.getConnection(params.getUrl(), params.getUsername(), params.getPassword());
            stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
        } finally {
            if (stmt != null)
            {
                try {
                    stmt.close();
                } catch (SQLException e1) {

                }
            }
            if (conn != null)
            {
                try {
                    conn.close();
                } catch (SQLException e1) {

                }
            }
        }

    }

    public static void addRecordIntoConnectionTrialsTable(int id, String className, String instanceName) throws ClassNotFoundException, SQLException {
        DatabaseConnectionParams params = getConnectionParams();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            Class.forName(params.getDriver());
            conn = DriverManager.getConnection(params.getUrl(), params.getUsername(), params.getPassword());
            stmt = conn.prepareStatement("insert into connectiontrials (id,classname,instancename) values (?,?,?)");
            stmt.setInt(1, id);
            stmt.setString(2, className);
            stmt.setString(3, instanceName);
            stmt.executeUpdate();
        } finally {
            if (stmt != null)
            {
                try {
                    stmt.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            if (conn != null)
            {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    public static void clearTable(String tableName) throws Exception {
        DatabaseConnectionParams params = getConnectionParams();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            Class.forName(params.getDriver());
            conn = DriverManager.getConnection(params.getUrl(), params.getUsername(), params.getPassword());
            stmt = conn.prepareStatement("delete from " + tableName);
            stmt.execute();
        } finally {
            if (stmt != null)
            {
                try {
                    stmt.close();
                } catch (SQLException e1) {

                }
            }
            if (conn != null)
            {
                try {
                    conn.close();
                } catch (SQLException e1) {

                }
            }
        }
    }
    public static void clearConnectionTrialsTable() throws Exception {
        clearTable("connectiontrials");
    }
}
