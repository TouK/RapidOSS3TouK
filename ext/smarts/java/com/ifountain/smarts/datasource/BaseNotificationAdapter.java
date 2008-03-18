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
 * Created on Feb 20, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.datasource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ifountain.smarts.datasource.queries.IQuery;
import com.ifountain.smarts.datasource.queries.QueryFactory;
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
import com.ifountain.smarts.util.params.NotificationParams;
import com.ifountain.smarts.util.params.NotificationUpdateParams;
import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValObjRef;
import com.smarts.repos.MR_AnyValString;
import com.smarts.repos.MR_AnyValUnsignedInt;
import com.smarts.repos.MR_Choice;
import com.smarts.repos.MR_Ref;

public class BaseNotificationAdapter extends BaseSmartsAdapter {
    public BaseNotificationAdapter()
	{
		super();
	}

	public BaseNotificationAdapter(String datasourceName, long reconnectInterval, Logger logger) {
        super(datasourceName, reconnectInterval, logger);
    }
    
    public String createNotification(String className, String instanceName, String eventName, Map<String, Object> optionalParams) throws Exception
    {
        Map<String, Object> clonedParams = new HashMap<String, Object>(optionalParams);
        NotificationIdentifierParams identifier = getIdentifierParameters(className, instanceName, eventName);
        NotificationNotifyParams notifyParameters = getNotifyParameters(clonedParams);
        NotificationAggregateParams aggParams = getAggregateParameters(clonedParams);
        MR_AnyValString unknownAgent=getMrStringValue(clonedParams,SmartsConstants.PARAM_UNKNOWNAGENT, "Ignore");
        
        Map<String, MR_AnyVal> optParams = new HashMap<String, MR_AnyVal>();
        List<String> keys = new ArrayList<String>(clonedParams.keySet());
        for (Iterator<String> iter = keys.iterator(); iter.hasNext();)
        {
            String propName = iter.next();
            optParams.put(propName, getValue(clonedParams, propName, null));
            
        }
        NotificationCreateParams create = new NotificationCreateParams(identifier, notifyParameters, optParams, aggParams, unknownAgent.getStringValue());
        
        return createNotification(create);
    }
    
    public String createNotification(NotificationCreateParams createParams) throws Exception {
        logger.info("Will create notification with the following parameters: "
                + createParams.getIdentifierParameters().getClassName() + ", "
                + createParams.getIdentifierParameters().getInstanceName() + ", "
                + createParams.getIdentifierParameters().getEventName()
        );
        MR_AnyVal[] args = createParams.getIdentifierParameters().getMethodArgs();
        MR_AnyVal notification = invokeOperation(SmartsConstants.NOTIFICATION_FACTORY_CLASS, SmartsConstants.NOTIFICATION_FACTORY_INSTANCE, SmartsConstants.MAKE_NOTIFICATION, args);
        String notificationInstanceName = SmartsHelper.parseNotificationName(notification);
        String invokeChangedOn = notificationInstanceName;

        if (notificationInstanceName == null) return null; // Notification was not created

        if(createParams.getAggregateParameters() != null){
            invokeChangedOn = makeAggregateNotification(createParams.getAggregateParameters(), notification);
        }

        if(invokeChangedOn==null){
            invokeChangedOn = notificationInstanceName; // if there was a problem making aggregate, reset back to notificationName
        }

        setAttributes(notificationInstanceName, createParams.getAttributeParameters());
        invokeNotify(notificationInstanceName, createParams.getNotifyParameters());
        MR_AnyVal occurredOn = findOccurredOn(createParams.getIdentifierParameters(), createParams.getUnknownAgent());
        if (occurredOn != null) { // skip occurredOn if class/instance does not exist or could not be created
            put(SmartsConstants.NOTIFICATION_CLASS, notificationInstanceName, "OccurredOn", occurredOn);
        }
        invokeChanged(invokeChangedOn);
        return notificationInstanceName;
    }
    
    public boolean archiveNotification(NotificationIdentifierParams identiferParams, NotificationAcknowledgeParams archiveParams) throws Exception{
       
        MR_AnyVal notification = findNotification(identiferParams);
        return archiveNotification(notification, archiveParams);
    }
    
    public boolean archiveNotification(String className, String instanceName, String eventName, String user, String auditTrailText) throws Exception
    {
        return archiveNotification(new NotificationIdentifierParams(className, instanceName, eventName), new NotificationAcknowledgeParams(user, auditTrailText));
    }
    
