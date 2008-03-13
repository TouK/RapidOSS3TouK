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
 * Created on Feb 21, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ifountain.smarts.datasource.BaseTopologyAdapter;
import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.test.util.SmartsTestUtils;
import com.smarts.repos.MR_PropertyNameValue;
import com.smarts.repos.MR_Ref;

public class SmartsPropertyHelperTest extends SmartsTestCase {

    public void testGetPropertiesForAttributes() throws Exception
    {
        SmartsTestUtils.deleteAllTopologyInstances("Host", ".*");
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("Location", "locationof");
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Host", "hostdeneme", attributes ,0,10);
        MR_PropertyNameValue[][] res = SmartsPropertyHelper.getProperties(SmartsTestUtils.getTopologyAdapter(), "Host", ".*", new String[]{"Location","Model"}, true);
        assertEquals(10, res.length);
        for (int i = 0; i < res.length; i++)
        {
            assertEquals("locationof", res[i][0].getPropertyValue().toString());
            assertEquals("", res[i][1].getPropertyValue().toString());
        }
    }
    
    public void testGetPropertiesForRelations() throws Exception
    {
        SmartsTestUtils.deleteAllTopologyInstances("Host", ".*");
        SmartsTestUtils.deleteAllTopologyInstances("Cable", ".*");
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("Location", "locationof");
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Host", "hostdeneme", attributes,0,10);
        attributes = new HashMap<String, String>();
        attributes.put("", "");
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Cable", "cabledeneme", attributes,0,10);
        for (int i = 0; i < 10; i++)
        {
            SmartsTestUtils.addRelationship("Host", "hostdeneme" + i , "Cable", "cabledeneme" + i,"ConnectedVia");
        }
        MR_PropertyNameValue[][] res = SmartsPropertyHelper.getProperties(SmartsTestUtils.getTopologyAdapter(), "Host", ".*", new String[]{"CreationClassName","Name","Location","ConnectedVia"}, true);
        assertEquals(10, res.length);
        for (int i = 0; i < res.length; i++)
        {
            assertEquals("locationof", res[i][2].getPropertyValue().toString());
            MR_Ref[] refs = (MR_Ref[])(res[i][3].getPropertyValue().getValue());
            assertEquals(1, refs.length);
            assertEquals("Cable", refs[0].getClassName());
            String propValue = res[i][1].getPropertyValue().toString();
            String cableInstanceName = "cabledeneme" + propValue.substring("hostdeneme".length(), propValue.length());
            assertEquals(cableInstanceName, refs[0].getInstanceName());
        }
    }
    
    public void testGetPropertiesGetsSubclassesProperties() throws Exception
    {
        BaseTopologyAdapter topologyAdapter = SmartsTestUtils.getTopologyAdapter();
        MR_Ref[] foundRefrences = topologyAdapter.getAllInstances("UnitaryComputerSystem", ".*", true);
        for (int i = 0; i < foundRefrences.length; i++)
        {
            topologyAdapter.deleteInstance(foundRefrences[i].getClassName(),foundRefrences[i].getInstanceName());
        }
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("Location", "locationof");
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Host", "hostdeneme", attributes,0,10);
        attributes = new HashMap<String, String>();
        attributes.put("", "");
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Switch", "switchdeneme",attributes,0,10);
        MR_PropertyNameValue[][] res = SmartsPropertyHelper.getProperties(topologyAdapter, "UnitaryComputerSystem", ".*", new String[]{"CreationClassName","Name","Location"}, true);
        assertEquals(20, res.length);
        for (int i = 0; i < res.length; i++)
        {
            if(res[i][0].getPropertyValue().toString().equals("Host"))
            {
                assertEquals("locationof", res[i][2].getPropertyValue().toString());
            }
            else
            {
                assertEquals("", res[i][2].getPropertyValue().toString());
            }
        }
    }
    
    public void testNonExistingPropertyNames() throws Exception
    {
        String[]props = {"Location", "A", "B"};
        List<String> nonexists = SmartsPropertyHelper.getNonExisentPropertyNames(SmartsTestUtils.getTopologyAdapter(), "Host", props);
        assertEquals(2, nonexists.size());
        assertEquals("A", nonexists.get(0));
        assertEquals("B", nonexists.get(1));
    }
}
