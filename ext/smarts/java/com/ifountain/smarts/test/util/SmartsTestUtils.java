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
import com.ifountain.smarts.util.params.*;
import com.smarts.repos.*;
import junit.framework.Assert;

import java.io.IOException;
import java.util.*;

public class SmartsTestUtils {
    private static String[] sessionNotifications = {
        "NOTIFICATION-Session_SESSION-APP-InChargeService__CNCC-SA->APP-InChargeService__CNCC-ATM_Disconnected",
        "NOTIFICATION-Session_SESSION-APP-InChargeService__CNCC-SA->APP-InChargeService__CNCC-XD-AI_Disconnected",
        "NOTIFICATION-Session_SESSION-APP-InChargeService__CNCC-SA->APP-InChargeService__CNCC-ASM_Disconnected",
        "NOTIFICATION-Session_SESSION-APP-InChargeService__CNCC-SA->APP-InChargeService__WestRegion_Disconnected",
        "NOTIFICATION-Session_SESSION-APP-InChargeService__CNCC-OI->APP-InChargeService__WestRegion_Disconnected"};
    public static final String SMARTS_TEST_CONNECTION_NAME = "SmartsTestConnection";
    private static BaseNotificationAdapter notificationAdapter;
    private static BaseTopologyAdapter topologyAdapter;
	public static SmartsConnectionParams getSmartsConnectionParams(int domainType){
        switch (domainType)
        {
            case SmartsTestConstants.SMARTS_SAM_CONNECTION_TYPE:
                return new SmartsConnectionParams(
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_SAM_BROKER),
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_SAM_DOMAIN),
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_SAM_USER),
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_SAM_PASSWORD)
                );
            case SmartsTestConstants.SMARTS_AM_CONNECTION_TYPE:
                return new SmartsConnectionParams(
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_AM_BROKER),
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_AM_DOMAIN),
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_AM_USER),
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_AM_PASSWORD)
                );
            case SmartsTestConstants.SMARTS_SECURE_AM_CONNECTION_TYPE:
            {
                SmartsConnectionParams  conParams = new SmartsConnectionParams(
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_AM_BROKER),
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_AM_DOMAIN),
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_AM_USER),
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_AM_PASSWORD)
                );
                conParams.setBrokerUsername(SmartsTestConstants.SMARTS_SECURE_AM_BROKER_USERNAME);
                conParams.setBrokerPassword(SmartsTestConstants.SMARTS_SECURE_AM_BROKER_PASSWORD);
                return conParams;
            }
            case SmartsTestConstants.SMARTS_SECURE_SAM_CONNECTION_TYPE:
            {
                SmartsConnectionParams  conParams = new SmartsConnectionParams(
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_SECURE_SAM_BROKER),
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_SECURE_SAM_DOMAIN),
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_SECURE_SAM_USER),
                        CommonTestUtils.getTestProperty(SmartsTestConstants.SMARTS_SECURE_SAM_PASSWORD)

                );
                conParams.setBrokerUsername(SmartsTestConstants.SMARTS_SECURE_SAM_BROKER_USERNAME);
                conParams.setBrokerPassword(SmartsTestConstants.SMARTS_SECURE_SAM_BROKER_PASSWORD);
                return conParams;
            }
            default:
                return null;
        }
    }


     public static ConnectionParam getConnectionParam(int domainType){
        SmartsConnectionParams connectionParams = getSmartsConnectionParams(domainType);
        Map<String, Object> otherParams = new HashMap<String, Object>();
        otherParams.put(SmartsConnectionImpl.BROKER, connectionParams.getBroker());
        otherParams.put(SmartsConnectionImpl.DOMAIN, connectionParams.getDomain());
        otherParams.put(SmartsConnectionImpl.USERNAME, connectionParams.getUsername());
        otherParams.put(SmartsConnectionImpl.PASSWORD, connectionParams.getPassword());
         otherParams.put(SmartsConnectionImpl.BROKER_USERNAME, connectionParams.getBrokerUsername());
         otherParams.put(SmartsConnectionImpl.BROKER_PASSWORD, connectionParams.getBrokerPassword());
        return new ConnectionParam("SmartsConnection", SMARTS_TEST_CONNECTION_NAME, SmartsConnectionImpl.class.getName(), otherParams, 10);
    }
	 
	public static BaseNotificationAdapter getNotificationAdapter(){
	    if(notificationAdapter == null){
	        notificationAdapter = new BaseNotificationAdapter(SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log);
	    }
	    return notificationAdapter;
	}
	public static BaseTopologyAdapter getTopologyAdapter(){
	    if(topologyAdapter == null){
	        topologyAdapter = new BaseTopologyAdapter(SMARTS_TEST_CONNECTION_NAME, 0, TestLogUtils.log);
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
        getNotificationAdapter().takeOwnership(className, instanceName, eventName, user, auditTrailText);
    }

    public static void releaseOwnershipNotification(String className, String instanceName, String eventName, String user, String auditTrailText) throws Exception
    {
        getNotificationAdapter().releaseOwnership(className, instanceName, eventName, user, auditTrailText);
    }
    
    public static void createNotification(String className, String instanceName, String eventName, Map<String, Object> attributes) throws Exception
    {
        Map smartsAttributes = convertStringAttributesToSmartsTypeAttributes(attributes);
        NotificationCreateParams createParams = getTestParametersForCreate(className, instanceName, eventName, smartsAttributes);
        getNotificationAdapter().createNotification(createParams);
    }

    public static boolean clearNotification(String className, String instanceName, String eventName) throws Exception
    {
        NotificationClearParams params = getTestParametersForClear();
        return getNotificationAdapter().clearNotification(className, instanceName, eventName, params.getSource(), params.getUser(), params.getAuditTrailText());
    }
	
    public static void archiveAllNotifications() throws Exception
    {
        String[] instances = getNotificationAdapter().getInstances(SmartsConstants.NOTIFICATION_CLASS);
        ArrayList<String> notificationsInAllList = SmartsHelper.getExistingNotificationsOfAList(getNotificationAdapter(), "ICS_NL-ALL_NOTIFICATIONS");
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
        attributes.put("SourceDomainName", new MR_AnyValString("EastRegion"));        
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
        topologyAdapter.addRelationshipBetweenTopologyObjects(classname1, instancename1, classname2, instancename2, relationship);
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

    public static Map convertStringAttributesToSmartsTypeAttributes(Map stringAttributes) throws Exception{
        Map smartsAttributes = new HashMap();
        Iterator iterator = stringAttributes.keySet().iterator();
        while(iterator.hasNext()){
            String propertyName = (String)iterator.next();
            String propertyValue = (String)stringAttributes.get(propertyName);
            MR_AnyVal smartsPropValue = SmartsPropertyHelper.getPropertyValue(getNotificationAdapter(), "ICS_Notification", propertyName, propertyValue);
            smartsAttributes.put(propertyName, smartsPropValue);
        }
        return smartsAttributes;
    }
}
