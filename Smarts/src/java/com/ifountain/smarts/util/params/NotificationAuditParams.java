/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
package com.ifountain.smarts.util.params;

import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValString;

/**
 * User: kinikogl
 * Date: Sep 15, 2004
 * Time: 2:25:21 PM
 */
public class NotificationAuditParams extends NotificationAcknowledgeParams{

    private MR_AnyValString actionType;


    public NotificationAuditParams(String user, String auditTrailText, String actionType) {
        super(user, auditTrailText);
        this.actionType = new MR_AnyValString(actionType);
    }

    public NotificationAuditParams(MR_AnyValString user, MR_AnyValString auditTrailText, MR_AnyValString actionType) {
        super(user, auditTrailText);
        this.actionType = actionType;
    }

    public MR_AnyVal[] getMethodArgs(){
        MR_AnyVal[] args = {getUserAsMR(), getActionTypeAsMR(), getAuditTrailTextAsMR()};
        return args;
    }

    public MR_AnyValString getActionTypeAsMR() {
        return actionType;
    }

    public String getActionType() {
        return actionType.toString();
    }

    public void setActionType(MR_AnyValString actionType) {
        this.actionType = actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = new MR_AnyValString(actionType);
    }
}
