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
package com.ifountain.smarts.connection;

import java.net.UnknownHostException;

import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException;
import com.ifountain.smarts.test.util.SmartsConnectionParams;
import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.test.util.SmartsTestUtils;
import com.smarts.remote.SmRemoteException;

public class SmartsConnectionTest extends SmartsTestCase {

	SmartsConnectionImpl datasource;
	SmartsConnectionParams connectionParams;
	public SmartsConnectionTest() {
		connectionParams = SmartsTestUtils.getConnectionParams();
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		datasource = new SmartsConnectionImpl();
	}
	@Override
	protected void tearDown() throws Exception {
		if(datasource.isConnected()){
			datasource.disconnect();
		}
		super.tearDown();
	}
	
	public void testInit() throws Exception {
		ConnectionParam param = SmartsTestUtils.getDatasourceParam();
		try {
			datasource.init(param);
		} catch (Exception e) {
			fail("should not throw exception");
		}
		assertSame(param, datasource.getParameters());
		assertNotNull(datasource.getDomainManager());
		
		param.getOtherParams().remove(SmartsConnectionImpl.BROKER);
        try {
        	datasource.init(param);
            fail("should throw exception");
        } catch (UndefinedConnectionParameterException e) {
        }
        param.getOtherParams().put(SmartsConnectionImpl.BROKER, connectionParams.getBroker());
        param.getOtherParams().remove(SmartsConnectionImpl.DOMAIN);
        try {
        	datasource.init(param);
            fail("should throw exception");
        } catch (UndefinedConnectionParameterException e) {
        }
        param.getOtherParams().put(SmartsConnectionImpl.DOMAIN, connectionParams.getDomain());
        param.getOtherParams().remove(SmartsConnectionImpl.USERNAME);
        try {
        	datasource.init(param);
            fail("should throw exception");
        } catch (UndefinedConnectionParameterException e) {
        }
        param.getOtherParams().put(SmartsConnectionImpl.USERNAME, connectionParams.getUsername());
        param.getOtherParams().remove(SmartsConnectionImpl.PASSWORD);
        try {
        	datasource.init(param);
            fail("should throw exception");
        } catch (UndefinedConnectionParameterException e) {
        }
	}
	
	public void testConnect() throws Exception {
        assertFalse(datasource.isConnected());
        ConnectionParam param = SmartsTestUtils.getDatasourceParam();
        datasource.init(param);
        assertFalse(datasource.isConnected());
        
        datasource.connect();
        assertTrue(datasource.isConnected());
        
        try
        {
            datasource.getDomainManager().noop();
        }
        catch (Exception e1)
        {
            fail("Should not throw exception because it is already connected");
        }
        
        try {
            datasource.connect();
        } catch (Exception e) {
            fail("should not throw exception");
        }
    }
	
	public void testConnectThrowsExceptionWithInvalidParams() throws Exception {
	    ConnectionParam param = SmartsTestUtils.getDatasourceParam();
	    param.getOtherParams().put(SmartsConnectionImpl.BROKER, "InvalidBroker");
        datasource.init(param);
        try {
            datasource.connect();
            fail("should throw exception");
        } catch (UnknownHostException e) {
            
        }
    }
	
	public void testDisconnect() throws Exception {
	    ConnectionParam param = SmartsTestUtils.getDatasourceParam();
        datasource.init(param);
        datasource.connect();
        assertTrue(datasource.isConnected());
        
        datasource.disconnect();
        
        assertFalse(datasource.isConnected());
        
        try{
            datasource.getDomainManager().noop();
            fail("Should throw exception");
        }catch (SmRemoteException e){
        }
    }
}