    public boolean archiveNotification(String notificationName, String user, String auditTrailText) throws Exception
    {
        MR_AnyVal notification = getInstanceFromRepository(SmartsConstants.NOTIFICATION_CLASS, notificationName);
        return archiveNotification(notification,new NotificationAcknowledgeParams(user, auditTrailText));
    }

    private boolean archiveNotification(MR_AnyVal notification, NotificationAcknowledgeParams archiveParams) throws Exception {
        if(notification == null) 
        {
            return true;
        }
        MR_AnyVal[] args = {notification, archiveParams.getUserAsMR(), archiveParams.getAuditTrailTextAsMR()};
        invokeOperation(SmartsConstants.NOTIFICATION_FACTORY_CLASS, SmartsConstants.NOTIFICATION_FACTORY_INSTANCE, SmartsConstants.ARCHIVE_NOTIFICATION, args);
        return true;
    }
    
    
    public void updateNotification(NotificationIdentifierParams identifierParams, NotificationUpdateParams updateParams) throws Exception{
        String notificationName = findNotificationName(identifierParams);
        if (notificationName == null) return; // Notification was not found, nothing is updated
        
        Map<String, MR_AnyVal> attributesToUpdate = updateParams.getAttributeParameters();
        Set<String> attributeNames = attributesToUpdate.keySet();
        for (Iterator<String> iterator = attributeNames.iterator(); iterator.hasNext();) {
             String attributeName =  iterator.next();
             MR_AnyVal attributeValue =  attributesToUpdate.get(attributeName);
             put(SmartsConstants.NOTIFICATION_CLASS, notificationName, attributeName, attributeValue);
        }
        invokeChanged(notificationName);
    }
    public void updateNotification(String className, String instanceName, String eventName, Map<String, Object> optionalParams) throws Exception
    {
        Map<String, Object> clonedParams = new HashMap<String, Object>(optionalParams);
        Map<String, MR_AnyVal> optParams = new HashMap<String, MR_AnyVal>();
        List<String> keys = new ArrayList<String>(clonedParams.keySet());
        for (Iterator<String> iter = keys.iterator(); iter.hasNext();)
        {
            String propName = iter.next();
            optParams.put(propName, getValue(clonedParams, propName, null));
        }
        NotificationUpdateParams updateParams = new NotificationUpdateParams(optParams);
        updateNotification(getIdentifierParameters(className, instanceName, eventName), updateParams);
    }
    
    public boolean clearNotification(NotificationIdentifierParams identifierParams,NotificationClearParams clearParameters) throws Exception {
        return invokeUpdateOnNotification(identifierParams, SmartsConstants.CLEAR, clearParameters);
    }
    
    public boolean acknowledge(NotificationIdentifierParams identifierParams, NotificationAcknowledgeParams acknowledgeParams) throws Exception{
        return invokeUpdateOnNotification(identifierParams, SmartsConstants.ACKNOWLEDGE, acknowledgeParams);
    }
    
    public boolean unacknowledge(NotificationIdentifierParams identifierParams, NotificationAcknowledgeParams unacknowledgeParams) throws Exception {
        return invokeUpdateOnNotification(identifierParams, SmartsConstants.UNACKNOWLEDGE, unacknowledgeParams);
    }
    
    public boolean addAuditEntry(NotificationIdentifierParams identifierParams, NotificationAuditParams auditParams) throws Exception {
        return invokeUpdateOnNotification(identifierParams, SmartsConstants.ADDAUDITENTRY, auditParams);
    }
    public void addAuditEntry(String className, String instanceName, String eventName, String user, String auditText, String action) throws Exception
    {
        NotificationAuditParams auditParams = new NotificationAuditParams(user, auditText, action);
        addAuditEntry(getIdentifierParameters(className, instanceName, eventName), auditParams);
    }
    public boolean takeOwnership(NotificationIdentifierParams identifierParams, NotificationAcknowledgeParams takeOwnershipParams) throws Exception{
        return invokeUpdateOnNotification(identifierParams, SmartsConstants.TAKE_OWNERSHIP, takeOwnershipParams);
    }
    
    public boolean releaseOwnership(NotificationIdentifierParams identifierParameters, NotificationAcknowledgeParams releaseOwnershipParameters) throws Exception {
        return invokeUpdateOnNotification(identifierParameters, SmartsConstants.RELEASE_OWNERSHIP, releaseOwnershipParameters);
    }
    
