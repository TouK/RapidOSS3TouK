/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
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
public class NotificationNotifyParams implements NotificationParams {

    private MR_AnyValString user;
    private MR_AnyValString source;
    private MR_AnyValString auditTrailText;
    private MR_AnyValString nlName;
    private MR_AnyValUnsignedInt notificationTime;
    private MR_AnyValUnsignedInt expiration;
    private MR_AnyValUnsignedInt count;

    public NotificationNotifyParams(String user, String source, String auditTrailText,
                                        String nlName, long notificationTime,
                                        long expiration, long count) {
        this.user = new MR_AnyValString(user);
        this.source = new MR_AnyValString(source);
        this.auditTrailText = new MR_AnyValString(auditTrailText);
        this.nlName = new MR_AnyValString(nlName);
        this.notificationTime = new MR_AnyValUnsignedInt(notificationTime);
        this.expiration = new MR_AnyValUnsignedInt(expiration);
        this.count = new MR_AnyValUnsignedInt(count);
    }

    public NotificationNotifyParams(MR_AnyValString user, MR_AnyValString source,
                                        MR_AnyValString auditTrailText, MR_AnyValString nlName,
                                        MR_AnyValUnsignedInt notificationTime,
                                        MR_AnyValUnsignedInt expiration, MR_AnyValUnsignedInt count) {
        this.user = user;
        this.source = source;
        this.auditTrailText = auditTrailText;
        this.nlName = nlName;
        this.notificationTime = notificationTime;
        this.expiration = expiration;
        this.count = count;
    }

    public MR_AnyVal[] getMethodArgs(){
        MR_AnyVal[] args = new MR_AnyVal[7];
        args[0] = getUserAsMR();
        args[1] = getSourceAsMR();
        args[2] = getAuditTrailTextAsMR();
        args[3] = getNotificationTimeAsMR();
        args[4] = getExpirationAsMR();
        args[5] = getNlNameAsMR();
        args[6] = getCountAsMR();
        return args;
    }

    public MR_AnyValString getUserAsMR() {
        return user;
    }

    public void setUser(MR_AnyValString user) {
        this.user = user;
    }

    public MR_AnyValString getSourceAsMR() {
        return source;
    }

    public void setSource(MR_AnyValString source) {
        this.source = source;
    }

    public MR_AnyValString getAuditTrailTextAsMR() {
        return auditTrailText;
    }

    public void setAuditTrailText(MR_AnyValString auditTrailText) {
        this.auditTrailText = auditTrailText;
    }

    public MR_AnyValString getNlNameAsMR() {
        return nlName;
    }

    public void setNlName(MR_AnyValString nlName) {
        this.nlName = nlName;
    }

    public MR_AnyValUnsignedInt getNotificationTimeAsMR() {
        return notificationTime;
    }

    public void setNotificationTime(MR_AnyValUnsignedInt notificationTime) {
        this.notificationTime = notificationTime;
    }

    public MR_AnyValUnsignedInt getExpirationAsMR() {
        return expiration;
    }

    public void setExpiration(MR_AnyValUnsignedInt expiration) {
        this.expiration = expiration;
    }

    public MR_AnyValUnsignedInt getCountAsMR() {
        return count;
    }

    public void setCount(MR_AnyValUnsignedInt count) {
        this.count = count;
    }

    public String getUser() {
        return user.getStringValue();
    }

    public void setUser(String user) {
        this.user = new MR_AnyValString(user);
    }

    public String getSource() {
        return source.getStringValue();
    }

    public void setSource(String source) {
        this.source = new MR_AnyValString(source);
    }

    public String getAuditTrailText() {
        return auditTrailText.getStringValue();
    }

    public void setAuditTrailText(String auditTrailText) {
        this.auditTrailText = new MR_AnyValString(auditTrailText);
    }

    public long getCount() {
        return count.getUnsignedIntValue();
    }

    public void setCount(long count) {
        this.count = new MR_AnyValUnsignedInt(count);
    }

    public String getNlName() {
        return nlName.getStringValue();
    }

    public void setNlName(String nlName) {
        this.nlName = new MR_AnyValString(nlName);
    }

    public long getNotificationTime() {
        return notificationTime.getUnsignedIntValue();
    }

    public void setNotificationTime(long notificationTime) {
        this.notificationTime = new MR_AnyValUnsignedInt(notificationTime);
    }

    public long getExpiration() {
        return expiration.getUnsignedIntValue();
    }

    public void setExpiration(long expiration) {
        this.expiration = new MR_AnyValUnsignedInt(expiration);
    }

    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("User: ").append(getUser()).append("\n");
        buffer.append("Source: ").append(getSource()).append("\n");
        buffer.append("AuditTrailText: ").append(getAuditTrailText()).append("\n");
        buffer.append("NlName: ").append(getNlName()).append("\n");
        buffer.append("NotificationTime: ").append(getNotificationTime()).append("\n");
        buffer.append("Expiration: ").append(getExpiration()).append("\n");
        buffer.append("Count: ").append(getCount()).append("\n");
        return buffer.toString();
    }

}
