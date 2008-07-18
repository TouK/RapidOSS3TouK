package com.ifountain.smarts.datasource;

import com.ifountain.comp.test.util.CommonTestUtils;
import com.ifountain.comp.test.util.WaitAction;
import com.ifountain.comp.test.util.logging.TestLogUtils;
import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.test.util.SmartsTestUtils;
import com.ifountain.smarts.test.util.SmartsTestConstants;
import com.ifountain.smarts.util.SmartsConstants;
import com.ifountain.smarts.util.params.*;
import com.ifountain.core.connection.ConnectionParam;
import com.smarts.remote.SmRemoteException;
import com.smarts.repos.MR_AnyValBoolean;
import com.smarts.repos.MR_AnyValString;
import com.smarts.repos.MR_AnyValUnsignedInt;
import com.smarts.repos.MR_PropertyNameValue;

import java.io.IOException;
import java.util.*;

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
 * Date: Jul 16, 2008
 * Time: 6:26:43 PM
 */
public class SmartsNotificationListeningAdapterTest extends SmartsTestCase implements Observer {
    SmartsNotificationListeningAdapter notificationAdapter;
    Map receivedObjects;
    List monitoredAtts;
    String nlList = "nlDeveloper";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        receivedObjects = new HashMap();
        monitoredAtts = new ArrayList();
        monitoredAtts.add(SmartsConstants.INSTANCENAME);
        monitoredAtts.add("Severity");
        SmartsTestUtils.archiveAllNotifications();
    }

    @Override
    protected void tearDown() throws Exception {
        if (notificationAdapter != null) {
            notificationAdapter.unsubscribe();
        }
        super.tearDown();
    }

    public void testDetermineEventType() throws Exception {
        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList);
        notificationAdapter.subscribe();

        // NOTIFY
        MR_PropertyNameValue[] nameValues = new MR_PropertyNameValue[3];
        nameValues[0] = new MR_PropertyNameValue("Name", new MR_AnyValString("MyNotification"));
        nameValues[1] = new MR_PropertyNameValue("Severity", new MR_AnyValUnsignedInt(1));
        nameValues[2] = new MR_PropertyNameValue("Active", new MR_AnyValBoolean(true));
        assertEquals(BaseSmartsListeningAdapter.NOTIFY, notificationAdapter.determineEventType("MyNotification", nameValues));

        //CHANGE
        nameValues[1] = new MR_PropertyNameValue("Severity", new MR_AnyValUnsignedInt(2));
        assertEquals(BaseSmartsListeningAdapter.CHANGE, notificationAdapter.determineEventType("MyNotification", nameValues));

        //CLEAR
        nameValues[2] = new MR_PropertyNameValue("Active", new MR_AnyValBoolean(false));
        assertEquals(BaseSmartsListeningAdapter.CLEAR, notificationAdapter.determineEventType("MyNotification", nameValues));

        //CHANGE
        nameValues[1] = new MR_PropertyNameValue("Severity", new MR_AnyValUnsignedInt(3));
        assertEquals(BaseSmartsListeningAdapter.CHANGE, notificationAdapter.determineEventType("MyNotification", nameValues));

        nameValues[2] = new MR_PropertyNameValue("Active", new MR_AnyValBoolean(true));
        assertEquals(BaseSmartsListeningAdapter.NOTIFY, notificationAdapter.determineEventType("MyNotification", nameValues));
    }

    public void testSmootherIsAlwaysClosedEvenIfUnsubscribeFromPropertiesThrowsException() throws Exception {
        final ArrayList methodCalls = new ArrayList();
        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, new ArrayList(), "nlDeveloper", 3, false) {
            protected void unsubscribeFromProperties() throws IOException, SmRemoteException {
                throw new IOException("");
            }

            public void subscribe() throws Exception {
                isSubscribed = true;
            }
        };
        Smoother smoother = new Smoother(null, 1) {
            public void stopSmoother() {
                methodCalls.add("stopSmoother called");
            }
        };
        notificationAdapter.setSmoother(smoother);
        notificationAdapter.subscribe();
        notificationAdapter.unsubscribe();
        assertEquals(1, methodCalls.size());
        assertEquals("stopSmoother called", methodCalls.get(0).toString());
    }

    public void testInvalidListNameThrowsException() throws Exception {
        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, new ArrayList(), "undefined_list");
        try {
            notificationAdapter.subscribe();
            fail("Should throw exception because notification list is invalid.");
        }
        catch (Exception e) {
        }
    }

    public void testInvalidMonitoredAttsThrowsException() throws Exception {
        monitoredAtts.add("InvalidMonitoredAtt");
        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList);
        try {
            notificationAdapter.subscribe();
            fail("Should throw exception because monitored attributes invalid.");
        }
        catch (Exception e) {
        }
    }

    public void testGetsOnlyRequestedAttributesInCaseOfNotifyAndUpdate() throws Exception {
        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList);

        notificationAdapter.addObserver(this);
        notificationAdapter.subscribe();
        SmartsTestUtils.createNotification("Switch", "ercaswnyc2", "Down", new HashMap());
        Map attributes = new HashMap();
        attributes.put("EventText", "sezgin");
        SmartsTestUtils.updateNotification("Switch", "ercaswnyc2", "Down", attributes);
        checkObjectListSize(receivedObjects, 1);
        String notificationName = "NOTIFICATION-Switch_ercaswnyc2_Down";
        Map notification = (Map) receivedObjects.get(notificationName + "1NOTIFY");
        assertTrue(notification.containsKey("Severity"));
        assertEquals(BaseSmartsListeningAdapter.NOTIFY, notification.get(BaseSmartsListeningAdapter.EVENT_TYPE_NAME));
        Thread.sleep(2000);
        assertEquals(1, receivedObjects.size());//no event for EventText update
    }

    /**
     * Smoothing is only done for change events. If a change comes after a Notify, Clear or Change, it will be put in staging area.
     * Notify or Clear will be put in staging and will remain there during the transientInterval even if there are change events. These
     * change events will not change teh eventType (Notify or Clear). A Notify or Archive after a Clear will immediately send the previous
     * Clear to the reader. A Clear or Archive after a Notify will immediately send the previous Notify to the reader.
     * Archive will always send itself immediately to reader after removing the notification from staging area and inMemoryNotifications.
     */
    public void testTransientInterval() throws Exception {
        int transientInterval = 2500;
        monitoredAtts.add("OccurrenceCount");
        monitoredAtts.add("Owner");
        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList, transientInterval, true);
        notificationAdapter.addObserver(this);
        notificationAdapter.subscribe();

        // SCENARIO 0: NOTIFY + CLEAR DURING TRANSIENT INTERVAL WILL BE A NOTIFY AND A CLEAR
        String param = "NOTIFICATION-Switch_ercaswnyc2_Down";
        SmartsTestUtils.createNotification("Switch", "ercaswnyc2", "Down", new HashMap());
        CommonTestUtils.wait(500);
        assertEquals(0, receivedObjects.size());

        SmartsTestUtils.clearNotification("Switch", "ercaswnyc2", "Down");
        CommonTestUtils.wait(1000);
        assertEquals(1, receivedObjects.size()); // Arrival of Clear should immediately send the previous Notify
        assertNotNull(receivedObjects.get(param + "1NOTIFY"));

        checkObjectListSize(receivedObjects, 2);
        assertNotNull(receivedObjects.get(param + "2CLEAR"));

        // SCENARIO 1: NOTIFY + CHANGE(S) DURING TRANSIENT WILL BE A NOTIFY
        SmartsTestUtils.createNotification("Host", "ercaswnyc2", "Down", new HashMap());
        CommonTestUtils.wait(200);
        param = "NOTIFICATION-Host_ercaswnyc2_Down";
        assertEquals(2, receivedObjects.size());

        // create the same notification   // this will cause a CHANGE event due to count change
        SmartsTestUtils.createNotification("Host", "ercaswnyc2", "Down", new HashMap());
        CommonTestUtils.wait(4000); // transientInterval has passed
        assertEquals(3, receivedObjects.size());
        // we should still get a NOTIFY
        assertNotNull(receivedObjects.get(param + "3NOTIFY"));

        // SCENARIO 2: MULTIPLE CHANGE EVENTS DURING TRANSIENT, ONLY THE LAST CHANGE CAPTURED
        SmartsTestUtils.takeOwnershipNotification("Host", "ercaswnyc2", "Down", "Owner", "taking ownership");
        CommonTestUtils.wait(50);
        SmartsTestUtils.takeOwnershipNotification("Host", "ercaswnyc2", "Down", "Owner1", "taking ownership");
        CommonTestUtils.wait(50);
        SmartsTestUtils.takeOwnershipNotification("Host", "ercaswnyc2", "Down", "Owner2", "taking ownership");
        CommonTestUtils.wait(4000);
        assertEquals(4, receivedObjects.size());

        Map notification = (Map) receivedObjects.get(param + "4CHANGE");
        assertNotNull(notification);
        assertEquals("Owner2", notification.get("Owner"));

        // SCENARIO 3: MULTIPLE CHANGE EVENTS DURING TRANSIENT + CLEAR, WILL BE 1 CHANGE + 1 CLEAR
        SmartsTestUtils.takeOwnershipNotification("Host", "ercaswnyc2", "Down", "Owner", "taking ownership");
        CommonTestUtils.wait(50);
        SmartsTestUtils.takeOwnershipNotification("Host", "ercaswnyc2", "Down", "Owner1", "taking ownership");
        CommonTestUtils.wait(50);
        SmartsTestUtils.clearNotification("Host", "ercaswnyc2", "Down");
        CommonTestUtils.wait(500);
        assertEquals(5, receivedObjects.size()); // Arrival of Clear should immediately send the previous Notify
        assertNotNull(receivedObjects.get(param + "5CHANGE"));

        CommonTestUtils.wait(4000);
        assertEquals(6, receivedObjects.size());
        assertNotNull(receivedObjects.get(param + "6CLEAR"));

        // SCENARIO 4: NOTIFY + CLEAR + NOTIFY DURING TRANSIENT INTERVAL WILL BE NOTIFY + CLEAR + NOTIFY
        param = "NOTIFICATION-Router_ercaswnyc2_Down";
        SmartsTestUtils.createNotification("Router", "ercaswnyc2", "Down", new HashMap());
        CommonTestUtils.wait(500);
        assertEquals(6, receivedObjects.size());

        SmartsTestUtils.clearNotification("Router", "ercaswnyc2", "Down");
        CommonTestUtils.wait(500);
        assertEquals(7, receivedObjects.size());  // Clear kicks the previous Notify
        assertNotNull(receivedObjects.get(param + "7NOTIFY"));

        SmartsTestUtils.createNotification("Router", "ercaswnyc2", "Down", new HashMap());
        CommonTestUtils.wait(500);
        assertEquals(8, receivedObjects.size());   // Create kicks the previous Clear
        assertNotNull(receivedObjects.get(param + "8CLEAR"));

        checkObjectListSize(receivedObjects, 9);// final Notify arrives after transdientInterval
        assertNotNull(receivedObjects.get(param + "9NOTIFY"));
    }

    public void testZeroTransientIntervalMeansNoSmoothing() throws Exception {
        int transientInterval = 0;
        monitoredAtts.add("OccurrenceCount");
        monitoredAtts.add("Owner");
        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList, transientInterval, true);
        notificationAdapter.addObserver(this);
        notificationAdapter.subscribe();

        // SCENARIO 0: NOTIFY + CLEAR
        // create a notification
        String param1 = "NOTIFICATION-Switch_ercaswnyc2_Down";
        SmartsTestUtils.createNotification("Switch", "ercaswnyc2", "Down", new HashMap());
        SmartsTestUtils.clearNotification("Switch", "ercaswnyc2", "Down");

        // SCENARIO 1: NOTIFY + CHANGE(S)
        // create a notification
        Map atts = new HashMap();
        atts.put("EventType", "MOMENTARY");
        SmartsTestUtils.createNotification("Host", "ercaswnyc2", "Down", atts);
        String param = "NOTIFICATION-Host_ercaswnyc2_Down";

        // create the same notification   // this will cause a CHANGE event due to count change
        SmartsTestUtils.createNotification("Host", "ercaswnyc2", "Down", atts);

        // SCENARIO 2: MULTIPLE CHANGE EVENTS
        // TAKE OWNERSHIP
        SmartsTestUtils.takeOwnershipNotification("Host", "ercaswnyc2", "Down", "Owner", "taking ownership");
        SmartsTestUtils.takeOwnershipNotification("Host", "ercaswnyc2", "Down", "Owner1", "taking ownership");
        SmartsTestUtils.takeOwnershipNotification("Host", "ercaswnyc2", "Down", "Owner2", "taking ownership");

        // SCENARIO 3: MULTIPLE CHANGE EVENTS + CLEAR + ONE LAST CHANGE
        SmartsTestUtils.takeOwnershipNotification("Host", "ercaswnyc2", "Down", "Owner", "taking ownership");
        SmartsTestUtils.takeOwnershipNotification("Host", "ercaswnyc2", "Down", "Owner1", "taking ownership");
        SmartsTestUtils.clearNotification("Host", "ercaswnyc2", "Down");
        SmartsTestUtils.takeOwnershipNotification("Host", "ercaswnyc2", "Down", "Owner3", "taking ownership");

        checkObjectListSize(receivedObjects, 11);
        assertNotNull(receivedObjects.get(param1 + "1NOTIFY"));
        assertNotNull(receivedObjects.get(param1 + "2CLEAR"));
        assertNotNull(receivedObjects.get(param + "3NOTIFY"));
        assertNotNull(receivedObjects.get(param + "4CHANGE"));
        Map notification = (Map) receivedObjects.get(param + "5CHANGE");
        assertNotNull(notification);
        assertEquals("Owner", notification.get("Owner"));
        notification = (Map) receivedObjects.get(param + "6CHANGE");
        assertNotNull(notification);
        assertEquals("Owner1", notification.get("Owner"));
        notification = (Map) receivedObjects.get(param + "7CHANGE");
        assertNotNull(notification);
        assertEquals("Owner2", notification.get("Owner"));
        assertNotNull(receivedObjects.get(param + "8CHANGE"));
        assertNotNull(receivedObjects.get(param + "9CHANGE"));
        assertNotNull(receivedObjects.get(param + "10CLEAR"));
        assertNotNull(receivedObjects.get(param + "11CHANGE"));
    }

    public void testArchiveCanBeSubscribedTo() throws Exception {
        int transientInterval = 0;
        monitoredAtts.add("Owner");
        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList, transientInterval, true);
        notificationAdapter.addObserver(this);
        notificationAdapter.subscribe();

        int eventCount = 0;
        String param = "NOTIFICATION-Switch_ercaswnyc2_Down";
        SmartsTestUtils.createNotification("Switch", "ercaswnyc2", "Down", new HashMap());
        checkObjectListSize(receivedObjects, ++eventCount);
        assertNotNull(receivedObjects.get(param + eventCount + "NOTIFY"));

        Map attributes = new HashMap();
        attributes.put("Severity", 4);
        SmartsTestUtils.updateNotification("Switch", "ercaswnyc2", "Down", attributes);
        checkObjectListSize(receivedObjects, ++eventCount);
        assertNotNull(receivedObjects.get(param + eventCount + "CHANGE"));

        SmartsTestUtils.clearNotification("Switch", "ercaswnyc2", "Down");
        checkObjectListSize(receivedObjects, ++eventCount);
        assertNotNull(receivedObjects.get(param + eventCount + "CLEAR"));

        SmartsTestUtils.acknowledgeNotification("Switch", "ercaswnyc2", "Down");
        checkObjectListSize(receivedObjects, ++eventCount);
        assertNotNull(receivedObjects.get(param + eventCount + "CHANGE"));

        SmartsTestUtils.archiveNotification("Switch", "ercaswnyc2", "Down");
        checkObjectListSize(receivedObjects, ++eventCount);
        assertNotNull(receivedObjects.get(param + eventCount + "ARCHIVE"));
    }

    public void testIfNotificationReaderMissesAnyNotificationsCreatedDuringStartupWithTailModeFalse() throws Exception {
        int transientInterval = 3000;
        assertAllNotificationsAreRead(transientInterval);
        transientInterval = 0;
        assertAllNotificationsAreRead(transientInterval);
    }

    private void assertAllNotificationsAreRead(int transientInterval) throws Exception {
        int notificationCount = 50;
        int timeBetweenNotifications = 20;
        SmartsTestUtils.archiveAllNotifications();

        // start creating notifications while the reader adapter is starting up
        Thread notificationCreator = new NotificationCreatorThread(notificationCount, timeBetweenNotifications);
        notificationCreator.start();

        CommonTestUtils.wait(80); // give some time for a couple notifications to be created before starting up the reader

        try {
            notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                    TestLogUtils.log, monitoredAtts, nlList, transientInterval, false);
            notificationAdapter.addObserver(this);
            notificationAdapter.subscribe();
            checkObjectListSize(receivedObjects, notificationCount);
        }
        finally {
            notificationAdapter.unsubscribe();
            notificationCreator.join();
            receivedObjects.clear();
        }
    }

    public void testCreateClearCreateClearChangeChangeArchiveArchive() throws Exception {
        int transientInterval = 0;
        int notificationCount = 10;
        int timeBetweenNotifications = 0;

        Thread notificationDriver = null;
        try {
            notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                    TestLogUtils.log, monitoredAtts, nlList, transientInterval, false);
            notificationAdapter.addObserver(this);
            notificationAdapter.subscribe();
            CommonTestUtils.wait(900); // let the reader complete startup and subscription
            notificationDriver = new NotificationCreatorChangeClearThread(notificationCount, timeBetweenNotifications);
            notificationDriver.start();
            checkObjectListSize(receivedObjects, notificationCount * 7);

        }
        finally {
            notificationAdapter.unsubscribe();
            notificationDriver.join();
        }
    }

    public void testAdapterDoesNotMissEventsWhenTheyArriveTooQuicklyForTheSameNotification() throws Exception, SmRemoteException {
        int transientInterval = 2500;
        monitoredAtts.add("OccurrenceCount");
        monitoredAtts.add("Owner");
        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList, transientInterval, true);
        notificationAdapter.addObserver(this);
        notificationAdapter.subscribe();

        // create a notification
        String param = "NOTIFICATION-Switch_ercaswnyc2_Down";
        Map atts = new HashMap();
        atts.put("EventType", "MOMENTARY");
        SmartsTestUtils.createNotification("Switch", "ercaswnyc2", "Down", atts);
        checkObjectListSize(receivedObjects, 1); // make sure transient interval passes and we get the notify update

        SmartsTestUtils.createNotification("Switch", "ercaswnyc2", "Down", atts);
        SmartsTestUtils.clearNotification("Switch", "ercaswnyc2", "Down");
        SmartsTestUtils.createNotification("Switch", "ercaswnyc2", "Down", atts);
        checkObjectListSize(receivedObjects, 4);
        assertNotNull(receivedObjects.get(param + "1NOTIFY"));
        assertNotNull(receivedObjects.get(param + "2CHANGE"));
        assertNotNull(receivedObjects.get(param + "3CLEAR"));
        assertNotNull(receivedObjects.get(param + "4NOTIFY"));
    }

