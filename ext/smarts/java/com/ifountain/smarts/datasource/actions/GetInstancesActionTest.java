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

import java.util.Arrays;

public class GetInstancesActionTest extends SmartsTestCase {

    SmartsConnectionImpl datasource;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        datasource = new SmartsConnectionImpl();
        ConnectionParam param = DatasourceTestUtils.getParamSupplier().getConnectionParam(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME);
        datasource.init(param);
    }
    @Override
    protected void tearDown() throws Exception {
        if(datasource.isConnected()){
            datasource._disconnect();
        }
        super.tearDown();
    }
    public void testExecuteWithClassName() throws Exception {
        String className = "Router";
        String instanceName1 = "router1";
        String instanceName2 = "router1";
        GetInstancesAction action = new GetInstancesAction(className);
        try {
            action.execute(datasource);
            fail("should throw exception");
        } catch (Exception e) {
        }
        
        datasource._connect();
        SmRemoteDomainManager domainManager = datasource.getDomainManager();
        try {
            domainManager.createInstance(className, instanceName1);
        } catch (Exception e) {
        }
        try {
            domainManager.createInstance(className, instanceName2);
        } catch (Exception e) {
        }
        action.execute(datasource);
        String[] expected = domainManager.getInstances(className);
        assertTrue(Arrays.equals(expected, action.getInstances()));
    }
    
    public void testGetAllInstances() throws Exception {
        GetInstancesAction action = new GetInstancesAction();
        datasource._connect();
        SmRemoteDomainManager domainManager = datasource.getDomainManager();
        action.execute(datasource);
        String[] expected = domainManager.getInstances();
        assertTrue(Arrays.equals(expected, action.getInstances()));
        
    }
}
