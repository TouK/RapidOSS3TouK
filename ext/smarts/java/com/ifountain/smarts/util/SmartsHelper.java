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
package com.ifountain.smarts.util;

import java.util.ArrayList;

import com.ifountain.smarts.datasource.BaseSmartsAdapter;
import com.smarts.repos.MR_AnyVal;

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
    
}
