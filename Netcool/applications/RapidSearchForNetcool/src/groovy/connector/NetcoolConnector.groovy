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
    public static MAPPING_FOR_KNOWN_COLUMNS = ["class":"netcoolclass"]
    Map nameMappings;
    NetcoolDatasource datasource;
    NetcoolColumn deleteMarkerField;
    def serverName;
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
        this.serverName = datasource.name;
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
        def whereClause = "StateChange>$lastEventStateChange AND StateChange <= getdate - 1";
        List records = datasource.getEvents(whereClause);
        def size =  records.size()
        for (Map rec in records){
            def res = invokeMethod(NetcoolEvent, "add",[getEventProperties(rec)] as Object[]);
            if(!res.hasErrors())
            {
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
        if( lastJournalStateChange == null)
        {
            lastJournalStateChange = 0;
        }
        def whereClause = "Chrono>$lastJournalStateChange AND Chrono <= getdate() - 1";
        List records = datasource.getJournalEntries(whereClause);
        def size =  records.size()
        for (Map rec in records){
            invokeMethod(NetcoolJournal, "add", [getJournalProperties(rec)] as Object[]);
            def lastStateChange = Long.parseLong(rec.chrono);
            if (lastStateChange > lastJournalStateChange){
                lastJournalStateChange = lastStateChange;
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
        eventMap["servername"] = serverName
        return eventMap;
    }

    def invokeMethod(opClass, methodName, args)
    {
        return opClass.metaClass.invokeStaticMethod(opClass, methodName, args);
    }
    def getJournalProperties(Map rec)
    {
        def eventSearchParams = [:];
        eventSearchParams[nameMappings["serverserial"]?nameMappings["serverserial"]:"serverserial"] = rec.SERIAL;
        eventSearchParams[nameMappings["servername"]?nameMappings["servername"]:"servername"] = this.serverName;
        def event = invokeMethod(NetcoolEvent, "get", eventSearchParams);
        def journalMap = [servername:serverName, event:event]
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