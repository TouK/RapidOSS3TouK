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
public class BaseConnectionTest extends RapidCoreTestCase{

     public void testGetConnection() throws Exception{
         BaseConnection conn = new BaseConnection(){

             public void init(ConnectionParam param) throws Exception {
             }

             public ConnectionParam getParameters() {
                 return null;
             }

             public boolean isConnected() {
                 return false;
             }

             public void connect() throws Exception {
             }
             public void disconnect() {
             }
         };
         assertFalse(conn.isConnectedOnce());
         conn.init(new ConnectionParam("type", "name", "class", new HashMap()));
         conn._connect();
         assertTrue(conn.isConnectedOnce());

         conn._disconnect();
         assertFalse(conn.isConnectedOnce());
     }
}