//     public void testNotificationList() throws Exception {
//    	ICNotificationReaderObserver.CONNECTION_RETRY_COUNT = 2;
//    	int transientInterval = 100;
//        RapidInChargeDomainManager domainManager = InChargeTestUtils.getTestRapidDomainManager();
//        InChargeTestUtils.archiveAllNotifications(domainManager);
//
//        ICNotificationReaderMockImpl inchargeReader = new ICNotificationReaderMockImpl();
//
//        // Start reader adapter.
//        ICNotificationReaderAdapterParams readerParams = getTestParams();
//
//        ICNotificationReaderAdapterImplMock inChargeReaderAdapterImpl = null;
//        try{
//            inChargeReaderAdapterImpl = new ICNotificationReaderAdapterImplMock(inchargeReader, readerParams,InChargeTestUtils.getInChargeConnectionParams(), transientInterval);
//
//            // create first notification  (Switch)
//            InChargeNotificationWriterCreateParameters createParameters = InChargeTestUtils.getTestParametersForCreate();
//            ICWriterAdapter.createNotification(createParameters);
//
//            // create second notification  (Session)
//            createParameters.getIdentifierParameters().setClassName("Session");
//            ICWriterAdapter.createNotification(createParameters);
//
//            // nlDeveloper will only see Switch
//            InChargeTestUtils.assertExpectedNotificationReadEventCount(inChargeReaderAdapterImpl.list, 1);
//        }
//        finally{
//            inChargeReaderAdapterImpl.closeAdapter();
//        }
//
//        // subscribe to an non-existent list with tail mode false (should throw exception)
//        readerParams.setNlName("NonExistentList");
//        inchargeReader = new ICNotificationReaderMockImpl();
//        try{
//            inChargeReaderAdapterImpl =
//                    new ICNotificationReaderAdapterImplMock(inchargeReader, readerParams,InChargeTestUtils.getInChargeConnectionParams(), transientInterval);
//            fail("Exception should have been thrown");
//        }
//        catch(Exception e){
//            inChargeReaderAdapterImpl.closeAdapter();
//        }
//
//        // subscribe to an non-existent list with tail mode true (should throw exception)
//        readerParams.setNlName("NonExistentList");
//        readerParams.setTailMode(true);
//        inchargeReader = new ICNotificationReaderMockImpl();
//        try{
//            inChargeReaderAdapterImpl =
//                    new ICNotificationReaderAdapterImplMock(inchargeReader, readerParams,InChargeTestUtils.getInChargeConnectionParams(), transientInterval);
//            fail("Exception should have been thrown");
//        }
//        catch(Exception e){
//            inChargeReaderAdapterImpl.closeAdapter();
//        }
//
//        // Subscribe to ALL_NOTIFICATIONS list
//        readerParams.setNlName("ALL_NOTIFICATIONS");
//        readerParams.setTailMode(false); // this time we will read all notifications but only one of them will be non-Session
//        inchargeReader = new ICNotificationReaderMockImpl();
//        try{
//            inChargeReaderAdapterImpl = new ICNotificationReaderAdapterImplMock(inchargeReader, readerParams,InChargeTestUtils.getInChargeConnectionParams(), transientInterval);
//            boolean found = false;
//            for (int i = 0 ; i < 100 ; i++)
//			{
//            	int sessionNotificationCount = 0;
//                int nonSessionNotificationCount = 0;
//                Iterator iterator = inChargeReaderAdapterImpl.list.keySet().iterator();
//            	while(iterator.hasNext())
//                {
//                	if(iterator.next().toString().indexOf("Session") > -1)
//                	{
//                		sessionNotificationCount++;
//                	}
//                	else
//                	{
//                		nonSessionNotificationCount++;
//                	}
//                }
//            	if(nonSessionNotificationCount == 1 && sessionNotificationCount >= 1)
//            	{
//            		found = true;
//            		break;
//            	}
//            	CommonTestUtils.wait(100);
//			}
//
//            if(!found)
//            {
//            	fail("Could not read all notifications from ALL_NOTIFICATIONS notification list");
//            }
//        }
//        finally{
//            inChargeReaderAdapterImpl.closeAdapter();
//        }
//    }

    public void testTailModeTrue() throws Exception {
        int transientInterval = 100;
        // create notifications
        SmartsTestUtils.createNotification("HostBefore1", "ercaswnyc2", "Down", new HashMap());
        SmartsTestUtils.createNotification("HostBefore2", "ercaswnyc2", "Down", new HashMap());
        SmartsTestUtils.createNotification("HostBefore3", "ercaswnyc2", "Down", new HashMap());

        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList, transientInterval, true);
        notificationAdapter.addObserver(this);
        notificationAdapter.subscribe();

        Thread.sleep(1000); //we have to wait in order to make sure that no notification is read
        assertEquals(0, receivedObjects.size());

        SmartsTestUtils.createNotification("HostAfter1", "ercaswnyc2", "Down", new HashMap());
        SmartsTestUtils.createNotification("HostAfter2", "ercaswnyc2", "Down", new HashMap());
        SmartsTestUtils.createNotification("HostAfter3", "ercaswnyc2", "Down", new HashMap());
        checkObjectListSize(receivedObjects, 3);
    }

    public void testNotifyChangeClearChangeArchive() throws Exception {
        int transientInterval = 100;
        // create notification
        SmartsTestUtils.createNotification("HostBefore", "ercaswnyc2", "Down", new HashMap());

        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList, transientInterval, true);
        notificationAdapter.addObserver(this);
        notificationAdapter.subscribe();

        // FIRST, change a non-monitored attribute, should be NO CHANGE TO EXISTING NOTIFICATION
        NotificationCreateParams createParameters = SmartsTestUtils.getTestParametersForCreate();
        NotificationIdentifierParams identiferParams = createParameters.getIdentifierParameters();
        identiferParams.setClassName("HostBefore");
        SmartsTestUtils.takeOwnershipNotification("HostBefore", "ercaswnyc2", "Down", "ZZOwner", "taking");
        assertTrue("Owner property should have changed.", SmartsTestUtils.waitForNotificationAttributeUpdate(createParameters.getIdentifierParameters(), "Owner", "ZZOwner"));
        Thread.sleep(1000); //we have to wait in order to make sure that no notification is read
        assertEquals(0, receivedObjects.size());

        // New Notification
        identiferParams.setClassName("HostAfter");
        SmartsTestUtils.getNotificationAdapter().createNotification(createParameters);
        checkObjectListSize(receivedObjects, 1);
        assertNotNull(receivedObjects.get("NOTIFICATION-HostAfter_ercaswnyc2_Down1NOTIFY"));

        // Attribute changed
        HashMap attributes = new HashMap();
        attributes.put("Severity", 4);
        SmartsTestUtils.updateNotification(identiferParams.getClassName(), identiferParams.getInstanceName(), identiferParams.getEventName(), attributes);
        checkObjectListSize(receivedObjects, 2);
        assertNotNull(receivedObjects.get("NOTIFICATION-HostAfter_ercaswnyc2_Down2CHANGE"));

        // cleared
        SmartsTestUtils.getNotificationAdapter().clearNotification(identiferParams, SmartsTestUtils.getTestParametersForClear());
        checkObjectListSize(receivedObjects, 3);
        assertNotNull(receivedObjects.get("NOTIFICATION-HostAfter_ercaswnyc2_Down3CLEAR"));

        // Attribute changed again
        attributes.put("Severity", 1);
        SmartsTestUtils.updateNotification(identiferParams.getClassName(), identiferParams.getInstanceName(), identiferParams.getEventName(), attributes);
        checkObjectListSize(receivedObjects, 4);
        assertNotNull(receivedObjects.get("NOTIFICATION-HostAfter_ercaswnyc2_Down4CHANGE"));

        SmartsTestUtils.getNotificationAdapter().archiveNotification(identiferParams, SmartsTestUtils.getTestParametersForAcknowledge());
        checkObjectListSize(receivedObjects, 5);
        assertNotNull(receivedObjects.get("NOTIFICATION-HostAfter_ercaswnyc2_Down5ARCHIVE"));
    }

    public void testClearChangeNotify() throws Exception {
        int transientInterval = 100;
        // create notification
        SmartsTestUtils.createNotification("Host", "ercaswnyc2", "Down", new HashMap());

        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList, transientInterval, true);
        notificationAdapter.addObserver(this);
        notificationAdapter.subscribe();
        // cleared
        SmartsTestUtils.clearNotification("Host", "ercaswnyc2", "Down");
        checkObjectListSize(receivedObjects, 1);
        assertNotNull(receivedObjects.get("NOTIFICATION-Host_ercaswnyc2_Down1CLEAR"));

        //          Attribute changed
        HashMap attributes = new HashMap();
        attributes.put("Severity", 4);
        SmartsTestUtils.updateNotification("Host", "ercaswnyc2", "Down", attributes);
        checkObjectListSize(receivedObjects, 2);
        assertNotNull(receivedObjects.get("NOTIFICATION-Host_ercaswnyc2_Down2CHANGE"));

        // New Notification
        SmartsTestUtils.createNotification("Host", "ercaswnyc2", "Down", new HashMap());
        checkObjectListSize(receivedObjects, 3);
        assertNotNull(receivedObjects.get("NOTIFICATION-Host_ercaswnyc2_Down3NOTIFY"));
    }

    public void testNotifyClearAcknowledgeNotifyUnacknowledge() throws Exception {
        int transientInterval = 0;
        ArrayList atts = new ArrayList();
        atts.add("Name");
        atts.add("Severity");
        atts.add("Acknowledged");
        atts.add("Owner");
        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, atts, nlList, transientInterval, true);
        notificationAdapter.addObserver(this);
        notificationAdapter.subscribe();

        SmartsTestUtils.createNotification("Host", "ercaswnyc2", "Down", new HashMap());
        checkObjectListSize(receivedObjects, 1);
        assertNotNull(receivedObjects.get("NOTIFICATION-Host_ercaswnyc2_Down1NOTIFY"));

        // cleared
        SmartsTestUtils.clearNotification("Host", "ercaswnyc2", "Down");
        checkObjectListSize(receivedObjects, 2);
        assertNotNull(receivedObjects.get("NOTIFICATION-Host_ercaswnyc2_Down2CLEAR"));

        //          Attribute changed
        HashMap attributes = new HashMap();
        SmartsTestUtils.acknowledgeNotification("Host", "ercaswnyc2", "Down");
        checkObjectListSize(receivedObjects, 3);
        assertNotNull(receivedObjects.get("NOTIFICATION-Host_ercaswnyc2_Down3CHANGE"));

