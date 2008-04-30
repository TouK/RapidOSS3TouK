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
package com.ifountain.smarts.datasource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.test.util.SmartsTestUtils;
import com.ifountain.smarts.util.SmartsPropertyHelper;
import com.smarts.remote.SmRemoteException;
import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValObjRef;
import com.smarts.repos.MR_AnyValObjRefSet;
import com.smarts.repos.MR_AnyValString;
import com.smarts.repos.MR_AnyValVoid;
import com.smarts.repos.MR_Ref;

public class BaseTopologyAdapterTest extends SmartsTestCase {

    BaseTopologyAdapter topologyAdapter;
    String className = "Switch";
    String instanceName = "eraaswiad";
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        topologyAdapter = SmartsTestUtils.getTopologyAdapter();
    }
    
    public void testFetchTopoInstances() throws Exception
    {
        SmartsTestUtils.deleteAllTopologyInstances(className, ".*");
        topologyAdapter.createTopologyInstanceWithProperties(className, instanceName, new HashMap<String, String>());
        Iterator<Map<String, Object>> results = topologyAdapter.fetchObjects(className, ".*", 10);
        int count = 0;
        while(results.hasNext())
        {
            Map<String, Object> rec = results.next();
            count++;
            assertEquals(className, rec.get("CreationClassName"));
            assertEquals(topologyAdapter.getAttributeNames(className).length + topologyAdapter.getRelationNames(className).length, rec.size());
        }
        assertEquals(1, count);
    }
    public void testGetTopoInstances() throws Exception
    {
        SmartsTestUtils.deleteAllTopologyInstances(className, ".*");
        topologyAdapter.createTopologyInstanceWithProperties(className, instanceName, new HashMap<String, String>());
        List<Map<String, Object>> objects = topologyAdapter.getObjects(className, ".*");
        assertEquals(1, objects.size());
        Map<String, Object> rec = objects.get(0);
        assertEquals(className, rec.get("CreationClassName"));
        assertEquals(topologyAdapter.getAttributeNames(className).length + topologyAdapter.getRelationNames(className).length, rec.size());
    }
    
    public void testFetchTopoInstancesWithExpDiabled() throws Exception
    {
        SmartsTestUtils.deleteAllTopologyInstances(className, ".*");
        topologyAdapter.createTopologyInstanceWithProperties(className, instanceName, new HashMap<String, String>());
        
        Iterator<Map<String, Object>> rset = topologyAdapter.fetchObjects(className, ".*", false, 10);
        assertFalse(rset.hasNext());
    }
    
    public void testFetchTopoInstancesWithNullProperties() throws Exception
    {
        SmartsTestUtils.deleteAllTopologyInstances(className, ".*");
        topologyAdapter.createTopologyInstanceWithProperties(className, instanceName, new HashMap<String, String>());
        Iterator<Map<String, Object>> rset = topologyAdapter.fetchObjects(className, ".*", true, 10);
        int count = 0;
        while(rset.hasNext())
        {
            Map<String, Object> rec = rset.next();
            count++;
            assertEquals(className, rec.get("CreationClassName"));
            assertEquals(topologyAdapter.getAttributeNames(className).length+ topologyAdapter.getRelationNames(className).length, rec.size());
        }
        assertEquals(1, count);
    }
    
    public void testFetchTopoInstancesWithEmptyProperties() throws Exception
    {
        SmartsTestUtils.deleteAllTopologyInstances(className, ".*");
        topologyAdapter.createTopologyInstanceWithProperties(className, instanceName, new HashMap<String, String>());
        Iterator<Map<String, Object>> rset = topologyAdapter.fetchObjects(className, ".*", new ArrayList<String>(), true, 10);
        int count = 0;
        while(rset.hasNext())
        {
            Map<String, Object> rec = rset.next();
            count++;
            assertEquals(className, rec.get("CreationClassName"));
            assertEquals(topologyAdapter.getAttributeNames(className).length+ topologyAdapter.getRelationNames(className).length, rec.size());
        }
        assertEquals(1, count);
    }
    
    public void testFetchTopoInstancesWithSpecifiedProperties() throws Exception
    {
        SmartsTestUtils.deleteAllTopologyInstances(className, ".*");
        String location = "Location";
        String locationValue = "Ankara";
        
        List<String> attrbutes = new ArrayList<String>();
        attrbutes.add(location);
        String unknownNotificationProperty = "UNKNOWN";
        attrbutes.add(unknownNotificationProperty);
        
        Map<String, String> propertyValues = new HashMap<String, String>();
        propertyValues.put(location, locationValue);
        topologyAdapter.createTopologyInstanceWithProperties(className, instanceName, propertyValues);
        
        Iterator<Map<String, Object>> rset = topologyAdapter.fetchObjects(className, ".*", attrbutes, true, 10);
        int count = 0;
        while(rset.hasNext())
        {
            Map<String, Object> rec = rset.next();
            count++;
            assertEquals(locationValue, rec.get(location));
            assertEquals(null, rec.get(unknownNotificationProperty));
            assertEquals(1, rec.size());
        }
        assertEquals(1, count);
    }
    
    public void testGetTopoInstance() throws Exception
    {
        SmartsTestUtils.deleteAllTopologyInstances(className, ".*");
        topologyAdapter.createTopologyInstanceWithProperties(className, instanceName, new HashMap<String, String>());
        
        Map<String, Object> rec = topologyAdapter.getObject(className, instanceName);
        assertEquals(className, rec.get("CreationClassName"));
        assertEquals(topologyAdapter.getAttributeNames(className).length + topologyAdapter.getRelationNames(className).length, rec.size());
    }
    
    public void testUpdateTopologyInstanceWithProperties() throws Exception
    {
        String className = "Switch";
        String instanceName = "ozgursswitchinstance";
        deleteInstances(new String[]{className}, new String[]{instanceName});
        
        //create the topology instance with some attributes
        
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("Vendor", "linksys");
        attributes.put("Model", "tempra");
        attributes.put("PrimaryOwnerName", "ozgur alkaner");

        topologyAdapter.createTopologyInstanceWithProperties(className, instanceName, attributes);

        MR_AnyVal[] switchProperties = topologyAdapter.getProperties(className, instanceName,
                new String[]{"Vendor", "Model", "PrimaryOwnerName"});
        assertEquals(3, switchProperties.length);
        assertEquals("linksys", ((MR_AnyValString)switchProperties[0]).toString());
        assertEquals("tempra", ((MR_AnyValString)switchProperties[1]).toString());
        assertEquals("ozgur alkaner", ((MR_AnyValString)switchProperties[2]).toString());
                
        attributes.put("Vendor", "datron");
        attributes.put("Model", "astra");
        attributes.put("PrimaryOwnerName", "mehmet");        
        
        //update topology instace
        topologyAdapter.updateTopologyInstanceWithProperties(className, instanceName, attributes);
        
        switchProperties = topologyAdapter.getProperties(className, instanceName, new String[]{"Vendor", "Model", "PrimaryOwnerName"});
        assertEquals(3, switchProperties.length);
        assertEquals("datron", ((MR_AnyValString)switchProperties[0]).toString());
        assertEquals("astra", ((MR_AnyValString)switchProperties[1]).toString());
        assertEquals("mehmet", ((MR_AnyValString)switchProperties[2]).toString());
        
        deleteInstances(new String[]{className}, new String[]{instanceName});
    }
    
    public void testCreateTopologyInstanceWithAttributes() throws Exception
    {
        String className = "Switch";
        String instanceName = "ozgursswitchinstance";
        deleteInstances(new String[]{className}, new String[]{instanceName});
                
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("Vendor", "linksys");
        attributes.put("Model", "tempra");
        attributes.put("PrimaryOwnerName", "ozgur alkaner");
        attributes.put("NoSuchAttribute", "NoSuchValue");

        topologyAdapter.createTopologyInstanceWithProperties(className, instanceName, attributes);

        MR_AnyVal[] switchProperties = topologyAdapter.getProperties(className, instanceName, new String[]{"Vendor", "Model", "PrimaryOwnerName", "NoSuchAttribute"});
        assertEquals(4, switchProperties.length);
        assertEquals("linksys", ((MR_AnyValString)switchProperties[0]).toString());
        assertEquals("tempra", ((MR_AnyValString)switchProperties[1]).toString());
        assertEquals("ozgur alkaner", ((MR_AnyValString)switchProperties[2]).toString());
        assertTrue(switchProperties[3] instanceof MR_AnyValVoid);
        
        deleteInstances(new String[]{className}, new String[]{instanceName});
    }
    
    public void testUpdatePropertyWithValueNotMatchingType() throws Exception
    {
                
        String  className = "ICS_Notification";
        String instanceName = "NOTIFICATION-Switch_eraswiad_Down";
        
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("Severity", "1");
        attributes.put("EventText", "eventtext1");
        topologyAdapter.createTopologyInstanceWithProperties(className, instanceName,attributes);
        
        MR_AnyVal[] values = topologyAdapter.getProperties(className, instanceName, new String[] {"Severity"});
        assertEquals("1", values[0].toString());
        
        attributes = new HashMap<String, String>();
        attributes.put("Severity", "aaa"); 
        attributes.put("EventText", "eventtext2");
        
        topologyAdapter.updateTopologyInstanceWithProperties(className, instanceName, attributes);
        
        //get the updated Location property value
        values = topologyAdapter.getProperties(className, instanceName, new String[]{"EventText"});
        assertEquals("eventtext2", values[0].toString());
        
        values = topologyAdapter.getProperties(className, instanceName, new String[]{"Severity"});
        assertEquals("1", values[0].toString());
    }
    
    public void testAddRelationShipBetweenTopologyObjects() throws Exception
    {
        //create a switch object (Switch, ozgursswitchinstance) 
        deleteInstances(new String[]{"Switch"}, new String[]{"ozgursswitchinstance"});

        topologyAdapter.createInstance("Switch", "ozgursswitchinstance");
        
        //create a cable object (Cable, ozgurscableinstance)
        deleteInstances(new String[]{"Cable"}, new String[]{"ozgurscableinstance"});        
        topologyAdapter.createInstance("Cable", "ozgurscableinstance");
        
        //create the relationship between two
                
        topologyAdapter.addRelationshipBetweenTopologyObjects("Switch", "ozgursswitchinstance", "Cable", "ozgurscableinstance",
                "ConnectedVia");

        MR_AnyVal[] switchProperties = topologyAdapter.getProperties("Switch", "ozgursswitchinstance", new String[]{"ConnectedVia"});
        MR_AnyValObjRefSet switchRefSet = (MR_AnyValObjRefSet)switchProperties[0];
        MR_Ref[] switchRef = switchRefSet.getObjRefSetValue();
        assertTrue(switchRef.length == 1);
        assertEquals("Cable",switchRef[0].getClassName());
        assertEquals("ozgurscableinstance",switchRef[0].getInstanceName());
                
        MR_AnyVal[] cableProperties = topologyAdapter.getProperties("Cable", "ozgurscableinstance", 
                new String[]{"ConnectedSystems"});
        MR_AnyValObjRefSet cableRefSet = (MR_AnyValObjRefSet)cableProperties[0];
        MR_Ref[] cableRef = cableRefSet.getObjRefSetValue();
        assertTrue(cableRef.length == 1);
        assertEquals("Switch",cableRef[0].getClassName());
        assertEquals("ozgursswitchinstance",cableRef[0].getInstanceName());
        
        deleteInstances(new String[]{"Switch"}, new String[]{"ozgursswitchinstance"});
        deleteInstances(new String[]{"Cable"}, new String[]{"ozgurscableinstance"});
    }
    
    public void addAddRelationToAlreadyExistingRelationShip() throws Exception
    {
        //create a switch object (Switch, ozgursswitchinstance) 
        deleteInstances(new String[]{"Switch"}, new String[]{"ozgursswitchinstance"});

        topologyAdapter.createInstance("Switch", "ozgursswitchinstance");
        
        //create a cable object (Cable, ozgurscableinstance)
        deleteInstances(new String[]{"Cable"}, new String[]{"ozgurscableinstance"});        
        topologyAdapter.createInstance("Cable", "ozgurscableinstance");
        
        //create a router object (Router, "ozgurrouter")
        deleteInstances(new String[]{"Router"}, new String[]{"ozgurrouter"});        
        topologyAdapter.createInstance("Router", "ozgurrouter");
        
        //create the relationship between two switch and cable
        topologyAdapter.addRelationshipBetweenTopologyObjects("Switch", "ozgursswitchinstance", "Cable", "ozgurscableinstance",
                "ConnectedVia");
        
        //create the relationship between two Router and cable
        topologyAdapter.addRelationshipBetweenTopologyObjects("Router", "ozgurrouter",
                "Cable", "ozgurscableinstance",
                "ConnectedVia");

        MR_AnyVal[] cableProperties = topologyAdapter.getProperties("Cable", "ozgurscableinstance",
                new String[]{"ConnectedSystems"});
        MR_AnyValObjRefSet cableRefSet = (MR_AnyValObjRefSet)cableProperties[0];
        MR_Ref[] cableRef = cableRefSet.getObjRefSetValue();
        assertTrue(cableRef.length == 2);
        assertEquals("Router",cableRef[0].getClassName());
        assertEquals("ozgurrouter",cableRef[0].getInstanceName());
        assertEquals("Switch",cableRef[0].getClassName());
        assertEquals("ozgursswitchinstance",cableRef[0].getInstanceName());
        
        deleteInstances(new String[]{"Switch"}, new String[]{"ozgursswitchinstance"});
        deleteInstances(new String[]{"Cable"}, new String[]{"ozgurscableinstance"});        
        deleteInstances(new String[]{"Router"}, new String[]{"ozgurrouter"});
    }
    
    public void testRemoveRelationShipBetweenTopologyObjects() throws Exception
    {
        //create a switch object (Switch, ozgursswitchinstance) 
        deleteInstances(new String[]{"Switch"}, new String[]{"ozgursswitchinstance"});

        topologyAdapter.createInstance("Switch", "ozgursswitchinstance");
        
        //create a cable object (Cable, ozgurscableinstance)
        deleteInstances(new String[]{"Cable"}, new String[]{"ozgurscableinstance"});        
        topologyAdapter.createInstance("Cable", "ozgurscableinstance");
        
        //create the relationship between two
                
        topologyAdapter.addRelationshipBetweenTopologyObjects("Switch", "ozgursswitchinstance", "Cable", "ozgurscableinstance",
                "ConnectedVia");
        
        topologyAdapter.removeTopologyRelationship("Switch", "ozgursswitchinstance", "Cable", "ozgurscableinstance",
                "ConnectedVia");

        
        MR_AnyVal[] switchProperties = topologyAdapter.getProperties("Switch", "ozgursswitchinstance",
                new String[]{"ConnectedVia"});
        
        MR_AnyValObjRefSet switchRefSet = (MR_AnyValObjRefSet)switchProperties[0];
        MR_Ref[] switchRef = switchRefSet.getObjRefSetValue();        
        assertEquals(0, switchRef.length);
        
        MR_AnyVal[] cableProperties = topologyAdapter.getProperties("Cable", "ozgurscableinstance", 
                new String[]{"ConnectedSystems"});
        
        MR_AnyValObjRefSet cableRefSet = (MR_AnyValObjRefSet)cableProperties[0];
        MR_Ref[] cableRef = cableRefSet.getObjRefSetValue();
        assertTrue(cableRef.length == 0);
                
        assertTrue(topologyAdapter.instanceExists("Switch", "ozgursswitchinstance"));
        assertTrue(topologyAdapter.instanceExists("Cable", "ozgurscableinstance"));
        
        deleteInstances(new String[]{"Switch"}, new String[]{"ozgursswitchinstance"});
        deleteInstances(new String[]{"Cable"}, new String[]{"ozgurscableinstance"});
    }
    
    public void testRemoveTopologyInstance() throws Exception
    {
        //create a switch object (Switch, ozgursswitchinstance) 
        deleteInstances(new String[]{"Switch"}, new String[]{"ozgursswitchinstance"});

        topologyAdapter.createInstance("Switch", "ozgursswitchinstance");
        
        //create a cable object (Cable, ozgurscableinstance)
        deleteInstances(new String[]{"Cable"}, new String[]{"cable1"});        
        topologyAdapter.createInstance("Cable", "cable1");
        
        deleteInstances(new String[]{"Cable"}, new String[]{"cable2"});        
        topologyAdapter.createInstance("Cable", "cable2");
                
        //create the relationship between Switch and cable1
                
        topologyAdapter.addRelationshipBetweenTopologyObjects("Switch", "ozgursswitchinstance", "Cable", "cable1",
                "ConnectedVia");
        
        //create the relationship between Switch and cable2
        
        topologyAdapter.addRelationshipBetweenTopologyObjects("Switch", "ozgursswitchinstance", "Cable", "cable2",
                "ConnectedVia");
        
        //delete the Switch instance
        
        topologyAdapter.deleteTopologyInstance("Switch", "ozgursswitchinstance");
        
        //now check if the 'ConnectedSystems' relationship exists in cable topolgy objects
        //it should not exist

        MR_AnyVal[] cableProperties = topologyAdapter.getProperties("Cable", "cable1",
                new String[]{"ConnectedSystems"});
        
        MR_AnyValObjRefSet cableRefSet = (MR_AnyValObjRefSet)cableProperties[0];
        MR_Ref[] cableRef = cableRefSet.getObjRefSetValue();
        assertTrue(cableRef.length == 0);

        cableProperties = topologyAdapter.getProperties("Cable", "cable2",
                new String[]{"ConnectedSystems"});
        
        cableRefSet = (MR_AnyValObjRefSet)cableProperties[0];
        cableRef = cableRefSet.getObjRefSetValue();
        assertTrue(cableRef.length == 0);
        
        deleteInstances(new String[]{"Switch"}, new String[]{"ozgursswitchinstance"});  
        deleteInstances(new String[]{"Cable"}, new String[]{"cable1"});
        deleteInstances(new String[]{"Cable"}, new String[]{"cable2"});
    }
    
    public void testOneToManyRelationshipBetweenIpAndRouterTopologyObjects() throws Exception
    {
        try
        {
            try
            {
                topologyAdapter.deleteTopologyInstance("IP", "IP_139.179.44.44");
                topologyAdapter.deleteTopologyInstance("Router", "erralink1");
            }
            catch (IOException e)
            {
            }
            catch (SmRemoteException e)
            {
            }
            try
            {
                //create an IP object (IP, IP_139.179.44.44)                   
                topologyAdapter.createInstance("IP", "IP_139.179.44.44");
            }
            catch (IOException e2)
            {
                e2.printStackTrace();
            }
            catch (SmRemoteException e2)
            {
                e2.printStackTrace();
            }
            
            try
            {
                //create a router object (Router, erralink1)
                topologyAdapter.createInstance("Router", "erralink1");
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            catch (SmRemoteException e1)
            {
                e1.printStackTrace();
            }
            
            //try to create a relationship between them
            //note that one of the relationships are automatically created
            topologyAdapter.addRelationshipBetweenTopologyObjects(
                    "IP", "IP_139.179.44.44","Router", "erralink1",                     
                    "HostedBy");

            //get the properties
            MR_AnyVal[] routerProperties = topologyAdapter.getProperties("Router", "erralink1", 
                    new String[]{"HostsAccessPoints"});
            
            MR_AnyValObjRefSet routerRefSet = (MR_AnyValObjRefSet)routerProperties[0];
            MR_Ref[] routerRef = routerRefSet.getObjRefSetValue();
            assertTrue(routerRef.length == 1);
            assertEquals("IP",routerRef[0].getClassName());
            assertEquals("IP_139.179.44.44",routerRef[0].getInstanceName());
            
            MR_AnyVal[] ipProperties = topologyAdapter.getProperties("IP", "IP_139.179.44.44", 
                    new String[]{"HostedBy"});
            
            MR_AnyValObjRef ipRefSet = (MR_AnyValObjRef)ipProperties[0];
            MR_Ref ipRef = ipRefSet.getObjRefValue();
            assertEquals("Router",ipRef.getClassName());
            assertEquals("erralink1",ipRef.getInstanceName());
                        
            //remove the created relationship
            topologyAdapter.removeTopologyRelationship( "IP", "IP_139.179.44.44",
                    "Router", "erralink1",                     
                    "HostedBy");            
            
            //get the properties
            
            routerProperties = topologyAdapter.getProperties("Router", "erralink1", 
                    new String[]{"HostsAccessPoints"});

            routerRefSet = (MR_AnyValObjRefSet)routerProperties[0];
            routerRef = routerRefSet.getObjRefSetValue();
            assertTrue(routerRef.length == 0);
            
            ipProperties = topologyAdapter.getProperties("IP", "IP_139.179.44.44", 
                    new String[]{"HostedBy"});
            
            ipRefSet = (MR_AnyValObjRef)ipProperties[0];   
            assertTrue(ipRefSet.getObjRefValue().isNull());
                        
        }
        finally
        {
            try
            {
                topologyAdapter.deleteTopologyInstance("IP", "IP_139.179.44.44");
                topologyAdapter.deleteTopologyInstance("Router", "erralink1");
            }
            catch (IOException e)
            {
            }
            catch (SmRemoteException e)
            {
            }
        }
    }

    
    /*
     * This test tries to add a relation to an already existing realatio
     * IP----HostedBy--->Router
     * Router-------HostsAccessPoints---->IP
     * 
     */
    public void testOneToManyRelationshipBetweenIpAndRouterTopologyObjectsByReversingArguments() throws Exception
    {
        try
        {
            try
            {
                topologyAdapter.deleteTopologyInstance("IP", "IP_139.179.44.44");
                topologyAdapter.deleteTopologyInstance("Router", "erralink1");
            }
            catch (IOException e)
            {
            }
            catch (SmRemoteException e)
            {
            }

            try
            {
                //create an IP object (IP, IP_139.179.44.44)                   
                topologyAdapter.createInstance("IP", "IP_139.179.44.44");
            }
            catch (IOException e2)
            {
                e2.printStackTrace();
            }
            catch (SmRemoteException e2)
            {
                e2.printStackTrace();
            }
            
            try
            {
                //create a router object (Router, erralink1)
                topologyAdapter.createInstance("Router", "erralink1");
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            catch (SmRemoteException e1)
            {
                e1.printStackTrace();
            }
            
            //try to create a relationship between them
            //note that one of the relationships are automatically created
            topologyAdapter.addRelationshipBetweenTopologyObjects("Router", "erralink1",
                    "IP", "IP_139.179.44.44",                     
                    "HostsAccessPoints");

            //get the properties
            MR_AnyVal[] routerProperties = topologyAdapter.getProperties("Router", "erralink1", 
                    new String[]{"HostsAccessPoints"});
            
            MR_AnyValObjRefSet routerRefSet = (MR_AnyValObjRefSet)routerProperties[0];
            MR_Ref[] routerRef = routerRefSet.getObjRefSetValue();
            assertTrue(routerRef.length == 1);
            assertEquals("IP",routerRef[0].getClassName());
            assertEquals("IP_139.179.44.44",routerRef[0].getInstanceName());
            
            MR_AnyVal[] ipProperties = topologyAdapter.getProperties("IP", "IP_139.179.44.44", 
                    new String[]{"HostedBy"});
            
            MR_AnyValObjRef ipRefSet = (MR_AnyValObjRef)ipProperties[0];
            MR_Ref ipRef = ipRefSet.getObjRefValue();
            assertEquals("Router",ipRef.getClassName());
            assertEquals("erralink1",ipRef.getInstanceName());
                        
            //remove the created relationship
            topologyAdapter.removeTopologyRelationship( "Router", "erralink1","IP", "IP_139.179.44.44",
                    "HostsAccessPoints");            
            
            //get the properties
            
            routerProperties = topologyAdapter.getProperties("Router", "erralink1", 
                    new String[]{"HostsAccessPoints"});

            routerRefSet = (MR_AnyValObjRefSet)routerProperties[0];
            routerRef = routerRefSet.getObjRefSetValue();
            assertTrue(routerRef.length == 0);
            
            ipProperties = topologyAdapter.getProperties("IP", "IP_139.179.44.44", 
                    new String[]{"HostedBy"});
            
            ipRefSet = (MR_AnyValObjRef)ipProperties[0];   
            assertTrue(ipRefSet.getObjRefValue().isNull());
                        
        }
        finally
        {
            try
            {
                topologyAdapter.deleteTopologyInstance("IP", "IP_139.179.44.44");
                topologyAdapter.deleteTopologyInstance("Router", "erralink1");
            }
            catch (IOException e)
            {
            }
            catch (SmRemoteException e)
            {
            }
        }
    }

    /*
     * This test tries to add a relation to an already existing realatio
     * Port----ReializedBy--->Card
     * Card----Reializes---->Port
     */
    
    public void testAddOneToManyRelationshipBetweenPortAndCardTopologyObjects() throws Exception
    {
        try
        {
            try
            {
                topologyAdapter.deleteTopologyInstance("Port", "myport");
                topologyAdapter.deleteTopologyInstance("Card", "mycard");
            }
            catch (IOException e)
            {
            }
            catch (SmRemoteException e)
            {
            }

            try
            {
                //create an IP object (IP, IP_139.179.44.44)                   
                topologyAdapter.createInstance("Port", "myport");
            }
            catch (IOException e2)
            {
                e2.printStackTrace();
            }
            catch (SmRemoteException e2)
            {
                e2.printStackTrace();
            }
            
            try
            {
                //create a router object (Router, erralink1)
                topologyAdapter.createInstance("Card", "mycard");
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            catch (SmRemoteException e1)
            {
                e1.printStackTrace();
            }
            
            //try to create a relationship between them
            //note that one of the relationships are automatically created
            topologyAdapter.addRelationshipBetweenTopologyObjects(
                    "Port", "myport","Card", "mycard",                     
                    "RealizedBy");            


        }
        finally
        {
            try
            {
                topologyAdapter.deleteTopologyInstance("Port", "myport");
                topologyAdapter.deleteTopologyInstance("Card", "mycard");
            }
            catch (IOException e)
            {
            }
            catch (SmRemoteException e)
            {
            }
        }           
    }
        
    public void testCreateSwitchInstance() throws Exception
    {
        String cableInstanceName = "cabledeneme";
        String cableClassName = "TrunkCable";
        deleteInstances(new String[]{cableClassName}, new String[]{cableInstanceName});

        topologyAdapter.createInstance(cableClassName, cableInstanceName);

        String switchClassName = "Switch";
        String switchInstanceName = "MyNewSwitch";

        deleteInstances(new String[]{switchClassName}, new String[]{switchInstanceName});

        topologyAdapter.createInstance(switchClassName, switchInstanceName);
        assertTrue("Instance should have been created", topologyAdapter.instanceExists(switchClassName, switchInstanceName));

        MR_AnyValObjRef relationSet = new MR_AnyValObjRef(new MR_Ref(cableClassName, cableInstanceName));
        topologyAdapter.createTopologyInstance(switchClassName, switchInstanceName, "ConnectedVia", relationSet);

        MR_AnyVal[] switchProperties = topologyAdapter.getProperties(switchClassName, switchInstanceName, new String[]{"ConnectedVia"});
        assertEquals(1, switchProperties.length);
        MR_AnyValObjRefSet cable_ref_set = (MR_AnyValObjRefSet)switchProperties[0];
        MR_Ref[] ref = cable_ref_set.getObjRefSetValue();
        assertEquals(1, ref.length);
        assertEquals(cableClassName, ref[0].getClassName());
        assertEquals(cableInstanceName, ref[0].getInstanceName());

        MR_AnyVal[] cableProperties = topologyAdapter.getProperties(cableClassName, cableInstanceName, new String[]{"ConnectedSystems"});
        assertEquals(1, cableProperties.length);
        MR_AnyValObjRefSet switch_ref_set = (MR_AnyValObjRefSet)cableProperties[0];
        MR_Ref[] switchRef = switch_ref_set.getObjRefSetValue();
        assertEquals(1, switchRef.length);
        assertEquals(switchClassName, switchRef[0].getClassName());
        assertEquals(switchInstanceName, switchRef[0].getInstanceName());

        deleteInstances(new String[]{switchClassName}, new String[]{switchInstanceName});
    }
    
    public void testMultipleUpdateWhenOneOfThemFailsFollowingOnesAreUpdated() throws Exception
    {
        try
        {
            topologyAdapter.deleteTopologyInstance("Switch", "eraaswiad");
        }
        catch (Exception e1)
        {
        }
        topologyAdapter.createInstance("Switch", "eraaswiad");
        
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("Model", "SLX");
        map.put("NonExistantProperty", "NonExistantPropertiesValue");
        map.put("Location", "Ankara");
        
        topologyAdapter.updateTopologyInstanceWithProperties("Switch", "eraaswiad", map);
    
        Map<String, String> allProps = SmartsPropertyHelper.getAllPropertiesOfInstanceAsMap(topologyAdapter, "Switch", "eraaswiad");
        assertEquals("SLX", allProps.get("Model"));
        assertEquals("Ankara", allProps.get("Location"));
        assertNull(allProps.get("NonExistantProperty"));
    }
    
    public void testGetObjectUsingMapListParams() throws Exception {
    	SmartsTestUtils.deleteAllTopologyInstances(className, ".*");
        Map atts = new HashMap();
        atts.put("Model", "model");
        atts.put("Vendor", "vendor");
        topologyAdapter.createTopologyInstanceWithProperties(className, instanceName, atts);

        Map idsMap = new HashMap();
        idsMap.put("CreationClassName", className);
        idsMap.put("Name", instanceName);

        List attsRequested = new ArrayList();
        attsRequested.add("Model");
        attsRequested.add("Vendor");
        Map<String, Object> rec = topologyAdapter.getObject(idsMap, attsRequested);
        assertEquals("model", rec.get("Model"));
        assertEquals("vendor", rec.get("Vendor"));

        idsMap.remove("CreationClassName");
        rec = topologyAdapter.getObject(idsMap, attsRequested);
        assertNull(rec);

        idsMap.put("CreationClassName", className);
        idsMap.remove("Name");
        rec = topologyAdapter.getObject(idsMap, attsRequested);
        assertNull(rec);
    }
    
    private void deleteInstances(String[] classNames, String[] instanceNames) throws Exception {
        assertTrue(classNames.length == instanceNames.length);
        for(int i = 0 ; i < classNames.length; i++)
        {
            try
            {
                topologyAdapter.deleteTopologyInstance(classNames[i], instanceNames[i]);
            } catch (Exception e) {

            }
        }
    }
}
