package com.ifountain.smarts.datasource;

import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.test.util.SmartsTestUtils;
import com.ifountain.smarts.test.util.SmartsTestConstants;
import com.ifountain.smarts.util.params.SmartsSubscribeParameters;
import com.ifountain.smarts.util.SmartsConstants;
import com.ifountain.smarts.connection.SmartsConnectionImpl;
import com.ifountain.smarts.datasource.actions.InvokeOperationAction;
import com.ifountain.comp.test.util.logging.TestLogUtils;
import com.ifountain.comp.test.util.CommonTestUtils;
import com.ifountain.comp.test.util.WaitAction;
import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.test.util.DatasourceTestUtils;

import java.util.*;

import org.apache.log4j.Logger;
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
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jul 15, 2008
 * Time: 11:53:45 AM
 */
public class SmartsTopologyListeningAdapterTest extends SmartsTestCase implements Observer {

    private SmartsTopologyListeningAdapter topologyAdapter;
    LinkedList receivedObjects;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SmartsTestUtils.deleteAllTopologyInstances("Router", ".*");
        SmartsTestUtils.deleteAllTopologyInstances("Host", ".*");
        SmartsTestUtils.deleteAllTopologyInstances("IPNetwork", ".*");
        SmartsTestUtils.deleteAllTopologyInstances("Cable", ".*");
        receivedObjects = new LinkedList();
    }

    @Override
    protected void tearDown() throws Exception {
        if (topologyAdapter != null) {
            topologyAdapter.unsubscribe();
        }
        super.tearDown();
    }

    public void testSubscriptionToTopologyObjects() throws Exception {
        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "routertrial.*", null);
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log, new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "routertrial", new HashMap(), 0, 1);
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "routertrial", 0, 1);
    }

    public void testSubscriptionToOneOfPropertiesOfTopologyObjects() throws Exception {
        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "trial.*", new String[]{"Location"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log, new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();

        CommonTestUtils.wait(2000);
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 0, 1);
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 1);

        Map receivedObject = (Map) receivedObjects.get(0);
        assertNotNull(receivedObject.get("Location"));
    }

    public void testSubscriptionToMoreThanOneOfPropertiesOfTopologyObjects() throws Exception {
        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "trial.*", new String[]{"Location", "Model"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log, new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();

        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 0, 1);

        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 1);

        Map receivedObject = (Map) receivedObjects.get(0);
        assertNotNull(receivedObject.get("Location"));
        assertNotNull(receivedObject.get("Model"));
    }

    public void testSubscriptionToMoreThanOneTopologyObjects() throws Exception {
        SmartsSubscribeParameters param1 = new SmartsSubscribeParameters("Router", "routerTrial.*", new String[]{"Location"});
        SmartsSubscribeParameters param2 = new SmartsSubscribeParameters("Host", "hostTrial.*", new String[]{"Location"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param1, param2});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "routerTrial", new HashMap(), 0, 1);
        Thread.sleep(100);
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Host", "hostTrial", new HashMap(), 0, 1);
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "routerTrial", 0, 1);
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Host", "hostTrial", 0, 1);
    }

    public void testThrowsExceptionForNonExistentClassInSubscriptionPeriod() throws Exception {
        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Routerdasdsfs", "trial.*", new String[]{"Location"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        try {
            topologyAdapter.subscribe();
            fail("Should throw exception because there is no class Routerdasdsfs defined in inchargemodel");
        }
        catch (Exception e) {
        }
        topologyAdapter.unsubscribe();
        SmartsSubscribeParameters param1 = new SmartsSubscribeParameters("Router", "routerTrial.*", new String[]{"Location"});
        SmartsSubscribeParameters param2 = new SmartsSubscribeParameters("Hostdasdsfs", "routerTrial.*", new String[]{"Location"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param1, param2});
        try {

            topologyAdapter.subscribe();
            fail("Should throw exception because there is no class Hostasdasdas defined in inchargemodel");
        }
        catch (Exception e) {
        }
    }

    public void testThrowsExceptionForNonExistentPropertyInSubscriptionPeriod() throws Exception {
        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "routerTrial.*", new String[]{"Locationsadasd"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        try {
            topologyAdapter.subscribe();
            fail("Should throw exception because there is no attribute Locationsadasd defined in class Router");
        }
        catch (Exception e) {
        }
    }

    public void testGetExitingDataObjects() throws Exception {
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 0, 2);
        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "trial.*", new String[]{"Location"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 2);
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 2, 1);
        Thread.sleep(500);
        assertEquals(3, receivedObjects.size());
    }

    public void testGetExitingAndChangeEventsOccurredOnDataObjects() throws Exception {
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 0, 2);

        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "trial.*", new String[]{"Location"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();
        Map atts = new HashMap();
        atts.put("Location", "Changed");
        SmartsTestUtils.updateTopologyInstanceWithProperties("Router", "trial0", atts);

        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 3);
        Thread.sleep(500);
        assertEquals(3, receivedObjects.size());

    }

    public void testSubscribeToAllPropertiesForClassesCreatedBeforeSubscription() throws Exception {
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 0, 1);

        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "trial.*", new String[]{".*"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 1);
        Map object = (Map)receivedObjects.get(0);
        assertTrue(object.containsKey("Location"));
        assertTrue(object.containsKey("Model"));
    }

     public void testIfNoAttributeSpecifiedAdapterSubscribesAllProperties() throws Exception {
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 0, 1);

        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "trial.*", new String[0]);
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 1);
        Map object = (Map)receivedObjects.get(0);
        assertTrue(object.containsKey("Location"));
        assertTrue(object.containsKey("Model"));
    }


    public void testSubscribeToPropertiesOfObjectsExistingBeforeSubscription() throws Exception {
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 0, 1);
        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "trial.*", new String[]{"Location", "Model"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 1);
        Map notification = (Map) receivedObjects.get(0);
        assertEquals(BaseSmartsListeningAdapter.CREATE, notification.get(BaseSmartsListeningAdapter.EVENT_TYPE_NAME));
        assertTrue(notification.containsKey("Location"));
        assertTrue(notification.containsKey("Model"));
    }

    public void testChangeEventsOccurredOnExistingObjects() throws Exception {
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 0, 2);

        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "trial.*", new String[]{"Location"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 2);// two change events for location attribute of two instances 
        Map atts = new HashMap();
        atts.put("Location", "Changed");
        SmartsTestUtils.updateTopologyInstanceWithProperties("Router", "trial0", atts);
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 3);
        Map object = (Map) receivedObjects.get(2);
        assertEquals(BaseSmartsListeningAdapter.CHANGE, object.get(BaseSmartsListeningAdapter.EVENT_TYPE_NAME));
        assertEquals("trial0", object.get(SmartsConstants.INSTANCENAME));
        assertEquals("Location", object.get("ModifiedAttributeName"));
        assertEquals("Changed", object.get("ModifiedAttributeValue"));
    }

    public void testSubscribeToAllPropertiesForClassesCreatedAfterSubscription() throws Exception {
        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "trial.*", new String[]{".*"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 0, 1);

        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 1);

        int numberOfExpectedAttributes = SmartsTestUtils.getTopologyAdapter().getPropNames("Router").length;
        int currentNumberOfAttributes = ((Map) receivedObjects.get(0)).size();

        assertEquals(numberOfExpectedAttributes, currentNumberOfAttributes - 1);//One for ICEventType

        Map atts = new HashMap();
        atts.put("Location", "Turkey");
        SmartsTestUtils.updateTopologyInstanceWithProperties("Router", "trial0", atts);
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 2);
    }

    public void testCanReadReadOnlyAttributes() throws Exception {
        //SystemName in ip network is read only attribute
        DatasourceTestUtils.getParamSupplier().setParam(SmartsTestUtils.getConnectionParam(SmartsTestConstants.SMARTS_AM_CONNECTION_TYPE));
        ConnectionParam param = SmartsTestUtils.getConnectionParam(SmartsTestConstants.SMARTS_AM_CONNECTION_TYPE);
        SmartsConnectionImpl datasource = new SmartsConnectionImpl();
        datasource.init(param);
        datasource._connect();

        SmartsSubscribeParameters subscriptionParam = new SmartsSubscribeParameters("Router", "Router1", new String[]{"SystemName"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{subscriptionParam});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();

        List opParams = new ArrayList();
        opParams.add("Router1");
        InvokeOperationAction action = new InvokeOperationAction(Logger.getRootLogger(), "ICIM_ObjectFactory", "ICIM-ObjectFactory", "makeRouter", opParams);
        action.execute(datasource);


        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "Router1", 0, 1);
        Map object = (Map) receivedObjects.get(0);
        assertEquals("Router1", object.get("SystemName"));
    }

    public void testCanReadRelationalAttributes() throws Exception {
        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "trial.*", new String[]{"ConnectedVia"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 0, 1);

        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 1);

        SmartsTestUtils.createTopologyInstancesWithPrefixes("Cable", "cabletrial", new HashMap(), 0, 1);

        SmartsTestUtils.addRelationship("Router", "trial0", "Cable", "cabletrial0", "ConnectedVia");
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 2);
    }

    public void testGettingCreationOfSubclassesByRegisteringSuperclass() throws Exception {
        SmartsSubscribeParameters param = new SmartsSubscribeParameters("UnitaryComputerSystem", ".*", null);
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 0, 1);
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 1);

        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 1, 3);
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 4);

        SmartsTestUtils.deleteTopologyInstancesWithPrefixes("Router", "trial", 0, 4);
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 8);
    }

    public void testGettingPropetiesOfSubclassesByRegisteringSuperclass() throws Exception {
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 0, 1);
        SmartsSubscribeParameters param = new SmartsSubscribeParameters("UnitaryComputerSystem", ".*", new String[]{"Model", "PrimaryOwnerName"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();

        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 1);
        Map atts = new HashMap();
        atts.put("Model", "changedModel");
        atts.put("PrimaryOwnerName", "changedPrimaryOwnerName");
        SmartsTestUtils.updateTopologyInstanceWithProperties("Router", "trial0", atts);
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 3);
    }

    public void testGettingPropetiesOfObjectsRegisteredBySuperclassAndItself() throws Exception {
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 0, 1);
        SmartsSubscribeParameters param1 = new SmartsSubscribeParameters("UnitaryComputerSystem", ".*", new String[]{"Model"});
        SmartsSubscribeParameters param2 = new SmartsSubscribeParameters("Router", ".*", new String[]{"PrimaryOwnerName"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param1, param2});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();

        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 1);
        Map atts = new HashMap();
        atts.put("Model", "changedModel");
        atts.put("PrimaryOwnerName", "changedPrimaryOwnerName");
        SmartsTestUtils.updateTopologyInstanceWithProperties("Router", "trial0", atts);
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 3);
    }

    public void testSubscribeUnsubscribe() throws Exception {
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 0, 1);
        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "trial.*", new String[]{"PrimaryOwnerName"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();

        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 1);

        topologyAdapter.unsubscribe();
        Thread.sleep(1000);

        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "trial", new HashMap(), 1, 1);
        Map atts = new HashMap();
        atts.put("PrimaryOwnerName", "changedPrimaryOwnerName");
        SmartsTestUtils.updateTopologyInstanceWithProperties("Router", "trial0", atts);
        SmartsTestUtils.updateTopologyInstanceWithProperties("Router", "trial1", atts);

        Thread.sleep(1000);

        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 1);// check doesnot recieve anything

        topologyAdapter.subscribe();
        atts.put("PrimaryOwnerName", "changedPrimaryOwnerName2");

        SmartsTestUtils.updateTopologyInstanceWithProperties("Router", "trial0", atts);

        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "trial", 0, 4);// 2 change for trial0 and 1 change for trial1
    }

    public void testTopologyObserverDiscardsFirstUpdatesRelatedToCreation() throws Exception {
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "routerTrial", new HashMap(), 0, 100);
        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "routerTrial.*", new String[]{".*"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();
        int iterationCount = 0;
        while (receivedObjects.size() == 0 && iterationCount < 1000) {
            Thread.sleep(10);
        }

        assertTrue("No Object creation observed.", iterationCount < 1000);
        Map atts = new HashMap();
        atts.put("Location", "ankara");
        SmartsTestUtils.updateTopologyInstanceWithProperties("Router", "routerTrial90",atts);
        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "routerTrial", 0, 101);
        Map lastRecievedData = (Map) receivedObjects.get(receivedObjects.size() - 1);
        assertEquals("Location", lastRecievedData.get("ModifiedAttributeName"));
        assertEquals("ankara", lastRecievedData.get("ModifiedAttributeValue"));
        assertEquals("routerTrial90", lastRecievedData.get("Name"));
        assertEquals(101, receivedObjects.size());
    }

    public void testDeleteTopologyObjectCreatesEvent() throws Exception
    {
    	SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "routerTrial", new HashMap(), 0, 1);
        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "routerTrial.*", new String[]{".*"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();

        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "routerTrial", 0, 1);
        Map object = (Map)receivedObjects.get(0);
        assertEquals(BaseSmartsListeningAdapter.CREATE, object.get(BaseSmartsListeningAdapter.EVENT_TYPE_NAME));

        SmartsTestUtils.deleteAllTopologyInstances("Router", ".*");

        BaseSmartsListeningAdapterTest.checkObjectListForObjects(receivedObjects, "Router", "routerTrial", 0, 2);
        object = (Map)receivedObjects.get(1);
        assertEquals(BaseSmartsListeningAdapter.DELETE, object.get(BaseSmartsListeningAdapter.EVENT_TYPE_NAME));
    }

    public void testRelationChange() throws Exception
    {
        SmartsTestUtils.createTopologyInstanceWithProperties("Router", "trial1", new HashMap());
        SmartsTestUtils.createTopologyInstanceWithProperties("Cable", "cable1", new HashMap());

        SmartsSubscribeParameters param = new SmartsSubscribeParameters("Router", "trial.*", new String[]{"ConnectedVia"});
        topologyAdapter = new SmartsTopologyListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log,
                new SmartsSubscribeParameters[]{param});
        topologyAdapter.addObserver(this);
        topologyAdapter.subscribe();
        CommonTestUtils.waitFor(new WaitAction(){
            public void check() throws Exception {
                assertEquals(1, receivedObjects.size());
            }
        }, 100);

        SmartsTestUtils.addRelationship("Router", "trial1", "Cable", "cable1", "ConnectedVia");
         CommonTestUtils.waitFor(new WaitAction(){
            public void check() throws Exception {
                assertEquals(2, receivedObjects.size());
            }
        }, 100);

        Map object = (Map)receivedObjects.get(1);
        assertEquals(BaseSmartsListeningAdapter.CHANGE, object.get(BaseSmartsListeningAdapter.EVENT_TYPE_NAME));
        assertEquals("ConnectedVia", object.get("ModifiedAttributeName"));
        Map[] connectedVias = (Map[]) object.get("ModifiedAttributeValue");
        assertEquals(1, connectedVias.length);
        assertEquals("Cable", connectedVias[0].get("CreationClassName"));
        assertEquals("cable1", connectedVias[0].get("Name"));
    }


    public void update(Observable o, Object arg) {
        if(((Map)arg).get(BaseSmartsListeningAdapter.EVENT_TYPE_NAME).equals(BaseSmartsListeningAdapter.RECEIVE_EXISTING_FINISHED)) return;
        receivedObjects.add(arg);
    }
}
