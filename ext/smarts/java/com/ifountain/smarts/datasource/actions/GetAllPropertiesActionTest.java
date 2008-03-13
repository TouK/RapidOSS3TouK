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

import java.util.Arrays;

import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.smarts.connection.SmartsConnectionImpl;
import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.test.util.SmartsTestUtils;
import com.smarts.remote.SmRemoteDomainManager;
import com.smarts.repos.MR_PropertyNameValue;

public class GetAllPropertiesActionTest extends SmartsTestCase {

    SmartsConnectionImpl datasource;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        datasource = new SmartsConnectionImpl();
        ConnectionParam param = SmartsTestUtils.getDatasourceParam();
        datasource.init(param);
    }
    @Override
    protected void tearDown() throws Exception {
        if(datasource.isConnected()){
            datasource.disconnect();
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
        
        datasource.connect();
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
        datasource.connect();
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
