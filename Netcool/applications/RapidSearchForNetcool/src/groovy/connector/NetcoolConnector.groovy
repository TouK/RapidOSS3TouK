package connector


import datasource.NetcoolColumn
import connector.NetcoolLastRecordIdentifier
import datasource.NetcoolDatasource
import org.apache.log4j.Logger
import com.ifountain.comp.utils.CaseInsensitiveMap
import datasource.NetcoolConversionParameter

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 4, 2008
 * Time: 8:25:24 AM
 * To change this template use File | Settings | File Templates.
 */
class NetcoolConnector {
    public static MAPPING_FOR_KNOWN_COLUMNS = ["class":"netcoolclass", "type":"nctype"]
    Map nameMappings;
    NetcoolDatasource datasource;
    NetcoolColumn deleteMarkerField;
    def connectorName;
    Class NetcoolEvent;
    Class NetcoolJournal;
    Logger logger;
    Map columnConversionParameters;
    public NetcoolConnector(NetcoolDatasource datasource, Logger logger, Map columnConversionParameters)
    {
        this.logger = logger;
        this.columnConversionParameters = columnConversionParameters;
        NetcoolEvent = this.class.classLoader.loadClass("NetcoolEvent");
        NetcoolJournal = this.class.classLoader.loadClass("NetcoolJournal");
        this.datasource = datasource;
        this.connectorName = datasource.name;
        nameMappings = new CaseInsensitiveMap();
        NetcoolColumn.list().each{NetcoolColumn map->
            nameMappings[map.netcoolName] = map.localName;
            if(map.isDeleteMarker)
            {
                deleteMarkerField = map;
            }
        }
        if(deleteMarkerField != null)
        {
            markAllEventsAsDeleted();
        }
    }

    //Get all events after marking as deleted. discard lastrecordidentifier
    def markAllEventsAsDeleted()
    {
//        def markedEvents = [:];
//        int offset= 0 ;
//        int batchSize = 1000;
//        while(true)
//        {
//            def res = invokeMethod(NetcoolEvent, "search", ["servername:${serverName} && ${deleteMarkerField.localName}:1", [max:batchSize, offset:offset, sort:"id"]] as Object[]);
//            if(res.total == 0)
//            {
//                break;
//            }
//            res.each{event->
//                markedEvents[event.serverserial] = event;
//            }
//            offset += batchSize;
//        }
//
//        def whereClause = "StateChange <= getdate - 1 AND ${deleteMarkerField.netcoolName} == 0";
//        List records = datasource.getEvents(whereClause);
//        def lastEventStateChange = 0;
//        for (Map rec in records){
//            def eventProps = getEventProperties(rec);
//
//            def res = invokeMethod(NetcoolEvent, "add",[getEventProperties(rec)] as Object[]);
//            if(!res.hasErrors())
//            {
//                def lastStateChange = Long.parseLong(rec.statechange);
//                if (lastStateChange > lastEventStateChange){
//                    lastEventStateChange = lastStateChange;
//                }
//            }
//            else
//            {
//                logger.warn("Could not added event with serial ${rec.SERVERSERIAL}. Reason :${res.errors}");
//            }
//        }
//        NetcoolLastRecordIdentifier.add(datasourceName:datasource.name, eventLastRecordIdentifier:lastEventStateChange);
    }


    def run()
    {
        processEvents();
        processJournals();
    }

    def processEvents()
    {

        NetcoolLastRecordIdentifier lastRecordIdentifier = NetcoolLastRecordIdentifier.get(datasourceName:datasource.name);
        if(lastRecordIdentifier == null)
        {
            lastRecordIdentifier = NetcoolLastRecordIdentifier.add(datasourceName:datasource.name, eventLastRecordIdentifier:0, journalLastRecordIdentifier:0);
        }
        def lastEventStateChange = lastRecordIdentifier.eventLastRecordIdentifier;
        logger.info("Processing journals. after ${lastEventStateChange}");
        def whereClause = "StateChange>$lastEventStateChange AND StateChange <= getdate - 1";
        List records = datasource.getEvents(whereClause);
        logger.info("Got ${records.size()} number of records");
        for (Map rec in records){
            logger.info("Adding event ${rec}");
            def eventProps = getEventProperties(rec);
            logger.info("Event properties are ${eventProps}");
            def res = invokeMethod(NetcoolEvent, "add",[eventProps] as Object[]);
            if(!res.hasErrors())
            {
                logger.info("Event added.");
                def lastStateChange = Long.parseLong(rec.statechange);
                if (lastStateChange > lastEventStateChange){
                    lastEventStateChange = lastStateChange;
                }
            }
            else
            {
                logger.warn("Could not added event with serial ${rec.SERVERSERIAL}. Reason :${res.errors}");    
            }
        }
        lastRecordIdentifier.eventLastRecordIdentifier = lastEventStateChange; 
    }

