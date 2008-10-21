/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 20, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.datasource;

import com.ifountain.comp.test.util.CommonTestUtils;
import com.ifountain.comp.test.util.WaitAction;
import com.ifountain.comp.test.util.logging.TestLogUtils;
import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.test.util.SmartsTestUtils;
import com.ifountain.smarts.util.SmartsConstants;
import com.ifountain.smarts.util.SmartsHelper;
import com.ifountain.smarts.util.SmartsPropertyHelper;
import com.ifountain.smarts.util.params.*;
import com.smarts.remote.SmRemoteException;
import com.smarts.repos.*;

import java.util.*;

public class BaseNotificationAdapterTest extends SmartsTestCase {

    BaseNotificationAdapter notificationAdapter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        notificationAdapter = SmartsTestUtils.getNotificationAdapter();
        SmartsTestUtils.archiveAllNotifications();
    }
   
    public void testNotificationCreationWithElementNameAndElementClassName() throws Exception {
        String routerInstanceName="router1";
        String portInstanceName="port1";
        SmartsTestUtils.createTopologyInstance("Router",routerInstanceName);
        SmartsTestUtils.createTopologyInstance("Port",portInstanceName);


        String eventName = "WENameWECName";
        String newNotificationName = "NOTIFICATION-Port_"+portInstanceName+"_"+eventName;
        SmartsTestUtils.archiveNotification("Port", portInstanceName, eventName);

        NotificationCreateParams createParameters = SmartsTestUtils.getTestParametersForCreate();
        createParameters.getIdentifierParameters().setClassName("Port");
        createParameters.getIdentifierParameters().setInstanceName(portInstanceName);
        createParameters.getIdentifierParameters().setEventName(eventName);

        SmartsTestUtils.archiveAllNotifications();
        //the case where both elementname and elementclassname are set, in this case the event will have the given elementname and elementclassname
        createParameters.getAttributeParameters().put("ElementName",new MR_AnyValString(routerInstanceName));
        createParameters.getAttributeParameters().put("ElementClassName",new MR_AnyValString("Router"));

        // expected property values
        MR_AnyValString expClassName = new MR_AnyValString("Port");
        MR_AnyValString expInstName = new MR_AnyValString(portInstanceName);
        MR_AnyVal expOccurredOn = new MR_AnyValObjRef(new MR_Ref("Router", routerInstanceName));
        MR_AnyValString expEventDisplayName = new MR_AnyValString(eventName);
        MR_AnyValString expElementClassName = new MR_AnyValString("Router");
        MR_AnyValString expElementName = new MR_AnyValString(routerInstanceName);

        String[] propertyNamesToAssert1 = {  SmartsConstants.PARAM_CLASSNAME, SmartsConstants.PARAM_INSTANCENAME, "EventDisplayName",  "OccurredOn","ElementClassName","ElementName"};        
        MR_AnyVal[] expectedPropVals1 = { expClassName, expInstName, expEventDisplayName,  expOccurredOn, expElementClassName,expElementName};

        assertCreateNotification(createParameters, newNotificationName, 1);
        SmartsTestUtils.assertPropertyValues(newNotificationName, propertyNamesToAssert1, expectedPropVals1);

    }

    public void testNotificationCreationWithElementNameWithoutElementClassName() throws Exception {
        String routerInstanceName="router1";
        String portInstanceName="port1";
        SmartsTestUtils.createTopologyInstance("Router",routerInstanceName);
        SmartsTestUtils.createTopologyInstance("Port",portInstanceName);


        String eventName = "WENameWoutECName";
        String newNotificationName = "NOTIFICATION-Port_"+portInstanceName+"_"+eventName;
        SmartsTestUtils.archiveNotification("Port", portInstanceName, eventName);

        NotificationCreateParams createParameters = SmartsTestUtils.getTestParametersForCreate();
        createParameters.getIdentifierParameters().setClassName("Port");
        createParameters.getIdentifierParameters().setInstanceName(portInstanceName);
        createParameters.getIdentifierParameters().setEventName(eventName);
        
        SmartsTestUtils.archiveAllNotifications();
        //the case where element name is set elementclassname is not set, elementname and elementclassname expected to be assigned as instancename classname
        createParameters.getAttributeParameters().put("ElementName",new MR_AnyValString(routerInstanceName));
        

        // expected property values
        MR_AnyValString expClassName = new MR_AnyValString("Port");
        MR_AnyValString expInstName = new MR_AnyValString(portInstanceName);
        MR_AnyVal expOccurredOn = new MR_AnyValObjRef(new MR_Ref("Port", portInstanceName));
        MR_AnyValString expEventDisplayName = new MR_AnyValString(eventName);
        MR_AnyValString expElementClassName = new MR_AnyValString("Port");
        MR_AnyValString expElementName = new MR_AnyValString(portInstanceName);


        String[] propertyNamesToAssert = {  SmartsConstants.PARAM_CLASSNAME, SmartsConstants.PARAM_INSTANCENAME, "EventDisplayName",  "OccurredOn","ElementClassName","ElementName"};
        MR_AnyVal[] expectedPropVals = { expClassName, expInstName, expEventDisplayName,  expOccurredOn,expElementClassName, expElementName};

        assertCreateNotification(createParameters, newNotificationName, 1);
        SmartsTestUtils.assertPropertyValues(newNotificationName, propertyNamesToAssert, expectedPropVals);
    }
     public void testNotificationCreationWithoutElementNameWithElementClassName() throws Exception {
        String routerInstanceName="router1";
        String portInstanceName="port1";
        SmartsTestUtils.createTopologyInstance("Router",routerInstanceName);
        SmartsTestUtils.createTopologyInstance("Port",portInstanceName);


        String eventName = "WOutENameWECName";
        String newNotificationName = "NOTIFICATION-Port_"+portInstanceName+"_"+eventName;
        SmartsTestUtils.archiveNotification("Port", portInstanceName, eventName);

        NotificationCreateParams createParameters = SmartsTestUtils.getTestParametersForCreate();
        createParameters.getIdentifierParameters().setClassName("Port");
        createParameters.getIdentifierParameters().setInstanceName(portInstanceName);
        createParameters.getIdentifierParameters().setEventName(eventName);
         
        SmartsTestUtils.archiveAllNotifications();
        //the case where element name is set elementclassname is not set, elementname and elementclassname expected to be assigned as instancename classname
        createParameters.getAttributeParameters().put("ElementClassName",new MR_AnyValString("Router"));

        // expected property values
        MR_AnyValString expClassName = new MR_AnyValString("Port");
        MR_AnyValString expInstName = new MR_AnyValString(portInstanceName);
        MR_AnyVal expOccurredOn = new MR_AnyValObjRef(new MR_Ref("Port", portInstanceName));
        MR_AnyValString expEventDisplayName = new MR_AnyValString(eventName);
        MR_AnyValString expElementClassName = new MR_AnyValString("Port");
        MR_AnyValString expElementName = new MR_AnyValString(portInstanceName);


        String[] propertyNamesToAssert = {  SmartsConstants.PARAM_CLASSNAME, SmartsConstants.PARAM_INSTANCENAME, "EventDisplayName",  "OccurredOn","ElementClassName","ElementName"};
        MR_AnyVal[] expectedPropVals = { expClassName, expInstName, expEventDisplayName,  expOccurredOn,expElementClassName, expElementName};

        assertCreateNotification(createParameters, newNotificationName, 1);
        SmartsTestUtils.assertPropertyValues(newNotificationName, propertyNamesToAssert, expectedPropVals);
    }
    public void testFetchNotificationInstances() throws Exception {
        String className = "Switch";
        String instanceName = "eraaswiad";
        String eventName = "Down";
        notificationAdapter.createNotification(className, instanceName, eventName, new HashMap<String, Object>());
        Iterator<Map<String, Object>> rset = notificationAdapter.fetchNotifications(className, instanceName, ".*", 10);
        int count = 0;
        while (rset.hasNext()) {
            int numberOfVoidAtts = 0;
            Map<String, Object> rec = rset.next();
            count++;
            assertEquals(className, rec.get("ClassName"));
            List expectedAttributeNames = Arrays.asList(notificationAdapter.getAttributeNames(SmartsConstants.NOTIFICATION_CLASS_NAME));
            if (expectedAttributeNames.contains("internalElementClassName")) {
                numberOfVoidAtts++;
            }
            if (expectedAttributeNames.contains("internalElementName")) {
                numberOfVoidAtts++;
            }
            List expectedRelationNames = Arrays.asList(notificationAdapter.getRelationNames(SmartsConstants.NOTIFICATION_CLASS_NAME));
            assertEquals(expectedAttributeNames.size() + expectedRelationNames.size() - numberOfVoidAtts, rec.size());
        }
        assertEquals(1, count);
    }

    public void testGetNotificationInstances() throws Exception {
        String className = "Switch";
        String instanceName = "eraaswiad";
        String eventName = "Down";
        notificationAdapter.createNotification(className, instanceName, eventName, new HashMap<String, Object>());
        List<Map<String, Object>> rset = notificationAdapter.getNotifications(className, instanceName, ".*");
        assertEquals(1, rset.size());
        Map<String, Object> rec = rset.get(0);
        assertEquals(className, rec.get("ClassName"));
        int numberOfVoidAtts = 0;
        List expectedAttributeNames = Arrays.asList(notificationAdapter.getAttributeNames(SmartsConstants.NOTIFICATION_CLASS_NAME));
        if (expectedAttributeNames.contains("internalElementClassName")) {
            numberOfVoidAtts++;
        }
        if (expectedAttributeNames.contains("internalElementName")) {
            numberOfVoidAtts++;
        }
        List expectedRelationNames = Arrays.asList(notificationAdapter.getRelationNames(SmartsConstants.NOTIFICATION_CLASS_NAME));
        assertEquals(expectedAttributeNames.size() + expectedRelationNames.size() - numberOfVoidAtts, rec.size());
    }

    public void testGetNotification() throws Exception {
        String className = "Switch";
        String instanceName = "eraaswiad";
        String eventName = "Down";

        notificationAdapter.createNotification(className, instanceName, eventName, new HashMap<String, Object>());

        Map<String, Object> rec = notificationAdapter.getNotification(className, instanceName, eventName);
        assertEquals(className, rec.get("ClassName"));
        int numberOfVoidAtts = 0;
        List expectedAttributeNames = Arrays.asList(notificationAdapter.getAttributeNames(SmartsConstants.NOTIFICATION_CLASS_NAME));
        if (expectedAttributeNames.contains("internalElementClassName")) {
            numberOfVoidAtts++;
        }
        if (expectedAttributeNames.contains("internalElementName")) {
            numberOfVoidAtts++;
        }
        List expectedRelationNames = Arrays.asList(notificationAdapter.getRelationNames(SmartsConstants.NOTIFICATION_CLASS_NAME));
        assertEquals(expectedAttributeNames.size() + expectedRelationNames.size() - numberOfVoidAtts, rec.size());
    }

    public void testFetchNotificationInstancesWIthExpressionDisabled() throws Exception {
        String className = "Switch";
        String instanceName = "eraaswiad";
        String eventName = "Down";

        notificationAdapter.createNotification(className, instanceName, eventName, new HashMap<String, Object>());

        Iterator<Map<String, Object>> rset = notificationAdapter.fetchNotifications(className, instanceName, ".*", false, 10);
        assertFalse(rset.hasNext());
    }

    public void testFetchNotificationInstancesWithNullProperties() throws Exception {
        String className = "Switch";
        String instanceName = "eraaswiad";
        String eventName = "Down";

        notificationAdapter.createNotification(className, instanceName, eventName, new HashMap<String, Object>());

        Iterator<Map<String, Object>> rset = notificationAdapter.fetchNotifications(className, instanceName, ".*", null, true, 10);
        int count = 0;
        while (rset.hasNext()) {
            Map<String, Object> rec = rset.next();
            count++;
            assertEquals(className, rec.get("ClassName"));
            int numberOfVoidAtts = 0;
            List expectedAttributeNames = Arrays.asList(notificationAdapter.getAttributeNames(SmartsConstants.NOTIFICATION_CLASS_NAME));
            if (expectedAttributeNames.contains("internalElementClassName")) {
                numberOfVoidAtts++;
            }
            if (expectedAttributeNames.contains("internalElementName")) {
                numberOfVoidAtts++;
            }
            List expectedRelationNames = Arrays.asList(notificationAdapter.getRelationNames(SmartsConstants.NOTIFICATION_CLASS_NAME));
            assertEquals(expectedAttributeNames.size() + expectedRelationNames.size() - numberOfVoidAtts, rec.size());
        }
        assertEquals(1, count);
    }

    public void testFetchNotificationInstancesWithEmptyProperties() throws Exception {
        String className = "Switch";
        String instanceName = "eraaswiad";
        String eventName = "Down";

        notificationAdapter.createNotification(className, instanceName, eventName, new HashMap<String, Object>());

        Iterator<Map<String, Object>> rset = notificationAdapter.fetchNotifications(className, instanceName, ".*", new ArrayList<String>(), true, 10);
        int count = 0;
        while (rset.hasNext()) {
            Map<String, Object> rec = rset.next();
            count++;
            assertEquals(className, rec.get("ClassName"));
            int numberOfVoidAtts = 0;
            List expectedAttributeNames = Arrays.asList(notificationAdapter.getAttributeNames(SmartsConstants.NOTIFICATION_CLASS_NAME));
            if (expectedAttributeNames.contains("internalElementClassName")) {
                numberOfVoidAtts++;
            }
            if (expectedAttributeNames.contains("internalElementName")) {
                numberOfVoidAtts++;
            }
            List expectedRelationNames = Arrays.asList(notificationAdapter.getRelationNames(SmartsConstants.NOTIFICATION_CLASS_NAME));
            assertEquals(expectedAttributeNames.size() + expectedRelationNames.size() - numberOfVoidAtts, rec.size());
        }
        assertEquals(1, count);
    }

    public void testFetchNotificationInstancesWithSpecifiedProperties() throws Exception {
        String className = "Switch";
        String instanceName = "eraaswiad";
        String eventName = "Down";

        List<String> attrbutes = new ArrayList<String>();
        attrbutes.add("Severity");
        String unknownNotificationProperty = "UNKNOWN";
        attrbutes.add(unknownNotificationProperty);

        Map<String, Object> propertyValues = new HashMap<String, Object>();
        propertyValues.put("Severity", "3");
        notificationAdapter.createNotification(className, instanceName, eventName, propertyValues);

        Iterator<Map<String, Object>> rset = notificationAdapter.fetchNotifications(className, instanceName, ".*", attrbutes, true, 10);
        int count = 0;
        while (rset.hasNext()) {
            Map<String, Object> rec = rset.next();
            count++;
            assertEquals(3l, rec.get("Severity"));
            assertEquals(null, rec.get(unknownNotificationProperty));
            assertEquals(1, rec.size());
        }
        assertEquals(1, count);
    }

    public void testArchiveNotificationRemovesFromNotificationList() throws Exception {

        String notificationName = "NOTIFICATION-Switch_ercaswnyc2_Down";
        SmartsTestUtils.archiveNotification(notificationName);

        int instanceCount = notificationAdapter.getInstances("ICS_Notification").length;
        ArrayList<String> listMemberNotifications = SmartsHelper.getExistingNotificationsOfAList(notificationAdapter, "ICS_NL-nlDeveloper");
        int listCount = listMemberNotifications.size();

        // Scenario 1: Delete active notification
        NotificationCreateParams createParameters = SmartsTestUtils.getTestParametersForCreate();
        notificationAdapter.createNotification(createParameters);

        listMemberNotifications = SmartsHelper.getExistingNotificationsOfAList(notificationAdapter, "ICS_NL-nlDeveloper");
        assertEquals(instanceCount + 1, notificationAdapter.getInstances("ICS_Notification").length);
        assertExpectedListCount(listMemberNotifications, listCount + 1);

        NotificationIdentifierParams identifierParameters = SmartsTestUtils.getTestParametersForIdentifier();
        NotificationAcknowledgeParams archiveParameters = SmartsTestUtils.getTestParametersForAcknowledge();
        notificationAdapter.archiveNotification(identifierParameters, archiveParameters);

        listMemberNotifications = SmartsHelper.getExistingNotificationsOfAList(notificationAdapter, "ICS_NL-nlDeveloper");
        assertEquals(instanceCount, notificationAdapter.getInstances("ICS_Notification").length);
        assertExpectedListCount(listMemberNotifications, listCount);

        // Scenario 2: Delete inactive (cleared) notification
        notificationAdapter.createNotification(createParameters);

        listMemberNotifications = SmartsHelper.getExistingNotificationsOfAList(notificationAdapter, "ICS_NL-nlDeveloper");
        assertEquals(instanceCount + 1, notificationAdapter.getInstances("ICS_Notification").length);
        assertExpectedListCount(listMemberNotifications, listCount + 1);

        NotificationClearParams clearParameters = SmartsTestUtils.getTestParametersForClear();
        notificationAdapter.clearNotification(identifierParameters, clearParameters);

        notificationAdapter.archiveNotification(identifierParameters, archiveParameters);

        listMemberNotifications = SmartsHelper.getExistingNotificationsOfAList(notificationAdapter, "ICS_NL-nlDeveloper");
        assertEquals(instanceCount, notificationAdapter.getInstances("ICS_Notification").length);
        assertExpectedListCount(listMemberNotifications, listCount);

        //Delete operation is successful since notification does not exist.
        assertTrue(notificationAdapter.archiveNotification(identifierParameters, archiveParameters));
    }

    public void testCreateAndClearNotification() throws Exception {
        String newNotificationName = "NOTIFICATION-Switch_ercaswnyc2_Down";
        SmartsTestUtils.archiveNotification(newNotificationName);

        NotificationCreateParams createParameters = SmartsTestUtils.getTestParametersForCreate();

        // expected property values
        String[] propertyNamesToAssert = {"Description", "CreationClassName", SmartsConstants.PARAM_INSTANCENAME, "EventDisplayName", "Severity", "Active", "OccurredOn", "OccurrenceCount", "UserDefined1", "Owner", "SourceDomainName"};
        MR_AnyValString expDesc = new MR_AnyValString("TUGRUL");
        MR_AnyValString expCreationClassName = new MR_AnyValString("ICS_Notification");
        MR_AnyValString expInstName = new MR_AnyValString("ercaswnyc2");
        MR_AnyVal expOccurredOn = new MR_AnyValObjRef(new MR_Ref("Switch", "ercaswnyc2"));
        MR_AnyValString expEventDisplayName = new MR_AnyValString("Down");
        MR_AnyValUnsignedInt expSeverity = new MR_AnyValUnsignedInt(2);
        MR_AnyValBoolean expActive = new MR_AnyValBoolean(true);
        MR_AnyValUnsignedInt expCount = new MR_AnyValUnsignedInt(1);
        MR_AnyValString expUserDefined1 = new MR_AnyValString("");
        MR_AnyValString expOwner = new MR_AnyValString("");
        MR_AnyValString source = new MR_AnyValString("EastRegion");
        MR_AnyVal[] expectedPropVals1 = {expDesc, expCreationClassName, expInstName, expEventDisplayName, expSeverity, expActive, expOccurredOn, expCount, expUserDefined1, expOwner, source};

        // TRY ACKNOWLEDGE FOR NON_EXISTENT NOTIFICATION
        NotificationAcknowledgeParams acknowledgeParameters = SmartsTestUtils.getTestParametersForAcknowledge();
        assertFalse("Notification should NOT be Acknowledged", notificationAdapter.acknowledge(createParameters.getIdentifierParameters(), acknowledgeParameters));

        // TRY UNACKNOWLEDGE FOR NON_EXISTENT NOTIFICATION
        NotificationAcknowledgeParams unacknowledgeParameters = SmartsTestUtils.getTestParametersForAcknowledge();
        assertFalse("Notification should NOT be Uncknowledged", notificationAdapter.unacknowledge(createParameters.getIdentifierParameters(), unacknowledgeParameters));

        // TRY ADD AUDIT ENTRY FOR NON_EXISTENT NOTIFICATION
        NotificationAuditParams auditParameters = SmartsTestUtils.getTestParametersForAudit();
        assertFalse("Audit Entry should NOT be added", notificationAdapter.addAuditEntry(createParameters.getIdentifierParameters(), auditParameters));

        // TRY TAKE OWNERSHIP FOR NON_EXISTENT NOTIFICATION
        NotificationAcknowledgeParams takeOwnershipParameters = SmartsTestUtils.getTestParametersForAcknowledge();
        assertFalse("Notification ownership should NOT be changed", notificationAdapter.takeOwnership(createParameters.getIdentifierParameters(), takeOwnershipParameters));

        // TRY RELEASE OWNERSHIP FOR NON_EXISTENT NOTIFICATION
        NotificationAcknowledgeParams releaseOwnershipParameters = SmartsTestUtils.getTestParametersForAcknowledge();
        assertFalse("Notification ownership should NOT be changed", notificationAdapter.releaseOwnership(createParameters.getIdentifierParameters(), releaseOwnershipParameters));

        // CREATE NOTIFICATION
        assertCreateNotification(createParameters, newNotificationName, 1);
        SmartsTestUtils.assertPropertyValues(newNotificationName, propertyNamesToAssert, expectedPropVals1);
        SmartsTestUtils.assertAuditTrail(newNotificationName, "TestUser", "NOTIFY", "auditTrailText");

        // ACKNOWLDEGE NOTIFICATION
        assertTrue("Notification is NOT Acknowledged", notificationAdapter.acknowledge(createParameters.getIdentifierParameters(), acknowledgeParameters));
        SmartsTestUtils.waitForNotificationAttributeUpdate(createParameters.getIdentifierParameters(), "Owner", "AckUser");
        expOwner = new MR_AnyValString("AckUser");
        MR_AnyVal[] expectedPropVals2 = {expDesc, expCreationClassName, expInstName, expEventDisplayName, expSeverity, expActive, expOccurredOn, expCount, expUserDefined1, expOwner, source};
        SmartsTestUtils.assertPropertyValues(newNotificationName, propertyNamesToAssert, expectedPropVals2);
        SmartsTestUtils.assertAuditTrail(newNotificationName, "AckUser", "ACKNOWLEDGE", "acknowledging the notification");

        // UNACKNOWLDEGE NOTIFICATION
        unacknowledgeParameters.setUser("UnackUser");
        unacknowledgeParameters.setAuditTrailText("Unacknowledging the notification");
        assertTrue("Notification is NOT Unacknowledged", notificationAdapter.unacknowledge(createParameters.getIdentifierParameters(), unacknowledgeParameters));
        SmartsTestUtils.waitForNotificationAttributeUpdate(createParameters.getIdentifierParameters(), "Owner", "UnackUser");
        expOwner = new MR_AnyValString("UnackUser");
        MR_AnyVal[] expectedPropVals3 = {expDesc, expCreationClassName, expInstName, expEventDisplayName, expSeverity, expActive, expOccurredOn, expCount, expUserDefined1, expOwner, source};
        SmartsTestUtils.assertPropertyValues(newNotificationName, propertyNamesToAssert, expectedPropVals3);
        SmartsTestUtils.assertAuditTrail(newNotificationName, "UnackUser", "UNACKNOWLEDGE", "Unacknowledging the notification");

        // ADD AUDIT ENTRY
        assertTrue("Notification is NOT Unacknowledged", notificationAdapter.addAuditEntry(createParameters.getIdentifierParameters(), auditParameters));
        // IT IS HARDER TO CHECK THIS, SO I AM LEAVING IT HERE FOR THE TIME BEING
        CommonTestUtils.wait(500);
        expOwner = new MR_AnyValString("UnackUser"); // owner does not change
        MR_AnyVal[] expectedPropVals4 = {expDesc, expCreationClassName, expInstName, expEventDisplayName, expSeverity, expActive, expOccurredOn, expCount, expUserDefined1, expOwner, source};
        SmartsTestUtils.assertPropertyValues(newNotificationName, propertyNamesToAssert, expectedPropVals4);
        SmartsTestUtils.assertAuditTrail(newNotificationName, "auditUser", "ACTION", "text for adding audit entry");

        // TAKE OWNERSHIP
        takeOwnershipParameters.setUser("Owner");
        takeOwnershipParameters.setAuditTrailText("Taking ownership of the notification");
        notificationAdapter.takeOwnership(createParameters.getIdentifierParameters(), takeOwnershipParameters);
        SmartsTestUtils.waitForNotificationAttributeUpdate(createParameters.getIdentifierParameters(), "Owner", "Owner");
        expOwner = new MR_AnyValString("Owner");
        MR_AnyVal[] expectedPropVals5 = {expDesc, expCreationClassName, expInstName, expEventDisplayName, expSeverity, expActive, expOccurredOn, expCount, expUserDefined1, expOwner, source};
        SmartsTestUtils.assertPropertyValues(newNotificationName, propertyNamesToAssert, expectedPropVals5);
        SmartsTestUtils.assertAuditTrail(newNotificationName, "Owner", "TAKE_OWNERSHIP", "Taking ownership of the notification");

        // RELEASE OWNERSHIP
        releaseOwnershipParameters.setUser("ReleasingOwner");
        releaseOwnershipParameters.setAuditTrailText("Releasing ownership of the notification");
        notificationAdapter.releaseOwnership(createParameters.getIdentifierParameters(), releaseOwnershipParameters);
        expOwner = new MR_AnyValString(""); // owner is released
        MR_AnyVal[] expectedPropVals6 = {expDesc, expCreationClassName, expInstName, expEventDisplayName, expSeverity, expActive, expOccurredOn, expCount, expUserDefined1, expOwner, source};
        SmartsTestUtils.assertPropertyValues(newNotificationName, propertyNamesToAssert, expectedPropVals6);
        SmartsTestUtils.assertAuditTrail(newNotificationName, "ReleasingOwner", "RELEASE_OWNERSHIP", "Releasing ownership of the notification");

        // TRY TO UPDATE A NON_EXISTENT NOTIFICATION  - NOTHING WILL BE UPDATED
        HashMap<String, MR_AnyVal> attributesToUpdate = new HashMap<String, MR_AnyVal>();
        attributesToUpdate.put("Severity", new MR_AnyValUnsignedInt(3));
        NotificationIdentifierParams identifierParameters = new NotificationIdentifierParams("XYZ", "XYZ", "XYZ");
        NotificationUpdateParams updateParameters = new NotificationUpdateParams(attributesToUpdate);
        notificationAdapter.updateNotification(identifierParameters, updateParameters);
        String[] propertyNamesToAssertForUpdate = {"Severity"};
        MR_AnyVal[] expectedPropValsForUpdate = {expSeverity};
        SmartsTestUtils.assertPropertyValues(newNotificationName, propertyNamesToAssertForUpdate, expectedPropValsForUpdate);

        // UPDATE SOME ATTRIBUTES
        identifierParameters = new NotificationIdentifierParams("Switch", "ercaswnyc2", "Down");
        attributesToUpdate.put("UserDefined1", new MR_AnyValString("Updated UserDefined1")); // add UserDefined to the existing Severity atribute
        updateParameters = new NotificationUpdateParams(attributesToUpdate);
        notificationAdapter.updateNotification(identifierParameters, updateParameters);
        expSeverity = new MR_AnyValUnsignedInt(3);
        expUserDefined1 = new MR_AnyValString("Updated UserDefined1");
        MR_AnyVal[] expectedPropVals7 = {expDesc, expCreationClassName, expInstName, expEventDisplayName, expSeverity, expActive, expOccurredOn, expCount, expUserDefined1, expOwner, source};
        SmartsTestUtils.assertPropertyValues(newNotificationName, propertyNamesToAssert, expectedPropVals7);
        SmartsTestUtils.assertAuditTrail(newNotificationName, "ReleasingOwner", "RELEASE_OWNERSHIP", "Releasing ownership of the notification"); // update does not update audit text

        // CLEAR NOTIFICATION
        String[] clearPropertyNames = {"CreationClassName", SmartsConstants.PARAM_INSTANCENAME, "EventDisplayName", "Severity", "Active"};
        expActive = new MR_AnyValBoolean(false);
        MR_AnyVal[] clearExpectedPropVals = {expCreationClassName, expInstName, expEventDisplayName, expSeverity, expActive};

        NotificationClearParams clearParameters = SmartsTestUtils.getTestParametersForClear();

        boolean notificationCleared = notificationAdapter.clearNotification(createParameters.getIdentifierParameters(), clearParameters);
        assertTrue("Notification Not Cleared", notificationCleared);
        SmartsTestUtils.assertPropertyValues(newNotificationName, clearPropertyNames, clearExpectedPropVals);
        SmartsTestUtils.assertAuditTrail(newNotificationName, "Tugrul", "CLEAR", "clearing the notification");
    }

    public void testCreateAndClearNotificationForNonExistingSwitchWithUnknownAgentIgnore() throws Exception {
        // CREATE NOTIFICATION
        SmartsTestUtils.deleteAllTopologyInstances("Switch", ".*");
        NotificationCreateParams createParameters = SmartsTestUtils.getTestParametersForCreate();
        NotificationIdentifierParams identifierParameters = createParameters.getIdentifierParameters();
        identifierParameters.setInstanceName("ABCDEFG");
        createParameters.setUnknownAgent(SmartsConstants.PARAM_IGNORE);

        String notificationName = notificationAdapter.createNotification(createParameters);

        String newNotificationName = "NOTIFICATION-Switch_ABCDEFG_Down";
        assertEquals(newNotificationName, notificationName);

        assertFalse("Switch ABCDEFG should not be created", notificationAdapter.instanceExists(identifierParameters.getClassName(), identifierParameters.getInstanceName()));
        assertTrue("Notification should have been created", notificationAdapter.instanceExists(SmartsConstants.NOTIFICATION_CLASS, newNotificationName));

        // CLEAR NOTIFICATION
        NotificationClearParams clearParameters = SmartsTestUtils.getTestParametersForClear();

        boolean notificationCleared = notificationAdapter.clearNotification(identifierParameters, clearParameters);
        assertTrue("Notification Not Cleared", notificationCleared);
    }

    public void testCreateAndClearNotificationForNonExistingSwitchWithUnknownAgentCreate() throws Exception {
        // SET EXPECTED VALUES
        String newNotificationName = "NOTIFICATION-Switch_ABCDEFG_Down";
        String[] propertyNames = {"Description", "CreationClassName", SmartsConstants.PARAM_INSTANCENAME, "EventDisplayName", "Severity", "Active", "OccurredOn"};
        MR_AnyValString desc = new MR_AnyValString("TUGRUL");
        MR_AnyValString creationClassName = new MR_AnyValString("ICS_Notification");
        MR_AnyValString instName = new MR_AnyValString("ABCDEFG");
        MR_AnyVal occurredOn = new MR_AnyValObjRef(new MR_Ref("Switch", "ABCDEFG"));
        MR_AnyValString eventDisplayName = new MR_AnyValString("Down");
        MR_AnyValUnsignedInt severity = new MR_AnyValUnsignedInt(2);
        MR_AnyValBoolean active = new MR_AnyValBoolean(true);
        MR_AnyVal[] expectedPropVals = {desc, creationClassName, instName, eventDisplayName, severity, active, occurredOn};

        // CREATE NOTIFICATION
        NotificationCreateParams createParameters = SmartsTestUtils.getTestParametersForCreate();
        NotificationIdentifierParams identifierParameters = createParameters.getIdentifierParameters();

        identifierParameters.setInstanceName("ABCDEFG");
        assertCreateNotification(createParameters, newNotificationName, 1);
        SmartsTestUtils.assertPropertyValues(newNotificationName, propertyNames, expectedPropVals);

        // CLEAR NOTIFICATION
        NotificationClearParams clearParameters = SmartsTestUtils.getTestParametersForClear();

        boolean notificationCleared = notificationAdapter.clearNotification(identifierParameters, clearParameters);
        assertTrue("Notification Not Cleared", notificationCleared);

        // delete new Switch instance
        notificationAdapter.deleteInstance(identifierParameters.getClassName(), identifierParameters.getInstanceName());

    }

    public void testCreateAndClearNotificationForNonExistingSwitchWithUnknownAgentCreateButCreateFails() throws Exception {
        // SET EXPECTED VALUES
        String newNotificationName = "NOTIFICATION-InvalidClazz_ABCDEFG_Down";
        String[] propertyNames = {"Description", "CreationClassName", SmartsConstants.PARAM_INSTANCENAME, "EventDisplayName", "Severity", "Active", "OccurredOn"};
        MR_AnyValString desc = new MR_AnyValString("TUGRUL");
        MR_AnyValString creationClassName = new MR_AnyValString("ICS_Notification");
        MR_AnyValString instName = new MR_AnyValString("ABCDEFG");
        MR_AnyVal occurredOn = new MR_AnyValObjRef(new MR_Ref("", ""));
        MR_AnyValString eventDisplayName = new MR_AnyValString("Down");
        MR_AnyValUnsignedInt severity = new MR_AnyValUnsignedInt(2);
        MR_AnyValBoolean active = new MR_AnyValBoolean(true);
        MR_AnyVal[] expectedPropVals = {desc, creationClassName, instName, eventDisplayName, severity, active, occurredOn};

        // CREATE NOTIFICATION
        NotificationCreateParams createParameters = SmartsTestUtils.getTestParametersForCreate();
        NotificationIdentifierParams identifierParameters = createParameters.getIdentifierParameters();
        identifierParameters.setClassName("InvalidClazz");
        identifierParameters.setInstanceName("ABCDEFG");
        assertFalse("Switch ABCDEFG should not be created beacuse the ClassName was invalid", notificationAdapter.instanceExists(identifierParameters.getClassName(), identifierParameters.getInstanceName()));
        assertCreateNotification(createParameters, newNotificationName, 1);
        SmartsTestUtils.assertPropertyValues(newNotificationName, propertyNames, expectedPropVals);

        // CLEAR NOTIFICATION
        NotificationClearParams clearParameters = SmartsTestUtils.getTestParametersForClear();

        boolean notificationCleared = notificationAdapter.clearNotification(identifierParameters, clearParameters);
        assertTrue("Notification Not Cleared", notificationCleared);

    }

    public void testAggregateNotification() throws Exception {
        try {
            SmartsTestUtils.archiveAllNotifications();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not delete notification instances");
        }

        try {
            // CREATE NEW INSTANCES IN REPOSITORY
            // We are not crefating an instance for Host1-CPU1, in order to test the scenario
            // where a notification is created for a non-existing class/instance.
            // domainManager.createInstance("CPU", "Host1-CPU1");
            notificationAdapter.createInstance("Memory", "Host1-Memory1");
            notificationAdapter.createInstance("Host", "Host1");

            // SET EXPECTED VALUES
            String newNotificationName0 = "NOTIFICATION-CPU_Host1-CPU0_HighUtilization";
            String newNotificationName1 = "NOTIFICATION-CPU_Host1-CPU1_HighUtilization";
            String newNotificationName2 = "NOTIFICATION-Memory_Host1-Memory1_BufferOverload";
            String aggregateNotification = "NOTIFICATION-Host_Host1_Degraded";

            String[] activePropName = {"Active"};
            MR_AnyVal[] activePropValue = {new MR_AnyValBoolean(true)};
            MR_AnyVal[] inactivePropValue = {new MR_AnyValBoolean(false)};

            // SCENARIO 1: CREATE NOTIFICATION WITH MOCK makeAggregate THAT THROWS AN EXCEPTION
            notificationAdapter = new BaseNotificationAdapter(SmartsTestUtils.SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log) {
                protected String makeAggregateNotification(NotificationAggregateParams aggregateParameters, MR_AnyVal notification) {
                    return null;
                }
            };

            NotificationCreateParams createParameters = SmartsTestUtils.getTestParametersForCreate();
            NotificationIdentifierParams identifierParameters = createParameters.getIdentifierParameters();
            NotificationAggregateParams aggregateParameters = new NotificationAggregateParams("Host", "Host1", "Degraded");
            identifierParameters.setClassName("CPU");
            identifierParameters.setInstanceName("Host1-CPU0");
            identifierParameters.setEventName("HighUtilization");
            createParameters.setAggregateParameters(aggregateParameters);
            assertCreateNotification(createParameters, newNotificationName0, 1);
            SmartsTestUtils.assertPropertyValues(newNotificationName0, activePropName, activePropValue);
            notificationAdapter = SmartsTestUtils.getNotificationAdapter();

            // Clear the notification (Host1-CPU0)
            NotificationClearParams clearParameters = SmartsTestUtils.getTestParametersForClear();

            boolean notificationCleared = notificationAdapter.clearNotification(identifierParameters, clearParameters);
            assertTrue("Notification Not Cleared", notificationCleared);

            // SCENARIO 2: CREATE NOTIFICATION WITH AGGREGATE
            identifierParameters.setInstanceName("Host1-CPU1");
            assertCreateNotification(createParameters, newNotificationName1, 2);

            SmartsTestUtils.assertPropertyValues(newNotificationName1, activePropName, activePropValue);
            SmartsTestUtils.assertPropertyValues(aggregateNotification, activePropName, activePropValue);

            // Create second notification with same aggregate
            identifierParameters.setClassName("Memory");
            identifierParameters.setInstanceName("Host1-Memory1");
            identifierParameters.setEventName("BufferOverload");
            assertCreateNotification(createParameters, newNotificationName2, 1);

            SmartsTestUtils.assertPropertyValues(newNotificationName2, activePropName, activePropValue);
            SmartsTestUtils.assertPropertyValues(aggregateNotification, activePropName, activePropValue);

            // Clear second notification (ercaswroc)
            notificationCleared = notificationAdapter.clearNotification(identifierParameters, clearParameters);
            assertTrue("Notification Not Cleared", notificationCleared);
            SmartsTestUtils.assertPropertyValues(newNotificationName2, activePropName, inactivePropValue);
            SmartsTestUtils.assertPropertyValues(aggregateNotification, activePropName, activePropValue);

            // Clear first notification (ercaswnyc2)
            identifierParameters.setClassName("CPU");
            identifierParameters.setInstanceName("Host1-CPU1");
            identifierParameters.setEventName("HighUtilization");

            notificationCleared = notificationAdapter.clearNotification(identifierParameters, clearParameters);
            assertTrue("Notification Not Cleared", notificationCleared);
            SmartsTestUtils.assertPropertyValues(newNotificationName1, activePropName, inactivePropValue);
            SmartsTestUtils.assertPropertyValues(aggregateNotification, activePropName, inactivePropValue);
        }
        finally {
            // Delete instances from repository
            try {
                notificationAdapter.deleteInstance("Memory", "Host1-Memory1");
                notificationAdapter.deleteInstance("Host", "Host1");
            } catch (SmRemoteException e) {
            }
        }
    }

    public void testGetNotificationAttributesWhenNotificationIsNotFound() throws Exception {
        String newNotificationName = "NOTIFICATION-Switch_ercaswnyc2_Down";
        SmartsTestUtils.archiveNotification(newNotificationName);

        MR_PropertyNameValue[] attributes = SmartsPropertyHelper.getNotificationAttributes(notificationAdapter, "Switch", "ercaswnyc2", "Down");
        assertNull("When notification is not found, return value should be null", attributes);
    }

    public void testGetNotificationAttributesWhenNotificationIsFound() throws Exception {

        NotificationCreateParams createParameters = SmartsTestUtils.getTestParametersForCreate();
        String notificationName = notificationAdapter.createNotification(createParameters);
        assertNotNull("Notification was not created", notificationName);

        MR_PropertyNameValue[] attributes = SmartsPropertyHelper.getNotificationAttributes(notificationAdapter, "Switch", "ercaswnyc2", "Down");

        // Total number of attributes is 71 but we are filtering
//        assertEquals(46, attributes.length);
        assertEquals("NOTIFICATION-Switch_ercaswnyc2_Down", SmartsTestUtils.getValueForName(attributes, "Name").toString());
        assertEquals("TUGRUL", ((MR_AnyValString) SmartsTestUtils.getValueForName(attributes, "Description")).getStringValue());
    }

    public void testGetNotificationUsingMapListParams() throws Exception {
        String className = "Switch";
        String instanceName = "eraaswiad";
        String eventName = "Down";

        Map atts = new HashMap();
        atts.put("Severity", "3");
        atts.put("EventText", "eventText");


        notificationAdapter.createNotification(className, instanceName, eventName, atts);

        List requestedAtts = new ArrayList();
        requestedAtts.add("Severity");
        requestedAtts.add("EventText");

        Map idsMap = new HashMap();
        idsMap.put("ClassName", className);
        idsMap.put("InstanceName", instanceName);
        idsMap.put("EventName", eventName);

        Map<String, Object> rec = notificationAdapter.getObject(idsMap, requestedAtts);
        assertEquals(3l, rec.get("Severity"));
        assertEquals("eventText", rec.get("EventText"));

        idsMap.remove("ClassName");
        rec = notificationAdapter.getObject(idsMap, requestedAtts);
        assertNull(rec);

        idsMap.put("ClassName", className);
        idsMap.remove("InstanceName");
        rec = notificationAdapter.getObject(idsMap, requestedAtts);
        assertNull(rec);

        idsMap.put("InstanceName", instanceName);
        idsMap.remove("EventName");
        rec = notificationAdapter.getObject(idsMap, requestedAtts);
        assertNull(rec);

    }

    private void assertCreateNotification(NotificationCreateParams createParameters,
                                          String newNotificationName, int countIncrement) throws Exception {
        SmartsTestUtils.archiveNotification(newNotificationName);

        String[] notificationInstances = notificationAdapter.getInstances(SmartsConstants.NOTIFICATION_CLASS);
        int nonSessionNotificationCountBefore = 0;
        for (int i = 0; i < notificationInstances.length; i++) {
            if (notificationInstances[i].indexOf("Session_SESSION-APP-InChargeService") < 0) {
                nonSessionNotificationCountBefore++;
            }
        }

        String notificationName = notificationAdapter.createNotification(createParameters);
        assertNotNull("Notification was not created", notificationName);

        notificationInstances = notificationAdapter.getInstances(SmartsConstants.NOTIFICATION_CLASS);
        int nonSessionNotificationCountAfter = 0;
        for (int i = 0; i < notificationInstances.length; i++) {
            if (notificationInstances[i].indexOf("Session_SESSION-APP-InChargeService") < 0) {
                nonSessionNotificationCountAfter++;
            }
        }
        assertEquals(nonSessionNotificationCountBefore + countIncrement, nonSessionNotificationCountAfter);
        assertEquals(newNotificationName, notificationName);
    }

    private void assertExpectedListCount(List<String> list, int expectedCount) throws InterruptedException {
        final int count = expectedCount;
        final List<String> finalList = list;
        CommonTestUtils.waitFor(new WaitAction() {
            @Override
            public void check() throws Exception {
                assertEquals(finalList.size(), count);
            }

        }, 900);
    }
}
