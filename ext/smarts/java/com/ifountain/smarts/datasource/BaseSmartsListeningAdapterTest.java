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
 * Created on Mar 12, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.datasource;

import com.ifountain.comp.test.util.CommonTestUtils;
import com.ifountain.comp.test.util.WaitAction;
import com.ifountain.comp.test.util.logging.TestLogUtils;
import com.ifountain.smarts.connection.SmartsConnectionImpl;
import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.test.util.SmartsTestUtils;
import com.ifountain.smarts.util.DataFromObservable;
import com.ifountain.smarts.util.SmartsConstants;
import com.ifountain.smarts.util.params.SmartsSubscribeParameters;
import com.ifountain.core.connection.ConnectionManager;
import com.smarts.remote.SmObserverEvent;
import com.smarts.remote.SmRemoteDomainManager;
import com.smarts.repos.MR_Choice;
import com.smarts.repos.MR_PropertyChoice;
import com.smarts.repos.MR_PropertyNameValue;

import java.util.*;

import org.apache.log4j.Logger;

public class BaseSmartsListeningAdapterTest<E> extends SmartsTestCase {

    private BaseSmartsListeningAdapterImpl smartsAdapter;
    LinkedList receivedObjects;
    int numberOfDiscconnectionMessages = 0;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SmartsTestUtils.deleteAllTopologyInstances("Router", ".*");
        receivedObjects = new LinkedList();
        numberOfDiscconnectionMessages = 0;
        smartsAdapter = new BaseSmartsListeningAdapterImpl(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log, null);
    }

    @Override
    protected void tearDown() throws Exception {
        smartsAdapter.unsubscribe();
        super.tearDown();
    }

    public void testGettingNewObjects() throws Exception {
        smartsAdapter.subscribe();
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "routertrial", new HashMap<String, String>(), 0, 1);
        checkObjectListForObjects(receivedObjects, "Router", "routertrial", 0, 1);
    }

    public void testGettingQuicklyCreatedObjectsCorrectly() throws Exception {
        smartsAdapter.subscribe();
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "routertrial", new HashMap<String, String>(), 0, 200);
        checkObjectListForObjects(receivedObjects, "Router", "routertrial", 0, 200);
    }

    public void testGettingDeletedObjects() throws Exception {
        smartsAdapter.subscribe();
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "routertrial", new HashMap<String, String>(), 0, 1);
        checkObjectListForObjects(receivedObjects, "Router", "routertrial", 0, 1);
        SmartsTestUtils.deleteTopologyInstancesWithPrefixes("Router", "routertrial", 0, 1);
        checkObjectListForObjects(receivedObjects, "Router", "routertrial", 1, 1);
    }

    public void testUnsubscribedAttributesOfExistingObjectsAreNotReceived() throws Exception {
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "routertrial", new HashMap<String, String>(), 0, 1);
        smartsAdapter.subscribe();
        Thread.sleep(2000);
        assertEquals(0, receivedObjects.size());
    }

    public void testGettingPropertiesOfObjectsCreatedAfterSubscription() throws Exception {
        subcribeToPropertyChanges("Router", "routertrial.*", "Location");
        smartsAdapter.subscribe();
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "routertrial", new HashMap<String, String>(), 0, 1);
        checkObjectListForObjects(receivedObjects, "Router", "routertrial", 0, 2);//create and update for location
    }

    public void testGettingPropertiesOfObjectsCreatedBeforeSubscription() throws Exception {
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "routertrial", new HashMap<String, String>(), 0, 1);
        subcribeToPropertyChanges("Router", "routertrial.*", "Location");
        smartsAdapter.subscribe();
        checkObjectListForObjects(receivedObjects, "Router", "routertrial", 0, 1);//update for location
    }

    public void testSubscriberDoesnotSendRecievedObjectsTillExistingObjectsRetrieved() throws Exception {
        SmartsConnectionImpl conn = (SmartsConnectionImpl) ConnectionManager.getConnection(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME);
        SmRemoteDomainManager domainManager = conn.getDomainManager();
        smartsAdapter.setExitingObjectsRetrieved(false);
        domainManager.createObserver(smartsAdapter);
        domainManager.topologySubscribe();
        smartsAdapter.setSubscribed(true);
        smartsAdapter.setObserverCreated(true);
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "routertrial", new HashMap(), 0, 1);
        Thread.sleep(1500);//give time to processor thread to notify reader

        assertTrue("Reader should not recive any object from observer because exiosting ones not finished", receivedObjects.isEmpty());//There is no Recieved objects becuase existing objects not finished
        smartsAdapter.setExitingObjectsRetrieved(true);
        checkObjectListForObjects(receivedObjects, "Router", "routertrial", 0, 1);
        domainManager.topologyUnsubscribe();
        domainManager.deleteObserver(smartsAdapter);
        smartsAdapter.setObserverCreated(false);
        smartsAdapter.setSubscribed(false);
        ConnectionManager.releaseConnection(conn);
    }

    public void testGettingUpdatedPropertiesOfObjects() throws Exception {
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "routertrial", new HashMap<String, String>(), 0, 1);
        subcribeToPropertyChanges("Router", "routertrial.*", "Location");
        smartsAdapter.subscribe();
        Map<String, String> props = new HashMap<String, String>();
        props.put("Location", "changed");
        SmartsTestUtils.updateTopologyInstanceWithProperties("Router", "routertrial0", props);
        checkObjectListForObjects(receivedObjects, "Router", "routertrial", 0, 2);//2 number of data will come first gives us value
        Thread.sleep(2000);
    }

    public void testIfDomainDisconnectEventRecievedListenerWillBeNotified() throws Exception {
        Observable observable = new Observable();
        SmObserverEvent event = new SmObserverEvent(SmObserverEvent.DOMAIN_DISCONNECT);
        smartsAdapter.subscribe();
        smartsAdapter.update(observable, event);

        assertEquals("Observer doesnot notify listeners about disconnection", 1, numberOfDiscconnectionMessages);
    }

    public void testIfDomainDetachEventRecievedListenerWillBeNotified() throws Exception {
        Observable observable = new Observable();
        SmObserverEvent event = new SmObserverEvent(SmObserverEvent.DOMAIN_DETACH);
        smartsAdapter.subscribe();
        smartsAdapter.update(observable, event);

        assertEquals("Observer doesnot notify listeners about disconnection", 1, numberOfDiscconnectionMessages);
    }

    private void subcribeToPropertyChanges(String className, String instanceNamePattern, String property) {
        SmartsSubscribeParameters param = new SmartsSubscribeParameters(className, instanceNamePattern, new String[]{property});
        SmartsSubscribeParameters[] params = new SmartsSubscribeParameters[]{param};
        smartsAdapter.setSubscribeParams(params);
    }

    public static void checkObjectListForObjects(final LinkedList recievedObjects, final String className, final String instancePrefix, final int startIndex, final int numberOfObjects) throws Exception {
        CommonTestUtils.waitFor(new WaitAction() {
            @Override
            public void check() throws Exception {
                int size = 0;
                int sIndex = startIndex;
                for (int i = sIndex; i < recievedObjects.size(); i++) {
                    Map properties = (Map) recievedObjects.get(i);
                    boolean classNameFound = false;
                    boolean instanceNameFound = false;
                    String receivedClassName = String.valueOf(properties.get(SmartsConstants.CLASSNAME));
                    String receivedInstanceName = String.valueOf(properties.get(SmartsConstants.INSTANCENAME));
                    if (className.equals(receivedClassName)) {
                        classNameFound = true;
                    }
                    if (receivedInstanceName != null && receivedInstanceName.startsWith(instancePrefix)) {
                        instanceNameFound = true;
                    }
                    if (classNameFound && instanceNameFound) {
                        size++;
                    }
                    sIndex++;
                }
                assertEquals("Expected number of objects couldnot be recieved", numberOfObjects, size);
            }
        }, 100);
    }

    class BaseSmartsListeningAdapterImpl extends BaseSmartsListeningAdapter {
        public BaseSmartsListeningAdapterImpl(String connectionName, long reconnectInterval, Logger logger, SmartsSubscribeParameters[] subscribeParams) {
            super(connectionName, reconnectInterval, logger, subscribeParams);

        }

        @Override
        public Object processIncomingData(DataFromObservable data) {
            MR_PropertyNameValue[] nameValuePairs = convertIncomingDataToMR_PropertyNameValue(data);
            switch (data.getEventType()) {
                case SmObserverEvent.INSTANCE_CREATE: {
                    receivedObjects.add(createObject(CREATE, nameValuePairs));
                    break;
                }
                case SmObserverEvent.INSTANCE_DELETE: {
                    receivedObjects.add(createObject(DELETE, nameValuePairs));
                    break;
                }
                case SmObserverEvent.ATTRIBUTE_CHANGE: {
                    receivedObjects.add(createObject(CHANGE, nameValuePairs));
                    break;
                }
                default:
                    break;
            }
            return null;
        }

        protected void getExistingObjects(SmartsSubscribeParameters[] parameters) throws Exception {

        }

        @Override
        protected void subscribeTo() throws Exception {
            SmRemoteDomainManager domainManager = ((SmartsConnectionImpl) getConnection()).getDomainManager();
            domainManager.topologySubscribe();
            if (subscribeParams != null) {
                SmartsSubscribeParameters param = subscribeParams[0];
                MR_PropertyChoice choice = new MR_PropertyChoice(param.getClassName(),
                        param.getInstanceName(), param.getParameter(0), MR_PropertyChoice.STICKY | MR_PropertyChoice.EXPAND_SUBCLASSES);
                domainManager.propertySubscribeAll(choice, 1);
            }
        }

        @Override
        protected void unsubscribeFrom() throws Exception {
            SmRemoteDomainManager domainManager = ((SmartsConnectionImpl) getConnection()).getDomainManager();
            domainManager.propertyUnsubscribeAll(new MR_PropertyChoice(".*", ".*", ".*", MR_Choice.EXPAND_SUBCLASSES));
            domainManager.topologyUnsubscribe();
        }

        @Override
        protected void disconnectDetected() throws Exception {
            numberOfDiscconnectionMessages++;
        }

        public void setExitingObjectsRetrieved(boolean existingObjectsRetrieved) {
            this.existingObjectsRetrieved = existingObjectsRetrieved;
        }

        public void setSubscribed(boolean isSubscribed){
            this.isSubscribed = isSubscribed;
        }
        public void setObserverCreated(boolean isObserverCreated){
            this.isObserverCreated = isObserverCreated;
        }

    }

}
