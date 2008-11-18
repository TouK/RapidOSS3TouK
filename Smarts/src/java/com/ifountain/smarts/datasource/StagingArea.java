package com.ifountain.smarts.datasource;

import com.smarts.repos.MR_PropertyNameValue;
import com.ifountain.smarts.util.SmartsHelper;

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
 * Date: Jul 16, 2008
 * Time: 10:40:26 AM
 */
public class StagingArea {
    protected Hashtable stagedNotifications = new Hashtable();
    private String nlName;
    private int transientInterval;
    private Logger logger;
    private SmartsNotificationListeningAdapter notificationAdapter;
    private String logPrefix = "[StagingArea]: ";

    public StagingArea(SmartsNotificationListeningAdapter nAdapter, Logger logger, String nlName, int transientInterval) {
        this.logger = logger;
        this.nlName = nlName;
        this.transientInterval = transientInterval;
        this.notificationAdapter = nAdapter;
    }

    // IMPORTANT COMMENT: This method regulates access to shared resource stagedNotifications and should be synchronized!
    // This method is used by the Processor thread in ListeningAdapter
    public synchronized void updateStagedNotifications(String notificationName, String eventType, MR_PropertyNameValue[] nameValuePairs) {
        if(stagedNotifications.containsKey(notificationName)){
            logger.debug(logPrefix + notificationName + " was in staging area");
            StagedNotification stagedNotification = (StagedNotification) stagedNotifications.get(notificationName);

            if(eventType.equalsIgnoreCase(BaseSmartsListeningAdapter.CHANGE)){
                boolean isChangeAfterNotify = stagedNotification.getEventName().equalsIgnoreCase(BaseSmartsListeningAdapter.NOTIFY);
                boolean isChangeAfterClear = stagedNotification.getEventName().equalsIgnoreCase(BaseSmartsListeningAdapter.CLEAR);
                if(isChangeAfterNotify || isChangeAfterClear){
                    logger.debug(logPrefix + "Notification is updated while in the staging area. Will leave the eventtype as " +
                            stagedNotification.getEventName());
                }
                else{
                    logger.debug(logPrefix + "changing the event type of the existing notification " + notificationName + " to " + eventType);
                    stagedNotification.setEventName(eventType);
                }
                stagedNotification.setNameValuePairs(nameValuePairs);
                logger.debug(logPrefix + "updated the monitored attributes of the notification.");
            }
            else if (eventType.equalsIgnoreCase(BaseSmartsListeningAdapter.ARCHIVE)){
                //send previous state of the staged notification
                sendNotification(stagedNotification.getEventName(), stagedNotification.getNameValuePairs());
                logger.debug(logPrefix + "got ARCHIVE, previous event <" + stagedNotification.getEventName() + "> sent to reader.");
                // send archive
                sendNotification(eventType, nameValuePairs);
                logger.debug(logPrefix + "ARCHIVE event sent to reader.");
                stagedNotifications.remove(notificationName);
                logger.debug(logPrefix + "notification <" + notificationName + ">removed from staging area.");
            }
            else{
                if(eventType.equalsIgnoreCase(BaseSmartsListeningAdapter.NOTIFY)){
                    if(stagedNotification.getEventName().equalsIgnoreCase(BaseSmartsListeningAdapter.CLEAR)){
                        sendNotification(stagedNotification.getEventName(), stagedNotification.getNameValuePairs());
                        logger.debug(logPrefix + "got NOTIFY, previous CLEAR sent to reader.");
                    }
                }
                else if(eventType.equalsIgnoreCase(BaseSmartsListeningAdapter.CLEAR)){
                    sendNotification(stagedNotification.getEventName(), stagedNotification.getNameValuePairs());
                    logger.debug(logPrefix + "got CLEAR, previous event <" + stagedNotification.getEventName() + "> sent to reader.");
                }
                stagedNotification.setEventName(eventType);
                stagedNotification.setNameValuePairs(nameValuePairs);
                logger.debug(logPrefix + "updated the monitored attributes of the notification.");
            }
        }
        else if(eventType.equalsIgnoreCase(BaseSmartsListeningAdapter.ARCHIVE)){
            // send archive
            sendNotification(eventType, nameValuePairs);
            logger.debug(logPrefix + "ARCHIVE event sent to reader.");
        }
        else{
            logger.debug(logPrefix + notificationName + " was NOT in staging area");
            StagedNotification stagedNotification = new StagedNotification(nameValuePairs, eventType, new Date().getTime());
            stagedNotifications.put(notificationName, stagedNotification);
            logger.debug(logPrefix + "created a new StagedNotification for " + notificationName + " and put into the staging area with event type: " + eventType);
        }
    }

