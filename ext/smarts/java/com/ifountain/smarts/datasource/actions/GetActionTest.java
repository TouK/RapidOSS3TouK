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
import com.smarts.remote.SmRemoteDomainManager;
import com.smarts.repos.MR_AnyValString;

public class GetActionTest extends SmartsTestCase {

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
        ConnectionParam param = SmartsTestUtils.getDatasourceParam();
        datasource.init(param);
        
        String className = "Router";
        String instanceName = "router1";
        String propertyName = "Location";
        GetAction action = new GetAction(className, instanceName, propertyName);
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
        MR_AnyValString propertyValue = new MR_AnyValString("Ankara");
        domainManager.put(className, instanceName, propertyName, propertyValue);
        
        action.execute(datasource);
        assertEquals(propertyValue, action.getPropertyValue());
        
    }
}
