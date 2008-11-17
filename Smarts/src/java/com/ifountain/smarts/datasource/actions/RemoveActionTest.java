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
import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValObjRef;
import com.smarts.repos.MR_AnyValObjRefSet;
import com.smarts.repos.MR_Ref;

public class RemoveActionTest extends SmartsTestCase {

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
        
        String className1 = "Router";
        String className2 = "Cable";
        String instanceName1 = "router1";
        String instanceName2 = "cable1";
        String propertyName = "ConnectedVia";
        MR_AnyVal expectedPropertyValue = new MR_AnyValObjRef(new MR_Ref(className2, instanceName2));
        RemoveAction action = new RemoveAction(className1, instanceName1, propertyName, expectedPropertyValue);
        try {
            action.execute(datasource);
            fail("should throw exception");
        } catch (Exception e) {
        }
        
        datasource._connect();
        SmRemoteDomainManager domainManager = datasource.getDomainManager();
        
        try
        {
            domainManager.deleteInstance(className1, instanceName1);
        }
        catch (Exception e)
        {
        }
        try
        {
            domainManager.deleteInstance(className2, instanceName2);
        }
        catch (Exception e)
        {
        }
        
        try
        {
            domainManager.createInstance(className1, instanceName1);
        }
        catch (Exception e)
        {
        }
        try
        {
            domainManager.createInstance(className2, instanceName2);
        }
        catch (Exception e)
        {
        }
        
        assertTrue("Instance doesnot exists in repository", domainManager.instanceExists(className1, instanceName1));
        assertTrue("Instance doesnot exists in repository", domainManager.instanceExists(className2, instanceName2));
        domainManager.insert(className1, instanceName1, propertyName, expectedPropertyValue);
        MR_AnyVal relation = domainManager.get(className1, instanceName1, propertyName);
        assertNotNull("Relation doesnot inserted",relation);
        MR_Ref[] relations = ((MR_AnyValObjRefSet)relation).getObjRefSetValue();
        assertEquals(1, relations.length);
        assertEquals("Relation doesnot inserted",instanceName2, relations[0].getInstanceName());
        
        action.execute(datasource);
        
        relation = domainManager.get(className1, instanceName1, propertyName);
        relations = ((MR_AnyValObjRefSet)relation).getObjRefSetValue();
        assertEquals(0, relations.length);
        
    }
}
