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
package com.ifountain.smarts.test.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import com.ifountain.comp.test.util.CommonTestUtils;
import com.ifountain.comp.test.util.logging.TestLogUtils;
import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.smarts.connection.SmartsConnectionImpl;
import com.ifountain.smarts.datasource.BaseNotificationAdapter;
import com.ifountain.smarts.datasource.BaseSmartsAdapter;
import com.ifountain.smarts.datasource.BaseTopologyAdapter;
import com.ifountain.smarts.util.SmartsConstants;
import com.ifountain.smarts.util.SmartsHelper;
import com.ifountain.smarts.util.SmartsPropertyHelper;
import com.ifountain.smarts.util.params.NotificationAcknowledgeParams;
import com.ifountain.smarts.util.params.NotificationAggregateParams;
import com.ifountain.smarts.util.params.NotificationAuditParams;
import com.ifountain.smarts.util.params.NotificationClearParams;
import com.ifountain.smarts.util.params.NotificationCreateParams;
import com.ifountain.smarts.util.params.NotificationIdentifierParams;
import com.ifountain.smarts.util.params.NotificationNotifyParams;
import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValArray;
import com.smarts.repos.MR_AnyValArraySet;
import com.smarts.repos.MR_AnyValString;
import com.smarts.repos.MR_AnyValUnsignedInt;
import com.smarts.repos.MR_Choice;
import com.smarts.repos.MR_PropertyNameValue;
import com.smarts.repos.MR_Ref;

