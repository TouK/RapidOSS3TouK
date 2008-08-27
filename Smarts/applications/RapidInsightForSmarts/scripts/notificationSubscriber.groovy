import org.apache.log4j.Logger
import com.ifountain.smarts.datasource.BaseSmartsListeningAdapter
import datasource.SmartsModel
import datasource.SmartsModelColumn
import com.ifountain.comp.utils.CaseInsensitiveMap

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
    logger = Logger.getLogger("topologySubscriber");
    logger.debug("Getting column mapping information.");
    SmartsModel.get(name:"RsEvent").columns.each{SmartsModelColumn col->
        columnLocalNameMappings[col.localName] = col.smartsName;
        columnSmartsNameMappings[col.smartsName] = col.localName;        
    }

    logger.debug("Marking all notifications as deleted.");
    notificationsMap = new CaseInsensitiveMap()
    def notificationNames = RsEvent.termFreqs("name").term;
    notificationNames.each {
        notificationsMap[it] = "deleted";
    }
    existingObjectsRetrieved = false;
}

def cleanUp(){

}

def update(notificationObject){
    logger.info("Received ${notificationObject}");
    
    def eventType = notificationObject[BaseSmartsListeningAdapter.EVENT_TYPE_NAME];
    def notificationProps = getNotificationProperties(notificationObject);
    if(!existingObjectsRetrieved)
    {
        notificationsMap.remove(notificationProps.name);    
    }
    if(eventType == BaseSmartsListeningAdapter.RECEIVE_EXISTING_FINISHED)
    {
        existingObjectsRetrieved = true;
        logger.info("Existing objects retrieved and ${notificationsMap.size()} number of events will be moved to HistoricalNotification.");
        notificationsMap.each{String notificationName, String value->
            def notification = RsEvent.search("name:${notificationName}").results[0];
            archiveNotification(notification);
        }
    }
    else if(eventType == BaseSmartsListeningAdapter.NOTIFY || eventType == BaseSmartsListeningAdapter.CHANGE)
    {
        RsEvent.add(notificationProps);
        logger.info("Added ${notificationProps.name} to repository");
    }
    else if(eventType == BaseSmartsListeningAdapter.CLEAR)
    {
        archiveNotification(RsEvent.get(notificationProps));
    }
    else if(eventType == BaseSmartsListeningAdapter.ARCHIVE)
    {
        archiveNotification(RsEvent.get(notificationProps));
    }
}

def archiveNotification(notification)
{
    if(notification == null) return;
    notification.remove();
    def historicalNotificationProps = [:];
    columnLocalNameMappings.each{String localName, String smartsName->
        historicalNotificationProps[localName] = notification[localName];
        RsHistoricalEvent.add(historicalNotificationProps);
    }
    logger.info("${notification.name} is moved  to HistoricalNotification");
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
    println localName
    return notficationProps;
}