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
 * Date: Sep 13, 2004
 * Time: 1:52:17 PM
 */

// This class is used for Acknowledge, Unacknowledge, takeOwnership and releaseOwnership
//since they all use the same parameters
public class NotificationAcknowledgeParams implements NotificationParams{

    MR_AnyValString user;
    MR_AnyValString auditTrailText;


    public NotificationAcknowledgeParams(String user, String auditTrailText) {
        this.user = new MR_AnyValString(user);
        this.auditTrailText = new MR_AnyValString(auditTrailText);
    }

    public NotificationAcknowledgeParams(MR_AnyValString user, MR_AnyValString auditTrailText) {
        this.user = user;
        this.auditTrailText = auditTrailText;
    }

    public MR_AnyVal[] getMethodArgs(){
        MR_AnyVal[] args = {getUserAsMR(), getAuditTrailTextAsMR()};
        return args;
    }

    public MR_AnyValString getUserAsMR() {
        return user;
    }

    public void setUser(MR_AnyValString user) {
        this.user = user;
    }

    public MR_AnyValString getAuditTrailTextAsMR() {
        return auditTrailText;
    }

    public void setAuditTrailText(MR_AnyValString auditTrailText) {
        this.auditTrailText = auditTrailText;
    }

    public String getUser() {
        return user.getStringValue();
    }

    public void setUser(String user) {
        this.user = new MR_AnyValString(user);
    }

    public String getAuditTrailText() {
        return auditTrailText.getStringValue();
    }

    public void setAuditTrailText(String auditTrailText) {
        this.auditTrailText = new MR_AnyValString(auditTrailText);
    }

    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("User: ").append(getUser()).append("/n");
        buffer.append("AuditTrailText: ").append(getAuditTrailText()).append("/n");
        return buffer.toString();
    }

}