    public boolean acknowledge(String className, String instanceName, String eventName, String user, String auditTrailText) throws Exception
    {
        return acknowledge(getIdentifierParameters(className, instanceName, eventName), new NotificationAcknowledgeParams(user, auditTrailText));
    }
    public boolean unacknowledge(String className, String instanceName, String eventName, String user, String auditTrailText) throws Exception
    {
        return unacknowledge(getIdentifierParameters(className, instanceName, eventName), new NotificationAcknowledgeParams(user, auditTrailText));
    }
    
    public boolean takeOwnership(String className, String instanceName, String eventName, String user, String auditTrailText) throws Exception
    {
        return takeOwnership(getIdentifierParameters(className, instanceName, eventName), new NotificationAcknowledgeParams(user, auditTrailText));
    }
    public boolean releaseOwnership(String className, String instanceName, String eventName, String user, String auditTrailText) throws Exception
    {
        return releaseOwnership(getIdentifierParameters(className, instanceName, eventName), new NotificationAcknowledgeParams(user, auditTrailText));
    }
    
    public boolean clearNotification(String className, String instanceName, String eventName, String source, String user, String auditTrailText) throws Exception
    {
        return clearNotification(getIdentifierParameters(className, instanceName, eventName), new NotificationClearParams(user, source, auditTrailText, System.currentTimeMillis()/1000));
    }
    
    public Iterator<Map<String, Object>> fetchNotifications(String className, String instanceName, String eventName, int fetchSize) throws Exception
    {
        return fetchNotifications(className, instanceName, eventName, null, true, fetchSize);
    }
    public Iterator<Map<String, Object>> fetchNotifications(String className, String instanceName, String eventName, boolean expEnabled, int fetchSize) throws Exception
    {
        return fetchNotifications(className, instanceName, eventName, null, expEnabled, fetchSize);
    }

    public Iterator<Map<String, Object>> fetchNotifications(String className, String instanceName, String eventName, List<String> attributes, boolean expEnabled, int fetchSize) throws Exception
    {
        if(attributes == null || attributes.isEmpty())
        {
            String[]propNames = getAttributeNames(SmartsConstants.NOTIFICATION_CLASS_NAME);
            String[]relationNames = getRelationNames(SmartsConstants.NOTIFICATION_CLASS_NAME);
            String[] allAtts = new String[relationNames.length+propNames.length];
            System.arraycopy(propNames, 0, allAtts, 0, propNames.length);
            System.arraycopy(relationNames, 0, allAtts, propNames.length, relationNames.length);
            attributes = Arrays.asList(allAtts);
        }
        String notificationName = SmartsHelper.constructNotificationName(className, instanceName, eventName);
        IQuery query = QueryFactory.getFindTopologyInstancesQuery(logger, this, SmartsConstants.NOTIFICATION_CLASS_NAME, notificationName, attributes, fetchSize, expEnabled);
        return query.execute();
    }
    
    public List<Map<String, Object>> getNotifications(String className, String instanceName, String eventName, List<String> attributes, boolean expEnabled) throws Exception{
        List<Map<String, Object>> notifications = new ArrayList<Map<String, Object>>();
        Iterator<Map<String, Object>> notificationIterator = fetchNotifications(className, instanceName, eventName, attributes, expEnabled, 10);
        while (notificationIterator.hasNext()) {
            Map<String, Object> notification = notificationIterator.next();
            notifications.add(notification);
        }
        return notifications;
    }
    
    public Map<String, Object> getNotification(String className, String instanceName, String eventName) throws Exception
    {
        return getNotification(className, instanceName, eventName, null);
    }
    
    public Map<String, Object> getNotification(String className, String instanceName, String eventName, List<String> attributes) throws Exception
    {
        List<Map<String, Object>> notifications = getNotifications(className, instanceName, eventName, attributes, false);
        if(notifications.size() > 0)
        {
            return notifications.get(0);
        }
        else
        {
            return null;
        }

    }    
    
