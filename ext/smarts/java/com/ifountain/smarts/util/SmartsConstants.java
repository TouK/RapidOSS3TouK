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

public interface SmartsConstants {

    final static String NOTIFICATION_CLASS_NAME = "ICS_Notification";
    final static String NOTIFICATION_FACTORY_CLASS = "ICS_NotificationFactory";
    final static String NOTIFICATION_FACTORY_INSTANCE = "ICS-NotificationFactory";
    final static String NOTIFICATION_CLASS = "ICS_Notification";
    final static String MAKE_NOTIFICATION = "makeNotification";
    final static String MAKE_AGGREGATE = "makeAggregate";
    final static String FIND_NOTIFICATION = "findNotification";
    final static String ARCHIVE_NOTIFICATION = "archiveNotification";
    final static String CHANGED = "changed";
    final static String NOTIFY = "notify";
    final static String ACKNOWLEDGE = "acknowledge";
    final static String ADDAUDITENTRY = "addAuditEntry";
    final static String CLEAR = "clear";
    final static String RELEASE_OWNERSHIP = "releaseOwnership";
    final static String TAKE_OWNERSHIP = "takeOwnership";
    final static String UNACKNOWLEDGE = "unacknowledge";

    final static String PARAM_CLASSNAME = "ClassName";
    final static String PARAM_INSTANCENAME = "InstanceName";
    final static String PARAM_EVENTNAME = "EventName";
    final static String PARAM_USER = "User";
    final static String PARAM_SOURCE = "SourceDomainName";
    final static String PARAM_AUDITTRAILTEXT = "AuditTrailText";
    final static String PARAM_NLNAME = "NlName";
    final static String PARAM_NOTIFICATIONTIME = "NotificationTime";
    final static String PARAM_CLEARTIME = "ClearTime";
    final static String PARAM_EXPIRATION = "Expiration";
    final static String PARAM_COUNT = "Count";
    final static String PARAM_ATTRIBUTES = "Attributes";
    final static String PARAM_AGGREGATE = "Aggregate";
    final static String PARAM_AGGREGATECLASSNAME = "AggregateElementClass";
    final static String PARAM_AGGREGATEINSTANCENAME = "AggregateElementName";
    final static String PARAM_AGGREGATEEVENT = "AggregateEvent";
    final static String PARAM_UNKNOWNAGENT = "UnknownAgent";
    final static String PARAM_CREATE = "Create";
    final static String PARAM_IGNORE = "Ignore";
    static final String NOTIFICATION_LIST_CLASS = "ICS_NotificationList";
    
    final static String CLASSNAME = "CreationClassName";
    final static String INSTANCENAME = "Name";
}
