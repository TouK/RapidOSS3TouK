package connector


import datasource.NetcoolColumn
import connector.NetcoolLastRecordIdentifier
import datasource.NetcoolDatasource
import org.apache.log4j.Logger
import com.ifountain.comp.utils.CaseInsensitiveMap
import datasource.NetcoolConversionParameter
import com.ifountain.rcmdb.domain.util.DomainClassUtils

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 4, 2008
 * Time: 8:25:24 AM
 * To change this template use File | Settings | File Templates.
 */
class NetcoolConnectorImpl {
    public static MAPPING_FOR_KNOWN_COLUMNS = ["class": "netcoolclass", "type": "nctype", "x733eventtype": "ncx733eventtype",
        "x733probablecause": "ncx733probablecause", "x733specificprob": "ncx733specificprob", "x733corrnotif": "ncx733corrnotif",
        "acknowledged": "acknowledged", "owneruid": "owner", "severity": "severity", "firstoccurrence": "createdAt",
        "suppressescl": "state", "expiretime": "willExpireAt", "statechange": "changedAt", "tally":"count"]
    public static TIMESTAMP_FIELDS = ["statechange", "firstoccurrence", "lastoccurrence", "internallast"]
    Map nameMappings;
    NetcoolDatasource datasource;
    def deleteMarkerField;
    def connectorName;
    Class NetcoolEvent;
    Class NetcoolJournal;
    Logger logger;
    Map columnConversionParameters;
    public NetcoolConnectorImpl(NetcoolConnector connector, Logger logger, Map columnConversionParameters)
    {
        this.logger = logger;
        this.columnConversionParameters = columnConversionParameters;
        NetcoolEvent = this.class.classLoader.loadClass("NetcoolEvent");
        NetcoolJournal = this.class.classLoader.loadClass("NetcoolJournal");
        def datasourceName = NetcoolConnector.getDatasourceName(connector.name)
        this.datasource = NetcoolDatasource.get(name: datasourceName);
        if (this.datasource == null) {
            throw new Exception("Datasource ${datasourceName} of Connector ${connector.name} could not be found.")
        }
        this.connectorName = connector.name;
        nameMappings = new CaseInsensitiveMap();
        NetcoolColumn.list().each {NetcoolColumn map ->
            nameMappings[map.netcoolName] = map.localName;
            if (map.isDeleteMarker)
            {
                deleteMarkerField = map;
            }
        }
        markAllEventsAsDeleted();
    }

    //Get all events after marking as deleted. discard lastrecordidentifier
    def markAllEventsAsDeleted()
    {
        logger.debug("Marking deleted events.")
        def deleteMarkerLocalName = deleteMarkerField ? deleteMarkerField.localName : "severity"
        def deleteMarkerNetcoolName = deleteMarkerField ? deleteMarkerField.netcoolName : "Severity"
        def markedEvents = [:];
        int offset = 0;
        int batchSize = 1000;
        def lastUpdateTime = 0;
        while (true)
        {
            def res = invokeMethod(NetcoolEvent, "search", ["rsDatasource:${connectorName}", [max: batchSize, offset: offset, sort: "id"]] as Object[]);
            if (res.results.isEmpty())
            {
                break;
            }
            res.results.each {event ->
                markedEvents[event.servername + event.serverserial] = event;
                if (event.statechange > lastUpdateTime)
                {
                    lastUpdateTime = event.statechange;
                }
            }
            offset += batchSize;
        }
        logger.debug("Last update time in repository is ${lastUpdateTime}")
        logger.debug("Getting active events from netcool repository.")
        def whereClause = "StateChange <= ${lastUpdateTime} AND ${deleteMarkerNetcoolName} > 0";

        List records = datasource.getEvents(whereClause);
        logger.debug(records.size() + " number of active events found that have been added to the repository.")
        for (Map rec in records)
        {
            markedEvents.remove(rec.SERVERNAME + rec.SERVERSERIAL);
        }
        logger.info("Following events are deleted before connector start ${markedEvents}");
        markedEvents.each {key, event ->
            event.clear();
        }
    }


    def run()
    {
        processEvents();
        processJournals();
    }

