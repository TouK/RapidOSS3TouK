package com.ifountain.smarts.datasource;

import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.comp.test.util.logging.TestLogUtils;
import com.smarts.repos.MR_PropertyNameValue;
import com.smarts.repos.MR_AnyValString;
import com.smarts.repos.MR_AnyValUnsignedInt;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.HashMap;

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
 * Time: 12:09:14 PM
 */
public class StagingAreaTest extends SmartsTestCase {
    public void testUpdateStagedNotifications() throws Exception {

        MockStagingArea stagingArea = new MockStagingArea(null, TestLogUtils.log, null, 0);
        String notificationName = "MyNotification";
        MR_PropertyNameValue[] nameValues = new MR_PropertyNameValue[3];
        nameValues[0] = new MR_PropertyNameValue("name", new MR_AnyValString("MyNotification"));
        nameValues[1] = new MR_PropertyNameValue("severity", new MR_AnyValUnsignedInt(1));
        nameValues[2] = new MR_PropertyNameValue("active", new MR_AnyValString("true"));

        // NOTIFY
        stagingArea.updateStagedNotifications(notificationName, "NOTIFY", nameValues);
        assertEquals(1, stagingArea.stagedNotifications.size());
        StagedNotification stagedNotification = (StagedNotification) stagingArea.stagedNotifications.get("MyNotification");
        assertEquals("NOTIFY", stagedNotification.getEventName());
        assertEquals("MyNotification", stagedNotification.getNameValuePairs()[0].getPropertyValue().toString());
        assertEquals("1", stagedNotification.getNameValuePairs()[1].getPropertyValue().toString());
        assertEquals("true", stagedNotification.getNameValuePairs()[2].getPropertyValue().toString());

        // CHANGE
        nameValues[1] = new MR_PropertyNameValue("severity", new MR_AnyValUnsignedInt(2));
        stagingArea.updateStagedNotifications(notificationName, "CHANGE", nameValues);
        assertEquals(1, stagingArea.stagedNotifications.size());
        stagedNotification = (StagedNotification) stagingArea.stagedNotifications.get("MyNotification");
        assertEquals("NOTIFY", stagedNotification.getEventName());
        assertEquals("MyNotification", stagedNotification.getNameValuePairs()[0].getPropertyValue().toString());
        assertEquals("2", stagedNotification.getNameValuePairs()[1].getPropertyValue().toString());
        assertEquals("true", stagedNotification.getNameValuePairs()[2].getPropertyValue().toString());

        // CLEAR
        nameValues[1] = new MR_PropertyNameValue("severity", new MR_AnyValUnsignedInt(3));
        nameValues[2] = new MR_PropertyNameValue("active", new MR_AnyValString("false"));
        stagingArea.updateStagedNotifications(notificationName, "CLEAR", nameValues);
        assertEquals(1, stagingArea.stagedNotifications.size());
        stagedNotification = (StagedNotification) stagingArea.stagedNotifications.get("MyNotification");
        assertEquals("CLEAR", stagedNotification.getEventName());
        assertEquals("MyNotification", stagedNotification.getNameValuePairs()[0].getPropertyValue().toString());
        assertEquals("3", stagedNotification.getNameValuePairs()[1].getPropertyValue().toString());
        assertExpectedNotificationReadEventCount(stagingArea.list, 1);
        MR_PropertyNameValue[] propertyNameValues = (MR_PropertyNameValue[]) stagingArea.list.get(notificationName + "1NOTIFY");
        assertNotNull(propertyNameValues);

        // CHANGE
        nameValues[1] = new MR_PropertyNameValue("severity", new MR_AnyValUnsignedInt(4));
        stagingArea.updateStagedNotifications(notificationName, "CHANGE", nameValues);
        assertEquals(1, stagingArea.stagedNotifications.size());
        stagedNotification = (StagedNotification) stagingArea.stagedNotifications.get("MyNotification");
        assertEquals("CLEAR", stagedNotification.getEventName());
        assertEquals("MyNotification", stagedNotification.getNameValuePairs()[0].getPropertyValue().toString());
        assertEquals("4", stagedNotification.getNameValuePairs()[1].getPropertyValue().toString());
        assertEquals("false", stagedNotification.getNameValuePairs()[2].getPropertyValue().toString());

        //ARCHIVE
        stagingArea.updateStagedNotifications(notificationName, "ARCHIVE", nameValues);
        assertEquals(0, stagingArea.stagedNotifications.size());
        assertExpectedNotificationReadEventCount(stagingArea.list, 3);
        propertyNameValues = (MR_PropertyNameValue[]) stagingArea.list.get(notificationName + "2CLEAR");
        assertNotNull(propertyNameValues);
        propertyNameValues = (MR_PropertyNameValue[]) stagingArea.list.get(notificationName + "3ARCHIVE");
        assertNotNull(propertyNameValues);
    }

