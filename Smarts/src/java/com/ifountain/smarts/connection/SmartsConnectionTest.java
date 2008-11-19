/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
package com.ifountain.smarts.connection;

import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException;
import com.ifountain.core.test.util.DatasourceTestUtils;
import com.ifountain.smarts.test.util.SmartsConnectionParams;
import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.test.util.SmartsTestConstants;
import com.ifountain.smarts.test.util.SmartsTestUtils;
import com.ifountain.smarts.connection.mocks.SmartsConnectionImplMock;
import com.smarts.remote.SmRemoteException;

import java.net.UnknownHostException;

public class SmartsConnectionTest extends SmartsTestCase {

	SmartsConnectionImpl datasource;
	SmartsConnectionParams connectionParams;
	public SmartsConnectionTest() {
		connectionParams = SmartsTestUtils.getSmartsConnectionParams(SmartsTestConstants.SMARTS_SAM_CONNECTION_TYPE);
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
		ConnectionParam param = DatasourceTestUtils.getParamSupplier().getConnectionParam(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME);
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
        ConnectionParam param = DatasourceTestUtils.getParamSupplier().getConnectionParam(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME);
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

    public void testConnectWithSecureBroker() throws Exception {
        assertFalse(datasource.isConnected());
        DatasourceTestUtils.getParamSupplier().setParam(SmartsTestUtils.getConnectionParam(SmartsTestConstants.SMARTS_SECURE_SAM_CONNECTION_TYPE));
        ConnectionParam param = DatasourceTestUtils.getParamSupplier().getConnectionParam(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME);
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
	    ConnectionParam param = DatasourceTestUtils.getParamSupplier().getConnectionParam(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME);
	    param.getOtherParams().put(SmartsConnectionImpl.BROKER, "InvalidBroker");
        datasource.init(param);
        try {
            datasource.connect();
            fail("should throw exception");
        } catch (UnknownHostException e) {
            
        }
    }
	
	public void testDisconnect() throws Exception {
	    ConnectionParam param = DatasourceTestUtils.getParamSupplier().getConnectionParam(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME);
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
    public void testDisconnectDetachesDomainManager() throws Exception {
	    ConnectionParam param = DatasourceTestUtils.getParamSupplier().getConnectionParam(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME);
        SmartsConnectionImplMock connection=new SmartsConnectionImplMock(); 
        connection.init(param);
        connection.connect();
        assertTrue(connection.isConnected());
        assertFalse(SmartsConnectionImplMock.detachCalled);
        connection.disconnect();

        assertTrue(SmartsConnectionImplMock.detachCalled);
    }
}
