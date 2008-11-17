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

import java.util.Arrays;

public class GetAttributeTypesActionTest extends SmartsTestCase {

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
        GetAttributeTypesAction action = new GetAttributeTypesAction(className);
        try {
            action.execute(datasource);
            fail("should throw exception");
        } catch (Exception e) {
        }
        
        datasource._connect();
        action.execute(datasource);
        int[] expected = datasource.getDomainManager().getAttributeTypes(className);
        assertTrue(Arrays.equals(expected, action.getAttributeTypes()));
    }
}
