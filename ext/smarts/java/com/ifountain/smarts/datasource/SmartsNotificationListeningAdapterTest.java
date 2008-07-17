package com.ifountain.smarts.datasource;

import com.ifountain.comp.test.util.CommonTestUtils;
import com.ifountain.comp.test.util.WaitAction;
import com.ifountain.comp.test.util.logging.TestLogUtils;
import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.test.util.SmartsTestUtils;
import com.ifountain.smarts.test.util.SmartsTestConstants;
import com.ifountain.smarts.util.SmartsConstants;
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
    // INTENTIONALLY LEFT THE WAITS FOR THE FOLLOWING TEST
    public void testTransientInterval() throws Exception {
        int transientInterval = 2500;
        monitoredAtts.add("OccurrenceCount");
        monitoredAtts.add("Owner");
        notificationAdapter = new SmartsNotificationListeningAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0,
                TestLogUtils.log, monitoredAtts, nlList, transientInterval, true);
        notificationAdapter.addObserver(this);
        notificationAdapter.subscribe();

        // SCENARIO 0: NOTIFY + CLEAR DURING TRANSIENT INTERVAL WILL BE A NOTIFY AND A CLEAR
        String notificationName = "NOTIFICATION-Switch_ercaswnyc2_Down";
        SmartsTestUtils.createNotification("Switch", "ercaswnyc2", "Down", new HashMap());
        CommonTestUtils.wait(500);
//        assertEquals(0, receivedObjects.size());
//
        SmartsTestUtils.clearNotification("Switch", "ercaswnyc2", "Down");
//        CommonTestUtils.wait(100000000);
//        assertEquals(1, receivedObjects.size()); // Arrival of Clear should immediately send the previous Notify
//        assertNotNull(receivedObjects.get(notificationName + "1NOTIFY"));
//        checkObjectListSize(receivedObjects, 2);
//         assertNotNull(receivedObjects.get(notificationName + "2CLEAR"));
//
//        // SCENARIO 1: NOTIFY + CHANGE(S) DURING TRANSIENT WILL BE A NOTIFY
//        // create a notification
//        SmartsTestUtils.createNotification("Host", "ercaswnyc2", "Down", new HashMap());
//        CommonTestUtils.wait(200);
//        notificationName = "NOTIFICATION-Host_ercaswnyc2_Down";
//        assertEquals(2, receivedObjects.size());
//
//        // create the same notification   // this will cause a CHANGE event due to count change
//        SmartsTestUtils.createNotification("Host", "ercaswnyc2", "Down", new HashMap());
//        CommonTestUtils.wait(4000); // transientInterval has passed
//        assertEquals(3, receivedObjects.size());
//        // we should still get a NOTIFY
//        assertNotNull(receivedObjects.get(notificationName + "3NOTIFY"));

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
