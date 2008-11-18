package com.ifountain.smarts.datasource;

import com.ifountain.smarts.connection.SmartsConnectionImpl;
import com.ifountain.smarts.util.DataFromObservable;
import com.ifountain.smarts.util.SmartsConstants;
import com.ifountain.smarts.util.SmartsPropertyHelper;
import com.ifountain.smarts.util.SmartsHelper;
import com.ifountain.smarts.util.params.SmartsSubscribeParameters;
import com.smarts.decs.DX_SubscriptionState;
import com.smarts.remote.SmObserverEvent;
import com.smarts.remote.SmRemoteDomainManager;
import com.smarts.remote.SmRemoteException;
import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValBoolean;
import com.smarts.repos.MR_PropertyNameValue;
import org.apache.log4j.Logger;

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
 * Time: 9:24:57 AM
 */
public class SmartsNotificationListeningAdapter extends BaseSmartsListeningAdapter {
    //set only by tests
    public static int CONNECTION_RETRY_COUNT = 120;
    boolean isTailMode = false;
    private int transientInterval = 0;
    private String notificationList;

    private StagingArea stagingArea;
    private String[] attributeNamesSentFromSmartsDuringUpdate;
    private Hashtable inMemoryNotifications = new Hashtable();
    private boolean processedFirstDataChangeUpdate = false;
    private boolean processedFirstRemovalUpdate = false;
    private boolean monitoredAttsCalculated = false;
    private List monitoredAttributes;
    private List inMemoryNotificationAttList;
    protected Smoother smoother;

    public SmartsNotificationListeningAdapter(String connectionName, long reconnectInterval, Logger logger,
                                              List monitoredAttributes, String nlList, int transientInterval, boolean isTailMode) {
        super(connectionName, reconnectInterval, logger, null);
        this.transientInterval = transientInterval;
        this.isTailMode = isTailMode;
        this.notificationList = "ICS_NL-" + nlList;
        this.monitoredAttributes = monitoredAttributes;
        this.stagingArea = new StagingArea(this, logger, notificationList, transientInterval);
        logPrefix = "[SmartsNotificationObserver]: ";
        SmartsSubscribeParameters params = new SmartsSubscribeParameters(SmartsConstants.NOTIFICATION_LIST_CLASS, notificationList,
                new String[]{SmartsConstants.NOTIFICATION_CHANGE_ATT, SmartsConstants.NOTIFICATION_REMOVAL_ATT});
        subscribeParams = new SmartsSubscribeParameters[]{params};
    }

    public SmartsNotificationListeningAdapter(String connectionName, long reconnectInterval, Logger logger, List attList, String nlList) {
        this(connectionName, reconnectInterval, logger, attList, nlList, 0, false);
    }

    protected void subscribeTo() throws Exception {
        processedFirstDataChangeUpdate = false;
        processedFirstRemovalUpdate = false;
        SmRemoteDomainManager domainManager = ((SmartsConnectionImpl) getConnection()).getDomainManager();
        if (!domainManager.instanceExists(SmartsConstants.NOTIFICATION_LIST_CLASS, notificationList)) {
            throw new Exception("Invalid notificationlist <" + notificationList + ">");
        }
        if (attributeNamesSentFromSmartsDuringUpdate == null) {
            attributeNamesSentFromSmartsDuringUpdate = SmartsPropertyHelper.getObservablePropertyNamesOfanICNotification(domainManager, notificationList);
        }
        calculateMonitoredAttributes(domainManager);
        for (int i = 0; i < subscribeParams.length; i++) {
            String[] properties = subscribeParams[i].getParameters();
            for (int j = 0; j < properties.length; j++) {
                domainManager.propertySubscribe(subscribeParams[i].getClassName(),
                        subscribeParams[i].getInstanceName(), properties[j], 0);
                waitForSubscriptionState(CONNECTION_RETRY_COUNT, subscribeParams[i].getClassName(),
                        subscribeParams[i].getInstanceName(), properties[j], DX_SubscriptionState.SUBSCRIBED);
            }
        }

        if(transientInterval > 0){ // otherwise no smoothing
            logger.debug(logPrefix + "Transient interval is greater than zero, starting smoother.");
            startSmoother();
        }
    }

    protected void unsubscribeFrom() throws Exception {
        try {
            unsubscribeFromProperties();
        }
        catch (SmRemoteException sre) {
            logger.warn(logPrefix + "Not even subscribed, no need to unsubscribe.", sre);
        }
        finally{
            if ( smoother != null )
            {
                smoother.stopSmoother();
                logger.debug(logPrefix+ "Stopping smoother");
                try
                {
                    smoother.join();
                }
                catch (InterruptedException e)
                {
                }
                logger.debug(logPrefix+ "Smoother stopped");
                smoother = null;
            }

        }
    }

