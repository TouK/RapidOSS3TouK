import org.apache.log4j.Logger
import com.ifountain.smarts.datasource.BaseSmartsListeningAdapter
import com.ifountain.comp.utils.CaseInsensitiveMap
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import org.apache.log4j.DailyRollingFileAppender
import org.apache.log4j.Level

def getParameters(){
   return [
           "Attributes":["ClassName", "InstanceName", "EventName", "Severity", "Acknowledged", "Name",
                   "EventText", "OccurrenceCount", "TroubleTicketID", "LastNotifiedAt", "LastChangedAt", "LastClearedAt", "FirstNotifiedAt", 
           "ElementName", "ElementClassName", "SourceDomainName", "Category", "EventType", "Owner", "IsRoot", "InstanceDisplayName", "ClassDisplayName"],
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
    logger.removeAllAppenders();
    def layout = new org.apache.log4j.PatternLayout("%d{yy/MM/dd HH:mm:ss.SSS} %p: %m%n");
    def appender = new DailyRollingFileAppender(layout, "logs/notificationSubscriber.log", "'.'yyyy-MM-dd");
    logger.addAppender(appender);
    logger.setAdditivity(false);
    logger.setLevel(Level.toLevel("DEBUG"));
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
def updateSmartsObjectState(elementName, instanceName, severity)
{
    def computerSystemName = elementName != null && elementName != ""?elementName:instanceName;
    def compSystemObject = RsTopologyObject.get(name:computerSystemName);
    if(compSystemObject instanceof RsComputerSystem || compSystemObject instanceof RsLink)
    {
        compSystemObject.setState(severity);
        if(compSystemObject.hasErrors())
        {
            logger.warn("Could not udpate state of ${compSystemObject} , Reason ${compSystemObject.errors}");
        }
    }
}
def update(notificationObject){
    logger.info("Received ${notificationObject}");
    def eventType = notificationObject[BaseSmartsListeningAdapter.EVENT_TYPE_NAME];
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
    else
    {
        def notificationName = getNotificationName(notificationObject);
        def notificationProps = getNotificationProperties(notificationObject);
        if(!existingObjectsRetrieved)
        {
            notificationsMap.remove(notificationName);
        }
        if(eventType == BaseSmartsListeningAdapter.NOTIFY || (eventType == BaseSmartsListeningAdapter.CHANGE && notificationObject.Active == "true"))
        {
            RsSmartsNotification addedEvent = RsSmartsNotification.add(notificationProps);
            if(!addedEvent.hasErrors())
            {
                logger.info("Added ${notificationName} to repository");
                updateSmartsObjectState(addedEvent.elementName, addedEvent.instanceName, addedEvent.severity)
                def notificationRelationPropValues = datasource.getNotification([ClassName:notificationObject.ClassName, InstanceName:notificationObject.InstanceName, EventName:notificationObject.EventName], ["CausedBy", "Causes"]);
                def causedByObjects = [];
                notificationRelationPropValues.CausedBy.each{notificationRelationProp->
                    def rsEvent = RsSmartsNotification.search("name:${notificationRelationProp.Name}").results[0];
                    if(rsEvent)
                    {
                        logger.debug("RsSmartsNotification ${rsEvent.name} will be added to ${addedEvent.name}'s causedBy")
                        causedByObjects.add(rsEvent);
                    }
                }
                def causesObjects = [];
                notificationRelationPropValues.Causeds.each{notificationRelationProp->
                    def rsEvent = RsSmartsNotification.search("name:${notificationRelationProp.Name}").results[0];
                    if(rsEvent)
                    {
                        logger.debug("RsSmartsNotification ${rsEvent.name} will be added to ${addedEvent.name}'s causes")
                        causesObjects.add(rsEvent);
                    }
                }
                addedEvent.addRelation(causedBy:causedByObjects);
                logger.info("${causedByObjects.size()} objects has been added to ${addedEvent.name}'s causedBy")
                addedEvent.addRelation(causes:causesObjects);
                logger.info("${causesObjects.size()} objects has been added to ${addedEvent.name}'s causes")

                if(!addedEvent.hasErrors())
                {
                    logger.info("Added Relations of ${notificationName} to repository");
                }
                else
                {
                    logger.warn("Could not add relations of ${notificationName} to repository, Reason ${addedEvent.errors}");
                }
            }
            else
            {
                logger.warn("Could not add ${notificationName} to repository, Reason ${addedEvent.errors}");
            }
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
    def historicalNotificationProps = [:];
    columnLocalNameMappings.each{String localName, String smartsName->
        historicalNotificationProps[localName] = notification[localName];
    }
    historicalNotificationProps["active"] = false;
    historicalNotificationProps["causedBy"] = serializeRelations(notification, "causedBy");
    historicalNotificationProps["causes"] = serializeRelations(notification, "causes");
    notification.remove();
    if(!notification.hasErrors())
    {
        RsSmartsHistoricalNotification.add(historicalNotificationProps);
        updateSmartsObjectState(notification.elementName, notification.instanceName, -1)
        logger.info("${notification.name} is moved  to HistoricalNotification");
    }
    else
    {
        logger.warn("${notification.name} can not be archived, Reason :${notification.errors} ");
    }
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
            if(localName == "lastNotifiedAt" || localName == "lastChangedAt" || localName == "lastClearedAt" || localName == "firstNotifiedAt")
            {
                propValue = propValue*1000;
            }
            notficationProps[localName] = propValue;
        }
    }
    notficationProps.put("rsDatasource",getDatasource().name)
    return notficationProps;
}

def getDatasource(){
    return datasource;
}