    // IMPORTANT COMMENT: This method regulates access to shared resource stagedNotifications and should be synchronized!
    // This method is used by the Smoother thread
    public synchronized void processedStagedNotifications() {
        logger.debug(logPrefix + "processing staged notifications.");
        Map<String, StagedNotification> expiredNotifications = getExpiredNotificationList();
        if(!expiredNotifications.isEmpty())
        {
            try
            {
                Map<String, String> notificationsInListAsMap = SmartsHelper.getExistingNotificationsOfAListAsMap(notificationAdapter.getDomainManager(), nlName, logger);
                logger.debug(logPrefix + notificationsInListAsMap.size() + " notifications exist in " + nlName);
                Set entries = expiredNotifications.entrySet();
                for(Iterator<Map.Entry<String,StagedNotification>> it=entries.iterator(); it.hasNext(); )
                {
                    Map.Entry<String,StagedNotification> notificationEntry = it.next();
                    processEachStagedNotification(notificationEntry.getKey(), notificationEntry.getValue(), notificationsInListAsMap);

                }
            }
            catch (Exception e)
            {
                String errorMsg = "Exception <" + e.toString() + "> while accesing notification list: " + nlName;
                logger.error(errorMsg);
            }
        }

    }
    private Map<String, StagedNotification> getExpiredNotificationList(){
        Map<String, StagedNotification> expiredNotifications = new HashMap<String, StagedNotification>();
        Enumeration notifications = stagedNotifications.keys();
        long currentTime = new Date().getTime();
        while (notifications.hasMoreElements()){
            String notificationName = (String)notifications.nextElement();
            StagedNotification stagedNotification = (StagedNotification)stagedNotifications.get(notificationName);
            if(stagedNotification.getTimeStamp()+ transientInterval < currentTime)
            {
                expiredNotifications.put(notificationName, stagedNotification);    
            }
        }
        return expiredNotifications;
    }
    private void processEachStagedNotification(String notificationName, StagedNotification notification, Map<String, String> notificationsInListAsMap){
        boolean isNotificationInList = notificationsInListAsMap.containsKey(notificationName);
        if (isNotificationInList){
            updateAdapter(notificationName,notification);
        }
        else{
            logger.debug(logPrefix + notificationName + " is not a member of " + nlName + ", will not send it to Reader. Removing from staged notifications.");
            stagedNotifications.remove(notificationName);
        }
    }

    // TODO: If exception occurs when  sendNotification is called , then the remaining notifications will not be processed
    // If exception will preserve on the next run of processedStagedNotifications some notifications will not be processed, and the situation will go on like that forever until the exception disappears
     private void updateAdapter(String notificationName, StagedNotification stagedNotification){
        logger.debug(logPrefix + "Sending " + notificationName + " to the Reader, with event name: " + stagedNotification.getEventName());
        sendNotification(stagedNotification.getEventName(), stagedNotification.getNameValuePairs());
        stagedNotifications.remove(notificationName);
        logger.debug(logPrefix + "Removed " + notificationName + " from staging area.");
    }

    protected void sendNotification(String eventType, MR_PropertyNameValue[] propertyNameValues) {
        notificationAdapter.sendDataToObservers(notificationAdapter.createObject(eventType, propertyNameValues));
    }
}
