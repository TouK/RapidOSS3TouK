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