    protected void unsubscribeFromProperties() throws IOException, SmRemoteException {
        if (subscribeParams != null) {
            SmRemoteDomainManager domainManager = ((SmartsConnectionImpl) getConnection()).getDomainManager();
            for (int i = 0; i < subscribeParams.length; i++) {
                String[] properties = subscribeParams[i].getParameters();
                for (int j = 0; j < properties.length; j++) {
                    domainManager.propertyUnsubscribe(subscribeParams[i].getClassName(),
                            subscribeParams[i].getInstanceName(), properties[j]);
                    waitForSubscriptionState(2, subscribeParams[i].getClassName(),
                            subscribeParams[i].getInstanceName(), properties[j],
                            DX_SubscriptionState.UNSUBSCRIBED);
                }
            }
        }
    }

    public Object processIncomingData(DataFromObservable data) {
        if (data.getEventType() != SmObserverEvent.ATTRIBUTE_CHANGE) {
            logger.debug(logPrefix + "Ignoring data.");
            return null;
        }
        MR_AnyVal val = data.getValue();
        if (val != null) {
            MR_AnyVal[] attributeValues = (MR_AnyVal[]) val.getValue();
            if (isExtraneousUpdates(data.getPropertyName(), attributeValues)) {
                return null;
            }

            if (data.getPropertyName().equals(SmartsConstants.NOTIFICATION_CHANGE_ATT)) {
                return updateForChange(attributeValues);
            } else {
                return updateForArchive(attributeValues);
            }
        } else {
            logger.debug(logPrefix + "Discarding data since property value is null");
            return null;
        }
    }

    protected void getExistingObjects(SmartsSubscribeParameters[] parameters) throws Exception {
        logger.debug(logPrefix + "Getting existing notifications.");
        SmRemoteDomainManager domainManager = ((SmartsConnectionImpl) getConnection()).getDomainManager();
        for (int j = 0; j < parameters.length; j++) {
            ArrayList notificationsInList = SmartsHelper.getExistingDetailsNotificationsOfAList(domainManager, parameters[j].getInstanceName());
            logger.info(logPrefix + notificationsInList.size() + " existing notifications retreived.");
            for (int i = 0; i < notificationsInList.size(); i++) {
                List<MR_PropertyNameValue> props = (List<MR_PropertyNameValue>) notificationsInList.get(i);
                String notificationName = String.valueOf(props.get(0).getPropertyValue().getValue());

                MR_PropertyNameValue[] nameValuePairs = (MR_PropertyNameValue[]) props.toArray(new MR_PropertyNameValue[props.size()]);
                boolean isActive = isNotificationActive(notificationName, nameValuePairs);
                boolean isInMemory = inMemoryNotifications.containsKey(notificationName);
                if (!isInMemory) {
                    InMemoryNotification inMemoryNotification = createInMemoryNotification(notificationName,
                            nameValuePairs);
                    inMemoryNotifications.put(inMemoryNotification.getName(), inMemoryNotification);
                    if (!isTailMode) {
                        if (isActive) {
                            logger.debug(logPrefix + "Existing notification <" + inMemoryNotification.getName() + "> is sent.");
                            sendDataToObservers(createObject(NOTIFY, nameValuePairs));
                        } else {
                            logger.debug(logPrefix + "Existing notification <" + inMemoryNotification.getName() + "> is not active. It is sent with CLEAR ICEventType.");
                            sendDataToObservers(createObject(CLEAR, nameValuePairs));
                        }
                    }
                }
            }
        }
        sendRetrieveExistingObjectsFinished();
    }

