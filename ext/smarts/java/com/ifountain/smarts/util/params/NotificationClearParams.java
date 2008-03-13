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
import com.smarts.repos.MR_AnyValUnsignedInt;

/**
 * User: kinikogl
 * Date: Sep 13, 2004
 * Time: 1:52:17 PM
 */
public class NotificationClearParams extends NotificationAcknowledgeParams{

    private MR_AnyValString source;
    private MR_AnyValUnsignedInt clearTime;

    public NotificationClearParams(String user, String source,
                                       String auditTrailText, long clearTime) {
        super(user, auditTrailText);
        this.source = new MR_AnyValString(source);
        this.clearTime = new MR_AnyValUnsignedInt(clearTime);
    }

    public MR_AnyVal[] getMethodArgs(){
        //todo: should clearTime be omitted?
//        MR_AnyVal[] args = {getUserAsMR(), getSourceAsMR(), getAuditTrailTextAsMR(), getClearTimeAsMR()};
        MR_AnyVal[] args = {getUserAsMR(), getSourceAsMR(), getAuditTrailTextAsMR()};
        return args;
    }

    public NotificationClearParams(MR_AnyValString user, MR_AnyValString source,
                                        MR_AnyValString auditTrailText, MR_AnyValUnsignedInt clearTime) {
        super(user, auditTrailText);
        this.source = source;
        this.clearTime = clearTime;
    }
    
    public MR_AnyValString getSourceAsMR() {
        return source;
    }

    public void setSource(MR_AnyValString source) {
        this.source = source;
    }

    public MR_AnyValUnsignedInt getClearTimeAsMR() {
        return clearTime;
    }

    public void setNotificationTime(MR_AnyValUnsignedInt clearTime) {
        this.clearTime = clearTime;
    }

    public String getSource() {
        return source.getStringValue();
    }

    public void setSource(String source) {
        this.source = new MR_AnyValString(source);
    }

    public long getClearTime() {
        return clearTime.getUnsignedIntValue();
    }

    public void setClearTime(long notificationTime) {
        this.clearTime = new MR_AnyValUnsignedInt(notificationTime);
    }

    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("User: ").append(getUser()).append("/n");
        buffer.append("Source: ").append(getSource()).append("/n");
        buffer.append("AuditTrailText: ").append(getAuditTrailText()).append("/n");
        buffer.append("ClearTime: ").append(getClearTime()).append("/n");
        return buffer.toString();
    }

}