    def processEvents()
    {

        NetcoolLastRecordIdentifier lastRecordIdentifier = NetcoolLastRecordIdentifier.get(connectorName: connectorName);
        if (lastRecordIdentifier == null)
        {
            lastRecordIdentifier = NetcoolLastRecordIdentifier.add(connectorName: connectorName, eventLastRecordIdentifier: 0, journalLastRecordIdentifier: 0);
        }
        def deleteMarkerNetcoolName = deleteMarkerField ? deleteMarkerField.netcoolName : "Severity"
        def lastEventStateChange = lastRecordIdentifier.eventLastRecordIdentifier;
        logger.info("Processing events. after ${lastEventStateChange}");
        def whereClause = "StateChange > ${lastEventStateChange} AND StateChange <= getdate - 1";
        List records = datasource.getEvents(whereClause);
        logger.info("Got ${records.size()} number of records");
        for (Map rec in records) {
            if (rec[deleteMarkerNetcoolName] == "0") {
                logger.info("Clearing event ${rec}")
                def event = NetcoolEvent.get(name: "${rec.SERVERNAME}_${rec.SERVERSERIAL}");
                if (event) {
                    event.clear();
                }
            }
            else {
                logger.info("Adding event ${rec}");
                def eventProps = getEventProperties(rec);
                logger.info("Event properties are ${eventProps}");
                def res = invokeMethod(NetcoolEvent, "add", [eventProps] as Object[]);
                if (!res.hasErrors())
                {
                    logger.info("Event added.");
                    def lastStateChange = Long.parseLong(rec.statechange);
                    if (lastStateChange > lastEventStateChange) {
                        lastEventStateChange = lastStateChange;
                    }
                }
                else
                {
                    logger.warn("Could not add event with serial ${rec.SERVERSERIAL}. Reason :${res.errors}");
                }
            }

        }
        lastRecordIdentifier.eventLastRecordIdentifier = lastEventStateChange;
    }

    def processJournals()
    {
        NetcoolLastRecordIdentifier lastRecordIdentifier = NetcoolLastRecordIdentifier.get(connectorName: connectorName);
        def lastJournalStateChange = lastRecordIdentifier.journalLastRecordIdentifier;
        logger.info("Processing journals. after ${lastJournalStateChange}");
        if (lastJournalStateChange == null)
        {
            lastJournalStateChange = 0;
        }
        def whereClause = "Chrono>$lastJournalStateChange AND Chrono <= getdate() - 1 ORDER BY Chrono";
        List records = datasource.getJournalEntries(whereClause);
        logger.info("Got ${records.size()} number of journals.");
        for (Map rec in records) {
            logger.info("Adding journal ${rec}");
            def journalProps = getJournalProperties(rec);

            if (journalProps != null)
            {
                logger.info("Adding journal with properties${journalProps}");
                def res = invokeMethod(NetcoolJournal, "add", [journalProps] as Object[]);
                if (!res.hasErrors())
                {
                    logger.info("Jourmal added.");
                    def lastStateChange = Long.parseLong(rec.chrono);
                    if (lastStateChange > lastJournalStateChange) {
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
        rec.each {String propName, String propValue ->
            def convProp = this.columnConversionParameters[propName];
            if (convProp != null)
            {
                try{
                    propValue = convProp[propValue.toInteger()]
                }
                catch(e){
                }

            }
            if(TIMESTAMP_FIELDS.contains(propName.toLowerCase())){
                try{
                   propValue = Long.parseLong(propValue) * 1000;
                }
                catch(e){}
            }
            def localColName = nameMappings[propName];
            if (localColName == null)
            {
                localColName = MAPPING_FOR_KNOWN_COLUMNS[propName.toLowerCase()] != null ? MAPPING_FOR_KNOWN_COLUMNS[propName.toLowerCase()] : propName.toLowerCase();
            }
            eventMap[localColName] = propValue;
        }
        eventMap["rsDatasource"] = connectorName
        eventMap["name"] = "${rec.SERVERNAME}_${rec.SERVERSERIAL}"
        return eventMap;
    }

    def invokeMethod(opClass, methodName, args)
    {
        return opClass.metaClass.invokeStaticMethod(opClass, methodName, args);
    }
    def getJournalProperties(Map rec)
    {
        def serverSerialColName = nameMappings["serverserial"] ? nameMappings["serverserial"] : "serverserial";
        def connectorNameColName = nameMappings["rsDatasource"] ? nameMappings["rsDatasource"] : "rsDatasource";
        def query = "${serverSerialColName}:\"${rec.SERIAL}\" AND ${connectorNameColName}:\"${this.connectorName}\""
        def event = invokeMethod(NetcoolEvent, "search", [query] as Object[]).results[0];
        if (event == null) return null;
        def journalMap = [servername: event.servername, serverserial: event.serverserial, rsDatasource: this.connectorName]
        StringBuffer text = new StringBuffer();
        rec.each {String propName, String propValue ->
            if (propName.toLowerCase().startsWith("text") && propValue != "")
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