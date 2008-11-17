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
import com.smarts.repos.MR_PropertyNameValue;

import java.util.Arrays;

public class GetAllPropertiesActionTest extends SmartsTestCase {

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
        
        
    public void testGetWithAllProperties() throws Exception {
        String className = "Router";
        String instanceName = "router1";
        long propertyTypeFlag = MR_PropertyNameValue.MR_BOTH;
        GetAllPropertiesAction action = new GetAllPropertiesAction(className, instanceName, propertyTypeFlag);
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
        
        try {
            domainManager.createInstance(className, instanceName);
        } catch (Exception e) {
        }
        assertTrue(domainManager.instanceExists(className, instanceName));
        
        MR_PropertyNameValue[] expected = domainManager.getAllProperties(className, instanceName, propertyTypeFlag);
        action.execute(datasource);
        assertTrue(Arrays.equals(expected, action.getAllProperties()));
    }
    
    public void testExecuteWithAttributesOnly() throws Exception {
        String className = "Router";
        String instanceName = "router1";
        long propertyTypeFlag = MR_PropertyNameValue.MR_ATTRS_ONLY;
        datasource._connect();
        SmRemoteDomainManager domainManager = datasource.getDomainManager();
        try {
            domainManager.deleteInstance(className, instanceName);
        } catch (Exception e) {
        }
        
        try {
            domainManager.createInstance(className, instanceName);
        } catch (Exception e) {
        }
        assertTrue(domainManager.instanceExists(className, instanceName));
        GetAllPropertiesAction action = new GetAllPropertiesAction(className, instanceName, propertyTypeFlag);
        action.execute(datasource);
        MR_PropertyNameValue[] expected = domainManager.getAllProperties(className, instanceName, propertyTypeFlag);
        assertTrue(Arrays.equals(expected, action.getAllProperties()));
        
    }
}