public class SmartsTestUtils {
    private static String[] sessionNotifications = {
        "NOTIFICATION-Session_SESSION-APP-InChargeService__CNCC-SA->APP-InChargeService__CNCC-ATM_Disconnected",
        "NOTIFICATION-Session_SESSION-APP-InChargeService__CNCC-SA->APP-InChargeService__CNCC-XD-AI_Disconnected",
        "NOTIFICATION-Session_SESSION-APP-InChargeService__CNCC-SA->APP-InChargeService__CNCC-ASM_Disconnected",
        "NOTIFICATION-Session_SESSION-APP-InChargeService__CNCC-SA->APP-InChargeService__WestRegion_Disconnected",
        "NOTIFICATION-Session_SESSION-APP-InChargeService__CNCC-OI->APP-InChargeService__WestRegion_Disconnected"};
    public static final String SMARTS_TEST_DATASOURCE_NAME = "SmartsTestDatasource";
    private static BaseNotificationAdapter notificationAdapter;
    private static BaseTopologyAdapter topologyAdapter;
	public static SmartsConnectionParams getConnectionParams(){
		return new SmartsConnectionParams(
				CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_BROKER),
				CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_DOMAIN),
				CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_USER),
				CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_PASSWORD)
		);
	}
	
	 public static ConnectionParam getDatasourceParam(){
        SmartsConnectionParams connectionParams = getConnectionParams();
        Map<String, Object> otherParams = new HashMap<String, Object>();
        otherParams.put(SmartsConnectionImpl.BROKER, connectionParams.getBroker());
        otherParams.put(SmartsConnectionImpl.DOMAIN, connectionParams.getDomain());
        otherParams.put(SmartsConnectionImpl.USERNAME, connectionParams.getUsername());
        otherParams.put(SmartsConnectionImpl.PASSWORD, connectionParams.getPassword());
        return new ConnectionParam("SmartsConnection", SMARTS_TEST_DATASOURCE_NAME, SmartsConnectionImpl.class.getName(), otherParams, 10);
    }
	 
	public static BaseNotificationAdapter getNotificationAdapter(){
	    if(notificationAdapter == null){
	        notificationAdapter = new BaseNotificationAdapter(SMARTS_TEST_DATASOURCE_NAME, 0, TestLogUtils.log);
	    }
	    return notificationAdapter;
	}
	public static BaseTopologyAdapter getTopologyAdapter(){
	    if(topologyAdapter == null){
	        topologyAdapter = new BaseTopologyAdapter(SMARTS_TEST_DATASOURCE_NAME, 0, TestLogUtils.log);
	    }
	    return topologyAdapter;
	}
	
	public static void archiveNotification(String className, String instanceName, String eventName) throws Exception{
	    getNotificationAdapter().archiveNotification(className, instanceName, eventName, "testuser", "");
	    assertNotificationDeletion(className, instanceName, eventName);
	}
	public static void archiveNotification(String notificationName) throws Exception{
	    getNotificationAdapter().archiveNotification(notificationName, "testuser", "");
	    assertNotificationDeletion(notificationName);
	}
	
	public static void acknowledgeNotification(String className, String instanceName, String eventName) throws Exception
	{
	    NotificationAcknowledgeParams params = getTestParametersForAcknowledge();
	    getNotificationAdapter().acknowledge(className, instanceName, eventName, params.getUser(), params.getAuditTrailText());
    }
    
    public static void unAcknowledgeNotification(String className, String instanceName, String eventName) throws Exception
    {
        NotificationAcknowledgeParams params = getTestParametersForAcknowledge();
        getNotificationAdapter().unacknowledge(className, instanceName, eventName, params.getUser(), params.getAuditTrailText());
    }
    
    
    public static void updateNotification(String className, String instanceName, String eventName, Map<String, Object> attributes) throws Exception
    {
        getNotificationAdapter().updateNotification(className, instanceName, eventName, attributes);
    }
    
    public static void takeOwnershipNotification(String className, String instanceName, String eventName, String user, String auditTrailText) throws Exception
    {
        NotificationAcknowledgeParams params = getTestParametersForAcknowledge();
        getNotificationAdapter().takeOwnership(className, instanceName, eventName, params.getUser(), params.getAuditTrailText());
    }
    
    public static void createNotification(String className, String instanceName, String eventName, Map<String, Object> attributes) throws Exception
    {
        getNotificationAdapter().createNotification(className, instanceName, eventName, attributes);
    }

    public static void clearNotification(String className, String instanceName, String eventName) throws Exception
    {
        NotificationClearParams params = getTestParametersForClear();
        getNotificationAdapter().clearNotification(className, instanceName, eventName, params.getSource(), params.getUser(), params.getAuditTrailText());
    }
	
    public static void archiveAllNotifications() throws Exception
    {
        String[] instances = notificationAdapter.getInstances(SmartsConstants.NOTIFICATION_CLASS);
        ArrayList<String> notificationsInAllList = SmartsHelper.getExistingNotificationsOfAList(notificationAdapter, "ICS_NL-ALL_NOTIFICATIONS");
        int numberOfDeletedObjectsInInstances = 0;
        int numberOfDeletedObjectsInNotificationList = 0;
        for (int i = 0; i < notificationsInAllList.size(); i++){
            // do not delete default session notifications
            if(!Arrays.asList(sessionNotifications).contains(notificationsInAllList.get(i))){
                archiveNotification(notificationsInAllList.get(i));
                numberOfDeletedObjectsInNotificationList++;
            }
        }
        for (int i = 0; i < instances.length; i++) {
            // do not delete default session notifications
            if(!Arrays.asList(sessionNotifications).contains(instances[i])){
                archiveNotification(instances[i]);
                numberOfDeletedObjectsInInstances++;
            }
        }
        if(numberOfDeletedObjectsInInstances == 0 &&  numberOfDeletedObjectsInNotificationList == 0)
        {
            return;
        }
        archiveAllNotifications(); // keep trying until all gone
    }
    
    public static void deleteTopologyInstancesWithPrefixes(String className, String instanceNamePrefix, int startingNumber, int numberOfObjects) throws Exception
    {
        for (int i = 0; i < numberOfObjects; i++)
        {
            try
            {
                getTopologyAdapter().deleteInstance(className, instanceNamePrefix + (startingNumber + i));
            }
            catch (Exception e)
            {
            }
        }
    }
    
    public static void deleteTopologyInstance(String className, String instanceName) throws Exception
    {
        try
        {
            getTopologyAdapter().deleteInstance(className, instanceName);
        }
        catch (Exception e)
        {
        }
    }
    
    public static void deleteAllTopologyInstances(String className, String instanceNameRegularExpression) throws Exception
    {
        deleteAllTopologyInstances(className, instanceNameRegularExpression, getTopologyAdapter());
    }
    
    public static void deleteAllTopologyInstances(String className, String instanceNameRegularExpression, BaseSmartsAdapter smartsAdapter) throws Exception
    {
        MR_Ref [] instances = smartsAdapter.findInstances(className, instanceNameRegularExpression, MR_Choice.NONE);
        for (int i = 0; i < instances.length; i++)
        {
            try
            {
                smartsAdapter.deleteInstance(className, instances[i].getInstanceName());
            }
            catch (Exception e)
            {
            }
        }
        
        String[] children = smartsAdapter.getChildren(className);
        if(children == null || children.length == 0)
        {
            return;
        }
        else
        {
            for (int i = 0 ; i < children.length ; i++)
            {
                deleteAllTopologyInstances(children[i], instanceNameRegularExpression, smartsAdapter);
            }
        }
    }
	
    public static NotificationCreateParams getTestParametersForCreate(String className, String instanceName, String eventName, Map<String, MR_AnyVal> attributes)
    {
        NotificationIdentifierParams identifierParams = new NotificationIdentifierParams(className, instanceName, eventName);
        NotificationNotifyParams notifyParameters = getTestParametersForNotify();
        NotificationAggregateParams aggregateParameters = null;
        String unknownAgent = SmartsConstants.PARAM_CREATE;
        return new NotificationCreateParams(identifierParams, notifyParameters, attributes, aggregateParameters, unknownAgent);
    }

    public static NotificationCreateParams getTestParametersForCreate()
    {
        return getTestParametersForCreate("Switch", "ercaswnyc2", "Down", getTestAttributes());
    }
    
    public static NotificationClearParams getTestParametersForClear()
    {
        return new NotificationClearParams("Tugrul", "EastRegion", "clearing the notification", System.currentTimeMillis()/1000);
    }

    public static NotificationAcknowledgeParams getTestParametersForAcknowledge()
    {
        return new NotificationAcknowledgeParams("AckUser", "acknowledging the notification");
    }
    
    public static NotificationAcknowledgeParams getTestParametersForUnAcknowledge()
    {
        return new NotificationAcknowledgeParams("AckUser", "un-acknowledging the notification");
    }

    public static NotificationAuditParams getTestParametersForAudit()
    {
        return new NotificationAuditParams("auditUser", "text for adding audit entry", "ACTION");
    }

    public static NotificationIdentifierParams getTestParametersForIdentifier()
    {
        return new NotificationIdentifierParams("Switch", "ercaswnyc2", "Down");
    }

    public static Map<String, MR_AnyVal> getTestAttributes()
    {
        Map<String, MR_AnyVal> attributes = new HashMap<String, MR_AnyVal>();
        attributes.put("Description", new MR_AnyValString("TUGRUL"));
        attributes.put("Severity", new MR_AnyValUnsignedInt(2));
        attributes.put("EventType", new MR_AnyValString("MOMENTARY"));
        return attributes;
    }
    
    public static NotificationNotifyParams getTestParametersForNotify()
    {
        return new NotificationNotifyParams("TestUser", "EastRegion", "auditTrailText", "", System.currentTimeMillis()/1000, 0, 1);
    }
    
    public static MR_AnyVal getValueForName(MR_PropertyNameValue[] nameValuePairs, String name)
    {
        for (int i = 0; i < nameValuePairs.length; i++)
        {
            if (nameValuePairs[i].getPropertyName().equalsIgnoreCase(name))
            {
                return nameValuePairs[i].getPropertyValue();
            }
        }
        return null;
    }
    public static void assertPropertyValues(String notificationName, String[] propertyNames, MR_AnyVal[] expectedPropVals) throws Exception {
        MR_AnyVal[] propValues = getNotificationAdapter().getProperties(SmartsConstants.NOTIFICATION_CLASS, notificationName, propertyNames);
        boolean expectedPropsFound = Arrays.equals(propValues, expectedPropVals);

        if (!expectedPropsFound) {
            for(int i = 0 ; i < propValues.length; i++)
            {
                if(!propValues[i].equals(expectedPropVals[i]))
                {
                    System.out.println("Expected " + expectedPropVals[i] + " for Property [" + propertyNames[i] + " ] but got " + propValues[i]);
                }
            }
        }
        Assert.assertTrue("Some of the expected values not found", expectedPropsFound);
    }
    
    public static boolean waitForNotificationAttributeUpdate(NotificationIdentifierParams identifierParameters, String attributeName, String expectedAttributeValue) throws Exception {
        boolean updated = false;
        for(int i = 0 ; i < 200;i++)
        {
            MR_PropertyNameValue[] nameValue = SmartsPropertyHelper.getNotificationAttributes(getNotificationAdapter(), identifierParameters.getClassName(),
                    identifierParameters.getInstanceName(), identifierParameters.getEventName());
            if(nameValue == null)
                continue;
            MR_AnyVal propertyValue = SmartsPropertyHelper.getPropertyValueFromList(nameValue, attributeName);
            updated = propertyValue.getValue().toString().equalsIgnoreCase(expectedAttributeValue);
            if(updated)
            {
                break;
            }
            else
            {
                CommonTestUtils.wait(50);
            }
        }
        return updated;
    }

    public static void assertAuditTrail(String newNotificationName, String user, String action, String auditTrailText) throws Exception {
        MR_AnyVal[] lastAuditTrailTextArray = getLatestAuditTrailTextArray(newNotificationName);
        Assert.assertEquals(user, lastAuditTrailTextArray[2].toString());                         //user
        Assert.assertEquals(action, lastAuditTrailTextArray[3].toString());                     //action
        Assert.assertEquals(auditTrailText, lastAuditTrailTextArray[4].toString());  //auditTrailText
    }
    
    public static void createTopologyInstanceWithProperties(String className, String instanceName, Map<String, String> properties) throws Exception
    {
        if (!getTopologyAdapter().instanceExists(className, instanceName))
        {
            getTopologyAdapter().createInstance(className, instanceName);
        }
        updateTopologyInstanceWithProperties(className, instanceName, properties);
    }
    public static void createTopologyInstance(String className, String instanceName) throws Exception {
        createTopologyInstanceWithProperties(className, instanceName,new HashMap<String, String>());
    }
    public static void createTopologyInstancesWithPrefixes(String className, String instanceNamePrefix, Map<String, String> properties, int startingNumber, int numberOfObjects) throws Exception
    {
        for (int i = 0; i < numberOfObjects; i++)
        {
            createTopologyInstanceWithProperties(className, instanceNamePrefix + (startingNumber + i), properties);
        }
    }
    public static void updateTopologyInstanceWithProperties(String className, String instanceName, Map<String, String> properties) throws IOException
    {
        getTopologyAdapter().updateTopologyInstanceWithProperties(className, instanceName, properties);
    }
    
    public static void addRelationship(String classname1, String instancename1, String classname2, String instancename2, String relationship) throws Exception
    {
        addRelationship(getTopologyAdapter(), classname1, instancename1, classname2, instancename2, relationship);
        
    }
    
    public static void addRelationship(BaseTopologyAdapter topologyAdapter, String classname1, String instancename1, String classname2, String instancename2, String relationship) throws Exception
    {
        topologyAdapter.addRelationShipBetweenTopologyObjects(classname1, instancename1, classname2, instancename2, relationship);
    }
    
    private static MR_AnyVal[] getLatestAuditTrailTextArray(String notificationName) throws Exception {
        String[] propertyNames = {"AuditTrail"};
        MR_AnyVal[] propValues = getNotificationAdapter().getProperties(SmartsConstants.NOTIFICATION_CLASS, notificationName, propertyNames);
        MR_AnyValArraySet auditTrail = (MR_AnyValArraySet) propValues[0];
        MR_AnyValArray[] auditTrailArray = auditTrail.getArraySetValue();
        MR_AnyVal[] auditTrailTexts = auditTrailArray[0].getArrayValue();
        return auditTrailTexts;
    }
    
	private static void assertNotificationDeletion(String className, String instanceName, String eventName){
        MR_AnyVal notification = null;
        for (int i = 0; i < 300; i++) {
            try {
                notification = getNotificationAdapter().findNotification(className, instanceName, eventName);
            } catch (Exception e) {
            }
            if(notification!= null) {
                CommonTestUtils.wait(40);
            }
            else break;
        }
        if (notification!= null) Assert.fail("Could not archive notification instance in time");
    }
	private static void assertNotificationDeletion(String notificationName){
        MR_AnyVal notification = null;
        for (int i = 0; i < 300; i++) {
            try {
                notification = getNotificationAdapter().getInstanceFromRepository(SmartsConstants.NOTIFICATION_CLASS, notificationName);
            } catch (Exception e) {
            }
            if(notification!= null) {
                CommonTestUtils.wait(40);
            }
            else break;
        }
        if (notification!= null) Assert.fail("Could not archive notification instance in time");
    }
}
