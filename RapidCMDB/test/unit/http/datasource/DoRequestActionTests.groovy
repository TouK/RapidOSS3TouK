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
 * Created on Feb 25, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package http.datasource

import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.core.connection.ConnectionParam
import connection.HttpConnectionImpl
import datasource.DoRequestAction
import org.apache.commons.httpclient.HttpException;

public class DoRequestActionTests extends GroovyTestCase {

    public void testExecute() throws Exception {
    	HttpConnectionImpl conn = new HttpConnectionImpl();
        Map otherParams = [:];
        otherParams.put(HttpConnectionImpl.BASE_URL, "http://localhost:9999");
        ConnectionParam param = new ConnectionParam("ds", HttpConnectionImpl.class.getName(), otherParams);
        conn.init(param);
        HttpUtilsMock httpUtilsMock = new HttpUtilsMock(); 
        conn.setHttpConnection(httpUtilsMock);
        DoRequestAction action = new DoRequestAction(TestLogUtils.log, "/Datasource/list", [:], DoRequestAction.GET, null);
        action.execute(conn);
        assertEquals("<GetRequest></GetRequest>", action.getResponse());
        httpUtilsMock.willThrowException = true;
        conn.setHttpConnection(httpUtilsMock);
        action = new DoRequestAction(TestLogUtils.log, "/Datasource/list", [:], DoRequestAction.GET, null);
        try {
            action.execute(conn);
            fail("should throw exception");
        } catch (HttpException e) {
        }
        httpUtilsMock = new HttpUtilsMock();
        conn.setHttpConnection(httpUtilsMock);
        action = new DoRequestAction(TestLogUtils.log, "/Datasource/list", [:], DoRequestAction.POST, null);
        action.execute(conn);
        assertEquals("<PostRequest></PostRequest>", action.getResponse());
        httpUtilsMock.willThrowException = true;
        conn.setHttpConnection(httpUtilsMock);
        action = new DoRequestAction(TestLogUtils.log, "/Datasource/list", [:], DoRequestAction.POST, null);
        try {
            action.execute(conn);
            fail("should throw exception");
        } catch (HttpException e) {
        }
        
    }
    

}
