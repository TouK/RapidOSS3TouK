/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
package com.ifountain.smarts.util.params;

import com.ifountain.smarts.util.SmartsConstants;
import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValString;

/**
 * User: kinikogl
 * Date: Sep 13, 2004
 * Time: 1:47:01 PM
 */
public class NotificationIdentifierParams implements NotificationParams{

    private MR_AnyValString className;
    private MR_AnyValString instanceName;
    private MR_AnyValString eventName;

    public NotificationIdentifierParams(String className,
                                            String instanceName,
                                            String eventName) {
        this.className = new MR_AnyValString(className);
        this.instanceName = new MR_AnyValString(instanceName);
        this.eventName = new MR_AnyValString(eventName);
    }

    public NotificationIdentifierParams(MR_AnyValString className,
                                            MR_AnyValString instanceName,
                                            MR_AnyValString eventName) {
        this.className = className;
        this.instanceName = instanceName;
        this.eventName = eventName;
    }

    public MR_AnyVal[] getMethodArgs(){
        MR_AnyVal className = getClassNameAsMR();
        MR_AnyVal instName = getInstanceNameAsMR();
        MR_AnyVal eventType = getEventNameAsMR();
        MR_AnyVal notificationClass = new MR_AnyValString(SmartsConstants.NOTIFICATION_CLASS);
        MR_AnyVal[] args = {className, instName, eventType, notificationClass};
        return args;
    }

    public MR_AnyValString getClassNameAsMR() {
        return className;
    }

    public void setClassName(MR_AnyValString className) {
        this.className = className;
    }

    public MR_AnyValString getInstanceNameAsMR() {
        return instanceName;
    }

    public void setInstanceName(MR_AnyValString instanceName) {
        this.instanceName = instanceName;
    }

    public MR_AnyValString getEventNameAsMR() {
        return eventName;
    }

    public void setEventName(MR_AnyValString eventName) {
        this.eventName = eventName;
    }

    public String getClassName() {
        return className.getStringValue();
    }

    public void setClassName(String className) {
        this.className = new MR_AnyValString(className);
    }

    public String getInstanceName() {
        return instanceName.getStringValue();
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = new MR_AnyValString(instanceName);
    }

    public String getEventName() {
        return eventName.getStringValue();
    }

    public void setEventName(String eventName) {
        this.eventName = new MR_AnyValString(eventName);
    }

    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("ClassName: ").append(getClassName()).append("\n");
        buffer.append("InstanceName: ").append(getInstanceName()).append("\n");
        buffer.append("EventName: ").append(getEventName()).append("\n");
        return buffer.toString();
    }


}