    private void waitForSubscriptionState(int seconds, String className, String instanceName, String attributeName, int subscriptionState) throws SmRemoteException, IOException {
        logger.info(logPrefix + "Waiting for subscription acknowledge from smarts for ClassName : <" + className + "> InstanceName : <" + instanceName + "> AttributeName : <" + attributeName + ">.");
        logger.info(logPrefix + "Waiting for subscription acknowledge from smarts can take at most 2 minutes.");
        boolean isSubscriptionStateAsExpected = false;
        SmRemoteDomainManager domainManager = ((SmartsConnectionImpl) getConnection()).getDomainManager();
        for (int i = 0; i < seconds * 100; i++) {
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException e) {
            }
            if (domainManager.getPropertySubscriptionState(className, instanceName, attributeName) == subscriptionState) {
                isSubscriptionStateAsExpected = true;
                break;
            }
        }
        if (!isSubscriptionStateAsExpected) {
            String errMsg = "Could not get expected subscription state <" + subscriptionState + " for property: " + attributeName;
            logger.error(logPrefix + errMsg);
            throw new SmRemoteException(errMsg);
        } else {
            logger.debug(logPrefix + "Subscription acknowledge reacived from smarts for ClassName : <" + className + "> InstanceName : <" + instanceName + "> AttributeName : <" + attributeName + ">");
        }
    }

    private Object updateForChange(MR_AnyVal[] attributeValues) {
        String notificationName = attributeValues[0].getValue().toString();
        logger.debug(logPrefix + "Update received for notification " + notificationName);
        try {
            MR_PropertyNameValue[] nameValuePairs = getNameValuePairs(attributeValues);
            String eventType = determineEventType(notificationName, nameValuePairs);
            if (!eventType.equals("NOCHANGE")) {
                if (transientInterval > 0) {
                    logger.debug(logPrefix + "Will update staging area for: " + notificationName + ". Event type is: " + eventType);
                    stagingArea.updateStagedNotifications(notificationName, eventType, nameValuePairs);
                } else {
                    logger.debug(logPrefix + "Will create a " + eventType + " notification for " + notificationName);
                    return createObject(eventType, nameValuePairs);
                }
            }
        }
        catch (Exception e) {
            logger.error(logPrefix + "Could not get Name Value pairs for: " + notificationName);
            logger.error(logPrefix + "EVENT COULD NOT BE SENT TO INCHARGE READER. Reason : " + e.getMessage());
        }
        return null;
    }

    private Object updateForArchive(MR_AnyVal[] attributeValues) {
        Object notification = null;
        try {
            if (transientInterval > 0) {
                String notificationName = attributeValues[0].getValue().toString();
                stagingArea.updateStagedNotifications(notificationName, ARCHIVE,
                        getNameValuePairs(attributeValues));
            } else {
                notification = createObject(ARCHIVE, getNameValuePairs(attributeValues));
            }
            String notificationName = attributeValues[0].getValue().toString();
            inMemoryNotifications.remove(notificationName);
            logger.debug(logPrefix + notificationName + " is removed from memory after being archived.");
        }
        catch (Exception e) {
            logger.error(logPrefix + "Could not get Name Value pairs for: " + attributeValues[0]);
            logger.error(logPrefix + "EVENT COULD NOT BE SENT TO SMARTS READER FOR ARCHIVING. Reason : " + e.getMessage());
        }
        return notification;
    }

    private MR_PropertyNameValue[] getNameValuePairs(MR_AnyVal[] attributeValues) throws SmRemoteException {

        if (attributeNamesSentFromSmartsDuringUpdate.length != attributeValues.length) {
            throw new SmRemoteException("Did not get expected number of attribute values with the update : Actual <" + attributeValues.length + "> Expected <" + attributeNamesSentFromSmartsDuringUpdate.length + ">");
        }
        MR_PropertyNameValue[] propertyNameValues = new MR_PropertyNameValue[attributeValues.length];
        for (int i = 0; i < attributeValues.length; i++) {
            propertyNameValues[i] = new MR_PropertyNameValue(
                    attributeNamesSentFromSmartsDuringUpdate[i], attributeValues[i]);
        }
        return propertyNameValues;
    }

    protected String determineEventType(String notificationName, MR_PropertyNameValue[] nameValuePairs) {
        boolean isNew = !inMemoryNotifications.containsKey(notificationName);
        boolean isActive = isNotificationActive(notificationName, nameValuePairs);

        if (isNew) {
            logger.debug(logPrefix + notificationName + " is new and put into memory.");
            inMemoryNotifications.put(notificationName, createInMemoryNotification(notificationName,
                    nameValuePairs));
            if (isActive) {
                logger.debug(logPrefix + "event type is set to NOTIFY for: " + notificationName);
                return NOTIFY;
            } else {
                logger.debug(logPrefix + "event type is set to CLEAR for: " + notificationName);
                return CLEAR;
            }
        } else {
            logger.debug(logPrefix + notificationName + " is not new.");

            InMemoryNotification inMemoryNotification = (InMemoryNotification) inMemoryNotifications.get(notificationName);
            boolean wasNotificationActive = ((MR_AnyValBoolean) inMemoryNotification.getMonitoredAttributes().get("Active")).getBooleanValue();
            if (inMemoryNotification.isChanged(nameValuePairs)) {
                logger.debug(logPrefix + notificationName + " has some changed attributes. Attribute values updated in memory.");
                if (wasNotificationActive && !isActive) {
                    logger.debug(logPrefix + notificationName + " became inactive and the event type is set to CLEAR");
                    return CLEAR;
                } else if (!wasNotificationActive && isActive) {
                    logger.debug(logPrefix + notificationName + " became active and the event type is set to NOTIFY");
                    return NOTIFY;
                } else {
                    logger.debug(logPrefix + notificationName + ": event type is set to CHANGE");
                    return CHANGE;
                }
            }
            logger.debug(logPrefix + notificationName + " has no monitored attribute change. Update will be discarded.");
            return NOCHANGE;
        }
    }

    private boolean isNotificationActive(String notificationName, MR_PropertyNameValue[] nameValuePairs) {
        boolean active = false;
        boolean propertyActiveObserved = false;
        for (int i = 0; i < nameValuePairs.length; i++) {
            MR_PropertyNameValue nameValuePair = nameValuePairs[i];
            if ("Active".equals(nameValuePair.getPropertyName())) {
                propertyActiveObserved = true;
                active = ((MR_AnyValBoolean) nameValuePair.getPropertyValue()).getBooleanValue();
                break;
            }
        }
        if (!propertyActiveObserved) {
            logger.warn(logPrefix + "Property Active doesnot exists among attributes of <" + notificationName + ">");
        }
        return active;
    }

    private boolean isExtraneousUpdates(String propertyName, MR_AnyVal[] attributeValue) {
        if (isSkipFirstDataChange(propertyName))
            return true;
        if (isSkipFirstDataRemoval(propertyName))
            return true;

        if ((attributeValue[0] == null) || (attributeValue[0].toString().length() == 0)) {
            logger.debug(logPrefix + "Eliminated due to null or empty attributeValue[0]: " + propertyName + " attributes " + Arrays.toString(attributeValue));
            return true; // ELIMINATE ATTRIBUTE_CHANGE NOTIFICATION WITH EMPTY
            // PROPERTY VALUES!!!
        }
        return false;
    }

    private boolean isSkipFirstDataChange(String propertyName) {
        // skip first notificationDataChange update since this is for last
        // change prior to subscription
        if (propertyName.equals(SmartsConstants.NOTIFICATION_CHANGE_ATT) && !processedFirstDataChangeUpdate) {
            logger.debug(logPrefix + "Eliminated first update for: " + propertyName);

            processedFirstDataChangeUpdate = true;
            return true;
        }
        return false;
    }

    private boolean isSkipFirstDataRemoval(String propertyName) {
        // skip first notificationDataRemoval update since this is for last
        // change prior to subscription
        if (propertyName.equals(SmartsConstants.NOTIFICATION_REMOVAL_ATT) && !processedFirstRemovalUpdate) {
            logger.debug(logPrefix + "Eliminated first update for: " + propertyName);
            processedFirstRemovalUpdate = true;
            return true;
        }
        return false;
    }

    protected SmRemoteDomainManager getDomainManager() {
        return ((SmartsConnectionImpl) getConnection()).getDomainManager();
    }

    private void calculateMonitoredAttributes(SmRemoteDomainManager domainManager) throws Exception {
        if (!monitoredAttsCalculated) {
            List allNotificationAtts = Arrays.asList(attributeNamesSentFromSmartsDuringUpdate);
            if (monitoredAttributes.isEmpty()) {
                logger.debug(logPrefix + "No monitoring attribute is defined, will subscribe to all attributes.");
                monitoredAttributes = allNotificationAtts;
            } else {
                for (Iterator iter = monitoredAttributes.iterator(); iter.hasNext();) {
                    String attributeName = (String) iter.next();
                    if (!allNotificationAtts.contains(attributeName)) {

                        throw new Exception("Monitored attribute <" + attributeName + "> is not a valid.");
                    }
                }
            }
            inMemoryNotificationAttList = monitoredAttributes;
            if (!inMemoryNotificationAttList.contains("Active")) {
                    inMemoryNotificationAttList.add("Active");
                }
        }
    }

    private InMemoryNotification createInMemoryNotification(String name, MR_PropertyNameValue[] nameValuePairs) {
        Hashtable attributes = new Hashtable();
        for (int i = 0; i < nameValuePairs.length; i++) {
            MR_PropertyNameValue nameValuePair = nameValuePairs[i];
            if (inMemoryNotificationAttList.contains(nameValuePair.getPropertyName())) {
                attributes.put(nameValuePair.getPropertyName(), nameValuePair.getPropertyValue());
            }
        }
        return new InMemoryNotification(name, attributes, logger);
    }

     protected Object createObject(String eventType, MR_PropertyNameValue[] propertyNameValues) {
        return super.createObject(eventType, propertyNameValues, monitoredAttributes); 
    }

    protected void startSmoother(){
        //Smoother thread will check stagedarea with an interval less than transientinterval. This is not mandatory. We choose dividing by 3.
        //It guarantees a notificaion waiting time should be no more than 1.3*transientInterval.
        int refreshInterval = transientInterval/3;
        if(refreshInterval == 0) refreshInterval = 1;
        smoother = new Smoother(stagingArea, refreshInterval);
		smoother.start();
    }
    protected void setSmoother(Smoother smoother) {
        this.smoother = smoother;
    }
}
