import org.apache.log4j.Logger
import com.ifountain.smarts.datasource.BaseSmartsListeningAdapter
import com.ifountain.comp.utils.CaseInsensitiveMap
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import com.ifountain.rcmdb.domain.util.DomainClassUtils

def getParameters(){
   return [
           "Attributes":["ClassName", "InstanceName", "EventName", "Severity", "Acknowledged", "Name",
                   "EventText", "OccurrenceCount", "TroubleTicketID", "LastNotifiedAt", "LastChangedAt", "LastClearedAt"],
           "NotificationList":"ALL_NOTIFICATIONS",
           "TransientInterval":300,
           "TailMode":false
   ]
}


logger = null;
notificationsMap = null; 
columnLocalNameMappings = [:]
columnSmartsNameMappings = [:]
existingObjectsRetrieved = false;

def init(){
    logger = Logger.getLogger("notificationSubscriber");
    logger.debug("Getting column mapping information.");

    GrailsDomainClass gdc = ApplicationHolder.getApplication().getDomainClass(RsSmartsNotification.name);
    def dcProperties = gdc.getProperties();
    def relations = DomainClassUtils.getRelations(gdc);
    dcProperties.each{GrailsDomainClassProperty prop->
        if(prop.isPersistent() && !relations.containsKey(prop.name))
        {
            def propName = prop.getName();
            def smartsName = propName.substring(0,1).toUpperCase()+propName.substring(1);
            columnLocalNameMappings[propName] = smartsName;
            columnSmartsNameMappings[smartsName] = propName;
        }
    }
    logger.debug("Marking all notifications as deleted.");
    notificationsMap = new CaseInsensitiveMap()
    def notificationNames = RsSmartsNotification.termFreqs("name", [size:10000000000]).term;
    notificationNames.each {
        notificationsMap[it] = "deleted";
    }
    existingObjectsRetrieved = false;
}

def cleanUp(){

}

def update(notificationObject){
    logger.info("Received ${notificationObject}");
    def notificationName = getNotificationName(notificationObject);
    def eventType = notificationObject[BaseSmartsListeningAdapter.EVENT_TYPE_NAME];
    def notificationProps = getNotificationProperties(notificationObject);
    if(!existingObjectsRetrieved)
    {
        notificationsMap.remove(notificationName);    
    }
    if(eventType == BaseSmartsListeningAdapter.RECEIVE_EXISTING_FINISHED)
    {
        existingObjectsRetrieved = true;
        logger.info("Existing objects retrieved and ${notificationsMap.size()} number of events will be moved to HistoricalNotification.");
        notificationsMap.each{String archivedNotificationName, String value->
            def notification = RsSmartsNotification.search("name:${archivedNotificationName}").results[0];
            archiveNotification(notification);
        }
        notificationsMap.clear();
    }
    else if(eventType == BaseSmartsListeningAdapter.NOTIFY || eventType == BaseSmartsListeningAdapter.CHANGE)
    {
        def addedEvent = RsSmartsNotification.add(notificationProps);
        def notificationRelationPropValues = datasource.getNotification([ClassName:notificationObject.ClassName, InstanceName:notificationObject.InstanceName, EventName:notificationObject.EventName], ["CausedBy", "Causes"]);
        def causedByObjects = [];
        notificationRelationPropValues.CausedBy.each{notificationRelationProp->
            def rsEvent = RsSmartsNotification.search("name:${notificationRelationProp.Name}").results[0];
            if(rsEvent)
            {
                causedByObjects.add(rsEvent);
            }
        }
        def causesObjects = [];
        notificationRelationPropValues.Causeds.each{notificationRelationProp->
            def rsEvent = RsSmartsNotification.search("name:${notificationRelationProp.Name}").results[0];
            if(rsEvent)
            {
                causesObjects.add(rsEvent);
            }
        }
        addedEvent.addRelation(causedBy:causedByObjects);
        addedEvent.addRelation(causes:causesObjects);
        logger.info("Added ${notificationName} to repository");
    }
    else if(eventType == BaseSmartsListeningAdapter.CLEAR)
    {
        archiveNotification(RsSmartsNotification.get(name:notificationName));
    }
    else if(eventType == BaseSmartsListeningAdapter.ARCHIVE)
    {
        archiveNotification(RsSmartsNotification.get(name:notificationName));
    }
}

def serializeRelations(domainObject, relationName)
{
    def serializedRelation = new StringBuffer();
    domainObject[relationName].each{
        serializedRelation.append(it.name).append(", ")   
    }
    if(serializedRelation.length() > 0)
    {
        return serializedRelation.substring(0, serializedRelation.length()-2);
    }
    return "";
}

def archiveNotification(notification)
{
    if(notification == null) return;
    notification.remove();
    def historicalNotificationProps = [:];
    columnLocalNameMappings.each{String localName, String smartsName->
        historicalNotificationProps[localName] = notification[localName];
        historicalNotificationProps["causedBy"] = serializeRelations(notification, causedby);
        historicalNotificationProps["causes"] = serializeRelations(notification, causes);
        RsHistoricalEvent.add(historicalNotificationProps);
    }
    logger.info("${notification.name} is moved  to HistoricalNotification");
}

def getNotificationName(notificationObject)
{
    def name = notificationObject.Name;
    if(name == null)
    {
        def notificationProps = datasource.getNotification([ClassName:notificationObject.ClassName, InstanceName:notificationObject.InstanceName, EventName:notificationObject.EventName], ["Name"]);
        name = notificationProps.Name;
    }
    return name;

}

def getNotificationProperties(notificationObject)
{
    def notficationProps = [:]
    notificationObject.each{String propName, propValue->
        def localName = columnSmartsNameMappings[propName];
        if(localName)
        {
            notficationProps[localName] = propValue;
        }
    }
    notficationProps.put("rsDatasource",getDatasource().name)
    return notficationProps;
}

def getDatasource(){
    return datasource;
}