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
 * Time: 2:04:35 PM
 */
public class NotificationAggregateParams extends NotificationIdentifierParams{

    private MR_AnyVal componentNotificationName;

    public NotificationAggregateParams(String className,
                                            String instanceName,
                                            String eventName) {
        super(className, instanceName, eventName);
    }

    public NotificationAggregateParams(MR_AnyValString className,
                                            MR_AnyValString instanceName,
                                            MR_AnyValString eventName) {
        super(className, instanceName, eventName);
    }

    public MR_AnyVal[] getMethodArgs(){
        MR_AnyVal className = getClassNameAsMR();
        MR_AnyVal instName = getInstanceNameAsMR();
        MR_AnyVal eventType = getEventNameAsMR();
        MR_AnyVal componentNotificationName = getComponentNotificationNameAsMR();
        MR_AnyVal notificationClass = new MR_AnyValString(SmartsConstants.NOTIFICATION_CLASS);
        MR_AnyVal[] args = {className, instName, eventType, componentNotificationName, notificationClass};
        return args;
    }

    public MR_AnyVal getComponentNotificationNameAsMR() {
        return componentNotificationName;
    }

    public void setComponentNotificationName(MR_AnyVal componentNotificationName) {
        this.componentNotificationName = componentNotificationName;
    }
}
