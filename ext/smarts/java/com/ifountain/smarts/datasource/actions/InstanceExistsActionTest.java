/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 19, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.datasource.actions;

import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.test.util.DatasourceTestUtils;
import com.ifountain.smarts.connection.SmartsConnectionImpl;
import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.test.util.SmartsTestUtils;
import com.smarts.remote.SmRemoteDomainManager;

public class InstanceExistsActionTest extends SmartsTestCase {

    SmartsConnectionImpl datasource;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        datasource = new SmartsConnectionImpl();
    }
    @Override
    protected void tearDown() throws Exception {
        if(datasource.isConnected()){
            datasource._disconnect();
        }
        super.tearDown();
    }
    public void testExecute() throws Exception {
        ConnectionParam param = DatasourceTestUtils.getParamSupplier().getConnectionParam(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME);
        datasource.init(param);
        
        String className = "Router";
        String instanceName = "router1";
        InstanceExistsAction action = new InstanceExistsAction(className, instanceName);
        try {
            action.execute(datasource);
            fail("should throw exception");
        } catch (Exception e) {
        }
        
        datasource._connect();
        SmRemoteDomainManager domainManager = datasource.getDomainManager();
        try {
            domainManager.deleteInstance(className, instanceName);
        } catch (Exception e) {
        }
        assertFalse(domainManager.instanceExists(className, instanceName));
        action.execute(datasource);
        assertFalse(action.instanceExists());
        
        try {
            domainManager.createInstance(className, instanceName);
        } catch (Exception e) {
        }
        assertTrue(domainManager.instanceExists(className, instanceName));
        action = new InstanceExistsAction(className, instanceName);
        action.execute(datasource);
        assertTrue(action.instanceExists());
    }
}