//             New Notification
        SmartsTestUtils.createNotification("Host", "ercaswnyc2", "Down", new HashMap());
        checkObjectListSize(receivedObjects, 4);
        assertNotNull(receivedObjects.get("NOTIFICATION-Host_ercaswnyc2_Down4NOTIFY"));

        //Unacknowledge
        SmartsTestUtils.unAcknowledgeNotification("Host", "ercaswnyc2", "Down");
        checkObjectListSize(receivedObjects, 5);
        assertNotNull(receivedObjects.get("NOTIFICATION-Host_ercaswnyc2_Down5CHANGE"));

    }

    public void testChangeNotifyOnAClearedEvent() throws Exception {
        int transientInterval = 100;
        // create notification
        SmartsTestUtils.createNotification("Host", "ercaswnyc2", "Down", new HashMap());
        SmartsTestUtils.clearNotification("Host", "ercaswnyc2", "Down");

        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList, transientInterval, false);
        notificationAdapter.addObserver(this);
        notificationAdapter.subscribe();
        checkObjectListSize(receivedObjects, 1);
        assertNotNull(receivedObjects.get("NOTIFICATION-Host_ercaswnyc2_Down1CLEAR"));
        //          Attribute changed
        HashMap attributes = new HashMap();
        attributes.put("Severity", 4);
        SmartsTestUtils.updateNotification("Host", "ercaswnyc2", "Down", attributes);
        checkObjectListSize(receivedObjects, 1);

        // New Notification
        SmartsTestUtils.createNotification("Host", "ercaswnyc2", "Down", new HashMap());
        checkObjectListSize(receivedObjects, 2);
        assertNotNull(receivedObjects.get("NOTIFICATION-Host_ercaswnyc2_Down2NOTIFY"));
    }

    public void testChangeNotifyOnAClearedEventWithTransientIntervalZero() throws Exception {
        int transientInterval = 0;
        SmartsTestUtils.createNotification("Host", "ercaswnyc2", "Down", new HashMap());
        SmartsTestUtils.clearNotification("Host", "ercaswnyc2", "Down");

        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList, transientInterval, false);
        notificationAdapter.addObserver(this);
        notificationAdapter.subscribe();
        checkObjectListSize(receivedObjects, 1);
        assertNotNull(receivedObjects.get("NOTIFICATION-Host_ercaswnyc2_Down1CLEAR"));

        //          Attribute changed
        HashMap attributes = new HashMap();
        attributes.put("Severity", 4);
        SmartsTestUtils.updateNotification("Host", "ercaswnyc2", "Down", attributes);
        checkObjectListSize(receivedObjects, 2);
        assertNotNull(receivedObjects.get("NOTIFICATION-Host_ercaswnyc2_Down2CHANGE"));

        // New Notification
        SmartsTestUtils.createNotification("Host", "ercaswnyc2", "Down", new HashMap());
        checkObjectListSize(receivedObjects, 3);
        assertNotNull(receivedObjects.get("NOTIFICATION-Host_ercaswnyc2_Down3NOTIFY"));
    }

    public void testNotifyClearSTARTAcknowledgeArchive() throws Exception {
        int transientInterval = 0;
        // create notification
        String notificationName = "NOTIFICATION-Switch_ercaswnyc2_Down";
        SmartsTestUtils.createNotification("Switch", "ercaswnyc2", "Down", new HashMap());
        // clear notification
        SmartsTestUtils.clearNotification("Switch", "ercaswnyc2", "Down");

        CommonTestUtils.wait(3000);

        monitoredAtts.add("LastChangedAt");
        monitoredAtts.add("Owner");
        monitoredAtts.add("Acknowledged");
        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList, transientInterval, false);
        notificationAdapter.addObserver(this);
        notificationAdapter.subscribe();
        checkObjectListSize(receivedObjects, 1);
        assertNotNull(receivedObjects.get(notificationName + "1CLEAR"));

        // Acknowledge
        SmartsTestUtils.acknowledgeNotification("Switch", "ercaswnyc2", "Down");
        checkObjectListSize(receivedObjects, 2);
        assertNotNull(receivedObjects.get(notificationName + "2CHANGE"));

        // Archive
        SmartsTestUtils.archiveNotification("Switch", "ercaswnyc2", "Down");
        checkObjectListSize(receivedObjects, 3);
        assertNotNull(receivedObjects.get(notificationName + "3ARCHIVE"));
    }

    public void testNotifyClearSTARTUnacknowledgeAcknowledgeArchive() throws Exception {
        int transientInterval = 0;

        // create notification
        String notificationName = "NOTIFICATION-Switch_ercaswnyc2_Down";
        SmartsTestUtils.createNotification("Switch", "ercaswnyc2", "Down", new HashMap());
        // clear notification
        SmartsTestUtils.clearNotification("Switch", "ercaswnyc2", "Down");

        monitoredAtts.add("LastChangedAt");
        monitoredAtts.add("Owner");
        monitoredAtts.add("Acknowledged");
        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList, transientInterval, false);
        notificationAdapter.addObserver(this);
        notificationAdapter.subscribe();
        checkObjectListSize(receivedObjects, 1);
        assertNotNull(receivedObjects.get(notificationName + "1CLEAR"));

        // Unacknowledge
        SmartsTestUtils.unAcknowledgeNotification("Switch", "ercaswnyc2", "Down");
        checkObjectListSize(receivedObjects, 2);
        assertNotNull(receivedObjects.get(notificationName + "2CHANGE"));

        // Acknowledge
        SmartsTestUtils.acknowledgeNotification("Switch", "ercaswnyc2", "Down");
        checkObjectListSize(receivedObjects, 3);
        assertNotNull(receivedObjects.get(notificationName + "3CHANGE"));

        // Archive
        SmartsTestUtils.archiveNotification("Switch", "ercaswnyc2", "Down");
        checkObjectListSize(receivedObjects, 4);
        assertNotNull(receivedObjects.get(notificationName + "4ARCHIVE"));
    }

    public void testAttributeChangesForClearedNotificationsAreNotIgnored() throws Exception, SmRemoteException {
        int transientInterval = 100;


        monitoredAtts.add("Owner");
        monitoredAtts.add("EventState");
        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList, transientInterval, true);
        notificationAdapter.addObserver(this);
        notificationAdapter.subscribe();

        String param = "NOTIFICATION-Host_ercaswnyc2_Down";
        // create a notification
        SmartsTestUtils.createNotification("Host", "ercaswnyc2", "Down", new HashMap());
        checkObjectListSize(receivedObjects, 1);
        assertNotNull(receivedObjects.get(param + "1NOTIFY"));

        // clear the notification
        SmartsTestUtils.clearNotification("Host", "ercaswnyc2", "Down");
        NotificationIdentifierParams identiferParams = new NotificationIdentifierParams("Host", "ercaswnyc2", "Down");
        assertTrue("Notification could not be updated in time", SmartsTestUtils.waitForNotificationAttributeUpdate(identiferParams, "Active", "false"));
        checkObjectListSize(receivedObjects, 2);
        Map notification = (Map)receivedObjects.get(param + "2CLEAR");
        assertNotNull(notification);
        assertEquals(false, notification.get("Active"));
        assertEquals("INACTIVE", notification.get("EventState"));

        // ACKNOWLEDGE
        NotificationAcknowledgeParams acknowledgeParameters = SmartsTestUtils.getTestParametersForAcknowledge();
        acknowledgeParameters.setUser("NewOwner");
        acknowledgeParameters.setAuditTrailText("Acknowledging the notification");
        SmartsTestUtils.getNotificationAdapter().acknowledge(identiferParams, acknowledgeParameters);
        checkObjectListSize(receivedObjects, 3);
        assertNotNull(receivedObjects.get(param + "3CHANGE"));

        // RELEASE OWNERSHIP
        NotificationAcknowledgeParams releaseOwnershipParameters = SmartsTestUtils.getTestParametersForAcknowledge();
        releaseOwnershipParameters.setUser("ReleasingOwner");
        releaseOwnershipParameters.setAuditTrailText("Releasing ownership of the notification");
        SmartsTestUtils.getNotificationAdapter().releaseOwnership(identiferParams, releaseOwnershipParameters);
        checkObjectListSize(receivedObjects, 4);
        assertNotNull(receivedObjects.get(param + "4CHANGE"));

        // TAKE OWNERSHIP
        NotificationAcknowledgeParams takeOwnershipParameters = SmartsTestUtils.getTestParametersForAcknowledge();
        SmartsTestUtils.getNotificationAdapter().takeOwnership(identiferParams, takeOwnershipParameters);
        checkObjectListSize(receivedObjects, 5);
        assertNotNull(receivedObjects.get(param + "5CHANGE"));
    }


    class NotificationCreatorChangeClearThread extends NotificationCreatorThread {
        public NotificationCreatorChangeClearThread(int count, int delay) {
            super(count, delay);
        }

        public void run() {
            NotificationCreateParams createParameters = SmartsTestUtils.getTestParametersForCreate();
            NotificationIdentifierParams identifierParameters = createParameters.getIdentifierParameters();
            NotificationClearParams clearParameters = SmartsTestUtils.getTestParametersForClear();
            NotificationAcknowledgeParams archiveParameters = SmartsTestUtils.getTestParametersForAcknowledge();
            try {
                for (int i = 0; i < count; i++) {
                    identifierParameters.setClassName("Host" + i);
                    SmartsTestUtils.getNotificationAdapter().createNotification(createParameters);
                }
                CommonTestUtils.wait(delay);
                for (int i = 0; i < count; i++) {
                    identifierParameters.setClassName("Host" + i);
                    SmartsTestUtils.getNotificationAdapter().clearNotification(identifierParameters, clearParameters);
                }
                CommonTestUtils.wait(delay);
                for (int i = 0; i < count; i++) {
                    identifierParameters.setClassName("Host" + i);
                    SmartsTestUtils.getNotificationAdapter().createNotification(createParameters);
                }
                CommonTestUtils.wait(delay);
                for (int i = 0; i < count; i++) {
                    identifierParameters.setClassName("Host" + i);
                    SmartsTestUtils.getNotificationAdapter().clearNotification(identifierParameters, clearParameters);
                }

                CommonTestUtils.wait(delay);
                Map attributes = new HashMap();
                attributes.put("Severity", new MR_AnyValUnsignedInt(4));
                NotificationUpdateParams updateParameters = new NotificationUpdateParams(attributes);
                for (int i = 0; i < count; i++) {
                    identifierParameters.setClassName("Host" + i);
                    SmartsTestUtils.getNotificationAdapter().updateNotification(identifierParameters, updateParameters);
                }
                CommonTestUtils.wait(delay);
                attributes = new HashMap();
                attributes.put("Severity", new MR_AnyValUnsignedInt(2));
                updateParameters = new NotificationUpdateParams(attributes);
                for (int i = 0; i < count; i++) {
                    identifierParameters.setClassName("Host" + i);
                    SmartsTestUtils.getNotificationAdapter().updateNotification(identifierParameters, updateParameters);
                }
                CommonTestUtils.wait(delay);
                for (int i = 0; i < count; i++) {
                    identifierParameters.setClassName("Host" + i);
                    SmartsTestUtils.getNotificationAdapter().archiveNotification(identifierParameters, archiveParameters);
                }
                CommonTestUtils.wait(delay);
                for (int i = 0; i < count; i++) {
                    identifierParameters.setClassName("Host" + i);
                    SmartsTestUtils.getNotificationAdapter().archiveNotification(identifierParameters, archiveParameters);
                }
            } catch (Exception e) {
            }
        }
    }


    class NotificationCreatorThread extends Thread {
        int count;
        int delay;

        public NotificationCreatorThread(int count, int delay) {
            this.count = count;
            this.delay = delay;
        }

        public void run() {
            NotificationCreateParams createParameters = SmartsTestUtils.getTestParametersForCreate();
            NotificationIdentifierParams identifierParameters = createParameters.getIdentifierParameters();
            try {
                for (int i = 0; i < count; i++) {
                    CommonTestUtils.wait(delay);
                    identifierParameters.setClassName("Host" + i);
                    SmartsTestUtils.getNotificationAdapter().createNotification(createParameters);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update(Observable o, Object arg) {
        Map notification = (Map) arg;
        String notificationName = (String) notification.get(SmartsConstants.INSTANCENAME);
        String eventType = (String) notification.get(BaseSmartsListeningAdapter.EVENT_TYPE_NAME);
        String incrementedNotificationName = notificationName + (receivedObjects.size() + 1) + eventType;
        receivedObjects.put(incrementedNotificationName, arg);
    }

    private void checkObjectListSize(final Map objects, final int numberOfObjects) throws Exception {
        CommonTestUtils.waitFor(new WaitAction() {
            @Override
            public void check() throws Exception {
                assertEquals("Expected number of objects couldnot be recieved", numberOfObjects, objects.size());
            }
        }, 100);
    }
}
