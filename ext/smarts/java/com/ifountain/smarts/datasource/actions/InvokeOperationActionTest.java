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
 * Created on Feb 19, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.datasource.actions;

import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.smarts.connection.SmartsConnectionImpl;
import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.test.util.SmartsTestUtils;
import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValString;

public class InvokeOperationActionTest extends SmartsTestCase {

    SmartsConnectionImpl datasource;
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
    public void testExecute() throws Exception {
        ConnectionParam param = SmartsTestUtils.getDatasourceParam();
        datasource.init(param);

        String className = "ICIM_ObjectFactory";
        String instanceName = "ICIM-ObjectFactory";
        String opName = "findComputerSystem";
        
        String routerClassName = "Router";
        String routerInstanceName = "router1";
        MR_AnyVal[] opParams = {new MR_AnyValString(routerInstanceName)};
        
        InvokeOperationAction action = new InvokeOperationAction(className, instanceName, opName, opParams);
        try {
            action.execute(datasource);
            fail("should throw exception");
        } catch (Exception e) {
        }
        
        datasource.connect();
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
