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
import connection.DatabaseConnectionImpl
import com.ifountain.rcmdb.test.util.DatabaseTestConstants
import java.sql.DriverManager;


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

    public void testIsConnectionException()
    {
        DatabaseConnectionImpl connection = new DatabaseConnectionImpl();
        ConnectException  exception = new ConnectException("exception");
        assertTrue (connection.isConnectionException(exception));

        SocketException socketException = new SocketException();
        assertTrue (connection.isConnectionException(socketException));

        NoRouteToHostException noRouteToHostException = new NoRouteToHostException();
        assertTrue (connection.isConnectionException(noRouteToHostException));

        IOException ioException = new IOException()
        assertFalse (connection.isConnectionException(ioException));

        Exception nestedException = new Exception(new SocketException());
        assertTrue (connection.isConnectionException(nestedException));

        Exception otherException = new Exception();
        assertFalse(connection.isConnectionException(otherException));
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
        def minTimeout=10000;
        ConnectionParam param = DatabaseConnectionImplTestUtils.getConnectionParam();
        param.setMinTimeout(minTimeout);

        conn.init(param);
        conn.connect();
        assertTrue(conn.checkConnection());
        assertFalse(conn.getConnection().isClosed());
        
        assertEquals((int)(minTimeout/1000),DriverManager.getLoginTimeout());
    }
    
    public void testDisconnect() throws Exception {
        ConnectionParam param = DatabaseConnectionImplTestUtils.getConnectionParam();
        conn.init(param);
        println param.getOtherParams()[DatabaseConnectionImpl.DRIVER]
        
        conn.connect();
        assertTrue(conn.checkConnection());
        assertFalse(conn.getConnection().isClosed());
        conn.disconnect();
        assertFalse(conn.checkConnection());
        assertTrue(conn.getConnection().isClosed());
    }



    public void testConnectTimeoutWithMysql() throws Exception {
        // info.put("connectTimeout",timeout); handles   
        def minTimeout=2000;
        ConnectionParam param = DatabaseConnectionImplTestUtils.getConnectionParam(DatabaseTestConstants.MYSQL);
        param.setMinTimeout(minTimeout);

        param.getOtherParams()[DatabaseConnectionImpl.URL]="jdbc:mysql://192.168.55.150:3306/students";
        println "url is"+param.getOtherParams()[DatabaseConnectionImpl.URL]

        conn.init(param);


        def startTime=0;
        def elapsedTime=0;
        try{
            startTime=System.currentTimeMillis();
            conn.connect();
            fail("should throw exception");
        }
        catch(com.mysql.jdbc.CommunicationsException e)
        {
            elapsedTime=System.currentTimeMillis()-startTime;
            println "elapsedTime ${elapsedTime} minTimeout ${minTimeout}"
            e.printStackTrace();
        }

        assertTrue(elapsedTime>=minTimeout)
        assertTrue(elapsedTime-minTimeout<1000);


    }

    public void testQueryTimeoutWithMysql() throws Exception {
        // info.put("socketTimeout",timeout); handles
        def minTimeout=25000;

        ConnectionParam param = DatabaseConnectionImplTestUtils.getConnectionParam(DatabaseTestConstants.MYSQL);
        param.setMinTimeout(minTimeout);

        conn.init(param);
        conn.connect();


        java.sql.Connection dbcon=conn.getConnection();
        java.sql.Statement stmt=dbcon.createStatement();
        
        def startTime=0;
        def elapsedTime=0;
        try{
            startTime=System.currentTimeMillis();
            stmt.executeQuery ("SELECT SLEEP(30) ")
            fail("should throw exception");
        }
        catch(com.mysql.jdbc.CommunicationsException e)
        {
            elapsedTime=System.currentTimeMillis()-startTime;
            println "elapsedTime ${elapsedTime} minTimeout ${minTimeout}"
            e.printStackTrace();
        }

        assertTrue(elapsedTime>=minTimeout)
        assertTrue(elapsedTime-minTimeout<1000);


    }

    public void testConnectTimeoutWithOracle() throws Exception {
        //setLoginTimeout handles
        //also setting oracle.net.CONNECT_TIMEOUT handles

        def minTimeout=2000;
        ConnectionParam param = DatabaseConnectionImplTestUtils.getConnectionParam(DatabaseTestConstants.ORACLE);
        param.setMinTimeout(minTimeout);

        param.getOtherParams()[DatabaseConnectionImpl.URL]="jdbc:oracle:thin:@192.168.1.150:1521:xe";
        println "url is"+param.getOtherParams()[DatabaseConnectionImpl.URL]

        conn.init(param);


        def startTime=0;
        def elapsedTime=0;
        try{
            startTime=System.currentTimeMillis();
            conn.connect();
            fail("should throw exception");
        }
        catch(java.sql.SQLException e)
        {
            elapsedTime=System.currentTimeMillis()-startTime;
            println "elapsedTime ${elapsedTime} minTimeout ${minTimeout}"
            e.printStackTrace();
        }
        
        assertTrue(elapsedTime>=minTimeout)
        assertTrue(elapsedTime-minTimeout<1000);

    }

     public void testQueryTimeoutWithOracle() throws Exception {
        def minTimeout=3000;

        ConnectionParam param = DatabaseConnectionImplTestUtils.getConnectionParam(DatabaseTestConstants.ORACLE);
        param.setMinTimeout(minTimeout);

         conn.init(param);

//        DriverManager.getDriver("jdbc:oracle:thin:@192.168.1.127:1521:xe").getPropertyInfo("jdbc:oracle:thin:@192.168.1.127:1521:xe",new Properties()).each { propInfo ->
//           println " ${propInfo.name}:${propInfo.value} "
//        }

        conn.connect();


        java.sql.Connection dbcon=conn.getConnection();
        java.sql.Statement stmt=dbcon.createStatement();

        def startTime=0;
        def elapsedTime=0;
        try{
            startTime=System.currentTimeMillis();
            stmt.executeQuery ("""BEGIN
            dbms_lock.sleep(20);
            END ; """)
            fail("should throw exception");
        }
        catch(java.sql.SQLRecoverableException e)
        {
            println "exception occured ${e}"
            elapsedTime=System.currentTimeMillis()-startTime;
            e.printStackTrace()

        }


        println "elapsedTime ${elapsedTime} minTimeout ${minTimeout}"
        assertTrue(elapsedTime>=minTimeout)
        assertTrue(elapsedTime-minTimeout<1000);
    }

    public void testCreateConnectionPropertiesGeneratesTimeoutsForMysql()
    {
        def minTimeout=3000;
        ConnectionParam param = DatabaseConnectionImplTestUtils.getConnectionParam(DatabaseTestConstants.MYSQL);
        param.setMinTimeout(minTimeout);

        conn.init(param);
        
        Properties info=conn.createConnectionProperties();
        assertEquals(4,info.size());

        assertEquals(conn.username,info.get("user"))
        assertEquals(conn.password,info.get("password"))
        
        assertEquals(minTimeout.toString(),info.get("connectTimeout"))
        assertEquals(minTimeout.toString(),info.get("socketTimeout"))

    }

    public void testCreateConnectionPropertiesGeneratesTimeoutsForOracle()
    {
        def minTimeout=3000;
        ConnectionParam param = DatabaseConnectionImplTestUtils.getConnectionParam(DatabaseTestConstants.ORACLE);
        param.setMinTimeout(minTimeout);

        conn.init(param);

        Properties info=conn.createConnectionProperties();
        assertEquals(4,info.size())
        assertEquals(conn.username,info.get("user"))
        assertEquals(conn.password,info.get("password"))

        assertEquals(minTimeout.toString(),info.get("oracle.net.CONNECT_TIMEOUT"))
        assertEquals(minTimeout.toString(),info.get("oracle.jdbc.ReadTimeout"))

    }

    public void testCreateConnectionPropertiesGeneratesTimeoutsForSybase()
    {
        def minTimeout=3000;
        ConnectionParam param = DatabaseConnectionImplTestUtils.getConnectionParam(DatabaseTestConstants.SYBASE);
        param.setMinTimeout(minTimeout);

        conn.init(param);

        Properties info=conn.createConnectionProperties();

        assertEquals(3,info.size())
        assertEquals(conn.username,info.get("user"))
        assertEquals(conn.password,info.get("password"))

        assertEquals(minTimeout.toString(),info.get("SESSION_TIMEOUT"))


    }

}
