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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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
        SmartsTestUtils.deleteAllTopologyInstances("Router", ".*");
    }
    @Override
    protected void tearDown() throws Exception {
        if(datasource.isConnected()){
            datasource._disconnect();
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
        List opParams = new ArrayList();
        opParams.add(routerInstanceName);
        
        InvokeOperationAction action = new InvokeOperationAction(Logger.getRootLogger(), className, instanceName, opName, opParams);
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

        assertEquals("Router", ((HashMap)(action.getInvokeResult())).get("CreationClassName"));
        assertEquals("router1", ((HashMap)(action.getInvokeResult())).get("Name"));

        routerInstanceName = "router2";
        String routerDisplayName = "router2DisplayName";
        opParams.clear();
        opParams.add(routerInstanceName);
        opParams.add(routerDisplayName);


        action = new InvokeOperationAction(Logger.getRootLogger(), className, instanceName, "makeRouter", opParams);
        action.execute(datasource);

        Map<String, Object> instance = SmartsTestUtils.getTopologyAdapter().getObject(routerClassName, routerInstanceName);
        assertEquals(routerClassName, instance.get("CreationClassName"));
        assertEquals(routerInstanceName, instance.get("Name"));
        assertEquals(routerDisplayName, instance.get("DisplayName"));

        // TEST ANOTHER OPERATION (makeServiceOffering)
        ArrayList parameters = new ArrayList();
        parameters.add("NewOffering");
        
        opName = "makeServiceOffering";
//        action = new InvokeOperationAction(Logger.getRootLogger(), className, instanceName, opName, parameters);
//        action.execute(datasource);
//        assertEquals("true", ((HashMap)(action.getInvokeResult())).get("CreationClassName"));
//        assertEquals("true", ((HashMap)(action.getInvokeResult())));
        
        // TEST YET ANOTHER OPERATION (isInstanceOf)
        parameters = new ArrayList();
        parameters.add("Router");
        
        opName = "isInstanceOf";
        action = new InvokeOperationAction(Logger.getRootLogger(), "Router", "router1", opName, parameters);
        action.execute(datasource);
        assertEquals("true", ((HashMap)(action.getInvokeResult())).get("element0"));



    }
}
