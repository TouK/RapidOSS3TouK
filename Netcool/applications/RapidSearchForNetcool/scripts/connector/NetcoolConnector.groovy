package connector


import configuration.NameMapping
import connector.NetcoolLastRecordIdentifier
import datasource.NetcoolDatasource

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
    NameMapping deleteMarkerField;
    def serverName;
    public NetcoolConnector(NetcoolDatasource datasource)
    {
        this.datasource = datasource;
        this.serverName = datasource.name;
        nameMappings = [:]
        NameMapping.list().each{NameMapping map->
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
            def res = NetcoolEvent.search("servername:${serverName} && ${deleteMarkerField.localName}:false", [max:batchSize, offset:offset, sort:"id"]);
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
        def lastEventStateChange = lastRecordIdentifier.eventLastRecordIdentifier;
        if( lastEventStateChange == null)
        {
            lastEventStateChange = 0;
        }
        def whereClause = "StateChange>$lastEventStateChange && StateChange <= getdate() - 1";
        List records = datasource.getEvents(whereClause);
        def size =  records.size()
        for (Map rec in records){
            NetcoolEvent.add(getEventProperties(rec));
            def lastStateChange = Long.parseLong(rec.statechange);
            if (longStateChange>lastStateChange){
                lastEventStateChange = longStateChange;
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
        def whereClause = "Chrono>$lastJournalStateChange && Chrono <= getdate() - 1";
        List records = datasource.getJournalEntries(whereClause);
        def size =  records.size()
        for (Map rec in records){
            NetcoolJournal.add(getJournalProperties(rec));
            def lastStateChange = Long.parseLong(rec.chrono);
            if (longStateChange > lastStateChange){
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
            eventMap[localColName] = propValue;
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
                journalMap[localColName] = propValue;
            }
        }
        journalMap["text"] = text.toString();
        return journalMap;
    }
}