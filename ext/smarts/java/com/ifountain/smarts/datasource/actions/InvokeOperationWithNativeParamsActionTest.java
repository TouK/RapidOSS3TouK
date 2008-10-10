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
import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValString;

public class InvokeOperationWithNativeParamsActionTest extends SmartsTestCase {

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

        String className = "ICIM_ObjectFactory";
        String instanceName = "ICIM-ObjectFactory";
        String opName = "findComputerSystem";
        
        String routerClassName = "Router";
        String routerInstanceName = "router1";
        MR_AnyVal[] opParams = {new MR_AnyValString(routerInstanceName)};
        
        InvokeOperationWithNativeParamsAction action = new InvokeOperationWithNativeParamsAction(className, instanceName, opName, opParams);
        try {
            action.execute(datasource);
            fail("should throw exception");
        } catch (Exception e) {
        }
        
        datasource._connect();
        try
        {
            datasource.getDomainManager().createInstance(routerClassName, routerInstanceName);
        }
        catch (Exception e)
        {
        }
       
        action.execute(datasource);
        MR_AnyVal expected = datasource.getDomainManager().invokeOperation(className, instanceName, opName, opParams);
        assertEquals(expected, action.getInvokeResult());
    }
}
