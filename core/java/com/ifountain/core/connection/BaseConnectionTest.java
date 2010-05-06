package com.ifountain.core.connection;

import com.ifountain.core.test.util.RapidCoreTestCase;

import java.util.HashMap;

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
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Apr 11, 2008
 * Time: 9:58:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class BaseConnectionTest extends RapidCoreTestCase {

    public void testGetConnection() throws Exception {
        BaseConnection conn = new BaseConnection() {
            public boolean isConnectionException(Throwable t) {
                return false;
            }

            public ConnectionParam getParameters() {
                return null;
            }

            public boolean checkConnection() {
                return true;
            }

            public void connect() throws Exception {
            }

            public void disconnect() {
            }
        };
        assertFalse(conn.isConnected());
        ConnectionParam param = new ConnectionParam("name", "class", new HashMap());
        conn.init(param);
        conn._connect();
        assertTrue(conn.isConnected());

        conn._disconnect();
        assertFalse(conn.isConnected());
    }

    public void testSetTimeout() throws Exception {
        BaseConnection conn = new BaseConnection() {
            public ConnectionParam getParameters() {
                return null;
            }

            public boolean isConnectionException(Throwable t) {
                return false;
            }

            public boolean checkConnection() {
                return true;
            }

            public void connect() throws Exception {
            }

            public void disconnect() {
            }
        };
        assertFalse(conn.isConnected());
        ConnectionParam param = new ConnectionParam("name", "class", new HashMap(), 10, 1000, 9000);
        conn.init(param);
        assertEquals(param.getMinTimeout(), conn.getTimeout());
        //timeout less than max 
        conn.setTimeout(100);
        assertEquals(param.getMinTimeout(), conn.getTimeout());

        //timeout greater than max
        conn.setTimeout(10000);
        assertEquals(param.getMaxTimeout(), conn.getTimeout());

        //an appropriate timeout value
        int timeout = 5000;
        conn.setTimeout(timeout);
        assertEquals(timeout, conn.getTimeout());
    }

    public void testInvalidate() {
        BaseConnection conn = new BaseConnection() {
            public ConnectionParam getParameters() {
                return null;
            }

            public boolean isConnectionException(Throwable t) {
                return false;
            }

            public boolean checkConnection() {
                return true;
            }

            public void connect() throws Exception {
            }

            public void disconnect() {
            }
        };
        assertTrue(conn.isValid());
        conn.invalidate();
        assertFalse(conn.isValid());
    }
}
