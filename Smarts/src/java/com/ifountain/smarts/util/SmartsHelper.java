/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 20, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.util;

import com.ifountain.smarts.datasource.BaseSmartsAdapter;
import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValArray;
import com.smarts.repos.MR_PropertyNameValue;
import com.smarts.remote.SmRepositoryInterfaceHandler;

import java.util.*;

import org.apache.log4j.Logger;

public class SmartsHelper {

    public static ArrayList<String> getExistingNotificationsOfAList(BaseSmartsAdapter smartsAdapter, String nlName) throws Exception {
        ArrayList<String> notificationsInList = new ArrayList<String>();
        String[] propNames = {"AllNotifications"};
        MR_AnyVal[] anyVals = smartsAdapter.getProperties(SmartsConstants.NOTIFICATION_LIST_CLASS, nlName, propNames);
        String[] nNames = (String[])anyVals[0].getValue();
        for (int i = 0; i < nNames.length; i++) {
            notificationsInList.add(nNames[i]);
        }
        return notificationsInList;
    }
    public static ArrayList<String> getExistingNotificationsOfAList(SmRepositoryInterfaceHandler domainManager, String nlName, Logger logger) throws Exception {
        logger.debug("Getting existing notifications of " + nlName);
        ArrayList<String> notificationsInList = new ArrayList<String>();
        String[] propNames = {"AllNotifications"};
        MR_AnyVal[] anyVals = domainManager.getProperties(SmartsConstants.NOTIFICATION_LIST_CLASS, nlName, propNames);
        String[] nNames = (String[])anyVals[0].getValue();
        for (int i = 0; i < nNames.length; i++) {
            notificationsInList.add(nNames[i]);
        }
        if(logger.isDebugEnabled())
        {
            StringBuffer notificationNameBuffer = new StringBuffer("Got from ").append(nlName).append("[");
            for(int i=0; i < notificationsInList.size(); i++)
            {
                notificationNameBuffer.append(notificationsInList.get(i)).append(", ");
            }
            notificationNameBuffer.append("]");
            logger.debug(notificationNameBuffer.toString());
        }
        return notificationsInList;
    }

    public static Map<String, String> getExistingNotificationsOfAListAsMap(SmRepositoryInterfaceHandler domainManager, String nlName, Logger logger) throws Exception {
        logger.debug("Getting existing notifications of " + nlName);
        Map<String, String> notificationsInList = new HashMap<String, String>();
        String[] propNames = {"AllNotifications"};
        MR_AnyVal[] anyVals = domainManager.getProperties(SmartsConstants.NOTIFICATION_LIST_CLASS, nlName, propNames);
        String[] nNames = (String[])anyVals[0].getValue();
        for (int i = 0; i < nNames.length; i++) {
            notificationsInList.put(nNames[i], nNames[i]);
        }
        if(logger.isDebugEnabled())
        {
            StringBuffer notificationNameBuffer = new StringBuffer("Got from ").append(nlName).append("[");
            Set entries = notificationsInList.entrySet();
            for(Iterator<Map.Entry<String, String>> it=entries.iterator(); it.hasNext(); )
            {
                notificationNameBuffer.append(it.next().getKey()).append(", ");    
            }
            notificationNameBuffer.append("]");
            logger.debug(notificationNameBuffer.toString());
        }
        return notificationsInList;
    }

    public static String constructNotificationName(String className, String instanceName, String eventName) {
        StringBuffer sBuffer = new StringBuffer("NOTIFICATION-");
        sBuffer.append(replaceSpacesAndUnderscores(className));
        sBuffer.append("_");
        sBuffer.append(replaceSpacesAndUnderscores(instanceName));
        sBuffer.append("_");
        sBuffer.append(replaceSpacesAndUnderscores(eventName));
        return sBuffer.toString();
    }
    private static String replaceSpacesAndUnderscores(String stringToBeReplaced) {
        String s;
        s = stringToBeReplaced.replaceAll("_", "__");
        s = s.replaceAll(" ", "_20");
        return s;
    }
    
    public static String parseNotificationName(MR_AnyVal notification)
    {
        if ((notification == null) || (notification.toString().equals("::")))
        {
            return null;
        }
        String notificationInstanceName = notification.toString().substring("ICS_Notification::".length());
        return notificationInstanceName;
    }

    public static ArrayList getExistingDetailsNotificationsOfAList(SmRepositoryInterfaceHandler domainManager, String nlName) throws Exception{
        ArrayList notificationsInList = new ArrayList();
        String[] propNames = {"AllNotificationsData","ColumnInfo"};
        MR_AnyVal[] anyVals = domainManager.getProperties(SmartsConstants.NOTIFICATION_LIST_CLASS, nlName, propNames);
        MR_AnyVal[] notifications = (MR_AnyVal[]) anyVals[0].getValue();
        MR_AnyVal[] colNamesValues = (MR_AnyVal[]) anyVals[1].getValue();
        String[] colNames = new String[colNamesValues.length];
        for (int i = 0; i < colNamesValues.length; i++)
        {
            colNames[i] = ((MR_AnyValArray)colNamesValues[i]).getArrayValue()[1].toString();
        }
        for (int i = 0; i < notifications.length; i++)
        {
            List props = new ArrayList();
            MR_AnyVal[] propValues = ((MR_AnyValArray)notifications[i]).getArrayValue();
            for (int j = 0; j < colNames.length; j++)
            {
                props.add(new MR_PropertyNameValue(colNames[j], propValues[j]));
            }
            notificationsInList.add(props);
        }

        return notificationsInList;
    }
    
}
