/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 20, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.datasource;

import com.ifountain.core.datasource.Action;
import com.ifountain.smarts.datasource.actions.*;
import com.ifountain.smarts.test.mocks.BaseSmartsAdapterMock;
import com.ifountain.smarts.test.util.SmartsTestCase;
import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValObjRef;
import com.smarts.repos.MR_AnyValString;
import com.smarts.repos.MR_Ref;

import java.util.ArrayList;

public class BaseSmartsAdapterTest extends SmartsTestCase {

    BaseSmartsAdapterMock mockAdapter;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mockAdapter = new BaseSmartsAdapterMock("", 0);
    }
    
    @Override
    protected void tearDown() throws Exception {
        mockAdapter.executedActions = new ArrayList<Action>();
        super.tearDown();
    }
    
    public void testCreateInstance() throws Exception {
        mockAdapter.createInstance("Router", "router1");
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof CreateInstanceAction);
    }
    public void testDeleteInstance() throws Exception {
        mockAdapter.deleteInstance("Router", "router1");
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof DeleteInstanceAction);
    }
    
    public void testFindInstances() throws Exception {
        mockAdapter.findInstances(".*", ".*", 0);
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof FindInstancesAction);
    }
    public void testGet() throws Exception {
        mockAdapter.get("Router", "router1", "Model");
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof GetAction);
    }
    public void testGetAllProperties() throws Exception {
        mockAdapter.getAllProperties("Router", "router1", 0);
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof GetAllPropertiesAction);
    }
    public void testGetAttributeNames() throws Exception {
        mockAdapter.getAttributeNames("Router");
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof GetAttributeNamesAction);
    }
    public void testGetAttributeTypes() throws Exception {
        mockAdapter.getAttributeTypes("Router");
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof GetAttributeTypesAction);
    }
    public void testGetChildren() throws Exception {
        mockAdapter.getChildren("Router");
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof GetChildrenAction);
    }
    public void testGetInstances() throws Exception {
        mockAdapter.getInstances();
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof GetInstancesAction);
    }
    public void testGetInstancesWithClassName() throws Exception {
        mockAdapter.getInstances("Router");
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof GetInstancesAction);
    }
    public void testGetProperties() throws Exception {
        mockAdapter.getProperties("Router", "router1", new String[]{"Model"});
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof GetPropertiesAction);
    }
    public void testGetRelationNames() throws Exception {
        mockAdapter.getRelationNames("Router");
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof GetRelationNamesAction);
    }
    public void testGetPropNames() throws Exception {
        mockAdapter.getPropNames("Router");
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof GetPropNamesAction);
    }
    public void testGetPropType() throws Exception {
        mockAdapter.getPropType("Router", "Model");
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof GetPropTypeAction);
    }
    public void testGetRelationTypes() throws Exception {
        mockAdapter.getRelationTypes("Router");
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof GetRelationTypesAction);
    }
    public void testInsert() throws Exception {
        mockAdapter.insert("Router", "router1", "ConnectedVia", new MR_AnyValObjRef(new MR_Ref("Cable", "cable1")));
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof InsertAction);
    }
    public void testInstanceExists() throws Exception {
        mockAdapter.instanceExists("Router", "router1");
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof InstanceExistsAction);
    }
    public void testInvoke() throws Exception {
        mockAdapter.invokeOperationWithNativeParams("ICIM_ObjectFactory", "ICIM-ObjectFactory", "findComputerSystem", new MR_AnyVal[]{new MR_AnyValString("router1")});
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof InvokeOperationWithNativeParamsAction);
    }
    public void testPut() throws Exception {
        mockAdapter.put("Router", "router1", "Location", new MR_AnyValString("Ankara"));
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof PutAction);
    }
    public void testRemove() throws Exception {
        mockAdapter.remove("Router", "router1", "Location", new MR_AnyValString("Ankara"));
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof RemoveAction);
    }
    public void testGetReverseRelation() throws Exception {
        mockAdapter.getReverseRelation("Router", "ConnectedVia");
        assertEquals(1, mockAdapter.executedActions.size());
        assertTrue(mockAdapter.executedActions.get(0) instanceof GetReverseRelationAction);
    }
}
