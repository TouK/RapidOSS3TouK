/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
package com.ifountain.smarts.connection;

import com.ifountain.smarts.test.util.SmartsTestCase;

import org.apache.log4j.Logger;

public class SmartsConnectionCheckerTest extends SmartsTestCase {
   
    public void testSmartsConnectionCheckerStopsWhenDisconnected() throws Exception{
        SmartsConnectionImplMock connection=new SmartsConnectionImplMock(); 
        SmartsConnectionChecker checker=new SmartsConnectionChecker(connection,Logger.getRootLogger());


        assertFalse(checker.isAlive());
        assertFalse(connection.isConnectedOnce());
        connection._connect();
        assertTrue(connection.isConnectedOnce());
        
        checker.start();
        assertTrue(checker.isAlive());
        assertTrue(connection.isConnectedOnce());
        
        connection.isConnected=false;

        Thread.sleep(2000);

        
        assertFalse(checker.isAlive());
        assertFalse(connection.isConnectedOnce());
    }
    public void testSmartsConnectionCheckerStopsWhenStopped()  throws Exception{
        SmartsConnectionImplMock connection=new SmartsConnectionImplMock();
        SmartsConnectionChecker checker=new SmartsConnectionChecker(connection,Logger.getRootLogger());

        assertFalse(checker.isAlive());
        assertFalse(connection.isConnectedOnce());
        connection._connect();
        assertTrue(connection.isConnectedOnce());
        
        checker.start();
        assertTrue(checker.isAlive());
        assertTrue(connection.isConnectedOnce());
        checker.stopChecker();

        Thread.sleep(2000);
      
        assertFalse(checker.isAlive());
        assertTrue(connection.isConnectedOnce());
    }
    
    class SmartsConnectionImplMock extends SmartsConnectionImpl{
        public boolean isConnected=true;

        public boolean checkConnection() {
            return isConnected;
        }

        protected void connect() throws Exception {
            
        }

        protected void disconnect() {

        }

    }   
    
}