    def processJournals()
    {
        NetcoolLastRecordIdentifier lastRecordIdentifier = NetcoolLastRecordIdentifier.get(datasourceName:datasource.name);
        def lastJournalStateChange = lastRecordIdentifier.journalLastRecordIdentifier;
        logger.info("Processing journals. after ${lastJournalStateChange}");
        if( lastJournalStateChange == null)
        {
            lastJournalStateChange = 0;
        }
        def whereClause = "Chrono>$lastJournalStateChange AND Chrono <= getdate() - 1 ORDER BY Chrono";
        List records = datasource.getJournalEntries(whereClause);
        logger.info("Got ${records.size()} number of journals.");
        for (Map rec in records){
            logger.info("Adding journal ${rec}");
            def journalProps = getJournalProperties(rec);

            if(journalProps != null)
            {
                logger.info("Adding journal with properties${journalProps}");
                def res = invokeMethod(NetcoolJournal, "add", [journalProps] as Object[]);
                if(!res.hasErrors())
                {
                    logger.info("Jourmal added.");
                    def lastStateChange = Long.parseLong(rec.chrono);
                    if (lastStateChange > lastJournalStateChange){
                        lastJournalStateChange = lastStateChange;
                    }
                }
                else
                {
                    logger.warn("Could not added journal with serial ${journalProps.serverserial}. Reason :${res.errors}");    
                }

            }
            else
            {
                logger.info("Found a journal with non existing event in rapidcmdb. Will retry to process journals ");
                break;
            }
        }
        lastRecordIdentifier.journalLastRecordIdentifier = lastJournalStateChange;
    }

    def getEventProperties(Map rec)
    {
        def eventMap = [:]
        rec.each{String propName, String propValue->
            def convProp = this.columnConversionParameters[propName];
            if(convProp != null)
            {
                propValue = convProp[propValue] 
            }
            def localColName = nameMappings[propName];
            if(localColName == null)
            {
                localColName = MAPPING_FOR_KNOWN_COLUMNS[propName.toLowerCase()]!= null?MAPPING_FOR_KNOWN_COLUMNS[propName.toLowerCase()]:propName;
            }
            eventMap[localColName.toLowerCase()] = propValue;
        }
        eventMap["connectorname"] = connectorName
        return eventMap;
    }

    def invokeMethod(opClass, methodName, args)
    {
        return opClass.metaClass.invokeStaticMethod(opClass, methodName, args);
    }
    def getJournalProperties(Map rec)
    {
        def serverSerialColName = nameMappings["serverserial"]?nameMappings["serverserial"]:"serverserial";
        def connectorNameColName = nameMappings["connectorname"]?nameMappings["connectorname"]:"connectorname";
        def query = "${serverSerialColName}:\"${rec.SERIAL}\" AND ${connectorNameColName}:\"${this.connectorName}\""
        def event = invokeMethod(NetcoolEvent, "search", [query] as Object[]).results[0];
        if(event == null) return null;
        def journalMap = [servername:event.servername, serverserial:event.serverserial, connectorname:this.connectorName]
        StringBuffer text = new StringBuffer();
        rec.each{String propName, String propValue->
            if(propName.startsWith("text") && propValue != "")
            {
                text.append(" ").append(propValue); 
            }
            else
            {
                journalMap[propName.toLowerCase()] = propValue;
            }
        }
        journalMap["text"] = text.toString();
        return journalMap;
    }
}