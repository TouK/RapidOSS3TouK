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
 * Created on Feb 7, 2008
 *
 * Author Sezgin
 */
package database.connection

import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException
import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.rcmdb.test.util.DatabaseConnectionImplTestUtils
import com.ifountain.rcmdb.test.util.DatabaseConnectionParams
import connection.DatabaseConnectionImpl;

public class DatabaseConnectionImplTests extends RapidCoreTestCase {
    DatabaseConnectionImpl conn;
    DatabaseConnectionParams connectionParams;
    public DatabaseConnectionImplTests(){
        connectionParams = DatabaseConnectionImplTestUtils.getConnectionParams();
    }

    protected void setUp() throws Exception {
        super.setUp();
        conn = new DatabaseConnectionImpl();
    }
    
    protected void tearDown() throws Exception {
        if(conn.isConnected()){
            conn.disconnect();
        }
        super.tearDown();
    }
    
    public void testInit() throws Exception {
        ConnectionParam param = DatabaseConnectionImplTestUtils.getConnectionParam();
        try {
            conn.init(param);
        } catch (Throwable e) {
            fail("should not throw exception");
        }
        assertSame(param, conn.getParameters());
        
        param.getOtherParams().remove(DatabaseConnectionImpl.DRIVER);
        try {
            conn.init(param);
            fail("should throw exception");
        } catch (UndefinedConnectionParameterException e) {
        }
        param.getOtherParams().put(DatabaseConnectionImpl.DRIVER, connectionParams.getDriver());
        param.getOtherParams().remove(DatabaseConnectionImpl.URL);
        try {
            conn.init(param);
            fail("should throw exception");
        } catch (UndefinedConnectionParameterException e) {
        }
        param.getOtherParams().put(DatabaseConnectionImpl.URL, connectionParams.getUrl());
        param.getOtherParams().remove(DatabaseConnectionImpl.USERNAME);
        try {
            conn.init(param);
            fail("should throw exception");
        } catch (UndefinedConnectionParameterException e) {
        }
        param.getOtherParams().put(DatabaseConnectionImpl.USERNAME, connectionParams.getUsername());
        param.getOtherParams().remove(DatabaseConnectionImpl.PASSWORD);
        try {
            conn.init(param);
            fail("should throw exception");
        } catch (UndefinedConnectionParameterException e) {
        }
        
        //invalid driver
        param.getOtherParams().put(DatabaseConnectionImpl.PASSWORD, connectionParams.getPassword());
        param.getOtherParams().put(DatabaseConnectionImpl.DRIVER, "com.ifountain.invalidDriver");
        try {
            conn.init(param);
            fail("should throw exception");
        } catch (ClassNotFoundException e) {
        }
    }
    
    public void testConnect() throws Exception {
        ConnectionParam param = DatabaseConnectionImplTestUtils.getConnectionParam();
        conn.init(param);
        conn.connect();
        assertTrue(conn.isConnected());
        assertFalse(conn.getConnection().isClosed());
    }
    
    public void testDisconnect() throws Exception {
        ConnectionParam param = DatabaseConnectionImplTestUtils.getConnectionParam();
        conn.init(param);
        conn.connect();
        assertTrue(conn.isConnected());
        assertFalse(conn.getConnection().isClosed());
        conn.disconnect();
        assertFalse(conn.isConnected());
        assertTrue(conn.getConnection().isClosed());
    }
}