    public void testUpdateStagedNotificationsArchiveComesWhenStagingIsEmpty() throws Exception {

        MockStagingArea stagingArea = new MockStagingArea(null, TestLogUtils.log, null, 0);
        String notificationName = "MyNotification";
        MR_PropertyNameValue[] nameValues = new MR_PropertyNameValue[3];
        nameValues[0] = new MR_PropertyNameValue("name", new MR_AnyValString("MyNotification"));
        nameValues[1] = new MR_PropertyNameValue("severity", new MR_AnyValUnsignedInt(1));
        nameValues[2] = new MR_PropertyNameValue("active", new MR_AnyValString("true"));

        //ARCHIVE
        stagingArea.updateStagedNotifications(notificationName, "ARCHIVE", nameValues);
        assertEquals(0, stagingArea.stagedNotifications.size());
        assertExpectedNotificationReadEventCount(stagingArea.list,1);
        MR_PropertyNameValue[] propertyNameValues = (MR_PropertyNameValue[]) stagingArea.list.get(notificationName+"1ARCHIVE");
        assertNotNull(propertyNameValues);

    }

    public void testUpdateStagedNotificationsChangeChangeClear() throws Exception {

        MockStagingArea stagingArea = new MockStagingArea(null, TestLogUtils.log, null, 0);
        String notificationName = "MyNotification";
        MR_PropertyNameValue[] nameValues = new MR_PropertyNameValue[3];
        nameValues[0] = new MR_PropertyNameValue("name", new MR_AnyValString("MyNotification"));
        nameValues[1] = new MR_PropertyNameValue("severity", new MR_AnyValUnsignedInt(1));
        nameValues[2] = new MR_PropertyNameValue("active", new MR_AnyValString("true"));

        // CHANGE
        nameValues[1] = new MR_PropertyNameValue("severity", new MR_AnyValUnsignedInt(1));
        stagingArea.updateStagedNotifications(notificationName, "CHANGE", nameValues);
        assertEquals(1, stagingArea.stagedNotifications.size());
        StagedNotification stagedNotification = (StagedNotification)stagingArea.stagedNotifications.get("MyNotification");
        assertEquals("CHANGE", stagedNotification.getEventName());
        assertEquals("MyNotification", stagedNotification.getNameValuePairs()[0].getPropertyValue().toString());
        assertEquals("1", stagedNotification.getNameValuePairs()[1].getPropertyValue().toString());
        assertEquals("true", stagedNotification.getNameValuePairs()[2].getPropertyValue().toString());

        // CHANGE
        nameValues[1] = new MR_PropertyNameValue("severity", new MR_AnyValUnsignedInt(2));
        stagingArea.updateStagedNotifications(notificationName, "CHANGE", nameValues);
        assertEquals(1, stagingArea.stagedNotifications.size());
        stagedNotification = (StagedNotification)stagingArea.stagedNotifications.get("MyNotification");
        assertEquals("CHANGE", stagedNotification.getEventName());
        assertEquals("MyNotification", stagedNotification.getNameValuePairs()[0].getPropertyValue().toString());
        assertEquals("2", stagedNotification.getNameValuePairs()[1].getPropertyValue().toString());
        assertEquals("true", stagedNotification.getNameValuePairs()[2].getPropertyValue().toString());

        // CLEAR
        nameValues[1] = new MR_PropertyNameValue("severity", new MR_AnyValUnsignedInt(3));
        nameValues[2] = new MR_PropertyNameValue("active", new MR_AnyValString("false"));
        stagingArea.updateStagedNotifications(notificationName, "CLEAR", nameValues);
        assertEquals(1, stagingArea.stagedNotifications.size());
        stagedNotification = (StagedNotification)stagingArea.stagedNotifications.get("MyNotification");
        assertEquals("CLEAR", stagedNotification.getEventName());
        assertEquals("MyNotification", stagedNotification.getNameValuePairs()[0].getPropertyValue().toString());
        assertEquals("3", stagedNotification.getNameValuePairs()[1].getPropertyValue().toString());
        assertExpectedNotificationReadEventCount(stagingArea.list,1);
        MR_PropertyNameValue[] propertyNameValues = (MR_PropertyNameValue[]) stagingArea.list.get(notificationName+"1CHANGE");
        assertNotNull(propertyNameValues);
    }

    class MockStagingArea extends StagingArea {
        private int notificationCount;
        public Map list;

        public MockStagingArea(SmartsNotificationListeningAdapter nAdapter, Logger logger, String nlName, int transientInterval) {
            super(nAdapter, logger, nlName, transientInterval);
        }

        protected void sendNotification(String eventType, MR_PropertyNameValue[] propertyNameValues) {
            if (list == null) {
                list = new HashMap();
            }
            notificationCount++;
            String incrementedNotificationName = null;
            for (int i = 0; i < propertyNameValues.length; i++) {
                MR_PropertyNameValue propertyNameValue = propertyNameValues[i];
                String name = propertyNameValue.getPropertyName();
                if (name.equalsIgnoreCase("Name")) {
                    String notificationName = propertyNameValue.getPropertyValue().toString();
                    incrementedNotificationName = notificationName + notificationCount + eventType;
                }
            }
            list.put(incrementedNotificationName, propertyNameValues);
        }
    }

    private void assertExpectedNotificationReadEventCount(Map list, int expectedCount) throws InterruptedException {
        boolean notificationsRead = false;
        for (int i = 0; i < 200; i++) {

            if(list.size() == expectedCount){
                notificationsRead = true;
                break;
            }
            else
            {
                Thread.sleep(100);
            }
        }
        if(!notificationsRead)
        {
            fail("expected [" + expectedCount + "] notifications but read [" + list.size() + "] notifications");
        }
    }
}