    public List<Map<String, Object>> getNotifications(String className, String instanceName, String eventName) throws Exception
    {
        return getNotifications(className, instanceName, eventName, null, true);
    }
    public List<Map<String, Object>> getNotifications(String className, String instanceName, String eventName, boolean expEnabled) throws Exception
    {
        return getNotifications(className, instanceName, eventName, null, expEnabled);
    }
    
    
    protected String makeAggregateNotification(NotificationAggregateParams aggregateParameters, MR_AnyVal notification){
        aggregateParameters.setComponentNotificationName(notification);
        MR_AnyVal[] aggregateArgs = aggregateParameters.getMethodArgs();
        MR_AnyVal aggregateNotification = null;
        try {
            aggregateNotification = invokeOperation(SmartsConstants.NOTIFICATION_FACTORY_CLASS, SmartsConstants.NOTIFICATION_FACTORY_INSTANCE, SmartsConstants.MAKE_AGGREGATE, aggregateArgs);
        } catch (Exception e) {
            logger.warn("EXCEPTION while creating aggregate Notification");
            //We do not care if this fails, we will still create the original notification
        }
        logger.info("aggregateNotification: " + aggregateNotification);
        return SmartsHelper.parseNotificationName(aggregateNotification);
    }
    
    private void setAttributes(String notification, Map<String, MR_AnyVal> attributes) throws Exception {
        Set<String> keys = attributes.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String attribute = iterator.next();
            MR_AnyVal value = attributes.get(attribute);
            put(SmartsConstants.NOTIFICATION_CLASS, notification, attribute, value);
        }
    }

    private void invokeNotify(String notification, NotificationNotifyParams notifyParameters) throws Exception{
        invokeOperation(SmartsConstants.NOTIFICATION_CLASS, notification, SmartsConstants.NOTIFY, notifyParameters.getMethodArgs());
    }
    
    private MR_AnyVal findOccurredOn(NotificationIdentifierParams identifierParams, String unknownAgent) {
        MR_AnyVal occurredOn = null;
        try {
            occurredOn = getInstanceFromRepository(identifierParams.getClassName(), identifierParams.getInstanceName());
            if (occurredOn == null) {
                logger.info("Instance to be associated not found");
                if (unknownAgent.equalsIgnoreCase(SmartsConstants.PARAM_CREATE)) {
                    logger.info("Creating nonexisting instance");
                    createInstance(identifierParams.getClassName(), identifierParams.getInstanceName());
                    occurredOn = getInstanceFromRepository(identifierParams.getClassName(), identifierParams.getInstanceName());
                }
            }
        } catch (Exception e) {
            logger.warn("EXCEPTION while finding/creating OcurredOn !!!");
            //We do not care if this fails, we will still create the original notification
        }
        return occurredOn;
    }
    
    public MR_AnyVal getInstanceFromRepository(String instanceClass, String instanceName) throws Exception
    {
        if (instanceExists(instanceClass, instanceName))
        {
            MR_Ref[] instances = findInstances(instanceClass, instanceName, MR_Choice.NONE);
            return new MR_AnyValObjRef(instances[0]);
        }
        return null;
    }
    
    
    private boolean invokeUpdateOnNotification(NotificationIdentifierParams identifierParams, String methodName, NotificationParams parameters)  throws Exception{
        String notificationInstanceName = findNotificationName(identifierParams);
        if (notificationInstanceName == null) return false; // Notification was not found

        invokeICSNotificationMethod(notificationInstanceName, methodName, parameters);
        invokeChanged(notificationInstanceName);
        return true;
    }

    private void invokeChanged(String notification) throws Exception {
        MR_AnyVal waitForNotify = new MR_AnyValUnsignedInt(0);
        MR_AnyVal[] args = {waitForNotify};
        invokeOperation(SmartsConstants.NOTIFICATION_CLASS, notification, SmartsConstants.CHANGED, args);
    }

    private void invokeICSNotificationMethod(String notificationInstanceName, String methodName, NotificationParams parameters) throws Exception{
       invokeOperation(SmartsConstants.NOTIFICATION_CLASS, notificationInstanceName, methodName, parameters.getMethodArgs());
    }
    
    private NotificationIdentifierParams getIdentifierParameters(String className, String instanceName, String eventName)
    {
        NotificationIdentifierParams identifier = new NotificationIdentifierParams(className, instanceName, eventName);
        return identifier;
    }
    
    private NotificationNotifyParams getNotifyParameters(Map<String, Object> optionalParams)
    {
        MR_AnyValString user = getMrStringValue(optionalParams,SmartsConstants.PARAM_USER, "ProxyUser");
        MR_AnyValString source = getMrStringValue(optionalParams,SmartsConstants.PARAM_SOURCE, "Proxy");
        MR_AnyValString auditText = getMrStringValue(optionalParams,SmartsConstants.PARAM_AUDITTRAILTEXT, "Proxy");
        MR_AnyValString nlname = getMrStringValue(optionalParams,SmartsConstants.PARAM_NLNAME, "");
        MR_AnyValUnsignedInt notificationTime = getIntValue(optionalParams,SmartsConstants.PARAM_NOTIFICATIONTIME, (int)(System.currentTimeMillis()/1000));
        MR_AnyValUnsignedInt expiration = getIntValue(optionalParams,SmartsConstants.PARAM_EXPIRATION, 0);
        MR_AnyValUnsignedInt count = getIntValue(optionalParams,SmartsConstants.PARAM_COUNT, 1);
        
        
        NotificationNotifyParams notifyParameters = new NotificationNotifyParams(user, source, auditText, nlname, notificationTime, expiration, count);
        return notifyParameters;
    }
    
    private NotificationAggregateParams getAggregateParameters(Map<String, Object> optionalParams)
    {
        MR_AnyValString aggregateClassName = getMrStringValue(optionalParams, SmartsConstants.PARAM_AGGREGATECLASSNAME, null);
        MR_AnyValString aggregateInstance = getMrStringValue(optionalParams, SmartsConstants.PARAM_AGGREGATEINSTANCENAME, null);
        MR_AnyValString aggregateEventName = getMrStringValue(optionalParams, SmartsConstants.PARAM_AGGREGATEEVENT, null);
        NotificationAggregateParams aggParams = null;
        if(aggregateClassName != null && aggregateEventName != null && aggregateInstance != null)
        {
            aggParams = new NotificationAggregateParams(aggregateClassName, aggregateInstance, aggregateEventName); 
        }
        return aggParams;
    }
    
    private MR_AnyVal getValue(Map<String, Object> params, String propName, String defaultValue) throws Exception
    {
        Object value = params.remove(propName);
        if(value == null)
        {
            if(defaultValue != null)
            {
                value = defaultValue;
            }
        }
        if(value != null)
        {
            return SmartsPropertyHelper.getPropertyValue(this, SmartsConstants.NOTIFICATION_CLASS_NAME, propName, String.valueOf(value));
        }
        else
        {
            return null;
        }
    }
    private MR_AnyValString getMrStringValue(Map<String, Object> params, String propName, String defaultValue)
    {
        Object value = params.remove(propName);
        if(value == null)
        {
            if(defaultValue != null)
                return new MR_AnyValString(defaultValue);
            else
                return null;
        }
        return new MR_AnyValString(String.valueOf(value));
    }
    private MR_AnyValUnsignedInt getIntValue(Map<String, Object> params, String propName, int defaultValue)
    {
        Object value = params.remove(propName);
        if(value == null)
        {
            return new MR_AnyValUnsignedInt(defaultValue);
        }
        return new MR_AnyValUnsignedInt(Integer.parseInt(String.valueOf(value)));
    }
    
    public MR_AnyVal findNotification(NotificationIdentifierParams identifierParams) throws Exception
    {
        MR_AnyVal[] findArgs = { identifierParams.getClassNameAsMR(), identifierParams.getInstanceNameAsMR(), identifierParams.getEventNameAsMR() };
        MR_AnyVal notification = invokeOperation(SmartsConstants.NOTIFICATION_FACTORY_CLASS, SmartsConstants.NOTIFICATION_FACTORY_INSTANCE, SmartsConstants.FIND_NOTIFICATION, findArgs);
        if ((notification == null) || (notification.toString().equals("::"))) {
            return null;
        }
        return notification;
    }
    public MR_AnyVal findNotification(String className, String instanceName, String eventName) throws Exception
    {
        return findNotification(new NotificationIdentifierParams(className, instanceName, eventName));
    }
    public String findNotificationName(NotificationIdentifierParams identifierParams) throws Exception
    {
        MR_AnyVal notification = findNotification(identifierParams);
        return SmartsHelper.parseNotificationName(notification);
    }
    public String findNotificationName(String className, String instanceName, String eventName) throws Exception
    {
        return findNotificationName(new NotificationIdentifierParams(className, instanceName, eventName));
    }

	@Override
	public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) throws Exception
	{
		return getNotification(ids.get("ClassName"), ids.get("InstanceName"), ids.get("EventName"), fieldsToBeRetrieved);
	}
}
