package connector


import datasource.NetcoolColumn
import connector.NetcoolLastRecordIdentifier
import datasource.NetcoolDatasource
import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 4, 2008
 * Time: 8:25:24 AM
 * To change this template use File | Settings | File Templates.
 */
class NetcoolConnector {
    Map nameMappings;
    NetcoolDatasource datasource;
    NetcoolColumn deleteMarkerField;
    def serverName;
    Class NetcoolEvent;
    Class NetcoolJournal;
    Logger logger;
    public NetcoolConnector(NetcoolDatasource datasource, Logger logger)
    {
        this.logger = logger;
        NetcoolEvent = this.class.classLoader.loadClass("NetcoolEvent");
        NetcoolJournal = this.class.classLoader.loadClass("NetcoolJournal");
        this.datasource = datasource;
        this.serverName = datasource.name;
        nameMappings = [:]
        NetcoolColumn.list().each{NetcoolColumn map->
            nameMappings[map.netcoolName] = map.localName;
            if(map.isDeleteMarker)
            {
                deleteMarkerField = map;
            }
        }
        if(deleteMarkerField == null)
        {
            throw new Exception("Please specify a marker field for deleted events.");
        }
        markAllEventsAsDeleted();
    }


    def markAllEventsAsDeleted()
    {
        int offset= 0 ;
        int batchSize = 1000;
        while(true)
        {
            def res = NetcoolEvent.metaClass.invokeStaticMethod(NetcoolEvent, "search", ["servername:${serverName} && ${deleteMarkerField.localName}:false", [max:batchSize, offset:offset, sort:"id"]] as Object[]);
            if(res.total == 0)
            {
                break;
            }
            res.each{event->
                event[deleteMarkerField.localName] = true;
            }
            offset += batchSize;
        }
        run();
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
            def res = NetcoolEvent.metaClass.invokeStaticMethod(NetcoolEvent, "add",[getEventProperties(rec)] as Object[]);
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
            NetcoolJournal.metaClass.invokeStaticMethod(NetcoolJournal, "add", [getJournalProperties(rec)] as Object[]);
            def lastStateChange = Long.parseLong(rec.chrono);
            if (lastStateChange > lastJournalStateChange){
                lastJournalStateChange = lastStateChange;
            }
        }
        lastRecordIdentifier.journalLastRecordIdentifier = lastJournalStateChange;
    }

    def getEventProperties(Map rec)
    {
        def eventMap = [servername:serverName]
        rec.each{String propName, String propValue->
            def localColName = nameMappings[propName];
            if(localColName == null)
            {
                localColName = propName;
            }
            eventMap[localColName.toLowerCase()] = propValue;
        }
        return eventMap;
    }

    def getJournalProperties(Map rec)
    {
        def journalMap = [servername:serverName]
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